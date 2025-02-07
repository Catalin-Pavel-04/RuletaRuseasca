package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Clasa MultiplayerGame gestioneaza logica jocului pentru modul multiplayer al aplicatiei Russian Roulette.
 * Ofera functionalitati pentru reancarcarea camerei, gestionarea tururilor si determinarea castigatorului.
 */
public class MultiplayerGame {
    /**
     * Lista jucatorilor din sesiunea multiplayer.
     */
    private final List<String> players;

    /**
     * Instanta UserManager utilizata pentru gestionarea utilizatorilor.
     */
    private final UserManager userManager;

    /**
     * Lista vietilor curente ale jucatorilor.
     */
    private final List<Integer> playerLives;

    /**
     * Camera cu gloante, reprezentata ca un stack de ShellType.
     */
    private final Stack<ShellType> chamber = new Stack<>();

    /**
     * Instanta Random utilizata pentru generarea valorilor aleatorii.
     */
    private final Random random = new Random();

    /**
     * Indica daca jocul s-a terminat.
     */
    private boolean isGameOver = false;

    /**
     * Tipurile de gloante din camera: LIVE_SHELL sau BLANK_SHELL.
     */
    private enum ShellType {
        LIVE_SHELL, BLANK_SHELL
    }

    /**
     * Constructor pentru clasa MultiplayerGame.
     *
     * @param players Lista jucatorilor din sesiunea multiplayer.
     * @param userManager Instanta UserManager utilizata pentru gestionarea utilizatorilor.
     * @param initialLives Numarul de vieti initiale pentru fiecare jucator.
     */
    public MultiplayerGame(List<String> players, UserManager userManager, int initialLives) {
        this.players = players;
        this.userManager = userManager;
        this.playerLives = new ArrayList<>(Collections.nCopies(players.size(), initialLives));
    }

    /**
     * Reincarca camera cu un numar aleator de gloante live si blank.
     */
    public void reloadGun() {
        chamber.clear();
        int numLiveShells = random.nextInt(5) + 1;
        int numBlankShells = 6 - numLiveShells;
        for (int i = 0; i < numLiveShells; i++) {
            chamber.push(ShellType.LIVE_SHELL);
        }
        for (int i = 0; i < numBlankShells; i++) {
            chamber.push(ShellType.BLANK_SHELL);
        }
        Collections.shuffle(chamber);
    }

    /**
     * Ruleaza jocul pana cand se determina un castigator.
     */
    public void play() {
        int currentPlayerIndex = 0;
        while (!isGameOver) {
            String result = playRound(currentPlayerIndex);
            System.out.println(result);
            displayAllLives(); // Afiseaza vietile jucatorilor dupa fiecare runda

            // Muta la urmatorul jucator
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

            // Sare peste jucatorii care sunt eliminati
            while (playerLives.get(currentPlayerIndex) <= 0) {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            }
        }
    }

    /**
     * Desfasoara o runda pentru jucatorul specificat.
     *
     * @param playerIndex Indexul jucatorului curent.
     * @return Rezumatul rundei.
     */
    public String playRound(int playerIndex) {
        if (isGameOver) {
            return "Game is over. No further actions are allowed.";
        }

        String currentPlayer = players.get(playerIndex);
        StringBuilder roundSummary = new StringBuilder("\n🔄 Round for " + currentPlayer);

        if (chamber.isEmpty()) {
            reloadGun();
            roundSummary.append("\n🔴 Reloaded Live Shells: " + getNumLiveShells())
                    .append(", 🔵 Blank Shells: " + getNumBlankShells() + "\n");
        }

        roundSummary.append("\nChoose action: 1) Shoot Yourself  2) Shoot Opponent\n");
        // Simulated choice, default to shooting self for testing purposes
        int choice = 1; // Replace with actual input handling logic

        if (choice == 1) {
            return shootSelf(playerIndex);
        } else {
            // Simuleaza tragerea intr-un adversar aleator
            int opponentIndex = getRandomOpponentIndex(playerIndex);
            return shootOpponent(playerIndex, opponentIndex);
        }
    }

    /**
     * Simuleaza actiunea de a te impusca singur.
     *
     * @param playerIndex Indexul jucatorului curent.
     * @return Rezumatul actiunii.
     */
    public String shootSelf(int playerIndex) {
        if (isGameOver) {
            return "Game is over. No further actions are allowed.";
        }

        String currentPlayer = players.get(playerIndex);
        StringBuilder roundSummary = new StringBuilder("\n🔄 Round for " + currentPlayer);

        if (chamber.isEmpty()) {
            reloadGun();
            roundSummary.append("\n🔴 Reloaded Live Shells: " + getNumLiveShells())
                    .append(", 🔵 Blank Shells: " + getNumBlankShells() + "\n");
        }

        ShellType currentShell = chamber.pop();

        if (currentShell == ShellType.LIVE_SHELL) {
            int lives = playerLives.get(playerIndex) - 1;
            playerLives.set(playerIndex, lives);
            roundSummary.append("\n💥 Hit! " + currentPlayer + " shot themselves and lost one life.\n");

            if (lives <= 0) {
                roundSummary.append("\n💀 " + currentPlayer + " is out of the game!\n");
            }
        } else {
            roundSummary.append("\n💨 Miss! " + currentPlayer + " shot themselves but survived.\n");
        }

        checkGameOver(roundSummary);
        return roundSummary.toString();
    }

