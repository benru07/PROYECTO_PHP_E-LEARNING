from Producto import Producto
from Pieza import Pieza
from PiezaPlastico import PiezaPlastico
from PiezaMetalica import PiezaMetalica


pm1 = PiezaMetalica("1", 3, 10.4, "https://fabricante1.com", "Acero")
pp1 = PiezaPlastico("2", 4, 5.7, "https://fabricante2.com", 7)

prducto1 = Producto("1", "Producto 1", [pm1, pp1])

pm2 = PiezaMetalica("3", 2, 8.9, "https://fabricante3.com", "Aluminio")
prducto1.agregarPieza(pm2)

pieza_repetida = PiezaPlastico("2", 4, 5.7, "https://fabricante2.com", 7)
prducto1.agregarPieza(pieza_repetida)

pieza_invalida = Pieza("4", 1, 3.5, "https://fabricante4.com")

prducto1.agregarPieza(pieza_invalida)

pp2 = PiezaPlastico("5", 3, 6.2, "https://fabricante5.com", 8)
prducto1.agregarPieza(pp2)

print("Precio total del producto:", prducto1.precioProducto())
print("Peso total del producto:", prducto1.pesoProducto())
print("Listado de piezas del producto:")
print(prducto1.listadopiezas())