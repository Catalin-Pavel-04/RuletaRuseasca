package org.example;

import javax.swing.*;
import java.awt.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.List;

/**
 * Clasa MainMenuFrame reprezinta meniul principal al aplicatiei Russian Roulette.
 * Ofera optiuni pentru a juca, a vizualiza clasamentul, a te autentifica, a te inregistra sau a iesi din aplicatie.
 */
public class MainMenuFrame extends JFrame {

    /**
     * Instanta UserManager utilizata pentru gestionarea utilizatorilor.
     */
    private final UserManager userManager;

    /**
     * Numele utilizatorului autentificat curent.
     */
    private String username;

    /**
     * Eticheta pentru afisarea mesajului de bun venit.
     */
    private JLabel welcomeLabel;

    /**
     * Constructor implicit care creeaza un meniu principal fara un utilizator autentificat.
     *
     * @param userManager Instanta UserManager care gestioneaza utilizatorii.
     */
    public MainMenuFrame(UserManager userManager) {
        this(userManager, null);
    }

    /**
     * Constructor care creeaza un meniu principal cu un utilizator autentificat.
     *
     * @param userManager Instanta UserManager care gestioneaza utilizatorii.
     * @param username    Numele utilizatorului autentificat (poate fi null).
     */
    public MainMenuFrame(UserManager userManager, String username) {
        this.userManager = userManager;
        this.username = username;

        setTitle("Main Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();

        if (username != null) {
            welcomeLabel.setText("Welcome, " + username + "!");
        }
    }

    /**
     * Initializeaza interfata grafica a meniului principal.
     */
    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(6, 1));
        welcomeLabel = new JLabel("Welcome to Russian Roulette", SwingConstants.CENTER);
        panel.add(welcomeLabel);

        JButton playButton = new JButton("Play");
        JButton scoreboardButton = new JButton("Scoreboard");
        JButton loginButton = new JButton("Log In");
        JButton signupButton = new JButton("Sign Up");
        JButton exitButton = new JButton("Exit");

        panel.add(playButton);
        panel.add(scoreboardButton);
        panel.add(loginButton);
        panel.add(signupButton);
        panel.add(exitButton);

        add(panel);

        playButton.addActionListener(e -> openPlayMenu());
        scoreboardButton.addActionListener(e -> showScoreboard());
        loginButton.addActionListener(e -> openLoginWindow());
        signupButton.addActionListener(e -> openSignupWindow());
        exitButton.addActionListener(e -> System.exit(0));
    }

    /**
     * Deschide meniul de selectie a modului de joc.
     */
    private void openPlayMenu() {
        if (username == null) {
            JOptionPane.showMessageDialog(this, "You need to log in to play.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            new PlayFrame(username, userManager).setVisible(true);
            dispose();
        }
    }

    /**
     * Afiseaza clasamentul utilizatorilor intr-o fereastra separata.
     */
    private void showScoreboard() {
        JFrame scoreboardFrame = new JFrame("Scoreboard");
        scoreboardFrame.setSize(400, 300);
        scoreboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        scoreboardFrame.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("🏆 Scoreboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        JTextArea scoreboardArea = new JTextArea();
        scoreboardArea.setEditable(false);

        List<Document> utilizatori = userManager.getScoreboard();
        if (utilizatori.isEmpty()) {
            scoreboardArea.setText("⚠️ No players registered yet.");
        } else {
            StringBuilder sb = new StringBuilder();
            int rank = 1;
            for (Document user : utilizatori) {
                if (!"admin".equals(user.getString("username"))) {
                    sb.append(rank++).append(". ")
                            .append(user.getString("username"))
                            .append(" - ")
                            .append(user.getInteger("scor", 0)).append(" points\n");
                }
            }
            scoreboardArea.setText(sb.toString());
        }

        JScrollPane scrollPane = new JScrollPane(scoreboardArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            new MainMenuFrame(userManager, username).setVisible(true);
            scoreboardFrame.dispose();
        });
        panel.add(backButton, BorderLayout.SOUTH);

        scoreboardFrame.add(panel);
        scoreboardFrame.setVisible(true);
    }

    /**
     * Deschide fereastra de autentificare.
     */
    private void openLoginWindow() {
        new LoginFrame(userManager, this::setUsername).setVisible(true);
        dispose();
    }

    /**
     * Deschide fereastra de inregistrare.
     */
    private void openSignupWindow() {
        new SignupFrame(userManager).setVisible(true);
        dispose();
    }

    /**
     * Seteaza numele utilizatorului autentificat curent.
     *
     * @param username Numele utilizatorului autentificat.
     */
    private void setUsername(String username) {
        this.username = username;
        welcomeLabel.setText("Welcome, " + username + "!");
        JOptionPane.showMessageDialog(this, "Logged in as: " + username, "Login Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Metoda principala care lanseaza aplicatia.
     *
     * @param args Argumente din linia de comanda.
     */
    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("proiect_russian_rulet");
        MongoCollection<Document> userCollection = database.getCollection("utilizatori");

        UserManager userManager = new UserManager(userCollection);

        SwingUtilities.invokeLater(() -> new MainMenuFrame(userManager, null).setVisible(true));
    }
}

