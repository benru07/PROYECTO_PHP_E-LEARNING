"use strict";

// Codificar aquí las clases
class genero {

    #idgenero;
    #genero;

    constructor(idgenero, genero) {
        this.#idgenero = idgenero;
        this.#genero = genero;
    }

    get idgenero(){
        return this.#idgenero;
    }

    set idgenero(idgenero){
        this.#idgenero = idgenero;
    }

    get genero(){
        return this.#genero;
    }

    set genero(genero){
        this.#genero = genero;
    }

    toJSON(){
        return{
            idgenero: this.#idgenero,
            genero: this.#genero
        };
    }
}

class libro {

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

    get idlibro(){
        return this.#idlibro;
    }

    set idlibro(idlibro){
        this.#idlibro = idlibro;
    }

    get idgenero(){
        return this.#idgenero;
    }

    set idgenero(idgenero){
        this.#idgenero = idgenero;
    }

    get titulo(){
        return this.#titulo;
    }

    set titulo(titulo){
        this.#titulo = titulo;
    }

    get autor(){
        return this.#autor;
    }

    set autor(autor){
        this.#autor = autor;
    }

    get descripcion(){
        return this.#descripcion;
    }

    set descripcion(descripcion){
        this.#descripcion = descripcion;
    }

    get imagen(){
        return this.#imagen;
    }

    set imagen(imagen){
        this.#imagen = imagen;
    }

    toJSON(){
        return{
            idlibro: this.#idlibro,
            idgenero: this.#idgenero,
            titulo: this.#titulo,
            autor: this.#autor,
            descripcion: this.#descripcion,
            imagen: this.#imagen
        };
    }
}

class respuesta {
    constructor(ok, mensaje, datos) {
        this._ok = ok;
        this._mensaje = mensaje;
        this._datos = datos;
    }

    get ok(){
        return this._ok;
    }

    set ok(ok){
        this._ok = ok;
    }

    get mensaje(){
        return this._mensaje;
    }

    set mensaje(mensaje){
        this._mensaje = mensaje;
    }

    get datos(){
        return this._datos;
    }

    set datos(datos){
        this._datos = datos;
    }

    toJSON(){
        return{
            ok: this._ok,
            mensaje: this._mensaje,
            datos: this._datos
        };
    }
}

class Librerria {

    async crearLibro(oLibro) {
        /*fetch("php/crear_libro.php")

        return oRespuesta = new respuesta("ok","mensaje","datos")*/
    }

    async getLibrosGenero(idgenero){
        /*fetch("php/get_libros_genero.php")

        return oRespuesta = new respuesta("ok","mensaje","datos")*/
    }

    async getGeneros(){
        //para las peticiones php solo tengo que ir al repositorio de dit  DINT_OPT0/PHP/app_bd_hlc
        return await peticionGET("get_generos.php" , new FormData());
    }
}