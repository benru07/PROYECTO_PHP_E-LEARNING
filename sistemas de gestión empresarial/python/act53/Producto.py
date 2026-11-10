from Pieza import Pieza
from PiezaPlastico import PiezaPlastico
from PiezaMetalica import PiezaMetalica

class Producto:
    def __init__(self, idProducto: str, nombre: str, piezas: list):
        self.idProducto = idProducto
        self.nombre = nombre
        self.piezas = piezas
        
    @property
    def idProducto(self) -> str:
        return self._idProducto
    
    @idProducto.setter
    def idProducto(self, value: str) -> None:
        if not value:
            print("El id del producto no puede estar vacío")
        self._idProducto = value
        
    @property
    def nombre(self) -> str:
        return self._nombre
    
    @nombre.setter
    def nombre(self, value: str) -> None:
        if not value:
            print("El nombre del producto no puede estar vacío")
        self._nombre = value
        
    @property
    def piezas(self) -> list:
        return self._piezas
    
    @piezas.setter
    def piezas(self, value: list) -> None:
        if not isinstance(value, list):
            print("Las piezas deben ser una lista")
        self._piezas = value    
            
    def agregarPieza(self, oPieza: Pieza):
        if not isinstance(oPieza, (PiezaMetalica, PiezaPlastico)):
            print("La pieza debe ser de tipo PiezaMetalica o PiezaPlastico")
        else:
            if oPieza in self.piezas:
                print("Pieza repetida")
            else:
                self.piezas.append(oPieza)
                print("Alta OK")

    def precioProducto(self):
        precioTotal = 0
        for Pieza in self.piezas:
            precioTotal += Pieza.precio
        return precioTotal
    
    def pesoProducto(self):
        pesoTotal = 0
        for Pieza in self.piezas:
            pesoTotal += Pieza.peso
        return pesoTotal
    
    def listadopiezas(self):
        listado = ""
        for Pieza in self.piezas:
            listado += str(Pieza) + "\n"
        return listado