<?php
require_once('config.php');

$idEntrada = (($_SERVER['REQUEST_METHOD'] ?? '') === 'POST') ? ($_POST['id'] ?? null) : ($_GET['id'] ?? null);
$idLeccion = filter_var($idEntrada, FILTER_VALIDATE_INT, array('options' => array('min_range' => 1)));
$error = '';
$mensaje = '';
$leccion = null;
$modulos = false;

if ($idLeccion === false) {
    http_response_code(400);
    $error = 'La lección indicada no es válida.';
} else {
    $conexion = obtenerConexion();
    if ($conexion->connect_errno) {
        $error = 'No se ha podido conectar con la base de datos.';
    } elseif (($_SERVER['REQUEST_METHOD'] ?? '') === 'POST') {
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
            $error = 'Revisa los datos: el título y el tipo de recurso son obligatorios; la duración debe ser igual o mayor que cero y el orden y el módulo deben ser válidos.';
        } else {
            $sqlActualizar = "UPDATE lecciones
                              SET modulo_id = ?, titulo = ?, tipo_recurso = ?, recurso_url = ?,
                                  duracion_min = ?, orden = ?, gratuita = ?
                              WHERE id = ?";
            $consultaActualizar = mysqli_prepare($conexion, $sqlActualizar);
            if ($consultaActualizar === false) {
                $error = 'No se ha podido preparar la edición de la lección.';
            } else {
                $urlRecurso = $url === '' ? null : $url;
                mysqli_stmt_bind_param($consultaActualizar, 'isssiiii', $moduloId, $titulo, $tipoRecurso, $urlRecurso, $duracion, $orden, $gratuita, $idLeccion);
                if (mysqli_stmt_execute($consultaActualizar)) {
                    $mensaje = 'Lección actualizada correctamente.';
                } elseif (mysqli_stmt_errno($consultaActualizar) === 1062) {
                    $error = 'Ya existe una lección con ese orden dentro del módulo seleccionado.';
                } else {
                    $error = 'No se ha podido actualizar la lección.';
                }
            }
        }
    }

    if ($error === '') {
        $consultaLeccion = mysqli_prepare($conexion, "SELECT id, modulo_id, titulo, tipo_recurso, recurso_url, duracion_min, orden, gratuita FROM lecciones WHERE id = ?");
        if ($consultaLeccion === false) {
            $error = 'No se ha podido preparar la carga de la lección.';
        } else {
            mysqli_stmt_bind_param($consultaLeccion, 'i', $idLeccion);
            if (mysqli_stmt_execute($consultaLeccion)) {
                $resultadoLeccion = mysqli_stmt_get_result($consultaLeccion);
                if ($resultadoLeccion !== false) {
                    $leccion = mysqli_fetch_assoc($resultadoLeccion);
                } else {
                    $error = 'No se han podido obtener los datos de la lección.';
                }
            } else {
                $error = 'No se ha podido cargar la lección.';
            }
        }
    }

    if ($error === '' && $leccion === null) {
        http_response_code(404);
        $error = 'No se ha encontrado la lección indicada.';
    }

    if ($error === '') {
        $sqlModulos = "SELECT modulos.id, modulos.titulo, cursos.titulo AS curso_titulo
                       FROM modulos
                       INNER JOIN cursos ON modulos.curso_id = cursos.id
                       ORDER BY cursos.titulo, modulos.orden, modulos.titulo";
        $modulos = mysqli_query($conexion, $sqlModulos);
        if ($modulos === false) {
            $error = 'No se han podido cargar los módulos.';
        }
    }
}

