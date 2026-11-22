<?php
session_start();

$modulo = json_decode($_POST['modulo'], true);


$_SESSION['idmodulo'] = $modulo['id'];

require_once('config.php');
$conexion = obtenerConexion();

$sql = "SELECT id, titulo FROM cursos ORDER BY id";
$resultado = mysqli_query($conexion, $sql);
$options = "";
while ($curso = mysqli_fetch_assoc($resultado)) {
    $selected = (int)$curso['id'] === (int)$modulo['curso_id'] ? ' selected' : '';
    $options .= "<option value='" . (int)$curso['id'] . "'$selected>" . htmlspecialchars($curso['titulo'], ENT_QUOTES, 'UTF-8') . "</option>";
}

include_once("cabecera.html");

?>
<div class="container" id="formularios">
    <div class="row">
        <form class="form-horizontal" action="proceso_editar_modulo.php" name="frmAltamodulo" id="frmAltamodulo" method="post">
            <fieldset>
                <!-- Form Name -->
                <legend>Edición de módulo</legend>

                <!-- TÍTULO -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="txtTitulo">Título</label>
                    <div class="col-xs-4">
                        <input value="<?php echo $modulo['titulo']; ?>" id="txtTitulo" name="txtTitulo" placeholder="Título del módulo" class="form-control input-md" maxlength="25" type="text">
                    </div>
                </div>

                <!-- Descripción -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="txtDescripcion">Descripción</label>
                    <div class="col-xs-4">
                        <input value="<?php echo $modulo['descripcion']; ?>" id="txtDescripcion" name="txtDescripcion" placeholder="Descripción del módulo" class="form-control input-md" type="text">
                    </div>
                </div>

                <!-- ORDEN -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="intorden">Orden</label>
                    <div class="col-xs-4">
                        <input value="<?php echo $modulo['orden']; ?>" id="intorden" name="intorden" placeholder="Orden" class="form-control input-md" type="number">
                    </div>
                </div>

                <!-- Desplegable cargado desde la base de datos de las dificultades -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="lstDificultades">Dificultad</label>
                    </label>
                    <div class="col-xs-4">
                        <select name="lstCursos" id="lstCursos" class="form-select" aria-label="Default select example">
                            <?php echo $options; ?>
                        </select>
                    </div>
                </div>

                <!-- OBLIGATORIO -->
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" name="obligatorio" value="1" id="obligatorio" <?php if ($modulo['obligatorio'] == 1) echo 'checked'; ?>>
                    <label class="form-check-label" for="obligatorio">
                        Obligatorio
                    </label>
                </div>

                <input value="<?php echo $modulo['id'] ?>" type='hidden' name='idmodulo' id='idmodulo' />

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