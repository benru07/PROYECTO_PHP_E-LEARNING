diccionario = { ("ferry",1) : [ 2500 ,350 ], # [ carga, pasajeros ]
 ("mercante",2) : [ 120000, 6500 ], # [ carga, autonomía ]
 ("mercante",3) : [ 200000, 3200 ], # [ carga, pasajeros ]
 ("ferry",4) : [ 3520 , 420] }


def __main__():
    print("Carga total de los barcos: ", totalCarga(diccionario))
    print("Cargas de los barcos mercantes: ", obtenerCarga(diccionario))


def totalCarga(diccionario):
    carga_total = 0
    for key, value in diccionario.items():
        carga_total += value[0]
    return carga_total

def obtenerCarga(diccionario):
    tuplaCargas = ()
    for key, value in diccionario.items():
        if key[0] == "mercante": 
            tuplaCargas += (value[0],)
    return tuplaCargas