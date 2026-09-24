package itprocurementsystem;

// JDBC classes let us connect to MySQL, run a query and read the returned rows.
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// ArrayList grows as we add supplier objects to it.
import java.util.ArrayList;

// DAO means Data Access Object. This class keeps supplier SQL out of the form.
public class VendorDAO {

    // Return suppliers for the quotation dropdown; the form will handle database errors.
    public ArrayList<Vendor> getAllVendors() throws SQLException {
        // <Vendor> means that the list contains Vendor objects.
        ArrayList<Vendor> vendors = new ArrayList<Vendor>();

        // Alphabetical order makes names easier to find. IDs settle ties between names.
        String sql = "SELECT vendor_id, vendor_name FROM vendors ORDER BY vendor_name, vendor_id";

        // try-with-resources closes these resources when reading finishes or fails.
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {
            // next() moves to the next row until no rows remain.
            while (result.next()) {
                Vendor vendor = new Vendor(result.getInt("vendor_id"),
                        result.getString("vendor_name"));
                vendors.add(vendor);
            }
        }
        // An empty table produces an empty list. It is not a connection error.
        return vendors;
    }
}
