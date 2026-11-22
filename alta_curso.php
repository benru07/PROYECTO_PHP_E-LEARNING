<?php
require_once('config.php');
require_once('funcionesCursos.php');
$conexion = obtenerConexion();

// Lista desplegable de categorías: se carga de la base de datos cada vez que se abre el formulario,
// por lo que las categorías nuevas aparecen automáticamente.
$options = opcionesCategorias($conexion, "id");

require_once('cabecera.html');

?>
<div class="container" id="formularios">
    <div class="row">
        <form class="form-horizontal" action="proceso_alta_curso.php" name="frmAltacurso" id="frmAltacurso" method="post">
            <fieldset>
                <!-- Form Name -->
                <legend>Alta de curso</legend>

                <!-- CATEGORIA (lista desplegable cargada de la base de datos) -->
                <div class="form-group mb-3">
                    <label class="col-xs-4 control-label" for="lstCategoria">Categoría</label>
                    <div class="col-xs-4">
                        <select name="lstCategoria" id="lstCategoria" class="form-select" required>
                            <option value="">— Selecciona una categoría —</option>
                            <?php echo $options; ?>
                        </select>
                    </div>
                </div>

                <!-- TITULO -->
                <div class="form-group mb-3">
                    <label class="col-xs-4 control-label" for="txtTitulo">Título</label>
                    <div class="col-xs-4">
                        <input id="txtTitulo" name="txtTitulo" placeholder="Título del curso" class="form-control input-md" maxlength="150" type="text" required>
                    </div>
                </div>

                <!-- Descripción -->
                <div class="form-group mb-3">
                    <label class="col-xs-4 control-label" for="txtDescripcion">Descripción</label>
                    <div class="col-xs-4">
                        <textarea id="txtDescripcion" name="txtDescripcion" placeholder="Descripción del curso" class="form-control input-md" rows="3"></textarea>
                    </div>
                </div>

                <!-- NIVEL -->
                <div class="form-group mb-3">
                    <label class="col-xs-4 control-label" for="lstNivel">Nivel de dificultad</label>
                    <div class="col-xs-4">
                        <select name="lstNivel" id="lstNivel" class="form-select">
                            <?php echo opcionesNivel("basico"); ?>
                        </select>
                    </div>
                </div>

                <!-- DURACION -->
                <div class="form-group mb-3">
                    <label class="col-xs-4 control-label" for="intduracion">Duración (horas)</label>
                    <div class="col-xs-4">
                        <input id="intduracion" name="intduracion" placeholder="Horas" class="form-control input-md" type="number" min="1" required>
                    </div>
                </div>

                <!-- PRECIO -->
                <div class="form-group mb-3">
                    <label class="col-xs-4 control-label" for="decprecio">Precio (€)</label>
                    <div class="col-xs-4">
                        <input id="decprecio" name="decprecio" placeholder="Precio" class="form-control input-md" type="number" step="0.01" min="0" value="0" required>
                    </div>
                </div>

                <!-- FECHA DE PUBLICACION -->
                <div class="form-group mb-3">
                    <label class="col-xs-4 control-label" for="fecha">Fecha de publicación</label>
                    <div class="col-xs-4">
                        <input id="fecha" name="fecha" class="form-control input-md" type="date" value="<?php echo date('Y-m-d'); ?>" required>
                    </div>
                </div>

                <!-- PUBLICADO -->
                <div class="form-check mb-3">
                    <input class="form-check-input" type="checkbox" name="publicado" value="1" id="publicado">
                    <label class="form-check-label" for="publicado">
                        Publicado
                    </label>
                </div>

                <!-- Button -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="btnAceptarAltaCurso"></label>
                    <div class="col-xs-4">
                        <input type="submit" id="btnAceptarAltaCurso" name="btnAceptarAltaCurso" class="btn btn-primary" value="Aceptar" />
                    </div>
                </div>
            </fieldset>
        </form>

    </div>
</div>
</body>

</html>
