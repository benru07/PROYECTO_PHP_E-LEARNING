<?php
require_once('config.php');
require_once('funciones_lecciones.php');

$conexion = obtenerConexion();
$duracionEntrada = trim((string)($_GET['duracion'] ?? ''));
$duracionFiltro = null;
$sql = "SELECT lecciones.id AS leccion_id, lecciones.titulo, lecciones.tipo_recurso,
               lecciones.recurso_url, lecciones.duracion_min, lecciones.orden,
               lecciones.gratuita, lecciones.fecha_publicacion,
               modulos.titulo AS modulo_titulo, cursos.titulo AS curso_titulo
        FROM lecciones
        INNER JOIN modulos ON lecciones.modulo_id = modulos.id
        INNER JOIN cursos ON modulos.curso_id = cursos.id";

if ($duracionEntrada !== '') {
    $duracionFiltro = filter_var($duracionEntrada, FILTER_VALIDATE_INT, array('options' => array('min_range' => 0)));
}

if ($duracionFiltro !== null && $duracionFiltro !== false) {
    $sql .= " WHERE lecciones.duracion_min = ?";
}
$sql .= " ORDER BY lecciones.duracion_min ASC, lecciones.titulo ASC";

$resultado = false;
$error = '';
if ($duracionEntrada !== '' && $duracionFiltro === false) {
    $error = 'Introduce una duración como número entero igual o mayor que cero.';
} elseif ($conexion->connect_errno) {
    $error = 'No se ha podido conectar con la base de datos.';
} elseif ($duracionFiltro !== null) {
    $consulta = mysqli_prepare($conexion, $sql);
    if ($consulta === false) {
        $error = 'No se ha podido preparar la búsqueda por duración.';
    } else {
        mysqli_stmt_bind_param($consulta, 'i', $duracionFiltro);
        if (mysqli_stmt_execute($consulta)) {
            $resultado = mysqli_stmt_get_result($consulta);
            if ($resultado === false) {
                $error = 'No se han podido obtener las lecciones encontradas.';
            }
        } else {
            $error = 'No se han podido buscar las lecciones por duración.';
        }
    }
} else {
    $resultado = mysqli_query($conexion, $sql);
    if ($resultado === false) {
        $error = 'No se han podido cargar las lecciones.';
    }
}

require_once('cabecera.html');

echo "<div class='container mt-4'>";
echo "<form class='row g-2 align-items-end mb-4' action='listado_lecciones_por_duracion.php' method='get'>";
echo "<div class='col-auto'><label class='form-label' for='duracion'>Duración (minutos)</label>";
echo "<input class='form-control' id='duracion' name='duracion' type='number' min='0' step='1' value='" . htmlspecialchars($duracionEntrada, ENT_QUOTES, 'UTF-8') . "' placeholder='Introduce un número'></div>";
echo "<div class='col-auto'><button type='submit' class='btn btn-primary'>Buscar</button></div>";
echo "</form></div>";

if ($error !== '') {
    echo "<div class='container'><div class='alert alert-danger' role='alert'>" . htmlspecialchars($error, ENT_QUOTES, 'UTF-8') . "</div></div>";
} else {
    $encabezado = $duracionFiltro === null ? 'Lecciones ordenadas por duración' : 'Lecciones de ' . $duracionFiltro . ' minutos';
    $sinResultados = $duracionFiltro === null ? 'Todavía no hay lecciones.' : 'No se encontraron lecciones con esa duración.';
    mostrarListadoLecciones($resultado, $encabezado, $sinResultados);
}
?>
</body>
</html>