package itprocurementsystem;

import java.sql.Connection;
import java.sql.SQLException;

// Fictional UGX prices make the class demonstration usable immediately.
// Stable seed codes prevent startup from duplicating entries or overwriting Admin edits.
final class CatalogueSeed {
    static void insertMissing(Connection c) throws SQLException {
        String[][] entries = {
            {"EQ-001","Laptop computer","Equipment","Computers","Each","Demo laptop: 8 GB RAM, 256 GB SSD","1800000"},
            {"EQ-002","Desktop computer","Equipment","Computers","Each","Demo desktop tower: 8 GB RAM, 256 GB SSD; monitor excluded","1500000"},
            {"EQ-003","Monitor","Equipment","Displays","Each","Demo 24-inch full-HD HDMI monitor","450000"},
            {"EQ-004","Printer","Equipment","Printing","Each","Demo monochrome laser printer with starter toner","750000"},
            {"EQ-005","Wireless router","Equipment","Networking","Each","Demo dual-band wireless router","180000"},
            {"EQ-006","Network switch","Equipment","Networking","Each","Demo 8-port gigabit switch","220000"},
            {"EQ-007","UPS","Equipment","Power","Each","Demo 650 VA backup power unit","300000"},
            {"EQ-008","External storage drive","Equipment","Storage","Each","Demo 1 TB USB external hard drive","250000"},
            {"SV-001","Computer setup","Service","Installation","Per computer","Demo setup: user account and basic settings; hardware excluded","50000"},
            {"SV-002","Software installation","Service","Software","Per package/computer","Install one customer-supplied licensed package; licence excluded","30000"},
            {"SV-003","Computer maintenance","Service","Maintenance","Per computer","Inspection and cleaning; replacement parts excluded","60000"},
            {"SV-004","Fault diagnosis","Service","Support","Per device","Diagnosis and findings only; repair excluded","25000"},
            {"SV-005","Network device configuration","Service","Networking","Per device","Basic router or switch configuration; hardware excluded","80000"},
            {"SV-006","Backup setup","Service","Data","Per computer","Configure one backup destination; storage and subscriptions excluded","40000"},
            {"SV-007","Basic computer training","Service","Training","Per person/session","One 2-hour session: files, typing and email basics","50000"}
        };
        for (String[] e : entries) {
            if (!Database.rows(c, "SELECT catalogue_id FROM catalogue WHERE seed_code=?", e[0]).isEmpty()) { continue; }
            java.util.ArrayList<Object[]> categories = Database.rows(c, "SELECT category_id FROM categories WHERE category_name=? ORDER BY category_id", e[3]);
            int category = categories.isEmpty() ? Database.insert(c, "INSERT INTO categories(category_name) VALUES (?)", e[3]) : Database.id(categories.get(0)[0]);
            Database.update(c, "INSERT IGNORE INTO catalogue(seed_code,item_name,item_type,category_id,unit,description,price,active) VALUES (?,?,?,?,?,?,?,TRUE)",
                    e[0], e[1], e[2], category, e[4], e[5] + " [Class demo]", new java.math.BigDecimal(e[6]));
        }
    }
}
