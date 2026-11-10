dia = int(input("Ingrese el día: "))

while dia < 1 or dia > 31:
    print("Día inválido. Ingrese un día entre 1 y 31.")
    dia = int(input("Ingrese el día: "))

mes = int(input("Ingrese el mes: "))

while mes < 1 or mes > 12:
    print("Mes inválido. Ingrese un mes entre 1 y 12.")
    mes = int(input("Ingrese el mes: "))

año = int(input("Ingrese el año: "))

match mes:
    case 1:
        print(f"La fecha ingresada es: {dia} de Enero de {año}")
    case 2:
        print(f"La fecha ingresada es: {dia} de Febrero de {año}")
    case 3:
        print(f"La fecha ingresada es: {dia} de Marzo de {año}")
    case 4:
        print(f"La fecha ingresada es: {dia} de Abril de {año}")
    case 5:
        print(f"La fecha ingresada es: {dia} de Mayo de {año}")
    case 6:
        print(f"La fecha ingresada es: {dia} de Junio de {año}")
    case 7:
        print(f"La fecha ingresada es: {dia} de Julio de {año}")
    case 8:
        print(f"La fecha ingresada es: {dia} de Agosto de {año}")
    case 9:
        print(f"La fecha ingresada es: {dia} de Septiembre de {año}")
    case 10:
        print(f"La fecha ingresada es: {dia} de Octubre de {año}")
    case 11:
        print(f"La fecha ingresada es: {dia} de Noviembre de {año}")
    case 12:
        print(f"La fecha ingresada es: {dia} de Diciembre de {año}")
