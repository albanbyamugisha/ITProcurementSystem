// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// JDBC reads requests and their items from MySQL.
// Import Connection for a JDBC connection to the database.
import java.sql.Connection;
// Import PreparedStatement for SQL statements with separate value placeholders.
import java.sql.PreparedStatement;
// Import ResultSet for the rows returned by a database query.
import java.sql.ResultSet;
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;

// Keep quotation queries separate from the form's controls.
// Define QuotationDAO as a class that groups its related data and methods.
public class QuotationDAO {
    // Quotation entry belongs to the purchaser role.
    // Reject a missing session or a remembered role that is not Purchaser before quotation work begins.
    private void checkPurchaser() {
        // Check the session identity before continuing so a missing or different account follows this branch.
        if (Session.getUserId() <= 0 || !"Purchaser".equals(Session.getRole())) {
            // Stop this operation with an exception: Please log in as a purchaser.
            throw new IllegalArgumentException("Please log in as a purchaser.");
        }
    }

    // Save the quotation, all its prices and the request status as one transaction.
    // Save an internal supplier quotation and all its item prices together after validating the request.
    public int saveQuotation(int requestId, int vendorId, String specs,
            ArrayList<QuotationItem> items) throws SQLException {
        // Reject a missing session or a remembered role that is not Purchaser before quotation work begins.
        checkPurchaser();
        // Continue with this branch when (items equals no object (null) or items is empty or vendorId is at
        // most 0).
        if (items == null || items.isEmpty() || vendorId <= 0) {
            // Stop this operation with an exception: Load items and select a vendor first.
            throw new IllegalArgumentException("Load items and select a vendor first.");
        }
        // Declare total with type java.math.BigDecimal. Create an exact decimal value from the supplied number
        // or text.
        java.math.BigDecimal total = new java.math.BigDecimal("0.00");
        // Declare ids with type java.util.HashSet<Integer>. Create a set for tracking unique values.
        java.util.HashSet<Integer> ids = new java.util.HashSet<Integer>();
        // A set rejects duplicate item IDs; each requested item must appear once.
        // Process each entry in items in turn, referring to the current entry as item.
        for (QuotationItem item : items) {
            // Continue with this branch when (item equals no object (null) or item.getUnitPrice() equals no object
            // (null) or not ids.add(item.getRequestItemId())).
            if (item == null || item.getUnitPrice() == null || !ids.add(item.getRequestItemId())) {
                // Stop this operation with an exception: Set a price for every item; duplicate items are not allowed.
                throw new IllegalArgumentException("Set a price for every item; duplicate items are not allowed.");
            }
            // Set total from the following operation: add the supplied amount to total and return the new
            // BigDecimal value.
            total = total.add(item.getLineTotal());
        }
        // Continue with this branch when (total.compareTo(new java.math.BigDecimal("9999999999.99")) is
        // greater than 0).
        if (total.compareTo(new java.math.BigDecimal("9999999999.99")) > 0) {
            // Stop this operation with an exception: The quotation total exceeds the database amount limit.
            throw new IllegalArgumentException("The quotation total exceeds the database amount limit.");
        }
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Begin a transaction so later database changes are saved together rather than one statement at a
            // time.
            c.setAutoCommit(false);
            // Recheck the database session and allow only the listed role to continue.
            Database.require(c, "Purchaser");
            // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
            try {
                // Recheck the role in MySQL in case the account changed after login.
                // Open the declared resources for this block; try-with-resources closes them in reverse order even if
                // an error occurs.
                // Declare st with type PreparedStatement. Read users records; WHERE limits the rows to the stated
                // conditions; question marks receive separately bound values.
                // Result columns in order: 0: role. Object[] positions start at zero; JDBC column numbers start at
                // one.
                try (PreparedStatement st = c.prepareStatement("SELECT role FROM users WHERE user_id=?")) {
                    // Bind the whole-number value the signed-in account ID to SQL placeholder 1.
                    st.setInt(1, Session.getUserId());
                    // Open the declared resources for this block; try-with-resources closes them in reverse order even if
                    // an error occurs.
                    // Declare r with type ResultSet. Execute the prepared SELECT and return its result rows for reading.
                    try (ResultSet r = st.executeQuery()) {
                        // Check whether the result contains a row before reading its columns or generated ID.
                        if (!r.next() || !"Purchaser".equals(r.getString(1))) {
                            // Stop this operation with an exception: A purchaser account is required.
                            throw new SQLException("A purchaser account is required.");
                        }
                    }
                }
                // Lock the parent request until commit so another decision cannot race this save.
                // Open the declared resources for this block; try-with-resources closes them in reverse order even if
                // an error occurs.
                // Declare st with type PreparedStatement. Read requests records; WHERE limits the rows to the stated
                // conditions; FOR UPDATE locks the selected rows until the transaction ends; question marks receive
                // separately bound values.
                // Result columns in order: 0: request_status. Object[] positions start at zero; JDBC column numbers
                // start at one.
                try (PreparedStatement st = c.prepareStatement("SELECT request_status FROM requests WHERE request_id=? FOR UPDATE")) {
                    // Bind the whole-number value requestId to SQL placeholder 1.
                    st.setInt(1, requestId);
                    // Open the declared resources for this block; try-with-resources closes them in reverse order even if
                    // an error occurs.
                    // Declare r with type ResultSet. Execute the prepared SELECT and return its result rows for reading.
                    try (ResultSet r = st.executeQuery()) {
                        // Check whether the result contains a row before reading its columns or generated ID.
                        if (!r.next() || !("Pending".equals(r.getString(1)) || "Quoted".equals(r.getString(1)) || "CustomerAccepted".equals(r.getString(1)))) {
                            // Stop this operation with an exception: This request no longer accepts quotations.
                            throw new SQLException("This request no longer accepts quotations.");
                        }
                    }
                }
                // Verify that every item still belongs to this request with unchanged details.
                // Declare count to hold a whole-number value. Its initial value is 0.
                int count = 0;
                // Open the declared resources for this block; try-with-resources closes them in reverse order even if
                // an error occurs.
                // Declare st with type PreparedStatement. Read request_items records; WHERE limits the rows to the
                // stated conditions; FOR UPDATE locks the selected rows until the transaction ends; question marks
                // receive separately bound values.
                // Result columns in order: 0: request_item_id; 1: item_description; 2: quantity. Object[] positions
                // start at zero; JDBC column numbers start at one.
                try (PreparedStatement st = c.prepareStatement("SELECT request_item_id, item_description, quantity FROM request_items WHERE request_id=? FOR UPDATE")) {
                    // Bind the whole-number value requestId to SQL placeholder 1.
                    st.setInt(1, requestId);
                    // Open the declared resources for this block; try-with-resources closes them in reverse order even if
                    // an error occurs.
                    // Declare r with type ResultSet. Execute the prepared SELECT and return its result rows for reading.
                    try (ResultSet r = st.executeQuery()) {
                        // Move to the next database result row and repeat while another row is available.
                        while (r.next()) {
                            // Increase count by one after this item or successful check.
                            count++;
                            // Declare found to hold a true-or-false flag. Its initial value is false.
                            boolean found = false;
                            // Process each entry in items in turn, referring to the current entry as item.
                            for (QuotationItem item : items) {
                                // Continue with this branch when (item.getRequestItemId() equals r.getInt(1) and
                                // item.getDescription().equals(r.getString(2)) and item.getQuantity() equals r.getInt(3)).
                                if (item.getRequestItemId() == r.getInt(1)
                                        && item.getDescription().equals(r.getString(2))
                                        // Store true in found for the remaining steps.
                                        && item.getQuantity() == r.getInt(3)) { found = true; }
                            }
                            // Continue with this branch when (!found).
                            // Stop this operation with an exception: Request items changed. Reload them first.
                            if (!found) { throw new SQLException("Request items changed. Reload them first."); }
                        }
                    }
                }
                // Continue with this branch when (count is not equal to the number of entries in items).
                // Stop this operation with an exception: Request items do not match.
                if (count != items.size()) { throw new SQLException("Request items do not match."); }
                // Declare quotationId to hold a whole-number value.
                int quotationId;
                // Open the declared resources for this block; try-with-resources closes them in reverse order even if
                // an error occurs.
                // Declare st with type PreparedStatement. Insert a record into quotations; question marks receive
                // separately bound values.
                try (PreparedStatement st = c.prepareStatement(
                        "INSERT INTO quotations (request_id,vendor_id,quoted_amount,specs,quotation_status) VALUES (?,?,?,?,'Submitted')",
                        java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    // Bind the whole-number value requestId to SQL placeholder 1.
                    st.setInt(1, requestId);
                    // Bind the whole-number value vendorId to SQL placeholder 2.
                    st.setInt(2, vendorId);
                    // Bind total as an exact decimal amount to SQL placeholder 3.
                    st.setBigDecimal(3, total);
                    // Bind specs as text to SQL placeholder 4.
                    st.setString(4, specs);
                    // Execute the prepared database change and report how many rows were affected.
                    st.executeUpdate();
                    // Open the declared resources for this block; try-with-resources closes them in reverse order even if
                    // an error occurs.
                    // Declare keys with type ResultSet. Read the generated ID values from the most recent INSERT on this
                    // statement.
                    try (ResultSet keys = st.getGeneratedKeys()) {
                        // Check whether the result contains a row before reading its columns or generated ID.
                        // Stop this operation with an exception: Quotation ID was not returned.
                        if (!keys.next()) { throw new SQLException("Quotation ID was not returned."); }
                        // Set quotationId from the following operation: read database column 1 as an int; JDBC returns zero
                        // for SQL NULL, which can be checked with wasNull().
                        quotationId = keys.getInt(1);
                    }
                }
                // Link each supplier price to the original requested item.
                // Open the declared resources for this block; try-with-resources closes them in reverse order even if
                // an error occurs.
                // Declare st with type PreparedStatement. Insert a record into quotation_items; question marks receive
                // separately bound values.
                try (PreparedStatement st = c.prepareStatement("INSERT INTO quotation_items (quotation_id,request_item_id,item_description,unit_price,quantity) VALUES (?,?,?,?,?)")) {
                    // Process each entry in items in turn, referring to the current entry as item.
                    for (QuotationItem item : items) {
                        // Bind the whole-number value quotationId to SQL placeholder 1.
                        st.setInt(1, quotationId);
                        // Bind the whole-number value `item.getRequestItemId()` to SQL placeholder 2.
                        st.setInt(2, item.getRequestItemId());
                        // Bind `item.getDescription()` as text to SQL placeholder 3.
                        st.setString(3, item.getDescription());
                        // Bind `item.getUnitPrice()` as an exact decimal amount to SQL placeholder 4.
                        st.setBigDecimal(4, item.getUnitPrice());
                        // Bind the whole-number value `item.getQuantity()` to SQL placeholder 5.
                        st.setInt(5, item.getQuantity());
                        // Execute the prepared database change and report how many rows were affected.
                        st.executeUpdate();
                    }
                }
                // Open the declared resources for this block; try-with-resources closes them in reverse order even if
                // an error occurs.
                // Declare st with type PreparedStatement. Update requests values only for rows matching the WHERE
                // condition; question marks receive separately bound values.
                try (PreparedStatement st = c.prepareStatement("UPDATE requests SET request_status='Quoted' WHERE request_id=? AND request_status='Pending'")) {
                    // Bind the whole-number value requestId to SQL placeholder 1.
                    st.setInt(1, requestId);
                    // Execute the prepared database change and report how many rows were affected.
                    st.executeUpdate();
                }
                // Process each entry in Database.rows(c, "SELECT user_id FROM users WHERE role='Manager'") in turn,
                // referring to the current entry as manager.
                // Result columns in order: 0: user_id. Object[] positions start at zero; JDBC column numbers start at
                // one.
                for (Object[] manager : Database.rows(c,"SELECT user_id FROM users WHERE role='Manager'")) {
                    // Save the notification message for the specified account in the current transaction.
                    Database.notify(c,Database.id(manager[0]),"An internal supplier quotation is available for request #" + requestId + ".");
                }
                // Add an audit entry identifying the current user, action and affected record in this transaction.
                Database.audit(c,"Recorded quotation for request #" + requestId,"quotations",quotationId);
                // Commit the transaction, making all its successful changes permanent together.
                c.commit();
                // Return quotationId to the caller.
                return quotationId;
            // Handle SQLException | RuntimeException ex from the preceding try block so the failure follows the
            // recovery steps below.
            } catch (SQLException | RuntimeException ex) {
                // Undo all changes if any item or the status update fails.
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Roll back the transaction so its unfinished database changes are not kept.
                // Handle SQLException rollbackError from the preceding try block so the failure follows the recovery
                // steps below.
                // Attach the secondary cleanup error to the original exception so neither failure is lost.
                try { c.rollback(); } catch (SQLException rollbackError) { ex.addSuppressed(rollbackError); }
                // Pass the caught exception back to the caller so the original failure is not treated as success.
                throw ex;
            }
        }
    }

