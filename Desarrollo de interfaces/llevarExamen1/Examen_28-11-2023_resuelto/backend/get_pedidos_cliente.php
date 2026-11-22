<?php
require_once('config.php');

$nombreCliente = trim($_GET["nombreCliente"] ?? "");

$conexion = obtenerConexion();
// JOIN con platos para tener nombre y precio; ORDER BY idpedido ASC (lo exige el enunciado).
// La coleccion utf8mb4_spanish_ci ignora mayusculas/acentos y los espacios finales ('Paco ' = 'Paco').
$stmt = $conexion->prepare(
    "SELECT p.idpedido, p.cliente, p.idplato, pl.nombre, p.unidades, pl.precio
       FROM pedidos p INNER JOIN platos pl ON p.idplato = pl.idplato
      WHERE p.cliente = ?
      ORDER BY p.idpedido ASC");
$stmt->bind_param("s", $nombreCliente);
$stmt->execute();
$resultado = $stmt->get_result();

$datos = [];
while ($fila = $resultado->fetch_assoc()) { $datos[] = $fila; }
responder($datos, false, "Pedidos recuperados correctamente", $conexion);
