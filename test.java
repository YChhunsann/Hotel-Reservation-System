import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class test {

    public static void main(String[] args) {
        // Database connection details
        String url = "jdbc:mysql://localhost:3306/idrive_data";
        String user = "root";
        String password = ""; // Update with your actual password

        // Establishing the database connection
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create a connection to the database
            Connection connection = DriverManager.getConnection(url, user, password);

            // Check if the connection is successful
            if (connection != null) {
                System.out.println("Successfully connected to the database!");

                // Query the users table
                queryUsersTable(connection);

            } else {
                System.out.println("Failed to connect to the database.");
            }

            // Close the connection
            connection.close();
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("SQL exception occurred.");
            e.printStackTrace();
        }
    }

    private static void queryUsersTable(Connection connection) {
        // SQL query to retrieve data from the users table
        String query = "SELECT * FROM users";

        // Create a statement object to execute the query
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            // Process the result set
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                java.sql.Timestamp createdAt = resultSet.getTimestamp("created_at");

                // Print the retrieved data
                System.out.println("ID: " + id);
                System.out.println("Username: " + username);
                System.out.println("Email: " + email);
                System.out.println("Password: " + password);
                System.out.println("Created At: " + createdAt);
                System.out.println();
            }

        } catch (SQLException e) {
            System.out.println("Error executing query.");
            e.printStackTrace();
        }
    }
}
