// GameServer/src/main/java/com/mycompany/gameserver/main/ServerMain.java
package com.mycompany.gameserver.main;

import com.mycompany.gameserver.network.GameServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMain {
    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);
    
    public static void main(String[] args) {
        logger.info("Iniciando servidor de juego...");
        GameServer server = new GameServer(5000);
        server.start();
    }
}
