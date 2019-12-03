import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    public Connection connect() {
        String URL = "jdbc:postgresql://localhost:5432/" + Constants.DATABASE_NAME;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return connection;
    }

    public void close(Connection connection) {
        try {
            connection.close();
            System.out.println("Connection closed successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}