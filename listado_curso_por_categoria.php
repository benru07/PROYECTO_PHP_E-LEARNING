<?php
require_once('config.php');
require_once('funcionesCursos.php');
$conexion = obtenerConexion();

// Lista desplegable con los NOMBRES de las categorías. Se carga de la base de datos en cada visita,
// así que las categorías que se vayan creando aparecen automáticamente.
$options = opcionesCategorias($conexion, "nombre");

include_once("cabecera.html");
?>

<div class="container" id="formularios">
    <div class="row">
        <form class="form-horizontal" action="proceso_buscar_curso_categoria.php"
              name="frmBuscarcursocategoria" id="frmBuscarcursocategoria" method="get">

            <fieldset>
                <legend>Buscar cursos por categoría</legend>

                <!-- Seleccionar categoría -->
                <div class="form-group mb-3">
                    <label class="col-xs-4 control-label" for="lstCategoria">
                        Categoría:
                    </label>

                    <div class="col-xs-4">
                        <select class="form-select" name="lstCategoria" id="lstCategoria" required>
                            <option value="">— Selecciona una categoría —</option>
                            <?php echo $options; ?>
                        </select>
                    </div>
                </div>

                <!-- Botón buscar -->
                <div class="form-group">
                    <div class="col-xs-offset-4 col-xs-4">
                        <input type="submit" id="btnBuscar"
                               name="btnBuscar"
                               class="btn btn-primary"
                               value="Buscar cursos" />
                    </div>
                </div>

            </fieldset>
        </form>
    </div>
</div>
</body>

</html>
