<?php
require_once("config.php");
$conexion = obtenerConexion();

// Recuperar parámetro
$nombre_modulo = $_GET['txtNombreModulo'];

$sql = "SELECT modulos.*, cursos.nombre AS curso_nombre
    FROM modulos
    INNER JOIN cursos ON modulos.curso_id = cursos.id
    WHERE modulos.titulo LIKE '%$nombre_modulo%';";

$resultado = mysqli_query($conexion, $sql);



if (mysqli_num_rows($resultado) > 0) {


    $mensaje = "<h2 class='text-center'>Listado de módulos</h2>";
    $mensaje .= "<table class='table table-striped'>";
    $mensaje .= "<thead><tr><th>ID</th><th>TITULO</th><th>DESCRIPCION</th><th>ORDEN</th><th>OBLIGATORIO</th><th>FECHA_CREACION</th></tr></thead>";
    $mensaje .= "<tbody>";

    // Recorrer filas mientras $fila != null
    // OJO: es una asignación a la variable $fila y después se evalua $fila != null
    while ($fila = mysqli_fetch_assoc($resultado)) {
        $mensaje .= "<tr><td>" . $fila['id'] . "</td>";
        $mensaje .= "<td>" . htmlspecialchars($fila['TITULO'], ENT_QUOTES, 'UTF-8') . "</td>";
        $mensaje .= "<td>" . htmlspecialchars($fila['descripcion'], ENT_QUOTES, 'UTF-8') . "</td>";
        $mensaje .= "<td>" . $fila['orden'] . "</td>";
        $mensaje .= "<td>" . $fila['obligatorio'] . "</td>";
        $mensaje .= "<td>" . $fila['fecha_creacion'] . "</td>";

        if ($fila['obligatorio'] == 1) {
            $mensaje .= "<td class='table-success'> si </td>";
        } else {
            $mensaje .= "<td class='table-danger'> no </td>";
        }

        $mensaje .= "<td>" . htmlspecialchars($fila['curso_nombre'], ENT_QUOTES, 'UTF-8') . "</td>";

        $mensaje .= "<td><form class='d-inline me-1' action='editar_modulo.php' method='post'>";
        $mensaje .= "<input type='hidden' name='modulo' value='" . htmlspecialchars(json_encode($fila), ENT_QUOTES) . "' />";
        $mensaje .= "<button name='Editar' class='btn btn-primary'><i class='bi bi-pencil-square'></i></button></form>";

        $mensaje .= "<form class='d-inline' action='proceso_borrar_modulo.php' method='post' onsubmit=\"return confirm('¿Estás seguro de que quieres eliminar este módulo?')\">";
        $mensaje .= "<input type='hidden' name='idmodulo' value='" . $fila['id']  . "' />";
        $mensaje .= "<button name='Borrar' class='btn btn-danger'><i class='bi bi-trash'></i></button></form>";

        $mensaje .= "</td></tr>";
    }

    // Cerrar tabla
    $mensaje .= "</tbody></table>";
} else {
    $mensaje .= "<h2 class='text-center'>No hay modulos con ese nombre</h2>";
}

include_once("cabecera.html");

// Mostrar mensaje calculado antes
echo $mensaje;
