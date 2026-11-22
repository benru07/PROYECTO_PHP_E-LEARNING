from decimal import Decimal, localcontext


def arctan(inverso, precision):
	with localcontext() as contexto:
		contexto.prec = precision
		valor = Decimal(1) / inverso
		termino = valor
		resultado = valor
		exponente = 1

		while abs(termino) > Decimal(10) ** (-precision):
			termino *= -(valor * valor)
			exponente += 2
			resultado += termino / exponente

		return resultado


def calcular_pi(digitos):
	with localcontext() as contexto:
		contexto.prec = digitos + 10
		pi = 16 * arctan(Decimal(5), contexto.prec)
		pi -= 4 * arctan(Decimal(239), contexto.prec)
		return format(+pi, f".{digitos}f")


try:
	digitos = int(input("¿Cuántos dígitos decimales de pi desea calcular? "))

	if digitos < 0:
		print("La cantidad de dígitos no puede ser negativa.")
	else:
		print(f"pi = {calcular_pi(digitos)}")
except ValueError:
	print("Debe introducir un número entero válido.")
