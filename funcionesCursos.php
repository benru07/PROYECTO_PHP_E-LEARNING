<?php
// Funciones compartidas por las páginas de cursos.

/**
 * Devuelve el HTML de los <option> con todas las categorías.
 * Se consulta la base de datos cada vez que se carga la página, así que cualquier categoría
 * nueva aparece automáticamente en la lista desplegable.
 *
 * $campo: columna que se usa como value del option ('id' o 'nombre')
 * $seleccionado: valor que debe aparecer seleccionado (opcional)
 */
function opcionesCategorias($conexion, $campo = "id", $seleccionado = null)
{
    $campo = ($campo === "nombre") ? "nombre" : "id"; // solo dos valores posibles

    $resultado = mysqli_query($conexion, "SELECT id, nombre FROM categorias ORDER BY nombre ASC;");
    $options = "";
    while ($fila = mysqli_fetch_assoc($resultado)) {
        $valor = htmlspecialchars($fila[$campo], ENT_QUOTES);
        $selected = ((string) $fila[$campo] === (string) $seleccionado) ? " selected" : "";
        $options .= "<option value='$valor'$selected>" . htmlspecialchars($fila["nombre"]) . "</option>";
    }
    return $options;
}

/** Devuelve las opciones del desplegable de nivel. */
function opcionesNivel($seleccionado = "basico")
{
    $niveles = ["basico" => "Básico", "intermedio" => "Intermedio", "avanzado" => "Avanzado"];
    $options = "";
    foreach ($niveles as $valor => $texto) {
        $selected = ($valor === $seleccionado) ? " selected" : "";
        $options .= "<option value='$valor'$selected>$texto</option>";
    }
    return $options;
}

/**
 * Construye la tabla HTML de cursos (con botones de editar y borrar) a partir de un resultado mysqli.
 * La consulta debe devolver c.* y la columna "categoria" (nombre de la categoría).
 */
function construirTablaCursos($resultado)
{
    $mensaje = "<table class='table table-striped'>";
    $mensaje .= "<thead><tr><th>IDCursos</th><th>TITULO</th><th>CATEGORIA</th><th>NIVEL</th><th>DURACION</th>"
              . "<th>PRECIO</th><th>FECHA_PUBLICACION</th><th>PUBLICADO</th><th>ACCION</th></tr></thead>";
    $mensaje .= "<tbody>";

    // Recorrer filas mientras $fila != null
    // OJO: es una asignación a la variable $fila y después se evalua $fila != null
    while ($fila = mysqli_fetch_assoc($resultado)) {
        $mensaje .= "<tr><td>" . $fila['id'] . "</td>";
        $mensaje .= "<td>" . htmlspecialchars($fila['titulo']) . "</td>";
        $mensaje .= "<td>" . htmlspecialchars($fila['categoria']) . "</td>";
        $mensaje .= "<td>" . htmlspecialchars(ucfirst($fila['nivel'])) . "</td>";
        $mensaje .= "<td>" . $fila['duracion_horas'] . " h</td>";
        $mensaje .= "<td>" . number_format($fila['precio'], 2, ',', '.') . " €</td>";
        $mensaje .= "<td>" . date("d/m/Y", strtotime($fila['fecha_publicacion'])) . "</td>";
        if ($fila['publicado'] == 1) {
            $mensaje .= "<td class='table-success'> si </td>";
        } else {
            $mensaje .= "<td class='table-danger'> no </td>";
        }

        // Botón editar: se envía la fila completa en un campo oculto (en formato JSON)
        $mensaje .= "<td class='text-nowrap'><form class='d-inline me-1' action='editar_curso.php' method='post'>";
        $mensaje .= "<input type='hidden' name='curso' value='" . htmlspecialchars(json_encode($fila), ENT_QUOTES) . "' />";
        $mensaje .= "<button name='Editar' class='btn btn-primary'><i class='bi bi-pencil-square'></i></button></form>";

        // Botón borrar: se envía solo el id
        $mensaje .= "<form class='d-inline' action='proceso_borrar_curso.php' method='post' onsubmit=\"return confirm('¿Estás seguro de que quieres eliminar este curso? Se borrarán también sus módulos y lecciones.')\">";
        $mensaje .= "<input type='hidden' name='idcurso' value='" . $fila['id'] . "' />";
        $mensaje .= "<button name='Borrar' class='btn btn-danger'><i class='bi bi-trash'></i></button></form>";

        $mensaje .= "</td></tr>";
    }

    // Cerrar tabla
    $mensaje .= "</tbody></table>";
    return $mensaje;
}
