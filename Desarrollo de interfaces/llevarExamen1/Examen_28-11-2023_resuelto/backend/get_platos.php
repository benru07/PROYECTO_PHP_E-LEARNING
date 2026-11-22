<?php
require_once('config.php');

$conexion = obtenerConexion();
$resultado = $conexion->query("SELECT idplato, nombre, descripcion, precio FROM platos ORDER BY idplato");
if ($resultado === false) {
    responder(null, true, "Error en la base de datos: " . $conexion->error, $conexion);
}
$datos = [];
while ($fila = $resultado->fetch_assoc()) { $datos[] = $fila; }
responder($datos, false, "Platos recuperados correctamente", $conexion);
