package com.mycompany.gameserver.network;

import com.mycompany.gameserver.model.*;
import com.mycompany.gameserver.model.BoardMovementManager.MoveResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.*;
import java.io.*;
import java.util.*;

public class GameServer {
    private static final Logger logger = LoggerFactory.getLogger(GameServer.class);
    private final int port;
    private ServerSocket serverSocket;
    private boolean running;
    private final List<ClientHandler> clients;
    private GameBoard gameBoard;
    private TurnManager turnManager;
    private final SpaceEffectHandler spaceEffectHandler;
    private static final int MAX_PLAYERS = 6;
    private static final int MIN_PLAYERS = 2;
    private boolean gameStarted;
    private final Random random;
    
    public GameServer(int port) {
        this.port = port;
        this.clients = Collections.synchronizedList(new ArrayList<>());
        this.gameBoard = new GameBoard();
        this.turnManager = new TurnManager();
        this.spaceEffectHandler = new SpaceEffectHandler(gameBoard, turnManager);
        this.gameStarted = false;
        this.random = new Random();
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            logger.info("Servidor iniciado en puerto: {}", port);
            
            while (running && clients.size() < MAX_PLAYERS) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Nuevo cliente conectado desde: {}", 
                          clientSocket.getInetAddress().getHostAddress());
                
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
                
                sendBoardToNewClient(clientHandler);
                
                if (clients.size() >= MIN_PLAYERS && !gameStarted) {
                    startGameIfReady();
                }
            }
        } catch (IOException e) {
            logger.error("Error en el servidor: {}", e.getMessage());
        }
    }
    
public void handleMessage(ClientHandler client, String message) {
    try {
        if (message.startsWith("REGISTER:")) {
            String[] parts = message.split(":", 3);
            String playerName = parts[1];
            GameCharacter character = GameCharacter.valueOf(parts[2]);
            
            // Verificar si el nombre o personaje ya están en uso
            if (turnManager.isPlayerNameInUse(playerName)) {
                client.sendMessage("ERROR:El nombre ya está en uso");
                return;
            }
            if (turnManager.isCharacterInUse(character)) {
                client.sendMessage("ERROR:El personaje ya está en uso");
                return;
            }
            
            turnManager.addPlayer(client, playerName, character);
            broadcast("PLAYER_JOINED:" + playerName + ":" + character.name());
        }
        else if (message.startsWith("NUMBER_GUESS:")) {
            int number = Integer.parseInt(message.split(":")[1]);
            turnManager.handleNumberGuess(client, number);
        }
        else if (message.startsWith("ROLL_DICE") || message.startsWith("DICE_ROLL:")) {
            if (!turnManager.isCurrentPlayer(client)) {
                return;
            }
            
            int totalSpaces;
            int dice1;
            int dice2;
            int penaltyTurns = 0;
            
            if (message.startsWith("DICE_ROLL:")) { // Modo prueba
                totalSpaces = Integer.parseInt(message.split(":")[1]);
                dice1 = totalSpaces / 2;
                dice2 = totalSpaces - dice1;
                logger.debug("Modo prueba: jugador {} avanza {} espacios", 
                    client.getPlayerName(), totalSpaces);
            } else { // Dados normales
                TurnManager.DiceRoll roll = turnManager.rollDice();
                dice1 = roll.getDice1();
                dice2 = roll.getDice2();
                penaltyTurns = roll.getPenaltyTurns();
                totalSpaces = roll.getTotal();
                logger.debug("Dados normales: jugador {} sacó {}+{} = {}", 
                    client.getPlayerName(), dice1, dice2, totalSpaces);
            }
            
            // Enviar resultado de dados
            broadcast("DICE_RESULT:" + client.getPlayerName() + ":" + 
                     dice1 + ":" + dice2 + ":" + penaltyTurns);
            
            // Manejar penalización si existe
            if (penaltyTurns > 0) {
                turnManager.applyPenalty(client, penaltyTurns);
                broadcast("GAME_MESSAGE:" + client.getPlayerName() + 
                        " pierde " + penaltyTurns + 
                        (penaltyTurns > 1 ? " turnos" : " turno"));
                turnManager.nextTurn();
                return;
            }
            
            // Procesar movimiento
            MoveResult moveResult = turnManager.movePlayer(client, totalSpaces);
            if (moveResult != null) {
                SpaceType spaceType = gameBoard.getSpaceType(moveResult.getFinalPosition());

                // Deshabilitar controles antes del efecto
                client.sendMessage("ENABLE_ROLL:false");
                client.sendMessage("ENABLE_TEST_MODE:false");

                if (spaceType == SpaceType.STAR) {
                    // Si es estrella, activar el efecto pero no pasar turno aún
                    spaceEffectHandler.handleSpaceEffect(client, spaceType, moveResult.getFinalPosition());
                    turnManager.setStarEffect(true);  // Marcar que usamos la estrella
                } else {
                    // Para cualquier otra casilla, ejecutar efecto y pasar turno
                    spaceEffectHandler.handleSpaceEffect(client, spaceType, moveResult.getFinalPosition());
                    turnManager.nextTurn();
                }
            }
        }
        else if (message.startsWith("CHAT:")) {
            broadcast("CHAT:" + client.getPlayerName() + ":" + message.substring(5));
        }
        else if (message.startsWith("FIRE_FLOWER_TARGET:")) {
            String targetPlayer = message.substring(17);
            ClientHandler targetClient = getClientByName(targetPlayer);
            if (targetClient != null) {
                // Mover al jugador objetivo al inicio
                turnManager.setPlayerPosition(targetClient, 0);
                broadcast("GAME_MESSAGE:" + client.getPlayerName() + 
                    " usó la Flor de Fuego en " + targetPlayer + ". ¡Vuelve al inicio!");
                broadcast("PLAYER_MOVE:" + targetPlayer + ":0");
            }
            turnManager.nextTurn();
        }
        else if (message.startsWith("ICE_FLOWER_TARGET:")) {
            String targetPlayer = message.substring(16);
            ClientHandler targetClient = getClientByName(targetPlayer);
            if (targetClient != null) {
                turnManager.applyPenalty(targetClient, 2);
                broadcast("GAME_MESSAGE:" + client.getPlayerName() + 
                    " usó la Flor de Hielo en " + targetPlayer + ". ¡Congelado por 2 turnos!");
                targetClient.sendMessage("ENABLE_ROLL:false");
                targetClient.sendMessage("ENABLE_TEST_MODE:false");
            }
            turnManager.nextTurn();
        }
        else if (message.startsWith("TAIL_MOVE:")) {
            int targetPosition = Integer.parseInt(message.substring(10));
            spaceEffectHandler.handleTailMove(client, targetPosition);
            turnManager.nextTurn();
        }
    } catch (Exception e) {
        logger.error("Error procesando mensaje de {}: {}", 
            client.getPlayerName(), e.getMessage());
        logger.error("Stack trace:", e);
        client.sendMessage("ERROR:" + e.getMessage());
    }
}

