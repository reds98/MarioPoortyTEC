package com.mycompany.gameclient.ui;

import com.google.gson.Gson;
import com.mycompany.gameclient.model.GameCharacter;
import com.mycompany.gameclient.model.SpaceType;
import com.mycompany.gameclient.network.GameClient;
import com.mycompany.gameclient.network.MessageListener;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameClientWindow extends JFrame implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(GameClientWindow.class);
    private GameClient client;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton connectButton;
    private JTextField nameField;
    private BoardPanel boardPanel;
    private GameControlPanel controlPanel;
    private JButton testModeButton;
    private final List<PlayerInfo> players;
    private final Gson gson;
    
    public GameClientWindow() {
        this.players = new ArrayList<>();
        this.gson = new Gson();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Juego de Tablero - Cliente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de conexión (Norte)
        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        nameField = new JTextField(15);
        connectButton = new JButton("Conectar");
        connectionPanel.add(new JLabel("Nombre:"));
        connectionPanel.add(nameField);
        connectionPanel.add(connectButton);
        
        // Panel de control (Oeste)
        controlPanel = new GameControlPanel();
        controlPanel.setPreferredSize(new Dimension(250, getHeight()));
        controlPanel.setRollButtonListener(e -> rollDice());
        controlPanel.setTestModeButtonListener(e -> handleTestMode());


        
       
       
        
        // Panel del tablero (Centro)
        boardPanel = new BoardPanel();
        JScrollPane boardScrollPane = new JScrollPane(boardPanel);
        
        // Panel de chat (Este)
        JPanel chatPanel = new JPanel(new BorderLayout(0, 5));
        chatPanel.setPreferredSize(new Dimension(250, getHeight()));
        
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        
        JPanel messagePanel = new JPanel(new BorderLayout(5, 0));
        messageField = new JTextField();
        sendButton = new JButton("Enviar");
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
        chatPanel.add(messagePanel, BorderLayout.SOUTH);
        
        mainPanel.add(connectionPanel, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.WEST);
        mainPanel.add(boardScrollPane, BorderLayout.CENTER);
        mainPanel.add(chatPanel, BorderLayout.EAST);
        
        add(mainPanel);
        
        connectButton.addActionListener(e -> connectToServer());
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
        
        messageField.setEnabled(false);
        sendButton.setEnabled(false);
    }

       private void handleTestMode() {
            if (client != null && client.isConnected()) {
            TestModePanel testPanel = new TestModePanel(this);
            testPanel.setVisible(true);

            int selectedValue = testPanel.getSelectedValue();
            if (selectedValue != -1) {
                // Enviar el valor total al servidor
                client.sendMessage("DICE_ROLL:" + selectedValue);

                // Deshabilitar botones después del movimiento
                controlPanel.enableRollButton(false);
                controlPanel.enableTestMode(false);

                // Mostrar mensaje en el chat
                chatArea.append("[SISTEMA] Modo prueba: Avanzando " + selectedValue + " espacios\n");
            }
        }
}
    
    private void connectToServer() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un nombre");
            return;
        }
        
        CharacterSelectionDialog dialog = new CharacterSelectionDialog(this);
        dialog.setVisible(true);
        
        if (!dialog.isSelectionMade()) {
            return;
        }
        
        GameCharacter selectedCharacter = dialog.getSelectedCharacter();
        
        try {
            client = new GameClient();
            client.addMessageListener(this);
            client.connect("localhost", nameField.getText(), selectedCharacter);
            
            controlPanel.setPlayerCharacter(selectedCharacter);
            controlPanel.setStatus("Conectado - Esperando jugadores...");
            
            messageField.setEnabled(true);
            sendButton.setEnabled(true);
            connectButton.setEnabled(false);
            nameField.setEnabled(false);
            
        } catch (Exception e) {
            logger.error("Error connecting to server: {}", e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error al conectar con el servidor: " + e.getMessage());
        }
    }
    
    private void sendMessage() {
        if (client != null && client.isConnected() && !messageField.getText().trim().isEmpty()) {
            client.sendChatMessage(messageField.getText().trim());
            messageField.setText("");
        }
    }
    
    private void rollDice() {
        if (client != null && client.isConnected()) {
            client.rollDice();
            controlPanel.enableRollButton(false);
            controlPanel.enableTestMode(false);
        }
    }
    
    private void showPlayerSelectionDialog(String message, Consumer<String> callback) {
        String[] playerNames = players.stream()
            .map(PlayerInfo::getName)
            .filter(name -> !name.equals(client.getPlayerName()))
            .toArray(String[]::new);
            
        if (playerNames.length > 0) {
            String selected = (String) JOptionPane.showInputDialog(this,
                message,
                "Seleccionar Jugador",
                JOptionPane.QUESTION_MESSAGE,
                null,
                playerNames,
                playerNames[0]);
                
            if (selected != null) {
                callback.accept(selected);
            }
        }
    }
    
    private void showTailMoveDialog(int currentPosition, Consumer<Integer> callback) {
        List<Integer> validPositions = new ArrayList<>();
        for (int i = Math.max(0, currentPosition - 3); 
             i <= Math.min(25, currentPosition + 3); i++) {
            if (i != currentPosition) {
                validPositions.add(i);
            }
        }
        
        Integer[] positions = validPositions.toArray(new Integer[0]);
        Integer selected = (Integer) JOptionPane.showInputDialog(this,
            "Selecciona la casilla a la que quieres moverte:",
            "Usar Cola",
            JOptionPane.QUESTION_MESSAGE,
            null,
            positions,
            positions[0]);
            
        if (selected != null) {
            callback.accept(selected);
        }
    }
    
    private void handleGameStart(String message) {
        String startType = message.substring(11);
        controlPanel.setStatus("Determinando orden de juego...");
        
        if (startType.equals("NUMBER")) {
            String input = JOptionPane.showInputDialog(this,
                "Ingresa un número entre 1 y 1000:",
                "Inicio del Juego",
                JOptionPane.QUESTION_MESSAGE);
            
            try {
                int number = Integer.parseInt(input);
                if (number >= 1 && number <= 1000) {
                    client.sendNumberGuess(number);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Número fuera de rango. Se enviará un número aleatorio.");
                    client.sendNumberGuess(new Random().nextInt(1000) + 1);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Número inválido. Se enviará un número aleatorio.");
                client.sendNumberGuess(new Random().nextInt(1000) + 1);
            }
        }
    }
    
    private void handlePlayerMove(String message) {
        String[] parts = message.split(":");
        String playerName = parts[1];
        int position = Integer.parseInt(parts[2]);
        
        // Remover jugador de todas las casillas
        for (int i = 0; i < boardPanel.getSpaceCount(); i++) {
            boardPanel.removePlayerFromSpace(playerName, i);
        }
        
        // Agregar jugador a nueva posición
        GameCharacter character = null;
        for (PlayerInfo player : players) {
            if (player.getName().equals(playerName)) {
                character = player.getCharacter();
                break;
            }
        }
        
        if (character != null) {
            boardPanel.addPlayerToSpace(playerName, character.getColor(), position);
        }
    }
    
@Override
public void onMessageReceived(String message) {
    SwingUtilities.invokeLater(() -> {
        try {
            if (message.startsWith("BOARD:")) {
                processBoardMessage(message.substring(6));
            }
            else if (message.startsWith("CHAT:")) {
                String[] parts = message.split(":", 3);
                if (parts.length == 3) {
                    chatArea.append(parts[1] + ": " + parts[2] + "\n");
                }
            }
            else if (message.startsWith("PLAYER_JOINED:")) {
                String[] parts = message.split(":", 3);
                if (parts.length == 3) {
                    String playerName = parts[1];
                    GameCharacter character = GameCharacter.valueOf(parts[2]);
                    players.add(new PlayerInfo(playerName, character));
                    controlPanel.addPlayer(playerName, character);
                    chatArea.append("[SISTEMA] " + playerName + " se unió como " + 
                        character.getName() + "\n");
                }
            }
            else if (message.startsWith("PLAYER_LEFT:")) {
                String playerName = message.substring(12);
                players.removeIf(p -> p.getName().equals(playerName));
                controlPanel.removePlayer(playerName);
                chatArea.append("[SISTEMA] " + playerName + " abandonó el juego\n");
            }
            else if (message.startsWith("GAME_START:")) {
                handleGameStart(message);
            }
            else if (message.startsWith("CURRENT_TURN:")) {
                String playerName = message.substring(13);
                boolean isMyTurn = playerName.equals(client.getPlayerName());
                controlPanel.setCurrentTurn(playerName, isMyTurn);
            }
            else if (message.equals("ENABLE_ROLL:true")) {
                logger.debug("Habilitando controles para jugador {}", client.getPlayerName());
                controlPanel.enableRollButton(true);
                controlPanel.enableTestMode(true);
            }
            else if (message.equals("ENABLE_ROLL:false")) {
                logger.debug("Deshabilitando controles para jugador {}", client.getPlayerName());
                controlPanel.enableRollButton(false);
                controlPanel.enableTestMode(false);
            }
            else if (message.startsWith("DICE_RESULT:")) {
                String[] parts = message.split(":");
                String playerName = parts[1];
                int dice1 = Integer.parseInt(parts[2]);
                int dice2 = Integer.parseInt(parts[3]);
                int penaltyTurns = Integer.parseInt(parts[4]);
                
                controlPanel.updateDice(dice1, dice2);
                String result = String.format("%s lanzó %d y %d", 
                    playerName, dice1, dice2);
                if (penaltyTurns > 0) {
                    result += String.format(" (¡Pierde %d turno%s!)", 
                        penaltyTurns, penaltyTurns > 1 ? "s" : "");
                }
                chatArea.append("[SISTEMA] " + result + "\n");
            }
            else if (message.startsWith("PLAYER_MOVE:")) {
                handlePlayerMove(message);
            }
            else if (message.startsWith("GAME_MESSAGE:")) {
                chatArea.append("[JUEGO] " + message.substring(13) + "\n");
            }
            else if (message.startsWith("SPACE_EFFECT:")) {
                String[] parts = message.split(":");
                String effect = parts[1];
                String playerName = parts[2];
                
                logger.debug("Efecto recibido: {} para jugador {}", effect, playerName);
                
                if (playerName.equals(client.getPlayerName())) {
                    switch (effect) {
                        case "FIRE_FLOWER":
                            showPlayerSelectionDialog(
                                "Selecciona un jugador para enviar al inicio:",
                                selected -> client.sendMessage("FIRE_FLOWER_TARGET:" + selected));
                            break;
                        case "ICE_FLOWER":
                            showPlayerSelectionDialog(
                                "Selecciona un jugador para congelar:",
                                selected -> client.sendMessage("ICE_FLOWER_TARGET:" + selected));
                            break;
                        case "TAIL":
                            int currentPos = Integer.parseInt(parts[3]);
                            showTailMoveDialog(currentPos,
                                targetPos -> client.sendMessage("TAIL_MOVE:" + targetPos));
                            break;
                        case "STAR":
                            // Esperamos el mensaje ENABLE_ROLL:true del servidor
                            break;
                    }
                }
            }
            else if (message.startsWith("SKIP_TURN:")) {
                String playerName = message.substring(10);
                chatArea.append("[SISTEMA] " + playerName + " pierde su turno\n");
                if (playerName.equals(client.getPlayerName())) {
                    controlPanel.enableRollButton(false);
                    controlPanel.enableTestMode(false);
                }
            }
            else if (message.startsWith("PLAYER_WIN:")) {
                String winner = message.substring(11);
                controlPanel.setStatus("¡" + winner + " ha ganado!");
                JOptionPane.showMessageDialog(this,
                    "¡" + winner + " ha ganado el juego!",
                    "¡Fin del Juego!",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            
        } catch (Exception e) {
            logger.error("Error procesando mensaje: {}", e.getMessage());
            e.printStackTrace(); // Para debugging
        }
    });
}
    
    private void processBoardMessage(String boardJson) {
        try {
            SpaceType[] boardSpaces = gson.fromJson(boardJson, SpaceType[].class);
            for (int i = 0; i < boardSpaces.length; i++) {
                boardPanel.updateSpace(i, boardSpaces[i]);
            }
        } catch (Exception e) {
            logger.error("Error procesando el tablero: {}", e.getMessage());
        }
    }
    
    private static class PlayerInfo {
        private final String name;
        private final GameCharacter character;
        
        public PlayerInfo(String name, GameCharacter character) {
            this.name = name;
            this.character = character;
        }
        
        public String getName() { return name; }
        public GameCharacter getCharacter() { return character; }
    }
}