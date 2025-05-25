package Java;

import java.util.ArrayList;
import java.util.List;

public class AutomataJava {

    // Clase interna para representar un escenario y sus resultados
    static class EscenarioResultado {
        String id;
        int n;
        int k;
        double tiempoMs;
        boolean mostrarDetalle;

        public EscenarioResultado(String id, int n, int k, boolean mostrarDetalle) {
            this.id = id;
            this.n = n;
            this.k = k;
            this.mostrarDetalle = mostrarDetalle;
        }
    }

    public static double ejecutarEscenario(int nVal, int kVal, boolean mostrarDetalle, int cantidadDeEjecuciones) {
        if (cantidadDeEjecuciones <= 0) {
            throw new IllegalArgumentException("La cantidad de ejecuciones debe ser un entero positivo.");
        }

        long tiempoTotalNanoSegundos = 0;
        AutomataListaEnlazada automataDeLaUltimaEjecucion = null; // Para mostrar detalle de la última ejecución si es
                                                                  // necesario

        for (int i = 0; i < cantidadDeEjecuciones; i++) {
            long startTime = System.nanoTime(); // Usar nanoTime para mayor precisión

            AutomataListaEnlazada automataActual = new AutomataListaEnlazada();
            automataActual.construirLista(nVal, kVal);

            long endTime = System.nanoTime();
            tiempoTotalNanoSegundos += (endTime - startTime);

            if (i == cantidadDeEjecuciones - 1) { // Si es la última ejecución
                automataDeLaUltimaEjecucion = automataActual; // Guardar para posible detalle
                System.out.println("Automata de la última ejecución:");
            }
        }

        double tiempoPromedioMs = (tiempoTotalNanoSegundos / (double) cantidadDeEjecuciones) / 1_000_000.0;
        if (mostrarDetalle && automataDeLaUltimaEjecucion != null) {
            automataDeLaUltimaEjecucion.mostrarLista(5);
        }

        return tiempoPromedioMs;
    }

    public static void calentamiento() {
        for (int i = 0; i < 1000; i++) {
            AutomataListaEnlazada automata = new AutomataListaEnlazada();
            automata.construirLista(100, 10);
        }
    }

    public static void main(String[] args) {
        List<EscenarioResultado> escenarios = new ArrayList<>();
        escenarios.add(new EscenarioResultado("a", 3, 4, true));
        escenarios.add(new EscenarioResultado("b", 10, 200, false));
        escenarios.add(new EscenarioResultado("c", 200, 10, false));

        calentamiento();

        System.out.println("=== INICIO DE PRUEBAS JAVA ===");
        for (EscenarioResultado esc : escenarios) {
            esc.tiempoMs = ejecutarEscenario(esc.n, esc.k, esc.mostrarDetalle, 100);
        }
        System.out.println("=== FIN DE PRUEBAS JAVA ===");

        System.out.println("\n---           Tiempos de Ejecución             ---");
        System.out.println("----------------------------------------------------");
        System.out.println("| Escenario ID |   n   |    k   | Tiempo(ms) |");
        System.out.println("----------------------------------------------------");
        for (EscenarioResultado res : escenarios) {
            System.out.printf("| %-12s | %-5d | %-6d | %-18.4f |%n",
                    res.id, res.n, res.k, res.tiempoMs);
        }
        System.out.println("-----------------------------------------------------");
    }
}