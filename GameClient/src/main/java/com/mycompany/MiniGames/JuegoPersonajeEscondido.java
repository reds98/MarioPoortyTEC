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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.net.URL;

public class JuegoPersonajeEscondido extends JFrame {
    private static final int FILAS = 10;
    private static final int COLUMNAS = 10;
    private static final int NUM_PERSONAJES = 15;
    private JButton[][] botones;
    private String personajeOculto;
    private List<String> personajes;
    private int celdasPorDescubrir;
    private JLabel celdasRestantesLabel;
    private BufferedImage imagenFondo;
    private int celdaWidth, celdaHeight;
    private String nombresPJ[] = {"Bill Bala","Blooper","Boo","Bowser","Capitan Toad",
                        "Chomp Cadenas","Donkey Kong","Goomba","Kamek",
                        "Lakitu","Luigi","Mario","Peach","Yoshi","Planta Piraña"};

    public JuegoPersonajeEscondido() {
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
        setTitle("Adivina quien");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        botones = new JButton[FILAS][COLUMNAS];
        personajes = cargarPersonajes();
    

        iniciarJuego();
        
        try {
        imagenFondo = ImageIO.read(new File("assests\\PJ_jpg\\"+ personajeOculto + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setLayout(new BorderLayout());
        JPanel panelBotones = new JPanel(new GridLayout(FILAS, COLUMNAS));
        ImageIcon bloqueImg = new ImageIcon("assests\\PJ_jpg\\bloque3.jpg");
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                botones[i][j] = new JButton(bloqueImg);
                botones[i][j].setBackground(Color.LIGHT_GRAY);
                botones[i][j].addActionListener(new CeldaListener(i, j));
                panelBotones.add(botones[i][j]);
            }
        }

        // Panel de información
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        celdasRestantesLabel = new JLabel("Celdas restantes por revelar: " + celdasPorDescubrir);
        panelInfo.add(celdasRestantesLabel);

        // Crear un JTextArea para mostrar la lista de personajes
        String personajesTexto = String.join("\n", personajes);
        JTextArea personajesArea = new JTextArea(personajesTexto);
        personajesArea.setEditable(false); // No editable
        personajesArea.setRows(2); // Ajusta el número de filas
        personajesArea.setColumns(30); // Ajusta el número de columnas
        personajesArea.setLineWrap(true);
        personajesArea.setWrapStyleWord(true);

        // Añadir el JTextArea a un JScrollPane
        JScrollPane scrollPane = new JScrollPane(personajesArea);
        panelInfo.add(scrollPane);

        add(panelInfo, BorderLayout.SOUTH);
        add(panelBotones, BorderLayout.CENTER);
        
    }

    private void iniciarJuego() {
        Random rand = new Random();
        personajeOculto = personajes.get(rand.nextInt(NUM_PERSONAJES));
        
        // Establece las dimensiones de cada celda
        celdaWidth = 800 / COLUMNAS;
        celdaHeight = 600 / FILAS;

        // Selecciona entre 4 y 8 celdas aleatorias
        celdasPorDescubrir = 4 + rand.nextInt(5);
    }

    private List<String> cargarPersonajes() {
        List<String> personajes = new ArrayList<>();
        for (int i = 0; i < NUM_PERSONAJES; i++) {
            personajes.add(nombresPJ[i]); // Asegúrate de que las imágenes existan
        }
        Collections.shuffle(personajes);
        return personajes;
    }

    private class CeldaListener implements ActionListener {
        private final int fila;
        private final int columna;

        public CeldaListener(int fila, int columna) {
            this.fila = fila;
            this.columna = columna;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            revelarCelda(fila, columna);
        }
    }

    private void revelarCelda(int fila, int columna) {
        // Cambia el fondo del botón a blanco
        botones[fila][columna].setBackground(Color.WHITE);
        botones[fila][columna].setEnabled(false); //Desactiva el boton
        // Calcula la parte de la imagen que se debe mostrar
        int x = columna * celdaWidth;
        int y = fila * celdaHeight;
        
        // Crea una imagen recortada de la parte correspondiente
        BufferedImage imagenRecortada = imagenFondo.getSubimage(x, y, celdaWidth, celdaHeight);
        botones[fila][columna].setIcon(new ImageIcon(imagenRecortada)); // Muestra la parte recortada
        botones[fila][columna].setDisabledIcon(new ImageIcon(imagenRecortada)); // Muestra la parte recortada
        celdasPorDescubrir--;

        // Actualiza el contador de celdas restantes
        celdasRestantesLabel.setText("Celdas restantes por revelar: " + celdasPorDescubrir);
        // Si ya no hay celdas por descubrir, pregunta al jugador
        if (celdasPorDescubrir == 0) {
            hacerEleccion();
        }
    }

    private void hacerEleccion() {
        String respuesta = JOptionPane.showInputDialog(this, "¿Cual es el personaje escondido?");
        if (respuesta != null && respuesta.equalsIgnoreCase(personajeOculto)) {
            JOptionPane.showMessageDialog(this, "¡Ganaste! El personaje era: " + personajeOculto);
        } else {
            JOptionPane.showMessageDialog(this, "Perdiste. El personaje era: " + personajeOculto);
        }
        setVisible(false); //you can't see me!
        dispose(); //Destroy the JFrame object
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JuegoPersonajeEscondido juego = new JuegoPersonajeEscondido();
            juego.setVisible(true);
        });
    }
}




