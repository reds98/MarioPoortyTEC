package com.mycompany.gameserver.model;

import com.mycompany.gameserver.network.ClientHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StarEffect extends BaseSpaceEffect {
    private static final Logger logger = LoggerFactory.getLogger(StarEffect.class);
    
    public StarEffect(GameBoard gameBoard, TurnManager turnManager) {
        super(gameBoard, turnManager);
    }
    
    @Override
    public void executeEffect(ClientHandler client, int currentPosition) {
        logger.debug("Activando efecto estrella para jugador {}", client.getPlayerName());
        
        // Activar efecto en TurnManager y reiniciar el estado
        turnManager.setStarEffect(true);
        turnManager.resetStarEffectUsed();
        
        // Notificar efecto
        broadcastMessage("SPACE_EFFECT:STAR:" + client.getPlayerName());
        broadcastMessage("GAME_MESSAGE:" + client.getPlayerName() + 
            " ⭐ obtuvo una estrella. ¡Puede lanzar los dados otra vez!");
        
        // Deshabilitar controles para todos primero
        for (TurnManager.PlayerInfo player : turnManager.getPlayers()) {
            player.getClient().sendMessage("ENABLE_ROLL:false");
            player.getClient().sendMessage("ENABLE_TEST_MODE:false");
        }
        
        // Pequeña pausa para asegurar que los mensajes de deshabilitación se procesen
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            logger.error("Error en pausa de efecto estrella", e);
        }
        
        // Habilitar controles solo para el jugador actual
        client.sendMessage("ENABLE_ROLL:true");
        client.sendMessage("ENABLE_TEST_MODE:true");
    }
}
