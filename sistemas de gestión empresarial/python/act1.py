import random
aleatorio = random.randint(1, 10)
intentos = 0
victoria = False
while intentos < 3 and not victoria:
    numero = int(input("Adivina el número de 1 al 10:"))
    intentos += 1
    if numero == aleatorio:
        print("GG has ganado")
        victoria = True
    else:
        print("intenta de nuevo")
        if numero < aleatorio:
            print("El número es mayor")
        else:
            print("El número es menor")
if not victoria:
    print("Has perdido, el número era:", aleatorio)