<?php
require_once('config.php');

// Los campos llegan por POST (FormData): cliente, idplato, unidades. idpedido es autonumerico.
$cliente  = trim($_POST["cliente"] ?? "");
$idplato  = (int)($_POST["idplato"] ?? 0);
$unidades = (int)($_POST["unidades"] ?? 0);

$conexion = obtenerConexion();
// Sentencia preparada: los datos van separados de la SQL (evita inyeccion y problemas con apostrofos)
$stmt = $conexion->prepare("INSERT INTO pedidos (cliente, idplato, unidades) VALUES (?, ?, ?)");
$stmt->bind_param("sii", $cliente, $idplato, $unidades);

if ($stmt->execute()) {
    responder(null, false, "Pedido registrado correctamente", $conexion);
} else {
    responder(null, true, "No se ha podido registrar el pedido: " . $stmt->error, $conexion);
}
