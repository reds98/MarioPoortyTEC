// GameClient/src/main/java/com/mycompany/gameclient/ui/BoardPanel.java
package com.mycompany.gameclient.ui;

import com.mycompany.gameclient.model.SpaceType;
import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    private static final int SPACES = 26;
    private Space[] spaces;
    
        // Asegurarnos que el constructor use el nuevo tamaño de casilla
    public BoardPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(700, 500));
        initializeBoard();
    }

    public int getSpaceCount() {
    return spaces.length;
}

public void removePlayerFromSpace(String playerName, int spaceIndex) {
    if (spaceIndex >= 0 && spaceIndex < spaces.length) {
        spaces[spaceIndex].removePlayer(playerName);
    }
}

public void addPlayerToSpace(String playerName, Color playerColor, int spaceIndex) {
    if (spaceIndex >= 0 && spaceIndex < spaces.length) {
        spaces[spaceIndex].addPlayer(playerName, playerColor);
    }
}

    
    private void initializeBoard() {
        spaces = new Space[SPACES];
        
        // Calculamos las posiciones para formar un camino serpenteante
        int rows = 4;
        int spacesPerRow = 7;
        int spaceWidth = 70;
        int spaceHeight = 70;
        int padding = 10;
        
        for (int i = 0; i < SPACES; i++) {
            // Calculamos la fila y columna
            int row = i / spacesPerRow;
            int col = i % spacesPerRow;
            
            // Si es una fila par, invertimos la dirección
            if (row % 2 == 1) {
                col = spacesPerRow - 1 - col;
            }
            
            int x = col * (spaceWidth + padding) + padding;
            int y = row * (spaceHeight + padding) + padding;
            
            spaces[i] = new Space(i);
            spaces[i].setBounds(x, y, spaceWidth, spaceHeight);
            add(spaces[i]);
        }
    }
    
    // Método actualizado para usar SpaceType
    public void updateSpace(int index, SpaceType type) {
        if (index >= 0 && index < spaces.length) {
            spaces[index].setType(type);
        }
    }
    
    // Método para colocar un jugador en una casilla
    public void setPlayerPosition(String playerName, Color playerColor, int spaceIndex) {
        if (spaceIndex >= 0 && spaceIndex < spaces.length) {
            spaces[spaceIndex].addPlayer(playerName, playerColor);
            repaint();
        }
    }
}