package itprocurementsystem;

import java.sql.*;
import java.util.ArrayList;

// Save and search the supporting records. Managers administer accounts and departments.
public class ManagementDAO {
    public ArrayList<Object[]> vendors() throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Database.require(c, "Manager", "Purchaser");
            return Database.rows(c, "SELECT vendor_id,vendor_name,contact_person,phone,email,address FROM vendors ORDER BY vendor_name");
        }
    }
    // An ID of zero means Add; a positive ID means Update Selected.
    public void saveVendor(int id, String name, String contact, String phone, String email, String address) throws SQLException {
        name = Database.text(name, "Vendor name", 100, true);
        contact = Database.text(contact, "Contact person", 100, false);
        phone = Database.text(phone, "Phone", 30, false);
        email = Database.text(email, "Email", 100, false);
        address = Database.text(address, "Address", 255, false);
        if (!email.isEmpty() && !email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) { throw new IllegalArgumentException("Enter a valid email address."); }
        try (Connection c = DBConnection.getConnection()) {
            Database.require(c, "Manager", "Purchaser");
            if (id == 0) { Database.update(c, "INSERT INTO vendors(vendor_name,contact_person,phone,email,address) VALUES (?,?,?,?,?)", name,contact,phone,email,address); }
            else if (Database.update(c, "UPDATE vendors SET vendor_name=?,contact_person=?,phone=?,email=?,address=? WHERE vendor_id=?", name,contact,phone,email,address,id) != 1) {
                throw new IllegalArgumentException("Vendor not found. Refresh first.");
            }
        }
    }
    public ArrayList<Object[]> departments() throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Database.require(c, "Manager");
            return Database.rows(c, "SELECT department_id,department_name FROM departments ORDER BY department_name");
        }
    }
    public void saveDepartment(int id, String name) throws SQLException {
        name = Database.text(name, "Department name", 100, true);
        try (Connection c = DBConnection.getConnection()) {
            Database.require(c, "Manager");
            if (!Database.rows(c, "SELECT department_id FROM departments WHERE department_name=? AND department_id<>?", name,id).isEmpty()) {
                throw new IllegalArgumentException("That department name already exists.");
            }
            if (id == 0) { Database.update(c, "INSERT INTO departments(department_name) VALUES (?)", name); }
            else if (Database.update(c, "UPDATE departments SET department_name=? WHERE department_id=?", name,id) != 1) { throw new IllegalArgumentException("Department not found."); }
        }
    }
    public ArrayList<Object[]> users(String search) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Database.require(c, "Manager");
            String match = "%" + search.trim() + "%";
            return Database.rows(c, "SELECT user_id,full_name,username,email,account_type,organisation_name,role FROM users WHERE full_name LIKE ? OR username LIKE ? OR email LIKE ? ORDER BY full_name", match,match,match);
        }
    }
    // Do not let a manager remove their own management access by mistake.
    public void changeRole(int user, String role) throws SQLException {
        if (!"Requester".equals(role) && !"Manager".equals(role) && !"Purchaser".equals(role)) { throw new IllegalArgumentException("Select a valid role."); }
        if (user == Session.getUserId()) { throw new IllegalArgumentException("Ask another manager to change your role."); }
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                Database.require(c, "Manager");
                if (Database.update(c, "UPDATE users SET role=? WHERE user_id=?", role,user) != 1) { throw new IllegalArgumentException("User not found."); }
                Database.audit(c, "Changed role to " + role, "users", user);
                Database.notify(c, user, "Your role is now " + role + ". Please log out and log in again.");
                c.commit();
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }
    // Notifications always belong to the signed-in user, including staff notifications.
    public ArrayList<Object[]> notifications(String filter) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Database.require(c, "Requester", "Manager", "Purchaser");
            return Database.rows(c, "SELECT notification_id,date_created,message,IF(is_read=0,'Unread','Read') FROM notifications WHERE user_id=? AND (?='All notifications' OR (?='Unread' AND is_read=0) OR (?='Read' AND is_read=1)) ORDER BY notification_id DESC", Session.getUserId(),filter,filter,filter);
        }
    }
    public int unreadCount() throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Database.require(c, "Requester", "Manager", "Purchaser");
            return Database.id(Database.one(c, "SELECT COUNT(*) FROM notifications WHERE user_id=? AND is_read=0", Session.getUserId())[0]);
        }
    }
    // Zero marks all of this user's notifications; another user's ID cannot be updated.
    public void markRead(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Database.require(c, "Requester", "Manager", "Purchaser");
            Database.update(c, "UPDATE notifications SET is_read=1 WHERE user_id=? AND (?=0 OR notification_id=?)", Session.getUserId(),id,id);
        }
    }
}
