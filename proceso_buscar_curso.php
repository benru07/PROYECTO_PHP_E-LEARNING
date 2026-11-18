<?php
require_once("config.php");
require_once("funcionesCursos.php");
$conexion = obtenerConexion();

// Recuperar parámetro
$titulo_curso = $_GET['txtTituloCurso'];

$sql = "SELECT c.*, ca.nombre AS categoria
        FROM cursos c, categorias ca
        WHERE c.categoria_id = ca.id
        AND c.titulo LIKE ?
        ORDER BY c.id ASC;";

$stmt = mysqli_prepare($conexion, $sql);
$patron = "%" . $titulo_curso . "%";
mysqli_stmt_bind_param($stmt, "s", $patron);
mysqli_stmt_execute($stmt);
$resultado = mysqli_stmt_get_result($stmt);

if (mysqli_num_rows($resultado) > 0) {

    $mensaje = "<h2 class='text-center'>Listado de cursos</h2>";
    $mensaje .= construirTablaCursos($resultado); // incluye los botones de editar y borrar
} else {
    $mensaje = "<h2 class='text-center'>No hay cursos con ese título</h2>";
}

include_once("cabecera.html");

// Mostrar mensaje calculado antes
echo "<div class='container'>" . $mensaje . "</div>";
?>
</body>

</html>
