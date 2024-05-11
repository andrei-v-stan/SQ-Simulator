package PERIPHS;

import CORE.Configurator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;

public class SimulatorFrame {

    public SimulatorFrame() {
        Keyboard keyboard = Configurator.keyboard;
        Screen screen = Configurator.screen;

        JFrame frame = new JFrame("CPU Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextArea screenOutput = new JTextArea();
        screenOutput.setEditable(false);

        JTextArea keyboardInput = new JTextArea();
        keyboardInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char key = e.getKeyChar();
                keyboard.write(key);
                System.out.println("Read from keyboard: " + key);
                if (key == '\n') {
                    String text = keyboardInput.getText();
                    screenOutput.setText(screenOutput.getText() + text);
                    keyboardInput.setText("");
                }
            }
        });
        /*
        keyboardInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char key = e.getKeyChar();
                System.out.println("Read from keyboard: " + key);
                keyboard.write(key);
                screenOutput.setText(screenOutput.getText() + key);
            }
        });
        */


        JTextArea systemPrints = new JTextArea();
        systemPrints.setEditable(false);

        PrintStream printStream = new PrintStream(new CustomOutputStream(systemPrints));
        System.setOut(printStream);
        System.setErr(printStream);

        JPanel screenOutputPanel = new JPanel();
        screenOutputPanel.setLayout(new BorderLayout());
        JLabel screenLabel = new JLabel("Screen Output");
        screenLabel.setForeground(Color.WHITE);
        screenOutputPanel.add(screenLabel, BorderLayout.NORTH);
        JScrollPane screenScrollPane = new JScrollPane(screenOutput);
        screenScrollPane.setPreferredSize(new Dimension(screen.getWidth(), screen.getLength()));
        screenOutputPanel.add(screenScrollPane, BorderLayout.CENTER);
        screenOutputPanel.setBackground(Color.BLACK);
        screenOutputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel systemPrintsPanel = new JPanel();
        systemPrintsPanel.setLayout(new BorderLayout());
        JLabel systemLabel = new JLabel("System Prints");
        systemLabel.setForeground(Color.WHITE);
        systemPrintsPanel.add(systemLabel, BorderLayout.NORTH);
        JScrollPane systemPrintsScrollPane = new JScrollPane(systemPrints);
        systemPrintsScrollPane.setPreferredSize(new Dimension(400, 200));
        systemPrintsPanel.add(systemPrintsScrollPane, BorderLayout.CENTER);
        systemPrintsPanel.setBackground(Color.BLACK);
        systemPrintsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new BorderLayout());
        JLabel keyboardLabel = new JLabel("Keyboard Input");
        keyboardLabel.setForeground(Color.WHITE);
        keyboardPanel.add(keyboardLabel, BorderLayout.NORTH);
        JScrollPane keyboardScrollPane = new JScrollPane(keyboardInput);
        keyboardScrollPane.setPreferredSize(new Dimension(200, 300));
        keyboardPanel.add(keyboardScrollPane, BorderLayout.CENTER);
        keyboardPanel.setBackground(Color.BLACK);
        keyboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(screenOutputPanel, BorderLayout.CENTER);
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(2, 1));
        sidePanel.add(systemPrintsPanel);
        sidePanel.add(keyboardPanel);
        mainPanel.add(new JScrollPane(sidePanel), BorderLayout.LINE_END);

        frame.add(mainPanel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}