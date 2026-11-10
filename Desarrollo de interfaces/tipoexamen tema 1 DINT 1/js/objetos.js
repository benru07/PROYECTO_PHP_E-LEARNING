"use strict";

// Codificar aquí las clases
class genero {
    constructor(idgenero, genero) {
        this._idgenero = idgenero;
        this._genero = genero;
    }

    get idgenero(){
        return this._idgenero;
    }

    set idgenero(idgenero){
        this._idgenero = idgenero;
    }

    get genero(){
        return this._genero;
    }

    set genero(genero){
        this._genero = genero;
    }

    toJSON(){
        return{
            idgenero: this._idgenero,
            genero: this._genero
        };
    }
}

class libro {
    constructor(idlibro, idgenero, titulo, autor, descripcion, imagen) {
        this._idlibro = idlibro;
        this._idgenero = idgenero;
        this._titulo = titulo;
        this._autor = autor;
        this._descripcion = descripcion;
        this._imagen = imagen;
    }

    get idlibro(){
        return this._idlibro;
    }

    set idlibro(idlibro){
        this._idlibro = idlibro;
    }

    get idgenero(){
        return this._idgenero;
    }

    set idgenero(idgenero){
        this._idgenero = idgenero;
    }

    get titulo(){
        return this._titulo;
    }

    set titulo(titulo){
        this._titulo = titulo;
    }

    get autor(){
        return this._autor;
    }

    set autor(autor){
        this._autor = autor;
    }

    get descripcion(){
        return this._descripcion;
    }

    set descripcion(descripcion){
        this._descripcion = descripcion;
    }

    get imagen(){
        return this._imagen;
    }

    set imagen(imagen){
        this._imagen = imagen;
    }

    toJSON(){
        return{
            idlibro: this._idlibro,
            idgenero: this._idgenero,
            titulo: this._titulo,
            autor: this._autor,
            descripcion: this._descripcion,
            imagen: this._imagen
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

class GestorNotas {

    function crearLibro(oLibro) {
        fetch("php/crear_libro.php")

        return oRespuesta = new respuesta("ok","mensaje","datos")
    }

    function getLibrosGenero(idgenero){
        fetch("php/get_libros_genero.php")

        return oRespuesta = new respuesta("ok","mensaje","datos")
    }

    function getGeneros(){
        fetch("php/get_generos.php")

        return oRespuesta = new respuesta("ok","mensaje","datos")
    }
}