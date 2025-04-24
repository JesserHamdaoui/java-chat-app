package client;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

public class Client {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String DB_URL = dotenv.get("DB_URL");
    private static final String DB_USER = dotenv.get("DB_USER");
    private static final String DB_PASS = dotenv.get("DB_PASS");
    private static final String SERVER_ADDRESS = dotenv.get("SERVER_URL");
    private static final int SERVER_PORT = 1234;

    final int userId;
    final String username;
    private final Socket socket;
    private final DataOutputStream dos;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private MessageListener messageListener;

    public interface MessageListener {
        void onMessageReceived(String message);
    }

    public Client(String username, String password) throws AuthenticationException, IOException {
        this.userId = authenticate(username, password);
        this.username = username;

        this.socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        this.dos = new DataOutputStream(socket.getOutputStream());
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        sendUserToServer();
    }

    private int authenticate(String username, String password) throws AuthenticationException {
        final String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new AuthenticationException("Invalid credentials");
                } else {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Database error: " + e.getMessage());
        }
    }

    private void sendUserToServer() throws IOException {
        dos.writeInt(this.userId);
        dos.writeUTF(this.username);
        dos.flush();
    }

    public void sendMessage(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    public void listenForMessages() {
        new Thread(() -> {
            try {
                while (socket.isConnected()) {
                    String message = reader.readLine();
                    if (message != null && messageListener != null) {
                        messageListener.onMessageReceived(message);
                    }
                }
            } catch (IOException e) {
                closeResources();
            }
        }).start();
    }

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    public void closeResources() {
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }

    public static class AuthenticationException extends Exception {
        public AuthenticationException(String message) {
            super(message);
        }
    }
}