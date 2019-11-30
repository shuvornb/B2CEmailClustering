import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private final String url = "jdbc:postgresql://localhost:5432/crusher_server";
    private final String user = "postgres";
    private final String password = "1234567t";

    public Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
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