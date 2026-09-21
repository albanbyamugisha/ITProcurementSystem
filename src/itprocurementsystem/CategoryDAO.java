package itprocurementsystem;

// JDBC classes open the connection, run the query and read database rows.
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// ArrayList is a list that grows as we add Category objects.
import java.util.ArrayList;

// DAO means Data Access Object. This class keeps category queries out of our form.
public class CategoryDAO {

    // Return the saved categories. The form handles SQLException if reading fails.
    public ArrayList<Category> getAllCategories() throws SQLException {
        // <Category> means this list holds Category objects.
        ArrayList<Category> categories = new ArrayList<Category>();

        // Sort by name for the dropdown, then by ID if two names are the same.
        String sql = "SELECT category_id, category_name FROM categories "
                + "ORDER BY category_name, category_id";

        // try-with-resources closes all three resources, even when an error occurs.
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {

            // next() moves to the next row and returns false after the last row.
            while (result.next()) {
                // Turn each database row into an object and add it to our list.
                Category category = new Category(result.getInt("category_id"),
                        result.getString("category_name"));
                categories.add(category);
            }
        }

        // An empty table produces an empty list, not a database error.
        return categories;
    }
}
