<?php
require_once('cabecera.html');
?>
<div class="container" id="formularios">
    <div class="row">
        <form class="form-horizontal" action="listado_lecciones.php" name="frmBuscarLeccion" id="frmBuscarLeccion" method="get">
            <fieldset>
                <legend>Buscar una lección</legend>
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="txtTitulo">Título</label>
                    <div class="col-xs-4">
                        <input id="txtTitulo" name="txtTitulo" placeholder="Título de la lección" class="form-control input-md" maxlength="150" type="search" required>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-xs-4 col-xs-offset-4">
                        <button type="submit" class="btn btn-primary">Buscar</button>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
</div>
</body>
</html>