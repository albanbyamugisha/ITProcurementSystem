// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Make the public types in java.sql available by short names; this does not create objects.
import java.sql.*;
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;

// These small helpers avoid repeating JDBC setup in every data access class.
// They are package-private: forms use the DAO classes, which check access first.
// Define Database as a class that groups its related data and methods; final prevents other classes
// from extending it.
final class Database {
    // Object... means callers may pass several values, such as an ID and a name.
    // Java gathers those values into an array for this method.
    // Put values into ? placeholders. Values are never joined into SQL text.
    // Fill the SQL question-mark placeholders in order; JDBC numbers them from one, while Java arrays
    // start at zero.
    private static void bind(PreparedStatement statement, Object... values) throws SQLException {
        // Repeat while i is less than values.length; initialise the counter once and update it after each
        // pass.
        // Bind `values[i]` to SQL placeholder i + 1 using its Java value type.
        for (int i = 0; i < values.length; i++) { statement.setObject(i + 1, values[i]); }
    }

    // Read rows into arrays. The SELECT column order matches the table column order.
    // Execute a prepared SELECT and return a list of rows; each Object array follows the SELECT column
    // order.
    static ArrayList<Object[]> rows(Connection c, String sql, Object... values) throws SQLException {
        // Declare rows with type ArrayList<Object[]>. Create an initially empty resizable list.
        ArrayList<Object[]> rows = new ArrayList<Object[]>();
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare statement with type PreparedStatement. Prepare the SQL on this connection; supplied values
        // will be bound separately to its placeholders.
        try (PreparedStatement statement = c.prepareStatement(sql)) {
            // Fill the SQL question-mark placeholders in order; JDBC numbers them from one, while Java arrays
            // start at zero.
            bind(statement, values);
            // Open the declared resources for this block; try-with-resources closes them in reverse order even if
            // an error occurs.
            // Declare result with type ResultSet. Execute the prepared SELECT and return its result rows for
            // reading.
            try (ResultSet result = statement.executeQuery()) {
                // Move to the next database result row and repeat while another row is available.
                while (result.next()) {
                    // Declare row to hold one row of values in SELECT or table-column order. Create an array of Object
                    // values with result.getMetaData().getColumnCount() positions, indexed from zero.
                    Object[] row = new Object[result.getMetaData().getColumnCount()];
                    // Repeat while i is less than row.length; initialise the counter once and update it after each pass.
                    // Read database column i + 1 using the driver's Java type for that value.
                    for (int i = 0; i < row.length; i++) { row[i] = result.getObject(i + 1); }
                    // Append row to rows.
                    rows.add(row);
                }
            }
        }
        // Return rows to the caller.
        return rows;
    }

    // Return exactly one row, or explain that the record has disappeared.
    // Read one matching database row and reject a missing record instead of returning an unusable null
    // value.
    static Object[] one(Connection c, String sql, Object... values) throws SQLException {
        // Declare result with type ArrayList<Object[]>. Execute a prepared SELECT and return a list of rows;
        // each Object array follows the SELECT column order.
        ArrayList<Object[]> result = rows(c, sql, values);
        // Continue with this branch when (result is empty).
        // Stop this operation with an exception: The record is no longer available. Refresh first.
        if (result.isEmpty()) { throw new IllegalArgumentException("The record is no longer available. Refresh first."); }
        // Read entry 0 from result; list positions start at zero. Return the resulting value to the caller.
        return result.get(0);
    }

