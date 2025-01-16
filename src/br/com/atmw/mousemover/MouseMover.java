package br.com.atmw.mousemover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class MouseMover {
    private static Timer timer;
    private static boolean moveRight = true;
    private static JLabel statusLabel;
    private static int countdown = 5; // Sempre iniciar com 5 segundos

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        // Criação da janela principal
        JFrame frame = new JFrame("Mouse Mover");
        frame.setSize(300, 200);
        frame.setLayout(new BorderLayout());

        // Ação de encerramento: fecha a janela e interrompe tarefas
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Encerrando a aplicação.");
                stopMouseMover(); // Interrompe qualquer tarefa ativa
                System.exit(0);
            }
        });

        // Painel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);

        // Adicionando o status (texto central)
        statusLabel = new JLabel("<html>Last: <b>none</b><br>Next: <b>none</b><br>It moves in: <b>5s</b></html>", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(statusLabel, BorderLayout.CENTER);

        // Botões na parte inferior
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");
        stopButton.setEnabled(false);

        startButton.addActionListener(e -> {
            startMouseMover();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        });

        stopButton.addActionListener(e -> {
            stopMouseMover();
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(startButton);
        bottomPanel.add(stopButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Configurar e exibir janela
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void startMouseMover() {
        try {
            Robot robot = new Robot();
            timer = new Timer();
            countdown = 5; // Reseta o contador para 5 segundos sempre que inicia

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // Atualiza a contagem regressiva
                    countdown--;

                    if (countdown == 0) {
                        // Movimento do mouse
                        Point mousePosition = MouseInfo.getPointerInfo().getLocation();
                        int x = (int) mousePosition.getX();
                        int y = (int) mousePosition.getY();

                        if (moveRight) {
                            robot.mouseMove(x + 10, y);
                            updateStatus("right", "left", "5s");
                        } else {
                            robot.mouseMove(x - 10, y);
                            updateStatus("left", "right", "5s");
                        }
                        moveRight = !moveRight;
                        countdown = 5; // Reinicia o temporizador
                    } else {
                        updateCountdown(countdown + "s");
                    }
                }
            }, 0, 1000); // Executar a cada 1 segundo (para atualizar o countdown)

        } catch (AWTException e) {
            System.err.println("Erro ao inicializar o Robot: " + e.getMessage());
        } catch (Exception e) {
        	System.err.println("Erro: " + e.getMessage());
        }
    }

    private static void stopMouseMover() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            countdown = 5; // Reseta a contagem para 5 segundos ao parar
            updateStatus("none", "none", "5s");
        }
    }

    private static void updateStatus(String last, String next, String countdown) {
        statusLabel.setText(String.format("<html>Last: <b>%s</b><br>Next: <b>%s</b><br>It moves in: <b>%s</b></html>", last, next, countdown));
    }

    private static void updateCountdown(String countdown) {
        String currentText = statusLabel.getText();
        String updatedText = currentText.replaceAll("It moves in: <b>.*?</b>", "It moves in: <b>" + countdown + "</b>");
        statusLabel.setText(updatedText);
    }
}



