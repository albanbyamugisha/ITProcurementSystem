// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// JDBC classes open the connection, run the query and read database rows.
// Import Connection for a JDBC connection to the database.
import java.sql.Connection;
// Import PreparedStatement for SQL statements with separate value placeholders.
import java.sql.PreparedStatement;
// Import ResultSet for the rows returned by a database query.
import java.sql.ResultSet;
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;
// ArrayList is a list that grows as Category objects are added.
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;

// DAO means Data Access Object. This class keeps category queries out of the form.
// Define CategoryDAO as a class that groups its related data and methods.
public class CategoryDAO {

    // Return the saved categories. The form handles SQLException if reading fails.
    // Read category IDs and names for the dropdown while preserving their database identities.
    public ArrayList<Category> getAllCategories() throws SQLException {
        // <Category> means this list holds Category objects.
        // Declare categories with type ArrayList<Category>. Create an initially empty resizable list.
        ArrayList<Category> categories = new ArrayList<Category>();

        // Sort by name for the dropdown, then by ID if two names are the same.
        // Declare sql to hold text. Read categories records and ORDER BY fixes their display order.
        // Result columns in order: 0: category_id; 1: category_name. Object[] positions start at zero; JDBC
        // column numbers start at one.
        String sql = "SELECT category_id, category_name FROM categories "
                + "ORDER BY category_name, category_id";

        // try-with-resources closes all three resources, even when an error occurs.
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

            // next() moves to the next row and returns false after the last row.
            // Move to the next database result row and repeat while another row is available.
            while (result.next()) {
                // Turn each database row into an object and add it to the list.
                // Declare category with type Category. Create a Category object using the supplied constructor values.
                Category category = new Category(result.getInt("category_id"),
                        result.getString("category_name"));
                // Append category to categories.
                categories.add(category);
            }
        }

        // An empty table produces an empty list, not a database error.
        // Return categories to the caller.
        return categories;
    }
}
