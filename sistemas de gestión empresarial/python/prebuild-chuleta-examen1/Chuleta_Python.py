# -*- coding: utf-8 -*-
# =============================================================================
#  CHULETA PYTHON - SISTEMAS DE GESTION EMPRESARIAL (2º DAM)
# =============================================================================
# ETIQUETAS: cabecera, titulo, chuleta, cheatsheet, apuntes, como usar
#
# COMO USAR ESTE ARCHIVO
#  - Es Python valido: se puede ejecutar o importar sin errores (solo define
#    cadenas de texto). Todo el contenido esta en cadenas triples o comentarios.
#  - Busca con Ctrl+F por las lineas "# ETIQUETAS:" (sinonimos ES/EN).
#  - Copia el bloque que necesites y quita la sangria/comentario.
#  - "(EXTRA, no aparece en el temario)" = cosas utiles que NO estan en los PDFs.
#  - "[JAVA]" = comparacion con Java.
#  - Fuentes: PDFs 1-5 (basico, OO, funcional, modulos, acceso a datos),
#    cheat sheets FSI (Python, Pandas, Matplotlib), Pythonlearn 01-10 y los
#    ejercicios/examenes del repositorio de clase (Python_26_27).
# =============================================================================


# =============================================================================
# INDICE CORTO
# =============================================================================
# ETIQUETAS: indice, index, contenido, tabla de contenidos, secciones
#  0. Imports y librerias habituales
#  1. Sintaxis basica y tipos
#  2. Control de flujo
#  3. Funciones
#  4. Colecciones (listas, tuplas, diccionarios, conjuntos, comprensiones)
#  5. Cadenas
#  6. Entrada/salida y ficheros
#  7. Clases y POO
#  8. Excepciones
#  9. Modulos y paquetes
# 10. Programacion funcional (orden superior, map, filter, reduce, lambda, generadores)
# 11. Acceso a bases de datos (sqlite3, pymysql, MySQLdb)
# 12. Pandas y Matplotlib (cheat sheets del temario)
# 13. Estructuras complejas / patrones de examen
# 14. Errores tipicos y checklist final


INDICE_DETALLADO = """
INDICE DETALLADO (sin codigo)
ETIQUETAS: indice detallado, que hay, resumen, mapa, contenido completo

0. IMPORTS: lista de los modulos que se usan en clase (functools para reduce y
   total_ordering, sqlite3, pymysql, MySQLdb, pandas, matplotlib, warnings) y
   modulos estandar utiles (math, random, os, sys, csv, json, datetime,
   collections, copy, statistics).

1. SINTAXIS Y TIPOS: comentarios, indentacion como bloques, variables sin
   declarar, tipado dinamico pero fuerte, tipos basicos (int, float, complex,
   str, bool, None), funcion type e isinstance, conversiones explicitas,
   operadores aritmeticos (incluida division entera y potencia), de bits,
   relacionales, logicos, de pertenencia (in) e identidad (is), asignacion
   multiple e intercambio, asignacion aumentada, f-strings con formato de
   decimales y anchura, metodo format y operador %, numeros especiales,
   funciones integradas (abs, round, min, max, sum, len, divmod, pow...).

2. CONTROL DE FLUJO: if/elif/else, operador ternario, condiciones encadenadas,
   valores "falsy", while con contador, while True con break, while con
   bandera, for sobre secuencias, range con sus tres parametros, enumerate,
   zip, reversed, sorted, break/continue/pass, el else de los bucles, match
   (switch de Python 3.10).

3. FUNCIONES: def, docstring, return multiple y desempaquetado, parametros por
   defecto, argumentos por nombre, *args (tupla), **kwargs (diccionario),
   orden obligatorio de parametros, desempaquetado al llamar, paso por
   referencia a objeto (mutables vs inmutables), variables globales y
   nonlocal, recursividad (factorial, fibonacci, suma de lista, busqueda
   binaria), funciones anidadas, type hints.

4. COLECCIONES: listas (creacion, indices negativos, slicing con paso,
   modificacion por rangos, todos los metodos: append, extend, insert, remove,
   pop, clear, index, count, sort, reverse, copy), matrices (listas de listas),
   tuplas (tupla de un elemento, inmutabilidad, desempaquetado, count/index),
   diccionarios (crear, acceder, get con defecto, in, keys/values/items, pop,
   popitem, update, setdefault, fromkeys, clear, copy, recorrer, ordenar por
   valor), conjuntos (add, remove, discard, union, interseccion, diferencia),
   comprensiones de listas, diccionarios, conjuntos y generadores, copia
   superficial frente a profunda, funciones comunes (len, sorted, min, max con
   key, sum, any, all).

5. CADENAS: comillas, escapes, cadenas multilinea, cadenas raw, concatenacion
   y repeticion, indexado y slicing, inmutabilidad, todos los metodos (upper,
   lower, capitalize, title, swapcase, strip, lstrip, rstrip, split, rsplit,
   splitlines, join, partition, replace, find, rfind, index, count,
   startswith, endswith, isdigit, isalpha, isalnum, isspace, isupper, islower,
   center, ljust, rjust, zfill), recorrer caracteres, ord y chr, contar
   palabras, invertir cadena, palindromo.

6. ENTRADA/SALIDA Y FICHEROS: print con sep y end, input y conversion,
   validacion de entrada con bucle, open y modos (r, w, a, x, b, t, +),
   encoding, read/readline/readlines, recorrer por lineas, write/writelines,
   seek/tell, close, with (cierre automatico), leer y escribir CSV a mano y
   con el modulo csv, JSON, comprobar si existe un fichero.

7. CLASES Y POO: clase con __init__ y self, atributos de instancia y de clase
   (trampa del atributo de clase que se "tapa"), metodos, herencia simple,
   llamada al constructor padre (Padre.__init__ y super()), sobrescritura,
   herencia multiple y orden de resolucion, clases vacias con pass,
   encapsulacion (_protegido, __privado y name mangling), getters/setters
   clasicos, property() y decorador @property/@x.setter, metodos especiales
   (__init__, __new__, __del__, __str__, __repr__, __len__, __eq__, __lt__...,
   operadores aritmeticos, __contains__, __getitem__, __iter__), singleton,
   @total_ordering, metodos estaticos y de clase, clases abstractas,
   isinstance/issubclass, polimorfismo, devolver copia de lista interna.
   Tabla comparativa con Java.

8. EXCEPCIONES: try/except/else/finally, capturar varias, capturar la
   excepcion con as, Exception generica, raise, relanzar, excepciones
   propias que heredan de Exception, lista de excepciones comunes, assert,
   with como alternativa a finally, patron de pedir un numero hasta que sea
   valido.

9. MODULOS Y PAQUETES: que es un modulo y un paquete, formas de importar
   (import, from-import, alias, import *), __name__ == "__main__", __doc__,
   __init__.py, importar de subpaquetes, pip install, estructura de proyecto
   de examen con varias clases en archivos separados.

10. PROGRAMACION FUNCIONAL: funciones como objetos, funcion que devuelve
    funcion (diccionario de funciones), closures, map (una y varias listas),
    filter, reduce con valor inicial, lambda, max/min/sorted con key,
    combinaciones filter+map+reduce sobre diccionarios (items), comprension
    con varios for, generadores con parentesis y con yield, convertir a lista,
    decoradores basicos, any/all, zip, enumerate.

11. BASES DE DATOS: drivers, conexion sqlite3 y pymysql/MySQLdb, cursor,
    execute, consultas parametrizadas (? en sqlite, %s en MySQL), commit,
    rollback, fetchone/fetchall/fetchmany, recorrer el cursor, executemany,
    lastrowid, rowcount, cerrar, funcion getConnection, CRUD completo,
    convertir filas en diccionarios, cargar CSV en BD, listado formateado.

12. PANDAS Y MATPLOTLIB: crear DataFrame/Series, leer CSV/Excel, head, info,
    describe, shape, seleccionar columnas y filas (loc, iloc), filtrar,
    ordenar, agrupar, agregar, nuevas columnas, nulos, guardar; graficos de
    lineas, barras, tarta, histograma, dispersion, titulos, ejes, leyenda,
    subplots y guardar figura.

13. ESTRUCTURAS COMPLEJAS DE EXAMEN (codigo completo): jerarquia de clases
    con properties y __str__ (empleado/vendedor), clase contenedora con
    diccionario interno (libro de recetas), lectura de datos por teclado a
    lista de tuplas, estadisticas en diccionario, analisis con
    filter/map/reduce (maximo, minimo, media), diccionario con claves tupla,
    contar palabras de un fichero, CSV -> diccionario -> BD -> listado, menu
    de consola con opciones, CRUD en memoria, ordenaciones, agrupar por clave,
    matriz, algoritmos (maximo manual, busqueda, burbuja, primos, fibonacci,
    MCD, invertir, palindromos, frecuencias).

14. ERRORES TIPICOS Y CHECKLIST: indentacion, dos puntos, = frente a ==,
    concatenar str con int, input devuelve str, mutabilidad, argumento por
    defecto mutable, copia de listas, iteradores que se agotan (map/filter),
    modificar lista mientras se recorre, is frente a ==, division entera,
    olvidar self, olvidar commit, olvidar return, print en vez de return,
    nombres que tapan builtins, y checklist antes de entregar.
"""


# =============================================================================
# 0. IMPORTS Y LIBRERIAS HABITUALES
# =============================================================================
SECCION_0_IMPORTS = """
ETIQUETAS: import, imports, librerias, libraries, modulos, from, as, requisitos

# ---- Del temario ----
from functools import reduce          # reduce (en Python 3 NO es builtin)
from functools import total_ordering  # completa operadores de comparacion
import sqlite3                        # BD SQLite (viene de serie)
import pymysql                        # MySQL (pip install pymysql) - usado en clase
# import MySQLdb                      # MySQL (pip install mysqlclient) - en el PDF
import pandas as pd                   # tablas / DataFrames
import matplotlib.pyplot as plt       # graficos
import warnings                       # silenciar avisos (lectura de Excel)

# ---- (EXTRA, no aparece en el temario) estandar utiles ----
import math          # sqrt, pi, floor, ceil, pow, factorial, gcd
import random        # randint(a,b), random(), choice(lista), shuffle(lista), sample
import os            # os.path.exists(ruta), os.listdir(), os.remove()
import sys           # sys.exit(), sys.argv
import csv           # csv.reader / csv.writer / DictReader
import json          # json.load / json.dump
import copy          # copy.copy (superficial) / copy.deepcopy (profunda)
from datetime import date, datetime, timedelta
from collections import Counter, defaultdict, namedtuple, OrderedDict
import statistics    # mean, median, mode, stdev

# Instalar librerias (en terminal o en Jupyter):
#   pip install pymysql pandas matplotlib openpyxl
#   %pip install pandas      <- dentro de un notebook
# [JAVA] import de Python ~ import de Java + anadir el .jar (pip ~ Maven)
"""


