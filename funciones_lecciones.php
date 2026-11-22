<?php
function mostrarListadoLecciones($resultado, $encabezado, $sinResultados)
{
    echo "<div class='container'>";
    echo "<h2 class='text-center'>" . htmlspecialchars($encabezado, ENT_QUOTES, 'UTF-8') . "</h2>";

    if (mysqli_num_rows($resultado) === 0) {
        echo "<p class='text-center'>" . htmlspecialchars($sinResultados, ENT_QUOTES, 'UTF-8') . "</p>";
        echo "</div>";
        return;
    }

    echo "<div class='table-responsive'><table class='table table-striped'>";
    echo "<thead><tr><th>ID</th><th>TÍTULO</th><th>RECURSO</th><th>DURACIÓN</th><th>ORDEN</th><th>MÓDULO</th><th>CURSO</th><th>GRATUITA</th><th>FECHA DE PUBLICACIÓN</th><th>ACCIONES</th></tr></thead><tbody>";

    while ($fila = mysqli_fetch_assoc($resultado)) {
        echo "<tr>";
        echo "<td>" . (int)$fila['leccion_id'] . "</td>";
        echo "<td>" . htmlspecialchars($fila['titulo'], ENT_QUOTES, 'UTF-8') . "</td>";
        echo "<td>" . htmlspecialchars($fila['tipo_recurso'], ENT_QUOTES, 'UTF-8');
        if ($fila['recurso_url'] !== null && $fila['recurso_url'] !== '') {
            $url = trim($fila['recurso_url']);
            if (filter_var($url, FILTER_VALIDATE_URL) && in_array(strtolower((string)parse_url($url, PHP_URL_SCHEME)), array('http', 'https'), true)) {
                echo " <a href='" . htmlspecialchars($url, ENT_QUOTES, 'UTF-8') . "' target='_blank' rel='noopener noreferrer'>Abrir</a>";
            } else {
                echo " (" . htmlspecialchars($url, ENT_QUOTES, 'UTF-8') . ")";
            }
        }
        echo "</td>";
        echo "<td>" . (int)$fila['duracion_min'] . " min</td>";
        echo "<td>" . (int)$fila['orden'] . "</td>";
        echo "<td>" . htmlspecialchars($fila['modulo_titulo'], ENT_QUOTES, 'UTF-8') . "</td>";
        echo "<td>" . htmlspecialchars($fila['curso_titulo'], ENT_QUOTES, 'UTF-8') . "</td>";
        echo "<td class='" . ((int)$fila['gratuita'] === 1 ? 'table-success' : 'table-danger') . "'>" . ((int)$fila['gratuita'] === 1 ? 'Sí' : 'No') . "</td>";
        echo "<td>" . htmlspecialchars($fila['fecha_publicacion'], ENT_QUOTES, 'UTF-8') . "</td>";
        echo "<td class='text-nowrap'>";
        echo "<a class='btn btn-sm btn-primary me-1' href='editar_leccion.php?id=" . (int)$fila['leccion_id'] . "'>Editar</a>";
        echo "<form class='d-inline' action='proceso_borrar_leccion.php' method='post' onsubmit=\"return confirm('¿Seguro que quieres borrar esta lección?');\">";
        echo "<input type='hidden' name='id' value='" . (int)$fila['leccion_id'] . "'>";
        echo "<button type='submit' class='btn btn-sm btn-danger'>Borrar</button>";
        echo "</form></td>";
        echo "</tr>";
    }

    echo "</tbody></table></div></div>";
}
?>
