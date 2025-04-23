import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Message {
    private int messageId;
    private int senderId;
    private String content;
    private LocalDateTime timestamp;

    // Constructor that takes a ResultSet (SQL row)
    public Message(ResultSet resultSet) throws SQLException {
        // Assuming the ResultSet is already positioned at the correct row
        this.messageId = resultSet.getInt("message_id");
        this.senderId = resultSet.getInt("sender_id");
        this.content = resultSet.getString("content");
        this.timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();

    }

    // Getters
    public int getMessageId() {
        return messageId;
    }

    public int getSenderId() {
        return senderId;
    }


    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }



    @Override
    public String toString() {
        return senderId + ":" + content + " at " + timestamp ;
    }
}