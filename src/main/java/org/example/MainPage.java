package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Clasa MainPage reprezinta punctul de intrare in aplicatia Russian Roulette.
 * Permite utilizatorilor sa joace in modul singleplayer sau multiplayer, sa vizualizeze clasamentul,
 * sa se autentifice, sa se inregistreze sau sa iasa din aplicatie.
 */
public class MainPage {

    /**
     * URL-ul conexiunii la MongoDB.
     */
    private static final String DB_URL = "mongodb://localhost:27017";

    /**
     * Numele bazei de date MongoDB.
     */
    private static final String DB_NAME = "proiect_russian_rulet";

    /**
     * Numele colectiei MongoDB pentru stocarea utilizatorilor.
     */
    private static final String COLLECTION_NAME = "utilizatori";
    /**
     * Constructor implicit pentru clasa MainPage.
     */
    public MainPage() {
        // Constructor utilizat pentru documentare si instantiere
    }

    /**
     * Metoda principala care lanseaza aplicatia Russian Roulette.
     *
     * @param args Argumentele din linia de comanda.
     */
    public static void main(String[] args) {
        // Conexiune MongoDB
        MongoClient mongoClient = MongoClients.create(DB_URL);
        MongoDatabase database = mongoClient.getDatabase(DB_NAME);
        MongoCollection<Document> userCollection = database.getCollection(COLLECTION_NAME);
        UserManager userManager = new UserManager(userCollection);

        Scanner scanner = new Scanner(System.in);
        String username = null;

        while (true) {
            // Pagina principala
            System.out.println("\n====== Main Page ======");
            System.out.println("1. Play");
            System.out.println("2. Scoreboard");
            System.out.println("3. Log In");
            System.out.println("4. Sign Up");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // Consumam newline-ul ramas

            switch (option) {
                case 1 -> {
                    // Play
                    if (username == null) {
                        System.out.println("\n❌ You need to log in to play.");
                    } else {
                        playGame(scanner, username, userManager);
                    }
                }
                case 2 -> {
                    // Scoreboard
                    userManager.afisareScoreboard();
                }
                case 3 -> {
                    // Log In
                    System.out.print("\nEnter username: ");
                    String inputUsername = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String inputPassword = scanner.nextLine();

                    if (userManager.autentificareUtilizator(inputUsername, inputPassword)) {
                        username = inputUsername;
                        System.out.println("✅ Login successful.");
                    } else {
                        System.out.println("❌ Login failed.");
                    }
                }
                case 4 -> {
                    // Sign Up
                    System.out.print("\nEnter new username: ");
                    String newUsername = scanner.nextLine();
                    System.out.print("Enter new password: ");
                    String newPassword = scanner.nextLine();

                    userManager.inregistrareUtilizator(newUsername, newPassword);
                }
                case 5 -> {
                    // Exit
                    System.out.println("\n🏃 Exiting application. Goodbye!");
                    mongoClient.close();
                    return;
                }
                default -> System.out.println("❌ Invalid option. Please choose between 1 and 5.");
            }
        }
    }

    /**
     * Gestioneaza logica optiunii "Play", permitand utilizatorului sa joace in modul singleplayer sau multiplayer.
     *
     * @param scanner Scanner pentru citirea input-ului utilizatorului.
     * @param username Numele utilizatorului autentificat.
     * @param userManager Instanta UserManager utilizata pentru gestionarea utilizatorilor.
     */
    private static void playGame(Scanner scanner, String username, UserManager userManager) {
        System.out.println("\n===== Play Menu =====");
        System.out.println("1. Singleplayer");
        System.out.println("2. Multiplayer");
        System.out.print("Choose an option: ");
        int playOption = scanner.nextInt();
        scanner.nextLine(); // Consumam newline-ul ramas

        System.out.print("\nSet the number of lives for each player (1-5): ");
        int lives = scanner.nextInt();
        scanner.nextLine(); // Consumam newline-ul ramas

        if (lives < 1 || lives > 5) {
            System.out.println("❌ Invalid number of lives. Setting to default (1).\n");
            lives = 1;
        }

        switch (playOption) {
            case 1 -> {
                // Singleplayer
                Game game = new Game(username, userManager);
                game.setPlayerLife(lives);
                game.setDealerLife(lives);
                while (!game.isGameOver()) {
                    System.out.print("Enter 's' to shoot yourself or 'd' to shoot the dealer: ");
                    String action = scanner.nextLine().trim();
                    if (action.equals("s") || action.equals("d")) {
                        System.out.println(game.playRoundSwing(action));
                    } else {
                        System.out.println("❌ Invalid action. Use 's' or 'd'.");
                    }
                }
            }
            case 2 -> {
                // Multiplayer
                System.out.print("\nEnter the number of players (2-4): ");
                int numPlayers = scanner.nextInt();
                scanner.nextLine(); // Consumam newline-ul ramas

                if (numPlayers < 2 || numPlayers > 4) {
                    System.out.println("❌ Invalid number of players. Defaulting to 2.");
                    numPlayers = 2;
                }

                List<String> players = new ArrayList<>();
                for (int i = 1; i <= numPlayers; i++) {
                    System.out.print("Enter name for Player " + i + ": ");
                    players.add(scanner.nextLine());
                }

                MultiplayerGame multiplayerGame = new MultiplayerGame(players, userManager, lives);
                multiplayerGame.play();
            }
            default -> System.out.println("❌ Invalid option. Returning to main page.");
        }
    }
}
