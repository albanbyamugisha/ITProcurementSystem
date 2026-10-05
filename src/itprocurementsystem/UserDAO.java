// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// These JDBC classes allow the application to run a query and read its result.
// Import Connection for a JDBC connection to the database.
import java.sql.Connection;
// Import PreparedStatement for SQL statements with separate value placeholders.
import java.sql.PreparedStatement;
// Import ResultSet for the rows returned by a database query.
import java.sql.ResultSet;
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;

// DAO means Data Access Object. This class keeps user database queries out of the form.
// Define UserDAO as a class that groups its related data and methods.
public class UserDAO {

    // Return true only when an existing user's password matches.
    // SQLException lets the form show a helpful message if the database is unavailable.
    // Verify the entered password hash against the saved account and start a session only after a match.
    public boolean checkLogin(String username, String password) throws SQLException {

        // Remove any previous user's details before checking a new login attempt.
        // Clear all remembered login details so no previous account remains signed in.
        Session.clear();

        // Refuse missing input even if this method is called from somewhere else later.
        // Continue with this branch when (username equals no object (null) or username.trim().isEmpty() or
        // password equals no object (null) or password is empty).
        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()) {
            // Return false to the caller.
            return false;
        }

        // The question mark is a placeholder for the username.
        // PreparedStatement treats the entered username as data, not as SQL commands.
        // The query also reads the user's details so the application can remember them after a successful login.
        // Declare sql to hold text. Read users records; WHERE limits the rows to the stated conditions;
        // question marks receive separately bound values.
        String sql = "SELECT user_id, username, full_name, role, password_hash, session_version, must_change_password "
                + "FROM users WHERE username = ?";

        // Close the connection and statement automatically when this block finishes.
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare connection with type Connection. Open a JDBC connection; the test URL property can select an
        // isolated database instead of the normal one.
        try (Connection connection = DBConnection.getConnection();
                // Declare statement with type PreparedStatement. Prepare the SQL on this connection; supplied values
                // will be bound separately to its placeholders.
                PreparedStatement statement = connection.prepareStatement(sql)) {

            // Put the entered username into the first placeholder.
            // Bind `username.trim()` as text to SQL placeholder 1.
            statement.setString(1, username.trim());

            // Run the SELECT query and automatically close its result afterward.
            // Open the declared resources for this block; try-with-resources closes them in reverse order even if
            // an error occurs.
            // Declare result with type ResultSet. Execute the prepared SELECT and return its result rows for
            // reading.
            try (ResultSet result = statement.executeQuery()) {

                // next() is true when the query found a user with this username.
                // Check whether the result contains a row before reading its columns or generated ID.
                if (result.next()) {
                    // Declare savedHash to hold text. Read database column "password_hash" as text from the current result
                    // row.
                    String savedHash = result.getString("password_hash");
                    // Declare enteredHash to hold text. Calculate the SHA-256 password hash and return it as hexadecimal
                    // text for storage or comparison.
                    String enteredHash = PasswordUtil.hashPassword(password);

                    // Passwords are case-sensitive. Their hashes must match exactly.
                    // Continue with this branch when (enteredHash.equals(savedHash)).
                    if (enteredHash.equals(savedHash)) {

                        // Remember the verified user's details, but not their password.
                        // Start the shared session with these verified account details and any supplied version or
                        // password-change flag.
                        Session.start(result.getInt("user_id"),
                                result.getString("username"),
                                result.getString("full_name"),
                                result.getString("role"), result.getInt("session_version"), result.getBoolean("must_change_password"));
                        // Return true to the caller.
                        return true;
                    }

                    // The username exists, but the password does not match.
                    // Return false to the caller.
                    return false;
                }

                // No matching username was found.
                // Return false to the caller.
                return false;
            }
        // Handle SQLException ex from the preceding try block so the failure follows the recovery steps below.
        } catch (SQLException ex) {
            // If reading or closing the database fails, leave nobody signed in.
            // Clear all remembered login details so no previous account remains signed in.
            Session.clear();

            // Pass the error to the form, which already shows a helpful message.
            // Pass the caught exception back to the caller so the original failure is not treated as success.
            throw ex;
        }
    }
}
