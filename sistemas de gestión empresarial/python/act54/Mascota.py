class Mascota:
    def __init__(self, idMascota: int, peso: int):
        self._idMascota = idMascota
        self._peso = peso
        
    @property
    def idMascota(self) -> int:
        return self._idMascota
    
    @idMascota.setter
    def idMascota(self, value: int):
        self._idMascota = value
        
    @property
    def peso(self) -> int:
        return self._peso
        
    @peso.setter
    def peso(self, value: int):
        self._peso = value
        
    def __str__(self) -> str:
        return f"ID: {self.idMascota}, Peso: {self.peso}"
        
class Perro(Mascota):
    def __init__(self, idMascota: int, peso: int, raza: str):
        super().__init__(idMascota, peso)
        self._raza = raza

    @property
    def raza(self) -> str:
        return self._raza

    @raza.setter
    def raza(self, value: str):
        self._raza = value
        
    def __str__(self) -> str:
        return f"{super().__str__()}, Raza: {self.raza}"
        
class Gato(Mascota):
    def __init__(self, idMascota: int, peso: int, altura: int):
        super().__init__(idMascota, peso)
        self._altura = altura

    @property
    def altura(self) -> int:
        return self._altura

    @altura.setter
    def altura(self, value: int):
        self._altura = value
        
    def __str__(self) -> str:
        return f"{super().__str__()}, Altura: {self.altura}"