import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AudioMonitorScreen extends JFrame {

    private Process recordProcess;
    private Thread readingThread;
    private JLabel predictionLabel = new JLabel("Prediction: None");
    private JLabel locationLabel = new JLabel("Location: N/A");
    private JLabel statusLabel = new JLabel("Status: Idle");
    private boolean isManualLogout = false;

    public AudioMonitorScreen() {
        setTitle("Audio Recognition Dashboard");
        setSize(520, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (isManualLogout) {
                    WelcomeScreen.showWelcome();
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setBackground(new Color(250, 250, 250)); // Very light background
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("🎧 Audio Recognition Dashboard");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        title.setForeground(new Color(50, 50, 50)); // Dark gray text
        panel.add(title);

        predictionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        predictionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(predictionLabel);
        locationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        locationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(locationLabel);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(statusLabel);

        JButton startButton = createStyledButton("Start Recognition", new Color(76, 175, 80));  // Green
        JButton stopButton = createStyledButton("Stop Recognition", new Color(244, 67, 54));    // Red
        JButton logoutButton = createStyledButton("Logout", new Color(120, 144, 156));          // Gray-blue

        // Action listeners
        startButton.addActionListener(e -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                        "/Users/mayur/IdeaProjects/projects/prediction/.venv/bin/python",
                        "predict_audio.py"
                );
                pb.directory(new File("/Users/mayur/IdeaProjects/projects")); // Adjust path if needed
                pb.redirectErrorStream(true);
                recordProcess = pb.start();
                System.out.println("🔊 Python process started: " + recordProcess.info());
                SwingUtilities.invokeLater(() -> {
                    title.setText("🎙️ Listening...");
                    statusLabel.setText("Status: Listening...");
                    predictionLabel.setText("Prediction: None");
                    locationLabel.setText("Location: N/A");
                });

                BufferedReader reader = new BufferedReader(new InputStreamReader(recordProcess.getInputStream()));
                readingThread = new Thread(() -> {
                    try {
                        String line;
                        while (recordProcess != null && recordProcess.isAlive() && (line = reader.readLine()) != null) {
                            System.out.println("PYTHON: " + line); // Optional logging

                            final String outputLine = line;
                            SwingUtilities.invokeLater(() -> {
                                if (outputLine.contains("📍 Location:")) {
                                    locationLabel.setText(outputLine.replace("📍 ", "").trim());
                                } else if (outputLine.contains("🚨 Scream detected")) {
                                    predictionLabel.setText("Prediction: Scream");
                                } else if (outputLine.toLowerCase().contains("non-scream")) {
                                    predictionLabel.setText("Prediction: Non-Scream");
                                }
                            });
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ignored) {}
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "⚠️ Error while reading Python output:\n" + ex.getMessage());
                        });
                    }
                });
                readingThread.start();

                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "🎙️ Real-time recognition started...")
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "❌ Failed to start recognition: " + ex.getMessage());
            }
        });
        stopButton.addActionListener(e -> {
            try {
                if (recordProcess != null && recordProcess.isAlive()) {
                    System.out.println("🛑 Terminating Python process...");
                    recordProcess.destroy();
                    recordProcess.waitFor();
                    recordProcess = null;
                    if (readingThread != null && readingThread.isAlive()) {
                        readingThread.interrupt();
                    }
                    readingThread = null;
                    predictionLabel.setText("Prediction: None");
                    JOptionPane.showMessageDialog(this, "🛑 Recognition stopped.");
                    SwingUtilities.invokeLater(() -> {
                        title.setText("🎧 Audio Recognition Dashboard");
                        statusLabel.setText("Status: Stopped");
                    });
                } else {
                    JOptionPane.showMessageDialog(this, "⚠️ No recognition is running.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "❌ Failed to stop recognition: " + ex.getMessage());
            }
        });
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                isManualLogout = true;
                dispose();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("session.txt"))) {
                    writer.write("false");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        // Add buttons with spacing
        panel.add(startButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(stopButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(logoutButton);

        add(panel);
        setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Optional shimmer/shine effect (fake ripple)
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 20, 20);
            }
        };

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(280, 45));
        button.setMaximumSize(new Dimension(280, 45));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1, true),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setOpaque(true);

        // Hover effects
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.darker());
                button.setForeground(Color.WHITE);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
                button.setForeground(Color.WHITE);
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AudioMonitorScreen screen = new AudioMonitorScreen();
            screen.setVisible(true);
        });
    }
}
