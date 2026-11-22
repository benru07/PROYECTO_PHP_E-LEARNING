<?php
require_once('config.php');
$conexion = obtenerConexion();

// Recuperar parámetros
$idcategoria = $_POST['lstCategoria'];
$titulo = $_POST['txtTitulo'];
$descripcion = $_POST['txtDescripcion'];
$nivel = $_POST['lstNivel'];
$duracion = (int) $_POST['intduracion'];
$precio = (float) $_POST['decprecio'];
$fecha_publicacion = $_POST['fecha'];
$publicado = isset($_POST['publicado']) ? 1 : 0; // un checkbox solo se envía si está marcado

// Consulta preparada: los datos nunca se concatenan en el SQL
$sql = "INSERT INTO cursos (categoria_id, titulo, descripcion, nivel, duracion_horas, precio, publicado, fecha_publicacion)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

$stmt = mysqli_prepare($conexion, $sql);
mysqli_stmt_bind_param($stmt, "isssidis", $idcategoria, $titulo, $descripcion, $nivel, $duracion, $precio, $publicado, $fecha_publicacion);
mysqli_stmt_execute($stmt);

if (mysqli_stmt_errno($stmt) != 0) {
    $numerror = mysqli_stmt_errno($stmt);
    $descrerror = mysqli_stmt_error($stmt);
    $mensaje =  "<h2 class='text-center mt-5'>Se ha producido un error numero $numerror que corresponde a: $descrerror </h2>";
} else {
    $mensaje =  "<h2 class='text-center mt-5'>Curso insertado</h2>";
}

// Redireccionar tras 5 segundos al index.
// Siempre debe ir antes de imprimir cualquier cosa (si no, el header no funciona)
header("refresh:5;url=index.php");

include_once("cabecera.html");

echo "<div class='container'>" . $mensaje . "</div>";
?>
</body>

</html>