    // Only requests still awaiting a decision can receive quotations.
    // List requests still eligible for supplier quotations, after checking Purchaser access in the
    // database.
    public ArrayList<Integer> getOpenRequestIds() throws SQLException {
        // Reject a missing session or a remembered role that is not Purchaser before quotation work begins.
        checkPurchaser();
        // Declare ids with type ArrayList<Integer>. Create an initially empty resizable list.
        ArrayList<Integer> ids = new ArrayList<Integer>();
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare connection with type Connection. Open a JDBC connection; the test URL property can select an
        // isolated database instead of the normal one.
        try (Connection connection = DBConnection.getConnection()) {
            // Check the saved role as well as the remembered session role.
            // Recheck the database session and allow only the listed role to continue.
            Database.require(connection,"Purchaser");
            // Process each entry in Database.rows(connection, "SELECT request_id FROM requests WHERE
            // request_status IN ('Pending','Quoted','CustomerAccepted') ORDER BY request_id DESC") in turn,
            // referring to the current entry as row.
            for (Object[] row : Database.rows(connection,
                    // Result columns in order: 0: request_id. Object[] positions start at zero; JDBC column numbers start
                    // at one.
                    "SELECT request_id FROM requests WHERE request_status IN ('Pending','Quoted','CustomerAccepted') ORDER BY request_id DESC")) {
                // Append `Database.id(row[0])` to ids.
                ids.add(Database.id(row[0]));
            }
        }
        // Return ids to the caller.
        return ids;
    }

