package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * Clasa SingleplayerFrame reprezinta interfata grafica pentru modul singleplayer al aplicatiei Russian Roulette.
 * Permite utilizatorilor sa joace impotriva dealerului, gestionand tururile, afisarea vietilor si interactiunile din joc.
 */
public class SingleplayerFrame extends JFrame {

    /**
     * Numele utilizatorului autentificat.
     */
    private final String username;

    /**
     * Instanta UserManager utilizata pentru gestionarea datelor utilizatorilor.
     */
    private final UserManager userManager;

    /**
     * Instanta Game care gestioneaza logica jocului.
     */
    private final Game game;

    /**
     * Zona de text pentru afisarea jurnalului de joc.
     */
    private JTextArea gameLog;

    /**
     * Buton pentru actiunea "Shoot Yourself".
     */
    private JButton shootSelfButton;

    /**
     * Buton pentru actiunea "Shoot Dealer".
     */
    private JButton shootDealerButton;

    /**
     * Eticheta pentru afisarea vietilor jucatorului.
     */
    private JLabel playerLifeLabel;

    /**
     * Eticheta pentru afisarea vietilor dealerului.
     */
    private JLabel dealerLifeLabel;

    /**
     * Eticheta pentru afisarea numarului de gloante live.
     */
    private JLabel liveShellsLabel;

    /**
     * Eticheta pentru afisarea numarului de gloante blank.
     */
    private JLabel blankShellsLabel;

    /**
     * Constructor pentru clasa SingleplayerFrame.
     *
     * @param username Numele utilizatorului autentificat.
     * @param userManager Instanta UserManager utilizata pentru gestionarea utilizatorilor.
     * @param lives Numarul initial de vieti ale jucatorului si ale dealerului.
     */
    public SingleplayerFrame(String username, UserManager userManager, int lives) {
        this.username = username != null ? username : "Guest"; // Asigura initializarea username-ului
        this.userManager = userManager;
        this.game = new Game(this.username, userManager);
        this.game.setPlayerLife(lives);
        this.game.setDealerLife(lives);

        setTitle("Singleplayer Mode");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    /**
     * Initializeaza interfata grafica a ferestrei pentru modul singleplayer.
     */
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        gameLog = new JTextArea();
        gameLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(gameLog);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(4, 1));

        JPanel lifePanel = new JPanel(new GridLayout(1, 2));
        playerLifeLabel = new JLabel("Player Lives: ❤️❤️❤️", SwingConstants.CENTER);
        dealerLifeLabel = new JLabel("Dealer Lives: ❤️❤️❤️", SwingConstants.CENTER);
        lifePanel.add(playerLifeLabel);
        lifePanel.add(dealerLifeLabel);
        controlPanel.add(lifePanel);

        JPanel shellPanel = new JPanel(new GridLayout(1, 2));
        liveShellsLabel = new JLabel("Live Shells: 0", SwingConstants.CENTER);
        blankShellsLabel = new JLabel("Blank Shells: 0", SwingConstants.CENTER);
        shellPanel.add(liveShellsLabel);
        shellPanel.add(blankShellsLabel);
        controlPanel.add(shellPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        shootSelfButton = new JButton("Shoot Yourself");
        shootDealerButton = new JButton("Shoot Dealer");
        buttonPanel.add(shootSelfButton);
        buttonPanel.add(shootDealerButton);
        controlPanel.add(buttonPanel);

        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            new MainMenuFrame(userManager, this.username).setVisible(true);
            dispose();
        });
        controlPanel.add(backButton);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        shootSelfButton.addActionListener(e -> playRound("s"));
        shootDealerButton.addActionListener(e -> playRound("d"));

        updateInterface();
    }

    /**
     * Gestioneaza o runda a jocului pe baza actiunii utilizatorului.
     *
     * @param action Actiunea efectuata: "s" pentru a te impusca sau "d" pentru a trage in dealer.
     */
    void playRound(String action) {
        if (game.isGameOver()) {
            askToContinueOrEnd();
            return;
        }

        shootSelfButton.setEnabled(false);
        shootDealerButton.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            try {
                gameLog.setText(""); // Curata jurnalul rundelor anterioare
                String result = game.playRoundSwing(action);
                gameLog.append(result + "\n");
                updateInterface();

                if (game.isGameOver()) {
                    askToContinueOrEnd();
                } else if (!game.isPlayerTurn()) {
                    // Automatizeaza tura dealerului
                    String dealerResult = game.dealerTurn();
                    gameLog.append(dealerResult + "\n");
                    updateInterface();

                    if (game.isGameOver()) {
                        askToContinueOrEnd();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                // Reactiveaza butoanele doar daca jocul nu s-a terminat si este tura jucatorului
                shootSelfButton.setEnabled(!game.isGameOver());
                shootDealerButton.setEnabled(!game.isGameOver());
            }
        });
    }

    /**
     * Gestioneaza optiunile de continuare sau de terminare a jocului dupa finalizare.
     */
    private void askToContinueOrEnd() {
        int finalScore = calculateScore();

        int currentHighScore = userManager.getHighScore(username);
        if (finalScore > currentHighScore) {
            userManager.actualizareScor(username, finalScore); // Actualizeaza doar daca scorul final este mai mare
        }

        if (game.getPlayerLife() <= 0) {
            // Jucatorul a pierdut, fara optiune de continuare
            JOptionPane.showMessageDialog(this, "You Lost! Your final score is " + finalScore + ".", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            new MainMenuFrame(userManager, this.username).setVisible(true);
            dispose();
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Your current score is " + finalScore + ".\nDo you want to continue playing for a higher score?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            // Reseteaza starea jocului pentru continuare
            game.resetGameState();
            gameLog.append("\n🎲 You chose to continue playing! Good luck!\n");
            updateInterface();
        } else {
            JOptionPane.showMessageDialog(this, "Your final score is " + finalScore + ".", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            new MainMenuFrame(userManager, this.username).setVisible(true);
            dispose();
        }
    }

    /**
     * Calculeaza scorul final al jucatorului.
     *
     * @return Scorul final calculat pe baza numarului de vieti si runde.
     */
    private int calculateScore() {
        int multiplier;
        switch (game.getPlayerLife()) {
            case 5 -> multiplier = 50;
            case 4 -> multiplier = 100;
            case 3 -> multiplier = 150;
            case 2 -> multiplier = 200;
            case 1 -> multiplier = 300;
            default -> multiplier = 0;
        }
        return game.getRoundNumber() * multiplier;
    }

    private void updateInterface() {
        playerLifeLabel.setText("Player Lives: " + "❤️".repeat(game.getPlayerLife()));
        dealerLifeLabel.setText("Dealer Lives: " + "❤️".repeat(game.getDealerLife()));
        liveShellsLabel.setText("Live Shells: " + game.getNumLiveShells());
        blankShellsLabel.setText("Blank Shells: " + game.getNumBlankShells());
    }
}
