# Micro Adversarial Search Service

Este proyecto es un microservicio basado en **Spring Boot** que proporciona un motor de inteligencia artificial para juegos de tablero mediante algoritmos de búsqueda adversarial (Minimax, Negamax).

El servicio expone una API REST que acepta el estado actual de un juego y devuelve el mejor movimiento calculado para el siguiente turno.

## 🎮 Juegos Soportados

Actualmente el servicio soporta los siguientes juegos:

- **Tres en Raya (Tic-Tac-Toe)**: Tablero 3x3.
- **Gatos y el Ratón (Cats vs Mouse)**: Juego asimétrico en tablero 8x8.
- **Damas (Checkers)**: Tablero 8x8.

## 🚀 Tecnologías

- **Java 21**: Lenguaje de programación.
- **Spring Boot 3.5.5**: Framework para la creación del microservicio.
- **Maven**: Gestión de dependencias y construcción.
- **H2 Database**: Base de datos en memoria (para persistencia de logs/requests si fuera necesario).
- **Lombok**: Reducción de código boilerplate.
- **Engine Lib**: Librería personalizada (`es.jastxz:engine-lib`) que contiene la lógica core de los algoritmos (Minimax/Negamax).

## 📋 Requisitos

- JDK 21 instalado.
- Maven instalado.
- **Importante**: Necesitas tener instalada la librería `engine-lib` en tu repositorio local Maven, ya que es una dependencia interna del proyecto.

## 🛠️ Instalación y Ejecución

1.  **Clonar el repositorio:**

    ```bash
    git clone https://github.com/tu-usuario/micro-adversarial-search.git
    cd micro-adversarial-search
    ```

2.  **Construir el proyecto:**

    ```bash
    ./mvnw clean install
    ```

3.  **Ejecutar la aplicación:**

    ```bash
    ./mvnw spring-boot:run
    ```

    La aplicación se iniciará por defecto en `http://localhost:8080`.

## 🔌 API Endpoints

Todos los endpoints están prefijados con `/v0`. Aceptan peticiones POST con un cuerpo JSON (`MundoRequest`).

### Estructura General de la Petición (`MundoRequest`)

```json
{
  "data": [
    [0, 0, 0],
    [0, 0, 0],
    [0, 0, 0]
  ],
  "posicionFila": 0,
  "posicionColumna": 0,
  "dificultad": 1,
  "profundidad": 9,
  "marca": 1,
  "turno": 1
}
```

- **data**: Matriz de enteros que representa el tablero. (3x3 para Tres en Raya, 8x8 para Gatos/Damas).
- **posicionFila / posicionColumna**: Fila y columna de la última jugada o posición relevante.
- **dificultad**: Nivel de dificultad (generalmente 1-4).
- **profundidad**: Profundidad de búsqueda del algoritmo.
- **marca**: Identificador del jugador (e.g., 1 para X, 2 para O, o IDs específicos en Gatos).
- **turno**: Turno actual (1 o 2).

### 1. Tres en Raya

**Endpoint**: `POST /v0/tresenraya`

- **Tablero**: 3x3
- **Profundidad Válida**: 1 o 9.

### 2. Gatos y Ratón

**Endpoint**: `POST /v0/gatos`

- **Tablero**: 8x8
- **Profundidad Válida**: 1, 2, 4, 6, 8.
- **Marca**: IDs específicos de Gatos o Ratón definidos en `FuncionesGato`.

### 3. Damas

**Endpoint**: `POST /v0/damas`

- **Tablero**: 8x8
- **Profundidad Válida**: 1 a 5.

## 📦 Respuesta

El servicio devuelve un objeto con el estado del tablero resultante tras el mejor movimiento calculado:

```json
{
  "data": [
    [1, 0, 0],
    [0, 2, 0],
    [0, 0, 1]
  ]
}
```