# =============================================================================
# 1. SINTAXIS BASICA Y TIPOS
# =============================================================================
SECCION_1_SINTAXIS = """
ETIQUETAS: sintaxis, tipos, types, variables, operadores, operators, basico, basics

# --- 1.1 Generalidades ---
# ETIQUETAS: indentacion, sangria, bloques, llaves, punto y coma, comentarios, comments
# - Bloques por INDENTACION (4 espacios). No hay llaves {} ni ; al final.
# - Tras if/for/while/def/class/try... va ":" obligatorio.
# - Comentario de linea: #     Comentario de documentacion: docstring (cadena
#   triple justo despues de def/class), parecido a JavaDoc.
# - Sensible a mayusculas: Nombre != nombre
# - [JAVA] No hay main obligatorio, no hay tipos al declarar, no hay new.
# - Ejecutar: python fichero.py     (#!/usr/bin/env python en Linux/Mac)

# --- 1.2 Variables y tipado ---
# ETIQUETAS: variable, declarar, asignar, tipado dinamico, dynamic typing, None, null, type, isinstance
variable = valor                 # no se declara el tipo
a, b, c = 1, 2, 3                # asignacion multiple
a, b = b, a                      # intercambio (swap) sin auxiliar
x = y = 0                        # mismo valor a varias
nada = None                      # equivale a null de Java
type(variable)                   # devuelve la clase: <class 'int'>
isinstance(variable, int)        # True/False (admite tupla de tipos: (int, float))
id(variable)                     # identidad (direccion) del objeto
del variable                     # borra el nombre
# Tipado DINAMICO (la variable puede cambiar de tipo) pero FUERTE
# ("3" + 3 da TypeError, no convierte solo).
# Una variable no asignada NO vale None: usarla da NameError.
# Constantes: por convenio en MAYUSCULAS (no hay final). MAX = 10

# --- 1.3 Tipos basicos ---
# ETIQUETAS: int, float, complex, str, bool, entero, real, decimal, booleano, cadena
entero = 3            # int (sin limite de tamano, no hay long)
real = 67.8           # float (doble precision)
complejo = 4 + 5j     # complex   .real  .imag
cadena = "hola"       # str (comillas simples o dobles, no hay char)
booleano = True       # True / False con mayuscula
grande = 1_000_000    # guiones bajos permitidos
cientifico = 1.5e3    # 1500.0
float("inf"), float("-inf"), float("nan")

# --- 1.4 Conversion de tipos (siempre explicita) ---
# ETIQUETAS: conversion, casting, cast, parse, parseInt, convertir, str, int, float, bool
int("42")          # 42           int("3.5") -> ValueError
int(3.99)          # 3 (trunca)
int("ff", 16)      # 255 (base)
float("3.5")       # 3.5
str(42)            # "42"         [JAVA] String.valueOf / Integer.parseInt
bool(0), bool(""), bool([]), bool(None)   # False (valores "falsy")
bool("algo"), bool(5)                      # True
list("abc")        # ['a','b','c']
tuple([1, 2])      # (1, 2)
set([1, 1, 2])     # {1, 2}
dict([("a", 1)])   # {'a': 1}
round(3.14159, 2)  # 3.14  (redondeo bancario: round(2.5) == 2)
chr(65), ord("A")  # 'A', 65
hex(255), bin(5), oct(8)

# --- 1.5 Operadores aritmeticos ---
# ETIQUETAS: aritmeticos, suma, resta, division, modulo, resto, potencia, division entera, floor
a + b    # suma (tambien concatena str/list)
a - b
a * b    # multiplica (str * int repite)
a / b    # division REAL siempre: 7/2 = 3.5   [JAVA] en Java 7/2 = 3
a // b   # division ENTERA (suelo): 7//2 = 3, -7//2 = -4
a % b    # resto (modulo) - signo del divisor
a ** b   # potencia: 2**10 = 1024
-a       # negacion
divmod(7, 2)   # (3, 1) cociente y resto
abs(-3), pow(2, 3), pow(2, 3, 5)
# Asignacion aumentada: += -= *= /= //= %= **=   (NO existe ++ ni --)

# --- 1.6 Operadores de bits ---
# ETIQUETAS: bits, bitwise, and, or, xor, desplazamiento, shift
a & b   # AND      a | b  # OR      a ^ b  # XOR
~a      # NOT      a << n # desplaza izq   a >> n # desplaza der

# --- 1.7 Relacionales, logicos, pertenencia, identidad ---
# ETIQUETAS: comparacion, relacional, logico, and, or, not, in, is, igualdad, equals
==  !=  <  >  <=  >=
0 < x < 10            # comparaciones encadenadas (valido en Python)
and  or  not          # [JAVA] && || !
x in coleccion        # pertenencia (listas, str, dict -> claves, set)
x not in coleccion
a is b                # misma referencia (identidad) [JAVA] ==
a == b                # mismo valor (llama a __eq__) [JAVA] equals()
x is None             # forma correcta de comparar con None
# and/or devuelven uno de los operandos: nombre = entrada or "anonimo"

# --- 1.8 f-strings y formato ---
# ETIQUETAS: f-string, format, formatear, printf, String.format, decimales, plantilla, interpolar
f"Hola {nombre}, tienes {edad} anos"
f"{precio:.2f}"           # 2 decimales
f"{n:5d}"                 # entero anchura 5 (derecha)
f"{texto:<10}"            # alinear izq en 10   > derecha   ^ centro
f"{texto:*^20}"           # centrado rellenando con *
f"{n:05d}"                # ceros a la izquierda: 00042
f"{n:,}"                  # separador de miles: 1,000,000
f"{x:.1%}"                # porcentaje: 0.256 -> 25.6%
f"{x:e}"                  # notacion cientifica
f"{a + b}"                # admite expresiones
f"{variable=}"            # (EXTRA) depuracion: variable=valor
f"{{llaves}}"             # llaves literales
"Hola {} tienes {}".format(nombre, edad)     # metodo format
"Hola {0} {1} {0}".format(a, b)              # por posicion
"Hola {n}".format(n=nombre)                  # por nombre
"Hola %s tienes %d y %.2f" % (nombre, edad, precio)   # estilo antiguo (C)

# --- 1.9 Funciones integradas utiles (builtins) ---
# ETIQUETAS: builtins, funciones integradas, len, max, min, sum, sorted, abs, round
len(x)  max(...)  min(...)  sum(iterable, inicio)  abs(x)  round(x, n)
sorted(iterable, key=..., reverse=...)  reversed(sec)  enumerate(sec, inicio)
zip(a, b)  map(f, it)  filter(f, it)  any(it)  all(it)  range(...)
input(msg)  print(...)  type(x)  isinstance(x, T)  id(x)  help(x)  dir(x)
int() float() str() bool() list() tuple() dict() set() chr() ord()
hasattr(obj, "attr")  getattr(obj, "attr", defecto)  setattr(obj, "attr", v)
"""


# =============================================================================
# 2. CONTROL DE FLUJO
# =============================================================================
SECCION_2_FLUJO = """
ETIQUETAS: control de flujo, condicional, bucle, loop, if, for, while, iterar

# --- 2.1 if / elif / else ---
# ETIQUETAS: if, else, elif, else if, condicional, condicion, si, switch
if condicion:
    ...
elif otra_condicion:          # [JAVA] else if
    ...
else:
    ...
# Sin parentesis obligatorios. Bloque vacio -> pass
# Falsy: False, None, 0, 0.0, "", [], (), {}, set()  -> if lista: (no vacia)

# --- 2.2 Operador ternario / asignacion condicional ---
# ETIQUETAS: ternario, ternary, condicional en linea, ?:, inline if
var = "par" if num % 2 == 0 else "impar"     # [JAVA] cond ? a : b

# --- 2.3 while ---
# ETIQUETAS: while, mientras, bucle condicional, do while, repetir
while condicion:
    ...
    contador += 1
# while True + break (simula do-while, no existe do-while en Python)
while True:
    entrada = input("> ")
    if entrada == "adios":
        break
    print(entrada)
# Con bandera
salir = False
while not salir:
    entrada = input("> ")
    if entrada == "adios":
        salir = True

# --- 2.4 for (siempre es for-each) ---
# ETIQUETAS: for, for each, foreach, recorrer, iterar, iterate, range, enumerate, zip
for elemento in secuencia:             # lista, tupla, str, dict, set, fichero...
    ...
for i in range(n):            # 0..n-1        [JAVA] for(int i=0;i<n;i++)
for i in range(ini, fin):     # ini..fin-1 (fin EXCLUIDO)
for i in range(ini, fin, paso):
for i in range(10, 0, -1):    # 10..1 cuenta atras
for i in range(len(lista)):   # por indice (mejor enumerate)
for i, elem in enumerate(lista):          # indice y valor
for i, elem in enumerate(lista, 1):       # indice empezando en 1
for a, b in zip(lista1, lista2):          # en paralelo (para en la mas corta)
for clave in dic:                         # claves
for clave, valor in dic.items():          # pares
for elem in reversed(lista):              # al reves
for elem in sorted(lista):                # ordenado sin modificar
for _ in range(3):                        # _ = variable que no se usa
for (nombre, precio, uds) in lista_tuplas:  # desempaquetar tuplas

# --- 2.5 break / continue / pass / else de bucle ---
# ETIQUETAS: break, continue, pass, salir del bucle, saltar, else del bucle
break      # sale del bucle mas interno
continue   # salta a la siguiente iteracion
pass       # no hace nada (bloque vacio obligatorio)
for x in lista:
    if x == buscado:
        print("encontrado")
        break
else:                     # se ejecuta si NO hubo break (tambien en while)
    print("no encontrado")
# Romper bucles anidados: usar una bandera o meterlo en funcion y return.

# --- 2.6 match (Python 3.10+) ---
# ETIQUETAS: match, case, switch, segun, patron, pattern matching
# (EXTRA, no aparece en el temario)
match opcion:
    case 1:
        ...
    case 2 | 3:                  # varios valores
        ...
    case "salir":
        ...
    case (x, y):                 # desempaqueta tupla
        ...
    case _ if opcion > 100:      # guarda
        ...
    case _:                      # default
        ...
# No hay "fall-through" ni break.
"""


# =============================================================================
# 3. FUNCIONES
# =============================================================================
SECCION_3_FUNCIONES = """
ETIQUETAS: funciones, functions, metodos, def, return, parametros, argumentos

# --- 3.1 Definicion basica ---
# ETIQUETAS: def, definir funcion, return, docstring, devolver
def mi_funcion(param1, param2):
    # Aqui iria el docstring (cadena triple) como documentacion.
    resultado = param1 * param2
    return resultado          # sin return -> devuelve None
# [JAVA] sin tipo de retorno, sin public/static. Se define ANTES de llamarla.

# --- 3.2 Retorno multiple ---
# ETIQUETAS: devolver varios valores, return multiple, tupla, desempaquetar, unpack
def f(x, y):
    return x * 2, y * 2      # devuelve tupla (x*2, y*2)
a, b = f(1, 2)
a, *resto = [1, 2, 3, 4]     # a=1, resto=[2,3,4]

# --- 3.3 Parametros por defecto y por nombre ---
# ETIQUETAS: parametros por defecto, default, opcional, keyword arguments, nombrados
def imprimir(texto, veces=1):
    print(veces * texto)
imprimir("hola")                  # usa veces=1
imprimir("hola", 2)
imprimir(veces=3, texto="hola")   # por nombre, en cualquier orden
# Los parametros con defecto van DESPUES de los obligatorios.
# [JAVA] sustituye a la sobrecarga de metodos (Python no tiene sobrecarga:
# la ultima def con ese nombre "pisa" a la anterior).
# TRAMPA: defecto mutable se comparte entre llamadas
def mal(x, lista=[]):        # MAL
    lista.append(x); return lista
def bien(x, lista=None):     # BIEN
    if lista is None:
        lista = []
    lista.append(x); return lista

# --- 3.4 *args y **kwargs ---
# ETIQUETAS: args, kwargs, varargs, numero variable de argumentos, variadic, asterisco
def varios(param1, param2, *otros):        # otros es una TUPLA
    for val in otros:
        print(val)
varios(1, 2, 3, 4)                          # otros = (3, 4)
def varios2(param1, **otros):               # otros es un DICCIONARIO
    for clave, valor in otros.items():
        print(clave, valor)
varios2(1, tercero=3, cuarto=4)             # otros = {'tercero':3,'cuarto':4}
def todo(a, b=0, *args, solo_nombre=None, **kwargs): ...
# Orden: normales, con defecto, *args, solo-por-nombre, **kwargs
# Desempaquetar al llamar:
lista = [1, 2]; f(*lista)                   # f(1, 2)
dic = {"x": 1, "y": 2}; f(**dic)            # f(x=1, y=2)
# [JAVA] *args ~ String... args

# --- 3.5 Paso de parametros ---
# ETIQUETAS: paso por valor, por referencia, mutable, inmutable, modificar parametro
def f(x, y):
    x = x + 3          # int inmutable: NO cambia fuera
    y.append(23)       # lista mutable: SI cambia fuera
x = 22; y = [22]
f(x, y)                # fuera: x = 22, y = [22, 23]
# Se pasa la REFERENCIA al objeto (como objetos en Java). Reasignar el
# parametro no afecta fuera; mutar el objeto (append, dic[k]=..) si.
# Para no modificar el original: pasar lista[:] o lista.copy()

# --- 3.6 Ambito: global y nonlocal ---
# ETIQUETAS: global, nonlocal, scope, ambito, variable global, alcance
contador = 0
def incrementar():
    global contador      # sin esto -> UnboundLocalError al asignar
    contador += 1
def externa():
    n = 0
    def interna():
        nonlocal n       # variable de la funcion de fuera
        n += 1
    interna(); return n

# --- 3.7 Lambda ---
# ETIQUETAS: lambda, funcion anonima, anonymous function, arrow function, expresion
cuadrado = lambda n: n ** 2
suma = lambda a, b: a + b
par = lambda n: "par" if n % 2 == 0 else "impar"
# Solo UNA expresion, sin return ni sentencias. [JAVA] (a,b) -> a+b
# Uso tipico: key de sorted/max/min, map, filter, reduce (ver seccion 10).

# --- 3.8 Recursividad ---
# ETIQUETAS: recursividad, recursion, recursivo, factorial, fibonacci, caso base
def factorial(n):
    if n <= 1:              # caso base
        return 1
    return n * factorial(n - 1)
def fibonacci(n):
    if n < 2:
        return n
    return fibonacci(n - 1) + fibonacci(n - 2)
def suma_lista(l):
    if not l:
        return 0
    return l[0] + suma_lista(l[1:])
def potencia(base, exp):
    return 1 if exp == 0 else base * potencia(base, exp - 1)
def busqueda_binaria(l, x, ini=0, fin=None):    # l ordenada
    if fin is None:
        fin = len(l) - 1
    if ini > fin:
        return -1
    medio = (ini + fin) // 2
    if l[medio] == x:
        return medio
    if l[medio] < x:
        return busqueda_binaria(l, x, medio + 1, fin)
    return busqueda_binaria(l, x, ini, medio - 1)
# Limite de recursion ~1000 (RecursionError). sys.setrecursionlimit(n)

# --- 3.9 Funciones anidadas y type hints ---
# ETIQUETAS: funcion dentro de funcion, anidada, type hints, anotaciones, tipos
def externa(x):
    def auxiliar(y):
        return y * 2
    return auxiliar(x)
def area(base: float, altura: float = 1.0) -> float:   # (EXTRA) solo informativo
    return base * altura

# --- 3.10 main ---
# ETIQUETAS: main, punto de entrada, entry point, __main__
def main():
    ...
if __name__ == "__main__":     # solo se ejecuta si lanzas este fichero
    main()
"""


