package server;

import database.DatabaseManager;
import database.DBCredentials;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private static final int PORT = 1234;

    public static void main(String[] args) {
        DBCredentials credentials = loadOrPromptCredentials();
        System.out.println(credentials.getDbUrl() + " " + credentials.getDbUser() + " " + credentials.getDbPass());
        DatabaseManager.setCredentials(credentials.getDbUrl(), credentials.getDbUser(), credentials.getDbPass());
        DatabaseManager.initializeDatabase();

        Conversation conversation = new Conversation(1);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                int userId = dis.readInt();
                String username = dis.readUTF();
                System.out.println("New connection: " + username + "@" + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket, userId, username, conversation);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static DBCredentials loadOrPromptCredentials() {
        File secretsFile = new File("secrets.ser");
        if (secretsFile.exists()) {
            System.out.print("Found secrets.ser. Use it? (Y/N): ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(secretsFile))) {
                    return (DBCredentials) ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Error reading secrets: " + e.getMessage());
                }
            }
        }

        System.out.println("Enter configuration:");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Database URL: ");
        String url = scanner.nextLine();
        System.out.print("Database Username: ");
        String user = scanner.nextLine();
        System.out.print("Database Password: ");
        String pass = scanner.nextLine();
        System.out.print("Server IP/Hostname: ");
        String serverAddress = scanner.nextLine();

        DBCredentials credentials = new DBCredentials(url, user, pass, serverAddress);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(secretsFile))) {
            oos.writeObject(credentials);
            System.out.println("Configuration saved to secrets.ser");
        } catch (IOException e) {
            System.err.println("Failed to save credentials: " + e.getMessage());
        }

        return credentials;
    }
}