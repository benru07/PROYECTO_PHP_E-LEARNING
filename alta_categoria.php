<?php
require_once('config.php');
require_once('cabecera.html');

$conexion = obtenerConexion();

$sql = "SELECT id,nombre FROM dificultades;";

$resultado = mysqli_query($conexion, $sql);

$options = "";
while ($fila = mysqli_fetch_assoc($resultado)) {
    $options .= "<option value='{$fila['id']}'>" . htmlspecialchars($fila['nombre']) . "</option>";
}

?>
<div class="container" id="formularios">
    <div class="row">
        <form class="form-horizontal" action="proceso_alta_categoria.php" name="frmAltacategoria" id="frmAltacategoria" method="post">
            <fieldset>
                <!-- Form Name -->
                <legend>Alta de categoría</legend>

                <!-- NOMBRE -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="txtNombre">Nombre</label>
                    <div class="col-xs-4">
                        <input id="txtNombre" name="txtNombre" placeholder="Nombre de la categoría" class="form-control input-md" maxlength="25" type="text">
                    </div>
                </div>

                <!-- Descripción -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="txtDescripcion">Descripción</label>
                    <div class="col-xs-4">
                        <input id="txtDescripcion" name="txtDescripcion" placeholder="Descripción de la categoría" class="form-control input-md" type="text">
                    </div>
                </div>

                <!-- ORDEN -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="intorden">Orden</label>
                    <div class="col-xs-4">
                        <input id="intorden" name="intorden" placeholder="Orden" class="form-control input-md" type="number">
                    </div>
                </div>

                <!-- Desplegable cargado desde la base de datos de las dificultades -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="lstDificultades">Dificultad</label>
                    </label>
                    <div class="col-xs-4">
                        <select name="lstDificultades" id="lstDificultades" class="form-select" aria-label="Default select example">
                            <?php echo $options; ?>
                        </select>
                    </div>
                </div>

                <!-- ACTIVA -->
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" name="activa" value="1" id="activa" checked>
                    <label class="form-check-label" for="activa">
                        Activa
                    </label>
                </div>

                <!-- Button -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="btnAceptarAltaCategoria"></label>
                    <div class="col-xs-4">
                        <input type="submit" id="btnAceptarAltaCategoria" name="btnAceptarAltaCategoria" class="btn btn-primary" value="Aceptar" />
                    </div>
                </div>
            </fieldset>
        </form>

    </div>
</div>
</body>