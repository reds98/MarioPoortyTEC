# Mario Party TEC

Juego de tablero multijugador basado en Mario Party, desarrollado en Java con arquitectura cliente-servidor.

## Integrantes
- Sahid Rojas
- Johel Arias

## Estructura del Proyecto

```
MarioPartyTEC/
├── GameServer/        # Servidor del juego
├── GameClient/        # Cliente del juego
├── docs/             # Documentación
│   ├── DiagramaCliente.pdf     # Diagrama de clases del cliente
│   └── DiagramaServer.pdf      # Diagrama de clases del servidor
└── README.md         # Documentación principal
```

## Requisitos

- Java 11 o superior
- Apache NetBeans IDE
- Puerto 5000 disponible para comunicación

## Instalación y Ejecución con NetBeans

1. Abrir NetBeans IDE

2. Importar proyectos:
   - File -> Open Project
   - Seleccionar la carpeta GameServer
   - Repetir para GameClient

3. Ejecutar el servidor:
   - Seleccionar proyecto GameServer en el explorador de proyectos
   - Click derecho -> Run Project

4. Ejecutar el cliente:
   - Seleccionar proyecto GameClient en el explorador de proyectos
   - Click derecho -> Run Project
   - Se pueden ejecutar múltiples instancias del cliente para probar el juego

## Características

- Tablero dinámico con casillas aleatorias
- Múltiples mini-juegos:
  - Gato (Tres en raya)
  - Sopa de Letras
  - Memory Path
  - Super Bros Memory
  - Catch the Cat
  - Treasure Hunt
  - Guess Who
  - Collect Coins
  - Mario Cards

- Casillas especiales:
  - Tubos de transporte
  - Estrella
  - Flor de Fuego
  - Flor de Hielo
  - Cola
  - Cárcel

## Diagramas de Clase

### Diagrama del Cliente
![Diagrama del Cliente](docs/DiagramaCliente.pdf)
El diagrama del cliente muestra la estructura completa del componente cliente, incluyendo:
- Interfaz gráfica
- Manejo de mini-juegos
- Comunicación con el servidor
- Gestión de estados del juego

### Diagrama del Servidor
![Diagrama del Servidor](docs/DiagramaServer.pdf)
El diagrama del servidor ilustra:
- Gestión de conexiones
- Lógica del juego
- Manejo de efectos especiales
- Sistema de turnos
- Control de mini-juegos

## Arquitectura

### Cliente
- Interfaz gráfica desarrollada en Java Swing
- Comunicación mediante sockets
- Sistema de eventos asíncrono
- Implementación de múltiples mini-juegos

### Servidor
- Manejo de múltiples conexiones simultáneas
- Control centralizado del estado del juego
- Gestión de mini-juegos y efectos especiales
- Sistema de turnos robusto

## Notas Adicionales
- El juego soporta de 2 a 6 jugadores
- Cada jugador debe seleccionar un personaje único
- El orden de los turnos se determina aleatoriamente al inicio
- Los mini-juegos se activan al caer en casillas específicas

## Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE.md](LICENSE.md) para más detalles.