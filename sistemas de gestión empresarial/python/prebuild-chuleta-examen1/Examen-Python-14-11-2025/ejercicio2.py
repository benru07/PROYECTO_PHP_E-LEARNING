from functools import reduce

def leer_tapas():
    
    ventas = []
    
    for _ in range(4):
        nombre = input("Nombre de la tapa: ").strip()
        precio = float(input("Precio unitario (€): "))
        cantidad = int(input("Cantidad vendida: "))
        ventas.append((nombre, precio, cantidad))
        
    return ventas



def calcular_estadisticas_tapas(listaventas):

#[('Serranito', 3.5, 25), ('Solomillo al whisky', 4.2, 18), ('Montadito de pringá', 2.8, 32), ('Espinacas con garbanzos', 3.0, 21)]

    diccionario = {}
    
    #match cantidad_v:
    #
    #        case e if e > 25:
    #            popularidad = "alta"
    #        case e if e > 15:
    #            popularidad = "media"
    #        case e if e <= 15:
    #            popularidad = "baja"
                
    for (nombre , precio, cantidad) in listaventas:
        total_vents = precio * cantidad
        iva_aplicado = total_vents * 10 / 100
        if cantidad > 25:
            popilaridad =  "Alta"
        elif cantidad > 15:
            popilaridad = "Media"
        else:
            popilaridad = "baja"
                
        
        #la key es el nombre y los values los datos
        diccionario[nombre] = (total_vents, iva_aplicado, popilaridad)
    
    return diccionario

def analizar_rendimiento_tapas(dic_estadisticas):
    
    tapas_popularidad_alta = list(filter( lambda tapa : tapa[1][2] == "Alta" , dic_estadisticas.items()))
    
    iva_tapas = sum(map(lambda tapa : tapa[1][1], dic_estadisticas.items())) / len(dic_estadisticas)
    
    #con reduce, tienes que importarlos "from functools import reduce"
    mayor_recaudacion = reduce(lambda tapa1, tapa2 : tapa1 if tapa1[1][0] > tapa2[1][0] else tapa2, dic_estadisticas.items())
    
    #sin reduce
    #mayor_recaudacion = max(dic_estadisticas, key=lambda nombre: dic_estadisticas[nombre][0])


    t = (tapas_popularidad_alta, iva_tapas, mayor_recaudacion)

    return t

