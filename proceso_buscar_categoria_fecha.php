<?php
require_once('config.php');
$conexion = obtenerConexion();

$condicion = $_GET['condicion'];
$fecha = $_GET['fecha'];

if ($condicion == "antes") {
    // Buscar categorías creadas antes de la fecha
    $comparador = "<";

} else {
    // Buscar categorías creadas después de la fecha
    $comparador = ">";
}

$sql = "SELECT categorias.*, dificultades.nombre AS dificultad_nombre
        FROM categorias
        INNER JOIN dificultades ON categorias.dificultad_id = dificultades.id
        WHERE categorias.fecha_creacion $comparador '$fecha'";


$resultado = mysqli_query($conexion, $sql);


// Montar tabla
$mensaje = "<h2 class='text-center'>Listado de categorias</h2>";
$mensaje .= "<table class='table table-striped'>";
$mensaje .= "<thead><tr><th>IDCategorias</th><th>NOMBRE</th><th>DESCRIPCION</th><th>ORDEN</th><th>FECHA_CREACION</th><th>ACTIVA</th><th>DIFICULTAD</th><th>ACCIONES</th></tr></thead>";
$mensaje .= "<tbody>";

// Recorrer filas mientras $fila != null
// OJO: es una asignación a la variable $fila y después se evalua $fila != null
while ($fila = mysqli_fetch_assoc($resultado)) {
    $mensaje .= "<tr><td>" . $fila['id'] . "</td>";
    $mensaje .= "<td>" . htmlspecialchars($fila['nombre'], ENT_QUOTES, 'UTF-8') . "</td>";
    $mensaje .= "<td>" . htmlspecialchars($fila['descripcion'], ENT_QUOTES, 'UTF-8') . "</td>";
    $mensaje .= "<td>" . $fila['orden'] . "</td>";
    $mensaje .= "<td>" . $fila['fecha_creacion'] . "</td>";

    if ($fila['activa'] == 1) {
        $mensaje .= "<td class='table-success'> si </td>";
    } else {
        $mensaje .= "<td class='table-danger'> no </td>";
    }

    $mensaje .= "<td>" . htmlspecialchars($fila['dificultad_nombre'], ENT_QUOTES, 'UTF-8') . "</td>";

    $mensaje .= "<td><form class='d-inline me-1' action='editar_categoria.php' method='post'>";
    $mensaje .= "<input type='hidden' name='categoria' value='" . htmlspecialchars(json_encode($fila), ENT_QUOTES) . "' />";
    $mensaje .= "<button name='Editar' class='btn btn-primary'><i class='bi bi-pencil-square'></i></button></form>";

    $mensaje .= "<form class='d-inline' action='proceso_borrar_categoria.php' method='post' onsubmit=\"return confirm('¿Estás seguro de que quieres eliminar esta categoría?')\">";
    $mensaje .= "<input type='hidden' name='idcategoria' value='" . $fila['id']  . "' />";
    $mensaje .= "<button name='Borrar' class='btn btn-danger'><i class='bi bi-trash'></i></button></form>";

    $mensaje .= "</td></tr>";
}

// Cerrar tabla
$mensaje .= "</tbody></table>";

include_once("cabecera.html");

echo $mensaje;

?>