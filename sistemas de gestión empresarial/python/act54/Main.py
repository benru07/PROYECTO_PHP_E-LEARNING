from Mascota import Mascota, Perro, Gato
from Clinica import Clinica


def main() -> None:
    print("Crear clínica con mascotas")
    perro = Perro(1, 12, "Labrador")
    gato = Gato(2, 5, 25)
    clinica = Clinica([perro, gato])
    print(f"Mascotas iniciales: {len(clinica.mascotas)}")

    print("\nCaso de uso: dar de alta una mascota")
    nuevo_perro = Perro(3, 8, "Beagle")
    print(clinica.altaMascota(nuevo_perro))

    print("\nCaso de uso: rechazar una mascota repetida")
    print(clinica.altaMascota(nuevo_perro))

    print("\nCaso de uso: rechazar una mascota que no es perro ni gato")
    mascota_generica = Mascota(4, 3)
    print(clinica.altaMascota(mascota_generica))

    print("\nCaso de uso: listar perros")
    perros = clinica.listaPerros()
    print(perros if perros else "No hay perros registrados")

    print("Caso de uso: consultar el peso máximo de los gatos")
    print(f"Peso máximo: {clinica.pesoMaximoGatos()} kg")

    print("\nCaso de uso: dar de baja una mascota existente")
    print(clinica.bajaMascota(1))

    print("\nCaso de uso: dar de baja una mascota inexistente")
    print(clinica.bajaMascota(99))


if __name__ == "__main__":
    main()
