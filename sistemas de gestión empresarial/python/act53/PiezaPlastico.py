from Pieza import Pieza

class PiezaPlastico(Pieza):
    def __init__(self, idPieza: str, peso: int, precio: float, urlFabricante: str, resistencia: int):
        super().__init__(idPieza, peso, precio, urlFabricante)
        self.resistencia = resistencia

    @property
    def resistencia(self) -> int:
        return self._resistencia

    @resistencia.setter
    def resistencia(self, value: int) -> None:
        self._resistencia = value
        
    def __str__(self) -> str:
        return f"{super().__str__()}, Resistencia: {self.resistencia}"