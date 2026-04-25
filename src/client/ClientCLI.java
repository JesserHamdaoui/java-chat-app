package client;

import java.io.IOException;
import java.util.Scanner;

public class ClientCLI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Client client = null;

        while (client == null) {
            Credentials credentials = promptCredentials(scanner);

            try {
                client = new Client(credentials.username(), credentials.password());
                System.out.println("Logged in successfully!");
            } catch (Client.AuthenticationException | IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        client.setMessageListener(message -> System.out.println(message));
        client.listenForMessages();

        while (true) {
            String message = scanner.nextLine();
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                System.err.println("Failed to send message");
                break;
            }
        }
    }

    static Credentials promptCredentials(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        return new Credentials(username, password);
    }

    record Credentials(String username, String password) {}
}