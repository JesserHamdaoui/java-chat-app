package server;

import java.sql.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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
        String query = "SELECT m.id AS message_id, m.conversation_id, m.user_id AS sender_id, u.username, m.content, m.created_at AS timestamp FROM messages m JOIN users u ON m.user_id = u.id WHERE m.conversation_id = ? ORDER BY m.created_at";

        Dotenv dotenv = Dotenv.load();

        try (Connection conn = DriverManager.getConnection(dotenv.get("DB_URL"), dotenv.get("DB_USER"), dotenv.get("DB_PASS"));
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(new Message(rs)); // assumes Message has a ResultSet constructor
            }
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
