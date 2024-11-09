package com.mycompany.gameserver.network;

import java.io.*;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private final GameServer gameServer;
    private PrintWriter out;
    private BufferedReader in;
    private String playerName;
    private boolean running;
    
    public ClientHandler(Socket socket, GameServer server) {
        this.clientSocket = socket;
        this.gameServer = server;
        this.running = true;
        this.playerName = "Unknown";
        
        try {
            out = new PrintWriter(new OutputStreamWriter(
                clientSocket.getOutputStream(), "UTF-8"), true);
            in = new BufferedReader(new InputStreamReader(
                clientSocket.getInputStream(), "UTF-8"));
        } catch (IOException e) {
            logger.error("Error inicializando streams: {}", e.getMessage());
            running = false;
        }
    }
    
    @Override
    public void run() {
        try {
            String inputLine;
            while (running && (inputLine = in.readLine()) != null) {
                try {
                    // Procesar el mensaje recibido
                    handleMessage(inputLine);
                } catch (Exception e) {
                    logger.error("Error procesando mensaje '{}': {}", 
                        inputLine, e.getMessage());
                    sendMessage("ERROR:" + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Error en la comunicación con el cliente: {}", e.getMessage());
        } finally {
            cleanup();
        }
    }
    
    private void handleMessage(String message) {
        if (message.startsWith("REGISTER:")) {
            // El primer mensaje debe ser de registro
            processRegistration(message);
        } else {
            // Todos los demás mensajes van al servidor
            gameServer.handleMessage(this, message);
        }
    }
    
    private void processRegistration(String message) {
        try {
            String[] parts = message.split(":", 3);
            if (parts.length != 3) {
                throw new IllegalArgumentException("Formato de registro inválido");
            }
            
            this.playerName = parts[1];
            logger.info("Jugador registrado: {}", playerName);
            
            // El servidor manejará la validación del nombre y personaje
            gameServer.handleMessage(this, message);
        } catch (Exception e) {
            logger.error("Error en registro: {}", e.getMessage());
            sendMessage("ERROR:Registro fallido: " + e.getMessage());
            cleanup();
        }
    }
    
    public void sendMessage(String message) {
        try {
            if (out != null && !clientSocket.isClosed()) {
                out.println(message);
                if (out.checkError()) {
                    logger.error("Error enviando mensaje a {}", playerName);
                    cleanup();
                }
            }
        } catch (Exception e) {
            logger.error("Error enviando mensaje: {}", e.getMessage());
            cleanup();
        }
    }
    
    private void cleanup() {
        running = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (!clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            logger.error("Error cerrando conexión: {}", e.getMessage());
        } finally {
            gameServer.removeClient(this);
        }
    }
    
    public void disconnect(String reason) {
        sendMessage("DISCONNECT:" + reason);
        cleanup();
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public boolean isConnected() {
        return running && !clientSocket.isClosed();
    }
    
    public String getClientAddress() {
        return clientSocket.getInetAddress().getHostAddress();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ClientHandler other = (ClientHandler) obj;
        return clientSocket == other.clientSocket;
    }
    
    @Override
    public int hashCode() {
        return clientSocket.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("Client[%s@%s]", 
            playerName, getClientAddress());
    }
}