<?php
session_start();
$idmodulo = $_SESSION["idmodulo"];

require_once("config.php");
$conexion = obtenerConexion();

$titulo = $_POST['txtTitulo'];
$descripcion = $_POST['txtDescripcion'];
$orden = (int)$_POST['intorden'];
$obligatorio = isset($_POST['obligatorio']) ? 1 : 0;
$fecha_creacion = date('Y-m-d');
$cursos = $_POST['lstCursos'];
$idmodulo = $_POST['idmodulo'];


if ($idmodulo != $idmodulo) {
    $mensaje =  "<h2 class='text-center mt-5'>idmodulo incorrecto</h2>";

} else {
    $sql = "UPDATE modulos SET titulo = '" .$titulo. "', descripcion = '" .$descripcion. "', orden = '" .$orden. "', obligatorio = '" .$obligatorio. "', curso_id = '" .$cursos. "' 
    WHERE id = '" .$idmodulo. "'";

    $resultado = mysqli_query($conexion, $sql);

    if (mysqli_errno($conexion) != 0) {
        $numerror = mysqli_errno($conexion);
        $descrerror = mysqli_error($conexion);
        $mensaje =  "<h2 class='text-center mt-5'>Se ha producido un error numero $numerror que corresponde a: $descrerror </h2>";
    } else {
        $mensaje =  "<h2 class='text-center mt-5'>Módulo editado</h2>";
    }

}

header("refresh:5;url=index.php");

// Aquí empieza la página
include_once("cabecera.html");

// Mostrar mensaje calculado antes
echo $mensaje;
?>  