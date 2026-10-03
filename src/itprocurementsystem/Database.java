package itprocurementsystem;

import java.sql.*;
import java.util.ArrayList;

// These small helpers avoid repeating JDBC setup in every data access class.
// They are package-private: forms use the DAO classes, which check access first.
final class Database {
    // Object... means callers may pass several values, such as an ID and a name.
    // Java gathers those values into an array for this method.
    // Put values into ? placeholders. Values are never joined into SQL text.
    private static void bind(PreparedStatement statement, Object... values) throws SQLException {
        for (int i = 0; i < values.length; i++) { statement.setObject(i + 1, values[i]); }
    }

    // Read rows into arrays. The SELECT column order matches the table column order.
    static ArrayList<Object[]> rows(Connection c, String sql, Object... values) throws SQLException {
        ArrayList<Object[]> rows = new ArrayList<Object[]>();
        try (PreparedStatement statement = c.prepareStatement(sql)) {
            bind(statement, values);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Object[] row = new Object[result.getMetaData().getColumnCount()];
                    for (int i = 0; i < row.length; i++) { row[i] = result.getObject(i + 1); }
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    // Return exactly one row, or explain that the record has disappeared.
    static Object[] one(Connection c, String sql, Object... values) throws SQLException {
        ArrayList<Object[]> result = rows(c, sql, values);
        if (result.isEmpty()) { throw new IllegalArgumentException("The record is no longer available. Refresh first."); }
        return result.get(0);
    }

    // Execute an INSERT, UPDATE or DELETE using the caller's connection and transaction.
    static int update(Connection c, String sql, Object... values) throws SQLException {
        try (PreparedStatement statement = c.prepareStatement(sql)) {
            bind(statement, values);
            return statement.executeUpdate();
        }
    }

    // Read an AUTO_INCREMENT key immediately after inserting its row.
    static int insert(Connection c, String sql, Object... values) throws SQLException {
        try (PreparedStatement statement = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(statement, values);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) { throw new SQLException("The database did not return an ID."); }
                return keys.getInt(1);
            }
        }
    }

    // IDs may be returned as different JDBC number types, so use Number.intValue().
    static int id(Object value) { return ((Number) value).intValue(); }

    // Re-read the role so a changed role takes effect even in an already open screen.
    static void require(Connection c, String... allowedRoles) throws SQLException {
        if (Session.getUserId() <= 0) { throw new IllegalArgumentException("Please log in first."); }
        Object[] account = one(c, "SELECT role,session_version FROM users WHERE user_id=?", Session.getUserId());
        if (id(account[1]) != Session.getVersion()) {
            throw new IllegalArgumentException("Your session has expired. Please log out and log in again.");
        }
        Object role = account[0];
        for (String allowed : allowedRoles) { if (allowed.equals(role)) { return; } }
        throw new IllegalArgumentException("Your account cannot perform this action.");
    }

    // Validate length before MySQL has to reject an oversized field.
    static String text(String value, String label, int max, boolean required) {
        String cleaned = value == null ? "" : value.trim();
        if ((required && cleaned.isEmpty()) || cleaned.length() > max) {
            throw new IllegalArgumentException(label + " " + (required ? "is required and " : "") + "must be at most " + max + " characters.");
        }
        return cleaned;
    }

    // Audit and notification rows use the same transaction as the action they describe.
    static void audit(Connection c, String action, String table, int id) throws SQLException {
        update(c, "INSERT INTO audit_logs(user_id,action,table_affected,record_id) VALUES (?,?,?,?)", Session.getUserId(), action, table, id);
    }
    static void notify(Connection c, int user, String message) throws SQLException {
        update(c, "INSERT INTO notifications(user_id,message) VALUES (?,?)", user, message);
    }
}
