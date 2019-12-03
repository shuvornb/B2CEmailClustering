import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private final String DATABASE_NAME = "crusher_server";
    private final String URL = "jdbc:postgresql://localhost:5432/" + DATABASE_NAME;
    private final String USER = "postgres";
    private final String PASSWORD = "1234567t";

    public Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
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