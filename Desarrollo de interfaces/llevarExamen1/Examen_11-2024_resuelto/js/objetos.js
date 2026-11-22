"use strict";

/* ===========================================================================
   APARTADO A (2 puntos) - objetos.js
   Clases Libro, Genero y la clase gestora que habla con el servidor.
   =========================================================================== */

/* ---------------------------------------------------------------------------
   A.1  Clase Libro: constructor + propiedades get/set + toJSON()
   - Un atributo privado (#) por cada columna de la tabla `libro`.
   - El constructor recibe los datos EN EL MISMO ORDEN que las columnas.
   - Dentro de los getters/setters siempre this.#campo (si usaras this.titulo
     dentro del getter "titulo" habria recursion infinita).
   --------------------------------------------------------------------------- */
class Libro {
  #idlibro;
  #idgenero;
  #titulo;
  #autor;
  #descripcion;
  #imagen;

  constructor(idlibro, idgenero, titulo, autor, descripcion, imagen) {
    this.#idlibro = idlibro;
    this.#idgenero = idgenero;
    this.#titulo = titulo;
    this.#autor = autor;
    this.#descripcion = descripcion;
    this.#imagen = imagen;
  }

  get idlibro() { return this.#idlibro; }
  set idlibro(valor) { this.#idlibro = valor; }

  get idgenero() { return this.#idgenero; }
  set idgenero(valor) { this.#idgenero = valor; }

  get titulo() { return this.#titulo; }
  set titulo(valor) { this.#titulo = valor; }

  get autor() { return this.#autor; }
  set autor(valor) { this.#autor = valor; }

  get descripcion() { return this.#descripcion; }
  set descripcion(valor) { this.#descripcion = valor; }

  get imagen() { return this.#imagen; }
  set imagen(valor) { this.#imagen = valor; }

  /* toJSON(): JSON.stringify() no ve los campos privados (#) y devolveria "{}".
     Al existir toJSON(), JSON.stringify la invoca y serializa lo que devuelve.
     Las CLAVES deben coincidir con las columnas: PHP lee $libro->idgenero, etc. */
  toJSON() {
    return {
      idlibro: this.#idlibro,
      idgenero: this.#idgenero,
      titulo: this.#titulo,
      autor: this.#autor,
      descripcion: this.#descripcion,
      imagen: this.#imagen,
    };
  }
}

/* ---------------------------------------------------------------------------
   A.2  Clase Genero: misma idea, solo 2 columnas (idgenero, genero)
   --------------------------------------------------------------------------- */
class Genero {
  #idgenero;
  #genero;

  constructor(idgenero, genero) {
    this.#idgenero = idgenero;
    this.#genero = genero;
  }

  get idgenero() { return this.#idgenero; }
  set idgenero(valor) { this.#idgenero = valor; }

  get genero() { return this.#genero; }
  set genero(valor) { this.#genero = valor; }

  toJSON() {
    return {
      idgenero: this.#idgenero,
      genero: this.#genero,
    };
  }
}

/* ---------------------------------------------------------------------------
   A.3  Clase gestora
   NOTA: el enunciado la llama "GestorNotas" (parece un despiste copiado de otro
   examen); aqui se llama "Libreria", como en la solucion oficial. Si te piden
   GestorNotas, cambia el nombre aqui y en codigo.js / tarjetas.html.

   Reglas comunes a los 3 metodos:
   - Son async porque fetch es asincrono: devuelven una Promesa.
   - Empaquetan los parametros en un FormData (lo que esperan peticionGET/POST).
   - NO tocan el DOM: solo devuelven la respuesta { ok, mensaje, datos }.
   --------------------------------------------------------------------------- */
class Libreria {
  /* a) crearLibro(oLibro) -> POST crear_libro.php
        El objeto viaja como UN solo campo de texto ("libro") con su JSON;
        PHP lo reconstruye con json_decode($_POST["libro"]). */
  async crearLibro(oLibro) {
    let datos = new FormData();
    datos.append("libro", JSON.stringify(oLibro)); // objeto JS -> string JSON (usa toJSON)

    return await peticionPOST("crear_libro.php", datos);
  }

  /* b) getLibrosGenero(idgenero) -> GET get_libros_genero.php
        El servidor devuelve los libros con el nombre del genero (JOIN). */
  async getLibrosGenero(idgenero) {
    let datos = new FormData();
    datos.append("idgenero", idgenero); // llega a PHP en $_GET["idgenero"]

    return await peticionGET("get_libros_genero.php", datos);
  }

  /* c) getGeneros() -> GET get_generos.php
        Sin parametros, pero peticionGET recorre el FormData con for...of:
        hay que pasarle un FormData vacio (no undefined). */
  async getGeneros() {
    return await peticionGET("get_generos.php", new FormData());
  }
}
