// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Import Connection for a JDBC connection to the database.
import java.sql.Connection;
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;

// Only a verified temporary-password session may replace that temporary password.
// Define PasswordChangeDAO as a class that groups its related data and methods.
public class PasswordChangeDAO {
    // Require a valid temporary-password session and matching new passwords before saving the replacement
    // and refreshing the session.
    public void change(String password, String confirmation) throws SQLException {
        // Continue with this branch when (password equals no object (null) or password.length() is less than 8
        // or password.length() is greater than 128).
        if (password == null || password.length() < 8 || password.length() > 128) {
            // Stop this operation with an exception: Choose a password of 8 to 128 characters.
            throw new IllegalArgumentException("Choose a password of 8 to 128 characters.");
        }
        // Continue with this branch when (not password.equals(confirmation)).
        // Stop this operation with an exception: The passwords do not match.
        if (!password.equals(confirmation)) { throw new IllegalArgumentException("The passwords do not match."); }
        // Check the session identity before continuing so a missing or different account follows this branch.
        // Stop this operation with an exception: Please log in first.
        if (Session.getUserId() <= 0) { throw new IllegalArgumentException("Please log in first."); }
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
                // Lock the account so another reset cannot race this password change.
                // Declare row to hold one row of values in SELECT or table-column order. Read users records; WHERE
                // limits the rows to the stated conditions; FOR UPDATE locks the selected rows until the transaction
                // ends; question marks receive separately bound values.
                // Result columns in order: 0: username; 1: full_name; 2: role; 3: password_hash; 4: session_version;
                // 5: IF(must_change_password,1,0). Object[] positions start at zero; JDBC column numbers start at one.
                Object[] row = Database.one(c,"SELECT username,full_name,role,password_hash,session_version,IF(must_change_password,1,0) FROM users WHERE user_id=? FOR UPDATE",Session.getUserId());
                // Continue with this branch when (Database.id(row[4]) is not equal to Session.getVersion() or
                // Database.id(row[5]) is not equal to 1).
                if (Database.id(row[4]) != Session.getVersion() || Database.id(row[5]) != 1) {
                    // Stop this operation with an exception: This password-change session has expired. Please log in
                    // again.
                    throw new IllegalArgumentException("This password-change session has expired. Please log in again.");
                }
                // Declare hash to hold text. Calculate the SHA-256 password hash and return it as hexadecimal text for
                // storage or comparison.
                String hash = PasswordUtil.hashPassword(password);
                // Continue with this branch when (hash.equals(row[3])).
                // Stop this operation with an exception: Choose a different password from the temporary one.
                if (hash.equals(row[3])) { throw new IllegalArgumentException("Choose a different password from the temporary one."); }
                // Declare nextVersion to hold a whole-number value. Its initial value is `Database.id(row[4]) + 1`.
                int nextVersion = Database.id(row[4]) + 1;
                // Update users values only for rows matching the WHERE condition; question marks receive separately
                // bound values.
                Database.update(c,"UPDATE users SET password_hash=?,must_change_password=FALSE,session_version=? WHERE user_id=?",hash,nextVersion,Session.getUserId());
                // Add an audit entry identifying the current user, action and affected record in this transaction.
                Database.audit(c,"Changed temporary password","users",Session.getUserId());
                // Commit the transaction, making all its successful changes permanent together.
                c.commit();
                // Give this login full access only after the database save succeeds.
                // Increasing the version also expires other temporary-password logins.
                // Start the shared session with these verified account details and any supplied version or
                // password-change flag.
                Session.start(Session.getUserId(),row[0].toString(),row[1].toString(),row[2].toString(),nextVersion);
            // Handle SQLException | RuntimeException ex from the preceding try block so the failure follows the
            // recovery steps below.
            // Roll back the transaction so its unfinished database changes are not kept.
            // Pass the caught exception back to the caller so the original failure is not treated as success.
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }
}
