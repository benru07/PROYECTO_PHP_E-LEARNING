salario = float(input("Ingrese el sueldo del empleado: "))
antiguedad = int(input("Ingrese la antigüedad del empleado: "))

if salario < 1500 and antiguedad >= 10:
    salario = salario * 0.20
    print(f"su salario actualizado es: {salario}")

elif salario < 1500 and antiguedad > 5:
    salario = salario * 0.05
    print(f"su salario actualizado es: {salario}")

elif salario > 1500:
    print(f"su salario actual es: {salario}")