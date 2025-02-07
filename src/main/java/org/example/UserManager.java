package org.example;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Clasa UserManager gestioneaza operatiunile legate de utilizatori, incluzand autentificarea, inregistrarea,
 * actualizarea scorurilor si afisarea clasamentului.
 */
public class UserManager {

    /**
     * Colectia MongoDB utilizata pentru stocarea datelor utilizatorilor.
     */
    private final MongoCollection<Document> userCollection;

    /**
     * Constructor pentru clasa UserManager.
     *
     * @param userCollection Colectia MongoDB utilizata pentru stocarea utilizatorilor.
     * @throws IllegalArgumentException daca userCollection este null.
     */
    public UserManager(MongoCollection<Document> userCollection) {
        if (userCollection == null) {
            throw new IllegalArgumentException("MongoCollection cannot be null");
        }
        this.userCollection = userCollection;
    }

    /**
     * Inregistreaza un utilizator nou in baza de date.
     *
     * @param username Numele de utilizator al noului utilizator.
     * @param password Parola noului utilizator.
     */
    public void inregistrareUtilizator(String username, String password) {
        Document utilizator = userCollection.find(new Document("username", username)).first();

        if (utilizator != null) {
            System.out.println("❌ Utilizatorul \"" + username + "\" exista deja.");
        } else {
            Document nouUtilizator = new Document("username", username)
                    .append("password", password)
                    .append("scor", 0);
            userCollection.insertOne(nouUtilizator);
            System.out.println("✅ Utilizatorul \"" + username + "\" a fost inregistrat cu succes.");
        }
    }

    /**
     * Autentifica un utilizator in baza de date.
     *
     * @param username Numele de utilizator.
     * @param password Parola utilizatorului.
     * @return True daca autentificarea a avut succes, altfel False.
     */
    public boolean autentificareUtilizator(String username, String password) {
        Document utilizator = userCollection.find(new Document("username", username)).first();

        if (utilizator != null) {
            return utilizator.getString("password").equals(password);
        }
        return false;
    }

    /**
     * Afiseaza clasamentul utilizatorilor, sortat descrescator dupa scor.
     */
    public void afisareScoreboard() {
        List<Document> utilizatori = userCollection.find().into(new ArrayList<>());
        utilizatori.sort((u1, u2) -> Integer.compare(u2.getInteger("scor", 0), u1.getInteger("scor", 0)));

        if (utilizatori.isEmpty()) {
            System.out.println("⚠️ Nu exista utilizatori inregistrati.");
        } else {
            utilizatori.forEach(u -> System.out.println(u.getString("username") + ": " + u.getInteger("scor")));
        }
    }

    /**
     * Actualizeaza scorul unui utilizator in baza de date.
     *
     * @param username Numele utilizatorului.
     * @param scor Noul scor al utilizatorului.
     */
    public void actualizareScor(String username, int scor) {
        Document query = new Document("username", username);
        Document update = new Document("$set", new Document("scor", scor));
        userCollection.updateOne(query, update);
        System.out.println("✅ Scor actualizat cu succes pentru utilizatorul \"" + username + "\" la " + scor + " puncte.");
    }

    /**
     * Returneaza cel mai mare scor al unui utilizator specificat.
     *
     * @param username Numele utilizatorului.
     * @return Cel mai mare scor al utilizatorului sau 0 daca utilizatorul nu exista.
     */
    public int getHighScore(String username) {
        Document utilizator = userCollection.find(new Document("username", username)).first();
        if (utilizator != null) {
            return utilizator.getInteger("scor", 0);
        }
        return 0;
    }

    /**
     * Returneaza clasamentul utilizatorilor sub forma unei liste de documente.
     *
     * @return Lista documentelor MongoDB continand utilizatorii si scorurile acestora, sortata descrescator dupa scor.
     */
    public List<Document> getScoreboard() {
        List<Document> utilizatori = userCollection.find().into(new ArrayList<>());
        utilizatori.sort((u1, u2) -> Integer.compare(u2.getInteger("scor", 0), u1.getInteger("scor", 0)));
        return utilizatori;
    }
}
