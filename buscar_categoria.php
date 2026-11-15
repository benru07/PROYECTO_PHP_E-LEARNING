
<?php
include_once("cabecera.html");
?>

<div class="container" id="formularios">
    <div class="row">
        <form class="form-horizontal" action="proceso_buscar_categoria.php" name="frmBuscarcategoria" id="frmBuscarcategoria" method="get">
            <fieldset>
                <!-- Form Name -->
                <legend>Buscar una categoria</legend>
                <!-- Text input-->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="txtNombreCategoria">Nombre</label>
                    <div class="col-xs-4">
                        <input id="txtNombreCategoria" name="txtNombreCategoria" placeholder="Nombre de la categoria" class="form-control input-md" type="text">
                    </div>
                </div>
                
                <!-- Button -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="btnAceptarBuscarCategoria"></label>
                    <div class="col-xs-4">
                        <input type="submit" id="btnAceptarBuscarCategoria" name="btnAceptarBuscarCategoria" class="btn btn-primary" value="Aceptar" />
                    </div>
                </div>
            </fieldset>
        </form>

    </div>
</div>
</body>

</html>