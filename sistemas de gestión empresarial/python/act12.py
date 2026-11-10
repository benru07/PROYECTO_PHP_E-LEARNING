a = float(input("Ingrese el valor de a: "))
b = float(input("Ingrese el valor de b: "))
c = float(input("Ingrese el valor de c: "))

if a == 0:
	print("No es una ecuación cuadrática porque a no puede ser 0.")
else:
	discriminante = b ** 2 - 4 * a * c

	if discriminante < 0:
		print("La ecuación no tiene soluciones reales.")
	elif discriminante == 0:
		solucion = -b / (2 * a)
		print(f"La ecuación tiene una solución doble: x = {solucion}")
	else:
		solucion1 = (-b + discriminante ** 0.5) / (2 * a)
		solucion2 = (-b - discriminante ** 0.5) / (2 * a)
		print(f"Las soluciones son: x1 = {solucion1} y x2 = {solucion2}")

