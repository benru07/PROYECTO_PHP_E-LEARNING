<?php
session_start();
$idmodulo = $_SESSION["idmodulo"];

require_once("config.php");
$conexion = obtenerConexion();


$idmodulo = $_POST['idmodulo'];

if ($idmodulo != $idmodulo) {
    $mensaje =  "<h2 class='text-center mt-5'>idmodulo incorrecto</h2>";

} else {
    $sql = "DELETE FROM modulos WHERE id = '" .$idmodulo. "'";

    $resultado = mysqli_query($conexion, $sql);

    if (mysqli_errno($conexion) != 0) {
        $numerror = mysqli_errno($conexion);
        $descrerror = mysqli_error($conexion);
        $mensaje =  "<h2 class='text-center mt-5'>Se ha producido un error numero $numerror que corresponde a: $descrerror </h2>";
    } else {
        $mensaje =  "<h2 class='text-center mt-5'>Módulo eliminado</h2>";
    }

}

header("refresh:5;url=index.php");


// Aquí empieza la página
include_once("cabecera.html");

// Mostrar mensaje calculado antes
echo $mensaje;
?>