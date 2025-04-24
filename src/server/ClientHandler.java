package server;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final int userId;
    private final String username;
    private final BufferedWriter writer;
    private final BufferedReader reader;
    private final Conversation conversation;

    public ClientHandler(Socket socket, int userId, String username, Conversation conversation) throws IOException {
        this.socket = socket;
        this.userId = userId;
        this.username = username;
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.conversation = conversation;

        conversation.join(this);
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                Message msg = new Message(userId, username, line);
                conversation.broadcastUserMessage(msg);
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String text) {
        try {
            writer.write(text);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to send message to " + username + ": " + e.getMessage());
        }
    }

    public void sendHistory(List<Message> history) {
        for (Message msg : history) {
            sendMessage(String.format("%s: %s", msg.getUsername(), msg.getContent()));
        }
    }

    private void closeResources() {
        conversation.leave(this);
        try {
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing resources for " + username + ": " + e.getMessage());
        }
    }
}
