<?php
require_once('config.php');
require_once('cabecera.html');

$conexion = obtenerConexion();

$sql = "SELECT id,titulo FROM cursos;";

$resultado = mysqli_query($conexion, $sql);

$options = "";
while ($fila = mysqli_fetch_assoc($resultado)) {
    $options .= "<option value='{$fila['id']}'>" . htmlspecialchars($fila['titulo']) . "</option>";
}

?>
<div class="container" id="formularios">
    <div class="row">
        <form class="form-horizontal" action="proceso_alta_modulo.php" name="frmAltaModulo" id="frmAltaModulo" method="post">
            <fieldset>
                <!-- Form Name -->
                <legend>Alta de Modulo</legend>

                <!-- NOMBRE -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="txtNombre">Nombre</label>
                    <div class="col-xs-4">
                        <input id="txtNombre" name="txtNombre" placeholder="Nombre del módulo" class="form-control input-md" maxlength="25" type="text">
                    </div>
                </div>

                <!-- Descripción -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="txtDescripcion">Descripción</label>
                    <div class="col-xs-4">
                        <input id="txtDescripcion" name="txtDescripcion" placeholder="Descripción del módulo" class="form-control input-md" type="text">
                    </div>
                </div>

                <!-- ORDEN -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="intorden">Orden</label>
                    <div class="col-xs-4">
                        <input id="intorden" name="intorden" placeholder="Orden" class="form-control input-md" type="number">
                    </div>
                </div>

                <!-- Desplegable cargado desde la base de datos de los cursos -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="lstCursos">Curso</label>
                    </label>
                    <div class="col-xs-4">
                        <select name="lstCursos" id="lstCursos" class="form-select" aria-label="Default select example">
                            <?php echo $options; ?>
                        </select>
                    </div>
                </div>

                <!-- OBLIGATORIO O NO -->
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" name="obligatorio" value="1" id="obligatorio" checked>
                    <label class="form-check-label" for="obligatorio">
                        Obligatorio
                    </label>
                </div>

                <!-- Button -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="btnAceptarAltaModulo"></label>
                    <div class="col-xs-4">
                        <input type="submit" id="btnAceptarAltaModulo" name="btnAceptarAltaModulo" class="btn btn-primary" value="Aceptar" />
                    </div>
                </div>
            </fieldset>
        </form>

    </div>
</div>
</body>