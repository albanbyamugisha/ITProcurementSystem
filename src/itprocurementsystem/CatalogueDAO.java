// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Import Connection for a JDBC connection to the database.
import java.sql.Connection;
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;
// Import BigDecimal for exact decimal values for prices and totals without floating-point rounding.
import java.math.BigDecimal;
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;

// The database, not the customer's text fields, supplies the selling price.
// Define CatalogueDAO as a class that groups its related data and methods.
public class CatalogueDAO {
    // Search catalogue names or descriptions, showing inactive entries only to an Admin.
    public ArrayList<Object[]> list(String search, String type) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed roles to continue.
            Database.require(c, "Requester", "Admin", "Manager", "Purchaser");
            // Declare admin to hold a true-or-false flag. Read users records; WHERE limits the rows to the stated
            // conditions; question marks receive separately bound values.
            // Result columns in order: 0: role. Object[] positions start at zero; JDBC column numbers start at
            // one.
            boolean admin = "Admin".equals(Database.one(c,"SELECT role FROM users WHERE user_id=?",Session.getUserId())[0]);
            // Read catalogue records with matching information from related tables; WHERE limits the rows to the
            // stated conditions and ORDER BY fixes their display order; question marks receive separately bound
            // values. Return the resulting value to the caller.
            // Result columns in order: 0: k.catalogue_id; 1: k.item_name; 2: k.item_type; 3: g.category_name; 4:
            // k.unit; 5: k.price; 6: IF(k.active,'Active','Inactive'); 7: k.description; 8: k.category_id.
            // Object[] positions start at zero; JDBC column numbers start at one.
            return Database.rows(c, "SELECT k.catalogue_id,k.item_name,k.item_type,g.category_name,k.unit,k.price,IF(k.active,'Active','Inactive'),k.description,k.category_id FROM catalogue k JOIN categories g ON g.category_id=k.category_id WHERE (k.active=TRUE OR ?) AND (k.item_name LIKE ? OR k.description LIKE ?) AND (?='' OR k.item_type=?) ORDER BY k.item_name",
                    admin, "%" + search.trim() + "%", "%" + search.trim() + "%", type, type);
        }
    }
    // Validate and save an Admin's catalogue changes, including the exact positive UGX price.
    public void save(int id, String name, String type, int category, String unit, String description,
            String amount, boolean active) throws SQLException {
        // Set name from the following operation: validate name as "Item name" with a maximum of 100
        // characters; a value is required.
        name = Database.text(name,"Item name",100,true);
        // Set unit from the following operation: validate unit as "Unit" with a maximum of 50 characters; a
        // value is required.
        unit = Database.text(unit,"Unit",50,true);
        // Set description from the following operation: validate description as "Description" with a maximum
        // of 255 characters; a value is required.
        description = Database.text(description,"Description",255,true);
        // Continue with this branch when (!"Equipment".equals(type) and !"Service".equals(type)).
        // Stop this operation with an exception: Select Equipment or Service.
        if (!"Equipment".equals(type) && !"Service".equals(type)) { throw new IllegalArgumentException("Select Equipment or Service."); }
        // Declare price to hold an exact decimal amount.
        BigDecimal price;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Set price from the following operation: create an exact decimal value from the supplied number or
        // text.
        try { price = new BigDecimal(amount.trim()); }
        // Handle NumberFormatException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Stop this operation with an exception: Enter a valid UGX price.
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Enter a valid UGX price."); }
        // Continue with this branch when (price.signum() is at most 0 or price.compareTo(new
        // BigDecimal("9999999999.99")) is greater than 0 or price.stripTrailingZeros().scale() is greater than
        // 2).
        if (price.signum() <= 0 || price.compareTo(new BigDecimal("9999999999.99")) > 0 || price.stripTrailingZeros().scale() > 2) {
            // Stop this operation with an exception: Enter a positive price with at most two decimal places.
            throw new IllegalArgumentException("Enter a positive price with at most two decimal places.");
        }
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
                // Recheck the database session and allow only the listed role to continue.
                Database.require(c,"Admin");
                // Continue with this branch when (id equals 0).
                if (id == 0) {
                    // Insert a record into catalogue; question marks receive separately bound values.
                    id = Database.insert(c,"INSERT INTO catalogue(item_name,item_type,category_id,unit,description,price,active) VALUES (?,?,?,?,?,?,?)", name,type,category,unit,description,price,active);
                // Continue with this branch when (Database.update(c, "UPDATE catalogue SET
                // item_name=?,item_type=?,category_id=?,unit=?,description=?,price=?,active=? WHERE catalogue_id=?",
                // name, type, category, unit, description, price, active, id) is not equal to 1).
                } else if (Database.update(c,"UPDATE catalogue SET item_name=?,item_type=?,category_id=?,unit=?,description=?,price=?,active=? WHERE catalogue_id=?",name,type,category,unit,description,price,active,id) != 1) {
                    // Stop this operation with an exception: That item no longer exists. Refresh the list.
                    throw new IllegalArgumentException("That item no longer exists. Refresh the list.");
                }
                // Add an audit entry identifying the current user, action and affected record in this transaction.
                Database.audit(c,"Saved catalogue entry","catalogue",id);
                // Commit the transaction, making all its successful changes permanent together.
                c.commit();
            // Handle SQLException | RuntimeException ex from the preceding try block so the failure follows the
            // recovery steps below.
            // Roll back the transaction so its unfinished database changes are not kept.
            // Pass the caught exception back to the caller so the original failure is not treated as success.
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }

    // Re-read and lock each selected item during request submission.
    // A changed price or specification requires the customer to remove and re-add that item.
    // Lock and compare the current catalogue entry with the selected snapshot so changed prices require a
    // fresh selection.
    static Object[] validate(Connection c, RequestItem item, String type) throws SQLException {
        // Continue with this branch when (item.getCatalogueId() is at most 0).
        // Stop this operation with an exception: Choose an item from the catalogue.
        if (item.getCatalogueId() <= 0) { throw new IllegalArgumentException("Choose an item from the catalogue."); }
        // Declare row to hold one row of values in SELECT or table-column order. Read catalogue records; WHERE
        // limits the rows to the stated conditions; FOR UPDATE locks the selected rows until the transaction
        // ends; question marks receive separately bound values.
        // Result columns in order: 0: category_id; 1: item_name; 2: description; 3: unit; 4: price; 5:
        // item_type; 6: active. Object[] positions start at zero; JDBC column numbers start at one.
        Object[] row = Database.one(c,"SELECT category_id,item_name,description,unit,price,item_type,active FROM catalogue WHERE catalogue_id=? FOR UPDATE",item.getCatalogueId());
        // Declare active to hold a true-or-false flag. Its initial value is `Boolean.TRUE.equals(row[6]) ||
        // "1".equals(row[6].toString())`.
        boolean active = Boolean.TRUE.equals(row[6]) || "1".equals(row[6].toString());
        // Continue with this branch when (!active or not type.equals(row[5])).
        // Stop this operation with an exception: This catalogue item is unavailable for this request type.
        if (!active || !type.equals(row[5])) { throw new IllegalArgumentException("This catalogue item is unavailable for this request type."); }
        // Continue with this branch when (((BigDecimal)row[4]).compareTo(item.getUnitCost()) is not equal to 0
        // or !row[2].equals(item.getDescription()) or !row[3].equals(item.getUnit()) or Database.id(row[0]) is
        // not equal to item.getCategory().getCategoryId()).
        if (((BigDecimal)row[4]).compareTo(item.getUnitCost()) != 0 || !row[2].equals(item.getDescription())
                || !row[3].equals(item.getUnit()) || Database.id(row[0]) != item.getCategory().getCategoryId()) {
            // Stop this operation with an exception: Catalogue details changed. Remove and re-add
            throw new IllegalArgumentException("Catalogue details changed. Remove and re-add " + row[1] + " to review the current price.");
        }
        // Return row to the caller.
        return row;
    }
}
