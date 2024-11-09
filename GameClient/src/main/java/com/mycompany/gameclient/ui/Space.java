// GameClient/src/main/java/com/mycompany/gameclient/ui/Space.java
package com.mycompany.gameclient.ui;

import com.mycompany.gameclient.model.SpaceType;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Space extends JPanel {
    private final int index;
    private SpaceType type;
    private final List<PlayerToken> players;
    private static final int PLAYER_TOKEN_SIZE = 20;
    private static final int PADDING = 5;
    
    public Space(int index) {
        this.index = index;
        this.type = SpaceType.EMPTY;
        this.players = new ArrayList<>();
        
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(80, 80));
        setLayout(null); // Usamos null layout para posicionar los tokens manualmente
    }
    
    public void setType(SpaceType type) {
        this.type = type;
        setBackground(type.getColor());
        repaint();
    }
    
    public void addPlayer(String name, Color color) {
        // Verificar si el jugador ya está en la casilla
        for (PlayerToken token : players) {
            if (token.getPlayerName().equals(name)) {
                return;
            }
        }
        
        PlayerToken token = new PlayerToken(name, color);
        players.add(token);
        repositionTokens();
        repaint();
    }
    
    public void removePlayer(String name) {
        players.removeIf(token -> token.getPlayerName().equals(name));
        repositionTokens();
        repaint();
    }
    
    private void repositionTokens() {
        // Calcular posiciones para los tokens en forma circular
        if (players.isEmpty()) return;
        
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - PLAYER_TOKEN_SIZE;
        
        for (int i = 0; i < players.size(); i++) {
            double angle = (2 * Math.PI * i) / players.size();
            int x = (int) (centerX + radius * Math.cos(angle)) - PLAYER_TOKEN_SIZE/2;
            int y = (int) (centerY + radius * Math.sin(angle)) - PLAYER_TOKEN_SIZE/2;
            
            players.get(i).setPosition(x, y);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dibujar número de casilla
        g2d.setColor(isDarkBackground() ? Color.WHITE : Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(String.valueOf(index), PADDING, PADDING + 12);
        
        // Dibujar tipo de casilla
        drawSpaceType(g2d);
        
        // Dibujar tokens de jugadores
        for (PlayerToken token : players) {
            token.draw(g2d);
        }
    }
    
    private void drawSpaceType(Graphics2D g2d) {
        String typeName = type.getName();
        FontMetrics fm = g2d.getFontMetrics();
        
        // Dividir el nombre en palabras si es muy largo
        String[] words = typeName.split(" ");
        int y = (getHeight() - (words.length * fm.getHeight())) / 2;
        
        for (String word : words) {
            int stringWidth = fm.stringWidth(word);
            int x = (getWidth() - stringWidth) / 2;
            g2d.drawString(word, x, y + fm.getAscent());
            y += fm.getHeight();
        }
    }
    
    private boolean isDarkBackground() {
        Color bg = type.getColor();
        return (bg.getRed() * 0.299 + bg.getGreen() * 0.587 + bg.getBlue() * 0.114) <= 186;
    }
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        repositionTokens();
    }
    
    // Clase interna para representar los tokens de los jugadores
    private static class PlayerToken {
        private final String playerName;
        private final Color color;
        private int x, y;
        
        public PlayerToken(String playerName, Color color) {
            this.playerName = playerName;
            this.color = color;
        }
        
        public String getPlayerName() {
            return playerName;
        }
        
        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public void draw(Graphics2D g2d) {
            // Dibujar borde negro
            g2d.setColor(Color.BLACK);
            g2d.fillOval(x-1, y-1, PLAYER_TOKEN_SIZE+2, PLAYER_TOKEN_SIZE+2);
            
            // Dibujar token con el color del jugador
            g2d.setColor(color);
            g2d.fillOval(x, y, PLAYER_TOKEN_SIZE, PLAYER_TOKEN_SIZE);
            
            // Dibujar borde interno
            g2d.setColor(color.brighter());
            g2d.drawOval(x+2, y+2, PLAYER_TOKEN_SIZE-4, PLAYER_TOKEN_SIZE-4);
        }
    }
}