class Empleado:

    def __init__(self, nombre, salario_base):
        self.__nombre = nombre
        self.__salario_base = salario_base

    @property
    def nombre(self):
        return self.__nombre

    @nombre.setter
    def nombre(self, valor):
        self.__nombre = valor

    @property
    def salario_base(self):
        return self.__salario_base

    @salario_base.setter
    def salario_base(self, valor):
        self.__salario_base = valor

    def calcular_salario(self, irpf):
        return self.salario_base * (100 - irpf) / 100

    def __str__(self):
        return f"Nombre: {self.nombre} Salario base: {self.salario_base}"

class Vendedor(Empleado):

    def __init__(self, nombre, lista_ventas):
        super().__init__(nombre, salario):
        self.__ventas_realizadas = lista_ventas

        @property
        def ventas_realizadas(self):
            return self.__ventas_realizadas[:]

        def calcular_salario(self):
            return self.salario_base + 0.1* sum(self.ventas_realizadas)
        
