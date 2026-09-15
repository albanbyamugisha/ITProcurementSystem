package itprocurementsystem;

// These classes let Java connect to the database and report connection problems.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Other classes will use this class whenever they need a database connection.
public class DBConnection {

    // This URL tells Java where to find our MySQL database.
    // jdbc:mysql identifies a MySQL connection using Java's JDBC library.
    // localhost:3306 means the server is on this computer, using port 3306.
    // it_procurement_db is the name of our database.
    // SSL (Secure Sockets Layer) encrypts data sent between Java and MySQL.
    // TLS is its modern replacement, but this setting still uses the name SSL.
    // useSSL=false disables connection encryption for our local assignment setup.
    // Encryption protects data in transit; the username and password control access.
    private static final String URL =
            "jdbc:mysql://localhost:3306/it_procurement_db?useSSL=false";

    // We are using the root account for this local assignment project.
    private static final String USERNAME = "root";

    // Our local database account has no password, so we leave this empty.
    private static final String PASSWORD = "";

    // static lets us call DBConnection.getConnection() without creating an object.
    // This method opens a new connection and returns it to the calling class.
    // If the connection fails, SQLException lets that class handle the problem.
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    // The calling class should close its connection when it finishes using it.
    // We will use try-with-resources in those classes to close it automatically.
}
