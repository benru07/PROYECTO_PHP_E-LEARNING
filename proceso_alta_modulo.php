<?php
require_once('config.php');
$conexion = obtenerConexion();


$nombre = $_POST['txtNombre'];
$descripcion = $_POST['txtDescripcion'];
$orden = (int)$_POST['intorden'];
$curso_id = (int)$_POST['lstCursos'];
$obligatorio = isset($_POST['obligatorio']) ? 1 : 0;
$fecha_creacion = date('Y-m-d');

$sql = "INSERT INTO `modulos` (`curso_id`, `titulo`, `descripcion`, `orden`, `obligatorio`, `fecha_creacion`)
    VALUES ('$curso_id', '$nombre', '$descripcion', '$orden', '$obligatorio', '$fecha_creacion')";

$consulta = mysqli_query($conexion, $sql);

if (mysqli_errno($conexion) != 0) {
    $numerror = mysqli_errno($conexion);
    $descrerror = mysqli_error($conexion);
    $mensaje =  "<h2 class='text-center mt-5'>Se ha producido un error numero $numerror que corresponde a: $descrerror </h2>";
} else {
    $mensaje =  "<h2 class='text-center mt-5'>Módulo insertado</h2>";
}




include_once("cabecera.html");

echo $mensaje;

header("refresh:5;url=index.php");
?>
