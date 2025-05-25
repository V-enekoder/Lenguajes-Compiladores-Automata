package Java;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AutomataListaEnlazada {
    Nodo cabeza;
    Nodo cola;
    int cantidadNodos;

    public AutomataListaEnlazada() {
        this.cabeza = null;
        this.cola = null;
        this.cantidadNodos = 0;
    }

    public void construirLista(int n, int k) {
        if (n <= 0) {
            System.out.println("El número de nodos (n) debe ser mayor que 0.");
            return;
        }

        // 1. Crear el primer nodo
        // Partida del primer nodo: SHA256 del día y hora de la creación
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        String fechaHoraActualStr = ahora.format(formatter);

        String partidaPrimerNodo = Nodo.generarSHA256(fechaHoraActualStr);

        Nodo primerNodo = new Nodo(partidaPrimerNodo, k);
        this.cabeza = primerNodo;
        this.cola = primerNodo;
        this.cantidadNodos = 1;

        // 2. Crear los nodos subsiguientes
        Nodo nodoAnterior = primerNodo;
        for (int i = 1; i < n; i++) { // n-1 nodos restantes
            // Partida para los próximos nodos es la copia del valor firma digital del nodo
            // que le precede.
            String partidaActual = nodoAnterior.firmaDigital;
            Nodo nuevoNodo = new Nodo(partidaActual, k);

            // Enlazar el nuevo nodo
            this.cola.siguiente = nuevoNodo;
            this.cola = nuevoNodo; // Actualizar la cola

            nodoAnterior = nuevoNodo; // Para la siguiente iteración
            this.cantidadNodos++;
        }
    }

    public void mostrarLista(int limiteNodosMostrar) {
        System.out.println("\n--- Mostrando Autómata (Lista de " + cantidadNodos + " nodos) ---");
        Nodo actual = cabeza;
        int contador = 0;
        while (actual != null) {
            if (contador < limiteNodosMostrar) {
                System.out.println("Nodo " + (contador + 1) + ":");
                System.out.print(actual.toString()); // toString ya añade un salto de línea al final
            } else if (contador == limiteNodosMostrar) {
                System.out.println("... (y " + (cantidadNodos - limiteNodosMostrar) + " nodos más)");
                break;
            }
            actual = actual.siguiente;
            contador++;
        }
        if (cantidadNodos == 0) {
            System.out.println("La lista está vacía.");
        }
    }
}