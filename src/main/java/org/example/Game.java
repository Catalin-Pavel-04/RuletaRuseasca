package org.example;

import java.util.Collections;
import java.util.Random;
import java.util.Stack;

/**
 * Clasa Game gestioneaza logica jocului pentru modul singleplayer al aplicatiei Russian Roulette.
 * Include functionalitati precum initializarea jocului, desfasurarea rundelor, reincarcarea camerei
 * si verificarea conditiilor de finalizare a jocului.
 */
public class Game {

    /**
     * Numele utilizatorului care joaca jocul.
     */
    private final String username;

    /**
     * Instanta UserManager utilizata pentru gestionarea datelor utilizatorilor.
     */
    private final UserManager userManager;

    /**
     * Instanta Random utilizata pentru generarea aleatoarelor.
     */
    private final Random random = new Random();

    /**
     * Camera cu gloante, reprezentata ca un stack de ShellType.
     */
    private final Stack<ShellType> chamber = new Stack<>();

    /**
     * Numarul curent de vieti ale jucatorului.
     */
    private int playerLife;

    /**
     * Numarul curent de vieti ale dealerului.
     */
    private int dealerLife;

    /**
     * Vietile initiale ale jucatorului.
     */
    private int initialPlayerLife;

    /**
     * Vietile initiale ale dealerului.
     */
    private int initialDealerLife;

    /**
     * Numarul curent al rundei.
     */
    private int roundNum = 0;

    /**
     * Scorul curent al jucatorului.
     */
    private int score = 0;

    /**
     * Indica daca jocul s-a terminat.
     */
    private boolean isGameOver = false;

    /**
     * Indica daca este tura jucatorului.
     */
    private boolean isPlayerTurn = true;

    /**
     * Numarul de gloante live din camera.
     */
    private int numLiveShells;

    /**
     * Numarul de gloante blank din camera.
     */
    private int numBlankShells;

    /**
     * Tipurile de gloante disponibile: LIVE_SHELL sau BLANK_SHELL.
     */
    private enum ShellType {
        LIVE_SHELL, BLANK_SHELL
    }

    /**
     * Constructor pentru clasa Game.
     *
     * @param username Numele utilizatorului care joaca jocul.
     * @param userManager Instanta UserManager care gestioneaza datele utilizatorilor.
     */
    public Game(String username, UserManager userManager) {
        this.username = username;
        this.userManager = userManager;
    }

    /**
     * Seteaza numarul de vieti ale jucatorului.
     *
     * @param life Numarul de vieti al jucatorului.
     */
    public void setPlayerLife(int life) {
        this.playerLife = life;
        this.initialPlayerLife = life;
    }

    /**
     * Seteaza numarul de vieti ale dealerului.
     *
     * @param life Numarul de vieti al dealerului.
     */
    public void setDealerLife(int life) {
        this.dealerLife = life;
        this.initialDealerLife = life;
    }

    /**
     * Reseteaza starea jocului la valorile initiale.
     */
    public void resetGameState() {
        chamber.clear();
        isGameOver = false;
        isPlayerTurn = true;
        numLiveShells = 0;
        numBlankShells = 0;

        playerLife = initialPlayerLife;
        dealerLife = initialDealerLife;
    }

