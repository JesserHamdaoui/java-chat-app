import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/chat";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "hamdi";

    public List<Message> getMessagesByConversationId(int conversationId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.id as message_id, m.conversation_id, m.sender_id, " +
                "m.content, m.created_at as timestamp " +
                "FROM message m " +
                "WHERE m.conversation_id = ? " +
                "ORDER BY m.created_at";


        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, conversationId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                messages.add(new Message(rs));
            }
        }
        return messages;
    }
}
