package test.java.com.yourcompany.hotelbooking;

import main.java.com.yourcompany.hotelbooking.util.GetConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {

    public static void main(String[] args) {
        // Get a connection to the database
        Connection connection = GetConnection.getConnection();

        // Check if the connection is successful
        if (connection != null) {
            System.out.println("Successfully connected to the database!");
        } else {
            System.out.println("Failed to connect to the database.");
        }

        // Close the connection
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing the connection.");
            e.printStackTrace();
        }
    }
}