    /**
     * Simuleaza actiunea de a trage intr-un adversar.
     *
     * @param playerIndex Indexul jucatorului curent.
     * @param opponentIndex Indexul adversarului.
     * @return Rezumatul actiunii.
     */
    public String shootOpponent(int playerIndex, int opponentIndex) {
        if (isGameOver) {
            return "Game is over. No further actions are allowed.";
        }

        String currentPlayer = players.get(playerIndex);
        String opponentPlayer = players.get(opponentIndex);
        StringBuilder roundSummary = new StringBuilder("\n🔄 Round for " + currentPlayer);

        if (chamber.isEmpty()) {
            reloadGun();
            roundSummary.append("\n🔴 Reloaded Live Shells: " + getNumLiveShells())
                    .append(", 🔵 Blank Shells: " + getNumBlankShells() + "\n");
        }

        ShellType currentShell = chamber.pop();

        if (currentShell == ShellType.LIVE_SHELL) {
            int lives = playerLives.get(opponentIndex) - 1;
            playerLives.set(opponentIndex, lives);
            roundSummary.append("\n💥 Hit! " + currentPlayer + " shot " + opponentPlayer + " and they lost one life.\n");

            if (lives <= 0) {
                roundSummary.append("\n💀 " + opponentPlayer + " is out of the game!\n");
            }
        } else {
            roundSummary.append("\n💨 Miss! " + currentPlayer + " shot at " + opponentPlayer + " but missed.\n");
        }

        checkGameOver(roundSummary);
        return roundSummary.toString();
    }

    /**
     * Selecteaza un adversar aleator dintre jucatorii activi.
     *
     * @param currentPlayerIndex Indexul jucatorului curent.
     * @return Indexul adversarului selectat aleator.
     */
    public int getRandomOpponentIndex(int currentPlayerIndex) {
        List<Integer> activePlayers = new ArrayList<>();
        for (int i = 0; i < playerLives.size(); i++) {
            if (i != currentPlayerIndex && playerLives.get(i) > 0) {
                activePlayers.add(i);
            }
        }
        return activePlayers.get(random.nextInt(activePlayers.size()));
    }

    /**
     * Verifica daca jocul s-a terminat si determina castigatorul.
     *
     * @param roundSummary Rezumatul curent al rundei, unde se vor adauga mesajele de finalizare a jocului.
     */
    private void checkGameOver(StringBuilder roundSummary) {
        long activePlayers = playerLives.stream().filter(lives -> lives > 0).count();
        if (activePlayers <= 1) {
            isGameOver = true;
            int winnerIndex = playerLives.indexOf(playerLives.stream().max(Integer::compare).orElse(0));
            roundSummary.append("\n🎉 Game Over! Winner: " + players.get(winnerIndex) + "\n");
        }
    }

    /**
     * Afiseaza numarul curent de vieti ale fiecarui jucator.
     */
    private void displayAllLives() {
        System.out.println("\nCurrent Player Lives:");
        for (int i = 0; i < players.size(); i++) {
            System.out.println(players.get(i) + ": " + playerLives.get(i) + " lives");
        }
    }

    /**
     * Returneaza numarul de gloante live din camera.
     *
     * @return Numarul de gloante live.
     */
    public int getNumLiveShells() {
        return (int) chamber.stream().filter(shell -> shell == ShellType.LIVE_SHELL).count();
    }

    /**
     * Returneaza numarul de gloante blank din camera.
     *
     * @return Numarul de gloante blank.
     */
    public int getNumBlankShells() {
        return (int) chamber.stream().filter(shell -> shell == ShellType.BLANK_SHELL).count();
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
     * Returneaza numarul de vieti ale unui jucator specificat.
     *
     * @param playerIndex Indexul jucatorului.
     * @return Numarul de vieti ale jucatorului.
     */
    public int getPlayerLives(int playerIndex) {
        return playerLives.get(playerIndex);
    }

    /**
     * Returneaza o lista cu vietile tuturor jucatorilor.
     *
     * @return O lista de numere intregi reprezentand vietile fiecarui jucator.
     */
    public List<Integer> getAllPlayerLives() {
        return new ArrayList<>(playerLives);
    }

    /**
     * Returneaza numarul de vieti al dealerului (nerelevant pentru modul multiplayer).
     *
     * @return 0, deoarece modul multiplayer nu are un dealer separat.
     */
    public int getDealerLives() {
        return 0;
    }
}
