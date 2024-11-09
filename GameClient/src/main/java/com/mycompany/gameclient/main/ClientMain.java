/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gameclient.main;

import com.mycompany.gameclient.ui.GameClientWindow;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMain {
    private static final Logger logger = LoggerFactory.getLogger(ClientMain.class);
    
    public static void main(String[] args) {
        // Usar el Look and Feel del sistema
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            logger.error("Failed to initialize LaF");
        }
        
        SwingUtilities.invokeLater(() -> {
            GameClientWindow client = new GameClientWindow();
            client.setVisible(true);
        });
    }
}