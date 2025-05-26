package main

import (
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"math/big"
	"math/rand"
	"strconv"
	"strings"
	"time"
)

type Nodo struct {
	partida      string
	cuerpo       []int
	firmaDigital string
	siguiente    *Nodo
}

func NewNodo(partidaVal string, k int) *Nodo {
	cuerpo := make([]int, k)
	for i := 0; i < k; i++ {
		cuerpo[i] = rand.Intn(100000) + 1
	}
	cuerpoStrElements := make([]string, k)
	for i, val := range cuerpo {
		cuerpoStrElements[i] = strconv.Itoa(val)
	}
	elementosCuerpoStr := strings.Join(cuerpoStrElements, " ")
	contenidoParaHash := partidaVal + elementosCuerpoStr
	hash := sha256.New()
	hash.Write([]byte(contenidoParaHash))
	firma := hex.EncodeToString(hash.Sum(nil))
	return &Nodo{
		partida:      partidaVal,
		cuerpo:       cuerpo,
		firmaDigital: firma,
		siguiente:    nil,
	}
}
func (n *Nodo) String() string {
	var cuerpoPreviewStr string
	if len(n.cuerpo) > 5 {
		preview := make([]string, 5)
		for i := 0; i < 5; i++ {
			preview[i] = strconv.Itoa(n.cuerpo[i])
		}
		cuerpoPreviewStr = fmt.Sprintf("[%s]... (total %d elems)", strings.Join(preview, ", "), len(n.cuerpo))
	} else {
		preview := make([]string, len(n.cuerpo))
		for i, val := range n.cuerpo {
			preview[i] = strconv.Itoa(val)
		}
		cuerpoPreviewStr = fmt.Sprintf("[%s]", strings.Join(preview, ", "))
	}
	return fmt.Sprintf("  Partida: %s\n  Cuerpo: %s\n  Firma Digital: %s\n", n.partida, cuerpoPreviewStr, n.firmaDigital)
}
func generarSHA256(input string) string {
	hash := sha256.New()
	hash.Write([]byte(input))
	return hex.EncodeToString(hash.Sum(nil))
}

type AutomataListaEnlazada struct {
	cabeza        *Nodo
	cola          *Nodo
	cantidadNodos int
}

func NewAutomataListaEnlazada() *AutomataListaEnlazada {
	return &AutomataListaEnlazada{
		cabeza:        nil,
		cola:          nil,
		cantidadNodos: 0,
	}
}
func (al *AutomataListaEnlazada) ConstruirLista(n, k int) {
	if n <= 0 {
		return
	}
	fechaHoraActualStr := time.Now().Format("2006-01-02 15:04:05.000000")
	partidaPrimerNodo := generarSHA256(fechaHoraActualStr)
	primerNodo := NewNodo(partidaPrimerNodo, k)
	al.cabeza = primerNodo
	al.cola = primerNodo
	al.cantidadNodos = 1
	nodoAnterior := primerNodo
	for i := 1; i < n; i++ {
		partidaActual := nodoAnterior.firmaDigital
		nuevoNodo := NewNodo(partidaActual, k)
		al.cola.siguiente = nuevoNodo
		al.cola = nuevoNodo
		nodoAnterior = nuevoNodo
		al.cantidadNodos++
	}
}
func (al *AutomataListaEnlazada) MostrarLista(limiteNodosMostrar int) {
	fmt.Printf("\n--- Mostrando Autómata (Lista de %d nodos) ---\n", al.cantidadNodos)
	actual := al.cabeza
	contador := 0
	for actual != nil {
		if contador < limiteNodosMostrar {
			fmt.Printf("Nodo %d:\n%s", contador+1, actual.String())
		} else if contador == limiteNodosMostrar {
			fmt.Printf("... (y %d nodos más)\n", al.cantidadNodos-limiteNodosMostrar)
			break
		}
		actual = actual.siguiente
		contador++
	}
	if al.cantidadNodos == 0 {
		fmt.Println("La lista está vacía.")
	}
}

// EscenarioResultado almacena los datos de un escenario y su resultado
type EscenarioResultado struct {
	ID             string
	N              int
	K              int
	TiempoMs       *big.Float // Cambiado a puntero a big.Float
	MostrarDetalle bool
}

// Constante para el divisor (1 millón para convertir ns a ms)
var divisorNsToMs = big.NewFloat(1e6)

