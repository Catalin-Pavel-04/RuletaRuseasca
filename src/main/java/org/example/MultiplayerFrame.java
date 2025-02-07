package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Clasa MultiplayerFrame reprezinta interfata grafica pentru modul de joc multiplayer al aplicatiei Russian Roulette.
 * Ofera functionalitati pentru gestionarea tururilor, afisarea vietilor jucatorilor si manipularea jocului.
 */
public class MultiplayerFrame extends JFrame {

    /**
     * Numele utilizatorului curent (Player 1).
     */
    private final String username;

    /**
     * Lista jucatorilor din sesiunea multiplayer.
     */
    private final List<String> players;

    /**
     * Instanta UserManager utilizata pentru gestionarea datelor utilizatorilor.
     */
    private final UserManager userManager;

    /**
     * Instanta MultiplayerGame care gestioneaza logica jocului multiplayer.
     */
    private final MultiplayerGame game;

    /**
     * Zona de text pentru afisarea jurnalului de joc.
     */
    private JTextArea gameLog;

    /**
     * Buton pentru actiunea "Shoot Yourself".
     */
    private JButton shootSelfButton;

    /**
     * Buton pentru actiunea "Shoot Opponent".
     */
    private JButton shootOpponentButton;

    /**
     * Panou pentru afisarea vietilor jucatorilor.
     */
    private JPanel playerLivesPanel;

    /**
     * Eticheta pentru afisarea numarului de live shells.
     */
    private JLabel liveShellsLabel;

    /**
     * Eticheta pentru afisarea numarului de blank shells.
     */
    private JLabel blankShellsLabel;

    /**
     * Indexul jucatorului curent care joaca.
     */
    private int currentPlayerIndex;

    /**
     * Constructor pentru clasa MultiplayerFrame.
     *
     * @param players Lista jucatorilor din sesiunea multiplayer.
     * @param userManager Instanta UserManager utilizata pentru gestionarea utilizatorilor.
     * @param lives Numarul de vieti initiale pentru fiecare jucator.
     */
    public MultiplayerFrame(List<String> players, UserManager userManager, int lives) {
        this.username = players.get(0); // Seteaza Player 1 ca utilizator curent conectat
        this.players = players;
        this.userManager = userManager;
        this.game = new MultiplayerGame(players, userManager, lives);
        this.currentPlayerIndex = 0;

        setTitle("Multiplayer Mode");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        preloadShells(); // Preload the shells for the first round
    }

    /**
     * Initializeaza interfata grafica a ferestrei pentru modul multiplayer.
     */
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        gameLog = new JTextArea();
        gameLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(gameLog);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(4, 1));

        JPanel shellPanel = new JPanel(new GridLayout(1, 2));
        liveShellsLabel = new JLabel("Live Shells: 0", SwingConstants.CENTER);
        blankShellsLabel = new JLabel("Blank Shells: 0", SwingConstants.CENTER);
        shellPanel.add(liveShellsLabel);
        shellPanel.add(blankShellsLabel);
        controlPanel.add(shellPanel);

        playerLivesPanel = new JPanel(new GridLayout(players.size(), 1));
        updatePlayerLives(); // Populate player lives panel
        controlPanel.add(playerLivesPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        shootSelfButton = new JButton("Shoot Yourself");
        shootOpponentButton = new JButton("Shoot Opponent");
        buttonPanel.add(shootSelfButton);
        buttonPanel.add(shootOpponentButton);
        controlPanel.add(buttonPanel);

        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            new MainMenuFrame(userManager, players.get(0)).setVisible(true);
            dispose();
        });
        controlPanel.add(backButton);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        shootSelfButton.addActionListener(e -> playRound("self"));
        shootOpponentButton.addActionListener(e -> playRound("opponent"));

        updateInterface();
    }

    /**
     * Gestioneaza o runda a jocului pe baza actiunii selectate.
     *
     * @param action Actiunea efectuata: "self" pentru a te impusca sau "opponent" pentru a trage in adversar.
     */
    private void playRound(String action) {
        if (game.isGameOver()) {
            endGame();
            return;
        }

        shootSelfButton.setEnabled(false);
        shootOpponentButton.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            try {
                gameLog.setText(""); // Clear previous round logs
                String result;
                if ("self".equals(action)) {
                    result = game.shootSelf(currentPlayerIndex);
                } else {
                    int opponentIndex = game.getRandomOpponentIndex(currentPlayerIndex);
                    result = game.shootOpponent(currentPlayerIndex, opponentIndex);
                }

                gameLog.append(result + "\n");
                updateInterface();

                if (game.isGameOver()) {
                    endGame();
                } else {
                    // Move to the next player
                    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                    while (game.getPlayerLives(currentPlayerIndex) <= 0) {
                        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                    }
                    gameLog.append("\nIt's " + players.get(currentPlayerIndex) + "'s turn!\n");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                shootSelfButton.setEnabled(!game.isGameOver());
                shootOpponentButton.setEnabled(!game.isGameOver());
            }
        });
    }

    /**
     * Incarca gloantele in camera inainte de inceperea primei runde.
     */
    void preloadShells() {
        game.reloadGun(); // Preload shells before the game starts
        gameLog.append("🔴 Preloaded Live Shells: " + game.getNumLiveShells() + ", 🔵 Blank Shells: " + game.getNumBlankShells() + "\n");
        updateInterface();
    }

    /**
     * Actualizeaza panoul de vieti pentru toti jucatorii.
     */
    private void updatePlayerLives() {
        playerLivesPanel.removeAll(); // Clear existing components
        for (int i = 0; i < players.size(); i++) {
            JLabel playerLabel = new JLabel(players.get(i) + ": " + game.getPlayerLives(i) + " ❤️");
            playerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            playerLivesPanel.add(playerLabel);
        }
        playerLivesPanel.revalidate();
        playerLivesPanel.repaint();
    }

    /**
     * Actualizeaza interfata grafica, inclusiv vietile si starea jocului.
     */
    private void updateInterface() {
        liveShellsLabel.setText("Live Shells: " + game.getNumLiveShells());
        blankShellsLabel.setText("Blank Shells: " + game.getNumBlankShells());
        updatePlayerLives(); // Update lives display
    }

    /**
     * Finalizeaza jocul si revine la meniul principal.
     */
    private void endGame() {
        JOptionPane.showMessageDialog(this, "Game Over!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        new MainMenuFrame(userManager, username).setVisible(true); // Transmite username-ul curent
        dispose();
    }
}
