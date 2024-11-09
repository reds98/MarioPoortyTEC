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
import javax.swing.plaf.metal.MetalButtonUI;

public class JuegoMonedas extends JFrame {
    private static final int TAMAÑO = 25;
    private JButton[][] botones = new JButton[TAMAÑO][TAMAÑO];
    private int[][] valoresMonedas = new int[TAMAÑO][TAMAÑO];
    private int puntajeTotal = 0;
    private int tiempoLimite;
    private int tiempoRestante;
    private JLabel etiquetaTiempo;
    private Timer temporizador;

    public JuegoMonedas() {
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
        setTitle("Juego de Monedas");
        setLayout(new BorderLayout());
        crearBotones();
        crearPanelSuperior();
        inicializarJuego();
        iniciarJuego();
        
    }

    private void crearPanelSuperior() {
        etiquetaTiempo = new JLabel("Tiempo restante: " + tiempoLimite + " segundos", SwingConstants.CENTER);
        etiquetaTiempo.setFont(new Font("Arial", Font.BOLD, 16));
        add(etiquetaTiempo, BorderLayout.NORTH);
    }

    private void inicializarJuego() {
        Random rand = new Random();
        // Llenar la matriz con monedas buenas y malas
        int monedasBuenas = 0, monedasMalas = 0;
        
        while (monedasBuenas < 312) { // 312 = (25 * 25) / 2
            int fila = rand.nextInt(TAMAÑO);
            int col = rand.nextInt(TAMAÑO);
            if (valoresMonedas[fila][col] == 0) {
                int valor = rand.nextInt(10) + 1; // valor entre 1 y 10
                botones[fila][col].setUI(new MetalButtonUI() {
                    // override the disabled text color for the button UI
                    protected Color getDisabledTextColor() {
                        return Color.GREEN;
                    }
                });
                valoresMonedas[fila][col] = valor; // buena
                monedasBuenas++;
            }
        }
        
        while (monedasMalas < 312) {
            int fila = rand.nextInt(TAMAÑO);
            int col = rand.nextInt(TAMAÑO);
            if (valoresMonedas[fila][col] == 0) {
                int valor = -1 * (rand.nextInt(10) + 1); // valor entre -1 y -10
                botones[fila][col].setUI(new MetalButtonUI() {
                    // override the disabled text color for the button UI
                    protected Color getDisabledTextColor() {
                        return Color.red;
                    }
                });
                valoresMonedas[fila][col] = valor; // mala
                monedasMalas++;
            }
        }
        
        tiempoLimite = rand.nextInt(3)*15 + 30; // tiempo entre 30, 45 0 60 segundos
        tiempoRestante = tiempoLimite;
    }

    private void crearBotones() {
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(TAMAÑO, TAMAÑO));
        for (int i = 0; i < TAMAÑO; i++) {
            for (int j = 0; j < TAMAÑO; j++) {
                botones[i][j] = new JButton();
                botones[i][j].setText("<html><font color = red></font></html>");
                botones[i][j].setMargin( new Insets(5, 0, 5, 5) );
                botones[i][j].setActionCommand(i + "," + j);
                botones[i][j].addActionListener(new ListenerBotonMoneda());
                panelBotones.add(botones[i][j]);
            }
        }
        add(panelBotones, BorderLayout.CENTER);
    }

    private void iniciarJuego() {
        temporizador = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tiempoRestante--;
                etiquetaTiempo.setText("Tiempo restante: " + tiempoRestante + " segundos");
                if (tiempoRestante <= 0) {
                    temporizador.stop();
                    terminarJuego();
                }
            }
        });
        temporizador.start();
    }

    private void terminarJuego() {
        JOptionPane.showMessageDialog(this, "¡Tiempo agotado! Tu puntaje total es: " + puntajeTotal);
        if (puntajeTotal > 0) {
            JOptionPane.showMessageDialog(this, "¡Ganaste!");
        } else {
            JOptionPane.showMessageDialog(this, "Perdiste. Intenta de nuevo.");
        }
        System.exit(0);
    }

    private class ListenerBotonMoneda implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton boton = (JButton) e.getSource();
            String[] coords = boton.getActionCommand().split(",");
            int fila = Integer.parseInt(coords[0]);
            int col = Integer.parseInt(coords[1]);

            // Añadir el valor de la moneda a la puntuación total
            puntajeTotal += valoresMonedas[fila][col];
            boton.setEnabled(false); // Deshabilitar el botón después de seleccionarlo
            boton.setText(String.valueOf(valoresMonedas[fila][col])); // Mostrar el valor
        }
    }

    public static void main(String[] args) {
        JuegoMonedas juego = new JuegoMonedas();
        juego.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        juego.setSize(800, 800);
        juego.setVisible(true);
    }
}


