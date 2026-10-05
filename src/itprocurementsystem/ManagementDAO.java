// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Make the public types in java.sql available by short names; this does not create objects.
import java.sql.*;
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;

// Save and search the supporting records. Admins administer accounts and departments.
// Define ManagementDAO as a class that groups its related data and methods.
public class ManagementDAO {
    // Read supplier records for authorised staff, ordered by supplier name.
    public ArrayList<Object[]> vendors() throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed roles to continue.
            Database.require(c, "Admin", "Manager", "Purchaser");
            // Read vendors records and ORDER BY fixes their display order. Return the resulting value to the
            // caller.
            // Result columns in order: 0: vendor_id; 1: vendor_name; 2: contact_person; 3: phone; 4: email; 5:
            // address. Object[] positions start at zero; JDBC column numbers start at one.
            return Database.rows(c, "SELECT vendor_id,vendor_name,contact_person,phone,email,address FROM vendors ORDER BY vendor_name");
        }
    }
    // An ID of zero means Add; a positive ID means Update Selected.
    // Validate supplier details, then insert a new vendor for ID zero or update the specified existing
    // vendor.
    public void saveVendor(int id, String name, String contact, String phone, String email, String address) throws SQLException {
        // Set name from the following operation: validate name as "Vendor name" with a maximum of 100
        // characters; a value is required.
        name = Database.text(name, "Vendor name", 100, true);
        // Set contact from the following operation: validate contact as "Contact person" with a maximum of 100
        // characters; the final argument controls whether a value is required.
        contact = Database.text(contact, "Contact person", 100, false);
        // Set phone from the following operation: validate phone as "Phone" with a maximum of 30 characters;
        // the final argument controls whether a value is required.
        phone = Database.text(phone, "Phone", 30, false);
        // Set email from the following operation: validate email as "Email" with a maximum of 100 characters;
        // the final argument controls whether a value is required.
        email = Database.text(email, "Email", 100, false);
        // Set address from the following operation: validate address as "Address" with a maximum of 255
        // characters; the final argument controls whether a value is required.
        address = Database.text(address, "Address", 255, false);
        // Continue with this branch when (not email is empty and not
        // email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")).
        // Stop this operation with an exception: Enter a valid email address.
        if (!email.isEmpty() && !email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) { throw new IllegalArgumentException("Enter a valid email address."); }
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed roles to continue.
            Database.require(c, "Admin", "Manager", "Purchaser");
            // Continue with this branch when (id equals 0).
            // Insert a record into vendors; question marks receive separately bound values.
            if (id == 0) { Database.update(c, "INSERT INTO vendors(vendor_name,contact_person,phone,email,address) VALUES (?,?,?,?,?)", name,contact,phone,email,address); }
            // Continue with this branch when (Database.update(c, "UPDATE vendors SET
            // vendor_name=?,contact_person=?,phone=?,email=?,address=? WHERE vendor_id=?", name, contact, phone,
            // email, address, id) is not equal to 1).
            else if (Database.update(c, "UPDATE vendors SET vendor_name=?,contact_person=?,phone=?,email=?,address=? WHERE vendor_id=?", name,contact,phone,email,address,id) != 1) {
                // Stop this operation with an exception: Vendor not found. Refresh first.
                throw new IllegalArgumentException("Vendor not found. Refresh first.");
            }
        }
    }
    // Read the department list for an Admin, ordered by department name.
    public ArrayList<Object[]> departments() throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed role to continue.
            Database.require(c, "Admin");
            // Read departments records and ORDER BY fixes their display order. Return the resulting value to the
            // caller.
            // Result columns in order: 0: department_id; 1: department_name. Object[] positions start at zero;
            // JDBC column numbers start at one.
            return Database.rows(c, "SELECT department_id,department_name FROM departments ORDER BY department_name");
        }
    }
    // Save a new or selected department after checking Admin access and duplicate names.
    public void saveDepartment(int id, String name) throws SQLException {
        // Set name from the following operation: validate name as "Department name" with a maximum of 100
        // characters; a value is required.
        name = Database.text(name, "Department name", 100, true);
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed role to continue.
            Database.require(c, "Admin");
            // Continue with this branch when (not Database.rows(c, "SELECT department_id FROM departments WHERE
            // department_name=? AND department_id<>?", name, id).isEmpty()).
            // Result columns in order: 0: department_id. Object[] positions start at zero; JDBC column numbers
            // start at one.
            if (!Database.rows(c, "SELECT department_id FROM departments WHERE department_name=? AND department_id<>?", name,id).isEmpty()) {
                // Stop this operation with an exception: That department name already exists.
                throw new IllegalArgumentException("That department name already exists.");
            }
            // Continue with this branch when (id equals 0).
            // Insert a record into departments; question marks receive separately bound values.
            if (id == 0) { Database.update(c, "INSERT INTO departments(department_name) VALUES (?)", name); }
            // Continue with this branch when (Database.update(c, "UPDATE departments SET department_name=? WHERE
            // department_id=?", name, id) is not equal to 1).
            // Stop this operation with an exception: Department not found.
            else if (Database.update(c, "UPDATE departments SET department_name=? WHERE department_id=?", name,id) != 1) { throw new IllegalArgumentException("Department not found."); }
        }
    }
    // Read account details for an Admin without selecting password hashes.
    public ArrayList<Object[]> users(String search) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed role to continue.
            Database.require(c, "Admin");
            // Declare match to hold text. Its initial value is `"%" + search.trim() + "%"`.
            String match = "%" + search.trim() + "%";
            // Read users records; WHERE limits the rows to the stated conditions and ORDER BY fixes their display
            // order; question marks receive separately bound values. Return the resulting value to the caller.
            // Result columns in order: 0: user_id; 1: full_name; 2: username; 3: email; 4: account_type; 5:
            // organisation_name; 6: role. Object[] positions start at zero; JDBC column numbers start at one.
            return Database.rows(c, "SELECT user_id,full_name,username,email,account_type,organisation_name,role FROM users WHERE full_name LIKE ? OR username LIKE ? OR email LIKE ? ORDER BY full_name", match,match,match);
        }
    }
    // Do not let an Admin remove their own management access by mistake.
    // Change another account's role after locking Admin records to prevent conflicting role changes.
    public void changeRole(int user, String role) throws SQLException {
        // Continue with this branch when (!"Requester".equals(role) and !"Manager".equals(role) and
        // !"Purchaser".equals(role) and !"Admin".equals(role)).
        // Stop this operation with an exception: Select a valid role.
        if (!"Requester".equals(role) && !"Manager".equals(role) && !"Purchaser".equals(role) && !"Admin".equals(role)) { throw new IllegalArgumentException("Select a valid role."); }
        // Check the session identity before continuing so a missing or different account follows this branch.
        // Stop this operation with an exception: Ask another Admin to change ythe role.
        if (user == Session.getUserId()) { throw new IllegalArgumentException("Ask another Admin to change your role."); }
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
                // Lock Admins in a consistent order before changing a role. Two Admins
                // cannot simultaneously remove each other's access and leave no Admin.
                // Read users records; WHERE limits the rows to the stated conditions and ORDER BY fixes their display
                // order; FOR UPDATE locks the selected rows until the transaction ends.
                // Result columns in order: 0: user_id. Object[] positions start at zero; JDBC column numbers start at
                // one.
                Database.rows(c, "SELECT user_id FROM users WHERE role='Admin' ORDER BY user_id FOR UPDATE");
                // Recheck the database session and allow only the listed role to continue.
                Database.require(c, "Admin");
                // Continue with this branch when (Database.update(c, "UPDATE users SET role=? WHERE user_id=?", role,
                // user) is not equal to 1).
                // Stop this operation with an exception: User not found.
                if (Database.update(c, "UPDATE users SET role=? WHERE user_id=?", role,user) != 1) { throw new IllegalArgumentException("User not found."); }
                // Add an audit entry identifying the current user, action and affected record in this transaction.
                Database.audit(c, "Changed role to " + role, "users", user);
                // Save the notification message for the specified account in the current transaction.
                Database.notify(c, user, "Your role is now " + role + ". Please log out and log in again.");
                // Commit the transaction, making all its successful changes permanent together.
                c.commit();
            // Handle SQLException | RuntimeException ex from the preceding try block so the failure follows the
            // recovery steps below.
            // Roll back the transaction so its unfinished database changes are not kept.
            // Pass the caught exception back to the caller so the original failure is not treated as success.
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }
    // Notifications always belong to the signed-in user, including staff notifications.
    // Read only the signed-in account's notifications matching the selected read-status filter.
    public ArrayList<Object[]> notifications(String filter) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed roles to continue.
            Database.require(c, "Requester", "Manager", "Purchaser", "Admin");
            // Read notifications records; WHERE limits the rows to the stated conditions and ORDER BY fixes their
            // display order; question marks receive separately bound values. Return the resulting value to the
            // caller.
            // Result columns in order: 0: notification_id; 1: date_created; 2: message; 3:
            // IF(is_read=0,'Unread','Read'). Object[] positions start at zero; JDBC column numbers start at one.
            return Database.rows(c, "SELECT notification_id,date_created,message,IF(is_read=0,'Unread','Read') FROM notifications WHERE user_id=? AND (?='All notifications' OR (?='Unread' AND is_read=0) OR (?='Read' AND is_read=1)) ORDER BY notification_id DESC", Session.getUserId(),filter,filter,filter);
        }
    }
    // Count unread notifications belonging to the signed-in account.
    public int unreadCount() throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed roles to continue.
            Database.require(c, "Requester", "Manager", "Purchaser", "Admin");
            // Read notifications records and calculate a record count; WHERE limits the rows to the stated
            // conditions; question marks receive separately bound values. Return the resulting value to the
            // caller.
            // Result columns in order: 0: COUNT(*). Object[] positions start at zero; JDBC column numbers start at
            // one.
            return Database.id(Database.one(c, "SELECT COUNT(*) FROM notifications WHERE user_id=? AND is_read=0", Session.getUserId())[0]);
        }
    }
    // Zero marks all of this user's notifications; another user's ID cannot be updated.
    // Mark the selected notification as read; ID zero means all notifications belonging to this account.
    public void markRead(int id) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed roles to continue.
            Database.require(c, "Requester", "Manager", "Purchaser", "Admin");
            // Update notifications values only for rows matching the WHERE condition; question marks receive
            // separately bound values.
            Database.update(c, "UPDATE notifications SET is_read=1 WHERE user_id=? AND (?=0 OR notification_id=?)", Session.getUserId(),id,id);
        }
    }
}
