// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Make the public types in javax.swing available by short names; this does not create objects.
import javax.swing.*;

// A modal dialog keeps this required step attached to LoginFrame.
// It needs only two inputs, so a separate JFrame and another designer form are unnecessary.
// Define PasswordChangeDialog as a class that groups its related data and methods.
public class PasswordChangeDialog {
    // Keep a modal new-password and confirmation dialog open until saving succeeds or the user logs out.
    public static boolean show(java.awt.Component parent) {
        // Declare password with type JPasswordField. Create an input that masks password characters.
        JPasswordField password = new JPasswordField(24);
        // Declare confirmation with type JPasswordField. Create an input that masks password characters.
        JPasswordField confirmation = new JPasswordField(24);
        // Declare fields with type JPanel. Create a container for controls inside an existing window.
        JPanel fields = new JPanel(new java.awt.GridLayout(0,1,4,6));
        // Append `new JLabel("Choose ythe own password before continuing (8-128 characters).")` to fields.
        fields.add(new JLabel("Choose your own password before continuing (8-128 characters)."));
        // Append `new JLabel("New password")` to fields.
        // Append password to fields.
        fields.add(new JLabel("New password")); fields.add(password);
        // Append `new JLabel("Confirm password")` to fields.
        // Append confirmation to fields.
        fields.add(new JLabel("Confirm password")); fields.add(confirmation);
        // Repeat until an explicit return or break ends the operation; this keeps the dialog or test loop
        // active.
        while (true) {
            // Declare choice to hold a whole-number value. Display the supplied custom choices and return the
            // selected option index; closing also produces a non-save result.
            int choice = JOptionPane.showOptionDialog(parent,fields,"Change temporary password",
                    JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,null,
                    new String[]{"Save password","Cancel and log out"},"Save password");
            // Continue with this branch when (choice is not equal to 0).
            if (choice != 0) {
                // Set the text displayed by password to empty text.
                // Set the text displayed by confirmation to empty text.
                // Clear all remembered login details so no previous account remains signed in.
                // Return false to the caller.
                password.setText(""); confirmation.setText(""); Session.clear(); return false;
            }
            // Declare first to hold a mutable array of characters. Read password as a character array that can be
            // cleared after use.
            // Declare second to hold a mutable array of characters. Read confirmation as a character array that
            // can be cleared after use.
            char[] first = password.getPassword(); char[] second = confirmation.getPassword();
            // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
            try {
                // Require a valid temporary-password session and matching new passwords before saving the replacement
                // and refreshing the session.
                new PasswordChangeDAO().change(new String(first),new String(second));
                // Return true to the caller.
                return true;
            // Handle java.sql.SQLException | IllegalArgumentException ex from the preceding try block so the
            // failure follows the recovery steps below.
            } catch (java.sql.SQLException | IllegalArgumentException ex) {
                // Show the caught error as a message instead of allowing the event action to fail silently.
                FormSupport.error(parent,ex);
            } finally {
                // Clear both visible inputs and temporary character arrays after every attempt.
                // Replace table rows through its model, keeping the existing column headings and allowing one selected
                // row.
                java.util.Arrays.fill(first,'\0'); java.util.Arrays.fill(second,'\0');
                // Set the text displayed by password to empty text.
                // Set the text displayed by confirmation to empty text.
                password.setText(""); confirmation.setText("");
            }
        }
    }
}