# =============================================================================
# 4. COLECCIONES
# =============================================================================
SECCION_4_COLECCIONES = """
ETIQUETAS: colecciones, collections, listas, tuplas, diccionarios, conjuntos, arrays, estructuras de datos

# Resumen:            mutable  ordenada  duplicados  sintaxis   [JAVA]
#  list   lista        si       si        si          [1, 2]     ArrayList
#  tuple  tupla        NO       si        si          (1, 2)     record/array final
#  dict   diccionario  si       si(3.7+)  claves no   {"a": 1}   HashMap/LinkedHashMap
#  set    conjunto     si       NO        NO          {1, 2}     HashSet

# --- 4.1 LISTAS ---
# ETIQUETAS: lista, list, array, vector, arraylist, append, slicing, indices
l = []                          # vacia      l = list()
l = [22, True, "texto", [1, 2]] # tipos mezclados
l = [0] * 10                    # 10 ceros
l = list(range(5))              # [0,1,2,3,4]
l[0]          # primero
l[-1]         # ultimo        l[-2] penultimo
l[1][0]       # lista anidada
l[0] = 99     # modificar
len(l)        # tamano        [JAVA] size()
# Slicing  l[inicio:fin:paso]   (fin EXCLUIDO, devuelve COPIA)
l[1:3]   l[:2]   l[2:]   l[:]   l[::2]   l[::-1] (invertida)   l[-3:] (3 ultimos)
l[0:2] = [0, 1]       # sustituye un rango (puede cambiar el tamano)
l[0:2] = []           # borra un rango
del l[0]              # borra por indice      del l[1:3]
# METODOS
l.append(x)           # anade al final (un elemento)          [JAVA] add
l.extend(iterable)    # anade todos los elementos             [JAVA] addAll
l.insert(i, x)        # inserta en posicion i
l.remove(x)           # borra la PRIMERA aparicion (ValueError si no esta)
l.pop()               # quita y devuelve el ultimo
l.pop(i)              # quita y devuelve el de la posicion i
l.clear()             # vacia
l.index(x[, ini[, fin]])  # posicion de x (ValueError si no esta)
l.count(x)            # veces que aparece
l.sort()              # ordena EN SITIO, devuelve None
l.sort(reverse=True)  # descendente
l.sort(key=len)       # por criterio  key=lambda t: t[1]  key=str.lower
l.reverse()           # invierte en sitio, devuelve None
l.copy()              # copia superficial (= l[:] = list(l))
# OPERADORES Y FUNCIONES
l1 + l2   l * 3   x in l   x not in l
sorted(l)  sorted(l, key=..., reverse=True)   # NUEVA lista
min(l) max(l) sum(l) len(l) any(l) all(l)
list(reversed(l))  list(enumerate(l))  list(zip(l1, l2))
" ".join(lista_de_str)         # lista -> cadena
# Ordenar por varios criterios: key=lambda p: (p.edad, p.nombre)
# Descendente en un criterio numerico: key=lambda p: (-p.edad, p.nombre)
# TRAMPA: l = l.sort() -> l vale None. Usa l.sort() o l = sorted(l).

# --- 4.2 Matrices (listas de listas) ---
# ETIQUETAS: matriz, matrix, bidimensional, 2D, tabla, filas, columnas
m = [[0] * columnas for _ in range(filas)]   # BIEN
m = [[0] * columnas] * filas                 # MAL: todas las filas son la MISMA
m[i][j] = valor
for fila in m:
    for celda in fila:
        ...
traspuesta = [list(f) for f in zip(*m)]
suma_total = sum(sum(f) for f in m)

# --- 4.3 TUPLAS ---
# ETIQUETAS: tupla, tuple, inmutable, registro, par, desempaquetar
t = (1, 2, True, "python")
t = 1, 2, 3            # parentesis opcionales
t = (1,)               # tupla de UN elemento (la coma es obligatoria; (1) es int)
t = ()                 # vacia
t[0]  t[0:2]  len(t)  x in t  t + (4,)  t * 2
t.count(x)  t.index(x)            # unicos metodos
a, b, c = t                       # desempaquetado
nombre, precio, uds = ("Tapa", 3.5, 25)
# Inmutables: t[0] = 5 -> TypeError (pero si contiene una lista, la lista SI cambia)
# Sirven como claves de diccionario y elementos de set (las listas NO).
# Lista de tuplas = tipico "tabla de registros": [(nombre, precio, uds), ...]
# (EXTRA) namedtuple:
from collections import namedtuple
Punto = namedtuple("Punto", ["x", "y"])
p = Punto(1, 2); p.x; p.y

# --- 4.4 DICCIONARIOS ---
# ETIQUETAS: diccionario, dict, map, hashmap, clave valor, key value, asociativo
d = {}                                 # vacio (OJO: {} es dict, no set)
d = {"clave": valor, "otra": valor2}
d = dict(a=1, b=2)
d = dict(zip(claves, valores))
d = dict.fromkeys(["a", "b"], 0)       # {'a':0,'b':0}
d[clave]                    # leer (KeyError si no existe)     [JAVA] get
d[clave] = valor            # insertar o actualizar            [JAVA] put
d.get(clave)                # None si no existe
d.get(clave, defecto)       # valor por defecto               [JAVA] getOrDefault
clave in d                  # existe la clave? (no busca en valores) [JAVA] containsKey
valor in d.values()         # buscar en valores
del d[clave]                # borrar (KeyError si no existe)
d.pop(clave)                # borra y devuelve (KeyError si no existe)
d.pop(clave, defecto)       # sin error
d.popitem()                 # quita y devuelve el ultimo par (clave, valor)
d.keys()                    # vista de claves     list(d.keys())
d.values()                  # vista de valores
d.items()                   # vista de tuplas (clave, valor)  [JAVA] entrySet
d.update(otro_dict)         # fusiona/actualiza   d.update(a=1)
d.setdefault(clave, defecto)  # devuelve d[clave]; si no existe la crea con defecto
d.clear()
d.copy()                    # copia superficial
len(d)
d3 = d1 | d2                # (EXTRA, 3.9+) union de diccionarios
# Recorrer
for k in d: ...
for k, v in d.items(): ...
for v in d.values(): ...
for k in sorted(d): ...                                   # por clave
for k, v in sorted(d.items(), key=lambda it: it[1]): ...  # por valor
for k, v in sorted(d.items(), key=lambda it: it[1], reverse=True): ...
max(d, key=d.get)            # clave con el valor maximo
# Contar frecuencias (patron estrella)
frec = {}
for x in datos:
    frec[x] = frec.get(x, 0) + 1
# Agrupar
grupos = {}
for x in datos:
    grupos.setdefault(clave_de(x), []).append(x)
# Valores compuestos: d = {"Tortilla": [("Huevos", 200), ("Patatas", 500)]}
#   d["Tortilla"][0][1] -> 200
# Claves tupla: d = {("ferry", 1): [2500, 350]}  ->  for (tipo, id), (carga, pas) in d.items()
# No se puede cambiar el tamano del dict mientras se recorre:
#   for k in list(d.keys()): del d[k]   <- recorrer una copia
# (EXTRA) Counter y defaultdict:
from collections import Counter, defaultdict
Counter("abracadabra").most_common(2)     # [('a',5),('b',2)]
dd = defaultdict(list); dd["x"].append(1) # crea la lista sola
dd2 = defaultdict(int); dd2["x"] += 1

# --- 4.5 CONJUNTOS ---
# ETIQUETAS: conjunto, set, sin duplicados, union, interseccion, diferencia, hashset
s = set()                    # vacio (no {} )
s = {1, 2, 3}
s = set(lista)               # quitar duplicados: list(set(lista)) (pierde orden)
s.add(x)                     # anadir
s.update(iterable)           # anadir varios
s.remove(x)                  # KeyError si no esta
s.discard(x)                 # sin error
s.pop()                      # quita uno cualquiera
s.clear()  len(s)  x in s
a | b   a.union(b)           # union
a & b   a.intersection(b)    # interseccion
a - b   a.difference(b)      # diferencia
a ^ b   a.symmetric_difference(b)
a <= b  a.issubset(b)        a >= b  a.issuperset(b)   a.isdisjoint(b)
frozenset([1, 2])            # set inmutable (puede ser clave de dict)

# --- 4.6 COMPRENSIONES ---
# ETIQUETAS: comprension, comprehension, list comprehension, generar lista, filtrar, transformar
[expr for x in iterable]                     # map
[expr for x in iterable if cond]             # filter + map
[a if cond else b for x in iterable]         # ternario (if va ANTES del for)
[s * v for s in m for v in l if v > 0]       # varios for (= bucles anidados)
[[0] * c for _ in range(f)]                  # matriz
{k: v for k, v in d.items() if v > 0}        # dict comprehension
{v: k for k, v in d.items()}                 # invertir diccionario
{x % 3 for x in lista}                       # set comprehension
(x ** 2 for x in lista)                      # GENERADOR (perezoso)
sum(x ** 2 for x in lista)                   # generador directo en funcion
[x for fila in matriz for x in fila]         # aplanar

# --- 4.7 Copia superficial y profunda ---
# ETIQUETAS: copia, copy, deepcopy, clonar, clone, alias, referencia, shallow, deep
b = a                 # NO copia: mismo objeto (alias)
b = a[:]              # copia superficial
b = a.copy()          # copia superficial (list, dict, set)
b = list(a)           # copia superficial
import copy
b = copy.deepcopy(a)  # copia profunda (listas de listas, dict de listas...)
# Superficial: la lista exterior es nueva pero los elementos internos
# (listas, objetos) son compartidos.
# En una clase, para no exponer la lista interna: return self.__lista[:]
"""


