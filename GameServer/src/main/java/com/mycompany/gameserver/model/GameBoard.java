package com.mycompany.gameserver.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.google.gson.Gson;

public class GameBoard {
    private SpaceType[] spaces;
    public static final int BOARD_SIZE = 26;
    private final Gson gson = new Gson();
    
    public GameBoard() {
        initializeBoard();
    }
    
    private void initializeBoard() {
        spaces = new SpaceType[BOARD_SIZE];
        List<SpaceType> availableTypes = generateSpaceTypeList();
        Collections.shuffle(availableTypes);
        
        for (int i = 0; i < BOARD_SIZE; i++) {
            spaces[i] = availableTypes.get(i);
        }
    }
    
    private List<SpaceType> generateSpaceTypeList() {
        List<SpaceType> types = new ArrayList<>();
        
        // Añadir juegos (2 de cada uno)
        addMultiple(types, SpaceType.GAME_GATO, 2);
        addMultiple(types, SpaceType.GAME_SOPA, 2);
        addMultiple(types, SpaceType.GAME_MEMORY_PATH, 2);
        addMultiple(types, SpaceType.GAME_BROS, 2);
        addMultiple(types, SpaceType.GAME_CAT, 2);
        addMultiple(types, SpaceType.GAME_TREASURE, 2);
        addMultiple(types, SpaceType.GAME_GUESS, 2);
        addMultiple(types, SpaceType.GAME_COINS, 2);
        addMultiple(types, SpaceType.GAME_CARDS, 2);
        
        // Añadir casillas especiales
        types.add(SpaceType.JAIL);
        types.add(SpaceType.TUBE_1);
        types.add(SpaceType.TUBE_2);
        types.add(SpaceType.TUBE_3);
        types.add(SpaceType.STAR);
        types.add(SpaceType.FIRE_FLOWER);
        types.add(SpaceType.ICE_FLOWER);
        types.add(SpaceType.TAIL);
        
        // Llenar el resto con casillas vacías
        while (types.size() < BOARD_SIZE) {
            types.add(SpaceType.EMPTY);
        }
        
        return types;
    }
    
    private void addMultiple(List<SpaceType> list, SpaceType type, int count) {
        for (int i = 0; i < count; i++) {
            list.add(type);
        }
    }
    
    // Obtener el tipo de casilla en una posición específica
    public SpaceType getSpaceType(int position) {
        if (position >= 0 && position < BOARD_SIZE) {
            return spaces[position];
        }
        return SpaceType.EMPTY;
    }
    
    // Encontrar la primera ocurrencia de un tipo de casilla
    public int findFirstSpace(SpaceType type) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (spaces[i] == type) {
                return i;
            }
        }
        return -1;
    }
    
    // Encontrar todas las ocurrencias de un tipo de casilla
    public List<Integer> findAllSpaces(SpaceType type) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (spaces[i] == type) {
                positions.add(i);
            }
        }
        return positions;
    }
    
    // Verificar si una posición es válida en el tablero
    public boolean isValidPosition(int position) {
        return position >= 0 && position < BOARD_SIZE;
    }
    
    // Verificar si una posición es una casilla de juego
    public boolean isGameSpace(int position) {
        if (!isValidPosition(position)) return false;
        SpaceType type = spaces[position];
        return type.name().startsWith("GAME_");
    }
    
    // Verificar si una posición es una casilla especial
    public boolean isSpecialSpace(int position) {
        if (!isValidPosition(position)) return false;
        SpaceType type = spaces[position];
        return !type.name().startsWith("GAME_") && type != SpaceType.EMPTY;
    }
    
    // Obtener el siguiente tubo en la secuencia
    public int getNextTubePosition(int currentPosition) {
        SpaceType currentType = getSpaceType(currentPosition);
        if (currentType == SpaceType.TUBE_1) {
            return findFirstSpace(SpaceType.TUBE_2);
        } else if (currentType == SpaceType.TUBE_2) {
            return findFirstSpace(SpaceType.TUBE_3);
        } else if (currentType == SpaceType.TUBE_3) {
            return findFirstSpace(SpaceType.TUBE_1);
        }
        return -1;
    }
    
    // Obtener posiciones válidas para el movimiento de cola (+-3)
    public List<Integer> getTailMoveOptions(int currentPosition) {
        List<Integer> options = new ArrayList<>();
        for (int i = Math.max(0, currentPosition - 3); 
             i <= Math.min(BOARD_SIZE - 1, currentPosition + 3); i++) {
            if (i != currentPosition) {
                options.add(i);
            }
        }
        return options;
    }
    
    // Reiniciar el tablero con nueva distribución
    public void resetBoard() {
        initializeBoard();
    }
    
    public String getJsonBoard() {
        return gson.toJson(spaces);
    }
    
    // Obtener una representación del tablero como String para debugging
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < BOARD_SIZE; i++) {
            sb.append(i).append(": ").append(spaces[i].getName()).append("\n");
        }
        return sb.toString();
    }
    
    // Obtener el array de espacios (útil para testing)
    public SpaceType[] getSpaces() {
        return spaces.clone();
    }
}