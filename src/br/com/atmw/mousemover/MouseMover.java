package br.com.atmw.mousemover;

import java.awt.*;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.text.*;

public class MouseMover {
    private static Timer timer;
    private static boolean moveRight = true;
    private static JLabel lastStatus;
    private static JLabel nextStatus;
    private static JLabel movementStatus;
    private static JTextField movementField;
    private static JTextField timeField;
    private static int countdown = 15;
    private static int movementAmount = 1;
    private static int moveInterval = 15;
    private static JTextArea errorArea;
    private static JScrollPane errorScrollPane;
    private static JButton restartButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MouseMover::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Mouse Mover");
        frame.setSize(300, 400); // Aumentei a altura para acomodar a área de erro
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Painel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        frame.add(mainPanel);

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(drawInputPanel());
        mainPanel.add(drawOutPanel());
        mainPanel.add(drawButtonPanel(frame));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createExpandablePanel()); // Adiciona o painel expansível

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private static JPanel createExpandablePanel() {
        // Painel principal que contém tudo
        JPanel expandablePanel = new JPanel();
        expandablePanel.setLayout(new BorderLayout());

        // Botão para expandir/recolher
        JButton toggleButton = new JButton("Show Error Log");
        toggleButton.setPreferredSize(new Dimension(150, 30));

        // Painel que contém a área de erros e o botão "Restart"
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(drawErrorPanel());
        contentPanel.add(drawRestartButton());
        contentPanel.setVisible(false); // Inicia recolhido

        // Ação do botão de expansão/recolhimento
        toggleButton.addActionListener(e -> {
            boolean isVisible = contentPanel.isVisible();
            contentPanel.setVisible(!isVisible); // Alterna a visibilidade
            toggleButton.setText(isVisible ? "Show Error Log" : "Hide Error Log"); // Atualiza o texto do botão
            expandablePanel.revalidate(); // Atualiza o layout
        });

        // Painel para o botão com margens horizontais de 16px
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16)); // Margens de 16px à esquerda e à direita
        buttonPanel.add(toggleButton);

        // Adiciona o painel do botão e o painel de conteúdo ao painel principal
        expandablePanel.add(buttonPanel, BorderLayout.NORTH);
        expandablePanel.add(contentPanel, BorderLayout.CENTER);

        return expandablePanel;
    }
    

    private static JPanel drawButtonPanel(JFrame frame) {
        // Botões centralizados
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");
        stopButton.setEnabled(false);

        // Definir tamanho dos botões
        Dimension buttonSize = new Dimension(100, startButton.getPreferredSize().height);
        startButton.setPreferredSize(buttonSize);
        stopButton.setPreferredSize(buttonSize);

        startButton.addActionListener(e -> {
            try {
                movementAmount = Integer.parseInt(movementField.getText());
                moveInterval = Integer.parseInt(timeField.getText());
                if (movementAmount <= 0 || moveInterval <= 0) throw new NumberFormatException();
                startMouseMover();
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                movementField.setEnabled(false);
                timeField.setEnabled(false);
                updateCountdown(String.valueOf(moveInterval) + "s");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Por favor, insira valores positivos válidos.", "Erro de entrada", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                handleError(ex);
            }
        });

        stopButton.addActionListener(e -> {
            stopMouseMover();
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            movementField.setEnabled(true);
            timeField.setEnabled(true);
            updateCountdown(String.valueOf(moveInterval) + "s");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        return buttonPanel;
    }

    private static JPanel drawInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel pixelLabel = new JLabel("Pixels:");
        JLabel timeLabel = new JLabel("Interval (s):");

        movementField = new JTextField(5);
        restrictToPositiveIntegers(movementField);
        movementField.setText(String.valueOf(movementAmount));
        movementField.setHorizontalAlignment(JTextField.CENTER);

        timeField = new JTextField(5);
        restrictToPositiveIntegers(timeField);
        timeField.setText(String.valueOf(moveInterval));
        timeField.setHorizontalAlignment(JTextField.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(pixelLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(movementField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(timeLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(timeField, gbc);

        return inputPanel;
    }

    private static JPanel drawOutPanel() {
        JPanel outputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints ogbc = new GridBagConstraints();
        ogbc.insets = new Insets(5, 5, 5, 5);
        ogbc.anchor = GridBagConstraints.CENTER;
        ogbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lastDirectionLabel = new JLabel("Last Direction:");
        JLabel nextDirectionLabel = new JLabel("Next Direction:");
        JLabel movementLabel = new JLabel("It moves in:");

        lastStatus = new JLabel("<html><b>none</b></html>", SwingConstants.CENTER);
        lastStatus.setFont(new Font("Arial", Font.PLAIN, 12));

        nextStatus = new JLabel("<html><b>none</b></html>", SwingConstants.CENTER);
        nextStatus.setFont(new Font("Arial", Font.PLAIN, 12));

        movementStatus = new JLabel("<html><b>" + countdown + "s</b></html>", SwingConstants.CENTER);
        movementStatus.setFont(new Font("Arial", Font.PLAIN, 12));

        ogbc.gridx = 0;
        ogbc.gridy = 0;
        outputPanel.add(lastDirectionLabel, ogbc);

        ogbc.gridx = 1;
        outputPanel.add(lastStatus, ogbc);

        ogbc.gridx = 0;
        ogbc.gridy = 1;
        outputPanel.add(nextDirectionLabel, ogbc);

        ogbc.gridx = 1;
        outputPanel.add(nextStatus, ogbc);

        ogbc.gridx = 0;
        ogbc.gridy = 2;
        outputPanel.add(movementLabel, ogbc);

        ogbc.gridx = 1;
        outputPanel.add(movementStatus, ogbc);

        return outputPanel;
    }

    private static JPanel drawErrorPanel() {
        errorArea = new JTextArea("No errors logged"); // Texto inicial
        errorArea.setEditable(false);
        errorArea.setLineWrap(true);
        errorArea.setWrapStyleWord(true);
        errorArea.setFont(new Font("Arial", Font.PLAIN, 12));
        errorArea.setBackground(new Color(255, 235, 235));
        errorArea.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        errorScrollPane = new JScrollPane(errorArea);
        errorScrollPane.setPreferredSize(new Dimension(300, 100));
        errorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel errorPanel = new JPanel();
        errorPanel.setLayout(new BorderLayout());
        errorPanel.add(errorScrollPane, BorderLayout.CENTER);

        return errorPanel;
    }

    private static JPanel drawRestartButton() {
        restartButton = new JButton("Restart");
        restartButton.setPreferredSize(new Dimension(100, 30));
        restartButton.addActionListener(e -> {
            stopMouseMover();

            countdown = moveInterval;
            updateCountdown(String.valueOf(countdown) + "s");
            updateStatus("none", "none", String.valueOf(countdown) + "s");

            startMouseMover();
        });

        JPanel restartPanel = new JPanel();
        restartPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        restartPanel.add(restartButton);

        return restartPanel;
    }

    private static void startMouseMover() {
        try {
            Robot robot = new Robot();
            timer = new Timer();
            countdown = moveInterval;

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        countdown--;

                        if (countdown == 0) {
                            Point mousePosition = MouseInfo.getPointerInfo().getLocation();
                            int x = (int) mousePosition.getX();
                            int y = (int) mousePosition.getY();

                            if (moveRight) {
                                robot.mouseMove(x + movementAmount, y);
                                updateStatus("right", "left", moveInterval + "s");
                            } else {
                                robot.mouseMove(x - movementAmount, y);
                                updateStatus("left", "right", moveInterval + "s");
                            }
                            moveRight = !moveRight;
                            countdown = moveInterval;
                        } else {
                            updateCountdown(countdown + "s");
                        }
                    } catch (Exception ex) {
                        handleError(ex);
                    }
                }
            }, 0, 1000);

        } catch (AWTException e) {
            handleError(e);
        }
    }

    private static void stopMouseMover() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private static void updateStatus(String last, String next, String countdown) {
        lastStatus.setText(String.format("<html><b>%s</b></html>", last));
        nextStatus.setText(String.format("<html><b>%s</b></html>", next));
        movementStatus.setText(String.format("<html><b>%s</b></html>", countdown));
    }

    private static void updateCountdown(String countdown) {
        String currentText = movementStatus.getText();
        String updatedText = currentText.replaceAll("<b>.*?</b>", "<b>" + countdown + "</b>");
        movementStatus.setText(updatedText);
    }

    private static void restrictToPositiveIntegers(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
                if (string.matches("\\d*")) {
                    super.replace(fb, offset, length, string, attrs);
                }
            }
        });
    }

    private static void handleError(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        errorArea.setText(sw.toString());
        errorScrollPane.setVisible(true);
    }
}