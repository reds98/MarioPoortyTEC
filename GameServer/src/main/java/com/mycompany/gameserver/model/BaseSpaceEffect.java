package com.mycompany.gameserver.model;

import com.mycompany.gameserver.network.ClientHandler;

public abstract class BaseSpaceEffect implements SpaceEffect {
    protected final GameBoard gameBoard;
    protected final TurnManager turnManager;
    
    public BaseSpaceEffect(GameBoard gameBoard, TurnManager turnManager) {
        this.gameBoard = gameBoard;
        this.turnManager = turnManager;
    }
    
    protected void broadcastMessage(String message) {
        turnManager.broadcastMessage(message);
    }
    
    protected void updatePlayerPosition(ClientHandler client, int newPosition) {
        turnManager.setPlayerPosition(client, newPosition);
        broadcastMessage("PLAYER_MOVE:" + client.getPlayerName() + ":" + newPosition);
    }
}