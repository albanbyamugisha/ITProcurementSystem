// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// These classes let Java connect to the database and report connection problems.
// Import Connection for a JDBC connection to the database.
import java.sql.Connection;
// Import DriverManager to find the registered JDBC driver and open a connection using the URL and credentials.
import java.sql.DriverManager;
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;

// Other classes will use this class whenever they need a database connection.
// Define DBConnection as a class that groups its related data and methods.
public class DBConnection {

    // This URL tells Java where to find the MySQL database.
    // jdbc:mysql identifies a MySQL connection using Java's JDBC library.
    // localhost:3306 means the server is on this computer, using port 3306.
    // it_procurement_db is the name of the database.
    // SSL (Secure Sockets Layer) encrypts data sent between Java and MySQL.
    // TLS is its modern replacement, but this setting still uses the name SSL.
    // useSSL=false disables connection encryption for the local assignment setup.
    // Encryption protects data in transit; the username and password control access.
    // Only this class accesses this field directly. Declare URL to hold text. Its initial value is
    // "jdbc:mysql://localhost:3306/it_procurement_db?useSSL=false". This field is shared by all instances
    // of the class. The reference or value cannot be reassigned after initialisation.
    private static final String URL =
            "jdbc:mysql://localhost:3306/it_procurement_db?useSSL=false";

    // The application uses the root account for this local assignment project.
    // Only this class accesses this field directly. Declare USERNAME to hold text. Its initial value is
    // "root". This field is shared by all instances of the class. The reference or value cannot be
    // reassigned after initialisation.
    private static final String USERNAME = "root";

    // The local database account has no password, so the code leaves this empty.
    // Only this class accesses this field directly. Declare PASSWORD to hold text. Its initial value is
    // empty text. This field is shared by all instances of the class. The reference or value cannot be
    // reassigned after initialisation.
    private static final String PASSWORD = "";

    // static allows the application to call DBConnection.getConnection() without creating an object.
    // This method opens a new connection and returns it to the calling class.
    // If the connection fails, SQLException lets that class handle the problem.
    // Open a JDBC connection; the test URL property can select an isolated database instead of the normal
    // one.
    public static Connection getConnection() throws SQLException {
        // Tests can choose a separate database without changing the normal local setup.
        // Read the optional procurement.test.url JVM property; use URL when that property is absent.
        // Keep the chosen address in databaseUrl so the normal application and isolated tests share this method.
        String databaseUrl = System.getProperty("procurement.test.url", URL);
        // Ask the JDBC driver to connect to databaseUrl as USERNAME with PASSWORD.
        // Return the open connection; a connection failure is passed to the caller as SQLException.
        return DriverManager.getConnection(databaseUrl, USERNAME, PASSWORD);
    }

    // The calling class should close its connection when it finishes using it.
    // The application uses try-with-resources in those classes to close it automatically.
}
