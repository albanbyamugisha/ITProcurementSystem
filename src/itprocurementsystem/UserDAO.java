package itprocurementsystem;

// These JDBC classes let us run a query and read its result.
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// DAO means Data Access Object. This class keeps user database queries out of the form.
public class UserDAO {

    // Return true only when an existing user's password matches.
    // SQLException lets the form show a helpful message if the database is unavailable.
    public boolean checkLogin(String username, String password) throws SQLException {

        // Refuse missing input even if this method is called from somewhere else later.
        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()) {
            return false;
        }

        // The question mark is a placeholder for the username.
        // PreparedStatement treats the entered username as data, not as SQL commands.
        String sql = "SELECT password_hash FROM users WHERE username = ?";

        // Close the connection and statement automatically when this block finishes.
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            // Put the entered username into the first placeholder.
            statement.setString(1, username.trim());

            // Run the SELECT query and automatically close its result afterward.
            try (ResultSet result = statement.executeQuery()) {

                // next() is true when the query found a user with this username.
                if (result.next()) {
                    String savedHash = result.getString("password_hash");
                    String enteredHash = PasswordUtil.hashPassword(password);

                    // Passwords are case-sensitive. Their hashes must match exactly.
                    return enteredHash.equals(savedHash);
                }

                // No matching username was found.
                return false;
            }
        }
    }
}
