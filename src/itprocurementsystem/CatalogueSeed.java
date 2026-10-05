// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Import Connection for a JDBC connection to the database.
import java.sql.Connection;
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;

// Initial catalogue entries and preset selling prices for this system.
// Stable seed codes prevent startup from duplicating entries or overwriting Admin edits.
// Define CatalogueSeed as a class that groups its related data and methods; final prevents other
// classes from extending it.
final class CatalogueSeed {
    // Insert missing preset catalogue entries and clean exact legacy descriptions without replacing edited
    // prices.
    static void insertMissing(Connection c) throws SQLException {
        // Declare entries to hold an array of rows, each containing ordered text values. The following
        // expression supplies its initial value.
        // Each row contains: stable code, item name, request type, category, pricing unit, specification, UGX price.
        // Legacy Demo wording is retained here only to recognise exact old records; the loop below cleans it before insertion.
        String[][] entries = {
            // Seed EQ-001: Laptop computer at UGX 1,800,000; pricing unit: Each.
            {"EQ-001","Laptop computer","Equipment","Computers","Each","Demo laptop: 8 GB RAM, 256 GB SSD","1800000"},
            // Seed EQ-002: Desktop computer at UGX 1,500,000; pricing unit: Each.
            {"EQ-002","Desktop computer","Equipment","Computers","Each","Demo desktop tower: 8 GB RAM, 256 GB SSD; monitor excluded","1500000"},
            // Seed EQ-003: Monitor at UGX 450,000; pricing unit: Each.
            {"EQ-003","Monitor","Equipment","Displays","Each","Demo 24-inch full-HD HDMI monitor","450000"},
            // Seed EQ-004: Printer at UGX 750,000; pricing unit: Each.
            {"EQ-004","Printer","Equipment","Printing","Each","Demo monochrome laser printer with starter toner","750000"},
            // Seed EQ-005: Wireless router at UGX 180,000; pricing unit: Each.
            {"EQ-005","Wireless router","Equipment","Networking","Each","Demo dual-band wireless router","180000"},
            // Seed EQ-006: Network switch at UGX 220,000; pricing unit: Each.
            {"EQ-006","Network switch","Equipment","Networking","Each","Demo 8-port gigabit switch","220000"},
            // Seed EQ-007: UPS at UGX 300,000; pricing unit: Each.
            {"EQ-007","UPS","Equipment","Power","Each","Demo 650 VA backup power unit","300000"},
            // Seed EQ-008: External storage drive at UGX 250,000; pricing unit: Each.
            {"EQ-008","External storage drive","Equipment","Storage","Each","Demo 1 TB USB external hard drive","250000"},
            // Seed SV-001: Computer setup at UGX 50,000; pricing unit: Per computer.
            {"SV-001","Computer setup","Service","Installation","Per computer","Demo setup: user account and basic settings; hardware excluded","50000"},
            // Seed SV-002: Software installation at UGX 30,000; pricing unit: Per package/computer.
            {"SV-002","Software installation","Service","Software","Per package/computer","Install one customer-supplied licensed package; licence excluded","30000"},
            // Seed SV-003: Computer maintenance at UGX 60,000; pricing unit: Per computer.
            {"SV-003","Computer maintenance","Service","Maintenance","Per computer","Inspection and cleaning; replacement parts excluded","60000"},
            // Seed SV-004: Fault diagnosis at UGX 25,000; pricing unit: Per device.
            {"SV-004","Fault diagnosis","Service","Support","Per device","Diagnosis and findings only; repair excluded","25000"},
            // Seed SV-005: Network device configuration at UGX 80,000; pricing unit: Per device.
            {"SV-005","Network device configuration","Service","Networking","Per device","Basic router or switch configuration; hardware excluded","80000"},
            // Seed SV-006: Backup setup at UGX 40,000; pricing unit: Per computer.
            {"SV-006","Backup setup","Service","Data","Per computer","Configure one backup destination; storage and subscriptions excluded","40000"},
            // Seed SV-007: Basic computer training at UGX 50,000; pricing unit: Per person/session.
            {"SV-007","Basic computer training","Service","Training","Per person/session","One 2-hour session: files, typing and email basics","50000"}
        };
        // Process each entry in entries in turn, referring to the current entry as e.
        for (String[] e : entries) {
            // Clean the exact earlier wording without changing prices or Admin-edited specifications.
            // Declare oldDescription to hold text. Its initial value is `e[5] + " [Class demo]"`.
            String oldDescription = e[5] + " [Class demo]";
            // Continue with this branch when (e[5].startsWith("Demo ")).
            // Remove the old Demo prefix from this seed description before storing the cleaned wording.
            if (e[5].startsWith("Demo ")) { e[5] = e[5].substring(5); }
            // Capitalise the first character of the cleaned description and retain the remaining text.
            e[5] = Character.toUpperCase(e[5].charAt(0)) + e[5].substring(1);
            // Update catalogue values only for rows matching the WHERE condition; question marks receive
            // separately bound values.
            Database.update(c,"UPDATE catalogue SET description=? WHERE seed_code=? AND description=?",e[5],e[0],oldDescription);
            // These fixed table names are the code, not user input. Only descriptive text changes.
            // Declare tables to hold an ordered array of text values. Its initial value is `{"request_items",
            // "quotation_items", "delivery_items", "inventory"}`.
            String[] tables = {"request_items","quotation_items","delivery_items","inventory"};
            // Process each entry in tables in turn, referring to the current entry as table.
            for (String table : tables) {
                // Run a prepared database change and return the number of affected rows; the caller controls the
                // transaction.
                Database.update(c,"UPDATE " + table + " SET item_description=? WHERE item_description=?",e[5],oldDescription);
            }
            // Continue with this branch when (not Database.rows(c, "SELECT catalogue_id FROM catalogue WHERE
            // seed_code=?", e[0]).isEmpty()).
            // Skip the remaining work for this loop entry and move to the next entry.
            if (!Database.rows(c, "SELECT catalogue_id FROM catalogue WHERE seed_code=?", e[0]).isEmpty()) { continue; }
            // Declare categories with type java.util.ArrayList<Object[]>. Read categories records; WHERE limits
            // the rows to the stated conditions and ORDER BY fixes their display order; question marks receive
            // separately bound values.
            java.util.ArrayList<Object[]> categories = Database.rows(c, "SELECT category_id FROM categories WHERE category_name=? ORDER BY category_id", e[3]);
            // Declare category to hold a whole-number value. Insert a record into categories; question marks
            // receive separately bound values.
            int category = categories.isEmpty() ? Database.insert(c, "INSERT INTO categories(category_name) VALUES (?)", e[3]) : Database.id(categories.get(0)[0]);
            // Insert a record into catalogue; a duplicate key is skipped instead of inserting the same seed again;
            // question marks receive separately bound values.
            Database.update(c, "INSERT IGNORE INTO catalogue(seed_code,item_name,item_type,category_id,unit,description,price,active) VALUES (?,?,?,?,?,?,?,TRUE)",
                    e[0], e[1], e[2], category, e[4], e[5], new java.math.BigDecimal(e[6]));
        }
    }
}
