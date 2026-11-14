<?php
session_start();
$idcategoria = $_SESSION["idcategoria"];

require_once("config.php");
$conexion = obtenerConexion();

$nombre = $_POST['txtNombre'];
$descripcion = $_POST['txtDescripcion'];
$orden = (int)$_POST['intorden'];
$activa = isset($_POST['activa']) ? 1 : 0;
$fecha_creacion = date('Y-m-d');
$idcategoria = $_POST['idcategoria'];

if ($idcategoria != $idcategoria) {
    $mensaje =  "<h2 class='text-center mt-5'>idcategoria incorrecto</h2>";

} else {
    $sql = "UPDATE categorias SET  	nombre = '" .$nombre. "', descripcion = '" .$descripcion. "', orden = " .$orden. ", activa = " .$activa. " 
    WHERE id = " .$idcategoria;

    $resultado = mysqli_query($conexion, $sql);

    if (mysqli_errno($conexion) != 0) {
        $numerror = mysqli_errno($conexion);
        $descrerror = mysqli_error($conexion);
        $mensaje =  "<h2 class='text-center mt-5'>Se ha producido un error numero $numerror que corresponde a: $descrerror </h2>";
    } else {
        $mensaje =  "<h2 class='text-center mt-5'>Categoria editado</h2>";
    }

}

header("refresh:5;url=index.php");

// Aquí empieza la página
include_once("cabecera.html");

// Mostrar mensaje calculado antes
echo $mensaje;