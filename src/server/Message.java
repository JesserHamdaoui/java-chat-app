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

    public Message(int messageId, int userId, String username, String content, LocalDateTime timestamp) {
        this.messageId = messageId;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
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