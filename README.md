
## Requisitos Previos y Configuración

Para ejecutar los ejemplos en este repositorio, necesitarás tener instalados los entornos de ejecución y herramientas correspondientes para cada lenguaje.

### 1. Python

*   **Instalación:**
    *   Se recomienda Python 3.x. Puedes descargarlo desde [python.org](https://www.python.org/downloads/).
    *   Asegúrate de que `python` y `pip` (el gestor de paquetes de Python) estén en tu PATH.
### 2. Go (Golang)

*   **Instalación:**
    *   Se recomienda Go 1.18 o superior. Puedes descargarlo desde [golang.org/dl/](https://go.dev/dl/).
    *   Asegúrate de que el comando `go` esté en tu PATH.
*   **Dependencias (Go Modules):**
    *   Los proyectos de Go en este repositorio utilizan Go Modules para la gestión de dependencias.
    *   Antes de compilar o ejecutar un proyecto Go:
        1.  Navega al directorio raíz del proyecto Go específico (la carpeta que contiene el archivo `go.mod`).
        2.  Ejecuta el siguiente comando para descargar las dependencias necesarias y limpiar el archivo `go.mod`:
            ```bash
            go mod tidy
            ```
            Este comando se asegura de que el archivo `go.mod` coincida con el código fuente del módulo, añadiendo las dependencias que faltan y eliminando las que no se usan.

### 3. Java

*   **Instalación:**
    *   Necesitarás un **JDK (Java Development Kit)**, versión 8 o superior (por ejemplo, OpenJDK, Oracle JDK).
        *   Puedes encontrar OpenJDK en sitios como [Adoptium (anteriormente AdoptOpenJDK)](https://adoptium.net/).
    *   Asegúrate de que las variables de entorno `JAVA_HOME` estén configuradas correctamente y que `java` y `javac` estén en tu PATH.

## Cómo Ejecutar los Ejemplos

Las instrucciones específicas para ejecutar cada ejemplo o proyecto pueden encontrarse dentro de los archivos `README.md` de sus respectivas carpetas (si existen) o directamente en los comentarios del código.

A continuación, se presentan formas generales de ejecutar código para cada lenguaje:

### Python

1.  Navega al directorio del script o proyecto Python:
    ```bash
    cd Ejercicio 2/Python
    ```
2.  Ejecuta el script:
    ```bash
    python automata.py
    ```

### Go

1.  Navega al directorio del proyecto Go:
    ```bash
    cd Ejercicio 2/Go
    ```
2.  Asegúrate de haber ejecutado `go mod tidy` para instalar dependencias.
3.  Para ejecutar con
    ```bash
    go run automata.go
    ```



### Java

*   **Compilación y Ejecución (ejemplo para `AutomataJava.java` en el paquete `Java`):**

    1.  Abre una terminal o línea de comandos.
    2.  Navega al directorio del proyecto Go:
    ```bash
    cd Ejercicio 2/Java
    ```
    3.  **Compila los archivos Java:**
        Para compilar todos los archivos `.java` dentro de la carpeta `Java`:
        ```bash
        javac Java/*.java
        ```
        Esto generará los archivos `.class` correspondientes dentro de la carpeta `Java/`.

    4.  **Ejecuta la clase principal:**
        Desde el mismo directorio donde ejecutaste `javac` (es decir, desde `TuProyecto/`), ejecuta la clase usando su nombre completamente calificado:
        ```bash
        java Java.AutomataJava
        ```
