/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.MiniGames;

/**
 *
 * @author jaria
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.awt.Font;

public class JuegoCartas extends JFrame {
    private JButton cartaButton;
    private Carta cartaActual; //La carta obtenida queda aqui
    
    public JuegoCartas() throws IOException {
        setTitle("Juego de Cartas");
        setSize(343, 488);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Crear el botón de la carta (con la carta volteada al inicio)
        cartaButton = new JButton();
        Image img = ImageIO.read(new File("assests\\PNG_cartas\\card_back.png"));
        img = img.getScaledInstance(343, 488,Image.SCALE_DEFAULT);
        cartaButton.setIcon(new ImageIcon(img));
        cartaButton.setFocusPainted(false);

        
        // Añadir el botón y la etiqueta a la ventana
        add(cartaButton, BorderLayout.CENTER);

        // Manejo del evento de clic sobre la carta
        cartaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cartaActual == null) {
                    // Genera una carta aleatoria
                    cartaActual = Mazo.obtenerCartaAleatoria();
                    Image img = null;
                    try {
                        img = ImageIO.read(new File("assests\\PNG_cartas\\"+ cartaActual.toString() +".png"));
                    } catch (IOException ex) {
                        Logger.getLogger(JuegoCartas.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    img = img.getScaledInstance(330, 460,Image.SCALE_DEFAULT);
                    cartaButton.setIcon(new ImageIcon(img));
                    cartaButton.setDisabledIcon(new ImageIcon(img));
                    cartaButton.setEnabled(false);
                }
            }
        });
        
        // Hacer visible la ventana
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    // Clase interna Carta
    static class Carta {
        private String valor;
        private String palo;
        
        public Carta(String valor, String palo) {
            this.valor = valor;
            this.palo = palo;
        }
        
        @Override
        public String toString() {
            return valor + "_of_" + palo;
        }
    }

    // Clase interna Mazo
    static class Mazo {
        private static final String[] VALORES = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};
        private static final String[] PALOS = {"hearts", "diamonds", "clubs", "spades"};
        private static ArrayList<Carta> mazo = new ArrayList<>();
        
        static {
            // Llenar el mazo con las 52 cartas
            for (String palo : PALOS) {
                for (String valor : VALORES) {
                    mazo.add(new Carta(valor, palo));
                }
            }
            // Mezclar el mazo
            //Collections.shuffle(mazo);
        }
        
        // Método para obtener una carta aleatoria
        public static Carta obtenerCartaAleatoria() {
            Random rand = new Random();
            return mazo.get(rand.nextInt(mazo.size())); // Devuelve una carta aleatoria
        }
    }
    
    public static void main(String[] args) {
        // Crear la ventana del juego
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new JuegoCartas();
                } catch (IOException ex) {
                    Logger.getLogger(JuegoCartas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}

