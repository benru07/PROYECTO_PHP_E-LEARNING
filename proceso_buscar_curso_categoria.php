<?php
require_once('config.php');
require_once('funcionesCursos.php');
$conexion = obtenerConexion();

// Nombre de la categoría elegida en la lista desplegable
$nombre_categoria = $_GET['lstCategoria'];

$sql = "SELECT c.*, ca.nombre AS categoria
        FROM cursos c, categorias ca
        WHERE c.categoria_id = ca.id
        AND ca.nombre = ?
        ORDER BY c.id ASC;";

$stmt = mysqli_prepare($conexion, $sql);
mysqli_stmt_bind_param($stmt, "s", $nombre_categoria);
mysqli_stmt_execute($stmt);
$resultado = mysqli_stmt_get_result($stmt);

if (mysqli_num_rows($resultado) > 0) {

    // Montar tabla
    $mensaje = "<h2 class='text-center'>Cursos de la categoría " . htmlspecialchars($nombre_categoria) . "</h2>";
    $mensaje .= construirTablaCursos($resultado);
} else {
    $mensaje = "<h2 class='text-center'>No hay cursos en la categoría " . htmlspecialchars($nombre_categoria) . "</h2>";
}

include_once("cabecera.html");

echo "<div class='container'>" . $mensaje . "</div>";

?>
</body>

</html>