# =============================================================================
# 5. CADENAS
# =============================================================================
SECCION_5_CADENAS = """
ETIQUETAS: cadenas, strings, str, texto, text, caracteres, metodos de cadena

# --- 5.1 Creacion, escapes, operadores ---
# ETIQUETAS: comillas, escape, salto de linea, multilinea, raw, concatenar, repetir
s = "texto"  s = 'texto'           # iguales (no hay char: 'a' es str)
"\\n" salto  "\\t" tab  "\\\\" barra  "\\'" "\\"" comillas
s = r"C:\\ruta\\sin\\escapes"      # raw string (las barras no escapan)
# Cadenas multilinea: con comillas triples (simples o dobles).
s = "a" + "b"      # concatenar (solo str + str: "edad " + str(20))
s = "ab" * 3       # "ababab"
s = ("cadena larga "
     "en varias lineas")           # concatenacion implicita
len(s)  s[0]  s[-1]  s[2:5]  s[::-1] (invertir)  "x" in s
# INMUTABLES: s[0] = "X" -> TypeError. Se crea una nueva: s = "X" + s[1:]
for c in s: ...                    # recorrer caracteres
ord("a") -> 97   chr(97) -> "a"
# Comparar: == compara contenido (no hace falta equals). < compara alfabetico.

# --- 5.2 Metodos (todos devuelven NUEVA cadena) ---
# ETIQUETAS: upper, lower, strip, trim, split, join, replace, find, indexOf, startswith, mayusculas
# Mayusculas / minusculas
s.upper()  s.lower()  s.capitalize()  s.title()  s.swapcase()  s.casefold()
# Espacios / limpiar
s.strip()            # quita espacios y \\n por los dos lados   [JAVA] trim
s.strip(",.")        # quita esos caracteres de los extremos
s.lstrip()  s.rstrip()       # rstrip("\\n") para lineas de fichero
# Dividir / unir
s.split()            # por espacios (cualquier cantidad)
s.split(",")         # por separador
s.split(",", 1)      # maximo 1 corte
s.rsplit(",", 1)     # desde la derecha
s.splitlines()       # por saltos de linea
s.partition(":")     # ('antes', ':', 'despues')
s.rpartition(":")
"-".join(lista)      # une lista de str (los elementos deben ser str)
",".join(map(str, numeros))
# Buscar
s.find(sub[, ini[, fin]])   # posicion o -1                  [JAVA] indexOf
s.rfind(sub)                # ultima posicion o -1           [JAVA] lastIndexOf
s.index(sub)                # como find pero ValueError
s.count(sub[, ini[, fin]])  # numero de apariciones
s.startswith(pre)  s.endswith(suf)   # admiten tupla: s.endswith((".py", ".txt"))
# Reemplazar
s.replace(viejo, nuevo[, cuantas])
# Comprobar (devuelven bool)
s.isdigit()  s.isnumeric()  s.isdecimal()  s.isalpha()  s.isalnum()
s.isspace()  s.isupper()  s.islower()  s.istitle()
# Alinear / rellenar
s.center(20, "*")  s.ljust(10)  s.rjust(10)  s.zfill(5)
# Otros
s.encode("utf-8")  s.removeprefix("x")  s.removesuffix("x")  (3.9+)
s.maketrans / s.translate  (sustituciones multiples de caracteres)

# --- 5.3 Patrones con cadenas ---
# ETIQUETAS: palindromo, invertir, contar vocales, palabras, limpiar puntuacion
invertida = s[::-1]
es_palindromo = s.lower().replace(" ", "") == s.lower().replace(" ", "")[::-1]
vocales = sum(1 for c in s.lower() if c in "aeiouáéíóú")
palabras = s.split()
num_palabras = len(s.split())
for signo in ",.;:?!¿¡()":
    s = s.replace(signo, " ")         # limpiar puntuacion
solo_letras = "".join(c for c in s if c.isalpha())
"""


# =============================================================================
# 6. ENTRADA / SALIDA Y FICHEROS
# =============================================================================
SECCION_6_FICHEROS = """
ETIQUETAS: entrada, salida, input, output, print, teclado, consola, ficheros, files, archivos

# --- 6.1 print e input ---
# ETIQUETAS: print, imprimir, System.out.println, input, leer teclado, Scanner
print("a", "b", 3)              # separa con espacio: a b 3
print("a", "b", sep=",")        # a,b
print("sin salto", end="")      # no anade \\n
print("Total:", total)          # no hace falta str() con comas
print("Total: " + str(total))   # con + SI hace falta str()
print()                          # linea en blanco
print("-" * 40)                  # separador
texto = input("Mensaje: ")       # SIEMPRE devuelve str   [JAVA] Scanner.nextLine
n = int(input("Numero: "))
x = float(input("Precio: "))
a, b = input("Dos numeros: ").split()          # en la misma linea
nums = list(map(int, input().split()))         # lista de enteros
# Validar entrada
while True:
    try:
        n = int(input("Edad: "))
        if n >= 0:
            break
        print("Debe ser positiva")
    except ValueError:
        print("No es un numero")

# --- 6.2 Abrir ficheros ---
# ETIQUETAS: open, abrir fichero, modo, r, w, a, encoding, utf-8, file
f = open(ruta, modo, encoding="utf-8")
# Modos: "r" leer (defecto, error si no existe)
#        "w" escribir (crea o BORRA el contenido)
#        "a" anadir al final (crea si no existe)
#        "x" crear (error si ya existe)
#        "t" texto (defecto)  "b" binario  "+" lectura y escritura
#        combinaciones: "rt", "wt", "r+", "rb", "wb", "a+"
f.close()                        # cerrar SIEMPRE
# Recomendado: with -> se cierra solo aunque haya excepcion
with open("datos.txt", "r", encoding="utf-8") as f:
    contenido = f.read()

# --- 6.3 Leer ---
# ETIQUETAS: leer fichero, read, readline, readlines, lineas, recorrer fichero
f.read()            # todo el fichero en un str
f.read(512)         # 512 caracteres
f.readline()        # una linea (con \\n). "" al llegar al final
f.readlines()       # lista de lineas (con \\n)
f.read().splitlines()   # lista de lineas SIN \\n
for linea in f:         # linea a linea (eficiente)
    linea = linea.strip()   # quitar \\n (si no, print deja lineas en blanco)
# while con readline
while True:
    linea = f.readline()
    if not linea:
        break
    print(linea, end="")

# --- 6.4 Escribir ---
# ETIQUETAS: escribir fichero, write, writelines, guardar, save
f.write("texto\\n")          # NO anade salto solo; solo admite str
f.writelines(["a\\n", "b\\n"])  # lista de str (tampoco anade saltos)
print("texto", file=f)      # (EXTRA) print a fichero (anade \\n)
# Posicion
f.seek(desplazamiento, desde)   # desde: 0 principio, 1 actual, 2 final
f.tell()                        # posicion actual

# --- 6.5 CSV ---
# ETIQUETAS: csv, comas, separado por comas, leer csv, escribir csv, cabecera
# A mano (como en clase)
with open("reservas.csv", "rt", encoding="utf-8") as f:
    lineas = f.readlines()
for linea in lineas[1:]:                  # saltar cabecera
    campos = linea.strip().split(",")
    id_, nombre, personas = int(campos[0]), campos[1], int(campos[2])
with open("salida.csv", "wt", encoding="utf-8") as f:
    for k, v in d.items():
        f.write(k + "," + str(v[0]) + "," + str(v[1]) + "\\n")
        # o: f.write(f"{k},{v[0]},{v[1]}\\n")
# (EXTRA) modulo csv
import csv
with open("f.csv", newline="", encoding="utf-8") as f:
    lector = csv.reader(f, delimiter=",")
    cabecera = next(lector)
    for fila in lector: ...                 # fila es lista de str
with open("f.csv", newline="", encoding="utf-8") as f:
    for fila in csv.DictReader(f): fila["nombre"]
with open("f.csv", "w", newline="", encoding="utf-8") as f:
    w = csv.writer(f)
    w.writerow(["id", "nombre"])
    w.writerows(lista_de_listas)

# --- 6.6 JSON y otros (EXTRA, no aparece en el temario) ---
# ETIQUETAS: json, guardar diccionario, serializar, os.path, existe fichero
import json, os
with open("d.json", "w", encoding="utf-8") as f:
    json.dump(diccionario, f, indent=4, ensure_ascii=False)
with open("d.json", encoding="utf-8") as f:
    diccionario = json.load(f)
os.path.exists("f.txt")      # existe?
os.remove("f.txt")           # borrar
"""


# =============================================================================
# 7. CLASES Y POO
# =============================================================================
SECCION_7_POO = """
ETIQUETAS: clases, class, POO, OOP, objetos, objects, orientacion a objetos

# --- 7.1 Clase basica ---
# ETIQUETAS: class, constructor, __init__, self, this, instanciar, new, atributos, metodos
class Coche:
    # docstring de la clase aqui
    def __init__(self, gasolina):        # "constructor" (inicializador)
        self.gasolina = gasolina         # atributo de instancia (se crea al asignar)
    def arrancar(self):                  # self obligatorio como 1er parametro
        if self.gasolina > 0:
            print("Arranca")
    def conducir(self):
        self.gasolina -= 1
mi_coche = Coche(3)                      # sin new
mi_coche.conducir()
print(mi_coche.gasolina)
# [JAVA] self = this pero EXPLICITO. Sin self.x seria variable local.
# Solo UN __init__ (no hay sobrecarga): usa parametros por defecto.
# Clase vacia: class Algo: pass

# --- 7.2 Atributos de clase vs instancia ---
# ETIQUETAS: atributo de clase, static, estatico, variable de clase, compartido, contador de instancias
class Ejemplo:
    estatico = 7                 # de CLASE (compartido)    [JAVA] static
    contador = 0
    def __init__(self):
        self.instancia = 3       # de INSTANCIA
        Ejemplo.contador += 1    # modificar el de clase: con el nombre de la clase
# TRAMPA: obj.estatico = 9 NO cambia el de clase: crea uno de instancia que lo tapa.

# --- 7.3 Metodos de clase y estaticos (EXTRA, no aparece en el temario) ---
# ETIQUETAS: staticmethod, classmethod, metodo estatico, factoria, constructor alternativo
class Fecha:
    @staticmethod
    def es_bisiesto(a):                  # no recibe self   [JAVA] static
        return a % 4 == 0 and (a % 100 != 0 or a % 400 == 0)
    @classmethod
    def desde_cadena(cls, s):            # recibe la clase: constructor alternativo
        d, m, a = map(int, s.split("/"))
        return cls(d, m, a)

# --- 7.4 Encapsulacion ---
# ETIQUETAS: encapsulacion, private, privado, protected, protegido, name mangling, guion bajo
self.publico        # publico
self._protegido     # convenio: "no lo toques" (accesible igualmente)
self.__privado      # name mangling: se renombra a _Clase__privado
#  obj.__privado -> AttributeError ; obj._Clase__privado -> funciona (no es privado real)
# Igual para metodos: def __metodo_privado(self): ...
# OJO: __init__, __str__ (dos guiones delante Y detras) NO son privados: son especiales.

# --- 7.5 Getters / setters y propiedades ---
# ETIQUETAS: getter, setter, property, propiedad, decorador, @property, validar atributo
# a) Estilo Java
class Fecha:
    def __init__(self):
        self.__dia = 1
    def getDia(self):
        return self.__dia
    def setDia(self, dia):
        if 0 < dia < 32:
            self.__dia = dia
        else:
            print("Error")
# b) property() clasica
    dia = property(getDia, setDia)       # f.dia = 8 llama a setDia
# c) Decoradores (LO QUE SE USA EN LOS EXAMENES)
class Empleado:
    def __init__(self, nombre, salario):
        self.nombre = nombre             # pasa por el setter (valida tambien aqui)
        self.salario = salario
    @property
    def nombre(self):                    # getter
        return self.__nombre
    @nombre.setter
    def nombre(self, valor):             # setter (mismo nombre)
        self.__nombre = valor
    @property
    def salario(self):
        return self.__salario
    @salario.setter
    def salario(self, valor):
        if valor < 0:
            raise ValueError("Salario negativo")
        self.__salario = valor
    @property
    def salario_anual(self):             # propiedad calculada de solo lectura
        return self.__salario * 14
e = Empleado("Ana", 1200)
e.salario = 1500         # setter (sin parentesis)
print(e.salario)         # getter
# Sin setter -> solo lectura (asignar da AttributeError).
# Propiedad que devuelve una lista: devolver COPIA -> return self.__lista[:]
# El atributo interno (__nombre) debe llamarse DISTINTO de la propiedad (nombre)
# o habra recursion infinita.

# --- 7.6 Herencia ---
# ETIQUETAS: herencia, inheritance, extends, super, subclase, padre, hijo, override, sobrescribir
class Instrumento:
    def __init__(self, precio):
        self.precio = precio
    def tocar(self):
        print("Estamos tocando musica")
class Bateria(Instrumento):              # [JAVA] extends
    pass                                  # hereda todo, incluido __init__
class Guitarra(Instrumento):
    def __init__(self, precio, tipo_cuerda):
        super().__init__(precio)                   # SIN self con super()
        # Instrumento.__init__(self, precio)       # alternativa: CON self
        self.tipo_cuerda = tipo_cuerda
    def tocar(self):                     # sobrescritura (override), sin anotacion
        super().tocar()                  # llamar a la version del padre
        print("con cuerdas", self.tipo_cuerda)
    def __str__(self):
        return super().__str__() + " ..."
# Si el hijo define __init__ y NO llama al del padre, los atributos del padre no existen.
# Los atributos __privados del padre NO son accesibles por nombre en el hijo
# (por el name mangling): usa la property o _protegido.
isinstance(g, Instrumento)   # True (tambien para subclases)   [JAVA] instanceof
issubclass(Guitarra, Instrumento)
type(g) == Guitarra          # tipo exacto
type(g).__name__             # "Guitarra"

# --- 7.7 Herencia multiple ---
# ETIQUETAS: herencia multiple, multiple inheritance, MRO, mixin, interfaces
class Terrestre:
    def __init__(self, patas): self.patas = patas
    def desplazar(self): print("anda")
class Acuatico:
    def __init__(self, aletas): self.aletas = aletas
    def desplazar(self): print("nada")
class Cocodrilo(Terrestre, Acuatico):
    def __init__(self, patas, aletas, longitud):
        Terrestre.__init__(self, patas)        # cada padre explicito
        Acuatico.__init__(self, aletas)
        self.longitud = longitud
Cocodrilo(4, 0, 5).desplazar()   # "anda": prevalece el padre de mas a la IZQUIERDA
Cocodrilo.__mro__                # orden de resolucion de metodos
# [JAVA] no existe herencia multiple de clases (solo interfaces).

# --- 7.8 Metodos especiales (dunder / magic methods) ---
# ETIQUETAS: metodos especiales, magic methods, dunder, __str__, toString, __eq__, equals, compareTo, sobrecarga de operadores
__init__(self, ...)      # inicializa atributos tras crear el objeto
__new__(cls, ...)        # crea el objeto (singleton)
__del__(self)            # al destruirse (finalize) - casi nunca se usa
__str__(self)            # print(obj), str(obj)            [JAVA] toString
__repr__(self)           # representacion tecnica (se ve en listas: print([obj]))
__len__(self)            # len(obj)
__eq__(self, otro)       # ==   [JAVA] equals   (__ne__ -> != se deduce)
__lt__ __le__ __gt__ __ge__   # < <= > >=   [JAVA] compareTo  (permite sorted(lista_objs))
__hash__(self)           # para usar en set/dict si defines __eq__
__add__ __sub__ __mul__ __truediv__   # + - * /   (EXTRA)
__contains__(self, x)    # x in obj   (EXTRA)
__getitem__(self, i)     # obj[i]     (EXTRA)
__iter__(self)           # for x in obj  (EXTRA)
__call__(self, ...)      # obj()      (EXTRA)
class Persona:
    def __init__(self, nombre, edad):
        self.nombre = nombre
        self.edad = edad
    def __str__(self):
        return f"Me llamo {self.nombre} y tengo {self.edad}"
    def __repr__(self):
        return f"Persona({self.nombre!r}, {self.edad})"
    def __eq__(self, otro):
        return isinstance(otro, Persona) and self.nombre == otro.nombre
    def __hash__(self):
        return hash(self.nombre)
    def __lt__(self, otro):
        return self.edad < otro.edad
# __str__ DEBE devolver str (return, no print). Usar str() con numeros si concatenas.

# @total_ordering: defines __eq__ y UNO de (__lt__, __le__, __gt__, __ge__)
from functools import total_ordering
@total_ordering
class Cuadrado:
    def __init__(self, lado): self.lado = lado
    def __lt__(self, otro): return self.lado < otro.lado
    def __eq__(self, otro): return self.lado == otro.lado
# ahora funcionan tambien >, >=, <=

# Singleton con __new__
class EjemploSingleton:
    __instancia = None
    def __new__(cls):
        if cls.__instancia is None:
            cls.__instancia = super().__new__(cls)
        return cls.__instancia
# EjemploSingleton() is EjemploSingleton() -> True

# --- 7.9 Clases abstractas y polimorfismo (EXTRA, no aparece en el temario) ---
# ETIQUETAS: abstracta, abstract, interfaz, interface, ABC, abstractmethod, polimorfismo
from abc import ABC, abstractmethod
class Figura(ABC):
    @abstractmethod
    def area(self): ...
class Circulo(Figura):
    def __init__(self, r): self.r = r
    def area(self): return 3.1416 * self.r ** 2
# Figura() -> TypeError. Sin ABC, alternativa del curso:
#   def area(self): raise NotImplementedError
for fig in lista_figuras:       # polimorfismo por "duck typing": basta con que
    print(fig.area())           # el objeto tenga el metodo, no hace falta interfaz

# --- 7.10 Tabla Java vs Python ---
# ETIQUETAS: java vs python, diferencias, comparacion java
# Java                       | Python
# public class A extends B   | class A(B):
# A(int x){ this.x = x; }    | def __init__(self, x): self.x = x
# new A(3)                   | A(3)
# this                       | self (explicito en cada metodo)
# super(x); super.m()        | super().__init__(x); super().m()
# private int x              | self.__x (name mangling)
# protected                  | self._x (convenio)
# static                     | atributo de clase / @staticmethod
# getX()/setX()              | @property / @x.setter
# toString()                 | __str__
# equals()/hashCode()        | __eq__ / __hash__
# compareTo / Comparable     | __lt__ (+ @total_ordering)
# instanceof                 | isinstance(obj, Clase)
# interface / abstract       | ABC + @abstractmethod
# @Override                  | nada (simplemente redefinir)
# sobrecarga de metodos      | parametros por defecto / *args
# null                       | None
"""


