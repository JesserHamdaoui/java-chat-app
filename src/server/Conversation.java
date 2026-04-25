package server;

import java.sql.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import database.DatabaseManager;
import io.github.cdimascio.dotenv.Dotenv;

public class Conversation {

    private final int id;
    private final List<Message> messages = new CopyOnWriteArrayList<>();
    private final List<ClientHandler> participants = new CopyOnWriteArrayList<>();

    public Conversation(int id) {
        this.id = id;
        loadMessagesFromDb();
    }

    private void loadMessagesFromDb() {
        try {
            messages.addAll(DatabaseManager.loadMessages(this.id));
        } catch (SQLException e) {
            System.err.println("Failed to load messages from DB: " + e.getMessage());
        }
    }

    public void join(ClientHandler handler) {
        participants.add(handler);
        broadcastSystemMessage(handler.getUsername() + " has joined");
        handler.sendHistory(messages);
    }

    public void leave(ClientHandler handler) {
        participants.remove(handler);
        broadcastSystemMessage(handler.getUsername() + " has left");
    }

    public void broadcastUserMessage(Message msg) {
        // Optionally persist msg to DB here
        messages.add(msg);
        String formatted = String.format("%s: %s", msg.getUsername(), msg.getContent());
        broadcastMessage(formatted);
    }

    public void broadcastSystemMessage(String text) {
        broadcastMessage("[SERVER]: " + text);
    }

    private void broadcastMessage(String text) {
        for (ClientHandler ch : participants) {
            ch.sendMessage(text);
        }
    }
}
