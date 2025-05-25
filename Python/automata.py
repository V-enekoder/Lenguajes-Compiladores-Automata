import hashlib
import random
from datetime import datetime
import time

class Nodo:
    def __init__(self, partida_val, k):
        self.partida = partida_val
        self.cuerpo = [random.randint(1, 100000) for _ in range(k)]
        self.firma_digital = self._calcular_firma_digital()
        self.siguiente = None

    def _calcular_firma_digital(self):
        """
        Calcula el SHA256 de (partida concatenado con los elementos del cuerpo separados por un espacio).
        """
        # Convertir los elementos del cuerpo a string y unirlos con espacios
        elementos_cuerpo_str = " ".join(map(str, self.cuerpo))
        # Concatenar partida con la cadena de elementos del cuerpo
        # Aseguramos que partida sea string, aunque ya debería serlo (hash previo o datetime string)
        contenido_para_hash = str(self.partida) + elementos_cuerpo_str
        
        # Calcular SHA256
        sha256 = hashlib.sha256()
        sha256.update(contenido_para_hash.encode('utf-8')) # Es importante codificar a bytes
        return sha256.hexdigest()

    def __str__(self):
        # Mostrar solo los primeros 5 elementos del cuerpo si es muy largo, para legibilidad
        cuerpo_preview = self.cuerpo[:5]
        if len(self.cuerpo) > 5:
            cuerpo_preview_str = f"{cuerpo_preview}... (total {len(self.cuerpo)} elems)"
        else:
            cuerpo_preview_str = f"{self.cuerpo}"

        return (f"  Partida: {self.partida}\n"
                f"  Cuerpo: {cuerpo_preview_str}\n"
                f"  Firma Digital: {self.firma_digital}\n")

class AutomataListaEnlazada:
    def __init__(self):
        self.cabeza = None
        self.cola = None # Para inserción eficiente al final
        self.cantidad_nodos = 0

    def construir_lista(self, n, k):
        """
        Construye la lista enlazada con n nodos y k elementos en el cuerpo de cada nodo.
        """
        if n <= 0:
            print("El número de nodos (n) debe ser mayor que 0.")
            return

        # 1. Crear el primer nodo
        # Partida del primer nodo: SHA256 del día y hora de la creación
        fecha_hora_actual_str = datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")
        sha256_inicial = hashlib.sha256()
        sha256_inicial.update(fecha_hora_actual_str.encode('utf-8'))
        partida_primer_nodo = sha256_inicial.hexdigest()
        
        primer_nodo = Nodo(partida_primer_nodo, k)
        self.cabeza = primer_nodo
        self.cola = primer_nodo
        self.cantidad_nodos = 1

        # 2. Crear los nodos subsiguientes
        nodo_anterior = primer_nodo
        for _ in range(1, n): # n-1 nodos restantes
            # Partida para los próximos nodos es la copia del valor firma digital del nodo que le precede.
            partida_actual = nodo_anterior.firma_digital
            nuevo_nodo = Nodo(partida_actual, k)
            
            # Enlazar el nuevo nodo
            self.cola.siguiente = nuevo_nodo
            self.cola = nuevo_nodo # Actualizar la cola
            
            nodo_anterior = nuevo_nodo # Para la siguiente iteración
            self.cantidad_nodos += 1

    def mostrar_lista(self, limite_nodos_mostrar=5):
        print(f"\n--- Mostrando Autómata (Lista de {self.cantidad_nodos} nodos) ---")
        actual = self.cabeza
        contador = 0
        while actual:
            if contador < limite_nodos_mostrar:
                print(f"Nodo {contador + 1}:")
                print(actual)
            elif contador == limite_nodos_mostrar:
                print(f"... (y {self.cantidad_nodos - limite_nodos_mostrar} nodos más)")
                break
            actual = actual.siguiente
            contador += 1
        if self.cantidad_nodos == 0:
            print("La lista está vacía.")

# --- Función para ejecutar y medir ---
def ejecutar_escenario(n_val, k_val, cantidad_de_ejecuciones=100, mostrar_detalle=False):
    if cantidad_de_ejecuciones <= 0:
        raise ValueError("La cantidad de ejecuciones debe ser un entero positivo.")

    tiempo_total_segundos = 0
    # Guardaremos el autómata de la última ejecución por si se necesita mostrar detalle
    automata_de_la_ultima_ejecucion = None

    for i in range(cantidad_de_ejecuciones):
        start_time = time.perf_counter() # Usar perf_counter para mayor precisión

        # Es importante crear una nueva instancia del autómata en cada iteración
        # si el objetivo es medir la construcción desde cero cada vez.
        automata_actual = AutomataListaEnlazada()
        automata_actual.construir_lista(n=n_val, k=k_val)

        end_time = time.perf_counter()
        tiempo_total_segundos += (end_time - start_time)

        if i == cantidad_de_ejecuciones - 1: # Si es la última ejecución
            automata_de_la_ultima_ejecucion = automata_actual # Guardar para posible detalle

    tiempo_promedio_segundos = tiempo_total_segundos / cantidad_de_ejecuciones
    tiempo_promedio_ms = tiempo_promedio_segundos * 1000

    if mostrar_detalle and automata_de_la_ultima_ejecucion:
        # Muestra el detalle del autómata correspondiente a la última ejecución del bucle
        automata_de_la_ultima_ejecucion.mostrar_lista()

    return tiempo_promedio_ms

def calentamient0():
    escenarios = [
    {"n": 3, "k": 4, "mostrar": False},    # a)
    {"n": 10, "k": 200, "mostrar": False},  # b)
    {"n": 200, "k": 10, "mostrar": False},  # c)
]
    for i, esc in enumerate(escenarios):
        tiempo_ms = ejecutar_escenario(esc["n"], esc["k"], mostrar_detalle=esc.get("mostrar", False))

# --- Escenarios de Prueba ---
escenarios = [
    {"n": 3, "k": 4, "mostrar": False},    # a)
    {"n": 10, "k": 200, "mostrar": False},  # b)
    {"n": 200, "k": 10, "mostrar": False},  # c)
]
calentamient0() 
print("=== FIN DE CALENTAMIENTO ===")
resultados_python = []

print("=== INICIO DE PRUEBAS PYTHON ===")
for i, esc in enumerate(escenarios):
    tiempo_ms = ejecutar_escenario(esc["n"], esc["k"], mostrar_detalle=esc.get("mostrar", False))
    resultados_python.append({
        "escenario_id": chr(ord('a') + i) if i < 3 else f"extra{i-2}", # a, b, c, extra1, extra2
        "n": esc["n"],
        "k": esc["k"],
        "tiempo_ms": tiempo_ms
    })
print("=== FIN DE PRUEBAS PYTHON ===")

print("\n---------      Tiempos de Ejecución        ---------")
print("------------------------------------------------------")
print("| Escenario ID |   n   |    k   | Tiempo(ms) |")
print("------------------------------------------------------")
for res in resultados_python:
    print(f"| {res['escenario_id']:<12} | {res['n']:<5} | {res['k']:<6} | {res['tiempo_ms']:<18.4f} |")
print("-------------------------------------------------------")