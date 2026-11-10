from Pieza import Pieza

class PiezaMetalica(Pieza):
    def __init__(self, idPieza: str, peso: int, precio: float, urlFabricante: str, metal: str):
        super().__init__(idPieza, peso, precio, urlFabricante)
        self.metal = metal

    @property
    def metal(self) -> str:
        return self._metal

    @metal.setter
    def metal(self, value: str) -> None:
        self._metal = value
        
    def __str__(self) -> str:
        return f"{super().__str__()}, Metal: {self.metal}"