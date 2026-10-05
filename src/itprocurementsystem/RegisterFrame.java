// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

/**
 * A JFrame is used because someone must register before they can enter MainFrame.
 * This form opens as its own window from the login screen.
 * Extending JFrame gives it a title bar and the normal window controls.
 *
 * @author alban-byamugisha
 */
// Define RegisterFrame as a JFrame: it owns a top-level window with a title bar and window controls.
public class RegisterFrame extends javax.swing.JFrame {
    // Keep department IDs while displaying only their names.
    // Only this class accesses this field directly. Declare departments with type
    // java.util.ArrayList<RegistrationDAO.Department>. Java initially uses null for this object reference.
    private java.util.ArrayList<RegistrationDAO.Department> departments;

    
    // Only this class accesses this field directly. Declare logger with type java.util.logging.Logger. Its
    // initial value is `java.util.logging.Logger.getLogger(RegisterFrame.class.getName())`. This field is
    // shared by all instances of the class. The reference or value cannot be reassigned after
    // initialisation.
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RegisterFrame.class.getName());

    /**
     * Creates new form RegisterFrame
     */
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public RegisterFrame() {
        // Build the controls arranged in NetBeans Design view.
        // Create the controls and apply the layout stored by NetBeans before reading or changing any of those
        // controls.
        initComponents();
        // Remove designer sample passwords before the user starts typing.
        // Set the text displayed by jPasswordFieldPassword to empty text.
        jPasswordFieldPassword.setText("");
        // Set the text displayed by jPasswordFieldConfirm to empty text.
        jPasswordFieldConfirm.setText("");
        // Always start with an instruction, never an automatically chosen account type.
        // Give jComboBoxAccountType the supplied model, which holds its choices or table data.
        jComboBoxAccountType.setModel(new javax.swing.DefaultComboBoxModel<String>(
                new String[] {"Select account type", "Individual customer", "Organisation user"}));
        // Set the text displayed by jLabelRoleInfo to "Create an account to request IT equipment and
        // services.".
        jLabelRoleInfo.setText("Create an account to request IT equipment and services.");
        // Load optional department choices while keeping their IDs aligned with displayed names.
        loadDepartments();
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jComboBoxAccountType.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Ask the compiler to verify that this method overrides an inherited method.
            @Override
            // Enable organisation-only fields for an organisation account and require an explicit account-type
            // choice.
            public void actionPerformed(java.awt.event.ActionEvent event) { updateAccountFields(); }
        });
        // Enable organisation-only fields for an organisation account and require an explicit account-type
        // choice.
        updateAccountFields();
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonCreateAccount.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Ask the compiler to verify that this method overrides an inherited method.
            @Override
            // Read registration fields, save through the DAO and automatically log in after successful account
            // creation.
            public void actionPerformed(java.awt.event.ActionEvent event) { createAccount(); }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonBackToLogin.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Ask the compiler to verify that this method overrides an inherited method.
            @Override
            // Open the login window and dispose of this window so only the intended entry screen remains open.
            public void actionPerformed(java.awt.event.ActionEvent event) { backToLogin(); }
        });
        // The window's close button returns to login just like Back to Login.
        // Register the window-close callback so closing the form follows its return-to-login behaviour.
        addWindowListener(new java.awt.event.WindowAdapter() {
            // Handle the window close request using the same navigation or cleanup as the form's return action.
            // Ask the compiler to verify that this method overrides an inherited method.
            @Override
            // Open the login window and dispose of this window so only the intended entry screen remains open.
            public void windowClosing(java.awt.event.WindowEvent event) { backToLogin(); }
        });
        // Center the window after its size has been calculated.
        // Centre this window on the screen; null means there is no parent window to centre against.
        setLocationRelativeTo(null);
        // Closing registration should not stop the whole application.
        // Choose what the window close button does: EXIT_ON_CLOSE stops the application; DISPOSE_ON_CLOSE
        // closes only this window.
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }

    // Organisation details are used only when the user explicitly chooses that type.
    // Enable organisation-only fields for an organisation account and require an explicit account-type
    // choice.
    private void updateAccountFields() {
        // Declare organisation to hold a true-or-false flag. Its initial value is
        // `jComboBoxAccountType.getSelectedIndex() == 2`.
        boolean organisation = jComboBoxAccountType.getSelectedIndex() == 2;
        // Set whether jTextFieldOrgaisationName accepts input or actions using organisation; false disables
        // it.
        jTextFieldOrgaisationName.setEnabled(organisation);
        // Set whether jLabelOrganisationName accepts input or actions using organisation; false disables it.
        jLabelOrganisationName.setEnabled(organisation);
        // Set the text displayed by jLabelOrganisationName to `"Organisation name" + (organisation ? " *" :
        // "")`.
        jLabelOrganisationName.setText("Organisation name" + (organisation ? " *" : ""));
        // Set whether jComboBoxDepartment accepts input or actions using `organisation && departments != null
        // && !departments.isEmpty()`; false disables it.
        jComboBoxDepartment.setEnabled(organisation && departments != null && !departments.isEmpty());
        // Set whether jLabelDepartment accepts input or actions using organisation; false disables it.
        jLabelDepartment.setEnabled(organisation);
        // Continue with this branch when (!organisation).
        if (!organisation) {
            // Clear stale organisation values when switching to an individual account.
            // Set the text displayed by jTextFieldOrgaisationName to empty text.
            jTextFieldOrgaisationName.setText("");
            // Select position 0 in jComboBoxDepartment; dropdown positions start at zero.
            jComboBoxDepartment.setSelectedIndex(0);
        }
        // The instruction at index zero is not a valid registration choice.
        // Set whether jButtonCreateAccount accepts input or actions using
        // `jComboBoxAccountType.getSelectedIndex() > 0`; false disables it.
        jButtonCreateAccount.setEnabled(jComboBoxAccountType.getSelectedIndex() > 0);
    }

    // Load the optional department choices from MySQL and report any loading failure.
    // Load optional department choices while keeping their IDs aligned with displayed names.
    private void loadDepartments() {
        // Remove the existing choices from jComboBoxDepartment before adding a fresh list.
        jComboBoxDepartment.removeAllItems();
        // Append "No department (optional)" as a selectable entry in jComboBoxDepartment.
        jComboBoxDepartment.addItem("No department (optional)");
        // Set whether jButtonCreateAccount accepts input or actions using false; false disables it.
        jButtonCreateAccount.setEnabled(false);
        // Declare saved to hold a true-or-false flag. Its initial value is false.
        boolean saved = false;
        // Clear all remembered login details so no previous account remains signed in.
        Session.clear();
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Set departments from the following operation: read department IDs and names in alphabetical order
            // for account registration.
            departments = new RegistrationDAO().getDepartments();
            // Repeat while i is less than the number of entries in departments; initialise the counter once and
            // update it after each pass.
            for (int i = 0; i < departments.size(); i++) {
                // Append `departments.get(i).getName()` as a selectable entry in jComboBoxDepartment.
                jComboBoxDepartment.addItem(departments.get(i).getName());
            }

            // An empty list is allowed: departments are optional for all customers.
        // Handle java.sql.SQLException ex from the preceding try block so the failure follows the recovery
        // steps below.
        } catch (java.sql.SQLException ex) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Could not load departments. Start MySQL, return to login and reopen Create Account.");
        }
    }

    // The form reads the controls; the DAO validates and saves the account.
    // Read registration fields, save through the DAO and automatically log in after successful account
    // creation.
    private void createAccount() {
        // Declare accountIndex to hold a whole-number value. Read the selected zero-based position in
        // jComboBoxAccountType; minus one means no selection.
        int accountIndex = jComboBoxAccountType.getSelectedIndex();
        // Check again here even though the button is disabled for the initial prompt.
        // Continue with this branch when (accountIndex is not equal to 1 and accountIndex is not equal to 2).
        if (accountIndex != 1 && accountIndex != 2) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            javax.swing.JOptionPane.showMessageDialog(this, "Please select an account type.");
            // Stop this method here; no further statements in this call are executed.
            return;
        }
        // Declare accountType to hold text. Its initial value is `accountIndex == 1 ? "Individual" :
        // "Organisation"`.
        String accountType = accountIndex == 1 ? "Individual" : "Organisation";
        // Declare index to hold a whole-number value. Its initial value is
        // `jComboBoxDepartment.getSelectedIndex() - 1`.
        int index = jComboBoxDepartment.getSelectedIndex() - 1;
        // Declare departmentId to hold a whole-number value. The following expression supplies its initial
        // value.
        int departmentId = index >= 0 && departments != null && index < departments.size()
                ? departments.get(index).getId() : 0;
        // Declare password to hold a mutable array of characters. Read jPasswordFieldPassword as a character
        // array that can be cleared after use.
        char[] password = jPasswordFieldPassword.getPassword();
        // Declare confirmation to hold a mutable array of characters. Read jPasswordFieldConfirm as a
        // character array that can be cleared after use.
        char[] confirmation = jPasswordFieldConfirm.getPassword();
        // Set whether jButtonCreateAccount accepts input or actions using false; false disables it.
        jButtonCreateAccount.setEnabled(false);
        // Declare saved to hold a true-or-false flag. Its initial value is false.
        boolean saved = false;
        // Clear all remembered login details so no previous account remains signed in.
        Session.clear();
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Validate account details and create a Requester account; public registration cannot choose a staff
            // role.
            new RegistrationDAO().register(jTextFieldFullName.getText(), jTextFieldUsername.getText(),
                    jTextFieldEmail.getText(), departmentId, new String(password), new String(confirmation),
                    accountType, jTextFieldOrgaisationName.getText(),
                    jComboBoxGender.getSelectedIndex() <= 0 ? null : (String) jComboBoxGender.getSelectedItem());
            // Store true in saved for the remaining steps.
            saved = true;
            // Use the normal password check so only a verified account opens MainFrame.
            // Continue with this branch when (!new UserDAO().checkLogin(jTextFieldUsername.getText(), new
            // String(password))).
            if (!new UserDAO().checkLogin(jTextFieldUsername.getText(), new String(password))) {
                // Stop this operation with an exception: Automatic login failed.
                throw new IllegalStateException("Automatic login failed.");
            }
            // Set whether new MainFrame() is shown using true.
            new MainFrame().setVisible(true);
            // Close this window and release its native resources without clearing other application windows.
            dispose();
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        } catch (IllegalArgumentException ex) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage());
        // Handle java.sql.SQLException | IllegalStateException ex from the preceding try block so the failure
        // follows the recovery steps below.
        } catch (java.sql.SQLException | IllegalStateException ex) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            javax.swing.JOptionPane.showMessageDialog(this,
                    saved ? "Your account was saved, but automatic login failed. Return to Login to sign in."
                    : "Could not confirm account creation. Try logging in before registering again.");
        } finally {
            // Clear temporary character arrays and visible password fields after every attempt.
            // Replace table rows through its model, keeping the existing column headings and allowing one selected
            // row.
            java.util.Arrays.fill(password, '\0');
            // Replace table rows through its model, keeping the existing column headings and allowing one selected
            // row.
            java.util.Arrays.fill(confirmation, '\0');
            // Set the text displayed by jPasswordFieldPassword to empty text.
            jPasswordFieldPassword.setText("");
            // Set the text displayed by jPasswordFieldConfirm to empty text.
            jPasswordFieldConfirm.setText("");
            // Enable organisation-only fields for an organisation account and require an explicit account-type
            // choice.
            updateAccountFields();
        }
    }

    // Open a fresh login window and close only this registration window.
    // Open the login window and dispose of this window so only the intended entry screen remains open.
    private void backToLogin() {
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

        // Set jLabelTitle from the following operation: create a non-editable text or image display.
        jLabelTitle = new javax.swing.JLabel();
        // Set jLabelFullName from the following operation: create a non-editable text or image display.
        jLabelFullName = new javax.swing.JLabel();
        // Set jLabelUserName from the following operation: create a non-editable text or image display.
        jLabelUserName = new javax.swing.JLabel();
        // Set jLabelEmail from the following operation: create a non-editable text or image display.
        jLabelEmail = new javax.swing.JLabel();
        // Set jLabelDepartment from the following operation: create a non-editable text or image display.
        jLabelDepartment = new javax.swing.JLabel();
        // Set jLabelPassword from the following operation: create a non-editable text or image display.
        jLabelPassword = new javax.swing.JLabel();
        // Set JLabelConfirmPassword from the following operation: create a non-editable text or image display.
        JLabelConfirmPassword = new javax.swing.JLabel();
        // Set jLabelRoleInfo from the following operation: create a non-editable text or image display.
        jLabelRoleInfo = new javax.swing.JLabel();
        // Set jButtonBackToLogin from the following operation: create a clickable action button.
        jButtonBackToLogin = new javax.swing.JButton();
        // Set jButtonCreateAccount from the following operation: create a clickable action button.
        jButtonCreateAccount = new javax.swing.JButton();
        // Set jComboBoxDepartment from the following operation: create a dropdown selection control.
        jComboBoxDepartment = new javax.swing.JComboBox<>();
        // Set jTextFieldFullName from the following operation: create a single-line text input.
        jTextFieldFullName = new javax.swing.JTextField();
        // Set jTextFieldUsername from the following operation: create a single-line text input.
        jTextFieldUsername = new javax.swing.JTextField();
        // Set jTextFieldEmail from the following operation: create a single-line text input.
        jTextFieldEmail = new javax.swing.JTextField();
        // Set jPasswordFieldPassword from the following operation: create an input that masks password
        // characters.
        jPasswordFieldPassword = new javax.swing.JPasswordField();
        // Set jPasswordFieldConfirm from the following operation: create an input that masks password
        // characters.
        jPasswordFieldConfirm = new javax.swing.JPasswordField();
        // Set jLabelAccountType from the following operation: create a non-editable text or image display.
        jLabelAccountType = new javax.swing.JLabel();
        // Set jComboBoxAccountType from the following operation: create a dropdown selection control.
        jComboBoxAccountType = new javax.swing.JComboBox<>();
        // Set jLabelOrganisationName from the following operation: create a non-editable text or image
        // display.
        jLabelOrganisationName = new javax.swing.JLabel();
        // Set jTextFieldOrgaisationName from the following operation: create a single-line text input.
        jTextFieldOrgaisationName = new javax.swing.JTextField();
        // Set jLabelGender from the following operation: create a non-editable text or image display.
        jLabelGender = new javax.swing.JLabel();
        // Set jComboBoxGender from the following operation: create a dropdown selection control.
        jComboBoxGender = new javax.swing.JComboBox<>();

        // Choose what the window close button does: EXIT_ON_CLOSE stops the application; DISPOSE_ON_CLOSE
        // closes only this window.
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // Set the text displayed by jLabelTitle to "Create Account".
        jLabelTitle.setText("Create Account");

        // Set the text displayed by jLabelFullName to "Full Name:".
        jLabelFullName.setText("Full Name:");

        // Set the text displayed by jLabelUserName to "User Name:".
        jLabelUserName.setText("User Name:");

        // Set the text displayed by jLabelEmail to "Email:".
        jLabelEmail.setText("Email:");

        // Set the text displayed by jLabelDepartment to "Department:".
        jLabelDepartment.setText("Department:");

        // Set the text displayed by jLabelPassword to "Password:".
        jLabelPassword.setText("Password:");

        // Set the text displayed by JLabelConfirmPassword to "Confirm Password".
        JLabelConfirmPassword.setText("Confirm Password");

        // Set the text displayed by jLabelRoleInfo to "Create an account to request IT equipment and
        // services.".
        jLabelRoleInfo.setText("Create an account to request IT equipment and services.");

        // Set the text displayed by jButtonBackToLogin to "Back to Login".
        jButtonBackToLogin.setText("Back to Login");

        // Set the text displayed by jButtonCreateAccount to "Create Account".
        jButtonCreateAccount.setText("Create Account");

        // Give jComboBoxDepartment the supplied model, which holds its choices or table data.
        jComboBoxDepartment.setModel(new javax.swing.DefaultComboBoxModel<String>(
            new String[] {"No department (optional)"}
        ));
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jComboBoxDepartment.addActionListener(this::jComboBoxDepartmentActionPerformed);

        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jTextFieldEmail.addActionListener(this::jTextFieldEmailActionPerformed);

        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jPasswordFieldPassword.addActionListener(this::jPasswordFieldPasswordActionPerformed);

        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jPasswordFieldConfirm.addActionListener(this::jPasswordFieldConfirmActionPerformed);

        // Set the text displayed by jLabelAccountType to "Account Type:".
        jLabelAccountType.setText("Account Type:");

        // Give jComboBoxAccountType the supplied model, which holds its choices or table data.
        jComboBoxAccountType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select account type", "Individual customer", "Organisation user" }));

        // Set the text displayed by jLabelOrganisationName to "Organisation Name".
        jLabelOrganisationName.setText("Organisation Name");

        // Set the text displayed by jLabelGender to "Gender (optional)".
        jLabelGender.setText("Gender (optional)");

        // Give jComboBoxGender the supplied model, which holds its choices or table data.
        jComboBoxGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select gender (optional)", "Female", "Male", "Other", "Prefer not to say" }));

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(0, 0, Short.MAX_VALUE)
                // Place jLabelRoleInfo in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelRoleInfo)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(37, 37, 37))
            // Nest a sequential group to place controls one after another.
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(23, 23, 23)
                        // Place JLabelConfirmPassword in this layout group; any following size arguments give minimum,
                        // preferred and maximum sizes.
                        .addComponent(JLabelConfirmPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(18, 18, 18)
                        // Place jPasswordFieldConfirm in this layout group; any following size arguments give minimum,
                        // preferred and maximum sizes.
                        .addComponent(jPasswordFieldConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave spacing between the group and its enclosing container edge.
                        .addContainerGap(182, Short.MAX_VALUE)
                        // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jLabelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(136, 136, 136))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(85, 85, 85)
                        // Place jButtonBackToLogin in this layout group; any following size arguments give minimum, preferred
                        // and maximum sizes.
                        .addComponent(jButtonBackToLogin)
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        // Place jButtonCreateAccount in this layout group; any following size arguments give minimum,
                        // preferred and maximum sizes.
                        .addComponent(jButtonCreateAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Place jLabelFullName in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelFullName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            // Place jLabelUserName in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelUserName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Leave spacing between the group and its enclosing container edge.
                                .addContainerGap()
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    // Place jLabelPassword in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jLabelPassword, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    // Place jLabelDepartment in this layout group; any following size arguments give minimum, preferred
                                    // and maximum sizes.
                                    .addComponent(jLabelDepartment, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    // Nest a sequential group to place controls one after another.
                                    .addGroup(layout.createSequentialGroup()
                                        // Nest a parallel group so controls share this horizontal or vertical region.
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            // Place jLabelAccountType in this layout group; any following size arguments give minimum, preferred
                                            // and maximum sizes.
                                            .addComponent(jLabelAccountType)
                                            // Place jLabelGender in this layout group; any following size arguments give minimum, preferred and
                                            // maximum sizes.
                                            .addComponent(jLabelGender, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            // Place jComboBoxDepartment in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jComboBoxDepartment, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            // Place jTextFieldFullName in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jTextFieldFullName, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                            // Place jTextFieldUsername in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jTextFieldUsername)
                            // Place jPasswordFieldPassword in this layout group; any following size arguments give minimum,
                            // preferred and maximum sizes.
                            .addComponent(jPasswordFieldPassword)
                            // Place jComboBoxAccountType in this layout group; any following size arguments give minimum,
                            // preferred and maximum sizes.
                            .addComponent(jComboBoxAccountType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            // Place jTextFieldOrgaisationName in this layout group; any following size arguments give minimum,
                            // preferred and maximum sizes.
                            .addComponent(jTextFieldOrgaisationName)
                            // Place jTextFieldEmail in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jTextFieldEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                            // Place jComboBoxGender in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jComboBoxGender, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(29, 29, 29))
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap()
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Place jLabelOrganisationName in this layout group; any following size arguments give minimum,
                        // preferred and maximum sizes.
                        .addComponent(jLabelOrganisationName, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(0, 0, Short.MAX_VALUE))
                    // Place jLabelEmail in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelTitle)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelFullName in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelFullName)
                    // Place jTextFieldFullName in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jTextFieldFullName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Place jTextFieldUsername in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jTextFieldUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jLabelUserName in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelUserName))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Place jLabelGender in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelGender)
                    // Place jComboBoxGender in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jComboBoxGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelEmail in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelEmail)
                    // Place jTextFieldEmail in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jTextFieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(18, 18, 18)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jComboBoxAccountType in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jComboBoxAccountType, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jLabelAccountType in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jLabelAccountType))
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(18, 18, 18)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelOrganisationName in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jLabelOrganisationName)
                    // Place jTextFieldOrgaisationName in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jTextFieldOrgaisationName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(18, 18, 18)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jComboBoxDepartment in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jComboBoxDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jLabelDepartment in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jLabelDepartment))
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(18, 18, 18)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jPasswordFieldPassword in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jPasswordFieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jLabelPassword in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelPassword))
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(18, 18, 18)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Place jPasswordFieldConfirm in this layout group; any following size arguments give minimum,
                        // preferred and maximum sizes.
                        .addComponent(jPasswordFieldConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        // Place jLabelRoleInfo in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jLabelRoleInfo)
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            // Place jButtonBackToLogin in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jButtonBackToLogin)
                            // Place jButtonCreateAccount in this layout group; any following size arguments give minimum,
                            // preferred and maximum sizes.
                            .addComponent(jButtonCreateAccount)))
                    // Place JLabelConfirmPassword in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(JLabelConfirmPassword))
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(14, 14, 14))
        );

        // Calculate the window size from the preferred sizes of its controls and layout.
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Handle the action event from jTextFieldEmail; any statements below run when that action is raised.
    private void jTextFieldEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldEmailActionPerformed
        // No extra action is needed here; the form connects its buttons in the constructor.
    }//GEN-LAST:event_jTextFieldEmailActionPerformed

    // Handle the action event from jComboBoxDepartment; any statements below run when that action is
    // raised.
    private void jComboBoxDepartmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxDepartmentActionPerformed
        // No extra action is needed here; the form connects its buttons in the constructor.
    }//GEN-LAST:event_jComboBoxDepartmentActionPerformed

    // Handle the action event from jPasswordFieldConfirm; any statements below run when that action is
    // raised.
    private void jPasswordFieldConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordFieldConfirmActionPerformed
        // No extra action is needed here; the form connects its buttons in the constructor.
    }//GEN-LAST:event_jPasswordFieldConfirmActionPerformed

    // Handle the action event from jPasswordFieldPassword; any statements below run when that action is
    // raised.
    private void jPasswordFieldPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordFieldPasswordActionPerformed
        // No extra action is needed here; the form connects its buttons in the constructor.
    }//GEN-LAST:event_jPasswordFieldPasswordActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new RegisterFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Only this class accesses this field directly. Declare JLabelConfirmPassword with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel JLabelConfirmPassword;
    // Only this class accesses this field directly. Declare jButtonBackToLogin with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonBackToLogin;
    // Only this class accesses this field directly. Declare jButtonCreateAccount with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonCreateAccount;
    // Only this class accesses this field directly. Declare jComboBoxAccountType with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxAccountType;
    // Only this class accesses this field directly. Declare jComboBoxDepartment with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxDepartment;
    // Only this class accesses this field directly. Declare jComboBoxGender with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxGender;
    // Only this class accesses this field directly. Declare jLabelAccountType with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelAccountType;
    // Only this class accesses this field directly. Declare jLabelDepartment with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelDepartment;
    // Only this class accesses this field directly. Declare jLabelEmail with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelEmail;
    // Only this class accesses this field directly. Declare jLabelFullName with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelFullName;
    // Only this class accesses this field directly. Declare jLabelGender with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelGender;
    // Only this class accesses this field directly. Declare jLabelOrganisationName with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelOrganisationName;
    // Only this class accesses this field directly. Declare jLabelPassword with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelPassword;
    // Only this class accesses this field directly. Declare jLabelRoleInfo with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelRoleInfo;
    // Only this class accesses this field directly. Declare jLabelTitle with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelTitle;
    // Only this class accesses this field directly. Declare jLabelUserName with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelUserName;
    // Only this class accesses this field directly. Declare jPasswordFieldConfirm with type
    // javax.swing.JPasswordField. Java initially uses null for this object reference.
    private javax.swing.JPasswordField jPasswordFieldConfirm;
    // Only this class accesses this field directly. Declare jPasswordFieldPassword with type
    // javax.swing.JPasswordField. Java initially uses null for this object reference.
    private javax.swing.JPasswordField jPasswordFieldPassword;
    // Only this class accesses this field directly. Declare jTextFieldEmail with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldEmail;
    // Only this class accesses this field directly. Declare jTextFieldFullName with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldFullName;
    // Only this class accesses this field directly. Declare jTextFieldOrgaisationName with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldOrgaisationName;
    // Only this class accesses this field directly. Declare jTextFieldUsername with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldUsername;
    // End of variables declaration//GEN-END:variables
}
