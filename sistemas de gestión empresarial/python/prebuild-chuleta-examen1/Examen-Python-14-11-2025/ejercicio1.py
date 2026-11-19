class Empleado:
    
    def __init__(self, nombre, salario_base):
        self.__nombre = nombre
        self.__salario_base = salario_base
        
    # ----- nombre -----
    @property
    def nombre(self):
        return self.__nombre

    @nombre.setter
    def nombre(self, nombre):
        self.__nombre = nombre

    # ----- salario_base -----
    @property
    def salario_base(self):
        return self.__salario_base

    @salario_base.setter
    def salario_base(self, salario_base):
        self.__salario_base = salario_base
        
    def calcular_salario(self, irpf):
        return self.salario_base - (self.salario_base * irpf / 100)
    
    def __str__(self):
        return f"Nombre: {self.nombre} - Salario base: {self.salario_base}"


class Vendedor (Empleado):
    
    def __init__(self, nombre, salario_base, ventas_realiazdas):
        super().__init__(nombre, salario_base)
        self.__ventas_realiazdas = list(ventas_realiazdas)
        
    @property
    def ventas_realizadas(self):
            return list(self.__ventas_realizadas)
        
    def calcular_salario(self, irpf = 0):
        return super().calcular_salario(irpf) + (sum(self.ventas_realizadas) * 10/ 100)
    
    def agregar_venta(self, importe):
        self.ventas_realizadas.append(importe)
        
    def __str__(self):
        return super().__str__() + f"- Ventas: {self.ventas_realizadas} - Salario total: {self.calcular_salario()}"
    

#Main

#1
empleado1 = Empleado("pepe", 1300)
empleado2 = Empleado("Rana", 4000)

vendedor1 = Vendedor("María Unfavor", 1200, [100, 150, 80])
vendedor2 = Vendedor("Elena Nito", 1300, [200, 50])

#2
print(empleado1)
print(empleado2)
print(vendedor1)
print(vendedor2)

#3
empleado1.nombre = "Lola Mento Pérez"
empleado2.salario_base = 2000
vendedor2.salario_base = 1400
vendedor1.nombre = "Elena Nito Gómez"

#4
vendedor1.agregar_venta(300)
vendedor1.agregar_venta(120)

#5
print(empleado1)
print(empleado2)
print(vendedor1)
print(vendedor2)