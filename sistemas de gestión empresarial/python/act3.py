temperaturaActual = float(input("Ingrese la temperatura actual: "))
temperaturaDeseada = float(input("Ingrese la temperatura deseada: "))
temperaturaMaxima = float(input("Ingrese la temperatura máxima: "))

if temperaturaActual < temperaturaDeseada:
    print("HACE FRIO SE VA A ENCENDER LA CALEFACCION")
elif temperaturaActual > temperaturaDeseada:
    print("HACE CALOR SE VA A ENCENDER EL AIRE ACONDICIONADO")
else:
    print("LA TEMPERATURA ES IDEAL")
