package itprocurementsystem;

// JDBC reads requests and their items from MySQL.
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// Keep quotation queries separate from the form's controls.
public class QuotationDAO {
    // Quotation entry belongs to the purchaser role.
    private void checkPurchaser() {
        if (Session.getUserId() <= 0 || !"Purchaser".equals(Session.getRole())) {
            throw new IllegalArgumentException("Please log in as a purchaser.");
        }
    }

    // Save the quotation, all its prices and the request status as one transaction.
    public int saveQuotation(int requestId, int vendorId, String specs,
            ArrayList<QuotationItem> items) throws SQLException {
        checkPurchaser();
        if (items == null || items.isEmpty() || vendorId <= 0) {
            throw new IllegalArgumentException("Load items and select a vendor first.");
        }
        java.math.BigDecimal total = new java.math.BigDecimal("0.00");
        java.util.HashSet<Integer> ids = new java.util.HashSet<Integer>();
        // A set rejects duplicate item IDs; each requested item must appear once.
        for (QuotationItem item : items) {
            if (item == null || item.getUnitPrice() == null || !ids.add(item.getRequestItemId())) {
                throw new IllegalArgumentException("Set a price for every item; duplicate items are not allowed.");
            }
            total = total.add(item.getLineTotal());
        }
        if (total.compareTo(new java.math.BigDecimal("9999999999.99")) > 0) {
            throw new IllegalArgumentException("The quotation total exceeds the database amount limit.");
        }
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                // Recheck the role in MySQL in case the account changed after login.
                try (PreparedStatement st = c.prepareStatement("SELECT role FROM users WHERE user_id=?")) {
                    st.setInt(1, Session.getUserId());
                    try (ResultSet r = st.executeQuery()) {
                        if (!r.next() || !"Purchaser".equals(r.getString(1))) {
                            throw new SQLException("A purchaser account is required.");
                        }
                    }
                }
                // Lock the parent request until commit so another decision cannot race this save.
                try (PreparedStatement st = c.prepareStatement("SELECT request_status FROM requests WHERE request_id=? FOR UPDATE")) {
                    st.setInt(1, requestId);
                    try (ResultSet r = st.executeQuery()) {
                        if (!r.next() || !("Pending".equals(r.getString(1)) || "Quoted".equals(r.getString(1)))) {
                            throw new SQLException("This request no longer accepts quotations.");
                        }
                    }
                }
                // Verify that every item still belongs to this request with unchanged details.
                int count = 0;
                try (PreparedStatement st = c.prepareStatement("SELECT request_item_id, item_description, quantity FROM request_items WHERE request_id=? FOR UPDATE")) {
                    st.setInt(1, requestId);
                    try (ResultSet r = st.executeQuery()) {
                        while (r.next()) {
                            count++;
                            boolean found = false;
                            for (QuotationItem item : items) {
                                if (item.getRequestItemId() == r.getInt(1)
                                        && item.getDescription().equals(r.getString(2))
                                        && item.getQuantity() == r.getInt(3)) { found = true; }
                            }
                            if (!found) { throw new SQLException("Request items changed. Reload them first."); }
                        }
                    }
                }
                if (count != items.size()) { throw new SQLException("Request items do not match."); }
                int quotationId;
                try (PreparedStatement st = c.prepareStatement(
                        "INSERT INTO quotations (request_id,vendor_id,quoted_amount,specs,quotation_status) VALUES (?,?,?,?,'Submitted')",
                        java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    st.setInt(1, requestId);
                    st.setInt(2, vendorId);
                    st.setBigDecimal(3, total);
                    st.setString(4, specs);
                    st.executeUpdate();
                    try (ResultSet keys = st.getGeneratedKeys()) {
                        if (!keys.next()) { throw new SQLException("Quotation ID was not returned."); }
                        quotationId = keys.getInt(1);
                    }
                }
                // Link each supplier price to the original requested item.
                try (PreparedStatement st = c.prepareStatement("INSERT INTO quotation_items (quotation_id,request_item_id,item_description,unit_price,quantity) VALUES (?,?,?,?,?)")) {
                    for (QuotationItem item : items) {
                        st.setInt(1, quotationId);
                        st.setInt(2, item.getRequestItemId());
                        st.setString(3, item.getDescription());
                        st.setBigDecimal(4, item.getUnitPrice());
                        st.setInt(5, item.getQuantity());
                        st.executeUpdate();
                    }
                }
                try (PreparedStatement st = c.prepareStatement("UPDATE requests SET request_status='Quoted' WHERE request_id=?")) {
                    st.setInt(1, requestId);
                    st.executeUpdate();
                }
                c.commit();
                return quotationId;
            } catch (SQLException | RuntimeException ex) {
                // Undo all changes if any item or the status update fails.
                try { c.rollback(); } catch (SQLException rollbackError) { ex.addSuppressed(rollbackError); }
                throw ex;
            }
        }
    }

    // Only requests still awaiting a decision can receive quotations.
    public ArrayList<Integer> getOpenRequestIds() throws SQLException {
        checkPurchaser();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        String sql = "SELECT request_id FROM requests WHERE request_status IN ('Pending', 'Quoted') ORDER BY request_id DESC";
        // Resources close automatically after all rows have been read.
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {
            while (result.next()) { ids.add(result.getInt("request_id")); }
        }
        return ids;
    }

    // Load original descriptions and quantities, leaving quoted prices unset.
    public ArrayList<QuotationItem> getRequestItems(int requestId) throws SQLException {
        checkPurchaser();
        ArrayList<QuotationItem> items = new ArrayList<QuotationItem>();
        // Check the status again because it may have changed since the dropdown loaded.
        String sql = "SELECT i.request_item_id, i.item_description, i.quantity "
                + "FROM request_items i JOIN requests r ON r.request_id=i.request_id "
                + "WHERE r.request_id=? AND r.request_status IN ('Pending', 'Quoted') ORDER BY i.request_item_id";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, requestId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    items.add(new QuotationItem(result.getInt("request_item_id"),
                            result.getString("item_description"), result.getInt("quantity")));
                }
            }
        }
        return items;
    }
}
