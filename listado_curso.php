<?php
require_once('config.php');
require_once('funcionesCursos.php');
$conexion = obtenerConexion();

// Todos los cursos, junto con el nombre de su categoría
$sql = "SELECT c.*, ca.nombre AS categoria
        FROM cursos c, categorias ca
        WHERE c.categoria_id = ca.id
        ORDER BY c.id ASC;";

$resultado = mysqli_query($conexion, $sql);

// Montar tabla
$mensaje = "<h2 class='text-center'>Listado de cursos</h2>";
$mensaje .= construirTablaCursos($resultado);

include_once("cabecera.html");

echo "<div class='container'>" . $mensaje . "</div>";

?>
</body>

</html>
