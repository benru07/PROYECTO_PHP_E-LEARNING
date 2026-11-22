<?php
require_once('config.php');
require_once('funciones_lecciones.php');

$conexion = obtenerConexion();
$sql = "SELECT lecciones.id AS leccion_id, lecciones.titulo, lecciones.tipo_recurso,
               lecciones.recurso_url, lecciones.duracion_min, lecciones.orden,
               lecciones.gratuita, lecciones.fecha_publicacion,
               modulos.titulo AS modulo_titulo, cursos.titulo AS curso_titulo
        FROM lecciones
        INNER JOIN modulos ON lecciones.modulo_id = modulos.id
        INNER JOIN cursos ON modulos.curso_id = cursos.id
        ORDER BY lecciones.duracion_min ASC, lecciones.titulo ASC";

$resultado = false;
$error = '';
if ($conexion->connect_errno) {
    $error = 'No se ha podido conectar con la base de datos.';
} else {
    $resultado = mysqli_query($conexion, $sql);
    if ($resultado === false) {
        $error = 'No se han podido cargar las lecciones.';
    }
}

require_once('cabecera.html');

if ($error !== '') {
    echo "<div class='container'><div class='alert alert-danger' role='alert'>" . htmlspecialchars($error, ENT_QUOTES, 'UTF-8') . "</div></div>";
} else {
    mostrarListadoLecciones($resultado, 'Lecciones ordenadas por duración', 'Todavía no hay lecciones.');
}
?>
</body>
</html>