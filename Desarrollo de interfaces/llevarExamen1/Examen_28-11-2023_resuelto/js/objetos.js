"use strict";

/* ===========================================================================
   APARTADO c) (2 puntos) - objetos.js
   Clases con los mismos atributos que las columnas de la BD.
   =========================================================================== */

/* c.i) Clase Pedido: constructor + propiedades get/set (tabla `pedidos`)
   OJO: el enunciado NO pide toJSON(). Con campos privados (#), JSON.stringify(oPedido)
   daria "{}". Por eso altaPedido() envia cada campo por separado usando los getters. */
class Pedido {
  #idpedido;
  #cliente;
  #idplato;
  #unidades;

  constructor(idpedido, cliente, idplato, unidades) {
    this.#idpedido = idpedido;
    this.#cliente = cliente;
    this.#idplato = idplato;
    this.#unidades = unidades;
  }

  get idpedido() { return this.#idpedido; }
  set idpedido(valor) { this.#idpedido = valor; }

  get cliente() { return this.#cliente; }
  set cliente(valor) { this.#cliente = valor; }

  get idplato() { return this.#idplato; }
  set idplato(valor) { this.#idplato = valor; }

  get unidades() { return this.#unidades; }
  set unidades(valor) { this.#unidades = valor; }
}

/* c.ii) Clase Plato: constructor + propiedades get/set (tabla `platos`) */
class Plato {
  #idplato;
  #nombre;
  #descripcion;
  #precio;

  constructor(idplato, nombre, descripcion, precio) {
    this.#idplato = idplato;
    this.#nombre = nombre;
    this.#descripcion = descripcion;
    this.#precio = precio;
  }

  get idplato() { return this.#idplato; }
  set idplato(valor) { this.#idplato = valor; }

  get nombre() { return this.#nombre; }
  set nombre(valor) { this.#nombre = valor; }

  get descripcion() { return this.#descripcion; }
  set descripcion(valor) { this.#descripcion = valor; }

  get precio() { return this.#precio; }
  set precio(valor) { this.#precio = valor; }
}

/* c.iii) Clase Bar: los tres metodos del enunciado.
   IMPORTANTE (distinto al examen de la libreria): en este examen la respuesta del
   servidor es { error, mensaje, datos } y error === true significa FALLO.
   Los tipos de retorno son distintos en cada metodo:
     altaPedido            -> la respuesta completa { error, mensaje, datos }
     getPlatos             -> un array de filas (respuesta.datos)
     listadoPedidosCliente -> un string con la tabla HTML */
class Bar {
  /* (1) altaPedido(oPedido): respuesta (error, mensaje, datos) */
  async altaPedido(oPedido) {
    let datos = new FormData();
    // No se envia idpedido: es autonumerico y lo genera la BD
    datos.append("cliente", oPedido.cliente);
    datos.append("idplato", oPedido.idplato);
    datos.append("unidades", oPedido.unidades);

    return await peticionPOST("alta_pedido.php", datos);
  }

  /* (2) getPlatos(): array de filas de la tabla platos
     Si el servidor da error se lanza una excepcion con su mensaje y quien llama
     (codigo.js) la captura con try/catch y la muestra. (Version minima si el
     tiempo apremia: return (await peticionGET(...)).datos; ) */
  async getPlatos() {
    let respuesta = await peticionGET("get_platos.php", new FormData()); // FormData vacio, no undefined

    if (respuesta.error) {
      throw new Error(respuesta.mensaje);
    }
    return respuesta.datos; // [ {idplato, nombre, descripcion, precio}, ... ]
  }

  /* (3) listadoPedidosCliente(nombreCliente): string (tabla HTML con el listado)
     El servidor devuelve los pedidos del cliente YA ordenados por idpedido (ORDER BY)
     y con el nombre y el precio del plato (JOIN). Aqui solo se construye el HTML,
     siguiendo la estructura de ejemplo_listado.html. */
  async listadoPedidosCliente(nombreCliente) {
    let datos = new FormData();
    datos.append("nombreCliente", nombreCliente); // llega a PHP en $_GET["nombreCliente"]

    let respuesta = await peticionGET("get_pedidos_cliente.php", datos);

    if (respuesta.error) {
      throw new Error(respuesta.mensaje);
    }

    // Titulo con el nombre introducido (ej.: "PEDIDOS DE Antonio")
    let html = `<h3>PEDIDOS DE ${this.#escapar(nombreCliente)}</h3>`;

    // Caso limite: cliente sin pedidos
    if (respuesta.datos.length == 0) {
      return html + "<p>Este cliente no tiene pedidos.</p>";
    }

    // Tabla con el estilo de Bootstrap: class="table table-striped"
    html += `<table class="table table-striped">
               <thead>
                 <tr><th>IDPEDIDO</th><th>PLATO</th><th>UNIDADES</th><th>PRECIO</th><th>TOTAL</th></tr>
               </thead>
               <tbody>`;

    for (let fila of respuesta.datos) {
      // DECIMAL llega como string ("5.50"): se convierte a numero antes de operar
      let precio = Number(fila.precio);
      let unidades = Number(fila.unidades);
      html += `<tr>
                 <td>${fila.idpedido}</td>
                 <td>${fila.nombre}</td>
                 <td>${unidades}</td>
                 <td>${precio.toFixed(2)}</td>
                 <td>${(unidades * precio).toFixed(2)}</td>
               </tr>`;
    }

    html += "</tbody></table>";
    return html;
  }

  /* Evita que lo escrito por el usuario se interprete como HTML al usar innerHTML */
  #escapar(texto) {
    return String(texto)
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;");
  }
}
