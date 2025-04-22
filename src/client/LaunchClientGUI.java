package client;

import gui.ChatForm;

import javax.swing.*;
import java.util.Scanner;

public class LaunchClientGUI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Client client = new Client(username, password);
        if (client != null) {
            SwingUtilities.invokeLater(() -> {
                ChatForm chatForm = new ChatForm(client, username);
                client.setMessageListener(chatForm);
                client.listenForMessage();
            });
        }
    }
}