    /**
     * Desfasoara o runda a jocului bazata pe actiunea utilizatorului.
     *
     * @param action Actiunea utilizatorului: "s" pentru a se impusca sau "d" pentru a trage in dealer.
     * @return Rezultatul rundei ca un sir de caractere.
     */
    public String playRoundSwing(String action) {
        StringBuilder roundSummary = new StringBuilder();

        // Incrementeaza numarul rundei doar in tura jucatorului
        if (isPlayerTurn) {
            roundNum++;
            roundSummary.append("\n🔄 Round: ").append(roundNum).append("\n");
        }

        // Reincarca camera daca este goala
        if (chamber.isEmpty()) {
            numLiveShells = random.nextInt(5) + 1;
            numBlankShells = 6 - numLiveShells;

            roundSummary.append("🔴 Live Shell(s): ").append(numLiveShells)
                    .append(", 🔵 Blank Shell(s): ").append(numBlankShells).append("\n");

            reloadGun(numLiveShells, numBlankShells);
        }

        // Actiunea utilizatorului sau a dealerului
        roundSummary.append((isPlayerTurn ? "Player" : "Dealer") + " action: ")
                .append(action.equals("s") ? "Shot Self" : "Shot Opponent").append("\n");

        ShellType currentShell = chamber.pop();
        if (currentShell == ShellType.LIVE_SHELL) {
            if ("s".equals(action)) {
                if (isPlayerTurn) {
                    playerLife--;
                    roundSummary.append("💥 Hit! You lose one health 🧑 💔\n");
                } else {
                    dealerLife--;
                    roundSummary.append("💥 Hit! Dealer loses one health 🤡 💔\n");
                }
            } else {
                if (isPlayerTurn) {
                    dealerLife--;
                    roundSummary.append("💥 Hit! Dealer loses one health 🤡 💔\n");
                } else {
                    playerLife--;
                    roundSummary.append("💥 Hit! You lose one health 🧑 💔\n");
                }
                isPlayerTurn = !isPlayerTurn; // Schimba tura doar daca tragi in dealer
            }
        } else {
            if ("s".equals(action)) {
                roundSummary.append("💨 Miss! You survive and keep your turn.\n");
            } else {
                roundSummary.append("💨 Miss! No one is hit, and turn changes.\n");
                isPlayerTurn = !isPlayerTurn;
            }
        }

        // Verifica daca jocul s-a terminat
        if (playerLife <= 0) {
            isGameOver = true;
            roundSummary.append("\n💀 Game over! You lost all lives\n");
        } else if (dealerLife <= 0) {
            isGameOver = true;
            roundSummary.append("\n🎉 You win! The dealer lost all lives\n");
        }

        return roundSummary.toString();
    }

    /**
     * Gestioneaza tura dealerului.
     *
     * @return Rezultatul actiunii dealerului.
     */
    public String dealerTurn() {
        if (!isGameOver) {
            StringBuilder result = new StringBuilder("\n🤡 Dealer's turn...\n");
            String action = random.nextBoolean() ? "s" : "d";
            result.append(playRoundSwing(action));

            if (dealerLife > 0) {
                result.append("\nDealer survived! It's your turn.\n");
            } else {
                isGameOver = true;
                result.append("\n💥 Dealer lost all lives! 🎉 You win!\n");
            }

            return result.toString();
        }
        return "\n⚠️ Game is over. No more actions.\n";
    }

    /**
     * Reincarca camera cu numarul specificat de gloante live si blank.
     *
     * @param numLiveShells Numarul de gloante live.
     * @param numBlankShells Numarul de gloante blank.
     */
    private void reloadGun(int numLiveShells, int numBlankShells) {
        chamber.clear();
        for (int i = 0; i < numLiveShells; i++) {
            chamber.push(ShellType.LIVE_SHELL);
        }
        for (int i = 0; i < numBlankShells; i++) {
            chamber.push(ShellType.BLANK_SHELL);
        }
        Collections.shuffle(chamber);
    }

    /**
     * Verifica daca jocul este terminat.
     *
     * @return True daca jocul este terminat, altfel False.
     */
    public boolean isGameOver() {
        return isGameOver;
    }

    /**
     * Returneaza numarul curent de vieti ale jucatorului.
     *
     * @return Numarul de vieti ale jucatorului.
     */
    public int getPlayerLife() {
        return playerLife;
    }

    /**
     * Returneaza numarul curent de vieti ale dealerului.
     *
     * @return Numarul de vieti ale dealerului.
     */
    public int getDealerLife() {
        return dealerLife;
    }

    /**
     * Verifica daca este tura jucatorului.
     *
     * @return True daca este tura jucatorului, altfel False.
     */
    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    /**
     * Returneaza numarul de gloante live din camera.
     *
     * @return Numarul de gloante live.
     */
    public int getNumLiveShells() {
        return numLiveShells;
    }

    /**
     * Returneaza numarul de gloante blank din camera.
     *
     * @return Numarul de gloante blank.
     */
    public int getNumBlankShells() {
        return numBlankShells;
    }

    /**
     * Returneaza numarul curent al rundei.
     *
     * @return Numarul curent al rundei.
     */
    public int getRoundNumber() {
        return roundNum;
    }

}
