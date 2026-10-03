package itprocurementsystem;

import java.sql.Connection;
import java.sql.SQLException;

// Add missing workflow/catalogue tables and account columns without deleting records.
// The original schema must already exist. The matching SQL script is in db/.
public final class DatabaseSetup {
    public static void ensureWorkflowTables(Connection connection) throws SQLException {
        // These additions keep existing accounts and request records intact.
        addColumn(connection, "users", "gender", "VARCHAR(30) NULL");
        addColumn(connection, "users", "session_version", "INT NOT NULL DEFAULT 0");
        // This flag survives closing the program, so a reset password cannot bypass the prompt.
        addColumn(connection, "users", "must_change_password", "BOOLEAN NOT NULL DEFAULT FALSE");
        // Older recorded resets also need the new rule. A completed change prevents re-flagging.
        Database.update(connection,"UPDATE users u SET must_change_password=TRUE WHERE must_change_password=FALSE AND EXISTS (SELECT 1 FROM audit_logs r WHERE r.user_id=u.user_id AND r.action='Classroom password reset' AND NOT EXISTS (SELECT 1 FROM audit_logs done WHERE done.user_id=u.user_id AND done.action='Changed temporary password' AND done.log_id>r.log_id))");
        // Catalogue IDs link new requests to products, while snapshots preserve old prices.
        Database.update(connection, "CREATE TABLE IF NOT EXISTS catalogue (catalogue_id INT AUTO_INCREMENT PRIMARY KEY, seed_code VARCHAR(20) UNIQUE, item_name VARCHAR(100) NOT NULL, item_type VARCHAR(20) NOT NULL, category_id INT NOT NULL, unit VARCHAR(50) NOT NULL, description VARCHAR(255) NOT NULL, price DECIMAL(12,2) NOT NULL, active BOOLEAN NOT NULL DEFAULT FALSE, FOREIGN KEY(category_id) REFERENCES categories(category_id)) ENGINE=InnoDB");
        addColumn(connection, "request_items", "catalogue_id", "INT NULL");
        addColumn(connection, "request_items", "unit", "VARCHAR(50) NULL");
        // Add the named relationship explicitly so reverse engineering shows it too.
        boolean linked = false;
        try (java.sql.ResultSet keys = connection.getMetaData().getImportedKeys(connection.getCatalog(), null, "request_items")) {
            while (keys.next()) { if ("catalogue_id".equals(keys.getString("FKCOLUMN_NAME"))) { linked = true; } }
        }
        if (!linked) { Database.update(connection,"ALTER TABLE request_items ADD CONSTRAINT fk_request_catalogue FOREIGN KEY(catalogue_id) REFERENCES catalogue(catalogue_id)"); }
        Database.update(connection, "CREATE TABLE IF NOT EXISTS order_decisions (request_id INT PRIMARY KEY, customer_id INT NOT NULL, decision VARCHAR(20) NOT NULL, comments TEXT, decision_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(request_id) REFERENCES requests(request_id), FOREIGN KEY(customer_id) REFERENCES users(user_id)) ENGINE=InnoDB");
        CatalogueSeed.insertMissing(connection);
        // IF NOT EXISTS makes startup safe when the tables were already created.
        Database.update(connection, "CREATE TABLE IF NOT EXISTS customer_decisions (     quotation_id INT PRIMARY KEY,     customer_id INT NOT NULL,     decision VARCHAR(20) NOT NULL,     comments TEXT,     decision_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,     FOREIGN KEY (quotation_id) REFERENCES quotations(quotation_id),     FOREIGN KEY (customer_id) REFERENCES users(user_id) ) ENGINE=InnoDB");
        Database.update(connection, "CREATE TABLE IF NOT EXISTS request_selections (     request_id INT PRIMARY KEY,     quotation_id INT NOT NULL UNIQUE,     FOREIGN KEY (request_id) REFERENCES requests(request_id),     FOREIGN KEY (quotation_id) REFERENCES quotations(quotation_id) ) ENGINE=InnoDB");
        Database.update(connection, "CREATE TABLE IF NOT EXISTS service_progress (     request_id INT PRIMARY KEY,     work_status VARCHAR(20) NOT NULL DEFAULT 'Not Started',     work_notes TEXT,     completion_date DATE,     updated_by INT NOT NULL,     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,     FOREIGN KEY (request_id) REFERENCES requests(request_id),     FOREIGN KEY (updated_by) REFERENCES users(user_id) ) ENGINE=InnoDB");
    }
    // Table and column names below are fixed by our code, never typed by users.
    static void addColumn(Connection c, String table, String column, String definition) throws SQLException {
        try (java.sql.ResultSet columns = c.getMetaData().getColumns(c.getCatalog(), null, table, column)) {
            if (columns.next()) { return; }
        }
        Database.update(c, "ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
    }
}
