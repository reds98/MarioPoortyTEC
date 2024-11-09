// GameServer/src/main/java/com/mycompany/gameserver/model/TubeEffect.java
package com.mycompany.gameserver.model;

import com.mycompany.gameserver.network.ClientHandler;

public class TubeEffect extends BaseSpaceEffect {
    private final SpaceType tubeType;
    
    public TubeEffect(GameBoard gameBoard, TurnManager turnManager, SpaceType tubeType) {
        super(gameBoard, turnManager);
        this.tubeType = tubeType;
    }
    
    @Override
    public void executeEffect(ClientHandler client, int currentPosition) {
        SpaceType nextTube = getNextTube();
        if (nextTube == null) return;
        
        int nextPosition = gameBoard.findFirstSpace(nextTube);
        if (nextPosition != -1) {
            // Primero enviamos el mensaje de efecto
            broadcastMessage("GAME_MESSAGE:" + client.getPlayerName() + 
                " se transportó de " + tubeType.getName() + " a " + nextTube.getName());
            
            // Luego actualizamos la posición
            updatePlayerPosition(client, nextPosition);
            
            // Finalmente, notificamos el efecto visual
            broadcastMessage("TUBE_EFFECT:" + tubeType.name() + ":" + nextTube.name());
        }
    }
    
    private SpaceType getNextTube() {
        switch (tubeType) {
            case TUBE_1: return SpaceType.TUBE_2;
            case TUBE_2: return SpaceType.TUBE_3;
            case TUBE_3: return SpaceType.TUBE_1;
            default: return null;
        }
    }
}