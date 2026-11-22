<?php
function obtenerConexion() {
    // Establecer conexión y opciones de mysql
    // Errores mysql sin excepciones
    mysqli_report(MYSQLI_REPORT_OFF);

    // Importante, ajustar los siguientes parámetros
    $conexion = new mysqli("sql103.infinityfree.com", "if0_43430422", "yNpUI7txqO", "if0_43430422_php_cursos","3306");
    // $conexion = mysqli_connect('host', 'usuario', 'contraseña', "data-base","puerto");
    mysqli_set_charset($conexion, 'utf8');

    return $conexion;
}
?>
