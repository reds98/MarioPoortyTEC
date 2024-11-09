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
import java.util.Random;

public class SopaDeLetras extends JFrame {
    Random rand = new Random();
    int cas = rand.nextInt(3);
    private final int N = 10 + cas*5; // Tamaño de la matriz
    private final String[] todasLasPalabras = {"JAVA", "CODIGO", "SOPA", "LETTERS", "PROGRAMACION"};
    private final String[] palabras = new String[4];
    private final char[][] matriz = new char[N][N];
    private final boolean[] palabrasEncontradas = new boolean[palabras.length];
    private final JLabel tiempoLabel;
    private final JLabel palabrasLabel; // Para mostrar las palabras a encontrar    
    private final Timer timer;
    private int tiempoRestante = 120; // 2 minutos en segundos
    private String palabraActual; // Para almacenar la palabra que se está buscando
    private int indexActual = 0; // Índice para la letra actual de la palabra
    private int lastRow, lastCol; // Para almacenar la última posición presionada

    public SopaDeLetras() {
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
        UIManager.put("Button.font",marioFuente);
        UIManager.put("Label.font",marioFuente);
        setTitle("Sopa de Letras");
        setSize(800, 800); // Ajustar tamaño de la ventana
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        
        tiempoLabel = new JLabel("Tiempo restante: " + tiempoRestante + "s");
        add(tiempoLabel, BorderLayout.NORTH);

        palabrasLabel = new JLabel("Palabras a encontrar: ");
        add(palabrasLabel, BorderLayout.SOUTH);
        
        JPanel panel = new JPanel();
        
        panel.setLayout(new GridLayout(N, N));
        crearMatriz();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                JButton boton = new JButton(String.valueOf(matriz[i][j]));
                boton.setFocusPainted(false);
                boton.setContentAreaFilled(false);
                boton.setActionCommand(i + "," + j);
                boton.setMargin( new Insets(5, 0, 5, 5) );
                boton.addActionListener(new BotonListener());
                panel.add(boton);
            }
        }
        add(panel, BorderLayout.CENTER);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tiempoRestante > 0) {
                    tiempoRestante--;
                    tiempoLabel.setText("Tiempo restante: " + tiempoRestante + "s");
                } else {
                    timer.stop();
                    JOptionPane.showMessageDialog(null, "¡Tiempo terminado! No ganaste.");
                    System.exit(0);
                }
            }
        });

        timer.start();
        actualizarPalabrasLabel();
    }

    private void crearMatriz() {
        // Inicializa la matriz con espacios
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matriz[i][j] = ' ';
            }
        }
        seleccionarPalabras();  
        colocarPalabras();
        llenarConLetrasAleatorias();
    }
    
    private void seleccionarPalabras() {
        Random rand = new Random();
        boolean[] seleccionadas = new boolean[todasLasPalabras.length];
        int count = 0;

        while (count < palabras.length) {
            int index = rand.nextInt(todasLasPalabras.length);
            if (!seleccionadas[index]) {
                palabras[count] = todasLasPalabras[index];
                seleccionadas[index] = true;
                count++;
            }
        }
    }

    private void llenarConLetrasAleatorias() {
        Random rand = new Random();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (matriz[i][j] == ' ') {
                    matriz[i][j] = (char) ('A' + rand.nextInt(26)); // Letras aleatorias de A a Z
                }
            }
        }
    }

    private void colocarPalabras() {
        Random rand = new Random();
        for (String palabra : palabras) {
            boolean colocada = false;
            while (!colocada) {
                int fila = rand.nextInt(N);
                int col = rand.nextInt(N);
                int direccion = rand.nextInt(2); // 0: horizontal, 1: vertical

                if (direccion == 0) { // Horizontal
                    if (col + palabra.length() <= N) {
                        boolean puedeColocar = true;
                        for (int i = 0; i < palabra.length(); i++) {
                            if (matriz[fila][col + i] != ' ') {
                                puedeColocar = false;
                                break;
                            }
                        }
                        if (puedeColocar) {
                            for (int i = 0; i < palabra.length(); i++) {
                                matriz[fila][col + i] = palabra.charAt(i);
                            }
                            colocada = true;
                        }
                    }
                } else { // Vertical
                    if (fila + palabra.length() <= N) {
                        boolean puedeColocar = true;
                        for (int i = 0; i < palabra.length(); i++) {
                            if (matriz[fila + i][col] != ' ') {
                                puedeColocar = false;
                                break;
                            }
                        }
                        if (puedeColocar) {
                            for (int i = 0; i < palabra.length(); i++) {
                                matriz[fila + i][col] = palabra.charAt(i);
                            }
                            colocada = true;
                        }
                    }
                }
            }
        }
    }

    private class BotonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] coords = e.getActionCommand().split(",");
            int i = Integer.parseInt(coords[0]);
            int j = Integer.parseInt(coords[1]);

            // Verifica si se ha encontrado la primera letra de una palabra
            for (int k = 0; k < palabras.length; k++) {
                if (!palabrasEncontradas[k] && matriz[i][j] == palabras[k].charAt(0)) {
                    palabraActual = palabras[k];
                    indexActual = 1; // Reiniciar el índice
                    lastRow = i; // Guardar la última posición
                    lastCol = j;
                    return;
                }
            }

            // Verifica si se presiona la letra siguiente de la palabra actual
            if (palabraActual != null && indexActual < palabraActual.length()) {
                // Verificar si la posición es adyacente
                if (esAdyacente(i, j)) {
                    if (matriz[i][j] == palabraActual.charAt(indexActual)) {
                        indexActual++;
                        lastRow = i; // Actualizar última posición
                        lastCol = j;
                        if (indexActual == palabraActual.length()) {
                            palabrasEncontradas[buscarPalabraActual()] = true;
                            JOptionPane.showMessageDialog(null, "¡Encontraste la palabra: " + palabraActual + "!");
                            palabraActual = null; // Reiniciar la palabra actual
                            indexActual = 0; // Reiniciar el índice
                            actualizarPalabrasLabel();
                            if (todasLasPalabrasEncontradas()) {
                                timer.stop();
                                JOptionPane.showMessageDialog(null, "¡Ganaste!");
                                System.exit(0);
                            }
                        }
                    } else {
                        // Reiniciar si se presiona una letra incorrecta
                        palabraActual = null;
                        indexActual = 0;
                    }
                } else {
                    // Reiniciar si la posición no es adyacente
                    palabraActual = null;
                    indexActual = 0;
                }
            }
        }

        private boolean esAdyacente(int i, int j) {
            // Comprueba si (i, j) es adyacente a (lastRow, lastCol)
            return (Math.abs(i - lastRow) == 1 && j == lastCol) || (Math.abs(j - lastCol) == 1 && i == lastRow);
        }

        private int buscarPalabraActual() {
            for (int k = 0; k < palabras.length; k++) {
                if (palabras[k].equals(palabraActual)) {
                    return k;
                }
            }
            return -1; // No encontrada
        }
    }
    
    private void actualizarPalabrasLabel() {
        StringBuilder sb = new StringBuilder("Palabras a encontrar: ");
        for (int i = 0; i < palabras.length; i++) {
            if (palabrasEncontradas[i]) {
                sb.append(palabras[i]).append("[X] \n ");
            } else {
                sb.append(palabras[i]).append("[ ] \n ");
            }
        }
        palabrasLabel.setText(sb.toString());
    }

    private boolean todasLasPalabrasEncontradas() {
        for (boolean encontrada : palabrasEncontradas) {
            if (!encontrada) return false;
        }
        return true;
    }

    public boolean run() {
        setVisible(true);
        return false; // El valor por defecto es "no ganado", el resultado se gestiona en el juego.
    }

    public static void main(String[] args) {
        SopaDeLetras juego = new SopaDeLetras();
        juego.run();
    }
}

