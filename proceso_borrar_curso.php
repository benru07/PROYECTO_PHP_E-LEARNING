<?php
require_once("config.php");
$conexion = obtenerConexion();

$idcurso = $_POST['idcurso'];

// Los módulos y lecciones del curso se borran automáticamente (ON DELETE CASCADE)
$sql = "DELETE FROM cursos WHERE id = ?";

$stmt = mysqli_prepare($conexion, $sql);
mysqli_stmt_bind_param($stmt, "i", $idcurso);
mysqli_stmt_execute($stmt);

if (mysqli_stmt_errno($stmt) != 0) {
    $numerror = mysqli_stmt_errno($stmt);
    $descrerror = mysqli_stmt_error($stmt);
    $mensaje =  "<h2 class='text-center mt-5'>Se ha producido un error numero $numerror que corresponde a: $descrerror </h2>";
} else {
    $mensaje =  "<h2 class='text-center mt-5'>Curso eliminado</h2>";
}

header("refresh:5;url=index.php");

// Aquí empieza la página
include_once("cabecera.html");

// Mostrar mensaje calculado antes
echo "<div class='container'>" . $mensaje . "</div>";
?>
</body>

</html>