require_once('cabecera.html');
?>
<div class="container" id="formularios">
    <div class="row">
        <form class="form-horizontal" action="editar_leccion.php" name="frmEditarLeccion" id="frmEditarLeccion" method="post">
            <fieldset>
                <legend>Edición de lección</legend>
                <?php if ($mensaje !== ''): ?>
                    <div class="alert alert-success" role="alert"><?php echo htmlspecialchars($mensaje, ENT_QUOTES, 'UTF-8'); ?></div>
                <?php endif; ?>
                <?php if ($error !== ''): ?>
                    <div class="alert alert-danger" role="alert"><?php echo htmlspecialchars($error, ENT_QUOTES, 'UTF-8'); ?></div>
                <?php elseif ($leccion !== null): ?>
                    <input type="hidden" name="id" value="<?php echo (int)$leccion['id']; ?>">

                    <div class="form-group">
                        <label class="col-xs-4 control-label" for="txtTitulo">Título</label>
                        <div class="col-xs-4">
                            <input id="txtTitulo" name="txtTitulo" class="form-control input-md" maxlength="150" type="text" value="<?php echo htmlspecialchars($leccion['titulo'], ENT_QUOTES, 'UTF-8'); ?>" required>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-xs-4 control-label" for="lstTipoRecurso">Tipo de recurso</label>
                        <div class="col-xs-4">
                            <select name="lstTipoRecurso" id="lstTipoRecurso" class="form-select" required>
                                <?php foreach (array('video' => 'Vídeo', 'pdf' => 'PDF', 'audio' => 'Audio', 'imagen' => 'Imagen', 'texto' => 'Texto') as $tipo => $etiqueta): ?>
                                    <option value="<?php echo htmlspecialchars($tipo, ENT_QUOTES, 'UTF-8'); ?>" <?php echo $leccion['tipo_recurso'] === $tipo ? 'selected' : ''; ?>><?php echo htmlspecialchars($etiqueta, ENT_QUOTES, 'UTF-8'); ?></option>
                                <?php endforeach; ?>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-xs-4 control-label" for="txtRecursoUrl">URL del recurso</label>
                        <div class="col-xs-4">
                            <input id="txtRecursoUrl" name="txtRecursoUrl" class="form-control input-md" maxlength="255" type="text" value="<?php echo htmlspecialchars((string)$leccion['recurso_url'], ENT_QUOTES, 'UTF-8'); ?>">
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-xs-4 control-label" for="intDuracion">Duración (minutos)</label>
                        <div class="col-xs-4">
                            <input id="intDuracion" name="intDuracion" class="form-control input-md" type="number" min="0" value="<?php echo (int)$leccion['duracion_min']; ?>" required>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-xs-4 control-label" for="intOrden">Orden dentro del módulo</label>
                        <div class="col-xs-4">
                            <input id="intOrden" name="intOrden" class="form-control input-md" type="number" min="1" value="<?php echo (int)$leccion['orden']; ?>" required>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-xs-4 control-label" for="lstModulos">Módulo</label>
                        <div class="col-xs-4">
                            <select name="lstModulos" id="lstModulos" class="form-select" required>
                                <?php while ($modulo = mysqli_fetch_assoc($modulos)): ?>
                                    <option value="<?php echo (int)$modulo['id']; ?>" <?php echo (int)$modulo['id'] === (int)$leccion['modulo_id'] ? 'selected' : ''; ?>>
                                        <?php echo htmlspecialchars($modulo['curso_titulo'] . ' - ' . $modulo['titulo'], ENT_QUOTES, 'UTF-8'); ?>
                                    </option>
                                <?php endwhile; ?>
                            </select>
                        </div>
                    </div>

                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" name="gratuita" value="1" id="gratuita" <?php echo (int)$leccion['gratuita'] === 1 ? 'checked' : ''; ?>>
                        <label class="form-check-label" for="gratuita">Lección gratuita</label>
                    </div>

                    <div class="form-group">
                        <div class="col-xs-4 col-xs-offset-4">
                            <button type="submit" class="btn btn-primary">Guardar cambios</button>
                            <a class="btn btn-secondary" href="listado_lecciones.php">Volver al listado</a>
                        </div>
                    </div>
                <?php endif; ?>
            </fieldset>
        </form>
    </div>
</div>
</body>
</html>