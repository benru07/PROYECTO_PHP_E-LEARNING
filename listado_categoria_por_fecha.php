
<?php
include_once("cabecera.html");
?>

<div class="container" id="formularios">
    <div class="row">
        <form class="form-horizontal" action="proceso_buscar_categoria_fecha.php"
              name="frmBuscarcategoria" id="frmBuscarcategoria" method="get">

            <fieldset>
                <legend>Buscar categorías por fecha de creación</legend>

                <!-- Seleccionar condición -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="condicion">
                        Buscar categorías creadas:
                    </label>

                    <div class="col-xs-4">
                        <select class="form-control" name="condicion" id="condicion" required>
                            <option value="antes">Antes de</option>
                            <option value="despues">Después de</option>
                        </select>
                    </div>
                </div>

                <!-- Seleccionar fecha -->
                <div class="form-group">
                    <label class="col-xs-4 control-label" for="fecha">
                        Fecha:
                    </label>

                    <div class="col-xs-4">
                        <input type="date" class="form-control"
                               name="fecha" id="fecha" required />
                    </div>
                </div>

                <!-- Botón buscar -->
                <div class="form-group">
                    <div class="col-xs-offset-4 col-xs-4">
                        <input type="submit" id="btnBuscar"
                               name="btnBuscar"
                               class="btn btn-primary"
                               value="Buscar categorías" />
                    </div>
                </div>

            </fieldset>
        </form>
    </div>
</div>