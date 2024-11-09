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

public class EncerrarGatoJuego extends JFrame {
    private static final int TAMAÑO = 11;
    private JButton[][] botones = new JButton[TAMAÑO][TAMAÑO];
    private int gatoX, gatoY;
    private boolean juegoGanado = false;

    public EncerrarGatoJuego() {
        setTitle("Encierra al Gato");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(TAMAÑO, TAMAÑO));

        inicializarBotones();
        colocarGato();

        setVisible(true);
    }

    private void inicializarBotones() {
        for (int i = 0; i < TAMAÑO; i++) {
            for (int j = 0; j < TAMAÑO; j++) {
                botones[i][j] = new JButton();
                botones[i][j].setBackground(Color.LIGHT_GRAY);
                botones[i][j].setPreferredSize(new Dimension(50, 50));
                botones[i][j].addActionListener(new BotonClickListener(i, j));
                add(botones[i][j]);
            }
        }
    }

    private void colocarGato() {
        gatoX = TAMAÑO / 2;
        gatoY = TAMAÑO / 2;
        actualizarPosicionGato();
    }

    private void actualizarPosicionGato() {
        for (int i = 0; i < TAMAÑO; i++) {
            for (int j = 0; j < TAMAÑO; j++) {
                if (i == gatoX && j == gatoY) {
                    botones[i][j].setText("😺");
                } else {
                    botones[i][j].setText("");
                }
            }
        }
    }

    private class BotonClickListener implements ActionListener {
        private int x, y;

        public BotonClickListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!juegoGanado && botones[x][y].getBackground() != Color.RED) {
                botones[x][y].setBackground(Color.RED);
                moverGato();
                verificarEstadoDelJuego();
            }
        }
    }

    private void moverGato() {
        if (juegoGanado) return;

        // Posiciones posibles para mover el gato
        int[][] movimientos = {
            {gatoX - 1, gatoY}, // Arriba
            {gatoX + 1, gatoY}, // Abajo
            {gatoX, gatoY - 1}, // Izquierda
            {gatoX, gatoY + 1}  // Derecha
        };

        // Elegir la mejor opción que no esté bloqueada
        int nuevaX = gatoX, nuevaY = gatoY;
        for (int[] movimiento : movimientos) {
            int x = movimiento[0];
            int y = movimiento[1];
            if (x >= 0 && x < TAMAÑO && y >= 0 && y < TAMAÑO && botones[x][y].getBackground() != Color.RED) {
                // Si la posición es válida y no está bloqueada, mover el gato
                if (nuevaX == gatoX && nuevaY == gatoY) {
                    nuevaX = x;
                    nuevaY = y;
                } else {
                    // Comparar distancias al borde
                    if (distanciaAlBorde(x, y) < distanciaAlBorde(nuevaX, nuevaY)) {
                        nuevaX = x;
                        nuevaY = y;
                    }
                }
            }
        }

        gatoX = nuevaX;
        gatoY = nuevaY;

        actualizarPosicionGato();
    }

    private int distanciaAlBorde(int x, int y) {
        return Math.min(Math.min(x, TAMAÑO - 1 - x), Math.min(y, TAMAÑO - 1 - y));
    }

    private void verificarEstadoDelJuego() {
        // Verificar si el gato escapa por los bordes
        if (gatoX == 0 || gatoX == TAMAÑO - 1 || gatoY == 0 || gatoY == TAMAÑO - 1) {
            JOptionPane.showMessageDialog(this, "¡El gato ha escapado! ¡Perdiste!");
            juegoGanado = false;
            System.exit(0);
        }

        // Verificar si el gato está encerrado
        if (estaElGatoAtrapado()) {
            JOptionPane.showMessageDialog(this, "¡Has encerrado al gato! ¡Ganaste!");
            juegoGanado = true;
        }
    }

    private boolean estaElGatoAtrapado() {
        return (gatoX == 0 || botones[gatoX - 1][gatoY].getBackground() == Color.RED) &&
               (gatoX == TAMAÑO - 1 || botones[gatoX + 1][gatoY].getBackground() == Color.RED) &&
               (gatoY == 0 || botones[gatoX][gatoY - 1].getBackground() == Color.RED) &&
               (gatoY == TAMAÑO - 1 || botones[gatoX][gatoY + 1].getBackground() == Color.RED);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EncerrarGatoJuego::new);
    }
}

