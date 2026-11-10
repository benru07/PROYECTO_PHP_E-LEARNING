from act55.Animal import Herviboro, Carnivoro, Animal

class Zoologico:
    def __init__(self, listaAnimales: list):
        self.listaAnimales = listaAnimales
        
        
    @property
    def listaAnimales(self) -> list:
        return self._listaAnimales
    
    @listaAnimales.setter
    def listaAnimales(self, value: list):
        if not isinstance(value, list):
            print("La lista de animales debe ser una lista")
        self._listaAnimales = value
        
        
    def altaAnimal(self, animal: Animal) -> str:
        texto = ""
        if not isinstance(animal, (Herviboro, Carnivoro)):
            texto = "el animal tiene que ser herviboro o carnivoro"
        else:
            if animal in self.listaAnimales:
                texto = "animal repetido"
            else:
                self.listaAnimales.append(animal)
                texto = "Alta OK"
        return texto

    def bajaAnimal(self, idAnimal: str) -> str:
        texto = ""
        for animal in self.listaAnimales:
            if animal.idAnimal == idAnimal:
                self.listaAnimales.remove(animal)
                texto = "Animal dado de baja"
        if not texto:
            texto = "Animal no encontrado"
        return texto

    def numKilosPasto(self) -> int:
        totalKilos = 0
        for animal in self.listaAnimales:
            if isinstance(animal, Herviboro):
                totalKilos += animal.kgdia
        return totalKilos
    
    def listadoPeligrosos(self) -> str:
        texto = ""
        for animal in self.listaAnimales:
            if isinstance(animal, Carnivoro) and animal.peligroso:
                texto += f"{animal}\n"
        return texto