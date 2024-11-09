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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.plaf.metal.MetalButtonUI;

public class JuegoMemoria extends JFrame {
    private static final int FILAS = 3;
    private static final int COLUMNAS = 6;
    private static final int NUM_PAREJAS = 9;
    private static final int TIEMPO_TOTAL = 60; // en segundos

    private JButton[][] botones;
    private String[][] imagenes;
    private boolean[][] elegido;
    private int paresEncontrados = 0;
    private JButton boton1, boton2;
    private Timer temporizador;
    private int tiempoRestante;
    private JLabel etiquetaTiempo;
    
    private ImageIcon dorsoImg;
    public JuegoMemoria() throws MalformedURLException, IOException {
        Font marioFuente = null;
        try {
        //create the font to use. Specify the size!
        marioFuente = Font.createFont(Font.TRUETYPE_FONT, new File("assests\\Fuentes\\SuperMario256.ttf")).deriveFont(20f);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //register the font
        ge.registerFont(marioFuente);
        } catch (IOException|FontFormatException e) {
        //Handle exception
        }
        // Configuración de la ventana
        UIManager.put("Label.font",marioFuente);
        setTitle("Juego de Memoria");
        setSize(930, 780);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        botones = new JButton[FILAS][COLUMNAS];
        imagenes = new String[FILAS][COLUMNAS];

        URL url = new URL("https://mario.wiki.gallery/images/thumb/c/c9/NAP-06_Back.png/303px-NAP-06_Back.png?download");
        Image img = ImageIO.read(url);
        img = img.getScaledInstance(150, 240, Image.SCALE_DEFAULT);
        dorsoImg = new ImageIcon(img);
        
        // Inicializar la interfaz
        JPanel panelJuego = new JPanel();
        panelJuego.setLayout(new GridLayout(FILAS, COLUMNAS));
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                botones[i][j] = new JButton();
                botones[i][j].setName("");
                botones[i][j].setIcon(dorsoImg);
                botones[i][j].setDisabledIcon(dorsoImg);
                botones[i][j].setFocusPainted(false);
                botones[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            botonPresionado((JButton) e.getSource());
                        } catch (IOException ex) {
                            Logger.getLogger(JuegoMemoria.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                panelJuego.add(botones[i][j]);
            }
        }

        etiquetaTiempo = new JLabel("Tiempo restante: " + TIEMPO_TOTAL + " seg", JLabel.CENTER);

        add(etiquetaTiempo, BorderLayout.NORTH);
        add(panelJuego, BorderLayout.CENTER);

        // Inicializar imágenes
        inicializarImagenes();
        
        // Iniciar temporizador
        tiempoRestante = TIEMPO_TOTAL;
        temporizador = new Timer();
        temporizador.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (tiempoRestante > 0) {
                    tiempoRestante--;
                    etiquetaTiempo.setText("Tiempo restante: " + tiempoRestante + " seg");
                } else {
                    temporizador.cancel();
                    mostrarResultado();
                }
            }
        }, 1000, 1000); // Ejecutar cada segundo

        setVisible(true);
    }

    private void inicializarImagenes() {
        // Asignamos imágenes a la matriz (simuladas con nombres)
        ArrayList<String> listaImagenes = new ArrayList<>();
        for (int i = 1; i <= NUM_PAREJAS; i++) {
            listaImagenes.add(""+i);
            listaImagenes.add(""+i); // Crear el par
        }
        Collections.shuffle(listaImagenes); // Mezclar las imágenes

        int index = 0;
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                imagenes[i][j] = listaImagenes.get(index++);
            }
        }
    }

    private void botonPresionado(JButton boton) throws MalformedURLException, IOException {
        // Encontrar el botón que fue presionado
        int fila = -1, columna = -1;
        outerLoop:
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (botones[i][j] == boton) {
                    fila = i;
                    columna = j;
                    break outerLoop;
                }
            }
        }

        // Mostrar la imagen en el botón presionado
        Image img = ImageIO.read(new File("assests\\cartas_memoria\\Spades_"+ imagenes[fila][columna]+".png"));
        img = img.getScaledInstance(150, 240, Image.SCALE_DEFAULT);
        boton.setIcon(new ImageIcon(img));
        boton.setName(imagenes[fila][columna]);
        boton.setDisabledIcon(new ImageIcon(img));
        boton.setEnabled(false); // Deshabilitar el botón una vez presionado

        // Si es el primer botón presionado
        if (boton1 == null) {
            boton1 = boton;
        } else if (boton2 == null) { // Si es el segundo botón presionado
            boton2 = boton;
            verificarPares();
        }
    }
    
    private void activarBotones(boolean estado){
        for (int y=0; y<FILAS;y++){
            for (int x=0; x<COLUMNAS;x++){
                if(botones[y][x].getName() == ""){
                    botones[y][x].setEnabled(estado);
                }
            }
        }
    }

    private void verificarPares() {
        // Si las coinciden
        if (boton1.getName().equals(boton2.getName())) {
            paresEncontrados++;
            boton1 = null;
            boton2 = null;
            // Si se encontraron todos los pares, el juego termina
            if (paresEncontrados == NUM_PAREJAS) {
                temporizador.cancel();
                mostrarResultado();
            }
        } else {
            activarBotones(false);
            // Si las imágenes no coinciden, ocultar de nuevo después de un pequeño retraso
            TimerTask revertirTarea = new TimerTask() {
                @Override
                public void run() {
                    boton1.setName("");
                    boton2.setName("");
                    activarBotones(true);
                    boton1.setIcon(dorsoImg);
                    boton2.setIcon(dorsoImg);
                    boton1.setDisabledIcon(dorsoImg);
                    boton2.setDisabledIcon(dorsoImg);
                    boton1 = null;
                    boton2 = null;
                }
            };
            new Timer().schedule(revertirTarea, 1000); // Esperar 1 segundo
        }
    }

    private void mostrarResultado() {
        JOptionPane.showMessageDialog(this, "Juego terminado\nPares encontrados: " + paresEncontrados, 
                                      "Resultado", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0); // Terminar el juego
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new JuegoMemoria();
                } catch (IOException ex) {
                    Logger.getLogger(JuegoMemoria.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}

