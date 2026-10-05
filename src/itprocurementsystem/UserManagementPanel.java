// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

/**
 * A JPanel is used because user management belongs inside MainFrame.
 * It groups the related controls without opening another window.
 * MainFrame provides the title bar and navigation for this panel.
 *
 * @author alban-byamugisha
 */
// Define UserManagementPanel as a JPanel: it groups this task's controls inside MainFrame rather than
// opening another window.
public class UserManagementPanel extends javax.swing.JPanel {
    // The DAO handles validation and SQL; the form handles display.
    // Only this class accesses this field directly. Declare management with type ManagementDAO. Create a
    // ManagementDAO object using the supplied constructor values. The reference or value cannot be
    // reassigned after initialisation.
    private final ManagementDAO management = new ManagementDAO();


    /**
     * Creates new form UserManagementPanel
     */
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public UserManagementPanel() {
        // Build the controls and layout saved in NetBeans Design view.
        // Create the controls and apply the layout stored by NetBeans before reading or changing any of those
        // controls.
        initComponents();
        // These buttons open authorised PDF previews; the PDF viewer provides Save As or Save a Copy.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonUsersPdf.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Build the authorised report and open its temporary PDF preview; saving a copy is handled by the PDF
                // viewer.
                try { ReportActions.export(UserManagementPanel.this,"users",0); }
                // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
                // steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (IllegalArgumentException ex) { FormSupport.error(UserManagementPanel.this,ex); }
            }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonSelectedUserPdf.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Build the authorised report and open its temporary PDF preview; saving a copy is handled by the PDF
                // viewer.
                try { ReportActions.export(UserManagementPanel.this,"selected",FormSupport.selectedId(jTableUsers)); }
                // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
                // steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (IllegalArgumentException ex) { FormSupport.error(UserManagementPanel.this,ex); }
            }
        });

        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Read current saved records for this screen and refresh its choices, table or count.
                try { reload(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Read current saved records for this screen and refresh its choices, table or count.
                try { reload(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonShowAll.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Set the text displayed by jTextFieldSearch to empty text.
                // Read current saved records for this screen and refresh its choices, table or count.
                try { jTextFieldSearch.setText(""); reload(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonUpdateRole.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Change another account's role after locking Admin records to prevent conflicting role changes.
                // Read current saved records for this screen and refresh its choices, table or count.
                try { management.changeRole(FormSupport.selectedId(jTableUsers), jComboBoxRole.getSelectedItem().toString()); reload(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Show the selected row in the editing fields.
        // Connect the table selection to valueChanged so selecting a row updates the displayed details.
        jTableUsers.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            // Respond to a table-selection change; the event can fire several times while a selection is
            // adjusting.
            public void valueChanged(javax.swing.event.ListSelectionEvent event) {
                // Wait for a completed selection change before reading the selected table row.
                // Set the text displayed by jLabelSelectedUser to `"Selected user: " + FormSupport.cell(jTableUsers,
                // 1)`.
                // Select the entry matching `FormSupport.cell(jTableUsers, 6)` in jComboBoxRole.
                if (!event.getValueIsAdjusting()) { jLabelSelectedUser.setText("Selected user: " + FormSupport.cell(jTableUsers,1)); jComboBoxRole.setSelectedItem(FormSupport.cell(jTableUsers,6)); }
            }
        });
        // Read current saved records for this screen and refresh its choices, table or count.
        reload();

    }

    // Search only public account details; password hashes never enter this table.
    // Read current saved records for this screen and refresh its choices, table or count.
    private void reload() {
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Replace the displayed rows using the supplied table model data; keep the existing headings.
        // Set the text displayed by jLabelCount to `"Users: " + jTableUsers.getRowCount()`.
        try { FormSupport.fill(jTableUsers, management.users(jTextFieldSearch.getText())); jLabelCount.setText("Users: " + jTableUsers.getRowCount()); }
        // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
        // Show the caught error as a message instead of allowing the event action to fail silently.
        catch (Exception ex) { FormSupport.error(this, ex); }
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

        // Set jButton3 from the following operation: create a clickable action button.
        jButton3 = new javax.swing.JButton();
        // Set jLabelTitle from the following operation: create a non-editable text or image display.
        jLabelTitle = new javax.swing.JLabel();
        // Set jLabelSearch from the following operation: create a non-editable text or image display.
        jLabelSearch = new javax.swing.JLabel();
        // Set jButtonRefresh from the following operation: create a clickable action button.
        jButtonRefresh = new javax.swing.JButton();
        // Set jButtonShowAll from the following operation: create a clickable action button.
        jButtonShowAll = new javax.swing.JButton();
        // Set jButtonSearch from the following operation: create a clickable action button.
        jButtonSearch = new javax.swing.JButton();
        // Set jTextFieldSearch from the following operation: create a single-line text input.
        jTextFieldSearch = new javax.swing.JTextField();
        // Set jScrollPane1 from the following operation: create a scrollable viewport for another control.
        jScrollPane1 = new javax.swing.JScrollPane();
        // Set jTableUsers from the following operation: create a table displaying rows and columns through a
        // model.
        jTableUsers = new javax.swing.JTable();
        // Set jLabelRole from the following operation: create a non-editable text or image display.
        jLabelRole = new javax.swing.JLabel();
        // Set jComboBoxRole from the following operation: create a dropdown selection control.
        jComboBoxRole = new javax.swing.JComboBox<>();
        // Set jButtonUpdateRole from the following operation: create a clickable action button.
        jButtonUpdateRole = new javax.swing.JButton();
        // Set jLabelCount from the following operation: create a non-editable text or image display.
        jLabelCount = new javax.swing.JLabel();
        // Set jLabelHint from the following operation: create a non-editable text or image display.
        jLabelHint = new javax.swing.JLabel();
        // Set jLabelSelectedUser from the following operation: create a non-editable text or image display.
        jLabelSelectedUser = new javax.swing.JLabel();
        // Set jButtonUsersPdf from the following operation: create a clickable action button.
        jButtonUsersPdf = new javax.swing.JButton();
        // Set jButtonSelectedUserPdf from the following operation: create a clickable action button.
        jButtonSelectedUserPdf = new javax.swing.JButton();

        // Set the text displayed by jButton3 to empty text.
        jButton3.setText("");

        // Set the text displayed by jLabelTitle to "User Management".
        jLabelTitle.setText("User Management");

        // Set the text displayed by jLabelSearch to "Search users:".
        jLabelSearch.setText("Search users:");

        // Set the text displayed by jButtonRefresh to "Refresh".
        jButtonRefresh.setText("Refresh");

        // Set the text displayed by jButtonShowAll to "Show All".
        jButtonShowAll.setText("Show All");

        // Set the text displayed by jButtonSearch to "Search".
        jButtonSearch.setText("Search");

        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jTextFieldSearch.addActionListener(this::jTextFieldSearchActionPerformed);

        // Give jTableUsers the supplied model, which holds its choices or table data.
        jTableUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "User ID", "Full Name", "Username", "Email", "Account Type", "Organisation", "Role"
            }
        ) {
            // Declare canEdit with type boolean[]. Create an array of boolean values containing the listed entries
            // in order.
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            // Tell the table model whether the indicated cell accepts direct typing; false protects displayed
            // database values.
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                // Return `canEdit[columnIndex]` to the caller.
                return canEdit [columnIndex];
            }
        });
        // Place jTableUsers inside jScrollPane1 so it can scroll when larger than the available space.
        jScrollPane1.setViewportView(jTableUsers);

        // Set the text displayed by jLabelRole to "Role:".
        jLabelRole.setText("Role:");

        // Give jComboBoxRole the supplied model, which holds its choices or table data.
        jComboBoxRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select role", "Requester", "Manager", "Purchaser", "Admin" }));

        // Set the text displayed by jButtonUpdateRole to "Update Role".
        jButtonUpdateRole.setText("Update Role");

        // Set the text displayed by jLabelCount to "Users: 0".
        jLabelCount.setText("Users: 0");

        // Set the text displayed by jLabelHint to "Role changes are restricted to authorised staff.".
        jLabelHint.setText("Role changes are restricted to authorised staff.");

        // Set the text displayed by jLabelSelectedUser to "Selected user: None".
        jLabelSelectedUser.setText("Selected user: None");

        // Set the text displayed by jButtonUsersPdf to "View Users PDF".
        jButtonUsersPdf.setText("View Users PDF");

        // Set the text displayed by jButtonSelectedUserPdf to "Selected User PDF".
        jButtonSelectedUserPdf.setText("Selected User PDF");

        // Declare layout with type javax.swing.GroupLayout. Create a layout manager describing horizontal and
        // vertical arrangements separately.
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        // Assign the supplied layout manager to this to control child-component positions and sizes.
        this.setLayout(layout);
        // Define the left-to-right arrangement and widths; the vertical group separately controls heights.
        layout.setHorizontalGroup(
            // Start a parallel group whose children occupy the same region on this axis, using the selected
            // alignment.
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap()
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Place jLabelSelectedUser in this layout group; any following size arguments give minimum, preferred
                        // and maximum sizes.
                        .addComponent(jLabelSelectedUser)
                        // Leave spacing between the group and its enclosing container edge.
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                // Place jLabelSearch in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jLabelSearch)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                // Place jTextFieldSearch in this layout group; any following size arguments give minimum, preferred
                                // and maximum sizes.
                                .addComponent(jTextFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                                // Place jButtonSearch in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jButtonSearch)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(18, 18, 18)
                                // Place jButtonShowAll in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jButtonShowAll)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(18, 18, 18)
                                // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jButtonRefresh))
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    // Place jLabelCount in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jLabelCount)
                                    // Nest a sequential group to place controls one after another.
                                    .addGroup(layout.createSequentialGroup()
                                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                        .addGap(54, 54, 54)
                                        // Place jButtonUsersPdf in this layout group; any following size arguments give minimum, preferred and
                                        // maximum sizes.
                                        .addComponent(jButtonUsersPdf)))
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    // Nest a sequential group to place controls one after another.
                                    .addGroup(layout.createSequentialGroup()
                                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                        .addGap(6, 6, 6)
                                        // Place jButtonSelectedUserPdf in this layout group; any following size arguments give minimum,
                                        // preferred and maximum sizes.
                                        .addComponent(jButtonSelectedUserPdf))
                                    // Place jLabelHint in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jLabelHint)))
                            // Nest a sequential group to place controls one after another.
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    // Nest a sequential group to place controls one after another.
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        // Place jLabelRole in this layout group; any following size arguments give minimum, preferred and
                                        // maximum sizes.
                                        .addComponent(jLabelRole)
                                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                        .addGap(18, 18, 18)
                                        // Place jComboBoxRole in this layout group; any following size arguments give minimum, preferred and
                                        // maximum sizes.
                                        .addComponent(jComboBoxRole, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                        .addGap(18, 18, 18)
                                        // Place jButtonUpdateRole in this layout group; any following size arguments give minimum, preferred
                                        // and maximum sizes.
                                        .addComponent(jButtonUpdateRole))
                                    // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jLabelTitle, javax.swing.GroupLayout.Alignment.LEADING))
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(0, 0, Short.MAX_VALUE)))
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(16, 16, 16))))
        );
        // Define the top-to-bottom arrangement and heights; the horizontal group separately controls widths.
        layout.setVerticalGroup(
            // Start a parallel group whose children occupy the same region on this axis, using the selected
            // alignment.
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap()
                // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelTitle)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(18, 18, 18)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelSearch in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelSearch)
                    // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonRefresh)
                    // Place jButtonShowAll in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonShowAll)
                    // Place jButtonSearch in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonSearch)
                    // Place jTextFieldSearch in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jTextFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Place jLabelSelectedUser in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jLabelSelectedUser)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelRole in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelRole)
                    // Place jComboBoxRole in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jComboBoxRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jButtonUpdateRole in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jButtonUpdateRole))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelCount in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelCount)
                    // Place jLabelHint in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelHint))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jButtonUsersPdf in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonUsersPdf)
                    // Place jButtonSelectedUserPdf in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jButtonSelectedUserPdf))
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap(9, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Handle the action event from jTextFieldSearch; any statements below run when that action is raised.
    private void jTextFieldSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSearchActionPerformed
        // No extra action is needed here; the form connects its buttons in the constructor.
    }//GEN-LAST:event_jTextFieldSearchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Only this class accesses this field directly. Declare jButton3 with type javax.swing.JButton. Java
    // initially uses null for this object reference.
    private javax.swing.JButton jButton3;
    // Only this class accesses this field directly. Declare jButtonRefresh with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonRefresh;
    // Only this class accesses this field directly. Declare jButtonSearch with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonSearch;
    // Only this class accesses this field directly. Declare jButtonSelectedUserPdf with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonSelectedUserPdf;
    // Only this class accesses this field directly. Declare jButtonShowAll with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonShowAll;
    // Only this class accesses this field directly. Declare jButtonUpdateRole with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonUpdateRole;
    // Only this class accesses this field directly. Declare jButtonUsersPdf with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonUsersPdf;
    // Only this class accesses this field directly. Declare jComboBoxRole with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxRole;
    // Only this class accesses this field directly. Declare jLabelCount with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelCount;
    // Only this class accesses this field directly. Declare jLabelHint with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelHint;
    // Only this class accesses this field directly. Declare jLabelRole with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelRole;
    // Only this class accesses this field directly. Declare jLabelSearch with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelSearch;
    // Only this class accesses this field directly. Declare jLabelSelectedUser with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelSelectedUser;
    // Only this class accesses this field directly. Declare jLabelTitle with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelTitle;
    // Only this class accesses this field directly. Declare jScrollPane1 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane1;
    // Only this class accesses this field directly. Declare jTableUsers with type javax.swing.JTable. Java
    // initially uses null for this object reference.
    private javax.swing.JTable jTableUsers;
    // Only this class accesses this field directly. Declare jTextFieldSearch with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldSearch;
    // End of variables declaration//GEN-END:variables
}
