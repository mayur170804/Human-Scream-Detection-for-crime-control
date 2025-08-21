import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class RegisterScreen extends JFrame {

    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JProgressBar strengthBar;
    private JLabel strengthLabel;

    public RegisterScreen() {
        setTitle("Register - Human Scream Detection");
        setSize(500, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);

        // Inner panel
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.setBackground(Color.WHITE);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Title
        JLabel title = new JLabel("📝 Create a New Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Username field
        JTextField usernameField = createStyledTextField("Username");

        // Password fields with eye icon
        passwordField = createStyledPasswordField();
        JButton passwordToggle = createEyeButton(passwordField);
        JPanel passwordPanel = createPasswordPanel(passwordField, passwordToggle, "Password");

        confirmPasswordField = createStyledPasswordField();
        JButton confirmToggle = createEyeButton(confirmPasswordField);
        JPanel confirmPanel = createPasswordPanel(confirmPasswordField, confirmToggle, "Confirm Password");

        // Strength meter
        strengthLabel = new JLabel("Password Strength:");
        strengthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        strengthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        strengthBar = new JProgressBar(0, 100);
        strengthBar.setValue(0);
        strengthBar.setStringPainted(true);
        strengthBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        strengthBar.setMaximumSize(new Dimension(300, 20));

        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updatePasswordStrength(new String(passwordField.getPassword()));
            }

            public void removeUpdate(DocumentEvent e) {
                updatePasswordStrength(new String(passwordField.getPassword()));
            }

            public void changedUpdate(DocumentEvent e) {
                updatePasswordStrength(new String(passwordField.getPassword()));
            }
        });

        // Buttons
        JButton registerButton = createStyledButton("Register", new Color(76, 175, 80));
        JButton backButton = createStyledButton("← Back to Login", new Color(33, 150, 243));

        registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Please fill in all fields.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "❗ Passwords do not match.");
                return;
            }

            if (DatabaseManager.userExists(username)) {
                JOptionPane.showMessageDialog(this, "❌ Username already exists. Choose another.");
                return;
            }

            String hashedPassword = PasswordUtils.hashPassword(password);
            if (DatabaseManager.addUser(username, hashedPassword)) {
                JOptionPane.showMessageDialog(this, "✅ Registration successful. Please login.");
                dispose();
                new LoginScreen();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error occurred during registration.");
            }
        });

        backButton.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });

        // Layout
        innerPanel.add(title);
        innerPanel.add(usernameField);
        innerPanel.add(passwordPanel);
        innerPanel.add(confirmPanel);
        innerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        innerPanel.add(strengthLabel);
        innerPanel.add(strengthBar);
        innerPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        innerPanel.add(registerButton);
        innerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        innerPanel.add(backButton);

        mainPanel.add(innerPanel);
        add(mainPanel);
        setVisible(true);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(300, 45));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createTitledBorder(placeholder));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setEchoChar('•');
        field.setBorder(null);
        return field;
    }

    private JPanel createPasswordPanel(JPasswordField passwordField, JButton eyeButton, String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setMaximumSize(new Dimension(300, 45));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(Color.WHITE);

        passwordField.setPreferredSize(new Dimension(260, 30));
        passwordField.setMaximumSize(new Dimension(260, 30));

        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(eyeButton);
        return panel;
    }

    private JButton createEyeButton(JPasswordField targetField) {
        JButton button = new JButton("👁");
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(35, 30));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            if (targetField.getEchoChar() == (char) 0) {
                targetField.setEchoChar('•');
            } else {
                targetField.setEchoChar((char) 0);
            }
        });

        return button;
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

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void updatePasswordStrength(String password) {
        int strength = 0;
        if (password.length() >= 8) strength += 20;
        if (password.matches(".*[a-z].*")) strength += 20;
        if (password.matches(".*[A-Z].*")) strength += 20;
        if (password.matches(".*\\d.*")) strength += 20;
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) strength += 20;

        strengthBar.setValue(strength);

        if (strength < 40) {
            strengthBar.setForeground(Color.RED);
            strengthBar.setString("Weak");
        } else if (strength < 80) {
            strengthBar.setForeground(Color.ORANGE);
            strengthBar.setString("Medium");
        } else {
            strengthBar.setForeground(new Color(0, 128, 0)); // Dark Green
            strengthBar.setString("Strong");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegisterScreen::new);
    }
}
