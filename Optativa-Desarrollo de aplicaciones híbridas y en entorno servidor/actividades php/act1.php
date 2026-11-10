<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>

<body>
    <h1>Introduce 5 números</h1>

    <form method="POST">

        <label>Número 1:</label>
        <input type="number" name="numero1" required>
        <br><br>

        <label>Número 2:</label>
        <input type="number" name="numero2" required>
        <br><br>

        <label>Número 3:</label>
        <input type="number" name="numero3" required>
        <br><br>

        <label>Número 4:</label>
        <input type="number" name="numero4" required>
        <br><br>

        <label>Número 5:</label>
        <input type="number" name="numero5" required>
        <br><br>

        <input type="submit" value="Enviar">

    </form>

    <?php

    if ($_SERVER["REQUEST_METHOD"] == "POST") {
        $numeros = [$_POST['numero1'], $_POST['numero2'], $_POST['numero3'], $_POST['numero4'], $_POST['numero5']];

        $min = min($numeros);
        $max = max($numeros);

        $mayor = PHP_INT_MIN;
        $menor = PHP_INT_MAX;

        for ($i = 0; $i < count($numeros); $i++) {
            if ($numeros[$i] > $mayor) {
                $mayor = $numeros[$i];
            }
            if ($numeros[$i] < $menor) {
                $menor = $numeros[$i];
            }
        }

        echo "Esta es la lista de números introducidos: " . implode(", ", $numeros);
        echo "<br>";
        echo "El número mínimo es: " . $min;
        echo "<br>";
        echo "El número máximo es: " . $max;
        echo "<br>";
        echo "ahora con el bucle for: ";
        echo "<br>";
        echo "El número mayor es: " . $mayor;
        echo "<br>";
        echo "El número menor es: " . $menor;
    }

    ?>

</body>

</html>