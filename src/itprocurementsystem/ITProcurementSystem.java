// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Reading the code: braces { } group a class, method, branch or loop; a semicolon ends a statement.
// A dot accesses a field or calls a method on an object or class. Parentheses contain arguments.
// Square brackets select an array position, starting at zero. Quoted text is a String value.
// public allows access from other classes; private keeps a member within its declaring class.
// static belongs to the class rather than one object; final prevents a variable from being reassigned.
// new constructs an object. this refers to the current object; null means no object is assigned.
// In a condition, && means both checks must pass, || means either may pass, and ! reverses a result.
// In condition ? first : second, Java chooses first when the condition is true and second otherwise.
// Comments describe executable statements and declarations; closing braces simply end their matching blocks.

// Connection represents the link between the program and the database.
// Import Connection for a JDBC connection to the database.
import java.sql.Connection;

// SQLException allows the application to handle database connection errors.
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;

// JOptionPane displays a small message window for the user.
// Import JOptionPane for standard Swing message, confirmation and input dialogs.
import javax.swing.JOptionPane;

// This is the starting class of the application.
// Define ITProcurementSystem as a class that groups its related data and methods.
public class ITProcurementSystem {

    // Java runs this method when Java starts the project.
    // Provide the entry point used when this Java class is launched directly.
    public static void main(String[] args) {

        // Ask the DBConnection class to open a connection to MySQL.
        // This is called try-with-resources: it closes the connection automatically
        // when this block finishes, even if an error occurs.
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare connection with type Connection. Open a JDBC connection; the test URL property can select an
        // isolated database instead of the normal one.
        try (Connection connection = DBConnection.getConnection()) {

            // Create the new workflow tables if this is the first run after updating.
            // Add missing columns and supporting tables, then seed missing catalogue entries while retaining
            // existing records.
            DatabaseSetup.ensureWorkflowTables(connection);

            // Check that the connection responds within three seconds.
            // Continue with this branch when (not connection.isValid(3)).
            if (!connection.isValid(3)) {
                // Stop this operation with an exception: The database connection did not respond.
                throw new SQLException("The database connection did not respond.");
            }

        // Handle SQLException ex from the preceding try block so the failure follows the recovery steps below.
        } catch (SQLException ex) {
            // Show useful checks instead of displaying a technical stack trace.
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(null,
                    "Could not prepare the database.\n"
                    + "Check that XAMPP MySQL is running on port 3306,\n"
                    + "the it_procurement_db database exists, and\n"
                    + "MySQL Connector/J is added to this project's Libraries.\n"
                    + "Our database username is root and the password is empty.\n"
                    + "Details: " + ex.getMessage(),
                    "Connection Test",
                    JOptionPane.ERROR_MESSAGE);

            // Stop here so the application does not show a success message after a failure.
            // Stop this method here; no further statements in this call are executed.
            return;
        }

        // The connection check succeeded. Start with nobody signed in.
        // Clear all remembered login details so no previous account remains signed in.
        Session.clear();

        // Swing uses an event thread to handle window updates and button clicks.
        // Runnable holds the instructions that this thread should run.
        // Schedule this work on Swing's event dispatch thread so window updates run on the UI thread.
        java.awt.EventQueue.invokeLater(new Runnable() {
            // Execute the work supplied to this Runnable when its caller or event queue schedules it.
            // Ask the compiler to verify that this method overrides an inherited method.
            @Override
            public void run() {
                // Run Project now opens the login form instead of only a test message.
                // Declare loginFrame with type LoginFrame. Create a LoginFrame object using the supplied constructor
                // values.
                LoginFrame loginFrame = new LoginFrame();
                // Set whether loginFrame is shown using true.
                loginFrame.setVisible(true);
            }
        });
    }
}