# =============================================================================
# 8. EXCEPCIONES
# =============================================================================
SECCION_8_EXCEPCIONES = """
ETIQUETAS: excepciones, exceptions, errores, try, except, catch, finally, raise, throw

# --- 8.1 try / except / else / finally ---
# ETIQUETAS: try catch, capturar, except, else, finally, manejo de errores
try:
    z = x / y
except ZeroDivisionError as e:       # [JAVA] catch (ArithmeticException e)
    print("Division por cero --", e)
except (NameError, ValueError):      # varias en una
    print("Algun error hay...")
except Exception as e:               # cualquier otra (la mas generica AL FINAL)
    print("Error:", type(e).__name__, e)
else:                                # solo si NO hubo excepcion
    print("Todo ha ido bien")
finally:                             # SIEMPRE se ejecuta
    print("Pase lo que pase")
# Orden: de la mas especifica a la mas general.
# except: a secas captura todo (incluso Ctrl+C) -> mejor except Exception.
# No hay checked exceptions ni "throws" en la firma.

# --- 8.2 Lanzar y relanzar ---
# ETIQUETAS: raise, throw, lanzar excepcion, relanzar, rethrow
raise ValueError("mensaje")          # [JAVA] throw new ...
raise                                # dentro de except: relanza la misma
raise MiError("x") from e            # encadenar (EXTRA)
assert condicion, "mensaje"          # AssertionError si es falsa (EXTRA)

# --- 8.3 Excepciones propias ---
# ETIQUETAS: excepcion personalizada, custom exception, MiError, heredar de Exception
class MiError(Exception):
    def __init__(self, valor):
        super().__init__(valor)
        self.valor = valor
    def __str__(self):
        return "Error " + str(self.valor)
class SaldoInsuficienteError(Exception):     # minima
    pass
try:
    if resultado > 20:
        raise MiError(33)
except MiError as e:
    print(e)                # Error 33

# --- 8.4 Excepciones comunes ---
# ETIQUETAS: tipos de excepciones, ValueError, TypeError, KeyError, IndexError, ZeroDivisionError
ValueError          # int("abc"), valor incorrecto
TypeError           # "a" + 1, tipo incorrecto, faltan argumentos
NameError           # variable no definida
IndexError          # indice fuera de rango
KeyError            # clave de dict inexistente
AttributeError      # atributo/metodo inexistente (None.algo)
ZeroDivisionError   # division por cero
FileNotFoundError   # fichero no existe (hija de OSError / IOError)
PermissionError
ImportError / ModuleNotFoundError
StopIteration       # iterador agotado
RecursionError
UnboundLocalError   # asignar global sin "global"
KeyboardInterrupt   # Ctrl+C
NotImplementedError
# sqlite3.Error, pymysql.MySQLError  -> errores de BD

# --- 8.5 Fichero con control de errores ---
try:
    with open("datos.txt", encoding="utf-8") as f:
        datos = f.read()
except FileNotFoundError:
    print("No existe el fichero")
    datos = ""
"""


# =============================================================================
# 9. MODULOS Y PAQUETES
# =============================================================================
SECCION_9_MODULOS = """
ETIQUETAS: modulos, modules, paquetes, packages, import, from, __init__, __name__, __main__

# --- 9.1 Conceptos ---
# Modulo = archivo .py        Paquete = carpeta con modulos (y __init__.py)
# [JAVA] modulo ~ clase/fichero; paquete ~ package; .egg/.whl ~ .jar
# Un modulo puede tener funciones, clases y variables (no todo dentro de clases).
# El codigo "suelto" del modulo se EJECUTA al importarlo (solo la 1a vez).

# --- 9.2 Formas de importar ---
# ETIQUETAS: import, from import, alias, as, importar funcion, importar clase
import modulo                       # modulo.mi_funcion()
import modulo as m                  # m.mi_funcion()
from modulo import mi_funcion       # mi_funcion()
from modulo import f1, f2, MiClase
from modulo import mi_funcion as mf
from modulo import *                # todo (desaconsejado)
import paq.subpaq.modulo            # paq.subpaq.modulo.func()
from paq.subpaq import modulo       # modulo.func()
from paq.subpaq.modulo import func  # func()
from .modulo import X               # import relativo DENTRO de un paquete

# --- 9.3 __name__ y __main__ ---
# ETIQUETAS: __name__, __main__, if name main, ejecutar vs importar
print("se muestra siempre")
if __name__ == "__main__":          # True si se ejecuta directamente
    print("solo si NO es importacion")
# Al importar, __name__ vale el nombre del modulo.
modulo.__doc__      # docstring del modulo
dir(modulo)         # lo que contiene

# --- 9.4 Estructura de paquete (como Ej53_OOP del repositorio) ---
# ETIQUETAS: estructura proyecto, varias clases, ficheros separados, __init__.py
#  proyecto/
#     main.py                 -> from factory.piece import Piece
#     factory/
#        __init__.py          -> puede estar vacio o reexportar:
#                                from .piece import Piece
#                                from .metal_piece import MetalPiece
#        piece.py             -> class Piece: ...
#        metal_piece.py       -> from .piece import Piece
#                                class MetalPiece(Piece): ...
#  Con el __init__ reexportando: from factory import Piece, MetalPiece
#  Version "plana" (EJ53BOL1TEMA1): Pieza.py, PiezaMetalica.py, Principal.py en
#  la misma carpeta -> from Pieza import Pieza
#  Ejecuta siempre desde la carpeta del main (o falla el import).
#  __pycache__ y .pyc: bytecode compilado (se pueden borrar).
"""