func ejecutarEscenario(nVal, kVal int, mostrarDetalle bool) *big.Float { // Devolver *big.Float
	startTime := time.Now()

	automata := NewAutomataListaEnlazada()
	automata.ConstruirLista(nVal, kVal)

	duration := time.Since(startTime)

	// Crear un big.Float a partir de los nanosegundos (int64)
	nanosBigFloat := new(big.Float).SetInt64(duration.Nanoseconds())

	// Dividir por 1,000,000 para obtener milisegundos
	// Creamos un nuevo big.Float para el resultado de la división
	tiempoEjecucionMs := new(big.Float).Quo(nanosBigFloat, divisorNsToMs)

	// Opcional: Establecer la precisión del resultado si es necesario
	// tiempoEjecucionMs.SetPrec(128) // Ejemplo: 128 bits de precisión para la mantisa

	if mostrarDetalle {
		automata.MostrarLista(5)
	}

	return tiempoEjecucionMs
}

func calentamiento() {
	scenariosBase := []EscenarioResultado{
		{ID: "a", N: 3, K: 4, MostrarDetalle: true},
		{ID: "b", N: 10, K: 200, MostrarDetalle: false},
		{ID: "c", N: 200, K: 10, MostrarDetalle: false},
	}

	for _, escBase := range scenariosBase {
		fmt.Printf("\n--- Procesando Escenario: ID=%s, n=%d, k=%d ---\n", escBase.ID, escBase.N, escBase.K)

		tiemposAcumulados := new(big.Float)

		for i := 0; i < 10; i++ {
			mostrarDetalleActual := escBase.MostrarDetalle && (escBase.ID == "a" && i == 0)
			tiempoIteracion := ejecutarEscenario(escBase.N, escBase.K, mostrarDetalleActual)
			tiemposAcumulados.Add(tiemposAcumulados, tiempoIteracion)
		}
	}

}

func main() {
	rand.Seed(time.Now().UnixNano())

	calentamiento()

	escenariosBase := []EscenarioResultado{
		{ID: "a", N: 3, K: 4, MostrarDetalle: true},
		{ID: "b", N: 10, K: 200, MostrarDetalle: false},
		{ID: "c", N: 200, K: 10, MostrarDetalle: false},
	}

	numIteracionesPorEscenario := 100
	resultadosFinales := []EscenarioResultado{}

	fmt.Println("=== INICIO DE PRUEBAS GO (con big.Float) ===")
	for _, escBase := range escenariosBase {
		fmt.Printf("\n--- Procesando Escenario: ID=%s, n=%d, k=%d ---\n", escBase.ID, escBase.N, escBase.K)

		tiemposAcumulados := new(big.Float)

		fmt.Printf("Ejecutando %d iteraciones de medición...\n", numIteracionesPorEscenario)
		for i := 0; i < numIteracionesPorEscenario; i++ {
			mostrarDetalleActual := escBase.MostrarDetalle && (escBase.ID == "a" && i == 0)
			tiempoIteracion := ejecutarEscenario(escBase.N, escBase.K, mostrarDetalleActual)
			tiemposAcumulados.Add(tiemposAcumulados, tiempoIteracion)
		}

		numIterBigFloat := new(big.Float).SetInt64(int64(numIteracionesPorEscenario))
		tiempoPromedioMs := new(big.Float).Quo(tiemposAcumulados, numIterBigFloat)

		fmt.Printf("Tiempo promedio para n=%d, k=%d: %s ms\n", escBase.N, escBase.K, tiempoPromedioMs.Text('f', 6))

		resultadosFinales = append(resultadosFinales, EscenarioResultado{
			ID:       escBase.ID,
			N:        escBase.N,
			K:        escBase.K,
			TiempoMs: tiempoPromedioMs,
		})
	}
	fmt.Println("=== FIN DE PRUEBAS GO (con big.Float) ===")

	fmt.Println("\n---           Tiempos de Ejecución           ---")
	fmt.Println("--------------------------------------------------")
	fmt.Println("| Escenario ID |   n   |    k   | Tiempo(ms) |")
	fmt.Println("--------------------------------------------------")
	for _, res := range resultadosFinales {
		fmt.Printf("| %-12s | %-5d | %-6d | %-15s |\n",
			res.ID, res.N, res.K, res.TiempoMs.Text('e', 4)) // 'E' para notación científica
	}
	fmt.Println("---------------------------------------------------")
}
