public class Main {
    public static void main(String[] args) {
        if (SessionManager.isUserLoggedIn()) {
            new AudioMonitorScreen(); // or whatever screen should show after login
        } else {
            WelcomeScreen.showWelcome();
        }
    }
}