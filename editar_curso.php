<?php
session_start();

// Si se entra directamente sin pasar por un listado, no hay curso que editar
if (!isset($_POST['curso'])) {
    header("Location: buscar_curso.php");
    exit;
}

// Recupero datos del curso en forma de array asociativo
$curso = json_decode($_POST['curso'], true);

$_SESSION['idcurso'] = $curso['id'];

require_once('config.php');
require_once('funcionesCursos.php');
$conexion = obtenerConexion();

// La categoría del curso aparece seleccionada en la lista desplegable
$options = opcionesCategorias($conexion, "id", $curso['categoria_id']);

include_once("cabecera.html");

?>
<div class="container" id="formularios">
    <div class="row">
        <form class="form-horizontal" action="proceso_editar_curso.php" name="frmAltacurso" id="frmAltacurso" method="post">
            <fieldset>
                <!-- Form Name -->
                <legend>Edición de curso</legend>

                <!-- CATEGORIA -->
                <div class="form-group mb-3">
                    <label class="col-xs-4 control-label" for="lstCategoria">Categoría</label>
                    <div class="col-xs-4">
                        <select name="lstCategoria" id="lstCategoria" class="form-select" required>
                            <?php echo $options; ?>
                        </select>
                    </div>
                </div>

                <!-- TITULO -->
                <div class="form-group mb-3">
                    <label class="col-xs-4 control-label" for="txtTitulo">Título</label>
                    <div class="col-xs-4">
                        <input value="<?php echo htmlspecialchars($curso['titulo'], ENT_QUOTES); ?>" id="txtTitulo" name="txtTitulo" placeholder="Título del curso" class="form-control input-md" maxlength="150" type="text" required>
                    </div>
                </div>

                <!-- Descripción -->
                <div class="form-group mb-3">
                    <label class="col-xs-4 control-label" for="txtDescripcion">Descripción</label>
                    <div class="col-xs-4">
                        <textarea id="txtDescripcion" name="txtDescripcion" placeholder="Descripción del curso" class="form-control input-md" rows="3"><?php echo htmlspecialchars($curso['descripcion'] ?? ''); ?></textarea>
                    </div>
                </div>

                <!-- NIVEL -->
                <div class="form-group mb-3">
                    <label class="col-xs-4 control-label" for="lstNivel">Nivel de dificultad</label>
                    <div class="col-xs-4">
                        <select name="lstNivel" id="lstNivel" class="form-select">
                            <?php echo opcionesNivel($curso['nivel']); ?>
                        </select>
                    </div>
                </div>

                <!-- DURACION -->
                <div class="form-group mb-3">
                    <label class="col-xs-4 control-label" for="intduracion">Duración (horas)</label>
                    <div class="col-xs-4">
                        <input value="<?php echo $curso['duracion_horas']; ?>" id="intduracion" name="intduracion" class="form-control input-md" type="number" min="1" required>
                    </div>
                </div>

                <!-- PRECIO -->
                <div class="form-group mb-3">
                    <label class="col-xs-4 control-label" for="decprecio">Precio (€)</label>
                    <div class="col-xs-4">
                        <input value="<?php echo $curso['precio']; ?>" id="decprecio" name="decprecio" class="form-control input-md" type="number" step="0.01" min="0" required>
                    </div>
                </div>

                <!-- FECHA DE PUBLICACION -->
                <div class="form-group mb-3">
                    <label class="col-xs-4 control-label" for="fecha">Fecha de publicación</label>
                    <div class="col-xs-4">
                        <input value="<?php echo $curso['fecha_publicacion']; ?>" id="fecha" name="fecha" class="form-control input-md" type="date" required>
                    </div>
                </div>

                <!-- PUBLICADO -->
                <div class="form-check mb-3">
                    <input class="form-check-input" type="checkbox" name="publicado" value="1" id="publicado" <?php if ($curso['publicado'] == 1) echo 'checked'; ?>>
                    <label class="form-check-label" for="publicado">
                        Publicado
                    </label>
                </div>

                <input value="<?php echo $curso['id']; ?>" type='hidden' name='idcurso' id='idcurso' />

                <!-- Button -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="btnAceptarEditarCurso"></label>
                    <div class="col-xs-4">
                        <input type="submit" id="btnAceptarEditarCurso" name="btnAceptarEditarCurso" class="btn btn-primary" value="Aceptar" />
                    </div>
                </div>
            </fieldset>
        </form>

    </div>
</div>
</body>

</html>
