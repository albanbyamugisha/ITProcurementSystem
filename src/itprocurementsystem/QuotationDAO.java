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