# =============================================================================
# 10. PROGRAMACION FUNCIONAL
# =============================================================================
SECCION_10_FUNCIONAL = """
ETIQUETAS: programacion funcional, functional, orden superior, higher order, map, filter, reduce, lambda, generadores

# --- 10.1 Funciones de orden superior ---
# ETIQUETAS: orden superior, funcion como parametro, devolver funcion, callback, closure
def aplicar(f, x):                 # recibe funcion
    return f(x)
aplicar(len, "hola")               # se pasa SIN parentesis
def saludar(lang):                 # devuelve funcion
    def saludar_es(): print("Hola")
    def saludar_en(): print("Hello")
    def saludar_fr(): print("Salut")
    lang_func = {"es": saludar_es, "en": saludar_en, "fr": saludar_fr}
    return lang_func[lang]
saludar("en")()                    # Hello
def multiplicador(n):              # closure
    return lambda x: x * n
doble = multiplicador(2); doble(5) # 10
# Diccionario de funciones = sustituto de switch para menus:
acciones = {"1": alta, "2": baja, "3": listar}
acciones[opcion]()

# --- 10.2 map ---
# ETIQUETAS: map, transformar, aplicar a todos, mapear
map(funcion, iterable)                    # devuelve ITERADOR perezoso (class 'map')
list(map(lambda n: n ** 2, [1, 2, 3]))    # [1, 4, 9]
list(map(int, ["1", "2"]))                # convertir tipos
list(map(lambda a, b: a + b, l1, l2))     # varias secuencias
list(map(lambda it: it[1][1], d.items())) # extraer campo de items de dict
# [JAVA] stream().map(...)

# --- 10.3 filter ---
# ETIQUETAS: filter, filtrar, seleccionar, where
filter(funcion_booleana, iterable)        # iterador con los que dan True
list(filter(lambda n: n % 2 == 0, l))
list(filter(None, l))                     # quita los falsy
dict(filter(lambda it: it[1] > 10, d.items()))   # filtrar diccionario -> dict

# --- 10.4 reduce ---
# ETIQUETAS: reduce, acumular, fold, sumar todo, maximo con reduce, agregar
from functools import reduce              # OBLIGATORIO importarlo
reduce(lambda x, y: x + y, [1, 2, 3])     # 6   ((1+2)+3)
reduce(lambda acc, x: acc + x[1], lista_tuplas, 0)   # con valor inicial
# max / min de items de un diccionario por un campo:
mayor = reduce(lambda a, b: a if a[1][0] > b[1][0] else b, d.items())
mayor[0]  # clave      mayor[1] valor
# reduce sobre secuencia vacia SIN valor inicial -> TypeError.
# Equivalentes mas sencillos: sum(), max(..., key=...), min(..., key=...)
max(d.items(), key=lambda it: it[1][0])[0]

# --- 10.5 lambda + sorted/max/min ---
# ETIQUETAS: key, ordenar con lambda, maximo por campo, criterio
sorted(lista, key=lambda t: t[1])                 # por 2o campo
sorted(lista, key=lambda t: (t[2], t[0]), reverse=True)
max(lista_objs, key=lambda o: o.precio)
min(d, key=d.get)                                 # clave con menor valor
sorted(d.items(), key=lambda it: it[1], reverse=True)[:3]   # top 3

# --- 10.6 Iteradores: TRAMPA ---
# ETIQUETAS: iterador agotado, consumir, list(map), generator exhausted
# map, filter, zip, generadores se CONSUMEN una vez:
f = filter(cond, l)
list(f)   # [..]
list(f)   # [] <- ya esta vacio
# Si necesitas reutilizarlo o len(): conviertelo a list() primero.

# --- 10.7 Comprension vs map/filter ---
[n ** 2 for n in l]            == list(map(lambda n: n ** 2, l))
[n for n in l if n % 2 == 0]   == list(filter(lambda n: n % 2 == 0, l))
[s * v for s in ["a", "b"] for v in [0, 1, 2, 3] if v > 0]
#  -> ['a', 'aa', 'aaa', 'b', 'bb', 'bbb']

# --- 10.8 Generadores ---
# ETIQUETAS: generador, generator, yield, perezoso, lazy, expresion generadora
gen = (n ** 2 for n in l)            # parentesis, no corchetes
def mi_generador(n, m, s):
    while n <= m:
        yield n                      # devuelve y se "pausa"
        n += s
for x in mi_generador(0, 5, 1):      # 0 1 2 3 4 5
    print(x)
lista = list(mi_generador(0, 5, 1))  # materializar (hay que LLAMAR a la funcion)
g = mi_generador(0, 5, 1); next(g)   # siguiente valor (StopIteration al final)
# No crean la lista en memoria.

# --- 10.9 Decoradores (EXTRA, no aparece en el temario) ---
# ETIQUETAS: decorador, decorator, @, envolver funcion, wrapper
def log(func):
    def envoltura(*args, **kwargs):
        print("Llamando a", func.__name__)
        return func(*args, **kwargs)
    return envoltura
@log
def sumar(a, b): return a + b

# --- 10.10 Otras utilidades funcionales ---
any(x > 10 for x in l)     # alguno cumple
all(x > 0 for x in l)      # todos cumplen
sum(x.precio for x in productos)
list(zip(nombres, edades)) ; dict(zip(nombres, edades))
list(enumerate(l, 1))
"""


# =============================================================================
# 11. ACCESO A BASES DE DATOS
# =============================================================================
SECCION_11_BBDD = """
ETIQUETAS: base de datos, database, BBDD, SQL, sqlite, mysql, pymysql, cursor, JDBC

# --- 11.1 Drivers ---
# pyodbc (ODBC), pymssql (SQL Server), psycopg2 (PostgreSQL),
# MySQLdb / mysqlclient (MySQL), pymysql (MySQL, el usado en clase), sqlite3 (de serie)
# Instalar: pip install pymysql      [JAVA] equivale al driver JDBC

# --- 11.2 Conexion ---
# ETIQUETAS: conectar, connect, conexion, getConnection, host, usuario, password
import sqlite3
conn = sqlite3.connect("mydatabase.db")      # crea el fichero si no existe
import pymysql
def getConnection():
    return pymysql.connect(host="localhost", user="root", password="clave",
                           database="mi_bd", port=3306, charset="utf8mb4")
# MySQLdb.connect(host=..., user=..., passwd=..., db=...)   (nombres distintos!)
# En el examen de clase el host era el contenedor docker: 'php-26-27-db-1'

# --- 11.3 Cursor, execute, commit ---
# ETIQUETAS: cursor, execute, commit, rollback, insert, update, delete, create table
cursor = conn.cursor()
cursor.execute("CREATE TABLE IF NOT EXISTS employees("
               "id INTEGER PRIMARY KEY, name TEXT, salary REAL)")
cursor.execute("INSERT INTO employees VALUES (?, ?, ?)", (1, "John", 700))   # sqlite: ?
cursor.execute("INSERT INTO reserva VALUES (%s, %s, %s)", (1, "Ana", 4))     # MySQL: %s
conn.commit()                 # OBLIGATORIO tras INSERT/UPDATE/DELETE
conn.rollback()               # deshacer
cursor.executemany("INSERT INTO t VALUES (?, ?)", lista_de_tuplas)
cursor.lastrowid              # id autogenerado del ultimo insert
cursor.rowcount               # filas afectadas
# PARAMETROS SIEMPRE como tupla: (valor,) si es uno solo -> la coma!
# NUNCA concatenar valores en el SQL (inyeccion SQL y fallos con comillas).
# [JAVA] PreparedStatement con ?

# --- 11.4 Consultas (SELECT) ---
# ETIQUETAS: select, consulta, query, fetchone, fetchall, fetchmany, resultados, filas, ResultSet
cursor.execute("SELECT id, name FROM employees WHERE salary > ?", (500,))
rows = cursor.fetchall()       # lista de TUPLAS
for row in rows:
    print(row[0], row[1])
for (id_, nombre) in rows: ...
row = cursor.fetchone()        # una tupla o None
while row is not None:
    print(row)
    row = cursor.fetchone()
cursor.fetchmany(5)            # las siguientes 5
for row in cursor:             # el cursor es iterable
    print(row)
# Contar: cursor.execute("SELECT COUNT(*) FROM t"); n = cursor.fetchone()[0]
# Filas como diccionarios:
#   sqlite3: conn.row_factory = sqlite3.Row  -> row["name"]
#   pymysql: conn.cursor(pymysql.cursors.DictCursor) -> row["name"]

# --- 11.5 Cerrar ---
cursor.close()
conn.close()
# Plantilla segura:
conn = None
try:
    conn = getConnection()
    cur = conn.cursor()
    cur.execute(sql, params)
    conn.commit()
except Exception as e:
    if conn:
        conn.rollback()
    print("Error BD:", e)
finally:
    if conn:
        conn.close()
# sqlite3: "with sqlite3.connect(...) as conn:" hace commit/rollback
# automatico pero NO cierra la conexion.
"""


# =============================================================================
# 12. PANDAS Y MATPLOTLIB (cheat sheets FSI del temario)
# =============================================================================
SECCION_12_PANDAS = """
ETIQUETAS: pandas, dataframe, series, excel, csv, tabla, matplotlib, graficos, plot, charts

# --- 12.1 Pandas: crear y leer ---
# ETIQUETAS: DataFrame, Series, read_csv, read_excel, crear tabla
import pandas as pd
s = pd.Series([1, 2, 3], index=["a", "b", "c"])
df = pd.DataFrame({"nombre": ["Ana", "Luis"], "edad": [20, 30]})
df = pd.DataFrame(lista_de_listas, columns=["a", "b"])
df = pd.read_csv("f.csv", sep=",", encoding="utf-8")
df = pd.read_excel("f.xlsx", sheet_name=0, header=7, nrows=53)  # necesita openpyxl
df = pd.read_sql("SELECT * FROM t", conn)
# Avisos del openpyxl al leer Excel (como en clase):
import warnings
with warnings.catch_warnings(record=True):
    warnings.simplefilter("always")
    df = pd.read_excel("3989.xlsx", header=7, nrows=53, engine="openpyxl")

# --- 12.2 Explorar ---
# ETIQUETAS: head, tail, info, describe, shape, columns, dtypes
df.head(n)  df.tail(n)  df.shape  df.columns  df.index  df.dtypes
df.info()   df.describe()   len(df)   df["col"].unique()   df["col"].nunique()
df["col"].value_counts()

# --- 12.3 Seleccionar y filtrar ---
# ETIQUETAS: seleccionar columna, loc, iloc, filtrar filas, where, condicion
df["col"]                 # Series
df[["c1", "c2"]]          # DataFrame
df.loc[fila_etiqueta, "col"]      # por etiqueta
df.iloc[0, 1]   df.iloc[0:5]      # por posicion
df[df["edad"] > 25]
df[(df["edad"] > 25) & (df["ciudad"] == "Sevilla")]    # & | ~ con parentesis
df[df["col"].isin(["a", "b"])]
df.query("edad > 25")

# --- 12.4 Transformar, ordenar, agrupar ---
# ETIQUETAS: ordenar, sort_values, groupby, agrupar, sum, mean, apply, nueva columna
df["total"] = df["precio"] * df["uds"]          # nueva columna
df["cat"] = df["n"].apply(lambda x: "Alta" if x > 25 else "Baja")
df.rename(columns={"viejo": "nuevo"})
df.drop(columns=["c"])   df.drop(index=[0])
df.sort_values("col", ascending=False)   df.sort_values(["a", "b"])
df.groupby("cat")["total"].sum()          # .mean() .count() .max() .min()
df.groupby("cat").agg({"total": "sum", "uds": "mean"})
df["col"].sum() .mean() .max() .min() .std() .median() .idxmax()
df.isnull().sum()   df.dropna()   df.fillna(0)   df.drop_duplicates()
pd.concat([df1, df2])   pd.merge(df1, df2, on="id")
df.set_index("col")   df.reset_index()
df.to_csv("salida.csv", index=False)   df.to_excel("salida.xlsx", index=False)
df.values / df.to_numpy()   df.to_dict()   df["col"].tolist()

# --- 12.5 Matplotlib ---
# ETIQUETAS: matplotlib, pyplot, grafico, plot, bar, barras, pie, tarta, hist, scatter
import matplotlib.pyplot as plt
plt.plot(x, y, color="red", linestyle="--", marker="o", label="serie")  # lineas
plt.bar(categorias, valores)        # barras    plt.barh(...) horizontales
plt.pie(valores, labels=etiquetas, autopct="%1.1f%%")    # tarta
plt.hist(datos, bins=10)            # histograma
plt.scatter(x, y)                   # dispersion
plt.title("Titulo"); plt.xlabel("X"); plt.ylabel("Y")
plt.legend(); plt.grid(True); plt.xticks(rotation=45)
plt.xlim(0, 10); plt.ylim(0, 100)
plt.figure(figsize=(10, 5))
fig, ax = plt.subplots(1, 2)        # varios graficos: ax[0].plot(...), ax[0].set_title(...)
plt.tight_layout()
plt.savefig("grafico.png")
plt.show()
df.plot(x="mes", y="ventas", kind="bar")   # directamente desde pandas
"""


# =============================================================================
# 13. ESTRUCTURAS COMPLEJAS / PATRONES DE EXAMEN
# =============================================================================
SECCION_13_A_JERARQUIA = """
ETIQUETAS: examen, ejercicio completo, jerarquia, herencia, property, empleado, vendedor, __str__, POO completo

from functools import reduce

class Empleado:
    def __init__(self, nombre, salario_base):
        self.nombre = nombre                  # usa los setters
        self.salario_base = salario_base

    @property
    def nombre(self):
        return self.__nombre

    @nombre.setter
    def nombre(self, valor):
        if not valor:
            raise ValueError("Nombre vacio")
        self.__nombre = valor

    @property
    def salario_base(self):
        return self.__salario_base

    @salario_base.setter
    def salario_base(self, valor):
        valor = float(valor)                  # acepta "1200" o 1200
        if valor < 0:
            raise ValueError("Salario negativo")
        self.__salario_base = valor

    def calcular_salario(self, irpf=0):
        return self.salario_base * (100 - irpf) / 100

    def __str__(self):
        return f"Nombre: {self.nombre} Salario base: {self.salario_base:.2f}"


class Vendedor(Empleado):
    COMISION = 0.1                             # atributo de clase

    def __init__(self, nombre, salario_base, ventas=None):
        super().__init__(nombre, salario_base)
        self.__ventas = list(ventas) if ventas else []   # copia: evita alias

    @property
    def ventas(self):
        return self.__ventas[:]                # devuelve copia

    def agregar_venta(self, importe):
        if importe <= 0:
            raise ValueError("Importe no valido")
        self.__ventas.append(importe)

    def calcular_salario(self, irpf=0):        # misma firma que el padre
        bruto = self.salario_base + Vendedor.COMISION * sum(self.__ventas)
        return bruto * (100 - irpf) / 100

    def __str__(self):
        return (super().__str__() + f" - Ventas: {self.ventas}"
                f" - Salario total: {self.calcular_salario():.2f}")


def main():
    plantilla = [Empleado("Pepe", 1200), Vendedor("Juan", 1300, [50, 160, 180])]
    plantilla[1].agregar_venta(100)
    for e in plantilla:
        print(e)                                # polimorfismo de __str__
    print("Total nominas:", sum(e.calcular_salario() for e in plantilla))
    mejor = max(plantilla, key=lambda e: e.calcular_salario())
    print("Mejor pagado:", mejor.nombre)
    solo_vendedores = [e for e in plantilla if isinstance(e, Vendedor)]

if __name__ == "__main__":
    main()
"""

