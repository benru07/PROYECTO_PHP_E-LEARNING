"use strict";

/* ===========================================================================
   PROGRAMA PRINCIPAL - codigo.js  (Apartado d)
   Los scripts se cargan al final del <body>: el DOM ya existe aqui.
   =========================================================================== */

const oBar = new Bar();

const frmAlta = document.getElementById("frmAltaPedido");
const frmListado = document.getElementById("frmListadoPedidosCliente");
const divListado = document.getElementById("listado");

registrarEventos();

function registrarEventos() {
  // Opciones del menu
  document.getElementById("mnuAltaPedido").addEventListener("click", mostrarFormulario);
  document.getElementById("mnuListadoPedidosCliente").addEventListener("click", mostrarFormulario);
  // Botones (referencia a la funcion, SIN parentesis)
  frmAlta.btnAltaPedido.addEventListener("click", procesarAltaPedido);
  frmListado.btnListadoPedidosCliente.addEventListener("click", procesarListadoPedidos);
}

/* ===========================================================================
   APARTADO d.i) (1,5 puntos)
   Visibilidad: formulario de alta / formulario de listado + capa #listado,
   y carga del desplegable de platos.

   OJO: estilos.css trae  form, #listado { display: none; }
   - Para MOSTRAR hay que poner style.display = "block" (estilo en linea, gana a la hoja).
   - style.display = "" NO sirve: quita el estilo en linea y vuelve a mandar la
     regla de la hoja (display:none).
   - Aqui no se usa la clase d-none de Bootstrap: no es la tecnica de esta plantilla.
   =========================================================================== */

function ocultarTodo() {
  frmAlta.style.display = "none";
  frmListado.style.display = "none";
  divListado.style.display = "none";
}

async function mostrarFormulario(oEvento) {
  oEvento.preventDefault(); // los <a href="#"> saltarian al inicio de la pagina

  ocultarTodo(); // primero ocultar todo => nunca se ven dos cosas a la vez

  switch (oEvento.target.id) {
    case "mnuAltaPedido":
      await cargarPlatos();
      frmAlta.style.display = "block";
      break;
    case "mnuListadoPedidosCliente":
      divListado.innerHTML = ""; // se limpia el listado anterior
      frmListado.style.display = "block";
      break;
  }
}

/* Rellena el <select> de platos:
   value = idplato (lo que se guarda en BD); texto visible = NOMBRE del plato
   (no la columna `descripcion`: "descripcion visible" del enunciado = lo que se ve). */
async function cargarPlatos() {
  try {
    let platos = await oBar.getPlatos(); // array de filas
    let options = "";
    for (let plato of platos) {
      options += `<option value="${plato.idplato}">${plato.nombre}</option>`;
    }
    frmAlta.lstPlatos.innerHTML = options; // se asigna una sola vez
  } catch (error) {
    alert("No se han podido cargar los platos: " + error.message);
  }
}

/* ===========================================================================
   APARTADO d.ii) (2 puntos)
   Alta de un pedido. Sin validaciones (lo dice el enunciado).
   Exito o error: alert con el mensaje del servidor.
   =========================================================================== */

async function procesarAltaPedido() {
  let cliente = frmAlta.txtNombreCliente.value.trim();
  let idplato = frmAlta.lstPlatos.value; // value del <option> = idplato

  // Grupo de radio buttons: frmAlta.rdoUnidades es un RadioNodeList y su .value
  // devuelve el value del que esta marcado (equivale a
  // document.querySelector('input[name="rdoUnidades"]:checked').value)
  let unidades = frmAlta.rdoUnidades.value;

  // idpedido = null: lo genera la BD (autonumerico)
  let oPedido = new Pedido(null, cliente, idplato, unidades);

  let respuesta = await oBar.altaPedido(oPedido); // await: sin el, respuesta seria una Promesa

  alert(respuesta.mensaje);

  // error === true => FALLO (aqui la propiedad se llama "error", no "ok")
  if (!respuesta.error) {
    frmAlta.reset(); // opcional (el enunciado no lo exige): deja el formulario listo para otro pedido
  }
}

/* ===========================================================================
   APARTADO d.iii) (2 puntos)
   Listado de pedidos de un cliente en la capa #listado.
   - Solo una tabla a la vez: se ASIGNA innerHTML (no += ), asi se sustituye.
   - La tabla (titulo, columnas, orden, estilo) la construye Bar.listadoPedidosCliente.
   =========================================================================== */

async function procesarListadoPedidos() {
  let nombreCliente = frmListado.txtNombreClienteListado.value.trim();

  try {
    divListado.innerHTML = await oBar.listadoPedidosCliente(nombreCliente);
    divListado.style.display = "block"; // #listado esta oculto por la hoja de estilos
  } catch (error) {
    alert("No se ha podido obtener el listado: " + error.message);
  }
}
