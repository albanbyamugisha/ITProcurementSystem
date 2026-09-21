package itprocurementsystem;

// Connection represents the link between our program and the database.
import java.sql.Connection;

// SQLException lets us handle database connection errors.
import java.sql.SQLException;

// JOptionPane displays a small message window for the user.
import javax.swing.JOptionPane;

// This is the starting class of our application.
public class ITProcurementSystem {

    // Java runs this method when we start the project.
    public static void main(String[] args) {

        // Ask our DBConnection class to open a connection to MySQL.
        // This is called try-with-resources: it closes the connection automatically
        // when this block finishes, even if an error occurs.
        try (Connection connection = DBConnection.getConnection()) {

            // Check that the connection responds within three seconds.
            if (!connection.isValid(3)) {
                throw new SQLException("The database connection did not respond.");
            }

        } catch (SQLException ex) {
            // Show useful checks instead of displaying a technical stack trace.
            JOptionPane.showMessageDialog(null,
                    "Could not connect to the database.\n"
                    + "Check that XAMPP MySQL is running on port 3306,\n"
                    + "the it_procurement_db database exists, and\n"
                    + "MySQL Connector/J is added to this project's Libraries.\n"
                    + "Our database username is root and the password is empty.",
                    "Connection Test",
                    JOptionPane.ERROR_MESSAGE);

            // Stop here so we do not show a success message after a failure.
            return;
        }

        // The connection check succeeded. Start with nobody signed in.
        Session.clear();

        // Swing uses an event thread to handle window updates and button clicks.
        // Runnable holds the instructions that this thread should run.
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Run Project now opens the login form instead of only a test message.
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}
