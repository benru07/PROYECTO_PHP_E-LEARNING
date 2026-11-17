<?php
require_once('config.php');

$conexion = obtenerConexion();
$modulos = false;
$errorModulos = '';

if ($conexion->connect_errno) {
    $errorModulos = 'No se ha podido conectar con la base de datos.';
} else {
    $sql = "SELECT modulos.id, modulos.titulo, cursos.titulo AS curso_titulo
            FROM modulos
            INNER JOIN cursos ON modulos.curso_id = cursos.id
            ORDER BY cursos.titulo, modulos.orden, modulos.titulo";
    $modulos = mysqli_query($conexion, $sql);

    if ($modulos === false) {
        $errorModulos = 'No se han podido cargar los módulos.';
    }
}

require_once('cabecera.html');
?>
<div class="container" id="formularios">
    <div class="row">
        <form class="form-horizontal" action="proceso_alta_leccion.php" name="frmAltaLeccion" id="frmAltaLeccion" method="post">
            <fieldset>
                <legend>Alta de lección</legend>

                <?php if ($errorModulos !== ''): ?>
                    <div class="alert alert-danger" role="alert"><?php echo htmlspecialchars($errorModulos, ENT_QUOTES, 'UTF-8'); ?></div>
                <?php elseif (mysqli_num_rows($modulos) === 0): ?>
                    <div class="alert alert-warning" role="alert">Antes de crear una lección, debe haber al menos un módulo.</div>
                <?php endif; ?>

                <div class="form-group">
                    <label class="col-xs-4 control-label" for="txtTitulo">Título</label>
                    <div class="col-xs-4">
                        <input id="txtTitulo" name="txtTitulo" placeholder="Título de la lección" class="form-control input-md" maxlength="150" type="text" required>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-xs-4 control-label" for="lstTipoRecurso">Tipo de recurso</label>
                    <div class="col-xs-4">
                        <select name="lstTipoRecurso" id="lstTipoRecurso" class="form-select" required>
                            <option value="video">Vídeo</option>
                            <option value="pdf">PDF</option>
                            <option value="audio">Audio</option>
                            <option value="imagen">Imagen</option>
                            <option value="texto">Texto</option>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-xs-4 control-label" for="txtRecursoUrl">URL del recurso</label>
                    <div class="col-xs-4">
                        <input id="txtRecursoUrl" name="txtRecursoUrl" placeholder="https://..." class="form-control input-md" maxlength="255" type="text">
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-xs-4 control-label" for="intDuracion">Duración (minutos)</label>
                    <div class="col-xs-4">
                        <input id="intDuracion" name="intDuracion" class="form-control input-md" type="number" min="0" value="0" required>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-xs-4 control-label" for="intOrden">Orden dentro del módulo</label>
                    <div class="col-xs-4">
                        <input id="intOrden" name="intOrden" class="form-control input-md" type="number" min="1" required>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-xs-4 control-label" for="lstModulos">Módulo</label>
                    <div class="col-xs-4">
                        <select name="lstModulos" id="lstModulos" class="form-select" required>
                            <?php if ($modulos !== false): ?>
                                <?php while ($modulo = mysqli_fetch_assoc($modulos)): ?>
                                    <option value="<?php echo (int)$modulo['id']; ?>">
                                        <?php echo htmlspecialchars($modulo['curso_titulo'] . ' - ' . $modulo['titulo'], ENT_QUOTES, 'UTF-8'); ?>
                                    </option>
                                <?php endwhile; ?>
                            <?php endif; ?>
                        </select>
                    </div>
                </div>

                <div class="form-check">
                    <input class="form-check-input" type="checkbox" name="gratuita" value="1" id="gratuita">
                    <label class="form-check-label" for="gratuita">Lección gratuita</label>
                </div>

                <div class="form-group">
                    <div class="col-xs-4 col-xs-offset-4">
                        <button type="submit" id="btnAceptarAltaLeccion" name="btnAceptarAltaLeccion" class="btn btn-primary" <?php echo ($errorModulos !== '' || ($modulos !== false && mysqli_num_rows($modulos) === 0)) ? 'disabled' : ''; ?>>Aceptar</button>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
</div>
</body>
</html>