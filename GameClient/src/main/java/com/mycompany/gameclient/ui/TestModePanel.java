package com.mycompany.gameclient.ui;

import javax.swing.*;
import java.awt.*;

public class TestModePanel extends JDialog {
    private int selectedValue = -1;
    private final JFrame parentFrame;
    
    public TestModePanel(JFrame parentFrame) {
        super(parentFrame, "Modo Prueba - Simular Dados", true);
        this.parentFrame = parentFrame;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Crear botones para valores posibles (1-10)
        for (int i = 1; i <= 10; i++) {
            JButton valueButton = new JButton(String.valueOf(i) + " espacios");
            final int value = i;
            valueButton.addActionListener(e -> {
                selectedValue = value;
                dispose();
            });
            mainPanel.add(valueButton);
        }
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> {
            selectedValue = -1;
            dispose();
        });
        bottomPanel.add(cancelButton);
        
        add(new JLabel("Selecciona cuántos espacios quieres avanzar:", 
            SwingConstants.CENTER), BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(parentFrame);
        setResizable(false);
    }
    
    public int getSelectedValue() {
        return selectedValue;
    }
}