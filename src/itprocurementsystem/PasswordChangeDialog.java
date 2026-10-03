package itprocurementsystem;

import javax.swing.*;

// A modal dialog keeps this required step attached to LoginFrame.
// It needs only two inputs, so a separate JFrame and another designer form are unnecessary.
public class PasswordChangeDialog {
    public static boolean show(java.awt.Component parent) {
        JPasswordField password = new JPasswordField(24);
        JPasswordField confirmation = new JPasswordField(24);
        JPanel fields = new JPanel(new java.awt.GridLayout(0,1,4,6));
        fields.add(new JLabel("Choose your own password before continuing (8-128 characters)."));
        fields.add(new JLabel("New password")); fields.add(password);
        fields.add(new JLabel("Confirm password")); fields.add(confirmation);
        while (true) {
            int choice = JOptionPane.showOptionDialog(parent,fields,"Change temporary password",
                    JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,null,
                    new String[]{"Save password","Cancel and log out"},"Save password");
            if (choice != 0) {
                password.setText(""); confirmation.setText(""); Session.clear(); return false;
            }
            char[] first = password.getPassword(); char[] second = confirmation.getPassword();
            try {
                new PasswordChangeDAO().change(new String(first),new String(second));
                return true;
            } catch (java.sql.SQLException | IllegalArgumentException ex) {
                FormSupport.error(parent,ex);
            } finally {
                // Clear both visible inputs and temporary character arrays after every attempt.
                java.util.Arrays.fill(first,'\0'); java.util.Arrays.fill(second,'\0');
                password.setText(""); confirmation.setText("");
            }
        }
    }
}
