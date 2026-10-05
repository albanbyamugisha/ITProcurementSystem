// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Import SecureRandom for unpredictable random values for temporary passwords.
import java.security.SecureRandom;
// Import Connection for a JDBC connection to the database.
import java.sql.Connection;
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;

// This username/full-name check is only a classroom recovery demonstration.
// Anyone who knows both details could reset the account; it is not identity proof.
// The original password cannot be read from its hash, so the application creates a replacement.
// Define PasswordResetDAO as a class that groups its related data and methods.
public class PasswordResetDAO {
    // Match the registered username and full name, then save a random temporary password hash and expire
    // previous sessions.
    public String reset(String username, String fullName) throws SQLException {
        // Set username from the following operation: validate username as "Username" with a maximum of 50
        // characters; a value is required.
        username = Database.text(username, "Username", 50, true);
        // Set fullName from the following operation: validate fullName as "Full name" with a maximum of 100
        // characters; a value is required.
        fullName = Database.text(fullName, "Full name", 100, true);
        // SecureRandom chooses unpredictable characters without storing a default password.
        // Declare letters to hold text. Its initial value is
        // "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789".
        String letters = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        // Declare random with type SecureRandom. Create a generator for unpredictable random values.
        SecureRandom random = new SecureRandom();
        // Declare password with type StringBuilder. Create a mutable text buffer for building a result piece
        // by piece.
        StringBuilder password = new StringBuilder();
        // Repeat while i is less than 12; initialise the counter once and update it after each pass.
        // Append `letters.charAt(random.nextInt(letters.length()))` to password.
        for (int i = 0; i < 12; i++) { password.append(letters.charAt(random.nextInt(letters.length()))); }
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Begin a transaction so later database changes are saved together rather than one statement at a
            // time.
            c.setAutoCommit(false);
            // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
            try {
                // Lock the account until its new hash and audit record are saved together.
                // Declare matches with type ArrayList<Object[]>. Read users records; WHERE limits the rows to the
                // stated conditions; FOR UPDATE locks the selected rows until the transaction ends; question marks
                // receive separately bound values.
                ArrayList<Object[]> matches = Database.rows(c,
                        // Result columns in order: 0: user_id; 1: full_name. Object[] positions start at zero; JDBC column
                        // numbers start at one.
                        "SELECT user_id,full_name FROM users WHERE username=? FOR UPDATE", username);
                // Continue with this branch when (matches is empty or not
                // fullName.equalsIgnoreCase(matches.get(0)[1].toString().trim())).
                if (matches.isEmpty() || !fullName.equalsIgnoreCase(matches.get(0)[1].toString().trim())) {
                    // Stop this operation with an exception: The username and full name do not match an account.
                    throw new IllegalArgumentException("The username and full name do not match an account.");
                }
                // Declare user to hold a whole-number value. Convert a JDBC numeric value to an int, even when the
                // driver returned another Number type.
                int user = Database.id(matches.get(0)[0]);
                // Update users values only for rows matching the WHERE condition; question marks receive separately
                // bound values.
                Database.update(c, "UPDATE users SET password_hash=?,must_change_password=TRUE,session_version=session_version+1 WHERE user_id=?",
                        PasswordUtil.hashPassword(password.toString()), user);
                // Never put the replacement password or its hash in notifications or logs.
                // Insert a record into audit_logs; question marks receive separately bound values.
                Database.update(c, "INSERT INTO audit_logs(user_id,action,table_affected,record_id) VALUES (?,'Password reset','users',?)", user, user);
                // Save the notification message for the specified account in the current transaction.
                Database.notify(c, user, "Your password was reset. Log in with the temporary password to choose your own.");
                // Commit the transaction, making all its successful changes permanent together.
                c.commit();
                // Convert password to its text representation. Return the resulting value to the caller.
                return password.toString();
            // Handle SQLException | RuntimeException ex from the preceding try block so the failure follows the
            // recovery steps below.
            // Roll back the transaction so its unfinished database changes are not kept.
            // Pass the caught exception back to the caller so the original failure is not treated as success.
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }
}
