package parejas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Juego extends JFrame {

    //CONSTANTES
    final int numParejas = 9;
    final int margenV = 40;
    final int margenH = 30;
    final int anchoCarta = 99;
    final int altoCarta = 132;
    final int tiempoCuentaAtras = 3;
    final Font fuente = new Font("Arial", Font.PLAIN, 20);
    final ImageIcon[] imagenes
            = {
                new ImageIcon(getClass().getResource("/imagenes/banderas/vacio.png")),
                new ImageIcon(getClass().getResource("/imagenes/banderas/luffy.png")),
                new ImageIcon(getClass().getResource("/imagenes/banderas/zoro.png")),
                new ImageIcon(getClass().getResource("/imagenes/banderas/ussop.png")),
                new ImageIcon(getClass().getResource("/imagenes/banderas/sanji.png")),
                new ImageIcon(getClass().getResource("/imagenes/banderas/nami.png")),
                new ImageIcon(getClass().getResource("/imagenes/banderas/chopper.png")),
                new ImageIcon(getClass().getResource("/imagenes/banderas/robin.png")),
                new ImageIcon(getClass().getResource("/imagenes/banderas/franky.png")),
                new ImageIcon(getClass().getResource("/imagenes/banderas/brook.png"))
            };
    //DECLARACION DE VARIABLES
    JLabel[] labels;
    JLabel tiempo, porcentaje, intentos, cuentaAtras;
    JPanel panel;
    JButton boton;
    int[][] cartas;
    int numClic, cartaUno, cartaDos, aciertos, numIntentos, minutos, segundos;
    boolean crono, comprueba, comenzar, clicable;
    Point puntoInicio;

    public Juego() {
        inicializaVentana(); //Inicializa el JFrame
        inicializaPanel(); //Inicializa el JPanel
        inicializaVariables(); //Inicializa todas la variables
        llenaArrayLogico(numParejas, cartas); //Llena con numeros aleatorios entre 1 y 9 un array de 3x6
        inicializaLabels(); //Inicializa todos los JLabel
    }

    private void inicializaVentana() {
        setTitle("Parejas de cartas. By Mario Villaescusa");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void inicializaPanel() {
        //Creo un JPanel
        panel = new JPanel();
        panel.setSize(getWidth(), getHeight());
        panel.setBackground(Color.black);
        panel.setLayout(null);

        //Creo un JButton y lo añado al panel
        boton = new JButton("Comenzar");
        boton.setActionCommand("Comenzar");
        boton.setSize(100, 25);
        boton.setLocation((getWidth() / 2) - boton.getWidth() / 2, 15);
        boton.addActionListener(al);
        panel.add(boton);

        add(panel); //Añado el panel al JFrame
    }

    private void inicializaVariables() {
        cartas = new int[3][6];
        crono = false;
        comprueba = false;
        comenzar = false;
        clicable = true;
        cartaUno = -1;
        cartaDos = -1;
        aciertos = 0;
        numIntentos = 0;
        minutos = 0;
        segundos = 0;
        numClic = 0;
        puntoInicio = new Point((getWidth() / 2) - (3 * anchoCarta) - (2 * margenH) - (margenH / 2), (getHeight() / 2) - (altoCarta + altoCarta / 2) - margenV);
    }

    private void inicializaLabels() {
        cuentaAtras = new JLabel(); //El label de cuenta atrás solo se muestra después de darle a comenzar
        cuentaAtras.setName("cuentaAtras");
        cuentaAtras.setLocation((getWidth() / 2) - 50, (getHeight() / 2) - 100);
        cuentaAtras.setSize(150, 150);
        cuentaAtras.setForeground(Color.white);
        cuentaAtras.setFont(fuente.deriveFont(150f));
        cuentaAtras.setVisible(false);
        panel.add(cuentaAtras);

        labels = new JLabel[numParejas * 2];
        int i = 0;
        for (int y = 0; y < cartas.length; y++) { // Inicializa los JLabel de las cartas con la imagen del reverso
            for (int x = 0; x < cartas[y].length; x++) {
                labels[i] = new JLabel("" + i);
                labels[i].setName(i + "," + y + "," + x);
                labels[i].setLocation(puntoInicio.x + ((anchoCarta + margenH) * x), puntoInicio.y + ((altoCarta + margenV) * y));
                labels[i].setSize(anchoCarta, altoCarta);
                labels[i].setIcon(imagenes[0]);
                labels[i].addMouseListener(ml);
                panel.add(labels[i]);
                i++;
            }
        }
        //Creo e inicializo el JLabel que muestra el tiempo transcurrido
        tiempo = new JLabel("Tiempo: 0");
        tiempo.setName("tiempo");
        tiempo.setLocation(getWidth() - 150, 20);
        tiempo.setSize(150, 20);
        tiempo.setForeground(Color.white);
        tiempo.setFont(fuente);
        panel.add(tiempo);
        //Creo e inicializo el JLabel que muestra el porcentaje de aciertos
        porcentaje = new JLabel("Porcentaje de aciertos: 0%");
        porcentaje.setName("porcentaje");
        porcentaje.setLocation(30, 20);
        porcentaje.setSize(300, 20);
        porcentaje.setForeground(Color.white);
        porcentaje.setFont(fuente);
        panel.add(porcentaje);
        //Creo e inicializo el JLabel que muestra el numero de intentos
        intentos = new JLabel("Intentos: 0");
        intentos.setName("intentos");
        intentos.setLocation(getWidth() - 275, 20);
        intentos.setSize(125, 20);
        intentos.setForeground(Color.white);
        intentos.setFont(fuente);
        panel.add(intentos);
    }

    private void llenaArrayLogico(int numParejas, int[][] cartas) { //Crea un array de 3*6 para saber que imagen poner en cada carta
        int contador = 0;
        int num = 1;
        int pareja = 0;
        for (int y = 0; y < cartas.length; y++) {
            for (int x = 0; x < cartas[y].length; x++) {
                cartas[y][x] = 0;
            }
        }
        while (contador != numParejas * 2) {
            int x, y;
            y = (int) (Math.random() * cartas.length);
            x = (int) (Math.random() * cartas[0].length);
            if (cartas[y][x] == 0) {
                cartas[y][x] = num;
                contador++;
                pareja++;
                if (pareja == 2) {
                    num++;
                    pareja = 0;
                }
            }
        }
    }

    private void espera(int milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException ex) {
            Logger.getLogger(Juego.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (ae.getActionCommand().equals("Comenzar")) {
                if (!comenzar) {
                    comenzar = true;
                    boton.setActionCommand("Reiniciar");
                    boton.setText("Reiniciar");
                    crono = true;
                    muestraCartas();
                }
            }
            if (ae.getActionCommand().equals("Reiniciar")) {
                if (comenzar) {
                    crono = false;
                    comenzar = false;
                    for (JLabel label : labels) {
                        label.setIcon(imagenes[0]);
                        label.removeMouseListener(ml);
                        label.addMouseListener(ml);
                    }
                    llenaArrayLogico(numParejas, cartas);
                    boton.setActionCommand("Comenzar");
                    boton.setText("Comenzar");

                    crono = false;
                    comprueba = false;
                    comenzar = false;
                    cartaUno = -1;
                    cartaDos = -1;
                    aciertos = 0;
                    numIntentos = 0;
                    minutos = 0;
                    segundos = 0;
                    numClic = 0;

                    porcentaje.setText("Porcentaje de aciertos: 0%");
                    tiempo.setText("Tiempo: 0");
                    intentos.setText("Intentos: 0");
                }
            }
        }
    };

    MouseListener ml = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (clicable) {
                if (comenzar) {
                    if (numClic >= 0) {
                        numClic++;
                        String[] info = e.getComponent().getName().split(",");
                        labels[Integer.parseInt(info[0])].setIcon(imagenes[cartas[Integer.parseInt(info[1])][Integer.parseInt(info[2])]]);
                        labels[Integer.parseInt(info[0])].removeMouseListener(ml);
                        if (numClic == 2) {
                            cartaDos = Integer.parseInt(info[0]);
                            numClic = 0;
                            comprueba = true;
                        } else {
                            cartaUno = Integer.parseInt(info[0]);
                        }
                    }
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    };

    private void muestraCartas() { //Muestra las cartas durante unos segundos antes de comenzar la partida
        new Thread(new Runnable() {
            public void run() {
                int i = 0;
                for (int y = 0; y < cartas.length; y++) {
                    for (int x = 0; x < cartas[y].length; x++) {
                        labels[i++].setIcon(imagenes[cartas[y][x]]);
                    }
                }
                cuentaAtras.setVisible(true);
                for (int j = tiempoCuentaAtras; j > 0; j--) {
                    cuentaAtras.setText("" + j);
                    espera(1000);
                }
                i = 0;
                for (int y = 0; y < cartas.length; y++) {
                    for (int x = 0; x < cartas[y].length; x++) {
                        labels[i++].setIcon(imagenes[0]);
                    }
                }
                cuentaAtras.setVisible(false);
                cronometro();
                comprueba();
            }
        }).start();
    }

    private void comprueba() { //Comprueba si la pareja es correcta o no
        new Thread(() -> {
            while (crono) {
                espera(1);
                if (comprueba) {
                    if ((cartaUno != -1) && (cartaDos != -1) && (numClic == 0)) {
                        numClic = -1;
                        numIntentos++;
                        if (labels[cartaUno].getIcon() != labels[cartaDos].getIcon()) {
                            espera(1000);
                            labels[cartaUno].setIcon(imagenes[0]);
                            labels[cartaUno].addMouseListener(ml);
                            labels[cartaDos].setIcon(imagenes[0]);
                            labels[cartaDos].addMouseListener(ml);
                            cartaUno = -1;
                            cartaDos = -1;
                        } else {
                            aciertos++;
                        }
                    }
                    comprueba = false;
                    numClic = 0;
                }
                if (aciertos == 9) {
                    crono = false;
                    JOptionPane.showMessageDialog(null, "Enhorabuena!\nHAS GANADO!");
                    //System.exit(0);
                }
            }
        }).start();
    }

    private void cronometro() { //Actualiza el tiempo transcurrido desde que empieza la partida
        new Thread(() -> {
            while (crono) {
                espera(1000);
                segundos++;
                if (segundos == 60) {
                    minutos++;
                    segundos = 0;
                }
                if (minutos > 0) {
                    tiempo.setText("Tiempo: " + minutos + ":" + segundos);
                    intentos.setText("Intentos: " + numIntentos);
                } else {
                    tiempo.setText("Tiempo: " + segundos);
                    intentos.setText("Intentos: " + numIntentos);
                }
                if (numIntentos != 0) {
                    porcentaje.setText("Porcentaje de aciertos: " + aciertos * 100 / numIntentos + "%");
                }
                if (!crono && boton.getActionCommand().equals("Comenzar")) {
                    segundos = 0;
                    minutos = 0;
                    tiempo.setText("Tiempo: " + segundos);
                }
            }
        }).start();
    }
}
