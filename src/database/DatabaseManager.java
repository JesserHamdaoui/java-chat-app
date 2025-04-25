package database;

import io.github.cdimascio.dotenv.Dotenv;
import server.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASS;

    public static void setCredentials(String url, String user, String pass) {
        DB_URL = url;
        DB_USER = user;
        DB_PASS = pass;
        System.out.println(DB_URL + " " + DB_PASS + " " + DB_USER);
    }

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INT PRIMARY KEY, " +
                            "username VARCHAR(255) UNIQUE NOT NULL, " +
                            "password VARCHAR(255) NOT NULL" +
                            ")"
            );

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS conversations(id INT PRIMARY KEY, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)"
            );

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS messages (" +
                            "id INT PRIMARY KEY, " +
                            "conversation_id INT NOT NULL REFERENCES conversations(id), " +
                            "user_id INT NOT NULL REFERENCES users(id), " +
                            "content TEXT NOT NULL, " +
                            "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );

            stmt.executeUpdate(
                    "INSERT IGNORE INTO users (id, username, password) " +
                            "VALUES (0, '[SERVER]', '')"
            );

            stmt.executeUpdate(
                    "INSERT IGNORE INTO conversations (id) " +
                            "VALUES (1)"
            );

        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public static int authenticateUser(String username, String password) throws SQLException {
        final String query = "SELECT id FROM users WHERE username = ? AND password = ?";
        System.out.println(DB_URL + " " + DB_PASS + " " + DB_USER);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Invalid credentials");
                }
                return rs.getInt("id");
            }
        }
    }

    public static List<Message> loadMessages(int conversationId) throws SQLException {
        final String query = "SELECT m.id AS message_id, m.conversation_id, m.user_id AS sender_id, "
                + "u.username, m.content, m.created_at AS timestamp "
                + "FROM messages m JOIN users u ON m.user_id = u.id "
                + "WHERE m.conversation_id = ? ORDER BY m.created_at";

        List<Message> messages = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, conversationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(new Message(rs));
                }
            }
        }
        return messages;
    }

    public static Message saveMessage(int conversationId, int userId, String username, String content) throws SQLException {
        final String query = "INSERT INTO messages(conversation_id, user_id, content, created_at) "
                + "VALUES(?, ?, ?, ?) RETURNING id, created_at";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, conversationId);
            stmt.setInt(2, userId);
            stmt.setString(3, content);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Message(
                            rs.getInt("id"),
                            userId,
                            username,
                            content,
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }
                throw new SQLException("Failed to save message");
            }
        }
    }
}