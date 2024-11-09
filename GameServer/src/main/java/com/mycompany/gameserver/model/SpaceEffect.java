package com.mycompany.gameserver.model;

import com.mycompany.gameserver.network.ClientHandler;

public interface SpaceEffect {
    void executeEffect(ClientHandler client, int currentPosition);
}