import javax.swing.*;

public class HomeScreen {
    public static void showHome(String username) {
        JFrame frame = new JFrame("Welcome");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JLabel label = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
        frame.add(label);

        frame.setVisible(true);
    }
}
