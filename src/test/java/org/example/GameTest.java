package org.example;

import com.mongodb.client.FindIterable;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.lang.reflect.Method;
import java.util.*;

class GameTest {
    private Game game;
    private UserManager mockUserManager;

    @BeforeEach
    void setUp() {
        mockUserManager = mock(UserManager.class);
        game = new Game("testUser", mockUserManager);
        game.setPlayerLife(3);
        game.setDealerLife(3);
    }

    @Test
    void testPlayerShootSelfWithLiveShell() {
        game.resetGameState();
        game.setPlayerLife(1);
        assertTrue(game.playRoundSwing("s").contains("You lose one health"));
        assertTrue(game.isGameOver());
    }

    @Test
    void testPlayerShootDealerWithLiveShell() {
        game.resetGameState();
        assertFalse(game.isGameOver());
        assertTrue(game.playRoundSwing("d").contains("Dealer loses one health"));
    }

    @Test
    void testGameOverWhenDealerLosesAllLives() {
        game.resetGameState();
        game.setDealerLife(1);
        assertTrue(game.playRoundSwing("d").contains("You win"));
        assertTrue(game.isGameOver());
    }

    @Test
    void testDealerTurn() {
        game.resetGameState();
        assertFalse(game.isGameOver());
        String dealerResult = game.dealerTurn();
        assertNotNull(dealerResult);
    }
}

class UserManagerTest {
    private UserManager userManager;
    private MongoCollection<Document> mockCollection;

    @BeforeEach
    void setUp() {
        mockCollection = mock(MongoCollection.class);
        userManager = new UserManager(mockCollection);
    }

    @Test
    void testRegisterUser() {
        FindIterable<Document> mockFindIterable = mock(FindIterable.class);
        when(mockFindIterable.first()).thenReturn(null); // No user found
        when(mockCollection.find((Bson) any())).thenReturn(mockFindIterable);

        userManager.inregistrareUtilizator("newUser", "password");

        verify(mockCollection).insertOne(any(Document.class));
    }

    @Test
    void testAuthenticateUserSuccess() {
        Document mockUser = new Document("username", "user1").append("password", "pass1");

        FindIterable<Document> mockFindIterable = mock(FindIterable.class);
        when(mockFindIterable.first()).thenReturn(mockUser); // User found
        when(mockCollection.find((Bson) any())).thenReturn(mockFindIterable);

        assertTrue(userManager.autentificareUtilizator("user1", "pass1"));
    }

    @Test
    void testGetHighScore() {
        Document mockUser = new Document("username", "user1").append("scor", 100);

        FindIterable<Document> mockFindIterable = mock(FindIterable.class);
        when(mockFindIterable.first()).thenReturn(mockUser); // User found
        when(mockCollection.find((Bson) any())).thenReturn(mockFindIterable);

        assertEquals(100, userManager.getHighScore("user1"));
    }
}

class MainPageTest {
    @Test
    void testMainPageSetup() {
        assertDoesNotThrow(() -> {
            MainPage.main(new String[]{});
        });
    }
}

class SingleplayerFrameTest {
    private SingleplayerFrame frame;
    private UserManager mockUserManager;

    @BeforeEach
    void setUp() {
        mockUserManager = mock(UserManager.class);
        frame = new SingleplayerFrame("testUser", mockUserManager, 3);
    }

    @Test
    void testSingleplayerFrameInitialization() {
        assertNotNull(frame);
    }

    @Test
    void testPlayRound() {
        assertDoesNotThrow(() -> {
            Method playRoundMethod = SingleplayerFrame.class.getDeclaredMethod("playRound", String.class);
            playRoundMethod.setAccessible(true);
            playRoundMethod.invoke(frame, "s");
        });
    }
}

class MultiplayerFrameTest {
    private MultiplayerFrame frame;
    private UserManager mockUserManager;

    @BeforeEach
    void setUp() {
        mockUserManager = mock(UserManager.class);
        List<String> players = Arrays.asList("Player1", "Player2");
        frame = new MultiplayerFrame(players, mockUserManager, 3);
    }

    @Test
    void testMultiplayerFrameInitialization() {
        assertNotNull(frame);
    }

    @Test
    void testPreloadShells() {
        assertDoesNotThrow(() -> {
            Method preloadShellsMethod = MultiplayerFrame.class.getDeclaredMethod("preloadShells");
            preloadShellsMethod.setAccessible(true);
            preloadShellsMethod.invoke(frame);
        });
    }
}
