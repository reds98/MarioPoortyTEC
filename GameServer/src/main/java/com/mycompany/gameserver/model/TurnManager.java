package com.mycompany.gameserver.model;

import java.util.*;
import com.google.gson.Gson;
import com.mycompany.gameserver.model.BoardMovementManager.MoveResult;
import com.mycompany.gameserver.network.ClientHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurnManager {
    private final List<PlayerInfo> players;
    private int currentPlayerIndex;
    private final Dice dice;
    private final Gson gson;
    private static final int MAX_NUMBER = 1000;
    private final Random random;
    private boolean orderDetermined;
    private int targetNumber;
    private int playersResponded;
    private final BoardMovementManager movementManager = new BoardMovementManager();
    private boolean starEffect = false;
    private static final Logger logger = LoggerFactory.getLogger(TurnManager.class);
    private boolean starEffectUsed = false;
    private boolean justUsedStar = false;  // Nueva variable para rastrear si acabamos de usar la estrella







    public TurnManager() {
        this.players = Collections.synchronizedList(new ArrayList<>());
        this.currentPlayerIndex = 0;
        this.dice = new Dice();
        this.gson = new Gson();
        this.random = new Random();
        this.orderDetermined = false;
        this.playersResponded = 0;
    }

    // Métodos de gestión de jugadores
     public void addPlayer(ClientHandler client, String name, GameCharacter character) {
        synchronized (players) {
            players.add(new PlayerInfo(client, name, character));
        }
    }
     
    public void setStarEffect(boolean active) {
    this.justUsedStar = active;
}
    
    public boolean hasUsedStarEffect() {
        return starEffectUsed;
    }


     public void removePlayer(ClientHandler client) {
        synchronized (players) {
            players.removeIf(p -> p.getClient() == client);
            if (currentPlayerIndex >= players.size()) {
                currentPlayerIndex = 0;
            }
        }
    }
     
     
    public MoveResult movePlayer(ClientHandler client, int diceTotal) {
    PlayerInfo player = null;
    for (PlayerInfo p : players) {
        if (p.getClient() == client) {
            player = p;
            break;
        }
    }
    
    if (player == null) return null;
    
    MoveResult result = movementManager.calculateMove(
        player.getPosition(), diceTotal);
    
    // Actualizar posición del jugador
    player.setPosition(result.getFinalPosition());
    
    // Notificar a todos los clientes
    String moveMessage = String.format("PLAYER_MOVE:%s:%d",
        player.getName(), result.getFinalPosition());
    broadcastMessage(moveMessage);
    
    // Si es un movimiento especial, notificar
    if (result.getMessage() != null) {
        broadcastMessage("GAME_MESSAGE:" + result.getMessage());
    }
    
    // Si es victoria, notificar
    if (result.isWinningMove()) {
        broadcastMessage("PLAYER_WIN:" + player.getName());
    }
    
    return result;
}
    public void finalizeTurnOrder() {
    synchronized(players) {
        Collections.sort(players, (p1, p2) -> {
            int diff1 = Math.abs(p1.getGuessedNumber() - targetNumber);
            int diff2 = Math.abs(p2.getGuessedNumber() - targetNumber);
            return Integer.compare(diff1, diff2);
        });
        
        orderDetermined = true;
        broadcastTurnOrder();
        logger.info("Orden de turnos determinado. Iniciando primer turno.");
        startFirstTurn();
    }
}
     
     
     public void handleNumberGuess(ClientHandler client, int number) {
    synchronized (players) {
        for (PlayerInfo player : players) {
            if (player.getClient() == client) {
                player.setGuessedNumber(number);
                playersResponded++;
                break;
            }
        }
        
        // Si todos respondieron, determinar orden
        if (playersResponded == players.size()) {
            finalizeTurnOrder();
        }
    }
}

    public String getPlayerName(ClientHandler client) {
        for (PlayerInfo player : players) {
            if (player.getClient() == client) {
                return player.getName();
            }
        }
        return null;
    }

    public boolean isCurrentPlayer(ClientHandler client) {
        if (players.isEmpty()) return false;
        PlayerInfo currentPlayer = players.get(currentPlayerIndex);
        return currentPlayer.getClient() == client;
    }

    public PlayerStatus getPlayerStatus(ClientHandler client) {
        for (PlayerInfo player : players) {
            if (player.getClient() == client) {
                return new PlayerStatus(
                    player.getPosition(),
                    player.getPenaltyTurns(),
                    isCurrentPlayer(client)
                );
            }
        }
        return null;
    }

    // Métodos de control del juego
   public void determineOrderByNumber() {
    targetNumber = random.nextInt(MAX_NUMBER) + 1;
    playersResponded = 0;
    orderDetermined = false;
    
    List<PlayerInfo> playersCopy;
    synchronized (players) {
        // Crear una copia segura de la lista
        playersCopy = new ArrayList<>(players);
    }
    
    // Asegurarnos que todos los controles estén deshabilitados
    for (PlayerInfo player : playersCopy) {
        player.getClient().sendMessage("DISABLE_ROLL:true");
        player.getClient().sendMessage("ENABLE_TEST_MODE:false");
    }
    
    logger.info("Solicitando números a los jugadores. Número objetivo: {}", targetNumber);
    for (PlayerInfo player : playersCopy) {
        player.getClient().sendMessage("REQUEST_NUMBER:" + targetNumber);
    }
}

    public void determineOrderByDice() {
        orderDetermined = false;
        for (PlayerInfo player : players) {
            int roll1 = dice.roll();
            int roll2 = dice.roll();
            player.setInitialRoll(roll1 + roll2);
            
            broadcastMessage("DICE_ROLL:" + player.getName() + ":" + roll1 + ":" + roll2);
        }

        Collections.sort(players, (p1, p2) -> 
            Integer.compare(p2.getInitialRoll(), p1.getInitialRoll()));

        orderDetermined = true;
        broadcastTurnOrder();
        startFirstTurn();
    }

    private void startFirstTurn() {
    synchronized(players) {
        if (players.isEmpty()) {
            logger.warn("No hay jugadores para iniciar el turno");
            return;
        }

        currentPlayerIndex = 0;
        PlayerInfo firstPlayer = players.get(0);
        
        logger.info("Iniciando primer turno con jugador: {}", firstPlayer.getName());
        
        // Deshabilitar controles para todos primero
        for (PlayerInfo player : new ArrayList<>(players)) {
            if (player != firstPlayer) {
                player.getClient().sendMessage("ENABLE_ROLL:false");
                player.getClient().sendMessage("ENABLE_TEST_MODE:false");
            }
        }
        
        // Pequeña pausa
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            logger.error("Error en pausa de inicialización", e);
        }
        
        // Habilitar controles explícitamente para el primer jugador
        firstPlayer.getClient().sendMessage("ENABLE_ROLL:true");
        firstPlayer.getClient().sendMessage("ENABLE_TEST_MODE:true");
        
        // Notificar el turno actual
        broadcastMessage("CURRENT_TURN:" + firstPlayer.getName());
        logger.info("Controles habilitados para el primer jugador: {}", firstPlayer.getName());
    }
}


    // Métodos de dados y movimiento
    public DiceRoll rollDice() {
        if (!orderDetermined) return null;

        int dice1 = dice.roll();
        int dice2 = dice.roll();

        int penaltyTurns = 0;
        if (dice.isPenalty(dice1)) penaltyTurns++;
        if (dice.isPenalty(dice2)) penaltyTurns++;

        return new DiceRoll(dice1, dice2, penaltyTurns);
    }

    public void applyPenalty(ClientHandler client, int turns) {
        for (PlayerInfo player : players) {
            if (player.getClient() == client) {
                player.addPenaltyTurns(turns);
                break;
            }
        }
    }

    

    private int calculateNewPosition(int currentPosition, int spaces) {
        int newPosition = currentPosition + spaces;
        if (newPosition >= GameBoard.BOARD_SIZE) {
            int excess = newPosition - (GameBoard.BOARD_SIZE - 1);
            newPosition = (GameBoard.BOARD_SIZE - 1) - excess;
        }
        return newPosition;
    }

    public void setPlayerPosition(ClientHandler client, int position) {
        for (PlayerInfo player : players) {
            if (player.getClient() == client) {
                player.setPosition(position);
                break;
            }
        }
    }

  public void nextTurn() {
    if (!orderDetermined) return;
    
    // Si acabamos de usar la estrella y terminar el turno extra
    if (justUsedStar) {
        justUsedStar = false;  // Reiniciar la bandera
        PlayerInfo currentPlayer = players.get(currentPlayerIndex);
        
        // Pasar al siguiente jugador
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            currentPlayer = players.get(currentPlayerIndex);
        } while (currentPlayer.getPenaltyTurns() > 0);
        
        broadcastMessage("CURRENT_TURN:" + currentPlayer.getName());
        currentPlayer.getClient().sendMessage("ENABLE_ROLL:true");
        currentPlayer.getClient().sendMessage("ENABLE_TEST_MODE:true");
        
        for (PlayerInfo player : players) {
            if (player != currentPlayer) {
                player.getClient().sendMessage("ENABLE_ROLL:false");
                player.getClient().sendMessage("ENABLE_TEST_MODE:false");
            }
        }
        return;
    }

    // Resto de la lógica normal de nextTurn...
    PlayerInfo currentPlayer = players.get(currentPlayerIndex);
    if (currentPlayer.getPenaltyTurns() > 0) {
        currentPlayer.decrementPenaltyTurns();
        broadcastMessage("SKIP_TURN:" + currentPlayer.getName());
    }
    
    do {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);
    } while (currentPlayer.getPenaltyTurns() > 0);
    
    broadcastMessage("CURRENT_TURN:" + currentPlayer.getName());
    currentPlayer.getClient().sendMessage("ENABLE_ROLL:true");
    currentPlayer.getClient().sendMessage("ENABLE_TEST_MODE:true");
    
    for (PlayerInfo player : players) {
        if (player != currentPlayer) {
            player.getClient().sendMessage("ENABLE_ROLL:false");
            player.getClient().sendMessage("ENABLE_TEST_MODE:false");
        }
    }
}

    // Métodos de verificación y estado
    public boolean hasWon(ClientHandler client) {
        for (PlayerInfo player : players) {
            if (player.getClient() == client) {
                return player.getPosition() == GameBoard.BOARD_SIZE - 1;
            }
        }
        return false;
    }

    public boolean canStartGame() {
        return players.size() >= 2 && players.size() <= 6;
    }

    public boolean canAddMorePlayers() {
        return players.size() < 6;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean isCharacterInUse(GameCharacter character) {
        return players.stream()
            .anyMatch(p -> p.getCharacter() == character);
    }

    public boolean isPlayerNameInUse(String name) {
        return players.stream()
            .anyMatch(p -> p.getName().equalsIgnoreCase(name));
    }

    public void resetGame() {
        for (PlayerInfo player : players) {
            player.setPosition(0);
            player.penaltyTurns = 0;
        }
        currentPlayerIndex = 0;
        orderDetermined = false;
        playersResponded = 0;
        broadcastMessage("GAME_RESET");
    }

    public int getPlayerPenaltyTurns(ClientHandler client) {
        for (PlayerInfo player : players) {
            if (player.getClient() == client) {
                return player.getPenaltyTurns();
            }
        }
        return 0;
    }

    public boolean isPlayerPenalized(ClientHandler client) {
        return getPlayerPenaltyTurns(client) > 0;
    }

    public int getPlayerPosition(ClientHandler client) {
        for (PlayerInfo player : players) {
            if (player.getClient() == client) {
                return player.getPosition();
            }
        }
        return -1;
    }

    // Métodos de comunicación
    private void broadcastTurnOrder() {
        List<String> playerOrder = new ArrayList<>();
        for (PlayerInfo player : players) {
            playerOrder.add(player.getName());
        }
        String orderJson = gson.toJson(playerOrder);
        broadcastMessage("TURN_ORDER:" + orderJson);
    }

    public void broadcastMessage(String message) {
    // Crear una copia de la lista de jugadores para iterar
    List<PlayerInfo> playersCopy;
    synchronized (players) {
        playersCopy = new ArrayList<>(players);
    }
    
    for (PlayerInfo player : playersCopy) {
        player.getClient().sendMessage(message);
    }
}

    // Clases internas
    public  static class PlayerInfo {
        private final ClientHandler client;
        private final String name;
        private final GameCharacter character;
        private int guessedNumber;
        private int initialRoll;
        private int penaltyTurns;
        private int position;

        public PlayerInfo(ClientHandler client, String name, GameCharacter character) {
            this.client = client;
            this.name = name;
            this.character = character;
            this.penaltyTurns = 0;
            this.position = 0;
        }

        public ClientHandler getClient() { return client; }
        public String getName() { return name; }
        public GameCharacter getCharacter() { return character; }
        public int getGuessedNumber() { return guessedNumber; }
        public void setGuessedNumber(int number) { this.guessedNumber = number; }
        public int getInitialRoll() { return initialRoll; }
        public void setInitialRoll(int roll) { this.initialRoll = roll; }
        public int getPenaltyTurns() { return penaltyTurns; }
        public void addPenaltyTurns(int turns) { this.penaltyTurns += turns; }
        public void decrementPenaltyTurns() { if (penaltyTurns > 0) penaltyTurns--; }
        public int getPosition() { return position; }
        public void setPosition(int position) { this.position = position; }
       
    }

    public static class PlayerStatus {
        private final int position;
        private final int penaltyTurns;
        private final boolean isCurrentTurn;

        public PlayerStatus(int position, int penaltyTurns, boolean isCurrentTurn) {
            this.position = position;
            this.penaltyTurns = penaltyTurns;
            this.isCurrentTurn = isCurrentTurn;
        }

        public int getPosition() { return position; }
        public int getPenaltyTurns() { return penaltyTurns; }
        public boolean isCurrentTurn() { return isCurrentTurn; }
    }

    public static class DiceRoll {
        private final int dice1;
        private final int dice2;
        private final int penaltyTurns;

        public DiceRoll(int dice1, int dice2, int penaltyTurns) {
            this.dice1 = dice1;
            this.dice2 = dice2;
            this.penaltyTurns = penaltyTurns;
        }

        public int getTotal() {
            return (dice1 == 6 ? 0 : dice1) + (dice2 == 6 ? 0 : dice2);
        }

        public int getPenaltyTurns() { return penaltyTurns; }
        public int getDice1() { return dice1; }
        public int getDice2() { return dice2; }
    }

    // Getters adicionales
    public List<PlayerInfo> getPlayers() {
        return new ArrayList<>(players);
    }

    public PlayerInfo getCurrentPlayer() {
        return players.isEmpty() ? null : players.get(currentPlayerIndex);
    }

    public boolean isOrderDetermined() {
        return orderDetermined;
    }
    public void resetStarEffectUsed() {
    this.starEffectUsed = false;
}
}