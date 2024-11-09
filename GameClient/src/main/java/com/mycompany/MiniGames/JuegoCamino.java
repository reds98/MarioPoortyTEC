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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JuegoCamino extends JFrame {
    private final int filas = 6;
    private final int columnas = 3;
    private int[] camino;
    private int nivelActual;
    private int intentos;
    private List<JButton> botones;

    public JuegoCamino() {
        Font marioFuente = null;
        try {
        //create the font to use. Specify the size!
        marioFuente = Font.createFont(Font.TRUETYPE_FONT, new File("assests\\Fuentes\\SuperMario256.ttf")).deriveFont(12f);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //register the font
        ge.registerFont(marioFuente);
        } catch (IOException|FontFormatException e) {
        //Handle exception
        }
        // Configuración de la ventana
        UIManager.put("Label.font",marioFuente);
        setTitle("Juego del Camino");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(filas + 1, columnas));

        botones = new ArrayList<>();
        reiniciarJuego();
        crearBotones();

        setVisible(true);
    }

    private void crearBotones() {
        for (int i = filas - 1; i >= 0; i--) { // Se añaden botones desde abajo hacia arriba
            for (int j = 0; j < columnas; j++) {
                JButton boton = new JButton();
                botones.add(boton); // Guardar referencia del botón
                final int fila = i;
                final int col = j;

                boton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (nivelActual == fila) {
                            if (camino[fila] == col) {
                                nivelActual++;
                                habilitarBotones();
                                if (nivelActual == filas) {
                                    JOptionPane.showMessageDialog(null, "¡Ganaste!");
                                    cerrarJuego();
                                }
                            } else {
                                intentos--;
                                JOptionPane.showMessageDialog(null, "Incorrecto. Intentos restantes: " + intentos);
                                if (intentos == 0) {
                                    JOptionPane.showMessageDialog(null, "Perdiste. El camino era: " + mostrarCamino());
                                    cerrarJuego();
                                } else {
                                    nivelActual = 0; // Reinicia al primer nivel
                                    habilitarBotones();
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Debes seguir el camino en el nivel actual.");
                        }
                    }
                });

                add(boton);
            }
        }
        habilitarBotones(); // Habilita botones para el nivel inicial
    }

    private int[] generarCamino() {
        Random rand = new Random();
        int[] camino = new int[filas];
        for (int i = 0; i < filas; i++) {
            camino[i] = rand.nextInt(columnas);
        }
        return camino;
    }

    private void reiniciarJuego() {
        camino = generarCamino();
        nivelActual = 0; // Comienza desde el primer nivel (más bajo)
        intentos = 3; // Total de intentos
        imprimirCamino(); // Imprime el camino en la consola para pruebas
    }
    
    private void imprimirCamino() {
        System.out.print("Camino correcto: ");
        for (int col : camino) {
            System.out.print(col + " ");
        }
        System.out.println();
    }
    
    private void habilitarBotones() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                JButton boton = botones.get(i * columnas + j);
                // Habilitar solo el nivel actual, desde la parte inferior
                boton.setEnabled(i == (filas - 1 - nivelActual)); // Mostrar solo el botón del nivel actual
            }
        }
    }

    private String mostrarCamino() {
        StringBuilder sb = new StringBuilder();
        for (int col : camino) {
            sb.append(col).append(" ");
        }
        return sb.toString().trim();
    }

    private void cerrarJuego() {
        System.exit(0); // Cierra la aplicación
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(JuegoCamino::new);
    }
}