SECCION_13_B_CONTENEDORA = """
ETIQUETAS: clase contenedora, gestor, coleccion de objetos, diccionario interno, libro de recetas, inventario, agenda, CRUD en memoria

from functools import reduce

class RecipeBook:
    def __init__(self):
        self.__recipes = {}   # {"Tortilla": [("Huevos", 200), ("Patatas", 500)], ...}

    def add_recipe(self, name, ingredients):
        if name in self.__recipes:
            return "La receta ya existe en el libro"
        self.__recipes[name] = list(ingredients)
        return "La receta se ha agregado en el libro"

    def remove_recipe(self, name):
        return self.__recipes.pop(name, None) is not None

    def get_recipe_weight(self, name):
        if name not in self.__recipes:
            return 0
        pesos = map(lambda ing: ing[1], self.__recipes[name])
        return reduce(lambda x, y: x + y, pesos, 0)       # o sum(...)

    def find_recipes_by_ingredient(self, ingredient):
        return [nombre for nombre, ings in self.__recipes.items()
                if any(ing[0] == ingredient for ing in ings)]

    def heaviest(self):
        if not self.__recipes:
            return None
        return max(self.__recipes, key=self.get_recipe_weight)

    def __len__(self):
        return len(self.__recipes)

    def __contains__(self, name):
        return name in self.__recipes

    def __str__(self):
        listado = ""
        for name, ingredients in self.__recipes.items():
            listado += f"Receta: {name}\\n"
            for ing_name, weight in ingredients:
                listado += f"- {ing_name}: {weight}g\\n"
            listado += "-" * 60 + "\\n"
        return listado


def main():
    r = RecipeBook()
    print(r.add_recipe("Tortilla", [("Huevos", 200), ("Patatas", 500)]))
    print(r.add_recipe("Tortilla", [("Huevos", 200)]))
    print(r.add_recipe("Bizcocho", [("Harina", 250), ("Azucar", 200), ("Huevos", 150)]))
    print(r)
    print("Peso Tortilla:", r.get_recipe_weight("Tortilla"))
    print("Con huevos:", r.find_recipes_by_ingredient("Huevos"))
    print("Mas pesada:", r.heaviest(), "| Total:", len(r), "| ", "Tortilla" in r)

if __name__ == "__main__":
    main()
"""

SECCION_13_C_TECLADO_ESTADISTICAS = """
ETIQUETAS: leer datos teclado, lista de tuplas, estadisticas, diccionario resultado, filter map reduce, maximo, minimo, media, tapas, equipos, baloncesto

from functools import reduce

def leer_datos(n=4):
    # Devuelve [(nombre, precio, unidades), ...]
    lista = []
    for i in range(1, n + 1):
        nombre = input(f"Nombre {i}: ").strip()
        while True:
            try:
                precio = float(input("Precio: "))
                unidades = int(input("Unidades: "))
                break
            except ValueError:
                print("Dato no valido, repite")
        lista.append((nombre, precio, unidades))
    return lista

def calcular_estadisticas(lista):
    # {nombre: (importe, iva, popularidad)}
    d = {}
    for nombre, precio, uds in lista:          # desempaquetar la tupla
        if uds > 25:
            pop = "Alta"
        elif uds > 15:
            pop = "Media"
        else:
            pop = "Baja"
        importe = precio * uds
        d[nombre] = (importe, importe * 0.1, pop)
    return d

def analizar(d):
    # d.items() -> [(nombre, (importe, iva, pop)), ...]  => it[0], it[1][0..2]
    altas = list(filter(lambda it: it[1][2] == "Alta", d.items()))
    nombres_altas = list(map(lambda it: it[0], altas))
    media_iva = sum(map(lambda it: it[1][1], d.items())) / len(d) if d else 0
    mayor = reduce(lambda a, b: a if a[1][0] > b[1][0] else b, d.items())
    menor = reduce(lambda a, b: a if a[1][0] < b[1][0] else b, d.items())
    total = reduce(lambda acc, it: acc + it[1][0], d.items(), 0)
    ranking = sorted(d.items(), key=lambda it: it[1][0], reverse=True)
    return nombres_altas, media_iva, mayor[0], menor[0], total, ranking

def guardar_csv(d, ruta="estadisticas.csv"):
    with open(ruta, "wt", encoding="utf-8") as f:
        f.write("nombre,importe,iva,popularidad\\n")
        for nombre, (importe, iva, pop) in d.items():
            f.write(f"{nombre},{importe:.2f},{iva:.2f},{pop}\\n")

def main():
    # datos = leer_datos()
    datos = [("Serranito", 3.5, 25), ("Solomillo", 4.2, 18),
             ("Montadito", 2.8, 32), ("Espinacas", 3.0, 21)]
    d = calcular_estadisticas(datos)
    print(d)
    print(analizar(d))
    guardar_csv(d)

if __name__ == "__main__":
    main()
"""

SECCION_13_D_CLAVES_TUPLA = """
ETIQUETAS: diccionario con claves tupla, filtrar diccionario, barcos, ferry, mercante, filter map sobre items, agrupar por tipo

from functools import reduce

barcos = {("ferry", 1): [2500, 350],          # (tipo, id): [carga, pasajeros]
          ("mercante", 2): [1200000, 6500],
          ("mercante", 3): [200000, 3200],
          ("ferry", 4): [3520, 420]}

def obtener_pasajeros(d):
    ferrys = filter(lambda it: it[0][0] == "ferry", d.items())
    return tuple(map(lambda it: it[1][1], ferrys))          # (350, 420)

def mayor_carga(d):
    mercantes = filter(lambda it: it[0][0] == "mercante", d.items())
    mejor = reduce(lambda a, b: a if a[1][0] > b[1][0] else b, mercantes)
    return [mejor[0][1]]                                     # [2]

def agrupar_por_tipo(d):
    grupos = {}
    for (tipo, id_barco), (carga, _) in d.items():
        grupos.setdefault(tipo, []).append(id_barco)
    return grupos                                            # {'ferry': [1, 4], ...}

def carga_total_por_tipo(d):
    totales = {}
    for (tipo, _), (carga, _) in d.items():
        totales[tipo] = totales.get(tipo, 0) + carga
    return totales
"""

SECCION_13_E_FICHERO_PALABRAS = """
ETIQUETAS: contar palabras, frecuencia, fichero de texto, word count, leer fichero escribir fichero, ranking palabras

def contar_palabras(ruta_entrada, ruta_salida):
    try:
        with open(ruta_entrada, "rt", encoding="utf-8") as f:
            texto = f.read()
    except FileNotFoundError:
        print("No existe", ruta_entrada)
        return {}
    texto = texto.upper()
    for signo in ",.;:?!¿¡()\\"'-":
        texto = texto.replace(signo, " ")
    frecuencias = {}
    for palabra in texto.split():              # split() ya ignora espacios y \\n
        frecuencias[palabra] = frecuencias.get(palabra, 0) + 1
    ordenadas = sorted(frecuencias.items(), key=lambda it: (-it[1], it[0]))
    with open(ruta_salida, "wt", encoding="utf-8") as f:
        for palabra, veces in ordenadas:
            f.write(f"{palabra}, {veces}\\n")
    return frecuencias

# Leer fichero con formato "nombre;edad;nota" a lista de diccionarios
def leer_alumnos(ruta):
    alumnos = []
    with open(ruta, encoding="utf-8") as f:
        for num, linea in enumerate(f, 1):
            linea = linea.strip()
            if not linea or linea.startswith("#"):
                continue
            try:
                nombre, edad, nota = linea.split(";")
                alumnos.append({"nombre": nombre, "edad": int(edad), "nota": float(nota)})
            except ValueError:
                print(f"Linea {num} incorrecta: {linea}")
    return alumnos
"""

SECCION_13_F_CSV_BD = """
ETIQUETAS: csv a base de datos, cargar csv, insertar en bd, listado por fecha, reservas, informe, pymysql completo, sqlite completo

import sqlite3
# import pymysql

def get_connection():
    return sqlite3.connect("examen.db")
    # return pymysql.connect(host="localhost", user="root", password="test", database="examen")
# MARCADOR: sqlite usa ?  /  pymysql usa %s
P = "?"

def crear_tabla():
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("CREATE TABLE IF NOT EXISTS reserva("
                "id INTEGER PRIMARY KEY, nombre_cliente TEXT, telefono TEXT, "
                "fecha_reserva TEXT, numero_personas INTEGER, numero_mesa INTEGER)")
    conn.commit()
    conn.close()

def cargar_csv(ruta="reservas.csv"):
    # CSV: id,nombre,telefono,fecha,personas,mesa  -> {id: {...}}
    d = {}
    with open(ruta, "rt", encoding="utf-8") as f:
        lineas = f.readlines()
    for linea in lineas[1:]:
        if not linea.strip():
            continue
        c = linea.strip().split(",")
        d[int(c[0])] = {"nombre": c[1], "telefono": c[2], "fecha": c[3],
                        "personas": int(c[4]), "mesa": int(c[5])}
    return d

def insertar(reservas):
    conn = get_connection()
    try:
        cur = conn.cursor()
        sql = ("INSERT INTO reserva(id, nombre_cliente, telefono, fecha_reserva, "
               f"numero_personas, numero_mesa) VALUES ({P},{P},{P},{P},{P},{P})")
        for id_, r in reservas.items():
            cur.execute(sql, (id_, r["nombre"], r["telefono"], r["fecha"],
                              r["personas"], r["mesa"]))
        conn.commit()
    except Exception as e:
        conn.rollback()
        print("Error al insertar:", e)
    finally:
        conn.close()

def listado_por_fecha(fecha):
    conn = get_connection()
    cur = conn.cursor()
    cur.execute(f"SELECT * FROM reserva WHERE fecha_reserva = {P} ORDER BY numero_mesa",
                (fecha,))                       # tupla de 1 -> coma
    filas = cur.fetchall()
    conn.close()
    texto = f"RESERVAS PARA {fecha}:\\n" + "=" * 27 + "\\n"
    personas = 0
    for f in filas:
        # f = (id, nombre, telefono, fecha, personas, mesa)
        texto += f"Mesa {f[5]}: {f[1]} ({f[4]} personas) - Tel: {f[2]}\\n"
        personas += f[4]
    texto += f"Total {len(filas)} reservas, {personas} personas"
    return texto

def actualizar_mesa(id_, mesa):
    conn = get_connection()
    cur = conn.cursor()
    cur.execute(f"UPDATE reserva SET numero_mesa = {P} WHERE id = {P}", (mesa, id_))
    conn.commit()
    afectadas = cur.rowcount
    conn.close()
    return afectadas > 0

def borrar(id_):
    conn = get_connection()
    cur = conn.cursor()
    cur.execute(f"DELETE FROM reserva WHERE id = {P}", (id_,))
    conn.commit()
    conn.close()

def estadisticas_sql():
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT fecha_reserva, COUNT(*), SUM(numero_personas) "
                "FROM reserva GROUP BY fecha_reserva")
    resultado = {fecha: (n, total) for fecha, n, total in cur.fetchall()}
    conn.close()
    return resultado
"""

