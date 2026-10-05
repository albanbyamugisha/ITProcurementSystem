// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

/**
 * A JFrame gives password recovery its own window before a user logs in.
 * @author alban-byamugisha
 */
// Define ForgotPasswordFrame as a JFrame: it owns a top-level window with a title bar and window
// controls.
public class ForgotPasswordFrame extends javax.swing.JFrame {
    
    // Only this class accesses this field directly. Declare logger with type java.util.logging.Logger. Its
    // initial value is `java.util.logging.Logger.getLogger(ForgotPasswordFrame.class.getName())`. This
    // field is shared by all instances of the class. The reference or value cannot be reassigned after
    // initialisation.
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ForgotPasswordFrame.class.getName());

    /**
     * Creates new form ForgotPasswordFrame
     */
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public ForgotPasswordFrame() {
        // Create the controls and apply the layout stored by NetBeans before reading or changing any of those
        // controls.
        initComponents();
        // Choose what the window close button does: EXIT_ON_CLOSE stops the application; DISPOSE_ON_CLOSE
        // closes only this window.
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        // Centre this window on the screen; null means there is no parent window to centre against.
        setLocationRelativeTo(null);
        // Set whether jTextFieldNewPassword allows direct typing using false; its value can still be changed
        // by code.
        jTextFieldNewPassword.setEditable(false);
        // Set the text displayed by jTextFieldNewPassword to empty text.
        jTextFieldNewPassword.setText("");
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonResetPassword.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Read the recovery fields, request a temporary password and display it only after the reset is saved.
            public void actionPerformed(java.awt.event.ActionEvent event) { resetPassword(); }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonBackToLogin.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Open the login window and dispose of this window so only the intended entry screen remains open.
            public void actionPerformed(java.awt.event.ActionEvent event) { backToLogin(); }
        });
        // Register the window-close callback so closing the form follows its return-to-login behaviour.
        addWindowListener(new java.awt.event.WindowAdapter() {
            // Handle the window close request using the same navigation or cleanup as the form's return action.
            // Open the login window and dispose of this window so only the intended entry screen remains open.
            public void windowClosing(java.awt.event.WindowEvent event) { backToLogin(); }
        });
    }

    // Display a replacement only after its hash has been saved successfully.
    // Read the recovery fields, request a temporary password and display it only after the reset is saved.
    private void resetPassword() {
        // Set the text displayed by jTextFieldNewPassword to empty text.
        jTextFieldNewPassword.setText("");
        // Set the text displayed by jLabelStatus to "Checking account details...".
        jLabelStatus.setText("Checking account details...");
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Declare replacement to hold text. Match the registered username and full name, then save a random
            // temporary password hash and expire previous sessions.
            String replacement = new PasswordResetDAO().reset(jTextFieldUsername.getText(), jTextFieldFullName.getText());
            // Set the text displayed by jTextFieldNewPassword to replacement.
            jTextFieldNewPassword.setText(replacement);
            // Set the text displayed by jLabelStatus to "Temporary password ready. Log in to change it.".
            jLabelStatus.setText("Temporary password ready. Log in to change it.");
        // Handle java.sql.SQLException | IllegalArgumentException ex from the preceding try block so the
        // failure follows the recovery steps below.
        } catch (java.sql.SQLException | IllegalArgumentException ex) {
            // Set the text displayed by jLabelStatus to "Password was not reset.".
            jLabelStatus.setText("Password was not reset.");
            // Show the caught error as a message instead of allowing the event action to fail silently.
            FormSupport.error(this, ex);
        }
    }

    // Clear the visible replacement when leaving this window.
    // Open the login window and dispose of this window so only the intended entry screen remains open.
    private void backToLogin() {
        // Set the text displayed by jTextFieldNewPassword to empty text.
        jTextFieldNewPassword.setText("");
        // Set whether new LoginFrame() is shown using true.
        new LoginFrame().setVisible(true);
        // Close this window and release its native resources without clearing other application windows.
        dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * Horizontal groups control widths and left/right positions; vertical groups control heights.
     * LEADING aligns starts, TRAILING aligns ends, and BASELINE aligns text baselines.
     * PREFERRED_SIZE uses the component's preferred size; DEFAULT_SIZE asks the layout for a default.
     * Short.MAX_VALUE permits a large flexible size; it is not the actual window size.
     * Comments within the generated method may be replaced when Design view regenerates it.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // Create and arrange the controls described by the matching NetBeans .form file. Design view
    // regenerates this method.
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        // Set jTextFieldUsername from the following operation: create a single-line text input.
        jTextFieldUsername = new javax.swing.JTextField();
        // Set jTextFieldFullName from the following operation: create a single-line text input.
        jTextFieldFullName = new javax.swing.JTextField();
        // Set jLabelUserName from the following operation: create a non-editable text or image display.
        jLabelUserName = new javax.swing.JLabel();
        // Set jLabelFullName from the following operation: create a non-editable text or image display.
        jLabelFullName = new javax.swing.JLabel();
        // Set jButtonResetPassword from the following operation: create a clickable action button.
        jButtonResetPassword = new javax.swing.JButton();
        // Set jTextFieldNewPassword from the following operation: create a single-line text input.
        jTextFieldNewPassword = new javax.swing.JTextField();
        // Set jLabelNewPassword from the following operation: create a non-editable text or image display.
        jLabelNewPassword = new javax.swing.JLabel();
        // Set jButtonBackToLogin from the following operation: create a clickable action button.
        jButtonBackToLogin = new javax.swing.JButton();
        // Set jLabeltitle from the following operation: create a non-editable text or image display.
        jLabeltitle = new javax.swing.JLabel();
        // Set jLabelStatus from the following operation: create a non-editable text or image display.
        jLabelStatus = new javax.swing.JLabel();

        // Choose what the window close button does: EXIT_ON_CLOSE stops the application; DISPOSE_ON_CLOSE
        // closes only this window.
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // Set the text displayed by jLabelUserName to "Enter ythe registered username:".
        jLabelUserName.setText("Enter your registered username:");

        // Set the text displayed by jLabelFullName to "Enter ythe registered full name".
        jLabelFullName.setText("Enter your registered full name");

        // Set the text displayed by jButtonResetPassword to "Reset Password".
        jButtonResetPassword.setText("Reset Password");

        // Set whether jTextFieldNewPassword allows direct typing using false; its value can still be changed
        // by code.
        jTextFieldNewPassword.setEditable(false);

        // Set the text displayed by jLabelNewPassword to "New Password".
        jLabelNewPassword.setText("New Password");

        // Set the text displayed by jButtonBackToLogin to "Back to Login".
        jButtonBackToLogin.setText("Back to Login");

        // Set the text displayed by jLabeltitle to "RESET PASSWORD".
        jLabeltitle.setText("RESET PASSWORD");

        // Set the text displayed by jLabelStatus to "Enter ythe registered username and full name.".
        jLabelStatus.setText("Enter your registered username and full name.");

        // Declare layout with type javax.swing.GroupLayout. Create a layout manager describing horizontal and
        // vertical arrangements separately.
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        // Assign the supplied layout manager to getContentPane() to control child-component positions and
        // sizes.
        getContentPane().setLayout(layout);
        // Define the left-to-right arrangement and widths; the vertical group separately controls heights.
        layout.setHorizontalGroup(
            // Start a parallel group whose children occupy the same region on this axis, using the selected
            // alignment.
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave spacing between the group and its enclosing container edge.
                        .addContainerGap()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            // Place jLabelUserName in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelUserName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            // Place jLabelFullName in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelFullName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            // Place jLabelNewPassword in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jLabelNewPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Place jTextFieldFullName in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jTextFieldFullName)
                            // Place jTextFieldNewPassword in this layout group; any following size arguments give minimum,
                            // preferred and maximum sizes.
                            .addComponent(jTextFieldNewPassword)
                            // Place jTextFieldUsername in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jTextFieldUsername, javax.swing.GroupLayout.Alignment.TRAILING)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Place jButtonResetPassword in this layout group; any following size arguments give minimum,
                                // preferred and maximum sizes.
                                .addComponent(jButtonResetPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(0, 0, Short.MAX_VALUE))
                            // Place jButtonBackToLogin in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jButtonBackToLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(123, 123, 123)
                                // Place jLabeltitle in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jLabeltitle, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(41, 41, 41)
                                // Place jLabelStatus in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jLabelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(0, 0, Short.MAX_VALUE)))
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap())
        );
        // Define the top-to-bottom arrangement and heights; the horizontal group separately controls widths.
        layout.setVerticalGroup(
            // Start a parallel group whose children occupy the same region on this axis, using the selected
            // alignment.
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(9, 9, 9)
                // Place jLabeltitle in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabeltitle, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jTextFieldUsername in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jTextFieldUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jLabelUserName in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelUserName))
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(30, 30, 30)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    // Place jLabelFullName in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelFullName)
                    // Place jTextFieldFullName in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jTextFieldFullName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(18, 18, 18)
                // Place jButtonResetPassword in this layout group; any following size arguments give minimum,
                // preferred and maximum sizes.
                .addComponent(jButtonResetPassword)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    // Place jLabelNewPassword in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jLabelNewPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    // Place jTextFieldNewPassword in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jTextFieldNewPassword))
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(18, 18, 18)
                // Place jLabelStatus in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelStatus)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                // Place jButtonBackToLogin in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jButtonBackToLogin)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(18, 18, 18))
        );

        // Calculate the window size from the preferred sizes of its controls and layout.
        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    // Provide the entry point used when this Java class is launched directly.
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Process each entry in javax.swing.UIManager.getInstalledLookAndFeels() in turn, referring to the
            // current entry as info.
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                // Continue with this branch when ("Nimbus".equals(info.getName())).
                if ("Nimbus".equals(info.getName())) {
                    // Apply the chosen installed Swing appearance to subsequently created controls.
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    // Leave the current loop once the required result has been found.
                    break;
                }
            }
        // Handle ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex from the
        // preceding try block so the failure follows the recovery steps below.
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            // Record the exception at SEVERE level so a look-and-feel setup failure can be diagnosed.
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        // Schedule this work on Swing's event dispatch thread so window updates run on the UI thread.
        java.awt.EventQueue.invokeLater(() -> new ForgotPasswordFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Only this class accesses this field directly. Declare jButtonBackToLogin with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonBackToLogin;
    // Only this class accesses this field directly. Declare jButtonResetPassword with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonResetPassword;
    // Only this class accesses this field directly. Declare jLabelFullName with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelFullName;
    // Only this class accesses this field directly. Declare jLabelStatus with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelStatus;
    // Only this class accesses this field directly. Declare jLabelNewPassword with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelNewPassword;
    // Only this class accesses this field directly. Declare jLabelUserName with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelUserName;
    // Only this class accesses this field directly. Declare jLabeltitle with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabeltitle;
    // Only this class accesses this field directly. Declare jTextFieldFullName with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldFullName;
    // Only this class accesses this field directly. Declare jTextFieldNewPassword with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldNewPassword;
    // Only this class accesses this field directly. Declare jTextFieldUsername with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldUsername;
    // End of variables declaration//GEN-END:variables
}
