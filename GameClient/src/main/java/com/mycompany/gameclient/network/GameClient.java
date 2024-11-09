package com.mycompany.gameclient.network;

import com.mycompany.gameclient.model.GameCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameClient {
    private static final Logger logger = LoggerFactory.getLogger(GameClient.class);
    private static final int DEFAULT_PORT = 5000;
    
    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected;
    private String playerName;
    private GameCharacter character;
    private final List<MessageListener> listeners;
    private final ExecutorService executorService;
    private Thread receiveThread;
    
    public GameClient() {
        this.serverPort = DEFAULT_PORT;
        this.connected = false;
        this.listeners = new ArrayList<>();
        this.executorService = Executors.newCachedThreadPool();
    }
    
    public void connect(String address, String playerName, GameCharacter character) throws IOException {
        this.serverAddress = address;
        this.playerName = playerName;
        this.character = character;
        
        try {
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(new OutputStreamWriter(
                socket.getOutputStream(), "UTF-8"), true);
            in = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), "UTF-8"));
            
            connected = true;
            
            // Enviar mensaje de registro
            sendRegistration();
            
            // Iniciar thread de recepción
            startReceiving();
            
            logger.info("Conectado al servidor: {}:{}", serverAddress, serverPort);
            notifyListeners("SYSTEM:Conectado al servidor");
            
        } catch (IOException e) {
            cleanup();
            throw new IOException("Error conectando al servidor: " + e.getMessage());
        }
    }
    
    private void sendRegistration() {
        if (connected) {
            String registrationMessage = String.format("REGISTER:%s:%s", 
                playerName, character.name());
            sendMessage(registrationMessage);
        }
    }
    
    private void startReceiving() {
        receiveThread = new Thread(() -> {
            try {
                String message;
                while (connected && (message = in.readLine()) != null) {
                    final String receivedMessage = message;
                    executorService.execute(() -> processMessage(receivedMessage));
                }
            } catch (IOException e) {
                if (connected) {
                    logger.error("Error recibiendo mensajes: {}", e.getMessage());
                    handleDisconnection("Error de conexión: " + e.getMessage());
                }
            }
        });
        receiveThread.start();
    }
    
    private void processMessage(String message) {
        try {
            if (message.startsWith("ERROR:")) {
                handleError(message.substring(6));
            } else if (message.startsWith("DISCONNECT:")) {
                handleDisconnection(message.substring(11));
            } else {
                notifyListeners(message);
            }
        } catch (Exception e) {
            logger.error("Error procesando mensaje '{}': {}", message, e.getMessage());
        }
    }
    
    private void handleError(String errorMessage) {
        logger.error("Error del servidor: {}", errorMessage);
        notifyListeners("ERROR:" + errorMessage);
    }
    
    private void handleDisconnection(String reason) {
        logger.info("Desconectado del servidor: {}", reason);
        notifyListeners("SYSTEM:Desconectado: " + reason);
        disconnect();
    }
    
    public void sendMessage(String message) {
        if (connected && out != null) {
            out.println(message);
            if (out.checkError()) {
                handleDisconnection("Error enviando mensaje");
            }
        }
    }
    
   public void sendChatMessage(String message) {
    if (connected) {
        // Enviar mensaje con el prefijo CHAT:
        sendMessage("CHAT:" + message);
    }
}
    public void rollDice() {
        if (connected) {
            sendMessage("ROLL_DICE");
        }
    }
    
    public void sendNumberGuess(int number) {
        if (connected) {
            sendMessage("NUMBER_GUESS:" + number);
        }
    }
    
    public void disconnect() {
        connected = false;
        cleanup();
        notifyListeners("SYSTEM:Desconectado del servidor");
    }
    
    private void cleanup() {
        connected = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            logger.error("Error cerrando conexión: {}", e.getMessage());
        }
        
        if (receiveThread != null) {
            receiveThread.interrupt();
        }
    }
    
    public void addMessageListener(MessageListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removeMessageListener(MessageListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(String message) {
        for (MessageListener listener : listeners) {
            try {
                listener.onMessageReceived(message);
            } catch (Exception e) {
                logger.error("Error en listener procesando mensaje: {}", e.getMessage());
            }
        }
    }
    
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public GameCharacter getCharacter() {
        return character;
    }
    
    public void setServerPort(int port) {
        this.serverPort = port;
    }
    
    // Métodos para acciones específicas del juego
    public void selectSpace(int spaceIndex) {
        if (connected) {
            sendMessage("SELECT_SPACE:" + spaceIndex);
        }
    }
    
    public void useSpecialItem(String itemType, String targetPlayer) {
        if (connected) {
            sendMessage("USE_ITEM:" + itemType + ":" + targetPlayer);
        }
    }
    
    public void submitGameAnswer(String gameType, String answer) {
        if (connected) {
            sendMessage("GAME_ANSWER:" + gameType + ":" + answer);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        cleanup();
        executorService.shutdown();
        super.finalize();
    }
}