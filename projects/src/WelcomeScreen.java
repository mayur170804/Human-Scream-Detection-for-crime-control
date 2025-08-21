import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WelcomeScreen extends JFrame {

    public WelcomeScreen() {
        setTitle("Welcome - Human Scream Detection");
        setSize(520, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(250, 250, 250));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Logo (optional)
        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = new ImageIcon("logo.png");
        if (logoIcon.getIconWidth() > 0) { // check if image is found
            Image scaledImage = logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledImage));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
            panel.add(logoLabel);
        }

        // Title
        JLabel title = new JLabel("🎤 Human Scream Detection");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(50, 50, 50));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        panel.add(title);

        // Buttons
        JButton loginButton = createStyledButton("Login", new Color(76, 175, 80)); // green
        JButton registerButton = createStyledButton("Register", new Color(33, 150, 243)); // blue

        loginButton.addActionListener(e -> {
            dispose();
            new LoginScreen(); // transition
        });

        registerButton.addActionListener(e -> {
            dispose();
            new RegisterScreen(); // transition
        });

        panel.add(loginButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(registerButton);

        add(panel);
        setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(280, 45));
        button.setMaximumSize(new Dimension(280, 45));
        button.setOpaque(true);

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public static void showWelcome() {
        SwingUtilities.invokeLater(WelcomeScreen::new);
    }

//    public static void main(String[] args) {
//        showWelcome();
//    }
}
