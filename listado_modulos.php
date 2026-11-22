<?php
require_once('config.php');
$conexion = obtenerConexion();

$sql = "SELECT modulos.*, cursos.titulo AS curso_titulo
    FROM modulos
    INNER JOIN cursos ON modulos.curso_id = cursos.id
    ORDER BY modulos.orden ASC;";

$resultado = mysqli_query($conexion, $sql);


// Montar tabla
$mensaje = "<h2 class='text-center'>Listado de modulos</h2>";
$mensaje .= "<table class='table table-striped'>";
$mensaje .= "<thead><tr><th>ID</th><th>TÍTULO</th><th>DESCRIPCIÓN</th><th>ORDEN</th><th>FECHA DE CREACIÓN</th><th>OBLIGATORIO</th><th>CURSO</th><th>ACCIONES</th></tr></thead>";
$mensaje .= "<tbody>";

// Recorrer filas mientras $fila != null
// OJO: es una asignación a la variable $fila y después se evalua $fila != null
while ($fila = mysqli_fetch_assoc($resultado)) {
    $mensaje .= "<tr><td>" . $fila['id'] . "</td>";
    $mensaje .= "<td>" . htmlspecialchars($fila['titulo'], ENT_QUOTES, 'UTF-8') . "</td>";
    $mensaje .= "<td>" . htmlspecialchars($fila['descripcion'], ENT_QUOTES, 'UTF-8') . "</td>";
    $mensaje .= "<td>" . $fila['orden'] . "</td>";
    $mensaje .= "<td>" . htmlspecialchars($fila['fecha_creacion'], ENT_QUOTES, 'UTF-8') . "</td>";

    if ($fila['obligatorio'] == 1) {
        $mensaje .= "<td class='table-success'>Sí</td>";
    } else {
        $mensaje .= "<td class='table-danger'>No</td>";
    }

    $mensaje .= "<td>" . htmlspecialchars($fila['curso_titulo'], ENT_QUOTES, 'UTF-8') . "</td>";

    $mensaje .= "<td><form class='d-inline me-1' action='editar_modulo.php' method='post'>";
    $mensaje .= "<input type='hidden' name='modulo' value='" . htmlspecialchars(json_encode($fila), ENT_QUOTES, 'UTF-8') . "' />";
    $mensaje .= "<button name='Editar' class='btn btn-primary'><i class='bi bi-pencil-square'></i></button></form>";

    $mensaje .= "<form class='d-inline' action='proceso_borrar_modulo.php' method='post' onsubmit=\"return confirm('¿Estás seguro de que quieres eliminar este módulo?')\">";
    $mensaje .= "<input type='hidden' name='idmodulo' value='" . $fila['id']  . "' />";
    $mensaje .= "<button name='Borrar' class='btn btn-danger'><i class='bi bi-trash'></i></button></form>";

    $mensaje .= "</td></tr>";
}

// Cerrar tabla
$mensaje .= "</tbody></table>";

include_once("cabecera.html");

echo $mensaje;

?>