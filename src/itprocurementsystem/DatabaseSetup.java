// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Import Connection for a JDBC connection to the database.
import java.sql.Connection;
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;

// Add missing workflow/catalogue tables and account columns without deleting records.
// The original schema must already exist. The matching SQL script is in db/.
// Define DatabaseSetup as a class that groups its related data and methods; final prevents other
// classes from extending it.
public final class DatabaseSetup {
    // Add missing columns and supporting tables, then seed missing catalogue entries while retaining
    // existing records.
    public static void ensureWorkflowTables(Connection connection) throws SQLException {
        // These additions keep existing accounts and request records intact.
        // Check database metadata first and add the fixed column only when it does not already exist.
        addColumn(connection, "users", "gender", "VARCHAR(30) NULL");
        // Check database metadata first and add the fixed column only when it does not already exist.
        addColumn(connection, "users", "session_version", "INT NOT NULL DEFAULT 0");
        // This flag survives closing the program, so a reset password cannot bypass the prompt.
        // Check database metadata first and add the fixed column only when it does not already exist.
        addColumn(connection, "users", "must_change_password", "BOOLEAN NOT NULL DEFAULT FALSE");
        // Older recorded resets also need the new rule. A completed change prevents re-flagging.
        // Read audit_logs records; WHERE limits the rows to the stated conditions.
        Database.update(connection,"UPDATE users u SET must_change_password=TRUE WHERE must_change_password=FALSE AND EXISTS (SELECT 1 FROM audit_logs r WHERE r.user_id=u.user_id AND r.action IN ('Classroom password reset','Password reset') AND NOT EXISTS (SELECT 1 FROM audit_logs done WHERE done.user_id=u.user_id AND done.action='Changed temporary password' AND done.log_id>r.log_id))");
        // Catalogue IDs link new requests to products, while snapshots preserve old prices.
        // Create the catalogue table only if it is missing; existing records remain intact.
        Database.update(connection, "CREATE TABLE IF NOT EXISTS catalogue (catalogue_id INT AUTO_INCREMENT PRIMARY KEY, seed_code VARCHAR(20) UNIQUE, item_name VARCHAR(100) NOT NULL, item_type VARCHAR(20) NOT NULL, category_id INT NOT NULL, unit VARCHAR(50) NOT NULL, description VARCHAR(255) NOT NULL, price DECIMAL(12,2) NOT NULL, active BOOLEAN NOT NULL DEFAULT FALSE, FOREIGN KEY(category_id) REFERENCES categories(category_id)) ENGINE=InnoDB");
        // Check database metadata first and add the fixed column only when it does not already exist.
        addColumn(connection, "request_items", "catalogue_id", "INT NULL");
        // Check database metadata first and add the fixed column only when it does not already exist.
        addColumn(connection, "request_items", "unit", "VARCHAR(50) NULL");
        // Add the named relationship explicitly so reverse engineering shows it too.
        // Declare linked to hold a true-or-false flag. Its initial value is false.
        boolean linked = false;
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare keys with type java.sql.ResultSet. Its initial value is
        // `connection.getMetaData().getImportedKeys(connection.getCatalog(), null, "request_items")`.
        try (java.sql.ResultSet keys = connection.getMetaData().getImportedKeys(connection.getCatalog(), null, "request_items")) {
            // Move to the next database result row and repeat while another row is available.
            // Continue with this branch when ("catalogue_id".equals(keys.getString("FKCOLUMN_NAME"))).
            // Store true in linked for the remaining steps.
            while (keys.next()) { if ("catalogue_id".equals(keys.getString("FKCOLUMN_NAME"))) { linked = true; } }
        }
        // Continue with this branch when (!linked).
        // Extend the existing table definition without replacing its records.
        if (!linked) { Database.update(connection,"ALTER TABLE request_items ADD CONSTRAINT fk_request_catalogue FOREIGN KEY(catalogue_id) REFERENCES catalogue(catalogue_id)"); }
        // Create the order_decisions table only if it is missing; existing records remain intact.
        Database.update(connection, "CREATE TABLE IF NOT EXISTS order_decisions (request_id INT PRIMARY KEY, customer_id INT NOT NULL, decision VARCHAR(20) NOT NULL, comments TEXT, decision_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(request_id) REFERENCES requests(request_id), FOREIGN KEY(customer_id) REFERENCES users(user_id)) ENGINE=InnoDB");
        // Insert missing preset catalogue entries and clean exact legacy descriptions without replacing edited
        // prices.
        CatalogueSeed.insertMissing(connection);
        // Replace one old notification template, keeping all other user messages intact.
        // Update notifications values only for rows matching the WHERE condition; question marks receive
        // separately bound values.
        Database.update(connection,"UPDATE notifications SET message=? WHERE message=?",
                "Your password was reset. Log in with the temporary password to choose your own.",
                "Your password was reset using the classroom recovery form.");
        // IF NOT EXISTS makes startup safe when the tables were already created.
        // Create the customer_decisions table only if it is missing; existing records remain intact.
        Database.update(connection, "CREATE TABLE IF NOT EXISTS customer_decisions (     quotation_id INT PRIMARY KEY,     customer_id INT NOT NULL,     decision VARCHAR(20) NOT NULL,     comments TEXT,     decision_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,     FOREIGN KEY (quotation_id) REFERENCES quotations(quotation_id),     FOREIGN KEY (customer_id) REFERENCES users(user_id) ) ENGINE=InnoDB");
        // Create the request_selections table only if it is missing; existing records remain intact.
        Database.update(connection, "CREATE TABLE IF NOT EXISTS request_selections (     request_id INT PRIMARY KEY,     quotation_id INT NOT NULL UNIQUE,     FOREIGN KEY (request_id) REFERENCES requests(request_id),     FOREIGN KEY (quotation_id) REFERENCES quotations(quotation_id) ) ENGINE=InnoDB");
        // Create the service_progress table only if it is missing; existing records remain intact.
        Database.update(connection, "CREATE TABLE IF NOT EXISTS service_progress (     request_id INT PRIMARY KEY,     work_status VARCHAR(20) NOT NULL DEFAULT 'Not Started',     work_notes TEXT,     completion_date DATE,     updated_by INT NOT NULL,     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,     FOREIGN KEY (request_id) REFERENCES requests(request_id),     FOREIGN KEY (updated_by) REFERENCES users(user_id) ) ENGINE=InnoDB");
    }
    // Table and column names below are fixed by the code, never typed by users.
    // Check database metadata first and add the fixed column only when it does not already exist.
    static void addColumn(Connection c, String table, String column, String definition) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare columns with type java.sql.ResultSet. Its initial value is
        // `c.getMetaData().getColumns(c.getCatalog(), null, table, column)`.
        try (java.sql.ResultSet columns = c.getMetaData().getColumns(c.getCatalog(), null, table, column)) {
            // Check whether the result contains a row before reading its columns or generated ID.
            // Stop this method here; no further statements in this call are executed.
            if (columns.next()) { return; }
        }
        // Extend the existing table definition without replacing its records.
        Database.update(c, "ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
    }
}