    // Execute an INSERT, UPDATE or DELETE using the caller's connection and transaction.
    // Run a prepared database change and return the number of affected rows; the caller controls the
    // transaction.
    static int update(Connection c, String sql, Object... values) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare statement with type PreparedStatement. Prepare the SQL on this connection; supplied values
        // will be bound separately to its placeholders.
        try (PreparedStatement statement = c.prepareStatement(sql)) {
            // Fill the SQL question-mark placeholders in order; JDBC numbers them from one, while Java arrays
            // start at zero.
            bind(statement, values);
            // Execute the prepared database change and report how many rows were affected. Return the resulting
            // value to the caller.
            return statement.executeUpdate();
        }
    }

    // Read an AUTO_INCREMENT key immediately after inserting its row.
    // Insert a database record and return its generated AUTO_INCREMENT ID for linking related records.
    static int insert(Connection c, String sql, Object... values) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare statement with type PreparedStatement. Prepare the SQL on this connection; supplied values
        // will be bound separately to its placeholders.
        try (PreparedStatement statement = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Fill the SQL question-mark placeholders in order; JDBC numbers them from one, while Java arrays
            // start at zero.
            bind(statement, values);
            // Execute the prepared database change and report how many rows were affected.
            statement.executeUpdate();
            // Open the declared resources for this block; try-with-resources closes them in reverse order even if
            // an error occurs.
            // Declare keys with type ResultSet. Read the generated ID values from the most recent INSERT on this
            // statement.
            try (ResultSet keys = statement.getGeneratedKeys()) {
                // Check whether the result contains a row before reading its columns or generated ID.
                // Stop this operation with an exception: The database did not return an ID.
                if (!keys.next()) { throw new SQLException("The database did not return an ID."); }
                // Read database column 1 as an int; JDBC returns zero for SQL NULL, which can be checked with
                // wasNull(). Return the resulting value to the caller.
                return keys.getInt(1);
            }
        }
    }

    // IDs may be returned as different JDBC number types, so use Number.intValue().
    // Convert a JDBC numeric value to an int, even when the driver returned another Number type.
    // Convert this numeric value to a Java int for use as a record ID or count. Return the resulting value
    // to the caller.
    static int id(Object value) { return ((Number) value).intValue(); }

    // Re-read the role so a changed role takes effect even in an already open screen.
    // Check the current database role, session version and password-change flag before allowing an
    // operation.
    static void require(Connection c, String... allowedRoles) throws SQLException {
        // Check the session identity before continuing so a missing or different account follows this branch.
        // Stop this operation with an exception: Please log in first.
        if (Session.getUserId() <= 0) { throw new IllegalArgumentException("Please log in first."); }
        // Declare account to hold one row of values in SELECT or table-column order. Read users records; WHERE
        // limits the rows to the stated conditions; question marks receive separately bound values.
        Object[] account = one(c, "SELECT role,session_version,IF(must_change_password,1,0) FROM users WHERE user_id=?", Session.getUserId());
        // Continue with this branch when (id(account[1]) is not equal to Session.getVersion()).
        if (id(account[1]) != Session.getVersion()) {
            // Stop this operation with an exception: Ythe session has expired. Please log out and log in again.
            throw new IllegalArgumentException("Your session has expired. Please log out and log in again.");
        }
        // Continue with this branch when (id(account[2]) is not equal to 0).
        if (id(account[2]) != 0) {
            // Stop this operation with an exception: Choose ythe own password before using the application.
            throw new IllegalArgumentException("Choose your own password before using the application.");
        }
        // Declare role with type Object. Its initial value is `account[0]`.
        Object role = account[0];
        // Process each entry in allowedRoles in turn, referring to the current entry as allowed.
        // Continue with this branch when (allowed.equals(role)).
        // Stop this method here; no further statements in this call are executed.
        for (String allowed : allowedRoles) { if (allowed.equals(role)) { return; } }
        // Stop this operation with an exception: Ythe account cannot perform this action.
        throw new IllegalArgumentException("Your account cannot perform this action.");
    }

    // Validate length before MySQL has to reject an oversized field.
    // Trim text and check its required status and maximum length before it is sent to MySQL.
    static String text(String value, String label, int max, boolean required) {
        // Declare cleaned to hold text. Remove spaces and other trim characters from both ends of value ==
        // null ? "" : value without changing text in the middle.
        String cleaned = value == null ? "" : value.trim();
        // Continue with this branch when ((required and cleaned is empty) or cleaned.length() is greater than
        // max).
        if ((required && cleaned.isEmpty()) || cleaned.length() > max) {
            // Reject the operation with an exception whose message identifies the invalid value or failed step.
            throw new IllegalArgumentException(label + " " + (required ? "is required and " : "") + "must be at most " + max + " characters.");
        }
        // Return cleaned to the caller.
        return cleaned;
    }

    // Audit and notification rows use the same transaction as the action they describe.
    // Record the signed-in user, action and affected record using the same transaction as the original
    // change.
    static void audit(Connection c, String action, String table, int id) throws SQLException {
        // Insert a record into audit_logs; question marks receive separately bound values.
        update(c, "INSERT INTO audit_logs(user_id,action,table_affected,record_id) VALUES (?,?,?,?)", Session.getUserId(), action, table, id);
    }
    // Create a notification for the specified account using the caller's transaction.
    static void notify(Connection c, int user, String message) throws SQLException {
        // Insert a record into notifications; question marks receive separately bound values.
        update(c, "INSERT INTO notifications(user_id,message) VALUES (?,?)", user, message);
    }
}
