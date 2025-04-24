package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 1234;

    public static void main(String[] args) {
        Conversation conversation = new Conversation(1); // hardcoded conversation id
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
}
