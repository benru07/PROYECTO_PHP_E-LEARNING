<?php
require_once('config.php');
require_once('funciones_lecciones.php');

$conexion = obtenerConexion();
$tituloFiltro = trim((string)($_GET['txtTitulo'] ?? ''));
$sql = "SELECT lecciones.id AS leccion_id, lecciones.titulo, lecciones.tipo_recurso,
               lecciones.recurso_url, lecciones.duracion_min, lecciones.orden,
               lecciones.gratuita, lecciones.fecha_publicacion,
               modulos.titulo AS modulo_titulo, cursos.titulo AS curso_titulo
        FROM lecciones
        INNER JOIN modulos ON lecciones.modulo_id = modulos.id
        INNER JOIN cursos ON modulos.curso_id = cursos.id";

if ($tituloFiltro !== '') {
    $sql .= " WHERE lecciones.titulo LIKE ?";
}
$sql .= " ORDER BY cursos.titulo, modulos.orden, lecciones.orden";

$resultado = false;
$error = '';
if ($conexion->connect_errno) {
    $error = 'No se ha podido conectar con la base de datos.';
} elseif ($tituloFiltro !== '') {
    $consulta = mysqli_prepare($conexion, $sql);
    if ($consulta === false) {
        $error = 'No se ha podido preparar la consulta de lecciones.';
    } else {
        $busqueda = '%' . $tituloFiltro . '%';
        mysqli_stmt_bind_param($consulta, 's', $busqueda);
        if (mysqli_stmt_execute($consulta)) {
            $resultado = mysqli_stmt_get_result($consulta);
            if ($resultado === false) {
                $error = 'No se han podido obtener los resultados de la búsqueda.';
            }
        } else {
            $error = 'No se han podido buscar las lecciones.';
        }
    }
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
    $encabezado = $tituloFiltro === '' ? 'Listado de lecciones' : 'Resultados de búsqueda';
    $sinResultados = $tituloFiltro === '' ? 'Todavía no hay lecciones.' : 'No se encontraron lecciones con ese título.';
    mostrarListadoLecciones($resultado, $encabezado, $sinResultados);
}
?>
</body>
</html>