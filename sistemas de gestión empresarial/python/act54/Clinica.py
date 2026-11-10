from Mascota import Mascota, Perro, Gato

class Clinica:
    def __init__(self, mascotas: list):
        self.mascotas = mascotas
        
    @property
    def mascotas(self) -> list:
        return self._mascotas
    @mascotas.setter
    def mascotas(self, lista: list):
        if not isinstance(lista, list):
            print("La lista de mascotas debe ser una lista")
        self._mascotas = lista
        
    def altaMascota(self, oMascota: Mascota) -> str:
        texto = ""
        if not isinstance(oMascota, (Perro, Gato)):
            texto = "La mascota tiene que ser un perro o un gato"
        else:
            if oMascota in self.mascotas:
                texto = "Mascota repetida"
            else:
                self.mascotas.append(oMascota)
                texto = "Alta OK"
        return texto
    
    def bajaMascota(self, idMascota: int) -> str:
        texto = ""
        for mascota in self.mascotas:
            if mascota.idMascota == idMascota:
                self.mascotas.remove(mascota)
                texto = "Baja OK"
                break
        else:
            texto = "Mascota no encontrada"
        return texto
    
    def listaPerros(self):
        texto = ""
        for mascota in self.mascotas:
            if isinstance(mascota, Perro):
                texto += f"{mascota}\n"
        return texto
    
    def pesoMaximoGatos(self):
        pesoMax = 0
        for mascota in self.mascotas:
            if isinstance(mascota, Gato):
                if mascota.peso > pesoMax:
                    pesoMax = mascota.peso
        return pesoMax