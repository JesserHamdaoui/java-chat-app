package client;

import gui.ChatForm;

import javax.swing.*;
import java.util.Scanner;

public class LaunchClientCLI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Client client = new Client(username, password);
        if (client != null) {
            client.setMessageListener(message -> System.out.println(message));
            client.listenForMessage();

            // Handle message input
            while (true) {
                String message = scanner.nextLine();
                client.sendMessage(message);
            }
        }

    }
}