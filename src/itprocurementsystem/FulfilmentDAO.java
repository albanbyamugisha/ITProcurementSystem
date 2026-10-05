// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Make the public types in java.sql available by short names; this does not create objects.
import java.sql.*;
// Import LocalDate for calendar dates without a time of day.
import java.time.LocalDate;
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;
// Import HashSet for a set that rejects repeated values.
import java.util.HashSet;

// Equipment creates deliveries and inventory units; services use their own progress table.
// Define FulfilmentDAO as a class that groups its related data and methods.
public class FulfilmentDAO {
    // Require an approved, correctly typed request before changing fulfilment records.
    // Check Purchaser access, request type and Approved status; optionally lock the request while saving.
    private Object[] approved(Connection c, int request, String type, boolean lock) throws SQLException {
        // Recheck the database session and allow only the listed role to continue.
        Database.require(c,"Purchaser");
        // Declare r to hold one row of values in SELECT or table-column order. Read requests records; WHERE
        // limits the rows to the stated conditions; FOR UPDATE locks the selected rows until the transaction
        // ends; question marks receive separately bound values.
        // Result columns in order: 0: request_status; 1: request_type; 2: requester_id. Object[] positions
        // start at zero; JDBC column numbers start at one.
        Object[] r = Database.one(c,"SELECT request_status,request_type,requester_id FROM requests WHERE request_id=?" + (lock ? " FOR UPDATE" : ""),request);
        // Continue with this branch when (!"Approved".equals(r[0]) or not type.equals(r[1])).
        // Stop this operation with an exception: Choose an approved
        if (!"Approved".equals(r[0]) || !type.equals(r[1])) { throw new IllegalArgumentException("Choose an approved " + type.toLowerCase() + " request."); }
        // Return r to the caller.
        return r;
    }
    // List approved requests of the selected Equipment or Service type for a Purchaser.
    public ArrayList<Object[]> requests(String type) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed role to continue.
            Database.require(c,"Purchaser");
            // Read requests records; WHERE limits the rows to the stated conditions and ORDER BY fixes their
            // display order; question marks receive separately bound values. Return the resulting value to the
            // caller.
            // Result columns in order: 0: request_id; 1: CONCAT(request_type,' / ',request_status). Object[]
            // positions start at zero; JDBC column numbers start at one.
            return Database.rows(c,"SELECT request_id,CONCAT(request_type,' / ',request_status) FROM requests WHERE request_type=? AND request_status='Approved' ORDER BY request_id DESC",type);
        }
    }
    // Find the supplier linked to the manager-selected quotation for an approved request.
    public String supplier(int request, String type) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Check Purchaser access, request type and Approved status; optionally lock the request while saving.
            approved(c,request,type,false);
            // Read request_selections records with matching information from related tables; WHERE limits the rows
            // to the stated conditions; question marks receive separately bound values. Return the resulting value
            // to the caller.
            // Result columns in order: 0: v.vendor_name. Object[] positions start at zero; JDBC column numbers
            // start at one.
            return Database.one(c,"SELECT v.vendor_name FROM request_selections s JOIN quotations q ON q.quotation_id=s.quotation_id JOIN vendors v ON v.vendor_id=q.vendor_id WHERE s.request_id=?",request)[0].toString();
        }
    }
    // Read ordered, received and outstanding quantities for the approved equipment request.
    public ArrayList<Object[]> equipmentItems(int request) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Check Purchaser access, request type and Approved status; optionally lock the request while saving.
            approved(c,request,"Equipment",false);
            // Read request_items records and add the matching amounts or quantities; LEFT JOIN keeps base rows
            // even when related records are missing; WHERE limits the rows to the stated conditions and ORDER BY
            // fixes their display order; question marks receive separately bound values. Return the resulting
            // value to the caller.
            // Result columns in order: 0: i.request_item_id; 1: i.item_description; 2: i.quantity; 3:
            // COALESCE(SUM(d.quantity),0); 4: i.quantity-COALESCE(SUM(d.quantity),0). Object[] positions start at
            // zero; JDBC column numbers start at one.
            return Database.rows(c,"SELECT i.request_item_id,i.item_description,i.quantity,COALESCE(SUM(d.quantity),0),i.quantity-COALESCE(SUM(d.quantity),0) FROM request_items i LEFT JOIN delivery_items d ON d.request_item_id=i.request_item_id WHERE i.request_id=? GROUP BY i.request_item_id,i.item_description,i.quantity ORDER BY i.request_item_id",request);
        }
    }
    // ISO dates avoid the ambiguity between day/month and month/day.
    // Parse a YYYY-MM-DD date, reject future dates, and allow an empty value only when it is optional.
    private java.sql.Date date(String value, boolean required) {
        // Continue with this branch when (value equals no object (null) or value.trim().isEmpty()).
        if (value == null || value.trim().isEmpty()) {
            // Continue with this branch when (required).
            // Stop this operation with an exception: Enter a date in YYYY-MM-DD format.
            if (required) { throw new IllegalArgumentException("Enter a date in YYYY-MM-DD format."); }
            // Return no value (null) to the caller.
            return null;
        }
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Declare parsed with type LocalDate. Its initial value is `LocalDate.parse(value.trim())`.
            LocalDate parsed = LocalDate.parse(value.trim());
            // Continue with this branch when (parsed.isAfter(LocalDate.now())).
            // Stop this operation with an exception: The date cannot be in the future.
            if (parsed.isAfter(LocalDate.now())) { throw new IllegalArgumentException("The date cannot be in the future."); }
            // Return `java.sql.Date.valueOf(parsed)` to the caller.
            return java.sql.Date.valueOf(parsed);
        // Handle java.time.format.DateTimeParseException ex from the preceding try block so the failure
        // follows the recovery steps below.
        // Stop this operation with an exception: Enter a real date in YYYY-MM-DD format.
        } catch (java.time.format.DateTimeParseException ex) { throw new IllegalArgumentException("Enter a real date in YYYY-MM-DD format."); }
    }
    // All units and their inventory rows are saved together, or none are saved.
    // Save each received serial-numbered unit and its inventory row together, preventing excess
    // deliveries.
    public void saveDelivery(int request, String deliveryDate, ArrayList<DeliveryUnit> units) throws SQLException {
        // Declare received with type java.sql.Date. Parse a YYYY-MM-DD date, reject future dates, and allow an
        // empty value only when it is optional.
        java.sql.Date received = date(deliveryDate,true);
        // Continue with this branch when (units equals no object (null) or units is empty).
        // Stop this operation with an exception: Add at least one received unit.
        if (units == null || units.isEmpty()) { throw new IllegalArgumentException("Add at least one received unit."); }
        // Declare serials with type HashSet<String>. Create a set for tracking unique values.
        HashSet<String> serials = new HashSet<String>();
        // Process each entry in units in turn, referring to the current entry as unit.
        for (DeliveryUnit unit : units) {
            // Continue with this branch when (unit equals no object (null) or not
            // serials.add(unit.getSerialNumber().toLowerCase(java.util.Locale.ROOT))).
            // Stop this operation with an exception: Each received unit needs a different serial number.
            if (unit == null || !serials.add(unit.getSerialNumber().toLowerCase(java.util.Locale.ROOT))) { throw new IllegalArgumentException("Each received unit needs a different serial number."); }
        }
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Read newly committed quantities after waiting for another purchaser's lock.
            // Otherwise MySQL's older snapshot could miss a just-saved partial delivery.
            // Read committed database values so a transaction sees updates saved before each new query.
            c.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            // Begin a transaction so later database changes are saved together rather than one statement at a
            // time.
            c.setAutoCommit(false);
            // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
            try {
                // Declare r to hold one row of values in SELECT or table-column order. Check Purchaser access, request
                // type and Approved status; optionally lock the request while saving.
                Object[] r = approved(c,request,"Equipment",true);
                // Declare vendor to hold a whole-number value. Read request_selections records with matching
                // information from related tables; WHERE limits the rows to the stated conditions; question marks
                // receive separately bound values.
                // Result columns in order: 0: q.vendor_id. Object[] positions start at zero; JDBC column numbers start
                // at one.
                int vendor = Database.id(Database.one(c,"SELECT q.vendor_id FROM request_selections s JOIN quotations q ON q.quotation_id=s.quotation_id WHERE s.request_id=?",request)[0]);
                // Declare delivery to hold a whole-number value. Insert a record into deliveries; question marks
                // receive separately bound values.
                int delivery = Database.insert(c,"INSERT INTO deliveries(request_id,vendor_id,delivery_date,received_by,delivery_status) VALUES (?,?,?,?,'Received')",request,vendor,received,Session.getUserId());
                // Process each entry in units in turn, referring to the current entry as unit.
                for (DeliveryUnit unit : units) {
                    // Declare item to hold one row of values in SELECT or table-column order. Read request_items records;
                    // WHERE limits the rows to the stated conditions; question marks receive separately bound values.
                    // Result columns in order: 0: item_description; 1: quantity; 2: category_id. Object[] positions start
                    // at zero; JDBC column numbers start at one.
                    Object[] item = Database.one(c,"SELECT item_description,quantity,category_id FROM request_items WHERE request_item_id=? AND request_id=?",unit.getItemId(),request);
                    // Declare already to hold a whole-number value. Read delivery_items records and add the matching
                    // amounts or quantities; WHERE limits the rows to the stated conditions; question marks receive
                    // separately bound values.
                    // Result columns in order: 0: COALESCE(SUM(quantity),0). Object[] positions start at zero; JDBC column
                    // numbers start at one.
                    int already = Database.id(Database.one(c,"SELECT COALESCE(SUM(quantity),0) FROM delivery_items WHERE request_item_id=?",unit.getItemId())[0]);
                    // Continue with this branch when (already is at least Database.id(item[1])).
                    // Stop this operation with an exception: You cannot receive more units than were ordered.
                    if (already >= Database.id(item[1])) { throw new IllegalArgumentException("You cannot receive more units than were ordered."); }
                    // Unique serial constraints also reject duplicates saved in another delivery.
                    // Declare deliveredItem to hold a whole-number value. Insert a record into delivery_items; question
                    // marks receive separately bound values.
                    int deliveredItem = Database.insert(c,"INSERT INTO delivery_items(delivery_id,request_item_id,item_description,serial_number,quantity) VALUES (?,?,?,?,1)",delivery,unit.getItemId(),item[0],unit.getSerialNumber());
                    // Insert a record into inventory; question marks receive separately bound values.
                    Database.update(c,"INSERT INTO inventory(delivery_item_id,item_description,serial_number,category_id) VALUES (?,?,?,?)",deliveredItem,item[0],unit.getSerialNumber(),item[2]);
                }
                // Declare ordered to hold a larger whole-number value. Read request_items records and add the matching
                // amounts or quantities; WHERE limits the rows to the stated conditions; question marks receive
                // separately bound values.
                // Result columns in order: 0: SUM(quantity). Object[] positions start at zero; JDBC column numbers
                // start at one.
                long ordered = ((Number) Database.one(c,"SELECT SUM(quantity) FROM request_items WHERE request_id=?",request)[0]).longValue();
                // Declare arrived to hold a larger whole-number value. Read delivery_items records and add the
                // matching amounts or quantities with matching information from related tables; WHERE limits the rows
                // to the stated conditions; question marks receive separately bound values.
                // Result columns in order: 0: COALESCE(SUM(d.quantity),0). Object[] positions start at zero; JDBC
                // column numbers start at one.
                long arrived = ((Number) Database.one(c,"SELECT COALESCE(SUM(d.quantity),0) FROM delivery_items d JOIN request_items i ON i.request_item_id=d.request_item_id WHERE i.request_id=?",request)[0]).longValue();
                // Continue with this branch when (arrived equals ordered).
                // Update requests values only for rows matching the WHERE condition; question marks receive separately
                // bound values.
                if (arrived == ordered) { Database.update(c,"UPDATE requests SET request_status='Delivered' WHERE request_id=?",request); }
                // Save the notification message for the specified account in the current transaction.
                Database.notify(c,Database.id(r[2]),"Request #" + request + ": received " + arrived + " of " + ordered + " equipment units.");
                // Add an audit entry identifying the current user, action and affected record in this transaction.
                Database.audit(c,"Recorded delivery for request #" + request,"deliveries",delivery);
                // Commit the transaction, making all its successful changes permanent together.
                c.commit();
            // Handle SQLException | RuntimeException ex from the preceding try block so the failure follows the
            // recovery steps below.
            // Roll back the transaction so its unfinished database changes are not kept.
            // Pass the caught exception back to the caller so the original failure is not treated as success.
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }
    // Read requested service items after checking that this is an approved service request.
    public ArrayList<Object[]> serviceItems(int request) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Check Purchaser access, request type and Approved status; optionally lock the request while saving.
            approved(c,request,"Service",false);
            // Read request_items records; WHERE limits the rows to the stated conditions; question marks receive
            // separately bound values. Return the resulting value to the caller.
            // Result columns in order: 0: request_item_id; 1: item_description; 2: quantity. Object[] positions
            // start at zero; JDBC column numbers start at one.
            return Database.rows(c,"SELECT request_item_id,item_description,quantity FROM request_items WHERE request_id=?",request);
        }
    }
    // Read saved service progress, or return initial values when no progress record exists yet.
    public Object[] progress(int request) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Check Purchaser access, request type and Approved status; optionally lock the request while saving.
            approved(c,request,"Service",false);
            // Declare rows with type ArrayList<Object[]>. Read service_progress records; WHERE limits the rows to
            // the stated conditions; question marks receive separately bound values.
            // Result columns in order: 0: work_status; 1: work_notes; 2: completion_date. Object[] positions start
            // at zero; JDBC column numbers start at one.
            ArrayList<Object[]> rows = Database.rows(c,"SELECT work_status,work_notes,completion_date FROM service_progress WHERE request_id=?",request);
            // Read entry 0 from rows.isEmpty() ? new Object[]{"Not Started", "", null} : rows; list positions
            // start at zero. Return the resulting value to the caller.
            return rows.isEmpty() ? new Object[] {"Not Started","",null} : rows.get(0);
        }
    }
    // Validate and save service status, notes and completion date in one transaction.
    public void saveProgress(int request, String status, String notes, String completionDate) throws SQLException {
        // Continue with this branch when (!"Not Started".equals(status) and !"In Progress".equals(status) and
        // !"Completed".equals(status)).
        // Stop this operation with an exception: Select a work status.
        if (!"Not Started".equals(status) && !"In Progress".equals(status) && !"Completed".equals(status)) { throw new IllegalArgumentException("Select a work status."); }
        // Set notes from the following operation: validate notes as "Work notes" with a maximum of 4000
        // characters; the final argument controls whether a value is required.
        notes = Database.text(notes,"Work notes",4000,false);
        // Declare completion with type java.sql.Date. Parse a YYYY-MM-DD date, reject future dates, and allow
        // an empty value only when it is optional.
        java.sql.Date completion = date(completionDate,"Completed".equals(status));
        // Continue with this branch when (!"Completed".equals(status) and completion is not equal to no object
        // (null)).
        // Stop this operation with an exception: Enter a completion date only for completed work.
        if (!"Completed".equals(status) && completion != null) { throw new IllegalArgumentException("Enter a completion date only for completed work."); }
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Use freshly committed values for each query in this transaction.
            // This avoids relying on an older snapshot while another staff member saves changes.
            // Read committed database values so a transaction sees updates saved before each new query.
            c.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            // Begin a transaction so later database changes are saved together rather than one statement at a
            // time.
            c.setAutoCommit(false);
            // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
            try {
                // Declare r to hold one row of values in SELECT or table-column order. Check Purchaser access, request
                // type and Approved status; optionally lock the request while saving.
                Object[] r = approved(c,request,"Service",true);
                // Insert a record into service_progress; update the existing keyed record instead if it is already
                // present; question marks receive separately bound values.
                Database.update(c,"INSERT INTO service_progress(request_id,work_status,work_notes,completion_date,updated_by) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE work_status=VALUES(work_status),work_notes=VALUES(work_notes),completion_date=VALUES(completion_date),updated_by=VALUES(updated_by)",request,status,notes,completion,Session.getUserId());
                // Continue with this branch when ("Completed".equals(status)).
                // Update requests values only for rows matching the WHERE condition; question marks receive separately
                // bound values.
                if ("Completed".equals(status)) { Database.update(c,"UPDATE requests SET request_status='Completed' WHERE request_id=?",request); }
                // Save the notification message for the specified account in the current transaction.
                Database.notify(c,Database.id(r[2]),"Service request #" + request + ": " + status + ".");
                // Add an audit entry identifying the current user, action and affected record in this transaction.
                Database.audit(c,"Service status: " + status,"requests",request);
                // Commit the transaction, making all its successful changes permanent together.
                c.commit();
            // Handle SQLException | RuntimeException ex from the preceding try block so the failure follows the
            // recovery steps below.
            // Roll back the transaction so its unfinished database changes are not kept.
            // Pass the caught exception back to the caller so the original failure is not treated as success.
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }
    // Inventory remains a staff screen; customers see fulfilment notifications.
    // Find staff-visible equipment by description or serial number, including any assigned user.
    public ArrayList<Object[]> inventory(String search) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed roles to continue.
            Database.require(c,"Admin","Manager","Purchaser");
            // Declare match to hold text. Its initial value is `"%" + search.trim() + "%"`.
            String match = "%" + search.trim() + "%";
            // Read inventory records; LEFT JOIN keeps base rows even when related records are missing; WHERE
            // limits the rows to the stated conditions and ORDER BY fixes their display order; question marks
            // receive separately bound values. Return the resulting value to the caller.
            // Result columns in order: 0: i.inventory_id; 1: i.item_description; 2: i.serial_number; 3:
            // c.category_name; 4: u.full_name; 5: i.date_added; 6: r.request_id. Object[] positions start at zero;
            // JDBC column numbers start at one.
            return Database.rows(c,"SELECT i.inventory_id,i.item_description,i.serial_number,c.category_name,u.full_name,i.date_added,r.request_id FROM inventory i JOIN categories c ON c.category_id=i.category_id LEFT JOIN users u ON u.user_id=i.assigned_to JOIN delivery_items d ON d.delivery_item_id=i.delivery_item_id JOIN request_items r ON r.request_item_id=d.request_item_id WHERE i.item_description LIKE ? OR i.serial_number LIKE ? ORDER BY i.inventory_id DESC",match,match);
        }
    }
    // List account IDs and readable names for assigning equipment after checking staff access.
    public ArrayList<Object[]> assignees() throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed roles to continue.
            Database.require(c,"Admin","Manager","Purchaser");
            // Read users records and ORDER BY fixes their display order. Return the resulting value to the caller.
            // Result columns in order: 0: user_id; 1: CONCAT(full_name,' (',username,')'). Object[] positions
            // start at zero; JDBC column numbers start at one.
            return Database.rows(c,"SELECT user_id,CONCAT(full_name,' (',username,')') FROM users ORDER BY full_name");
        }
    }
    // Update or clear an inventory assignment and save its audit record and optional user notification
    // together.
    public void assign(int inventory, Integer user) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Use freshly committed values for each query in this transaction.
            // This avoids relying on an older snapshot while another staff member saves changes.
            // Read committed database values so a transaction sees updates saved before each new query.
            c.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            // Begin a transaction so later database changes are saved together rather than one statement at a
            // time.
            c.setAutoCommit(false);
            // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
            try {
                // Recheck the database session and allow only the listed roles to continue.
                Database.require(c,"Admin","Manager","Purchaser");
                // Continue with this branch when (Database.update(c, "UPDATE inventory SET assigned_to=? WHERE
                // inventory_id=?", user, inventory) is not equal to 1).
                // Stop this operation with an exception: Equipment no longer exists.
                if (Database.update(c,"UPDATE inventory SET assigned_to=? WHERE inventory_id=?",user,inventory) != 1) { throw new IllegalArgumentException("Equipment no longer exists."); }
                // Add an audit entry identifying the current user, action and affected record in this transaction.
                Database.audit(c,user == null ? "Unassigned equipment" : "Assigned equipment to user #" + user,"inventory",inventory);
                // Continue with this branch when (user is not equal to no object (null)).
                // Save the notification message for the specified account in the current transaction.
                if (user != null) { Database.notify(c,user,"Equipment #" + inventory + " has been assigned to you."); }
                // Commit the transaction, making all its successful changes permanent together.
                c.commit();
            // Handle SQLException | RuntimeException ex from the preceding try block so the failure follows the
            // recovery steps below.
            // Roll back the transaction so its unfinished database changes are not kept.
            // Pass the caught exception back to the caller so the original failure is not treated as success.
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }
}
