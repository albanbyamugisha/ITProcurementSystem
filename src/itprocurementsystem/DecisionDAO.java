// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Make the public types in java.sql available by short names; this does not create objects.
import java.sql.*;
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;

// Customers confirm their saved catalogue order. Managers separately authorise the staff to fulfil it.
// This staff check is the same for individuals and organisations; it is not a customer's department approval.
// Define DecisionDAO as a class that groups its related data and methods.
public class DecisionDAO {
    // Only the owner can see a customer's quotation screen; staff use the review screen.
    // Check role and ownership for one request, optionally locking it to protect a decision transaction.
    private Object[] request(Connection c, int id, boolean staff, boolean lock) throws SQLException {
        // Recheck the database session and allow only the listed role to continue.
        Database.require(c, staff ? "Manager" : "Requester");
        // Declare row to hold one row of values in SELECT or table-column order. Read requests records; WHERE
        // limits the rows to the stated conditions; FOR UPDATE locks the selected rows until the transaction
        // ends; question marks receive separately bound values.
        // Result columns in order: 0: requester_id; 1: request_status; 2: request_type; 3: notes. Object[]
        // positions start at zero; JDBC column numbers start at one.
        Object[] row = Database.one(c, "SELECT requester_id,request_status,request_type,notes FROM requests WHERE request_id=?" + (lock ? " FOR UPDATE" : ""), id);
        // Check the session identity before continuing so a missing or different account follows this branch.
        // Stop this operation with an exception: This request belongs to another customer.
        if (!staff && Database.id(row[0]) != Session.getUserId()) { throw new IllegalArgumentException("This request belongs to another customer."); }
        // Return row to the caller.
        return row;
    }
    // List a customer's own requests or the requests eligible for manager review.
    public ArrayList<Object[]> requests(boolean staff) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed role to continue.
            Database.require(c, staff ? "Manager" : "Requester");
            // Continue with this branch when (staff).
            // Read requests records; WHERE limits the rows to the stated conditions and ORDER BY fixes their
            // display order. Return the resulting value to the caller.
            // Result columns in order: 0: request_id; 1: CONCAT(request_type,' / ',request_status). Object[]
            // positions start at zero; JDBC column numbers start at one.
            if (staff) { return Database.rows(c, "SELECT request_id,CONCAT(request_type,' / ',request_status) FROM requests WHERE request_status IN ('CustomerAccepted','Approved','Rejected','Delivered','Completed') ORDER BY request_id DESC"); }
            // Read requests records; WHERE limits the rows to the stated conditions and ORDER BY fixes their
            // display order; question marks receive separately bound values. Return the resulting value to the
            // caller.
            // Result columns in order: 0: request_id; 1: CONCAT(request_type,' / ',request_status). Object[]
            // positions start at zero; JDBC column numbers start at one.
            return Database.rows(c, "SELECT request_id,CONCAT(request_type,' / ',request_status) FROM requests WHERE requester_id=? ORDER BY request_id DESC",Session.getUserId());
        }
    }
    // Read saved customer order totals for Requesters or internal supplier quotations for Managers.
    public ArrayList<Object[]> quotations(int id, boolean staff) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Check the selected request and the caller's role or ownership before reading related information.
            request(c,id,staff,false);
            // Continue with this branch when (!staff).
            if (!staff) {
                // Keep this screen's existing fthe columns, but return only selling totals.
                // Read requests records and calculate a record count and add the matching amounts or quantities; LEFT
                // JOIN keeps base rows even when related records are missing; WHERE limits the rows to the stated
                // conditions; question marks receive separately bound values. Return the resulting value to the
                // caller.
                // Result columns in order: 0: r.request_id; 1: IF(COUNT(i.catalogue_id)=COUNT(*),'Saved
                // catalogue','Legacy estimate'); 2: SUM(i.quantity*i.estimated_cost); 3:
                // COALESCE(d.decision,r.request_status). Object[] positions start at zero; JDBC column numbers start
                // at one.
                return Database.rows(c,"SELECT r.request_id,IF(COUNT(i.catalogue_id)=COUNT(*),'Saved catalogue','Legacy estimate'),SUM(i.quantity*i.estimated_cost),COALESCE(d.decision,r.request_status) FROM requests r JOIN request_items i ON i.request_id=r.request_id LEFT JOIN order_decisions d ON d.request_id=r.request_id WHERE r.request_id=? GROUP BY r.request_id,r.request_status,d.decision",id);
            }
            // Read quotations records with matching information from related tables; WHERE limits the rows to the
            // stated conditions and ORDER BY fixes their display order; question marks receive separately bound
            // values. Return the resulting value to the caller.
            // Result columns in order: 0: q.quotation_id; 1: v.vendor_name; 2: q.quoted_amount; 3:
            // q.quotation_status. Object[] positions start at zero; JDBC column numbers start at one.
            return Database.rows(c,"SELECT q.quotation_id,v.vendor_name,q.quoted_amount,q.quotation_status FROM quotations q JOIN vendors v ON v.vendor_id=q.vendor_id WHERE q.request_id=? ORDER BY q.quotation_id",id);
        }
    }
    // Read saved selling-price items for the customer or supplier-price items for staff review.
    public ArrayList<Object[]> items(int request, int quote, boolean staff) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Check the selected request and the caller's role or ownership before reading related information.
            request(c,request,staff,false);
            // Continue with this branch when (!staff).
            if (!staff) {
                // Read request_items records; WHERE limits the rows to the stated conditions and ORDER BY fixes their
                // display order; question marks receive separately bound values. Return the resulting value to the
                // caller.
                // Result columns in order: 0: item_description; 1: quantity; 2: estimated_cost; 3:
                // quantity*estimated_cost. Object[] positions start at zero; JDBC column numbers start at one.
                return Database.rows(c,"SELECT item_description,quantity,estimated_cost,quantity*estimated_cost FROM request_items WHERE request_id=? ORDER BY request_item_id",request);
            }
            // Read quotation_items records with matching information from related tables; WHERE limits the rows to
            // the stated conditions; question marks receive separately bound values. Return the resulting value to
            // the caller.
            // Result columns in order: 0: i.item_description; 1: i.quantity; 2: i.unit_price; 3:
            // i.quantity*i.unit_price. Object[] positions start at zero; JDBC column numbers start at one.
            return Database.rows(c,"SELECT i.item_description,i.quantity,i.unit_price,i.quantity*i.unit_price FROM quotation_items i JOIN quotations q ON q.quotation_id=i.quotation_id WHERE q.request_id=? AND q.quotation_id=?",request,quote);
        }
    }
    // Build readable request and decision details, exposing supplier information only on the staff path.
    public String details(int id, int quote, boolean staff) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Declare r to hold one row of values in SELECT or table-column order. Check the selected request and
            // the caller's role or ownership before reading related information.
            Object[] r = request(c,id,staff,false);
            // Declare text to hold text. Its initial value is `"Request #" + id + " / " + r[2] + " / " + r[1] +
            // "\nNotes: " + (r[3] == null ? "" : r[3])`.
            String text = "Request #" + id + " / " + r[2] + " / " + r[1] + "\nNotes: " + (r[3] == null ? "" : r[3]);
            // Declare order with type ArrayList<Object[]>. Read order_decisions records; WHERE limits the rows to
            // the stated conditions; question marks receive separately bound values.
            // Result columns in order: 0: decision; 1: comments. Object[] positions start at zero; JDBC column
            // numbers start at one.
            ArrayList<Object[]> order = Database.rows(c,"SELECT decision,comments FROM order_decisions WHERE request_id=?",id);
            // Continue with this branch when (not order is empty).
            // Append this request, quotation or decision detail to the text already being assembled.
            if (!order.isEmpty()) { text += "\nOrder decision: " + order.get(0)[0] + "\nComments: " + order.get(0)[1]; }
            // Continue with this branch when (!staff).
            if (!staff) {
                // Append this request, quotation or decision detail to the text already being assembled.
                text += "\nAmounts are saved customer prices in UGX. This is not a payment receipt.";
                // Continue with this branch when (not Database.rows(c, "SELECT request_item_id FROM request_items
                // WHERE request_id=? AND catalogue_id IS NULL", id).isEmpty()).
                // Result columns in order: 0: request_item_id. Object[] positions start at zero; JDBC column numbers
                // start at one.
                if (!Database.rows(c,"SELECT request_item_id FROM request_items WHERE request_id=? AND catalogue_id IS NULL",id).isEmpty()) {
                    // Append this request, quotation or decision detail to the text already being assembled.
                    text += "\nLegacy request: amounts are the original estimates, not repriced catalogue items.";
                }
                // Return text to the caller.
                return text;
            }
            // Continue with this branch when (quote is greater than 0).
            if (quote > 0) {
                // Declare q to hold one row of values in SELECT or table-column order. Read quotations records; WHERE
                // limits the rows to the stated conditions; question marks receive separately bound values.
                // Result columns in order: 0: specs. Object[] positions start at zero; JDBC column numbers start at
                // one.
                Object[] q = Database.one(c,"SELECT specs FROM quotations WHERE quotation_id=? AND request_id=?",quote,id);
                // Append this request, quotation or decision detail to the text already being assembled.
                text += "\nQuotation notes: " + (q[0] == null ? "" : q[0]);
                // Declare decision with type ArrayList<Object[]>. Read customer_decisions records; WHERE limits the
                // rows to the stated conditions; question marks receive separately bound values.
                // Result columns in order: 0: decision; 1: comments. Object[] positions start at zero; JDBC column
                // numbers start at one.
                ArrayList<Object[]> decision = Database.rows(c,"SELECT decision,comments FROM customer_decisions WHERE quotation_id=?",quote);
                // Continue with this branch when (not decision is empty).
                if (!decision.isEmpty()) {
                    // Append this request, quotation or decision detail to the text already being assembled.
                    text += "\nCustomer decision: " + decision.get(0)[0];
                    // Append this request, quotation or decision detail to the text already being assembled.
                    text += "\nCustomer comments: " + (decision.get(0)[1] == null ? "" : decision.get(0)[1]);
                }
            }
            // Declare selected with type ArrayList<Object[]>. Read request_selections records; WHERE limits the
            // rows to the stated conditions; question marks receive separately bound values.
            // Result columns in order: 0: quotation_id. Object[] positions start at zero; JDBC column numbers
            // start at one.
            ArrayList<Object[]> selected = Database.rows(c,"SELECT quotation_id FROM request_selections WHERE request_id=?",id);
            // Continue with this branch when (not selected is empty).
            // Append this request, quotation or decision detail to the text already being assembled.
            if (!selected.isEmpty()) { text += "\nSelected internal supplier quotation #" + selected.get(0)[0]; }
            // Return text to the caller.
            return text;
        }
    }
    // Read the manager-visible approval changes and the staff member responsible for each change.
    public ArrayList<Object[]> history(int id) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Check the selected request and the caller's role or ownership before reading related information.
            request(c,id,true,false);
            // Read approval_history records with matching information from related tables; WHERE limits the rows
            // to the stated conditions and ORDER BY fixes their display order; question marks receive separately
            // bound values. Return the resulting value to the caller.
            // Result columns in order: 0: h.history_id; 1: h.old_status; 2: h.new_status; 3: u.full_name; 4:
            // h.change_date. Object[] positions start at zero; JDBC column numbers start at one.
            return Database.rows(c,"SELECT h.history_id,h.old_status,h.new_status,u.full_name,h.change_date FROM approval_history h JOIN approvals a ON a.approval_id=h.approval_id JOIN users u ON u.user_id=h.changed_by WHERE a.request_id=? ORDER BY h.history_id",id);
        }
    }
    // The old method signature remains for the existing panel; quote is not a supplier choice.
    // Save the owner's acceptance or rejection of a fixed-price catalogue order once only.
    public void customerDecision(int id, int quote, boolean accept, String comments) throws SQLException {
        // Set comments from the following operation: validate comments as "Comments" with a maximum of 4000
        // characters; the final argument controls whether a value is required.
        comments = Database.text(comments,"Comments",4000,false);
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
                // Declare r to hold one row of values in SELECT or table-column order. Check the selected request and
                // the caller's role or ownership before reading related information.
                Object[] r = request(c,id,false,true);
                // Continue with this branch when (!"Pending".equals(r[1]) and !"Quoted".equals(r[1])).
                if (!"Pending".equals(r[1]) && !"Quoted".equals(r[1])) {
                    // Stop this operation with an exception: This order already has a decision.
                    throw new IllegalArgumentException("This order already has a decision.");
                }
                // Continue with this branch when (not Database.rows(c, "SELECT request_item_id FROM request_items
                // WHERE request_id=? AND catalogue_id IS NULL", id).isEmpty()).
                // Result columns in order: 0: request_item_id. Object[] positions start at zero; JDBC column numbers
                // start at one.
                if (!Database.rows(c,"SELECT request_item_id FROM request_items WHERE request_id=? AND catalogue_id IS NULL",id).isEmpty()) {
                    // Stop this operation with an exception: This is a legacy estimate. Create a new catalogue request to
                    // confirm fixed prices.
                    throw new IllegalArgumentException("This is a legacy estimate. Create a new catalogue request to confirm fixed prices.");
                }
                // Declare decision to hold text. Its initial value is `accept ? "Accepted" : "Declined"`.
                String decision = accept ? "Accepted" : "Declined";
                // Insert a record into order_decisions; question marks receive separately bound values.
                Database.update(c,"INSERT INTO order_decisions(request_id,customer_id,decision,comments) VALUES (?,?,?,?)",id,Session.getUserId(),decision,comments);
                // Update requests values only for rows matching the WHERE condition; question marks receive separately
                // bound values.
                Database.update(c,"UPDATE requests SET request_status=? WHERE request_id=?",accept ? "CustomerAccepted" : "Declined",id);
                // Process each entry in Database.rows(c, "SELECT user_id FROM users WHERE role='Manager'") in turn,
                // referring to the current entry as manager.
                // Result columns in order: 0: user_id. Object[] positions start at zero; JDBC column numbers start at
                // one.
                for (Object[] manager : Database.rows(c,"SELECT user_id FROM users WHERE role='Manager'")) {
                    // Save the notification message for the specified account in the current transaction.
                    Database.notify(c,Database.id(manager[0]),"Order #" + id + " was " + decision.toLowerCase() + " by its customer.");
                }
                // Add an audit entry identifying the current user, action and affected record in this transaction.
                Database.audit(c,decision + " catalogue order","requests",id);
                // Commit the transaction, making all its successful changes permanent together.
                c.commit();
            // Handle SQLException | RuntimeException ex from the preceding try block so the failure follows the
            // recovery steps below.
            // Roll back the transaction so its unfinished database changes are not kept.
            // Pass the caught exception back to the caller so the original failure is not treated as success.
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }
    // The manager chooses the internal supplier; the saved customer price stays unchanged.
    // Save a manager's decision and internal supplier selection without changing the customer's saved
    // selling price.
    public void review(int id, int quote, boolean approve, String comments) throws SQLException {
        // Set comments from the following operation: validate comments as "Review comments" with a maximum of
        // 4000 characters; the final argument controls whether a value is required.
        comments = Database.text(comments,"Review comments",4000,!approve);
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
                // Declare r to hold one row of values in SELECT or table-column order. Check the selected request and
                // the caller's role or ownership before reading related information.
                Object[] r = request(c,id,true,true);
                // Continue with this branch when (!"CustomerAccepted".equals(r[1])).
                // Stop this operation with an exception: Only a customer-accepted request can be reviewed once.
                if (!"CustomerAccepted".equals(r[1])) { throw new IllegalArgumentException("Only a customer-accepted request can be reviewed once."); }
                // Declare oldSelection with type ArrayList<Object[]>. Read request_selections records; WHERE limits
                // the rows to the stated conditions; question marks receive separately bound values.
                // Result columns in order: 0: quotation_id. Object[] positions start at zero; JDBC column numbers
                // start at one.
                ArrayList<Object[]> oldSelection = Database.rows(c,"SELECT quotation_id FROM request_selections WHERE request_id=?",id);
                // Continue with this branch when (not oldSelection is empty).
                if (!oldSelection.isEmpty()) {
                    // Preserve the supplier already selected by the earlier application version.
                    // Continue with this branch when (Database.id(oldSelection.get(0)[0]) is not equal to quote).
                    // Stop this operation with an exception: Choose the saved legacy supplier quotation.
                    if (Database.id(oldSelection.get(0)[0]) != quote) { throw new IllegalArgumentException("Choose the saved legacy supplier quotation."); }
                } else {
                    // Declare supplier to hold one row of values in SELECT or table-column order. Read quotations records;
                    // WHERE limits the rows to the stated conditions; FOR UPDATE locks the selected rows until the
                    // transaction ends; question marks receive separately bound values.
                    // Result columns in order: 0: quotation_status. Object[] positions start at zero; JDBC column numbers
                    // start at one.
                    Object[] supplier = Database.one(c,"SELECT quotation_status FROM quotations WHERE request_id=? AND quotation_id=? FOR UPDATE",id,quote);
                    // Continue with this branch when (!"Submitted".equals(supplier[0])).
                    // Stop this operation with an exception: Choose a submitted supplier quotation.
                    if (!"Submitted".equals(supplier[0])) { throw new IllegalArgumentException("Choose a submitted supplier quotation."); }
                    // Continue with this branch when (approve).
                    if (approve) {
                        // Insert a record into request_selections; question marks receive separately bound values.
                        Database.update(c,"INSERT INTO request_selections(request_id,quotation_id) VALUES (?,?)",id,quote);
                        // Update quotations values only for rows matching the WHERE condition; question marks receive
                        // separately bound values.
                        Database.update(c,"UPDATE quotations SET quotation_status=IF(quotation_id=?,'Accepted','Not Selected') WHERE request_id=?",quote,id);
                    }
                }
                // Declare status to hold text. Its initial value is `approve ? "Approved" : "Rejected"`.
                String status = approve ? "Approved" : "Rejected";
                // Declare approval to hold a whole-number value. Insert a record into approvals; question marks
                // receive separately bound values.
                int approval = Database.insert(c,"INSERT INTO approvals(request_id,manager_id,approval_status,comments,approval_date) VALUES (?,?,?,?,NOW())",id,Session.getUserId(),status,comments);
                // Insert a record into approval_history; question marks receive separately bound values.
                Database.update(c,"INSERT INTO approval_history(approval_id,old_status,new_status,changed_by) VALUES (?,'Pending',?,?)",approval,status,Session.getUserId());
                // Update requests values only for rows matching the WHERE condition; question marks receive separately
                // bound values.
                Database.update(c,"UPDATE requests SET request_status=? WHERE request_id=?",status,id);
                // Save the notification message for the specified account in the current transaction.
                Database.notify(c,Database.id(r[0]),"Request #" + id + " was " + status.toLowerCase() + (comments.isEmpty() ? "." : ". Review: " + comments.substring(0,Math.min(160,comments.length()))));
                // Continue with this branch when (approve).
                if (approve) {
                    // Process each entry in Database.rows(c, "SELECT user_id FROM users WHERE role='Purchaser'") in turn,
                    // referring to the current entry as purchaser.
                    // Result columns in order: 0: user_id. Object[] positions start at zero; JDBC column numbers start at
                    // one.
                    for (Object[] purchaser : Database.rows(c,"SELECT user_id FROM users WHERE role='Purchaser'")) {
                        // Save the notification message for the specified account in the current transaction.
                        Database.notify(c,Database.id(purchaser[0]),"Request #" + id + " is approved for " + r[2] + " fulfilment.");
                    }
                }
                // Add an audit entry identifying the current user, action and affected record in this transaction.
                Database.audit(c,status + " quotation #" + quote,"requests",id);
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
