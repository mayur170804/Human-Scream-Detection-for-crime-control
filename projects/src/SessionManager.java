import java.io.*;

public class SessionManager {
    private static final String SESSION_FILE = "session.txt";

    public static boolean isUserLoggedIn() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SESSION_FILE))) {
            String status = reader.readLine();
            return "true".equalsIgnoreCase(status);
        } catch (IOException e) {
            return false;
        }
    }

    public static void setUserLoggedIn(boolean loggedIn) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_FILE))) {
            writer.write(String.valueOf(loggedIn));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logout() {
        setUserLoggedIn(false);
        // Clear scream_location_log.txt upon logout
        try (PrintWriter writer = new PrintWriter("scream_location_log.txt")) {
            writer.print(""); // Clear contents
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}