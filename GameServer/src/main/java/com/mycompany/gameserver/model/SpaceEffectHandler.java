package com.mycompany.gameserver.model;

import com.mycompany.gameserver.network.ClientHandler;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceEffectHandler {
    private final Map<SpaceType, SpaceEffect> effects;
    private final GameBoard gameBoard;
    private final TurnManager turnManager;
    private static final Logger logger = LoggerFactory.getLogger(SpaceEffectHandler.class);
    
    public SpaceEffectHandler(GameBoard gameBoard, TurnManager turnManager) {
        this.gameBoard = gameBoard;
        this.turnManager = turnManager;
        this.effects = new EnumMap<>(SpaceType.class);
        
        // Registrar efectos
        effects.put(SpaceType.TUBE_1, new TubeEffect(gameBoard, turnManager, SpaceType.TUBE_1));
        effects.put(SpaceType.TUBE_2, new TubeEffect(gameBoard, turnManager, SpaceType.TUBE_2));
        effects.put(SpaceType.TUBE_3, new TubeEffect(gameBoard, turnManager, SpaceType.TUBE_3));
        effects.put(SpaceType.STAR, new StarEffect(gameBoard, turnManager));
    }
    
    public void handleSpaceEffect(ClientHandler client, SpaceType spaceType, int currentPosition) {
        SpaceEffect effect = effects.get(spaceType);
        if (effect != null) {
            logger.debug("Ejecutando efecto {} para jugador {} en posición {}", 
                spaceType, client.getPlayerName(), currentPosition);
            effect.executeEffect(client, currentPosition);
        }
    }
    public void handleTailMove(ClientHandler client, int targetPosition) {
    int currentPosition = turnManager.getPlayerPosition(client);
    
    if (Math.abs(targetPosition - currentPosition) <= 3) {
        turnManager.setPlayerPosition(client, targetPosition);
        turnManager.broadcastMessage("GAME_MESSAGE:" + client.getPlayerName() + 
            " usó la Cola para moverse a la casilla " + targetPosition);
        turnManager.broadcastMessage("PLAYER_MOVE:" + client.getPlayerName() + ":" + targetPosition);
    }
}
    
}