    // Load original descriptions and quantities, leaving quoted prices unset.
    // Read the selected request's descriptions and quantities into quotation items with prices initially
    // unset.
    public ArrayList<QuotationItem> getRequestItems(int requestId) throws SQLException {
        // Reject a missing session or a remembered role that is not Purchaser before quotation work begins.
        checkPurchaser();
        // Declare items with type ArrayList<QuotationItem>. Create an initially empty resizable list.
        ArrayList<QuotationItem> items = new ArrayList<QuotationItem>();
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare connection with type Connection. Open a JDBC connection; the test URL property can select an
        // isolated database instead of the normal one.
        try (Connection connection = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed role to continue.
            Database.require(connection,"Purchaser");
            // Declare sql to hold text. Read request_items records with matching information from related tables;
            // WHERE limits the rows to the stated conditions and ORDER BY fixes their display order; question
            // marks receive separately bound values.
            String sql = "SELECT i.request_item_id,i.item_description,i.quantity "
                    + "FROM request_items i JOIN requests r ON r.request_id=i.request_id "
                    + "WHERE r.request_id=? AND r.request_status IN ('Pending','Quoted','CustomerAccepted') ORDER BY i.request_item_id";
            // Process each entry in Database.rows(connection, sql, requestId) in turn, referring to the current
            // entry as row.
            for (Object[] row : Database.rows(connection,sql,requestId)) {
                // Append `new QuotationItem(Database.id(row[0]), row[1].toString(), Database.id(row[2]))` to items.
                items.add(new QuotationItem(Database.id(row[0]),row[1].toString(),Database.id(row[2])));
            }
        }
        // Return items to the caller.
        return items;
    }
}
