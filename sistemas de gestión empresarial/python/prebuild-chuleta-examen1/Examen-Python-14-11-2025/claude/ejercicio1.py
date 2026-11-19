"""
Ejercicio 1 - Programación Orientada a Objetos
Sistema de gestión de empleados: clase base Empleado y clase derivada Vendedor.
"""


class Empleado:
    """Clase base que representa a un empleado de la empresa."""

    def __init__(self, nombre, salario_base):
        # Se asigna a través de las propiedades para reutilizar las validaciones
        self.nombre = nombre
        self.salario_base = salario_base

    # ----- nombre -----
    @property
    def nombre(self):
        return self.__nombre

    @nombre.setter
    def nombre(self, nombre):
        if not isinstance(nombre, str) or not nombre.strip():
            raise ValueError("El nombre debe ser un texto no vacío")
        self.__nombre = nombre.strip()

    # ----- salario_base -----
    @property
    def salario_base(self):
        return self.__salario_base

    @salario_base.setter
    def salario_base(self, salario_base):
        if salario_base < 0:
            raise ValueError("El salario base no puede ser negativo")
        self.__salario_base = salario_base

    # ----- métodos -----
    def calcular_salario(self, irpf):
        """Devuelve el salario base menos el porcentaje de IRPF (0-100).

        Ejemplo: salario_base = 100, irpf = 15 -> 100 - 15% de 100 = 85
        """
        if not 0 <= irpf <= 100:
            raise ValueError("El IRPF debe estar entre 0 y 100")
        return self.salario_base - self.salario_base * irpf / 100

    def __str__(self):
        return f"Nombre: {self.nombre} - Salario base: {self.salario_base}"


class Vendedor(Empleado):
    """Empleado que además cobra una comisión del 10 % de sus ventas."""

    COMISION = 0.10

    def __init__(self, nombre, salario_base, ventas_realizadas):
        super().__init__(nombre, salario_base)
        # Se guarda una copia para que la lista original no pueda alterar el objeto
        self.__ventas_realizadas = list(ventas_realizadas)

    # Propiedad de solo lectura (sin setter): devuelve una copia de la lista
    @property
    def ventas_realizadas(self):
        return list(self.__ventas_realizadas)

    def agregar_venta(self, importe):
        """Añade una nueva venta (en €) a la lista de ventas."""
        if importe <= 0:
            raise ValueError("El importe de la venta debe ser positivo")
        self.__ventas_realizadas.append(importe)

    def calcular_salario(self, irpf=0):
        """Salario base + 10 % del total de ventas.

        Por compatibilidad con Empleado admite un irpf opcional (0 por defecto,
        con lo que devuelve exactamente: salario base + 10 % de las ventas).
        """
        if not 0 <= irpf <= 100:
            raise ValueError("El IRPF debe estar entre 0 y 100")
        bruto = self.salario_base + self.COMISION * sum(self.__ventas_realizadas)
        return bruto - bruto * irpf / 100

    def __str__(self):
        return (
            f"Nombre: {self.nombre} - Salario base: {self.salario_base}"
            f" - Ventas: {self.__ventas_realizadas}"
            f" - Salario total: {round(self.calcular_salario(), 2)}"
        )


def main():
    # 1. Dos objetos de cada clase
    emp1 = Empleado("Lola Mento", 1500)
    emp2 = Empleado("Aitor Tilla", 1800)
    ven1 = Vendedor("María Unfavor", 1200, [100, 150, 80])
    ven2 = Vendedor("Elena Nito", 1300, [200, 50])

    # 2. Mostrar la información
    print("=== INFORMACIÓN INICIAL ===")
    for persona in (emp1, emp2, ven1, ven2):
        print(persona)

    print("\nSalario neto de", emp1.nombre, "con IRPF 15%:", emp1.calcular_salario(15))

    # 3. Modificar atributos con los setters
    emp1.nombre = "Lola Mento Pérez"
    emp2.salario_base = 2000
    ven2.salario_base = 1400
    ven2.nombre = "Elena Nito Gómez"

    # 4. Agregar dos ventas a uno de los vendedores
    ven1.agregar_venta(300)
    ven1.agregar_venta(120)

    # 5. Mostrar la información actualizada
    print("\n=== INFORMACIÓN ACTUALIZADA ===")
    for persona in (emp1, emp2, ven1, ven2):
        print(persona)


if __name__ == "__main__":
    main()
