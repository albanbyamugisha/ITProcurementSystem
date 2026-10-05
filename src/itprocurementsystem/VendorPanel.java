// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

/**
 * A JPanel is used because vendor management belongs inside MainFrame.
 * It groups the related controls without opening another window.
 * MainFrame provides the title bar and navigation for this panel.
 *
 * @author alban-byamugisha
 */
// Define VendorPanel as a JPanel: it groups this task's controls inside MainFrame rather than opening
// another window.
public class VendorPanel extends javax.swing.JPanel {
    // The DAO handles validation and SQL; the form handles display.
    // Only this class accesses this field directly. Declare management with type ManagementDAO. Create a
    // ManagementDAO object using the supplied constructor values. The reference or value cannot be
    // reassigned after initialisation.
    private final ManagementDAO management = new ManagementDAO();


    /**
     * Creates new form VendorPanel
     */
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public VendorPanel() {
        // Build the controls and layout saved in NetBeans Design view.
        // Create the controls and apply the layout stored by NetBeans before reading or changing any of those
        // controls.
        initComponents();
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
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Clear the selected vendor and the editable supplier fields for a fresh entry.
                try { clearFields(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonAddVendor.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Validate supplier details, then insert a new vendor for ID zero or update the specified existing
                // vendor.
                try { saveVendor(0); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonUpdateVendor.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Validate supplier details, then insert a new vendor for ID zero or update the specified existing
                // vendor.
                try { saveVendor(FormSupport.selectedId(jTableVendors)); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Show the selected row in the editing fields.
        // Connect the table selection to valueChanged so selecting a row updates the displayed details.
        jTableVendors.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            // Respond to a table-selection change; the event can fire several times while a selection is
            // adjusting.
            public void valueChanged(javax.swing.event.ListSelectionEvent event) {
                // Wait for a completed selection change before reading the selected table row.
                // Set the text displayed by jTextFieldVendorName to `FormSupport.cell(jTableVendors, 1).toString()`.
                // Set the text displayed by jTextFieldContactPerson to `FormSupport.cell(jTableVendors,
                // 2).toString()`.
                // Set the text displayed by jTextFieldPhone to `FormSupport.cell(jTableVendors, 3).toString()`.
                // Set the text displayed by jTextFieldEmail to `FormSupport.cell(jTableVendors, 4).toString()`.
                // Set the text displayed by jTextAreaAddress to `FormSupport.cell(jTableVendors, 5).toString()`.
                if (!event.getValueIsAdjusting()) { jTextFieldVendorName.setText(FormSupport.cell(jTableVendors, 1).toString()); jTextFieldContactPerson.setText(FormSupport.cell(jTableVendors, 2).toString()); jTextFieldPhone.setText(FormSupport.cell(jTableVendors, 3).toString()); jTextFieldEmail.setText(FormSupport.cell(jTableVendors, 4).toString()); jTextAreaAddress.setText(FormSupport.cell(jTableVendors, 5).toString()); }
            }
        });
        // Read current saved records for this screen and refresh its choices, table or count.
        reload();

    }

    // Reload saved rows after an add or update.
    // Read current saved records for this screen and refresh its choices, table or count.
    private void reload() {
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Replace the displayed rows using the supplied table model data; keep the existing headings.
        // Set the text displayed by jLabelCount to `"Vendors: " + jTableVendors.getRowCount()`.
        try { FormSupport.fill(jTableVendors, management.vendors()); jLabelCount.setText("Vendors: " + jTableVendors.getRowCount()); }
        // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
        // Show the caught error as a message instead of allowing the event action to fail silently.
        catch (Exception ex) { FormSupport.error(this, ex); }
    }
    // Clear the selected vendor and the editable supplier fields for a fresh entry.
    private void clearFields() {
        // Clear the selected row or entry in jTableVendors without deleting its data.
        jTableVendors.clearSelection();
        // Set the text displayed by jTextFieldVendorName to empty text.
        jTextFieldVendorName.setText("");
        // Set the text displayed by jTextFieldContactPerson to empty text.
        jTextFieldContactPerson.setText("");
        // Set the text displayed by jTextFieldPhone to empty text.
        jTextFieldPhone.setText("");
        // Set the text displayed by jTextFieldEmail to empty text.
        jTextFieldEmail.setText("");
        // Set the text displayed by jTextAreaAddress to empty text.
        jTextAreaAddress.setText("");
    }
    // Validate supplier details, then insert a new vendor for ID zero or update the specified existing
    // vendor.
    private void saveVendor(int id) throws java.sql.SQLException {
        // Validate supplier details, then insert a new vendor for ID zero or update the specified existing
        // vendor.
        management.saveVendor(id, jTextFieldVendorName.getText(), jTextFieldContactPerson.getText(), jTextFieldPhone.getText(), jTextFieldEmail.getText(), jTextAreaAddress.getText());
        // Read current saved records for this screen and refresh its choices, table or count.
        // Clear the selected vendor and the editable supplier fields for a fresh entry.
        reload(); clearFields();
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
        // Set jLabelVendorName from the following operation: create a non-editable text or image display.
        jLabelVendorName = new javax.swing.JLabel();
        // Set jLabelContactName from the following operation: create a non-editable text or image display.
        jLabelContactName = new javax.swing.JLabel();
        // Set jLabelPhone from the following operation: create a non-editable text or image display.
        jLabelPhone = new javax.swing.JLabel();
        // Set jLabelEmail from the following operation: create a non-editable text or image display.
        jLabelEmail = new javax.swing.JLabel();
        // Set jLabelAddress from the following operation: create a non-editable text or image display.
        jLabelAddress = new javax.swing.JLabel();
        // Set jButtonAddVendor from the following operation: create a clickable action button.
        jButtonAddVendor = new javax.swing.JButton();
        // Set jButtonUpdateVendor from the following operation: create a clickable action button.
        jButtonUpdateVendor = new javax.swing.JButton();
        // Set jButtonClear from the following operation: create a clickable action button.
        jButtonClear = new javax.swing.JButton();
        // Set jButtonRefresh from the following operation: create a clickable action button.
        jButtonRefresh = new javax.swing.JButton();
        // Set jScrollPane1 from the following operation: create a scrollable viewport for another control.
        jScrollPane1 = new javax.swing.JScrollPane();
        // Set jTableVendors from the following operation: create a table displaying rows and columns through a
        // model.
        jTableVendors = new javax.swing.JTable();
        // Set jLabelCount from the following operation: create a non-editable text or image display.
        jLabelCount = new javax.swing.JLabel();
        // Set jTextFieldEmail from the following operation: create a single-line text input.
        jTextFieldEmail = new javax.swing.JTextField();
        // Set jTextFieldVendorName from the following operation: create a single-line text input.
        jTextFieldVendorName = new javax.swing.JTextField();
        // Set jTextFieldContactPerson from the following operation: create a single-line text input.
        jTextFieldContactPerson = new javax.swing.JTextField();
        // Set jTextFieldPhone from the following operation: create a single-line text input.
        jTextFieldPhone = new javax.swing.JTextField();
        // Set jScrollPane2 from the following operation: create a scrollable viewport for another control.
        jScrollPane2 = new javax.swing.JScrollPane();
        // Set jTextAreaAddress from the following operation: create a multi-line text display or input.
        jTextAreaAddress = new javax.swing.JTextArea();




        // Set the text displayed by jLabelTitle to "Vendor Management".
        jLabelTitle.setText("Vendor Management");

        // Set the text displayed by jLabelVendorName to "Vendor Name:".
        jLabelVendorName.setText("Vendor Name:");

        // Set the text displayed by jLabelContactName to "Contact person:".
        jLabelContactName.setText("Contact person:");

        // Set the text displayed by jLabelPhone to "Phone:".
        jLabelPhone.setText("Phone:");

        // Set the text displayed by jLabelEmail to "Email:".
        jLabelEmail.setText("Email:");

        // Set the text displayed by jLabelAddress to "Address:".
        jLabelAddress.setText("Address:");

        // Set the text displayed by jButtonAddVendor to "Add Vendor".
        jButtonAddVendor.setText("Add Vendor");

        // Set the text displayed by jButtonUpdateVendor to "Update Selected".
        jButtonUpdateVendor.setText("Update Selected");

        // Set the text displayed by jButtonClear to "Clear".
        jButtonClear.setText("Clear");

        // Set the text displayed by jButtonRefresh to "Refresh".
        jButtonRefresh.setText("Refresh");

        // Give jTableVendors the supplied model, which holds its choices or table data.
        jTableVendors.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Vendor ID", "Vendor Name", "Contact Person", "Phone", "Email", "Address"
            }
        ) {
            // Declare canEdit with type boolean[]. Create an array of boolean values containing the listed entries
            // in order.
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            // Tell the table model whether the indicated cell accepts direct typing; false protects displayed
            // database values.
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                // Return `canEdit[columnIndex]` to the caller.
                return canEdit [columnIndex];
            }
        });
        // Place jTableVendors inside jScrollPane1 so it can scroll when larger than the available space.
        jScrollPane1.setViewportView(jTableVendors);

        // Set the text displayed by jLabelCount to "Vendors: 0".
        jLabelCount.setText("Vendors: 0");

        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jTextFieldVendorName.addActionListener(this::jTextFieldVendorNameActionPerformed);

        // Control whether wrapped text breaks at word boundaries rather than between letters.
        jTextAreaAddress.setWrapStyleWord(true);
        // Control whether jTextAreaAddress wraps long text onto another line.
        jTextAreaAddress.setLineWrap(true);
        // Set the preferred text width of jTextAreaAddress to 20 columns; this is not a text-length limit.
        jTextAreaAddress.setColumns(20);
        // Set the preferred visible height of jTextAreaAddress to 5 text rows.
        jTextAreaAddress.setRows(5);
        // Place jTextAreaAddress inside jScrollPane2 so it can scroll when larger than the available space.
        jScrollPane2.setViewportView(jTextAreaAddress);

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
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(46, 46, 46)
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(249, 249, 249)
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    // Place jLabelAddress in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jLabelAddress)
                                    // Place jLabelEmail in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jLabelEmail)))
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Place jButtonAddVendor in this layout group; any following size arguments give minimum, preferred
                                // and maximum sizes.
                                .addComponent(jButtonAddVendor)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(18, 18, 18)
                                // Place jButtonUpdateVendor in this layout group; any following size arguments give minimum, preferred
                                // and maximum sizes.
                                .addComponent(jButtonUpdateVendor)))
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(54, 54, 54)
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    // Place jTextFieldEmail in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jTextFieldEmail)
                                    // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)))
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(11, 11, 11)
                                // Place jButtonClear in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jButtonClear)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(63, 63, 63)
                                // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jButtonRefresh)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(0, 0, Short.MAX_VALUE))))
                    // Place jLabelCount in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelCount)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave spacing between the group and its enclosing container edge.
                        .addContainerGap()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelTitle)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Place jLabelVendorName in this layout group; any following size arguments give minimum, preferred
                                // and maximum sizes.
                                .addComponent(jLabelVendorName)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                // Place jTextFieldVendorName in this layout group; any following size arguments give minimum,
                                // preferred and maximum sizes.
                                .addComponent(jTextFieldVendorName, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    // Place jLabelContactName in this layout group; any following size arguments give minimum, preferred
                                    // and maximum sizes.
                                    .addComponent(jLabelContactName)
                                    // Place jLabelPhone in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jLabelPhone))
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(2, 2, 2)
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    // Place jTextFieldContactPerson in this layout group; any following size arguments give minimum,
                                    // preferred and maximum sizes.
                                    .addComponent(jTextFieldContactPerson, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                                    // Place jTextFieldPhone in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jTextFieldPhone))))))
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap())
            // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
            // maximum sizes.
            .addComponent(jScrollPane1)
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
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelVendorName in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jLabelVendorName)
                    // Place jLabelEmail in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelEmail)
                    // Place jTextFieldEmail in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jTextFieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jTextFieldVendorName in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jTextFieldVendorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            // Place jLabelContactName in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jLabelContactName)
                            // Place jLabelAddress in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelAddress)
                            // Place jTextFieldContactPerson in this layout group; any following size arguments give minimum,
                            // preferred and maximum sizes.
                            .addComponent(jTextFieldContactPerson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            // Place jLabelPhone in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelPhone)
                            // Place jTextFieldPhone in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jTextFieldPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(6, 6, 6)
                        // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(18, 18, 18)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonRefresh, javax.swing.GroupLayout.Alignment.TRAILING)
                    // Nest a parallel group so controls share this horizontal or vertical region.
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        // Place jButtonAddVendor in this layout group; any following size arguments give minimum, preferred
                        // and maximum sizes.
                        .addComponent(jButtonAddVendor)
                        // Place jButtonUpdateVendor in this layout group; any following size arguments give minimum, preferred
                        // and maximum sizes.
                        .addComponent(jButtonUpdateVendor)
                        // Place jButtonClear in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonClear)))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jLabelCount in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelCount)
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Handle the action event from jTextFieldVendorName; any statements below run when that action is
    // raised.
    private void jTextFieldVendorNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldVendorNameActionPerformed
        // No extra action is needed here; the form connects its buttons in the constructor.
    }//GEN-LAST:event_jTextFieldVendorNameActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Only this class accesses this field directly. Declare jButtonAddVendor with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonAddVendor;
    // Only this class accesses this field directly. Declare jButtonClear with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonClear;
    // Only this class accesses this field directly. Declare jButtonRefresh with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonRefresh;
    // Only this class accesses this field directly. Declare jButtonUpdateVendor with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonUpdateVendor;
    // Only this class accesses this field directly. Declare jLabelAddress with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelAddress;
    // Only this class accesses this field directly. Declare jLabelContactName with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelContactName;
    // Only this class accesses this field directly. Declare jLabelCount with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelCount;
    // Only this class accesses this field directly. Declare jLabelEmail with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelEmail;
    // Only this class accesses this field directly. Declare jLabelPhone with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelPhone;
    // Only this class accesses this field directly. Declare jLabelTitle with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelTitle;
    // Only this class accesses this field directly. Declare jLabelVendorName with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelVendorName;
    // Only this class accesses this field directly. Declare jScrollPane1 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane1;
    // Only this class accesses this field directly. Declare jScrollPane2 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane2;
    // Only this class accesses this field directly. Declare jTableVendors with type javax.swing.JTable.
    // Java initially uses null for this object reference.
    private javax.swing.JTable jTableVendors;
    // Only this class accesses this field directly. Declare jTextAreaAddress with type
    // javax.swing.JTextArea. Java initially uses null for this object reference.
    private javax.swing.JTextArea jTextAreaAddress;
    // Only this class accesses this field directly. Declare jTextFieldContactPerson with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldContactPerson;
    // Only this class accesses this field directly. Declare jTextFieldEmail with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldEmail;
    // Only this class accesses this field directly. Declare jTextFieldPhone with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldPhone;
    // Only this class accesses this field directly. Declare jTextFieldVendorName with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldVendorName;
    // End of variables declaration//GEN-END:variables
}
