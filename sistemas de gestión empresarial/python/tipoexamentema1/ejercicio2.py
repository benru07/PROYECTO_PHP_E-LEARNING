from functools import reduce
#1er ejercicio
def leer_ventas_tapas():
    lista = []
    for _ in range(4):
        nombre_tapa = input("Nombre de la tapa: ")
        precio = float(input("Precio de la tapa: "))
        unidades = int(input("Unidades vendidas: "))
        lista.append( (nombre_tapa, precio, unidades) )

        # tupla = (nombre_tapa, precio, unidades)
        # lista2.append (tupla)

#2do ejercicio
def calcular_estadisticas_tapas(lista_tapas):
    d = {}

    for tapa in lista_tapas:
        if tapa[2] > 25:
            popularidad = 'Alta'
        elif tapa[2] > 15:
            popularidad = 'Media'
        else:
            popularidad = 'Baja'
        d[tapa[0]] = (tapa[1] * tapa[2], tapa[1] * tapa[2] * 0.1 , popularidad)
    return d

#3er ejercicio
def analizar_rendimiento_tapas(dic_estadisticas):

    t_p_a = filter(lambda tapa : tapa[1][2] == 'Alta' , dic_estadisticas.items() )
    
    promedio_iva = sum(map(lambda tapa : tapa[1][1], dic_estadisticas.items() )) / len(dic_estadisticas)

    mayor_importe = reduce(lambda t1,t2: t1 if t1[1][0] > t2[1][0] else t2, dic_estadisticas.items())

    return(t_p_a, promedio_iva, mayor_importe)
#MAIN
#datos = leer_ventas_tapas()
555#print (datos)

datos2 = [('Serranito', 3.5, 25), ('Solomillo al whisky', 4.2, 18), ('Montadito de pringá', 2.8, 32), ('Espinacas con garbanzos', 3.0, 21)]
print(calcular_estadisticas_tapas(datos2))
print(analizar_rendimiento_tapas(calcular_estadisticas_tapas(datos2)))
