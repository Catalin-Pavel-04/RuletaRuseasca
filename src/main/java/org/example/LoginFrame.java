package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * Clasa LoginFrame reprezinta interfata grafica pentru autentificarea utilizatorilor.
 * Permite utilizatorilor sa introduca un nume de utilizator si o parola pentru a se autentifica.
 */
public class LoginFrame extends JFrame {

    /**
     * Constructor pentru clasa LoginFrame.
     *
     * @param userManager Instanta UserManager utilizata pentru autentificarea utilizatorilor.
     * @param onLogin Functionalitate pentru actiunea de login, transmisa ca un Consumer.
     */
    public LoginFrame(UserManager userManager, java.util.function.Consumer<String> onLogin) {
        setTitle("Log In");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JButton loginButton = new JButton("Log In");
        JButton backButton = new JButton("Back");

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(loginButton);
        panel.add(backButton);

        add(panel);

        // Actionare buton "Log In"
        loginButton.addActionListener(e -> {
            String inputUsername = userField.getText();
            String inputPassword = new String(passField.getPassword());
            if (userManager.autentificareUtilizator(inputUsername, inputPassword)) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                new MainMenuFrame(userManager, inputUsername).setVisible(true); // Transmite username
                dispose(); // Inchide fereastra LoginFrame
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Actionare buton "Back"
        backButton.addActionListener(e -> {
            new MainMenuFrame(userManager).setVisible(true);
            dispose();
        });
    }
}
