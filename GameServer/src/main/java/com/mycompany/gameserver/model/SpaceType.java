package com.mycompany.gameserver.model;

import java.awt.Color;
import java.util.Arrays;

public enum SpaceType {
    // Casillas de juegos
    GAME_GATO("Gato", new Color(255, 182, 193), "Juega una partida de Gato contra otro jugador"),
    GAME_SOPA("Sopa de Letras", new Color(173, 216, 230), "Encuentra 4 palabras en 2 minutos"),
    GAME_MEMORY_PATH("Memory Path", new Color(144, 238, 144), "Memoriza y reproduce el camino correcto"),
    GAME_BROS("Super Bros Memory", new Color(255, 218, 185), "Encuentra los pares de imágenes"),
    GAME_CAT("Catch the Cat", new Color(221, 160, 221), "Atrapa al gato antes de que escape"),
    GAME_TREASURE("Treasure Hunt", new Color(255, 215, 0), "Encuentra el tesoro con las bombas disponibles"),
    GAME_GUESS("Guess Who", new Color(176, 196, 222), "Adivina el personaje con las pistas dadas"),
    GAME_COINS("Collect Coins", new Color(255, 255, 0), "Recolecta monedas con valor positivo"),
    GAME_CARDS("Mario Cards", new Color(255, 99, 71), "Obtén la carta más alta"),

    // Casillas especiales
    JAIL("Cárcel", Color.GRAY, "Pierdes 2 turnos"),
    TUBE_1("Tubo 1", new Color(75, 0, 130), "Te transporta al Tubo 2"),
    TUBE_2("Tubo 2", new Color(106, 90, 205), "Te transporta al Tubo 3"),
    TUBE_3("Tubo 3", new Color(138, 43, 226), "Te transporta al Tubo 1"),
    STAR("Estrella", Color.YELLOW, "Lanza los dados nuevamente"),
    FIRE_FLOWER("Flor de Fuego", Color.RED, "Envía a un jugador al inicio"),
    ICE_FLOWER("Flor de Hielo", Color.CYAN, "Congela a un jugador por 2 turnos"),
    TAIL("Cola", Color.ORANGE, "Muévete ±3 casillas"),
    EMPTY("Vacío", Color.WHITE, "Casilla sin efecto");

    private final String name;
    private final Color color;
    private final String description;

    SpaceType(String name, Color color, String description) {
        this.name = name;
        this.color = color;
        this.description = description;
    }

    // Getters básicos
    public String getName() { return name; }
    public Color getColor() { return color; }
    public String getDescription() { return description; }

    // Métodos de utilidad
    public boolean isGameSpace() {
        return name().startsWith("GAME_");
    }

    public boolean isSpecialSpace() {
        return !isGameSpace() && this != EMPTY;
    }

    public boolean isTube() {
        return this == TUBE_1 || this == TUBE_2 || this == TUBE_3;
    }

    public boolean isPenaltySpace() {
        return this == JAIL || this == ICE_FLOWER;
    }

    public boolean requiresTargetPlayer() {
        return this == FIRE_FLOWER || this == ICE_FLOWER;
    }

    public boolean requiresPlayerInput() {
        return this == TAIL;
    }

    public int getPenaltyTurns() {
        switch (this) {
            case JAIL: return 2;
            case ICE_FLOWER: return 2;
            default: return 0;
        }
    }

    public SpaceType getNextTube() {
        switch (this) {
            case TUBE_1: return TUBE_2;
            case TUBE_2: return TUBE_3;
            case TUBE_3: return TUBE_1;
            default: return null;
        }
    }

    public int getTimeLimit() {
        switch (this) {
            case GAME_SOPA: return 120;
            case GAME_COINS: return 45;
            default: return 0;
        }
    }

    public int getMaxAttempts() {
        switch (this) {
            case GAME_MEMORY_PATH: return 3;
            case GAME_GUESS: return 1;
            default: return 0;
        }
    }

    public String getFormattedInfo() {
        StringBuilder info = new StringBuilder();
        info.append(name).append("\n");
        info.append(description);
        
        if (getTimeLimit() > 0) {
            info.append("\nTiempo límite: ").append(getTimeLimit()).append(" segundos");
        }
        if (getMaxAttempts() > 0) {
            info.append("\nIntentos máximos: ").append(getMaxAttempts());
        }
        return info.toString();
    }

    public static boolean isInTailRange(int currentPos, int targetPos) {
        if (currentPos < 0 || targetPos < 0) return false;
        int distance = Math.abs(currentPos - targetPos);
        return distance > 0 && distance <= 3;
    }

    public static SpaceType getByName(String name) {
        for (SpaceType type : values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return EMPTY;
    }

    // Métodos corregidos para obtener espacios
    public static SpaceType[] getAllGameSpaces() {
        return Arrays.stream(values())
                    .filter(SpaceType::isGameSpace)
                    .toArray(SpaceType[]::new);
    }

    public static SpaceType[] getAllSpecialSpaces() {
        return Arrays.stream(values())
                    .filter(SpaceType::isSpecialSpace)
                    .toArray(SpaceType[]::new);
    }

    @Override
    public String toString() {
        return name;
    }
}