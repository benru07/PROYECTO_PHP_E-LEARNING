"use strict";

/* ===========================================================================
   PROGRAMA PRINCIPAL - codigo.js  (Apartado B)
   Se carga el ultimo en index.html, asi que ya existen Libreria, Libro y
   peticionGET/POST. Al estar el <script> al final del <body>, el DOM ya esta listo.
   =========================================================================== */

// Referencias a los elementos que usaremos. Se obtienen explicitamente con
// getElementById (no dependemos de las variables globales automaticas por id).
const frmCrear = document.getElementById("frmCrearLibro");
const frmListado = document.getElementById("frmListado");

/* IMPORTANTE: "var" y no "let/const". Un let/const de nivel superior NO pasa a
   ser propiedad de window, y window.opener.oLibreria seria undefined. Aqui no
   lo necesitamos porque tarjetas.html crea su propio objeto, pero con "var"
   tambien funcionaria la variante opener.oLibreria de la solucion oficial. */
var oLibreria = new Libreria();

registrarEventos();

function registrarEventos() {
  // Opciones del menu (B.1)
  document.getElementById("mnuCrearLibro").addEventListener("click", mostrarFormulario);
  document.getElementById("mnuListado").addEventListener("click", mostrarFormulario);

  // Botones de los formularios (B.2 y B.3).
  // Se pasa la REFERENCIA a la funcion (sin parentesis): si no, se ejecutaria ya.
  frmCrear.btnCrearLibro.addEventListener("click", procesarAltaLibro);
  frmListado.btnListado.addEventListener("click", procesarListado);
}

/* ===========================================================================
   APARTADO B.1 (1,5 puntos)
   Visibilidad de los formularios (nunca a la vez) y carga de los desplegables
   =========================================================================== */

function mostrarFormulario(oEvento) {
  oEvento.preventDefault(); // los <a href="#"> saltarian al inicio de la pagina

  // Patron "ocultar todo y luego mostrar uno": no depende del estado anterior
  // y garantiza que los dos formularios nunca se vean a la vez.
  ocultarFormularios();

  switch (oEvento.target.id) {
    case "mnuCrearLibro":
      cargarDesplegables();
      frmCrear.classList.remove("d-none"); // d-none = display:none de Bootstrap
      break;
    case "mnuListado":
      cargarDesplegables();
      frmListado.classList.remove("d-none");
      break;
  }
}

function ocultarFormularios() {
  frmCrear.classList.add("d-none");
  frmListado.classList.add("d-none");
}

/* Rellena LOS DOS <select> de genero con los generos de la BD.
   TRAMPA: en index.html hay dos elementos con id="lstGenero", asi que
   getElementById("lstGenero") solo devolveria el primero. Se accede a traves
   de cada formulario: frm.lstGenero (busca por el atributo name). */
async function cargarDesplegables() {
  let respuesta = await oLibreria.getGeneros(); // await: sin el, respuesta seria una Promesa

  if (respuesta.ok) {
    let options = "";
    for (let genero of respuesta.datos) {
      // value = idgenero (lo que se guarda en BD); texto visible = nombre del genero
      options += `<option value="${genero.idgenero}">${genero.genero}</option>`;
    }
    // Se acumula en un string y se asigna una sola vez
    frmCrear.lstGenero.innerHTML = options;
    frmListado.lstGenero.innerHTML = options;
  } else {
    // Si !ok, respuesta.datos es null: no se debe recorrer
    alert("No se han podido cargar los generos literarios: " + respuesta.mensaje);
  }
}

/* ===========================================================================
   APARTADO B.2 (2 puntos)
   Crear un libro en la base de datos
   - Sin validacion de datos (lo dice el enunciado).
   - Exito o error: SIEMPRE un alert con el mensaje del servidor.
   - Exito  -> resetear el formulario y ocultarlo.
   - Error  -> no hacer nada mas: el formulario conserva los datos.
   =========================================================================== */

async function procesarAltaLibro() {
  // 1) Leer los campos del formulario (trim quita espacios sobrantes)
  let titulo = frmCrear.txtTitulo.value.trim();
  let autor = frmCrear.txtAutor.value.trim();
  let descripcion = frmCrear.txtDescripcion.value.trim();
  let imagen = frmCrear.txtImagen.value.trim();
  let idgenero = frmCrear.lstGenero.value; // string: es el value del <option> elegido

  // 2) Construir el objeto. idlibro = null porque lo genera la BD (AUTO_INCREMENT)
  let oLibro = new Libro(null, idgenero, titulo, autor, descripcion, imagen);

  // 3) Llamar al servidor y ESPERAR la respuesta
  let respuesta = await oLibreria.crearLibro(oLibro);

  // 4) Informar siempre
  alert(respuesta.mensaje);

  // 5) Solo si ha ido bien: resetear y ocultar
  if (respuesta.ok) {
    frmCrear.reset();
    frmCrear.classList.add("d-none");
  }
}

/* ===========================================================================
   APARTADO B.3 (2,5 puntos) - parte de index.html
   Abre una pestana nueva (tarjetas.html) pasando por la URL el id del genero
   (para consultar) y su nombre (para el titulo). El resto del apartado
   (pedir los libros y pintar las tarjetas) esta en tarjetas.html.
   =========================================================================== */

function procesarListado() {
  let lst = frmListado.lstGenero;
  let idgenero = lst.value; // id del <option> seleccionado
  let genero = lst.options[lst.selectedIndex].textContent; // texto visible = nombre

  // open() sin await previo: asi el navegador no bloquea la ventana emergente.
  // encodeURIComponent protege nombres con espacios o tildes ("Ciencia ficción").
  open(
    "tarjetas.html?idgenero=" + encodeURIComponent(idgenero) +
      "&genero=" + encodeURIComponent(genero),
  );
}
