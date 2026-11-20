
<?php
include_once("cabecera.html");
?>

<div class="container" id="formularios">
    <div class="row">
        <form class="form-horizontal" action="proceso_buscar_modulo.php" name="frmBuscarcategoria" id="frmBuscarcategoria" method="get">
            <fieldset>
                <!-- Form Name -->
                <legend>Buscar un módulo</legend>
                <!-- Text input-->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="txtNombreModulo">Nombre</label>
                    <div class="col-xs-4">
                        <input id="txtNombreModulo" name="txtNombreModulo" placeholder="Nombre del módulo" class="form-control input-md" type="text">
                    </div>
                </div>
                
                <!-- Button -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="btnAceptarBuscarModulo"></label>
                    <div class="col-xs-4">
                        <input type="submit" id="btnAceptarBuscarModulo" name="btnAceptarBuscarModulo" class="btn btn-primary" value="Aceptar" />
                    </div>
                </div>
            </fieldset>
        </form>

    </div>
</div>
</body>

</html>