<?php
require_once('config.php');
$conexion = obtenerConexion();


$nombre = $_POST['txtNombre'];
$descripcion = $_POST['txtDescripcion'];
$orden = (int)$_POST['intorden'];
$activa = isset($_POST['activa']) ? 1 : 0;
$fecha_creacion = date('Y-m-d');
$dificultad = $_POST['lstDificultades'];

$sql = "INSERT INTO `categorias` (`id`, `nombre`, `descripcion`, `orden`, `activa`, `fecha_creacion`, `dificultad_id`) 
                        VALUES (NULL, '$nombre', '$descripcion', '$orden', '$activa', '$fecha_creacion', '$dificultad')";

$consulta = mysqli_query($conexion, $sql);

if (mysqli_errno($conexion) != 0) {
    $numerror = mysqli_errno($conexion);
    $descrerror = mysqli_error($conexion);
    $mensaje =  "<h2 class='text-center mt-5'>Se ha producido un error numero $numerror que corresponde a: $descrerror </h2>";
} else {
    $mensaje =  "<h2 class='text-center mt-5'>Categoria insertado</h2>";
}




include_once("cabecera.html");

echo $mensaje;

header("refresh:5;url=index.php");
?>
