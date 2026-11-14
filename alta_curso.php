<?php
require_once("config.php");

$conexion = obtenerConexion();

$sql = "SELECT idtipo,tipo FROM tipo;";

$resultado = mysqli_query($conexion, $sql);

$options = "";
while ($fila = mysqli_fetch_assoc($resultado)) {
    // $tipos[] = $fila; // Insertar una fila al final
    $options .= " <option value='" . $fila["idtipo"] . "'>" . $fila["tipo"] . "</option>";
}

//Cabecera HTML con navbar
require_once("cabecera.html");

?>
<html lang="es">

<body>
    <div class="container" id="formularios">
        <div class="row">
            <form class="form-horizontal" action="proceso_alta_curso.php" name="frmAltacurso" id="frmAltacurso" method="post">
                <fieldset>
                    <!-- Formulario nombre -->
                    <legend>Alta de curso</legend>
                    <!-- Campo nombre curso-->
                    <div class="form-group">
                        <label class="col-xs-4 control-label" for="txtNombreCurso">Nombre</label>
                        <div class="col-xs-4">
                            <input id="txtNombreCurso" name="txtNombreCurso" placeholder="Nombre del curso" class="form-control input-md" maxlength="25" type="text">
                        </div>
                    </div>
                    <!-- Formulario descripción curso -->
                    <div class="form-group">
                        <label class="col-xs-4 control-label" for="txtDescripcion">Descripción</label>
                        <div class="col-xs-4">
                            <input id="txtDescripcion" name="txtDescripcion" placeholder="Descripcion" class="form-control input-md" type="text">
                        </div>
                    </div>
                    <!-- Formulario precio curso -->
                    <div class="form-group">
                        <label class="col-xs-4 control-label" for="txtPrecio">Precio</label>
                        <div class="col-xs-4">
                            <input id="txtPrecio" name="txtPrecio" placeholder="Precio" class="form-control input-md" type="number">
                        </div>
                    </div>

                    <!-- Formulario categoría curso -->
                    <div class="form-group">
                        <label class="col-xs-4 control-label" for="lstTipo">Categoría</label>
                        <div class="col-xs-4">
                            <select name="lstTipo" id="lstTipo" class="form-select" aria-label="Default select example">
                                <?php echo $options; ?>
                            </select>
                        </div>
                    </div>
                    <!-- Botón insertar curso -->
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