SECCION_13_G_MENU = """
ETIQUETAS: menu, menu de consola, opciones, bucle principal, programa completo, pantalla, interfaz texto, CRUD

class Producto:
    def __init__(self, codigo, nombre, precio, stock=0):
        self.codigo = codigo
        self.nombre = nombre
        self.precio = precio
        self.stock = stock
    def __str__(self):
        return f"[{self.codigo}] {self.nombre:<15} {self.precio:>8.2f} EUR  stock: {self.stock}"

class Inventario:
    def __init__(self):
        self.__productos = {}               # {codigo: Producto}
    def alta(self, p):
        if p.codigo in self.__productos:
            raise KeyError(f"Codigo {p.codigo} repetido")
        self.__productos[p.codigo] = p
    def baja(self, codigo):
        return self.__productos.pop(codigo, None)
    def buscar(self, codigo):
        return self.__productos.get(codigo)
    def listar(self, orden="nombre"):
        return sorted(self.__productos.values(), key=lambda p: getattr(p, orden))
    def valor_total(self):
        return sum(p.precio * p.stock for p in self.__productos.values())
    def sin_stock(self):
        return [p for p in self.__productos.values() if p.stock == 0]

def pedir_float(msg):
    while True:
        try:
            return float(input(msg))
        except ValueError:
            print("Numero no valido")

def menu():
    inv = Inventario()
    opciones = "\\n1. Alta\\n2. Baja\\n3. Buscar\\n4. Listar\\n5. Valor total\\n0. Salir"
    while True:
        print(opciones)
        op = input("Opcion: ").strip()
        if op == "1":
            try:
                inv.alta(Producto(input("Codigo: "), input("Nombre: "),
                                  pedir_float("Precio: "), int(pedir_float("Stock: "))))
            except KeyError as e:
                print(e)
        elif op == "2":
            print("Borrado" if inv.baja(input("Codigo: ")) else "No existe")
        elif op == "3":
            p = inv.buscar(input("Codigo: "))
            print(p if p else "No encontrado")
        elif op == "4":
            for p in inv.listar():
                print(p)
        elif op == "5":
            print(f"Valor: {inv.valor_total():.2f}")
        elif op == "0":
            print("Adios")
            break
        else:
            print("Opcion incorrecta")

# if __name__ == "__main__":
#     menu()
"""

SECCION_13_H_ALGORITMOS = """
ETIQUETAS: algoritmos, algorithms, maximo, minimo, buscar, ordenar, burbuja, bubble sort, primos, mcd, fibonacci, media, contar

# Maximo/minimo manual (sin max)
def maximo(l):
    mayor = l[0]
    for x in l[1:]:
        if x > mayor:
            mayor = x
    return mayor

# Posicion del maximo
pos = max(range(len(l)), key=lambda i: l[i])     # o l.index(max(l))

# Media, mediana
def media(l):
    return sum(l) / len(l) if l else 0
def mediana(l):
    s = sorted(l); n = len(s); m = n // 2
    return s[m] if n % 2 else (s[m - 1] + s[m]) / 2

# Busqueda lineal
def buscar(l, x):
    for i, v in enumerate(l):
        if v == x:
            return i
    return -1

# Burbuja
def burbuja(l):
    l = l[:]
    n = len(l)
    for i in range(n - 1):
        cambiado = False
        for j in range(n - 1 - i):
            if l[j] > l[j + 1]:
                l[j], l[j + 1] = l[j + 1], l[j]
                cambiado = True
        if not cambiado:
            break
    return l

# Seleccion
def seleccion(l):
    l = l[:]
    for i in range(len(l)):
        m = min(range(i, len(l)), key=lambda k: l[k])
        l[i], l[m] = l[m], l[i]
    return l

# Primos
def es_primo(n):
    if n < 2:
        return False
    for d in range(2, int(n ** 0.5) + 1):
        if n % d == 0:
            return False
    return True
primos = [n for n in range(2, 100) if es_primo(n)]

# MCD (Euclides) y MCM
def mcd(a, b):
    while b:
        a, b = b, a % b
    return a
def mcm(a, b):
    return a * b // mcd(a, b)

# Fibonacci iterativo
def fib(n):
    a, b = 0, 1
    for _ in range(n):
        a, b = b, a + b
    return a

# Suma de digitos / invertir numero
def suma_digitos(n):
    return sum(int(c) for c in str(abs(n)))
def invertir_numero(n):
    return int(str(n)[::-1])

# Eliminar duplicados manteniendo orden
def sin_duplicados(l):
    vistos, res = set(), []
    for x in l:
        if x not in vistos:
            vistos.add(x); res.append(x)
    return res                       # o list(dict.fromkeys(l))

# Contar pares/impares, positivos/negativos
pares = len([x for x in l if x % 2 == 0])

# Agrupar lista de tuplas por un campo y sumar
def sumar_por(lista, idx_clave, idx_valor):
    res = {}
    for t in lista:
        res[t[idx_clave]] = res.get(t[idx_clave], 0) + t[idx_valor]
    return res

# Tabla de multiplicar formateada
for i in range(1, 11):
    print(f"{7} x {i:2} = {7 * i:3}")

# Piramide
n = 5
for i in range(1, n + 1):
    print(" " * (n - i) + "*" * (2 * i - 1))

# Matriz: suma de filas, columnas, diagonal
m = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
suma_filas = [sum(f) for f in m]
suma_cols = [sum(c) for c in zip(*m)]
diagonal = sum(m[i][i] for i in range(len(m)))
"""


# =============================================================================
# 14. ERRORES TIPICOS Y CHECKLIST FINAL
# =============================================================================
SECCION_14_ERRORES = """
ETIQUETAS: errores tipicos, errores comunes, trampas, pitfalls, bugs, checklist, repaso final

ERRORES TIPICOS
 1. IndentationError: mezclar tabuladores y espacios, o sangria inconsistente.
 2. Olvidar los ":" tras if/for/while/def/class/else/try/except.
 3. = (asignar) vs == (comparar). No existen ++ ni --, ni && || !.
 4. "Total: " + 5 -> TypeError. Usa str(5), comas en print o f-strings.
 5. input() siempre devuelve str: convierte con int()/float().
 6. 7 / 2 = 3.5 (no 3). Division entera: //.
 7. l = l.sort() -> None. sort() y reverse() modifican y devuelven None.
 8. b = a no copia la lista: usa a[:], a.copy() o copy.deepcopy(a).
 9. [[0]*3]*3 crea 3 referencias a la MISMA fila.
10. Argumento por defecto mutable (def f(l=[])) se comparte: usa None.
11. map/filter/zip/generadores se agotan tras recorrerlos una vez.
12. Modificar una lista o dict mientras se recorre: recorre una copia.
13. Tupla de un elemento: (x,) -> tambien en parametros SQL: (fecha,).
14. is vs ==: is es identidad. Usa == para valores e "is None" para None.
15. Olvidar self en los metodos, o self. al usar atributos.
16. super().__init__(args) SIN self; Padre.__init__(self, args) CON self.
17. Property y atributo interno con el mismo nombre -> recursion infinita.
18. __str__ debe hacer RETURN de un str, no print.
19. print en lugar de return en funciones que deben devolver algo.
20. Olvidar conn.commit() tras INSERT/UPDATE/DELETE.
21. Concatenar valores en SQL en vez de parametros (? o %s).
22. Olvidar "from functools import reduce".
23. Lineas de fichero con \\n al final: usa strip(); cabecera CSV: [1:].
24. f.write() solo admite str y no anade salto de linea.
25. Abrir con "w" BORRA el fichero. Para anadir: "a".
26. Llamar a variables como builtins (list, dict, str, sum, max, id, input):
    las "tapas" y luego list(...) falla.
27. dict[clave] con clave inexistente -> KeyError: usa get() o in.
28. reduce sobre lista vacia sin valor inicial -> TypeError.
29. Comparar float con ==: usa abs(a - b) < 1e-9 o round.
30. Cambiar de tipo un atributo por accidente: Empleado("Pepe", "1200")
    guarda un str; luego 1200 * ... falla o repite cadena.
31. obj.atributo_de_clase = x crea uno de instancia (no cambia el de clase).
32. Atributos __privados del padre no se ven como self.__x en el hijo.

CHECKLIST ANTES DE ENTREGAR
 [ ] Imports arriba (reduce, total_ordering, pymysql/sqlite3...).
 [ ] Nombres de funciones/clases EXACTOS a los del enunciado.
 [ ] Las funciones DEVUELVEN (return) lo que pide el enunciado y del tipo
     pedido (lista, tupla, diccionario) - ojo a "lista de un elemento".
 [ ] Probado con los datos de ejemplo del enunciado (deja input comentado
     y una lista fija para probar).
 [ ] __str__ con el formato exacto pedido.
 [ ] Properties/setters donde se pidan; atributos privados con __.
 [ ] super().__init__ en las subclases.
 [ ] Ficheros cerrados (with) y encoding="utf-8".
 [ ] commit() y close() en BD; parametros como tupla.
 [ ] Bloque if __name__ == "__main__": main().
 [ ] Ejecutar el script entero una vez antes de entregar.
"""


# =============================================================================
# FIN. Al ejecutar el archivo solo se muestra este aviso.
# =============================================================================
if __name__ == "__main__":
    secciones = [n for n in dir() if n.startswith("SECCION_") or n == "INDICE_DETALLADO"]
    print("Chuleta cargada correctamente:", len(secciones), "bloques.")
    print("Usa Ctrl+F con '# ETIQUETAS:' o 'ETIQUETAS:' para buscar.")


"""
1. Funciones básicas
Función	¿Para qué sirve?	Ejemplo
print()	Mostrar información	print("Hola")
input()	Pedir datos al usuario	input("Nombre: ")
type()	Saber el tipo de dato	type(5) → int
int()	Convertir a entero	int("10") → 10
float()	Convertir a decimal	float("2.5") → 2.5
str()	Convertir a texto	str(25) → "25"
bool()	Convertir a booleano	bool(1) → True
isinstance()	Comprobar el tipo de un objeto	isinstance(5, int)
id()	Obtener identificador de un objeto	id(5)
help()	Consultar ayuda	help(str)

2. Funciones matemáticas
Estas son bastante habituales en ejercicios.
Función	¿Para qué sirve?	Ejemplo
abs()	Valor absoluto	abs(-5) → 5
round()	Redondear	round(3.567, 2) → 3.57
pow()	Potencia	pow(2, 3) → 8
max()	Obtener el mayor	max(2, 8, 4) → 8
min()	Obtener el menor	min(2, 8, 4) → 2
sum()	Sumar elementos	sum([1, 2, 3]) → 6
divmod()	Obtener cociente y resto	divmod(10, 3) → (3, 1)

3. Funciones para listas, tuplas y colecciones
Muy importantes para el examen
Función	¿Para qué sirve?	Ejemplo
len()	Contar elementos	len([1, 2, 3]) → 3
sorted()	Ordenar una colección	sorted([3, 1, 2]) → [1, 2, 3]
reversed()	Recorrer al revés	list(reversed([1, 2, 3]))
enumerate()	Obtener índice y elemento	list(enumerate(["A","B"]))
zip()	Unir colecciones elemento a elemento	list(zip([1,2],["A","B"]))
any()	Comprobar si alguno es verdadero	any([False, True]) → True
all()	Comprobar si todos son verdaderos	all([True, True]) → True
list()	Crear una lista	list((1, 2))
tuple()	Crear una tupla	tuple([1, 2])
set()	Crear un conjunto sin duplicados	set([1, 1, 2])
dict()	Crear un diccionario	dict(a=1, b=2)
range()	Generar una secuencia de números	list(range(5))

4. Funciones relacionadas con lambda
Estas son especialmente importantes teniendo en cuenta lo que estamos estudiando.
Función	¿Para qué sirve?
map()	Aplicar una función a todos los elementos
filter()	Filtrar elementos según una condición
sorted()	Ordenar usando un criterio con key
max()	Buscar el máximo usando un criterio con key
min()	Buscar el mínimo usando un criterio con key

5. Funciones para cadenas de texto
Función	¿Para qué sirve?
ord()	Obtener el código Unicode de un carácter
chr()	Convertir un código Unicode en carácter
ascii()	Obtener una representación ASCII
repr()	Obtener la representación oficial de un objeto
format()	Dar formato a un valor

6. Funciones para trabajar con objetos y diccionarios
Función	¿Para qué sirve?
getattr()	Obtener un atributo de un objeto
setattr()	Modificar un atributo
hasattr()	Comprobar si existe un atributo
delattr()	Eliminar un atributo
callable()	Comprobar si algo se puede ejecutar
vars()	Obtener los atributos de un objeto
dir()	Ver los atributos y métodos disponibles
"""