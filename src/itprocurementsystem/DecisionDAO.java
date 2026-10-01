package itprocurementsystem;

import java.sql.*;
import java.util.ArrayList;

// Customers choose a quotation. Managers separately authorise our staff to fulfil it.
// This staff check is the same for individuals and organisations; it is not a customer's department approval.
public class DecisionDAO {
    // Only the owner can see a customer's quotation screen; staff use the review screen.
    private Object[] request(Connection c, int id, boolean staff, boolean lock) throws SQLException {
        Database.require(c, staff ? "Manager" : "Requester");
        Object[] row = Database.one(c, "SELECT requester_id,request_status,request_type,notes FROM requests WHERE request_id=?" + (lock ? " FOR UPDATE" : ""), id);
        if (!staff && Database.id(row[0]) != Session.getUserId()) { throw new IllegalArgumentException("This request belongs to another customer."); }
        return row;
    }
    public ArrayList<Object[]> requests(boolean staff) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Database.require(c, staff ? "Manager" : "Requester");
            if (staff) { return Database.rows(c, "SELECT request_id,CONCAT(request_type,' / ',request_status) FROM requests WHERE request_status IN ('CustomerAccepted','Approved','Rejected','Delivered','Completed') ORDER BY request_id DESC"); }
            return Database.rows(c, "SELECT request_id,CONCAT(request_type,' / ',request_status) FROM requests WHERE requester_id=? ORDER BY request_id DESC",Session.getUserId());
        }
    }
    public ArrayList<Object[]> quotations(int id, boolean staff) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            request(c,id,staff,false);
            return Database.rows(c,"SELECT q.quotation_id,v.vendor_name,q.quoted_amount,q.quotation_status FROM quotations q JOIN vendors v ON v.vendor_id=q.vendor_id WHERE q.request_id=? ORDER BY q.quotation_id",id);
        }
    }
    public ArrayList<Object[]> items(int request, int quote, boolean staff) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            request(c,request,staff,false);
            return Database.rows(c,"SELECT i.item_description,i.quantity,i.unit_price,i.quantity*i.unit_price FROM quotation_items i JOIN quotations q ON q.quotation_id=i.quotation_id WHERE q.request_id=? AND q.quotation_id=?",request,quote);
        }
    }
    public String details(int id, int quote, boolean staff) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Object[] r = request(c,id,staff,false);
            String text = "Request #" + id + " / " + r[2] + " / " + r[1] + "\nNotes: " + (r[3] == null ? "" : r[3]);
            if (quote > 0) {
                Object[] q = Database.one(c,"SELECT specs FROM quotations WHERE quotation_id=? AND request_id=?",quote,id);
                text += "\nQuotation notes: " + (q[0] == null ? "" : q[0]);
            }
            ArrayList<Object[]> selected = Database.rows(c,"SELECT quotation_id FROM request_selections WHERE request_id=?",id);
            if (!selected.isEmpty()) { text += "\nCustomer chose quotation #" + selected.get(0)[0]; }
            return text;
        }
    }
    public ArrayList<Object[]> history(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            request(c,id,true,false);
            return Database.rows(c,"SELECT h.history_id,h.old_status,h.new_status,u.full_name,h.change_date FROM approval_history h JOIN approvals a ON a.approval_id=h.approval_id JOIN users u ON u.user_id=h.changed_by WHERE a.request_id=? ORDER BY h.history_id",id);
        }
    }
    // Lock the parent request so two browser windows cannot accept different quotations.
    public void customerDecision(int id, int quote, boolean accept, String comments) throws SQLException {
        comments = Database.text(comments,"Comments",4000,false);
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                Object[] r = request(c,id,false,true);
                if (!"Quoted".equals(r[1])) { throw new IllegalArgumentException("This request no longer accepts a customer decision."); }
                Object[] q = Database.one(c,"SELECT quotation_status FROM quotations WHERE quotation_id=? AND request_id=? FOR UPDATE",quote,id);
                if (!"Submitted".equals(q[0])) { throw new IllegalArgumentException("Choose a quotation that is still Submitted."); }
                String decision = accept ? "Accepted" : "Declined";
                Database.update(c,"INSERT INTO customer_decisions(quotation_id,customer_id,decision,comments) VALUES (?,?,?,?)",quote,Session.getUserId(),decision,comments);
                Database.update(c,"UPDATE quotations SET quotation_status=? WHERE quotation_id=?",decision,quote);
                if (accept) {
                    Database.update(c,"INSERT INTO request_selections(request_id,quotation_id) VALUES (?,?)",id,quote);
                    Database.update(c,"UPDATE requests SET request_status='CustomerAccepted' WHERE request_id=?",id);
                    Database.update(c,"UPDATE quotations SET quotation_status='Not Selected' WHERE request_id=? AND quotation_id<>? AND quotation_status='Submitted'",id,quote);
                    // Notify managers so acceptance can be reviewed before fulfilment.
                    for (Object[] manager : Database.rows(c,"SELECT user_id FROM users WHERE role='Manager'")) {
                        Database.notify(c,Database.id(manager[0]),"Request #" + id + " has a customer-selected quotation ready for review.");
                    }
                }
                Database.audit(c,decision + " quotation #" + quote,"requests",id);
                c.commit();
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }
    // The manager can approve only the exact quotation accepted by the customer.
    public void review(int id, int quote, boolean approve, String comments) throws SQLException {
        comments = Database.text(comments,"Review comments",4000,!approve);
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                Object[] r = request(c,id,true,true);
                if (!"CustomerAccepted".equals(r[1])) { throw new IllegalArgumentException("Only a customer-accepted request can be reviewed once."); }
                int selected = Database.id(Database.one(c,"SELECT quotation_id FROM request_selections WHERE request_id=?",id)[0]);
                if (selected != quote) { throw new IllegalArgumentException("Select the customer's accepted quotation."); }
                String status = approve ? "Approved" : "Rejected";
                int approval = Database.insert(c,"INSERT INTO approvals(request_id,manager_id,approval_status,comments,approval_date) VALUES (?,?,?,?,NOW())",id,Session.getUserId(),status,comments);
                Database.update(c,"INSERT INTO approval_history(approval_id,old_status,new_status,changed_by) VALUES (?,'Pending',?,?)",approval,status,Session.getUserId());
                Database.update(c,"UPDATE requests SET request_status=? WHERE request_id=?",status,id);
                Database.notify(c,Database.id(r[0]),"Request #" + id + " was " + status.toLowerCase() + (comments.isEmpty() ? "." : ". Review: " + comments.substring(0,Math.min(160,comments.length()))));
                if (approve) {
                    for (Object[] purchaser : Database.rows(c,"SELECT user_id FROM users WHERE role='Purchaser'")) {
                        Database.notify(c,Database.id(purchaser[0]),"Request #" + id + " is approved for " + r[2] + " fulfilment.");
                    }
                }
                Database.audit(c,status + " quotation #" + quote,"requests",id);
                c.commit();
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }
}
