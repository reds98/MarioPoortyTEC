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

public class JuegoDelTesoro extends JFrame {
    
    Random rand = new Random();
    int cas = rand.nextInt(3);
    private final int MATRIZ_SIZE = 10 + cas*5; // Cambia esto a 15 o 20 según necesites
    private boolean[][] tesoro;
    private int bombasRestantes = 7;
    private JButton[][] botones;
    private Random random;
    private String tipoBombaSeleccionada = "Simple"; // Tipo de bomba seleccionada
    private JLabel contadorBombas;

    public JuegoDelTesoro() {
        random = new Random();
        tesoro = new boolean[MATRIZ_SIZE][MATRIZ_SIZE];
        botones = new JButton[MATRIZ_SIZE][MATRIZ_SIZE];

        colocarTesoro();
        inicializarGUI();
    }

    private void inicializarGUI() {
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
        setTitle("Bomber Mario");
        setLayout(new BorderLayout());

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(MATRIZ_SIZE, MATRIZ_SIZE));

        // Inicializa los botones de la matriz
        for (int i = 0; i < MATRIZ_SIZE; i++) {
            for (int j = 0; j < MATRIZ_SIZE; j++) {
                botones[i][j] = new JButton();
                botones[i][j].setPreferredSize(new Dimension(50, 50));
                botones[i][j].setActionCommand(i + "," + j);
                botones[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (bombasRestantes > 0) {
                            String[] coords = e.getActionCommand().split(",");
                            int x = Integer.parseInt(coords[0]);
                            int y = Integer.parseInt(coords[1]);
                            colocarBomba(x, y);
                        } else {
                            JOptionPane.showMessageDialog(null, "No tienes más bombas!");
                        }
                    }
                });
                panelBotones.add(botones[i][j]);
            }
        }

        // Contador de bombas
        contadorBombas = new JLabel("Bombas restantes: " + bombasRestantes);
        add(contadorBombas, BorderLayout.NORTH);

        // Botones para seleccionar tipo de bomba
        JPanel panelSeleccion = new JPanel();
        String[] tiposDeBombas = {"Simple", "Dobles", "Cruz", "Linea"};
        for (String tipo : tiposDeBombas) {
            JButton botonTipo = new JButton(tipo);
            botonTipo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tipoBombaSeleccionada = tipo;
                    //JOptionPane.showMessageDialog(null, "Tipo de bomba seleccionado: " + tipoBombaSeleccionada);
                }
            });
            panelSeleccion.add(botonTipo);
        }

        add(panelSeleccion, BorderLayout.SOUTH);
        add(panelBotones, BorderLayout.CENTER);

        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void colocarTesoro() {
        int x = random.nextInt(MATRIZ_SIZE - 1);
        int y = random.nextInt(MATRIZ_SIZE - 1);
        
        // Coloca el tesoro en un 2x2
        tesoro[x][y] = true;
        tesoro[x][y + 1] = true;
        tesoro[x + 1][y] = true;
        tesoro[x + 1][y + 1] = true;
    }

    private void colocarBomba(int x, int y) {
        bombasRestantes--;
        contadorBombas.setText("Bombas restantes: " + bombasRestantes);
        botones[x][y].setEnabled(false);

        // Se revela la casilla
        if (tesoro[x][y]) {
            botones[x][y].setBackground(Color.GREEN); // Color para casillas de tesoro
        } else {
            botones[x][y].setBackground(Color.RED);
        }
        
        // Explota según el tipo de bomba seleccionada
        switch (tipoBombaSeleccionada) {
            case "Simple":
                explotarSimple(x, y);
                break;
            case "Dobles":
                explotarDobles(x, y);
                break;
            case "Cruz":
                explotarCruz(x, y);
                break;
            case "Linea":
                explotarLinea(x, y);
                break;
        }

        checkVictory();

        if (bombasRestantes == 0) {
            JOptionPane.showMessageDialog(null, "No te quedan más bombas. Fin del juego.");
            cerrarJuego();
        }
    }

    private void explotarSimple(int x, int y) {
        revelarCasilla(x, y);
    }

    private void explotarDobles(int x, int y) {
        if (x < MATRIZ_SIZE - 1 && y < MATRIZ_SIZE - 1) {
            revelarCasilla(x, y);
            revelarCasilla(x, y + 1);
            revelarCasilla(x + 1, y);
            revelarCasilla(x + 1, y + 1);
        }
    }

    private void explotarCruz(int x, int y) {
        revelarCasilla(x, y); // Centro
        if (x > 0) revelarCasilla(x - 1, y); // Arriba
        if (x < MATRIZ_SIZE - 1) revelarCasilla(x + 1, y); // Abajo
        if (y > 0) revelarCasilla(x, y - 1); // Izquierda
        if (y < MATRIZ_SIZE - 1) revelarCasilla(x, y + 1); // Derecha
    }

    private void explotarLinea(int x, int y) {
    Random rand = new Random();
    int direccion = rand.nextInt(4); // 0: Arriba, 1: Abajo, 2: Derecha, 3: Izquierda

    switch (direccion) {
        case 0: // Arriba
            if (x >= 3) {
                for (int i = 0; i < 4; i++) {
                    revelarCasilla(x - i, y);
                }
            }
            break;
        case 1: // Abajo
            if (x <= MATRIZ_SIZE - 4) {
                for (int i = 0; i < 4; i++) {
                    revelarCasilla(x + i, y);
                }
            }
            break;
        case 2: // Derecha
            if (y <= MATRIZ_SIZE - 4) {
                for (int i = 0; i < 4; i++) {
                    revelarCasilla(x, y + i);
                }
            }
            break;
        case 3: // Izquierda
            if (y >= 3) {
                for (int i = 0; i < 4; i++) {
                    revelarCasilla(x, y - i);
                }
            }
            break;
        }
    }

    private void revelarCasilla(int x, int y) {
        if (x >= 0 && x < MATRIZ_SIZE && y >= 0 && y < MATRIZ_SIZE) {
            botones[x][y].setEnabled(false);
            if (tesoro[x][y]) {
                botones[x][y].setBackground(Color.GREEN); // Color para casillas de tesoro
            } else {
                botones[x][y].setBackground(Color.RED); // Color para casillas no de tesoro
            }
        }
    }

    private void checkVictory() {
        boolean gano = true;
        for (int i = 0; i < MATRIZ_SIZE; i++) {
            for (int j = 0; j < MATRIZ_SIZE; j++) {
                // Verifica si todas las casillas del tesoro han sido reveladas
                if (tesoro[i][j] && botones[i][j].getBackground() != Color.GREEN) {
                    gano = false;
                    
                }
            }
        }
        if (gano) {
            JOptionPane.showMessageDialog(null, "¡Has encontrado el tesoro!");
            cerrarJuego();
        }
    }

    private void cerrarJuego() {
        dispose(); // Cierra la ventana
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JuegoDelTesoro();
            }
        });
    }
}
