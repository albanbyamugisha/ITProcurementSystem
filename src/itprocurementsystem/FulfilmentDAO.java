package itprocurementsystem;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

// Equipment creates deliveries and inventory units; services use their own progress table.
public class FulfilmentDAO {
    // Require an approved, correctly typed request before changing fulfilment records.
    private Object[] approved(Connection c, int request, String type, boolean lock) throws SQLException {
        Database.require(c,"Purchaser");
        Object[] r = Database.one(c,"SELECT request_status,request_type,requester_id FROM requests WHERE request_id=?" + (lock ? " FOR UPDATE" : ""),request);
        if (!"Approved".equals(r[0]) || !type.equals(r[1])) { throw new IllegalArgumentException("Choose an approved " + type.toLowerCase() + " request."); }
        return r;
    }
    public ArrayList<Object[]> requests(String type) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Database.require(c,"Purchaser");
            return Database.rows(c,"SELECT request_id,CONCAT(request_type,' / ',request_status) FROM requests WHERE request_type=? AND request_status='Approved' ORDER BY request_id DESC",type);
        }
    }
    public String supplier(int request, String type) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            approved(c,request,type,false);
            return Database.one(c,"SELECT v.vendor_name FROM request_selections s JOIN quotations q ON q.quotation_id=s.quotation_id JOIN vendors v ON v.vendor_id=q.vendor_id WHERE s.request_id=?",request)[0].toString();
        }
    }
    public ArrayList<Object[]> equipmentItems(int request) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            approved(c,request,"Equipment",false);
            return Database.rows(c,"SELECT i.request_item_id,i.item_description,i.quantity,COALESCE(SUM(d.quantity),0),i.quantity-COALESCE(SUM(d.quantity),0) FROM request_items i LEFT JOIN delivery_items d ON d.request_item_id=i.request_item_id WHERE i.request_id=? GROUP BY i.request_item_id,i.item_description,i.quantity ORDER BY i.request_item_id",request);
        }
    }
    // ISO dates avoid the ambiguity between day/month and month/day.
    private java.sql.Date date(String value, boolean required) {
        if (value == null || value.trim().isEmpty()) {
            if (required) { throw new IllegalArgumentException("Enter a date in YYYY-MM-DD format."); }
            return null;
        }
        try {
            LocalDate parsed = LocalDate.parse(value.trim());
            if (parsed.isAfter(LocalDate.now())) { throw new IllegalArgumentException("The date cannot be in the future."); }
            return java.sql.Date.valueOf(parsed);
        } catch (java.time.format.DateTimeParseException ex) { throw new IllegalArgumentException("Enter a real date in YYYY-MM-DD format."); }
    }
    // All units and their inventory rows are saved together, or none are saved.
    public void saveDelivery(int request, String deliveryDate, ArrayList<DeliveryUnit> units) throws SQLException {
        java.sql.Date received = date(deliveryDate,true);
        if (units == null || units.isEmpty()) { throw new IllegalArgumentException("Add at least one received unit."); }
        HashSet<String> serials = new HashSet<String>();
        for (DeliveryUnit unit : units) {
            if (unit == null || !serials.add(unit.getSerialNumber().toLowerCase(java.util.Locale.ROOT))) { throw new IllegalArgumentException("Each received unit needs a different serial number."); }
        }
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                Object[] r = approved(c,request,"Equipment",true);
                int vendor = Database.id(Database.one(c,"SELECT q.vendor_id FROM request_selections s JOIN quotations q ON q.quotation_id=s.quotation_id WHERE s.request_id=?",request)[0]);
                int delivery = Database.insert(c,"INSERT INTO deliveries(request_id,vendor_id,delivery_date,received_by,delivery_status) VALUES (?,?,?,?,'Received')",request,vendor,received,Session.getUserId());
                for (DeliveryUnit unit : units) {
                    Object[] item = Database.one(c,"SELECT item_description,quantity,category_id FROM request_items WHERE request_item_id=? AND request_id=?",unit.getItemId(),request);
                    int already = Database.id(Database.one(c,"SELECT COALESCE(SUM(quantity),0) FROM delivery_items WHERE request_item_id=?",unit.getItemId())[0]);
                    if (already >= Database.id(item[1])) { throw new IllegalArgumentException("You cannot receive more units than were ordered."); }
                    // Unique serial constraints also reject duplicates saved in another delivery.
                    int deliveredItem = Database.insert(c,"INSERT INTO delivery_items(delivery_id,request_item_id,item_description,serial_number,quantity) VALUES (?,?,?,?,1)",delivery,unit.getItemId(),item[0],unit.getSerialNumber());
                    Database.update(c,"INSERT INTO inventory(delivery_item_id,item_description,serial_number,category_id) VALUES (?,?,?,?)",deliveredItem,item[0],unit.getSerialNumber(),item[2]);
                }
                long ordered = ((Number) Database.one(c,"SELECT SUM(quantity) FROM request_items WHERE request_id=?",request)[0]).longValue();
                long arrived = ((Number) Database.one(c,"SELECT COALESCE(SUM(d.quantity),0) FROM delivery_items d JOIN request_items i ON i.request_item_id=d.request_item_id WHERE i.request_id=?",request)[0]).longValue();
                if (arrived == ordered) { Database.update(c,"UPDATE requests SET request_status='Delivered' WHERE request_id=?",request); }
                Database.notify(c,Database.id(r[2]),"Request #" + request + ": received " + arrived + " of " + ordered + " equipment units.");
                Database.audit(c,"Recorded delivery for request #" + request,"deliveries",delivery);
                c.commit();
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }
    public ArrayList<Object[]> serviceItems(int request) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            approved(c,request,"Service",false);
            return Database.rows(c,"SELECT request_item_id,item_description,quantity FROM request_items WHERE request_id=?",request);
        }
    }
    public Object[] progress(int request) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            approved(c,request,"Service",false);
            ArrayList<Object[]> rows = Database.rows(c,"SELECT work_status,work_notes,completion_date FROM service_progress WHERE request_id=?",request);
            return rows.isEmpty() ? new Object[] {"Not Started","",null} : rows.get(0);
        }
    }
    public void saveProgress(int request, String status, String notes, String completionDate) throws SQLException {
        if (!"Not Started".equals(status) && !"In Progress".equals(status) && !"Completed".equals(status)) { throw new IllegalArgumentException("Select a work status."); }
        notes = Database.text(notes,"Work notes",4000,false);
        java.sql.Date completion = date(completionDate,"Completed".equals(status));
        if (!"Completed".equals(status) && completion != null) { throw new IllegalArgumentException("Enter a completion date only for completed work."); }
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                Object[] r = approved(c,request,"Service",true);
                Database.update(c,"INSERT INTO service_progress(request_id,work_status,work_notes,completion_date,updated_by) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE work_status=VALUES(work_status),work_notes=VALUES(work_notes),completion_date=VALUES(completion_date),updated_by=VALUES(updated_by)",request,status,notes,completion,Session.getUserId());
                if ("Completed".equals(status)) { Database.update(c,"UPDATE requests SET request_status='Completed' WHERE request_id=?",request); }
                Database.notify(c,Database.id(r[2]),"Service request #" + request + ": " + status + ".");
                Database.audit(c,"Service status: " + status,"requests",request);
                c.commit();
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }
    // Inventory remains a staff screen; customers see fulfilment notifications.
    public ArrayList<Object[]> inventory(String search) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Database.require(c,"Manager","Purchaser");
            String match = "%" + search.trim() + "%";
            return Database.rows(c,"SELECT i.inventory_id,i.item_description,i.serial_number,c.category_name,u.full_name,i.date_added,r.request_id FROM inventory i JOIN categories c ON c.category_id=i.category_id LEFT JOIN users u ON u.user_id=i.assigned_to JOIN delivery_items d ON d.delivery_item_id=i.delivery_item_id JOIN request_items r ON r.request_item_id=d.request_item_id WHERE i.item_description LIKE ? OR i.serial_number LIKE ? ORDER BY i.inventory_id DESC",match,match);
        }
    }
    public ArrayList<Object[]> assignees() throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Database.require(c,"Manager","Purchaser");
            return Database.rows(c,"SELECT user_id,CONCAT(full_name,' (',username,')') FROM users ORDER BY full_name");
        }
    }
    public void assign(int inventory, Integer user) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                Database.require(c,"Manager","Purchaser");
                if (Database.update(c,"UPDATE inventory SET assigned_to=? WHERE inventory_id=?",user,inventory) != 1) { throw new IllegalArgumentException("Equipment no longer exists."); }
                Database.audit(c,user == null ? "Unassigned equipment" : "Assigned equipment to user #" + user,"inventory",inventory);
                if (user != null) { Database.notify(c,user,"Equipment #" + inventory + " has been assigned to you."); }
                c.commit();
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }
}
