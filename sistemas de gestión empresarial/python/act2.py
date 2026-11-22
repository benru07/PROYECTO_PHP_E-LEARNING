numero1 = int
numero2 = int

print ("dimer el primer numero a ingresar")
numero1 = int(input())
print ("dimer el segundo numero a ingresar")
numero2 = int(input())

nMenu = 0

while nMenu < 1 or nMenu > 4:
    print ("MENU")
    print ("1. Sumar")
    print ("2. Restar")
    print ("3. Multiplicar")
    print ("4. Dividir")
    nMenu = int(input("Ingrese la opcion que desea realizar: "))

    match nMenu:
        case 1:
            print ("La suma es: ", numero1 + numero2)
        case 2:
            print ("La resta es: ", numero1 - numero2)
        case 3:
            print ("La multiplicacion es: ", numero1 * numero2)
        case 4:
            if numero2 == 0:
                print ("No se puede dividir por cero.")
                break
            print ("La division es: ", numero1 / numero2)  