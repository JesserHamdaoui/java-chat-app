import java.sql.*;
import java.util.Scanner;

public class LoginSystem {

    static final String DB_URL = "jdbc:mariadb://localhost:3306/chat_project";
    static final String DB_USER = "root";
    static final String DB_PASS = "james_459375";


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        login(username, password);
        scanner.close();
    }

    public static void login(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        try {
            // Load the MariaDB JDBC driver (optional if it's auto-loaded)
            Class.forName("org.mariadb.jdbc.Driver");

            // Connect to the database
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            // Prepare and execute query
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


            // Cleanup
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}