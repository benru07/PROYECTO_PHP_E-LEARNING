<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>

    <style>
        div {
            height: 10px;
            background-color: black;
        }
    </style>
</head>

<body>
    <h1>Gráfico de Temperaturas</h1>

    <form method="POST">

        <label>Enero:</label>
        <input type="number" name="enero" required>
        <br><br>

        <label>Febrero:</label>
        <input type="number" name="febrero" required>
        <br><br>

        <label>Marzo:</label>
        <input type="number" name="marzo" required>
        <br><br>

        <label>Abril:</label>
        <input type="number" name="abril" required>
        <br><br>

        <label>Mayo:</label>
        <input type="number" name="mayo" required>
        <br><br>

        <label>Junio:</label>
        <input type="number" name="junio" required>
        <br><br>

        <label>Julio:</label>
        <input type="number" name="julio" required>
        <br><br>

        <label>Agosto:</label>
        <input type="number" name="agosto" required>
        <br><br>

        <label>Septiembre:</label>
        <input type="number" name="septiembre" required>
        <br><br>

        <label>Octubre:</label>
        <input type="number" name="octubre" required>
        <br><br>

        <label>Noviembre:</label>
        <input type="number" name="noviembre" required>
        <br><br>

        <label>Diciembre:</label>
        <input type="number" name="diciembre" required>
        <br><br>

        <input type="submit" value="Enviar">

    </form>

    <?php
    if ($_SERVER["REQUEST_METHOD"] == "POST") {
        $meses = [
            'Enero' => $_POST['enero'],
            'Febrero' => $_POST['febrero'],
            'Marzo' => $_POST['marzo'],
            'Abril' => $_POST['abril'],
            'Mayo' => $_POST['mayo'],
            'Junio' => $_POST['junio'],
            'Julio' => $_POST['julio'],
            'Agosto' => $_POST['agosto'],
            'Septiembre' => $_POST['septiembre'],
            'Octubre' => $_POST['octubre'],
            'Noviembre' => $_POST['noviembre'],
            'Diciembre' => $_POST['diciembre']
        ];

        foreach ($meses as $mes => $valor) {
            echo $mes . ": " . $valor . " ºC";
            echo "<div class='barra' style='width: {$valor}rem'></div>";
        }
    }
    ?>
</body>