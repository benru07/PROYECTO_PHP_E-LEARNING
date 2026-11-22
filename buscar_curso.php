<?php
include_once("cabecera.html");
?>

<div class="container" id="formularios">
    <div class="row">
        <form class="form-horizontal" action="proceso_buscar_curso.php" name="frmBuscarcurso" id="frmBuscarcurso" method="get">
            <fieldset>
                <!-- Form Name -->
                <legend>Buscar un curso</legend>
                <!-- Text input-->
                <div class="form-group mb-3">
                    <label class="col-xs-4 control-label" for="txtTituloCurso">Título</label>
                    <div class="col-xs-4">
                        <input id="txtTituloCurso" name="txtTituloCurso" placeholder="Título del curso (o parte)" class="form-control input-md" type="text" required>
                    </div>
                </div>

                <!-- Button -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="btnAceptarBuscarCurso"></label>
                    <div class="col-xs-4">
                        <input type="submit" id="btnAceptarBuscarCurso" name="btnAceptarBuscarCurso" class="btn btn-primary" value="Aceptar" />
                    </div>
                </div>
            </fieldset>
        </form>

    </div>
</div>
</body>

</html>
