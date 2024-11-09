// GameClient/src/main/java/com/mycompany/gameclient/ui/CharacterSelectionDialog.java
package com.mycompany.gameclient.ui;

import com.mycompany.gameclient.model.GameCharacter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CharacterSelectionDialog extends JDialog {
    private GameCharacter selectedCharacter;
    private boolean selectionMade = false;
    
    public CharacterSelectionDialog(JFrame parent) {
        super(parent, "Selecciona tu Personaje", true);
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel charactersPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        charactersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (GameCharacter character : GameCharacter.values()) {
            JButton characterButton = createCharacterButton(character);
            charactersPanel.add(characterButton);
        }
        
        add(new JLabel("Selecciona tu personaje:", SwingConstants.CENTER), BorderLayout.NORTH);
        add(new JScrollPane(charactersPanel), BorderLayout.CENTER);
        
        setSize(400, 500);
        setLocationRelativeTo(getParent());
    }
    
    private JButton createCharacterButton(GameCharacter character) {
        JButton button = new JButton(character.getName());
        button.setBackground(character.getColor());
        button.setForeground(isDarkColor(character.getColor()) ? Color.WHITE : Color.BLACK);
        
        button.addActionListener(e -> {
            selectedCharacter = character;
            selectionMade = true;
            dispose();
        });
        
        return button;
    }
    
    private boolean isDarkColor(Color color) {
        // Fórmula para determinar si un color es oscuro
        return (color.getRed() * 0.299 + 
                color.getGreen() * 0.587 + 
                color.getBlue() * 0.114) <= 186;
    }
    
    public GameCharacter getSelectedCharacter() {
        return selectedCharacter;
    }
    
    public boolean isSelectionMade() {
        return selectionMade;
    }
}