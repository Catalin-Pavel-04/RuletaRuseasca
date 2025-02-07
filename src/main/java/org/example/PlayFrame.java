package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa PlayFrame reprezinta interfata grafica pentru selectarea modului de joc in aplicatia Russian Roulette.
 * Permite utilizatorului sa aleaga intre modul singleplayer si multiplayer.
 */
public class PlayFrame extends JFrame {

    /**
     * Numele utilizatorului autentificat.
     */
    private final String username;

    /**
     * Instanta UserManager utilizata pentru gestionarea utilizatorilor.
     */
    private final UserManager userManager;

    /**
     * Constructor pentru clasa PlayFrame.
     *
     * @param username Numele utilizatorului autentificat.
     * @param userManager Instanta UserManager utilizata pentru gestionarea utilizatorilor.
     */
    public PlayFrame(String username, UserManager userManager) {
        this.username = username;
        this.userManager = userManager;

        setTitle("Select Game Mode");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    /**
     * Initializeaza interfata grafica a ferestrei pentru selectarea modului de joc.
     */
    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        JLabel label = new JLabel("Choose Game Mode", SwingConstants.CENTER);
        panel.add(label);

        JButton singleplayerButton = new JButton("Singleplayer");
        JButton multiplayerButton = new JButton("Multiplayer");

        panel.add(singleplayerButton);
        panel.add(multiplayerButton);

        add(panel);

        singleplayerButton.addActionListener(e -> startSingleplayerGame());
        multiplayerButton.addActionListener(e -> openMultiplayerSetup());
    }

    /**
     * Gestioneaza logica pentru lansarea modului singleplayer.
     * Solicita utilizatorului sa introduca numarul de vieti.
     */
    private void startSingleplayerGame() {
        String livesInput = JOptionPane.showInputDialog(this, "Enter number of lives (1-5):", "3");
        try {
            int lives = Integer.parseInt(livesInput);
            if (lives < 1 || lives > 5) {
                JOptionPane.showMessageDialog(this, "Invalid number of lives. Defaulting to 3.", "Error", JOptionPane.ERROR_MESSAGE);
                lives = 3;
            }

            new SingleplayerFrame(username, userManager, lives).setVisible(true);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Returning to main menu.", "Error", JOptionPane.ERROR_MESSAGE);
            new MainMenuFrame(userManager).setVisible(true);
            dispose();
        }
    }

    /**
     * Gestioneaza logica pentru configurarea si lansarea modului multiplayer.
     * Solicita utilizatorului sa introduca numarul de jucatori si numele acestora.
     */
    private void openMultiplayerSetup() {
        String playerCountInput = JOptionPane.showInputDialog(this, "Enter number of players (2-4):", "2");
        try {
            int playerCount = Integer.parseInt(playerCountInput);
            if (playerCount < 2 || playerCount > 4) {
                JOptionPane.showMessageDialog(this, "Invalid number of players. Defaulting to 2.", "Error", JOptionPane.ERROR_MESSAGE);
                playerCount = 2;
            }

            // Initialize players list with the current user as Player 1
            List<String> players = new ArrayList<>();
            players.add(username); // Player 1 is the current user

            // Ask for names of other players
            for (int i = 2; i <= playerCount; i++) {
                String playerName = JOptionPane.showInputDialog(this, "Enter name for Player " + i + ":", "Player " + i);
                if (playerName == null || playerName.trim().isEmpty()) {
                    playerName = "Player " + i; // Default name
                }
                players.add(playerName);
            }

            // Start MultiplayerFrame
            new MultiplayerFrame(players, userManager, 3).setVisible(true);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Returning to main menu.", "Error", JOptionPane.ERROR_MESSAGE);
            new MainMenuFrame(userManager, username).setVisible(true);
            dispose();
        }
    }

}