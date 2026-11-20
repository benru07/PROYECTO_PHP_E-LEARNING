<?php
require_once('config.php');

$error = '';
$mensaje = '';

if (($_SERVER['REQUEST_METHOD'] ?? '') !== 'POST') {
    http_response_code(405);
    $error = 'Método no permitido.';
} else {
    $idLeccion = filter_var($_POST['id'] ?? null, FILTER_VALIDATE_INT, array('options' => array('min_range' => 1)));
    if ($idLeccion === false) {
        http_response_code(400);
        $error = 'La lección indicada no es válida.';
    } else {
        $conexion = obtenerConexion();
        if ($conexion->connect_errno) {
            $error = 'No se ha podido conectar con la base de datos.';
        } else {
            $consulta = mysqli_prepare($conexion, "DELETE FROM lecciones WHERE id = ?");
            if ($consulta === false) {
                $error = 'No se ha podido preparar el borrado de la lección.';
            } else {
                mysqli_stmt_bind_param($consulta, 'i', $idLeccion);
                if (mysqli_stmt_execute($consulta)) {
                    if (mysqli_stmt_affected_rows($consulta) === 1) {
                        $mensaje = 'Lección borrada correctamente.';
                    } else {
                        http_response_code(404);
                        $error = 'No se ha encontrado la lección indicada.';
                    }
                } else {
                    $error = 'No se ha podido borrar la lección.';
                }
            }
        }
    }
}

require_once('cabecera.html');
?>
<div class="container mt-5">
    <?php if ($error !== ''): ?>
        <div class="alert alert-danger" role="alert"><?php echo htmlspecialchars($error, ENT_QUOTES, 'UTF-8'); ?></div>
    <?php else: ?>
        <div class="alert alert-success" role="alert"><?php echo htmlspecialchars($mensaje, ENT_QUOTES, 'UTF-8'); ?></div>
    <?php endif; ?>
    <a class="btn btn-primary" href="listado_lecciones.php">Volver al listado de lecciones</a>
</div>
</body>
</html>
