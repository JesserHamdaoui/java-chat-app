package server;

import java.sql.*;
import java.time.LocalDateTime;
import io.github.cdimascio.dotenv.Dotenv;

public class Message {
    private int messageId;
    private int userId;
    private String username;
    private String content;
    private LocalDateTime timestamp;

    public Message(ResultSet resultSet) throws SQLException {
        this.messageId = resultSet.getInt("message_id");
        this.userId = resultSet.getInt("sender_id");
        this.username = resultSet.getString("username");
        this.content = resultSet.getString("content");
        this.timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();
    }

    public Message(int userId, String username, String content) {
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.timestamp = LocalDateTime.now();

        Dotenv dotenv = Dotenv.load();

        String query = "INSERT INTO messages(conversation_id, user_id, content, created_at) VALUES(?, ?, ?, ?);";
        try (Connection conn = DriverManager.getConnection(dotenv.get("DB_URL"), dotenv.get("DB_USER"), dotenv.get("DB_PASS"));
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, 1);
            stmt.setInt(2, userId);
            stmt.setString(3, content);
            stmt.setTimestamp(4, Timestamp.valueOf(timestamp));
            ResultSet rs = stmt.executeQuery();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.messageId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating message failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: id: " + userId + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: id: " + userId + e.getMessage());
        }
    }

    public int getMessageId() {
        return messageId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return username + "(" + timestamp + ")" + ": " + content;
    }
}