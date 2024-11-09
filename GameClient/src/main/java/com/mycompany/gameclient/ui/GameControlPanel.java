package com.mycompany.gameclient.ui;

import com.mycompany.gameclient.model.GameCharacter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameControlPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(GameControlPanel.class);
     private JLabel turnLabel;
    private JLabel playerLabel;
    private JLabel statusLabel;
    private JButton rollButton;
    private JButton testModeButton; // Agregar esta variable
    private JPanel dicePanel;
    private JLabel dice1Label;
    private JLabel dice2Label;
    private JPanel playerListPanel;
    
    public GameControlPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior con información del turno
        JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        turnLabel = new JLabel("Esperando inicio del juego...");
        playerLabel = new JLabel("Tu personaje: -");
        statusLabel = new JLabel("Estado: -");
        
        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        topPanel.add(turnLabel);
        topPanel.add(playerLabel);
        topPanel.add(statusLabel);
        
        // Panel central con dados y botones
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        dicePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        dicePanel.setBorder(BorderFactory.createTitledBorder("Dados"));
        
        dice1Label = createDiceLabel();
        dice2Label = createDiceLabel();
        dicePanel.add(dice1Label);
        dicePanel.add(dice2Label);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        rollButton = new JButton("Lanzar Dados");
        testModeButton = new JButton("Modo Prueba");
        rollButton.setEnabled(false);
        testModeButton.setEnabled(false);
        
        buttonPanel.add(rollButton);
        buttonPanel.add(testModeButton);
        
        centerPanel.add(dicePanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Panel de jugadores
        playerListPanel = new JPanel();
        playerListPanel.setLayout(new BoxLayout(playerListPanel, BoxLayout.Y_AXIS));
        playerListPanel.setBorder(BorderFactory.createTitledBorder("Jugadores"));
        JScrollPane scrollPane = new JScrollPane(playerListPanel);
        scrollPane.setPreferredSize(new Dimension(200, 150));
        
        // Agregar todo al panel principal
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }
    
    private JLabel createDiceLabel() {
        JLabel label = new JLabel("?");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setPreferredSize(new Dimension(50, 50));
        return label;
    }
    
   
   public void enableTestMode(boolean enable) {
    if (testModeButton != null) {
        testModeButton.setEnabled(enable);
        logger.debug("Test mode enabled: {}", enable);
    }
}
    
    public void setRollButtonListener(ActionListener listener) {
        rollButton.addActionListener(listener);
    }
    
    public void setTestModeButtonListener(ActionListener listener) {
        testModeButton.addActionListener(listener);
    }
    
    public void updateDice(int dice1, int dice2) {
        dice1Label.setText(dice1 == 6 ? "X" : String.valueOf(dice1));
        dice2Label.setText(dice2 == 6 ? "X" : String.valueOf(dice2));
        
        // Colorear dados de castigo en rojo
        dice1Label.setForeground(dice1 == 6 ? Color.RED : Color.BLACK);
        dice2Label.setForeground(dice2 == 6 ? Color.RED : Color.BLACK);
    }
    
    public void resetDice() {
        dice1Label.setText("?");
        dice2Label.setText("?");
        dice1Label.setForeground(Color.BLACK);
        dice2Label.setForeground(Color.BLACK);
    }
    
    public void setCurrentTurn(String playerName, boolean isMyTurn) {
        turnLabel.setText("Turno de: " + playerName);
        if (isMyTurn) {
            turnLabel.setForeground(Color.BLUE);
            // No habilitamos los controles aquí, esperamos el mensaje ENABLE_ROLL
        } else {
            turnLabel.setForeground(Color.BLACK);
            // Asegurarnos de que los controles estén deshabilitados
            rollButton.setEnabled(false);
            testModeButton.setEnabled(false);
        }
    }

    public void enableButtons(boolean enable) {
        rollButton.setEnabled(enable);
        testModeButton.setEnabled(enable);
    }

  

    
    public void setPlayerCharacter(GameCharacter character) {
        playerLabel.setText("Tu personaje: " + character.getName());
    }
    
    public void setStatus(String status) {
        statusLabel.setText("Estado: " + status);
    }
    
    public void addPlayer(String name, GameCharacter character) {
        JPanel playerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Crear indicador de color del personaje
        JPanel colorIndicator = new JPanel();
        colorIndicator.setPreferredSize(new Dimension(15, 15));
        colorIndicator.setBackground(character.getColor());
        colorIndicator.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        JLabel nameLabel = new JLabel(name + " (" + character.getName() + ")");
        
        playerPanel.add(colorIndicator);
        playerPanel.add(nameLabel);
        
        playerListPanel.add(playerPanel);
        playerListPanel.revalidate();
        playerListPanel.repaint();
    }
    
    public void removePlayer(String name) {
        Component[] components = playerListPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Component[] subComponents = panel.getComponents();
                for (Component sub : subComponents) {
                    if (sub instanceof JLabel) {
                        JLabel label = (JLabel) sub;
                        if (label.getText().startsWith(name)) {
                            playerListPanel.remove(panel);
                            playerListPanel.revalidate();
                            playerListPanel.repaint();
                            return;
                        }
                    }
                }
            }
        }
    }
    
    public void clearPlayers() {
        playerListPanel.removeAll();
        playerListPanel.revalidate();
        playerListPanel.repaint();
    }
    
   public void enableRollButton(boolean enable) {
    if (rollButton != null) {
        rollButton.setEnabled(enable);
        logger.debug("Roll button enabled: {}", enable);
    }
}
}