package client;

import java.io.*;
import java.net.Socket;
import java.sql.*;

import database.DBCredentials;
import database.DatabaseManager;
import io.github.cdimascio.dotenv.Dotenv;

public class Client {
    private static String SERVER_ADDRESS;
    private static final int SERVER_PORT = 1234;

    static {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("secrets.ser"))) {
            DBCredentials credentials = (DBCredentials) ois.readObject();
            // Set credentials for database access
            DatabaseManager.setCredentials(
                    credentials.getDbUrl(),
                    credentials.getDbUser(),
                    credentials.getDbPass()
            );
            SERVER_ADDRESS = credentials.getServerAddress();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading configuration: " + e.getMessage());
            SERVER_ADDRESS = "localhost"; // Fallback
        }
    }

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
        try {
            return DatabaseManager.authenticateUser(username, password);
        } catch (SQLException e) {
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
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