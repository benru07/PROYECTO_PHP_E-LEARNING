<?php
session_start();
$idcurso_sesion = $_SESSION["idcurso"] ?? null; // id del curso que se abrió en editar_curso.php

require_once("config.php");
$conexion = obtenerConexion();

$idcurso = $_POST['idcurso'];
$idcategoria = $_POST['lstCategoria'];
$titulo = $_POST['txtTitulo'];
$descripcion = $_POST['txtDescripcion'];
$nivel = $_POST['lstNivel'];
$duracion = (int) $_POST['intduracion'];
$precio = (float) $_POST['decprecio'];
$fecha_publicacion = $_POST['fecha'];
$publicado = isset($_POST['publicado']) ? 1 : 0;

// Solo se puede modificar el curso que se abrió para editar
if ($idcurso_sesion != $idcurso) {
    $mensaje =  "<h2 class='text-center mt-5'>idcurso incorrecto</h2>";

} else {
    $sql = "UPDATE cursos
            SET categoria_id = ?, titulo = ?, descripcion = ?, nivel = ?, duracion_horas = ?,
                precio = ?, publicado = ?, fecha_publicacion = ?
            WHERE id = ?";

    $stmt = mysqli_prepare($conexion, $sql);
    mysqli_stmt_bind_param($stmt, "isssidisi", $idcategoria, $titulo, $descripcion, $nivel, $duracion, $precio, $publicado, $fecha_publicacion, $idcurso);
    mysqli_stmt_execute($stmt);

    if (mysqli_stmt_errno($stmt) != 0) {
        $numerror = mysqli_stmt_errno($stmt);
        $descrerror = mysqli_stmt_error($stmt);
        $mensaje =  "<h2 class='text-center mt-5'>Se ha producido un error numero $numerror que corresponde a: $descrerror </h2>";
    } else {
        $mensaje =  "<h2 class='text-center mt-5'>Curso editado</h2>";
    }

}

header("refresh:5;url=index.php");

// Aquí empieza la página
include_once("cabecera.html");

// Mostrar mensaje calculado antes
echo "<div class='container'>" . $mensaje . "</div>";
?>
</body>

</html>
