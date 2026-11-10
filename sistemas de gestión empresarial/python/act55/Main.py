from act55.Animal import Herviboro, Carnivoro, Animal
from act55.Zoologico import Zoologico

h1 = Herviboro(1, "Vaca", 10)
h2 = Herviboro(2, "Caballo", 15)
c1 = Carnivoro(3, "Tigre", True)
c2 = Carnivoro(4, "Camaleon", False)

zoo = Zoologico([h1, h2, c1, c2])

a1 = Animal(5, "Oveja")
h3 = Herviboro(6, "Cabra", 5)
c3 = Carnivoro(7, "León", True)

print(zoo.altaAnimal(a1))
print(zoo.altaAnimal(h3))
print(zoo.altaAnimal(c3))
print(zoo.bajaAnimal(2))
print(zoo.numKilosPasto())
print(zoo.listadoPeligrosos())
