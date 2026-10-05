// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// JDBC classes allow the application to connect to MySQL, run a query and read the returned rows.
// Import Connection for a JDBC connection to the database.
import java.sql.Connection;
// Import PreparedStatement for SQL statements with separate value placeholders.
import java.sql.PreparedStatement;
// Import ResultSet for the rows returned by a database query.
import java.sql.ResultSet;
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;
// ArrayList grows as supplier objects are added.
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;

// DAO means Data Access Object. This class keeps supplier SQL out of the form.
// Define VendorDAO as a class that groups its related data and methods.
public class VendorDAO {

    // Return suppliers for the quotation dropdown; the form will handle database errors.
    // Read supplier IDs and names so the form can display names while saving the correct foreign key.
    public ArrayList<Vendor> getAllVendors() throws SQLException {
        // <Vendor> means that the list contains Vendor objects.
        // Declare vendors with type ArrayList<Vendor>. Create an initially empty resizable list.
        ArrayList<Vendor> vendors = new ArrayList<Vendor>();

        // Alphabetical order makes names easier to find. IDs settle ties between names.
        // Declare sql to hold text. Read vendors records and ORDER BY fixes their display order.
        // Result columns in order: 0: vendor_id; 1: vendor_name. Object[] positions start at zero; JDBC column
        // numbers start at one.
        String sql = "SELECT vendor_id, vendor_name FROM vendors ORDER BY vendor_name, vendor_id";

        // try-with-resources closes these resources when reading finishes or fails.
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare connection with type Connection. Open a JDBC connection; the test URL property can select an
        // isolated database instead of the normal one.
        try (Connection connection = DBConnection.getConnection();
                // Declare statement with type PreparedStatement. Prepare the SQL on this connection; supplied values
                // will be bound separately to its placeholders.
                PreparedStatement statement = connection.prepareStatement(sql);
                // Declare result with type ResultSet. Execute the prepared SELECT and return its result rows for
                // reading.
                ResultSet result = statement.executeQuery()) {
            // next() moves to the next row until no rows remain.
            // Move to the next database result row and repeat while another row is available.
            while (result.next()) {
                // Declare vendor with type Vendor. Create a Vendor object using the supplied constructor values.
                Vendor vendor = new Vendor(result.getInt("vendor_id"),
                        result.getString("vendor_name"));
                // Append vendor to vendors.
                vendors.add(vendor);
            }
        }
        // An empty table produces an empty list. It is not a connection error.
        // Return vendors to the caller.
        return vendors;
    }
}
