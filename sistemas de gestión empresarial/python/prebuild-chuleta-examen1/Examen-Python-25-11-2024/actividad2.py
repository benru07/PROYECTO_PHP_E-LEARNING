from functools import reduce
import csv

def entrada_datos():
    partidos = []

    for i in range(3):
        nombre = input("nombre")
        puntosfavor = input("puntos a favor")
        puntoscontra = input("puntos en contra")
        partidosjugados = input("partidos jugados")

        partidos.append((nombre, puntosfavor, puntoscontra, partidosjugados))

    return partidos

def calculo_estadisticas(lista_datos):
    diccionariostats = {}

    for (nombre, puntosfavor, puntoscontra, partidosjugados) in lista_datos:
        diffpuntos = puntosfavor - puntoscontra
        diccionariostats[nombre] = (diffpuntos, diffpuntos/partidosjugados)
    return diccionariostats



def extremos(dicc_datos):



    extremosvarmax = reduce(lambda equipo1, equipo2: equipo1 if equipo1[1][0] > equipo2[1][0] else equipo2, dicc_datos.items())

    extremosvarmin = reduce(lambda equipo1, equipo2: equipo1 if equipo1[1][0] < equipo2[1][0] else equipo2, dicc_datos.items())

    extremosvar = [extremosvarmax[0], extremosvarmin[0]]

    print(extremosvar)


def guardar_datos(dicc_datos):

    with open("baloncesto.csv", "w", newline="", encoding="utf-8") as fichero:
        escritor = csv.writer(fichero)

        for equipo in dicc_datos.items():
            escritor.writerow([equipo[0], equipo[1][0], equipo[1][1]])


def main():
    # equpos_raw = [("Triana Heat", 786, 648, 10), ("ElCerro Bulls", 870, 718, 10), ("Bami Nuggets", 580, 950, 18)]

    equpos_raw = entrada_datos()

    stats_equipos = calculo_estadisticas(equpos_raw)

    extremos(stats_equipos)

    guardar_datos(stats_equipos)


if name == "main":
    main()