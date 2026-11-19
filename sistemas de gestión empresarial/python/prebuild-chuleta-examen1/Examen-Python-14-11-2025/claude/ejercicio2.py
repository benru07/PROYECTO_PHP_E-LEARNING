"""
Ejercicio 2 - Estructuras de datos y funciones de orden superior
Ventas de tapas típicas sevillanas.
"""

IVA_TAPAS = 0.10


def _pedir_numero(mensaje, tipo):
    """Pide un número por teclado y repite la pregunta hasta que sea válido."""
    while True:
        try:
            valor = tipo(input(mensaje).replace(",", "."))
            if valor < 0:
                print("  El valor no puede ser negativo.")
                continue
            return valor
        except ValueError:
            print("  Valor no válido, inténtalo de nuevo.")


def leer_ventas_tapas():
    """Pide los datos de 4 tapas y devuelve una lista de tuplas
    (nombre_tapa, precio_unitario, cantidad_vendida)."""
    ventas = []
    for i in range(1, 5):
        print(f"--- Tapa {i} de 4 ---")
        nombre = input("Nombre de la tapa: ").strip()
        precio = _pedir_numero("Precio unitario (€): ", float)
        cantidad = _pedir_numero("Cantidad vendida: ", int)
        ventas.append((nombre, precio, cantidad))
    return ventas


def _popularidad(cantidad):
    """'Alta' si cantidad > 25, 'Media' si > 15, 'Baja' si <= 15."""
    if cantidad > 25:
        return "Alta"
    if cantidad > 15:
        return "Media"
    return "Baja"


def calcular_estadisticas_tapas(lista_ventas):
    """Devuelve {nombre: (total_ventas, iva_aplicado, popularidad)}."""
    return {
        nombre: (
            round(precio * cantidad, 2),
            round(precio * cantidad * IVA_TAPAS, 2),
            _popularidad(cantidad),
        )
        for nombre, precio, cantidad in lista_ventas
    }


def analizar_rendimiento_tapas(dic_estadisticas):
    """Devuelve (tapas_alta_popularidad, promedio_iva, tapa_top) SIN usar bucles.

    - tapas_alta_popularidad: [(nombre, total_ventas)] de popularidad 'Alta',
      ordenadas de mayor a menor recaudación.
    - promedio_iva: media del IVA de todas las tapas (2 decimales).
    - tapa_top: nombre de la tapa con mayor recaudación total.
    """
    if not dic_estadisticas:
        return [], 0, None

    # 1. Filtrar las tapas con popularidad "Alta" (filter + map + sorted)
    tapas_alta = sorted(
        map(
            lambda item: (item[0], item[1][0]),
            filter(lambda item: item[1][2] == "Alta", dic_estadisticas.items()),
        ),
        key=lambda tapa: tapa[1],
        reverse=True,
    )

    # 2. Promedio de IVA de todas las tapas
    promedio_iva = round(
        sum([datos[1] for datos in dic_estadisticas.values()]) / len(dic_estadisticas),
        2,
    )

    # 3. Tapa con mayor recaudación total
    tapa_top = max(dic_estadisticas, key=lambda nombre: dic_estadisticas[nombre][0])

    return tapas_alta, promedio_iva, tapa_top


def main():
    print("=== VENTAS DE TAPAS ===")
    ventas = leer_ventas_tapas()
    print("\nVentas leídas:")
    print(ventas)

    estadisticas = calcular_estadisticas_tapas(ventas)
    print("\nEstadísticas:")
    print(estadisticas)

    alta, promedio_iva, top = analizar_rendimiento_tapas(estadisticas)
    print("\nAnálisis de rendimiento:")
    print(f"  Tapas de alta popularidad: {alta}")
    print(f"  Promedio de IVA: {promedio_iva}")
    print(f"  Tapa con mayor recaudación: {top}")


if __name__ == "__main__":
    main()
