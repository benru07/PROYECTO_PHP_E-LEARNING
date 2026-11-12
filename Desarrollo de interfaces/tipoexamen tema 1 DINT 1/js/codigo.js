"use strict";

// Codificar aquí el programa principal

//MAIN
var oLibreria = new Librerria();


registrarEventos();

function registrarEventos(){
    //Opciones de menu
    document.getElementById("mnuCrearLibro").addEventListener("click", mostrarFormulario)
    document.getElementById("mnuListado").addEventListener("click", mostrarFormulario)
}

function mostrarFormulario(oEvento){
    let opcion = oEvento.target.id;

    ocultarFormularios();

    switch(opcion){
        case"mnuCrearLibro":
        cargargarDesplegables();
            frmCrearLibro.classList.remove("d-none");
            break;
        
        case"mnuListado":
        cargargarDesplegables();
            frmListado.classList.remove("d-none");
            break;
    }
}

function ocultarFormularios(){
    frmCrearLibro.classList.add("d-none");
    frmListado.classList.add("d-none");
}

async function cargargarDesplegables(){
    let resouesto = await oLibreria.getGeneros();
    let options ="";
    

    if (respuesta.ok){
        for (let genero of respuesta.datos){
        options += `<option value="${genero.idGenero}"> ${genero.genero} </opiton>`;
    }

    frmCrearLibro.lstGenero.innerHTML = options;
    frmListado.lstGenero.innerHTML = options;
    } else{
        alert("No se ha podido cargar los generos literario: " + respuesta.mendaje);
    }
}