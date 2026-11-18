<?php
require_once('config.php');

function mostrarResultadoAltaLeccion($mensaje, $tipo)
{
    header('Refresh: 5; url=listado_lecciones.php');
    require_once('cabecera.html');
    echo "<div class='container'><div class='alert alert-" . htmlspecialchars($tipo, ENT_QUOTES, 'UTF-8') . " text-center mt-5' role='alert'>" . htmlspecialchars($mensaje, ENT_QUOTES, 'UTF-8') . "</div></div>";
    echo '</body></html>';
}

if (($_SERVER['REQUEST_METHOD'] ?? '') !== 'POST') {
    http_response_code(405);
    mostrarResultadoAltaLeccion('Método no permitido.', 'danger');
    exit;
}

$titulo = trim((string)($_POST['txtTitulo'] ?? ''));
$tipoRecurso = (string)($_POST['lstTipoRecurso'] ?? '');
$url = trim((string)($_POST['txtRecursoUrl'] ?? ''));
$moduloId = filter_var($_POST['lstModulos'] ?? null, FILTER_VALIDATE_INT, array('options' => array('min_range' => 1)));
$duracion = filter_var($_POST['intDuracion'] ?? null, FILTER_VALIDATE_INT, array('options' => array('min_range' => 0)));
$orden = filter_var($_POST['intOrden'] ?? null, FILTER_VALIDATE_INT, array('options' => array('min_range' => 1)));
$gratuita = isset($_POST['gratuita']) ? 1 : 0;
$tiposValidos = array('video', 'pdf', 'audio', 'imagen', 'texto');
$longitudTitulo = preg_match_all('/./us', $titulo);
$longitudUrl = preg_match_all('/./us', $url);

if ($titulo === '' || $longitudTitulo === false || $longitudTitulo > 150 || !in_array($tipoRecurso, $tiposValidos, true)
    || $moduloId === false || $duracion === false || $orden === false
    || $longitudUrl === false || $longitudUrl > 255) {
    http_response_code(400);
    mostrarResultadoAltaLeccion('Revisa los datos: el título y el tipo de recurso son obligatorios; la duración debe ser igual o mayor que cero y el orden y el módulo deben ser válidos.', 'warning');
    exit;
}

$conexion = obtenerConexion();
if ($conexion->connect_errno) {
    mostrarResultadoAltaLeccion('No se ha podido conectar con la base de datos.', 'danger');
    exit;
}

$sql = "INSERT INTO lecciones (modulo_id, titulo, tipo_recurso, recurso_url, duracion_min, orden, gratuita, fecha_publicacion)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
$consulta = mysqli_prepare($conexion, $sql);
if ($consulta === false) {
    mostrarResultadoAltaLeccion('No se ha podido preparar el alta de la lección.', 'danger');
    exit;
}

$urlRecurso = $url === '' ? null : $url;
$fechaPublicacion = date('Y-m-d');
mysqli_stmt_bind_param($consulta, 'isssiiis', $moduloId, $titulo, $tipoRecurso, $urlRecurso, $duracion, $orden, $gratuita, $fechaPublicacion);

if (mysqli_stmt_execute($consulta)) {
    mostrarResultadoAltaLeccion('Lección insertada correctamente.', 'success');
} else {
    if (mysqli_stmt_errno($consulta) === 1062) {
        mostrarResultadoAltaLeccion('Ya existe una lección con ese orden dentro del módulo seleccionado.', 'warning');
    } else {
        mostrarResultadoAltaLeccion('No se ha podido insertar la lección.', 'danger');
    }
}
?>
