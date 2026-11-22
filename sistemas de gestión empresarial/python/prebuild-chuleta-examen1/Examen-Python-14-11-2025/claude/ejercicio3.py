"""
Ejercicio 3 - Manejo de ficheros y bases de datos
Gestión de reservas de un restaurante (reservas.csv + MySQL, base de datos 'examen').

Requisitos:   pip install mysql-connector-python
Antes de ejecutar, restaurar examen.sql en MySQL y ajustar DB_CONFIG si hace falta.
"""

import csv
import os
from pprint import pprint

import mysql.connector

# --- Ajusta estos valores a tu servidor MySQL ---
DB_CONFIG = {
    "host": "localhost",      # en el volcado aparece el servidor "db" (Docker)
    "user": "root",
    "password": "",
    "database": "examen",
}

RUTA_CSV = os.path.join(os.path.dirname(os.path.abspath(__file__)), "reservas.csv")


def cargar_reservas_desde_csv():
    """Lee reservas.csv y devuelve {id: {nombre, telefono, fecha, personas, mesa}}."""
    reservas = {}
    with open(RUTA_CSV, newline="", encoding="utf-8") as fichero:
        for fila in csv.DictReader(fichero):
            reservas[int(fila["id"])] = {
                "nombre": fila["nombre"],
                "telefono": fila["telefono"],
                "fecha": fila["fecha"],
                "personas": int(fila["personas"]),
                "mesa": int(fila["mesa"]),
            }
    return reservas


def insertar_reservas_bbdd(reservas):
    """Inserta en la tabla 'reserva' todas las reservas del diccionario."""
    sql = (
        "INSERT INTO reserva "
        "(id, nombre_cliente, telefono, fecha_reserva, numero_personas, numero_mesa) "
        "VALUES (%s, %s, %s, %s, %s, %s) "
        # Permite ejecutar el programa varias veces sin error de clave duplicada
        "ON DUPLICATE KEY UPDATE "
        "nombre_cliente = VALUES(nombre_cliente), telefono = VALUES(telefono), "
        "fecha_reserva = VALUES(fecha_reserva), "
        "numero_personas = VALUES(numero_personas), numero_mesa = VALUES(numero_mesa)"
    )
    datos = [
        (id_reserva, r["nombre"], r["telefono"], r["fecha"], r["personas"], r["mesa"])
        for id_reserva, r in reservas.items()
    ]

    conexion = None
    try:
        conexion = mysql.connector.connect(**DB_CONFIG)
        cursor = conexion.cursor()
        cursor.executemany(sql, datos)
        conexion.commit()
        print(f"Se han procesado {len(datos)} reservas en la base de datos.")
    except mysql.connector.Error as error:
        if conexion is not None:
            conexion.rollback()
        print(f"Error al insertar las reservas: {error}")
    finally:
        if conexion is not None and conexion.is_connected():
            conexion.close()


def mostrar_reservas_fecha(fecha):
    """Muestra las reservas de la fecha indicada (formato AAAA-MM-DD)."""
    fecha = fecha.strip()
    sql = (
        "SELECT numero_mesa, nombre_cliente, numero_personas, telefono "
        "FROM reserva WHERE fecha_reserva = %s ORDER BY id"
    )

    conexion = None
    try:
        conexion = mysql.connector.connect(**DB_CONFIG)
        cursor = conexion.cursor()
        cursor.execute(sql, (fecha,))
        filas = cursor.fetchall()
    except mysql.connector.Error as error:
        print(f"Error al consultar las reservas: {error}")
        return
    finally:
        if conexion is not None and conexion.is_connected():
            conexion.close()

    titulo = f"RESERVAS PARA {fecha}:"
    print(titulo)
    print("=" * len(titulo))
    if not filas:
        print("No hay reservas para esta fecha.\n")
        return

    for mesa, nombre, personas, telefono in filas:
        print(f"Mesa {mesa}: {nombre} ({personas} personas) - Tel: {telefono}")

    total_personas = sum(fila[2] for fila in filas)
    print(f"\nTotal: {len(filas)} reservas, {total_personas} personas\n")


def main():
    # 1. Cargar el CSV en un diccionario y mostrarlo
    reservas = cargar_reservas_desde_csv()
    print("Reservas cargadas desde el CSV:")
    pprint(reservas, sort_dicts=False)
    print()

    # 2. Insertar los datos en la base de datos
    insertar_reservas_bbdd(reservas)
    print()

    # 3. Mostrar las reservas de las fechas indicadas
    mostrar_reservas_fecha("2025-03-15")
    mostrar_reservas_fecha("2025-11-14")


if __name__ == "__main__":
    main()
