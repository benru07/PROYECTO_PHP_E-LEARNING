<?php
function obtenerConexion() {
    // Establecer conexión y opciones de mysql
    // Errores mysql sin excepciones
    mysqli_report(MYSQLI_REPORT_OFF);

    // Importante, ajustar los siguientes parámetros
    $conexion = new mysqli("db", "root", "test", "empresa","3306");
    // $conexion = mysqli_connect('host', 'usuario', 'contraseña', "data-base","puerto");
    mysqli_set_charset($conexion, 'utf8');

    return $conexion;
}