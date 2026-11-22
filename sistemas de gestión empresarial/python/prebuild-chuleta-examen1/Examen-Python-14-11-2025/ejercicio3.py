import csv
import pymysql # type: ignore

def cargar_reservas_desde_csv():

    dicccionario_reservas = {}

    #chema
    with open('reservas.csv', node = 'r', encoding='utf-8') as archivo:
        lector = csv.DictReader(archivo) 

        for fila in lector:
            id_reserva = int(fila["id"])
            dicccionario_reservas[id_reserva] = {"nombre": fila[1], "telefono": fila[2], "fecha": fila[3], "personas": int (fila[4]), "mesa": int (fila[5].strip())}
            
        #return dicccionario_reservas
            

    #abres el fichero lees las lineas y lo cierras
    fichero= open("reservas.csv" , "rt", encoding="UTF=8")

    lista_lineas = fichero.readlines()

    fichero.close

    dicccionario = {}

    for linea in lista_lineas[1:]:
        #2,Luis Martínez,622334455,2025-03-15,2,3

        #cortamos cada parametro separado por la ",""
        lista_campos = linea.split(",")
        
        #lo anyadimos
        dicccionario[int (lista_campos[0])]  = {'nombre': lista_campos[1], 'telefono': lista_campos[2], 'fecha': lista_campos[3], 'personas': int (lista_campos[4]), 'mesa': int (lista_campos[5].strip())}

        return dicccionario


def insertar_reserbas_bbdd(reservas):
    #conn  = pymysql.connect(nombre del contenedor, usuario, contraseña y base de datos)
    # esto ya nos lo da el profesor solo hay que abir ejemplo_mysql.py
    conn  = pymysql.connect(host='php-26-27-db-1', user='root', password='test', database='examen')

    cursor = conn.cursor()

    for reserva in reservas.items():
        #este es sin poner el cursro.execute() (este mejor)
        #sql = f"INSERT INTO reserva(id, nombre_cliente, telefono, fecha_reserva, numero_personas_, numero_mesa) VALUES ({reserva[0]}, '{reserva[1]['nombre']}', '{reserva[1]['telefono']}', '{reserva[1]['fecha']}', {reserva[1]['personas']}, {reserva[1]['mesa']})"
        
        #este es con el cursor.execute()
        sql = "INSERT INTO reserva(id, nombre_cliente, telefono, fecha_reserva, numero_personas_, numero_mesa) VALUES ( %s , %s , %s , %s , %s , %s)"
        cursor.execute(sql,(reserva[0], reserva[1]['nombre'], reserva[1]['telefono'],reserva[1]['fecha'] ,reserva[1]['personas'] ,reserva[1]['mesa']) )

    conn.commit
    conn.close


def mostrar_reservas_fechas(fecha):
    conn  = pymysql.connect(host='php-26-27-db-1', user='root', password='test', database='examen')

    cursor= conn.cursor()

    #btw este mejor
    #sql = f"SELECT * FROM reserva WEHRE fecha_reserva = {fecha}"
    
    sql  = "SELECT * FROM reserva WHERE fecha_reserva = %s"
    cursor.execute(sql, (fecha, ))

    rows  =cursor.fetchall() #lista de tuplas cada tubla es una fila de datos

    conn.close()

    listado = f"RESERVAS PARA {fecha}\n"
    listado += "==========================\n"

    num_personas = 0
    num_reservas = 0
    for row in rows:
        listado += f"Mesa {row[5]}: {row[1]} ({row[4]} personas) - Tel:{row[2]}\n"
        num_reservas +=1
        num_personas += row[4]

    listado += f"Total: {num_reservas} reservas, {num_personas} personas"

    return listado


#mian
dicccionario  = cargar_reservas_desde_csv()
print(dicccionario)

#insertar_reserbas_bbdd(reservas)

print(mostrar_reservas_fechas(14))