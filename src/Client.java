import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import io.github.cdimascio.dotenv.Dotenv;

public class Client {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    private String username;

    static final Dotenv dotenv = Dotenv.load();

    static final String DB_URL = dotenv.get("DB_URL");
    static final String DB_USER = dotenv.get("DB_USER");
    static final String DB_PASS = dotenv.get("DB_PASS");


    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public static Client login(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        try {
            Class.forName("org.mariadb.jdbc.Driver");

            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            Scanner scanner = new Scanner(System.in);
            while(!(rs.next())) {
                System.out.println("❌ Invalid username or password. \n Enter your credentials again");
                System.out.print("Enter username: ");
                username = scanner.nextLine();

                System.out.print("Enter password: ");
                password = scanner.nextLine();

                stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);

                rs = stmt.executeQuery();
            }
            System.out.println("✅ Login successful! Welcome, " + rs.getString("username") + "!");
            Socket socket = new Socket("localhost", 1234);
            Client client = new Client(socket, username);

            rs.close();
            stmt.close();
            conn.close();

            return client;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(this.username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(this.username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroupChat;

                while (socket.isConnected()) {
                    try {
                        messageFromGroupChat = bufferedReader.readLine();
                        System.out.println(messageFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedWriter, bufferedReader);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }

            if(bufferedReader != null) {
                bufferedReader.close();
            }

            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Client client = login(username, password);

        if(client != null) {
            client.listenForMessage();
            client.sendMessage();
        }

    }
}
