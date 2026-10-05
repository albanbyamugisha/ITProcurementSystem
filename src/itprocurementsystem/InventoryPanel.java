// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

/**
 * A JPanel is used because equipment inventory belongs inside MainFrame.
 * It groups the related controls without opening another window.
 * MainFrame provides the title bar and navigation for this panel.
 *
 * @author alban-byamugisha
 */
// Define InventoryPanel as a JPanel: it groups this task's controls inside MainFrame rather than
// opening another window.
public class InventoryPanel extends javax.swing.JPanel {
    // Keep fulfilment SQL and validation separate from the displayed controls.
    // Only this class accesses this field directly. Declare fulfilment with type FulfilmentDAO. Create a
    // FulfilmentDAO object using the supplied constructor values. The reference or value cannot be
    // reassigned after initialisation.
    private final FulfilmentDAO fulfilment = new FulfilmentDAO();


    /**
     * Creates new form InventoryPanel
     */
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public InventoryPanel() {
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
        jButtonAssign.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Update or clear an inventory assignment and save its audit record and optional user notification
                // together.
                // Read current saved records for this screen and refresh its choices, table or count.
                try { fulfilment.assign(FormSupport.selectedId(jTableInventory),FormSupport.choice(jComboBoxAssignedTo)); reload(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonUnassign.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Update or clear an inventory assignment and save its audit record and optional user notification
                // together.
                // Read current saved records for this screen and refresh its choices, table or count.
                try { fulfilment.assign(FormSupport.selectedId(jTableInventory),null); reload(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Show the selected row in the editing fields.
        // Connect the table selection to valueChanged so selecting a row updates the displayed details.
        jTableInventory.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            // Respond to a table-selection change; the event can fire several times while a selection is
            // adjusting.
            public void valueChanged(javax.swing.event.ListSelectionEvent event) {
                // Wait for a completed selection change before reading the selected table row.
                // Set the text displayed by jLabelSelectedEquipment to `"Selected equipment: " +
                // FormSupport.cell(jTableInventory, 1)`.
                if (!event.getValueIsAdjusting()) { jLabelSelectedEquipment.setText("Selected equipment: " + FormSupport.cell(jTableInventory,1)); }
            }
        });
        // Read current saved records for this screen and refresh its choices, table or count.
        reload();

    }

    // Refresh users as well as equipment so newly registered accounts can be selected.
    // Read current saved records for this screen and refresh its choices, table or count.
    private void reload() {
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Replace the displayed rows using the supplied table model data; keep the existing headings.
            FormSupport.fill(jTableInventory, fulfilment.inventory(jTextFieldSearch.getText()));
            // Rebuild the dropdown with its prompt followed by the supplied ID-and-description choices.
            FormSupport.choices(jComboBoxAssignedTo, fulfilment.assignees(), "Select user");
            // Set the text displayed by jLabelCount to `"Equipment units: " + jTableInventory.getRowCount()`.
            jLabelCount.setText("Equipment units: " + jTableInventory.getRowCount());
        // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
        // Show the caught error as a message instead of allowing the event action to fail silently.
        } catch (Exception ex) { FormSupport.error(this, ex); }
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
        // Set jLabelSearch from the following operation: create a non-editable text or image display.
        jLabelSearch = new javax.swing.JLabel();
        // Set jTextFieldSearch from the following operation: create a single-line text input.
        jTextFieldSearch = new javax.swing.JTextField();
        // Set jButtonSearch from the following operation: create a clickable action button.
        jButtonSearch = new javax.swing.JButton();
        // Set jButtonShowAll from the following operation: create a clickable action button.
        jButtonShowAll = new javax.swing.JButton();
        // Set jButtonRefresh from the following operation: create a clickable action button.
        jButtonRefresh = new javax.swing.JButton();
        // Set jScrollPane1 from the following operation: create a scrollable viewport for another control.
        jScrollPane1 = new javax.swing.JScrollPane();
        // Set jTableInventory from the following operation: create a table displaying rows and columns through
        // a model.
        jTableInventory = new javax.swing.JTable();
        // Set jLabelSelectedEquipment from the following operation: create a non-editable text or image
        // display.
        jLabelSelectedEquipment = new javax.swing.JLabel();
        // Set jLabelAssignTo from the following operation: create a non-editable text or image display.
        jLabelAssignTo = new javax.swing.JLabel();
        // Set jComboBoxAssignedTo from the following operation: create a dropdown selection control.
        jComboBoxAssignedTo = new javax.swing.JComboBox<>();
        // Set jButtonAssign from the following operation: create a clickable action button.
        jButtonAssign = new javax.swing.JButton();
        // Set jButtonUnassign from the following operation: create a clickable action button.
        jButtonUnassign = new javax.swing.JButton();
        // Set jLabelCount from the following operation: create a non-editable text or image display.
        jLabelCount = new javax.swing.JLabel();


        // Set the text displayed by jLabelTitle to "Equipment Inventory".
        jLabelTitle.setText("Equipment Inventory");

        // Set the text displayed by jLabelSearch to "Search:".
        jLabelSearch.setText("Search:");

        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jTextFieldSearch.addActionListener(this::jTextFieldSearchActionPerformed);

        // Set the text displayed by jButtonSearch to "Search".
        jButtonSearch.setText("Search");

        // Set the text displayed by jButtonShowAll to "Show All".
        jButtonShowAll.setText("Show All");

        // Set the text displayed by jButtonRefresh to "Refresh".
        jButtonRefresh.setText("Refresh");

        // Give jTableInventory the supplied model, which holds its choices or table data.
        jTableInventory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Inventory ID", "Description", "Serial Number", "Category", "Assigned To", "Date Added", "Request ID"
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
        // Place jTableInventory inside jScrollPane1 so it can scroll when larger than the available space.
        jScrollPane1.setViewportView(jTableInventory);

        // Set the text displayed by jLabelSelectedEquipment to "Selected equipment: None".
        jLabelSelectedEquipment.setText("Selected equipment: None");

        // Set the text displayed by jLabelAssignTo to "Assign to:".
        jLabelAssignTo.setText("Assign to:");

        // Give jComboBoxAssignedTo the supplied model, which holds its choices or table data.
        jComboBoxAssignedTo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select user" }));

        // Set the text displayed by jButtonAssign to "Assign Selected".
        jButtonAssign.setText("Assign Selected");

        // Set the text displayed by jButtonUnassign to "Unassign Selected".
        jButtonUnassign.setText("Unassign Selected");

        // Set the text displayed by jLabelCount to "Equipment units: 0".
        jLabelCount.setText("Equipment units: 0");

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
                    // Place jLabelSelectedEquipment in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jLabelSelectedEquipment)
                    // Place jLabelCount in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelCount))
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(0, 0, Short.MAX_VALUE))
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane1)
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap())
            // Nest a sequential group to place controls one after another.
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                // Place jLabelSearch in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelSearch)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Place jTextFieldSearch in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jTextFieldSearch)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(18, 18, Short.MAX_VALUE)
                // Place jButtonSearch in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jButtonSearch)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(55, 55, 55)
                // Place jButtonShowAll in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jButtonShowAll)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(65, 65, 65)
                // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jButtonRefresh)
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap())
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Place jLabelAssignTo in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelAssignTo)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Place jComboBoxAssignedTo in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jComboBoxAssignedTo, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                // Place jButtonAssign in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jButtonAssign)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(28, 28, 28)
                // Place jButtonUnassign in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jButtonUnassign)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(158, 158, 158))
            // Nest a sequential group to place controls one after another.
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(0, 0, Short.MAX_VALUE)
                // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelTitle)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(334, 334, 334))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Nest a parallel group so controls share this horizontal or vertical region.
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        // Place jLabelSearch in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jLabelSearch)
                        // Place jTextFieldSearch in this layout group; any following size arguments give minimum, preferred
                        // and maximum sizes.
                        .addComponent(jTextFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    // Nest a parallel group so controls share this horizontal or vertical region.
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        // Place jButtonSearch in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonSearch)
                        // Place jButtonShowAll in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonShowAll)
                        // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonRefresh)))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jLabelSelectedEquipment in this layout group; any following size arguments give minimum,
                // preferred and maximum sizes.
                .addComponent(jLabelSelectedEquipment)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelAssignTo in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelAssignTo)
                    // Place jComboBoxAssignedTo in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jComboBoxAssignedTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jButtonAssign in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonAssign)
                    // Place jButtonUnassign in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonUnassign))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Place jLabelCount in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelCount)
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap(15, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Handle the action event from jTextFieldSearch; any statements below run when that action is raised.
    private void jTextFieldSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSearchActionPerformed
        // No extra action is needed here; the form connects its buttons in the constructor.
    }//GEN-LAST:event_jTextFieldSearchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Only this class accesses this field directly. Declare jButtonAssign with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonAssign;
    // Only this class accesses this field directly. Declare jButtonRefresh with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonRefresh;
    // Only this class accesses this field directly. Declare jButtonSearch with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonSearch;
    // Only this class accesses this field directly. Declare jButtonShowAll with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonShowAll;
    // Only this class accesses this field directly. Declare jButtonUnassign with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonUnassign;
    // Only this class accesses this field directly. Declare jComboBoxAssignedTo with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxAssignedTo;
    // Only this class accesses this field directly. Declare jLabelAssignTo with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelAssignTo;
    // Only this class accesses this field directly. Declare jLabelCount with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelCount;
    // Only this class accesses this field directly. Declare jLabelSearch with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelSearch;
    // Only this class accesses this field directly. Declare jLabelSelectedEquipment with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelSelectedEquipment;
    // Only this class accesses this field directly. Declare jLabelTitle with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelTitle;
    // Only this class accesses this field directly. Declare jScrollPane1 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane1;
    // Only this class accesses this field directly. Declare jTableInventory with type javax.swing.JTable.
    // Java initially uses null for this object reference.
    private javax.swing.JTable jTableInventory;
    // Only this class accesses this field directly. Declare jTextFieldSearch with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldSearch;
    // End of variables declaration//GEN-END:variables
}
