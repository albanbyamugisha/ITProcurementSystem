package itprocurementsystem;

import java.sql.Connection;
import java.sql.SQLException;

// Add the three new workflow tables without changing or deleting existing records.
// The original schema must already exist. The matching SQL script is in db/.
public final class DatabaseSetup {
    public static void ensureWorkflowTables(Connection connection) throws SQLException {
        // These additions keep existing accounts and request records intact.
        addColumn(connection, "users", "gender", "VARCHAR(30) NULL");
        addColumn(connection, "users", "session_version", "INT NOT NULL DEFAULT 0");
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
