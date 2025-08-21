import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {

    public LoginScreen() {
        setTitle("Login - Human Scream Detection");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 245, 245));  // Light gray

        // Inner panel
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.setBackground(Color.WHITE);
        innerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        // Title
        JLabel title = new JLabel("Login to Continue");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Username and password fields
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        styleInput(usernameField, "Username");

        // Password with visibility toggle
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
        passwordPanel.setMaximumSize(new Dimension(300, 45));
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBorder(BorderFactory.createTitledBorder("Password"));

        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setEchoChar('●');
        passwordField.setBorder(null);

        JButton toggleBtn = new JButton("👁");
        toggleBtn.setFocusPainted(false);
        toggleBtn.setBorderPainted(false);
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        toggleBtn.addActionListener(e -> {
            if (passwordField.getEchoChar() == 0) {
                passwordField.setEchoChar('●');
            } else {
                passwordField.setEchoChar((char) 0);
            }
        });

        passwordPanel.add(passwordField);
        passwordPanel.add(Box.createHorizontalStrut(5));
        passwordPanel.add(toggleBtn);

        // Login Button - green
        JButton loginBtn = createStyledButton("Login", new Color(76, 175, 80));
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            String storedHash = DatabaseManager.getUserPasswordHash(username);
            if (storedHash != null && PasswordUtils.checkPassword(password, storedHash)) {
                JOptionPane.showMessageDialog(this, "✅ Login successful.");
                SessionManager.setUserLoggedIn(true);
                dispose();
                new AudioMonitorScreen();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Invalid username or password.");
            }
        });

        // Pressing Enter triggers login
        getRootPane().setDefaultButton(loginBtn);

        // Back Button - blue-gray
        JButton backBtn = createStyledButton("← Back", new Color(33, 150, 243));
        backBtn.addActionListener(e -> {
            dispose();
            new WelcomeScreen();
        });

        // Layout
        innerPanel.add(title);
        innerPanel.add(usernameField);
        innerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        innerPanel.add(passwordPanel);
        innerPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        innerPanel.add(loginBtn);
        innerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        innerPanel.add(backBtn);

        mainPanel.add(innerPanel);
        add(mainPanel);
        setVisible(true);
    }

    private void styleInput(JTextField field, String placeholder) {
        field.setMaximumSize(new Dimension(300, 45));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createTitledBorder(placeholder));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(300, 45));
        button.setMaximumSize(new Dimension(300, 45));
        button.setOpaque(true);
        button.setBorderPainted(false);
        return button;
    }
}
