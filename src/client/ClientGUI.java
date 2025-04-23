package client;

import gui.ChatForm;
import javax.swing.*;
import java.io.IOException;
import java.util.Scanner;

public class ClientGUI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Client client = null;

        while (client == null) {
            ClientCLI.Credentials credentials = ClientCLI.promptCredentials(scanner);

            try {
                client = new Client(credentials.username(), credentials.password());
                System.out.println("Logged in successfully! Launching GUI...");
            } catch (Client.AuthenticationException | IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        Client finalClient = client;
        Client finalClient1 = client;
        SwingUtilities.invokeLater(() -> {
            ChatForm chatForm = new ChatForm(finalClient, finalClient1.username);
            finalClient.setMessageListener(chatForm);
            finalClient.listenForMessages();
        });
    }
}