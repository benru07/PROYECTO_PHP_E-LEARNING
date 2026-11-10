class Animal:
    def __init__(self, idAnimal: int, especie: str):
        self._idAnimal = idAnimal
        self._especie = especie

    @property
    def idAnimal(self) -> int:
        return self._idAnimal

    @idAnimal.setter
    def idAnimal(self, value: int):
        self._idAnimal = value
        
    @property
    def especie(self) -> str:
        return self._especie
    
    @especie.setter
    def especie(self, value: str):
        self._especie = value
        
    __str__ = lambda self: f"ID: {self.idAnimal}, Especie: {self.especie}"
        
class Herviboro(Animal):
    def __init__(self, idAnimal: int, especie: str, kgdia: int):
        super().__init__(idAnimal, especie)
        self._kgdia = kgdia

    @property
    def kgdia(self) -> int:
        return self._kgdia

    @kgdia.setter
    def kgdia(self, value: int):
        self._kgdia = value
        
    __str__ = lambda self: f"{super().__str__()}, Kg/día: {self.kgdia}"
        
class Carnivoro(Animal):
    def __init__(self, idAnimal: int, especie: str, peligroso: bool):
        
        super().__init__(idAnimal, especie)
        self._peligroso = peligroso

    @property
    def peligroso(self) -> bool:
        return self._peligroso

    @peligroso.setter
    def peligroso(self, value: bool):
        self._peligroso = value

    __str__ = lambda self: f"{super().__str__()}, Peligroso: {self.peligroso}"