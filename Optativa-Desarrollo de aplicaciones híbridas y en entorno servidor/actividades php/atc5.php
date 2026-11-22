<?php

$pares = [];

$inpares = [];

for ($i = 0; $i < 20; $i++) {
    $num = rand(1, 100);

    if ($num % 2 == 0) {
        $pares[] = $num;
    } else {
        $inpares[] = $num;
    }
}

$ordenados = array_merge($pares, $inpares);

echo "estos son los núemros generados: a la izquierda los pares y a la derecha los impares: " . implode(", ", $ordenados);

?>