// Método auxiliar para obtener cliente por nombre
private ClientHandler getClientByName(String playerName) {
    synchronized(clients) {
        for (ClientHandler client : clients) {
            if (client.getPlayerName().equals(playerName)) {
                return client;
            }
        }
    }
    return null;
}    
    private void startGameIfReady() {
    if (gameStarted) return;
    
    synchronized (clients) {
        if (clients.size() >= MIN_PLAYERS) {
            gameStarted = true;
            
            // Primero deshabilitamos los controles para todos
            for (ClientHandler client : clients) {
                client.sendMessage("DISABLE_ROLL:true");
                client.sendMessage("ENABLE_TEST_MODE:false");
            }
            
            // Luego determinamos el orden
            if (random.nextBoolean()) {
                broadcast("GAME_START:NUMBER");
                logger.info("Iniciando juego con determinación por número");
                turnManager.determineOrderByNumber();
            } else {
                broadcast("GAME_START:DICE");
                logger.info("Iniciando juego con determinación por dados");
                turnManager.determineOrderByDice();
            }
        }
    }
}
    
    public void sendBoardToNewClient(ClientHandler client) {
        String boardMessage = "BOARD:" + gameBoard.getJsonBoard();
        client.sendMessage(boardMessage);
    }
    
    public void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler client : new ArrayList<>(clients)) {
                client.sendMessage(message);
            }
        }
    }
    
    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
            turnManager.removePlayer(client);
            broadcast("PLAYER_LEFT:" + client.getPlayerName());
            
            if (clients.isEmpty()) {
                gameStarted = false;
            }
        }
    }
}