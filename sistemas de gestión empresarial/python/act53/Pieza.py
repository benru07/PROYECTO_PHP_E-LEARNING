class Pieza:
    def __init__(self, idPieza: str, peso: int, precio: float, urlFabricante: str):
        self.idPieza = idPieza
        self.peso = peso
        self.precio = precio
        self.urlFabricante = urlFabricante
        
    @property
    def idPieza(self) -> str:
        return self._idPieza

    @idPieza.setter
    def idPieza(self, value: str) -> None:
        if not value:
            print("El id de la pieza no puede estar vacío")
        self._idPieza = value

    @property
    def peso(self) -> int:
        return self._peso
    
    @peso.setter
    def peso(self, value: int) -> None:
        if value < 0:
            print("El peso no puede ser negativo")
        self._peso = value

    @property
    def precio(self) -> float:
        return self._precio
    
    @precio.setter
    def precio(self, value: float) -> None:
        if value < 0:
            print("El precio no puede ser negativo")
        self._precio = value

    @property
    def urlFabricante(self) -> str:
        return self._urlFabricante
        
    @urlFabricante.setter
    def urlFabricante(self, value: str) -> None:
        if not value:
            print("La URL del fabricante no puede estar vacía")
        self._urlFabricante = value
        
    def __eq__(self, other) -> bool:
        return isinstance(other, Pieza) and self.idPieza == other.idPieza
        
    def __str__(self):
        return f'Pieza: {self.idPieza}, Peso: {self.peso}, Precio: {self.precio}, URL Fabricante: {self.urlFabricante}'