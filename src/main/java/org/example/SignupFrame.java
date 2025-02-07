package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * Clasa SignupFrame reprezinta interfata grafica pentru inregistrarea utilizatorilor.
 * Permite utilizatorilor sa creeze un cont nou prin furnizarea unui nume de utilizator si a unei parole.
 */
public class SignupFrame extends JFrame {

    /**
     * Constructor pentru clasa SignupFrame.
     *
     * @param userManager Instanta UserManager utilizata pentru gestionarea utilizatorilor.
     */
    public SignupFrame(UserManager userManager) {
        setTitle("Sign Up");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        JLabel userLabel = new JLabel("New Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("New Password:");
        JPasswordField passField = new JPasswordField();
        JButton signupButton = new JButton("Sign Up");
        JButton backButton = new JButton("Back");

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(signupButton);
        panel.add(backButton);

        add(panel);

        // Actionare buton "Sign Up"
        signupButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (username.isBlank() || password.isBlank()) {
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            userManager.inregistrareUtilizator(username, password);
            JOptionPane.showMessageDialog(this, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            new MainMenuFrame(userManager).setVisible(true);
            dispose();
        });

        // Actionare buton "Back"
        backButton.addActionListener(e -> {
            new MainMenuFrame(userManager).setVisible(true);
            dispose();
        });
    }
}
