<?php
/* config.php - conexion y respuesta JSON del backend de ejemplo (base de datos `bar`).
   NOTA: el zip del examen NO incluye PHP; estos scripts son de apoyo para poder probar el
   proyecto. Formato de respuesta de ESTE examen: { error: bool, mensaje: string, datos: any }
   (error === true significa fallo). Ajusta servidor/usuario/password a tu entorno. */
$basedatos = array(
    "basedatos" => "bar",
    "usuario"   => "root",
    "password"  => "test",
    "servidor"  => "db",       // en XAMPP suele ser "localhost"
    "puerto"    => 3306
);

error_reporting(E_ERROR | E_PARSE);
mysqli_report(MYSQLI_REPORT_OFF);

function obtenerConexion()
{
    global $basedatos;
    $conexion = new mysqli($basedatos["servidor"], $basedatos["usuario"], $basedatos["password"],
                           $basedatos["basedatos"], $basedatos["puerto"]);
    if ($conexion->connect_errno) {
        responder(null, true, "Error de conexion: " . $conexion->connect_error);
    }
    $conexion->set_charset("utf8mb4");
    return $conexion;
}

function responder($datos, $error, $mensaje, $conexion = null)
{
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode(["error" => $error, "mensaje" => $mensaje, "datos" => $datos], JSON_UNESCAPED_UNICODE);
    if ($conexion) { $conexion->close(); }
    exit;
}
