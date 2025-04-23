package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private static final List<ClientHandler> clients = new ArrayList<>();

    private final Socket socket;
    private final String username;
    private final BufferedWriter writer;
    private final BufferedReader reader;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.username = reader.readLine();

        synchronized (clients) {
            clients.add(this);
        }
        broadcastSystemMessage(username + " has joined");
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                String message = reader.readLine();
                if (message == null) break;
                broadcastMessage(username + ": " + message);
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private void broadcastMessage(String message) {
        synchronized (clients) {
            clients.forEach(client -> {
                try {
                    client.writer.write(message);
                    client.writer.newLine();
                    client.writer.flush();
                } catch (IOException e) {
                    System.err.println("Broadcast failed for " + client.username);
                }
            });
        }
    }

    private void broadcastSystemMessage(String message) {
        broadcastMessage("[SERVER]: " + message);
    }

    private void closeResources() {
        synchronized (clients) {
            clients.remove(this);
        }
        broadcastSystemMessage(username + " has left");

        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing client resources: " + e.getMessage());
        }
    }
}