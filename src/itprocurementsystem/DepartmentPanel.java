// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

/**
 * A JPanel is used because department management belongs inside MainFrame.
 * It groups the related controls without opening another window.
 * MainFrame provides the title bar and navigation for this panel.
 *
 * @author alban-byamugisha
 */
// Define DepartmentPanel as a JPanel: it groups this task's controls inside MainFrame rather than
// opening another window.
public class DepartmentPanel extends javax.swing.JPanel {
    // The DAO handles validation and SQL; the form handles display.
    // Only this class accesses this field directly. Declare management with type ManagementDAO. Create a
    // ManagementDAO object using the supplied constructor values. The reference or value cannot be
    // reassigned after initialisation.
    private final ManagementDAO management = new ManagementDAO();


    /**
     * Creates new form DepartmentPanel
     */
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public DepartmentPanel() {
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
                // Clear the selected row or entry in jTableDepartments without deleting its data.
                // Set the text displayed by jTextFieldDepartmentName to empty text.
                try { jTableDepartments.clearSelection(); jTextFieldDepartmentName.setText(""); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonAddDepartment.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Save a new or selected department after checking Admin access and duplicate names.
                try { saveDepartment(0); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonUpdateDepartment.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Save a new or selected department after checking Admin access and duplicate names.
                try { saveDepartment(FormSupport.selectedId(jTableDepartments)); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Show the selected row in the editing fields.
        // Connect the table selection to valueChanged so selecting a row updates the displayed details.
        jTableDepartments.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            // Respond to a table-selection change; the event can fire several times while a selection is
            // adjusting.
            public void valueChanged(javax.swing.event.ListSelectionEvent event) {
                // Wait for a completed selection change before reading the selected table row.
                // Set the text displayed by jTextFieldDepartmentName to `FormSupport.cell(jTableDepartments,
                // 1).toString()`.
                // Set the text displayed by jLabelSelectedDepartment to `"Selected department: " +
                // FormSupport.cell(jTableDepartments, 1)`.
                if (!event.getValueIsAdjusting()) { jTextFieldDepartmentName.setText(FormSupport.cell(jTableDepartments,1).toString()); jLabelSelectedDepartment.setText("Selected department: " + FormSupport.cell(jTableDepartments,1)); }
            }
        });
        // Read current saved records for this screen and refresh its choices, table or count.
        reload();

    }

    // Refresh the table so it reflects the database, not an unsaved field.
    // Read current saved records for this screen and refresh its choices, table or count.
    private void reload() {
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Replace the displayed rows using the supplied table model data; keep the existing headings.
        // Set the text displayed by jLabelCount to `"Departments: " + jTableDepartments.getRowCount()`.
        try { FormSupport.fill(jTableDepartments, management.departments()); jLabelCount.setText("Departments: " + jTableDepartments.getRowCount()); }
        // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
        // Show the caught error as a message instead of allowing the event action to fail silently.
        catch (Exception ex) { FormSupport.error(this, ex); }
    }
    // Save a new or selected department after checking Admin access and duplicate names.
    private void saveDepartment(int id) throws java.sql.SQLException {
        // Save a new or selected department after checking Admin access and duplicate names.
        // Read current saved records for this screen and refresh its choices, table or count.
        // Set the text displayed by jTextFieldDepartmentName to empty text.
        management.saveDepartment(id, jTextFieldDepartmentName.getText()); reload(); jTextFieldDepartmentName.setText("");
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
        // Set jLabelDepartmentName from the following operation: create a non-editable text or image display.
        jLabelDepartmentName = new javax.swing.JLabel();
        // Set jTextFieldDepartmentName from the following operation: create a single-line text input.
        jTextFieldDepartmentName = new javax.swing.JTextField();
        // Set jButtonAddDepartment from the following operation: create a clickable action button.
        jButtonAddDepartment = new javax.swing.JButton();
        // Set jButtonUpdateDepartment from the following operation: create a clickable action button.
        jButtonUpdateDepartment = new javax.swing.JButton();
        // Set jButtonClear from the following operation: create a clickable action button.
        jButtonClear = new javax.swing.JButton();
        // Set jButtonRefresh from the following operation: create a clickable action button.
        jButtonRefresh = new javax.swing.JButton();
        // Set jScrollPane1 from the following operation: create a scrollable viewport for another control.
        jScrollPane1 = new javax.swing.JScrollPane();
        // Set jTableDepartments from the following operation: create a table displaying rows and columns
        // through a model.
        jTableDepartments = new javax.swing.JTable();
        // Set jLabelSelectedDepartment from the following operation: create a non-editable text or image
        // display.
        jLabelSelectedDepartment = new javax.swing.JLabel();
        // Set jLabelCount from the following operation: create a non-editable text or image display.
        jLabelCount = new javax.swing.JLabel();



        // Set the text displayed by jLabelTitle to "Department Management".
        jLabelTitle.setText("Department Management");

        // Set the text displayed by jLabelDepartmentName to "Department name:".
        jLabelDepartmentName.setText("Department name:");

        // Set the text displayed by jButtonAddDepartment to "Add Department".
        jButtonAddDepartment.setText("Add Department");

        // Set the text displayed by jButtonUpdateDepartment to "Update Selected".
        jButtonUpdateDepartment.setText("Update Selected");

        // Set the text displayed by jButtonClear to "Clear".
        jButtonClear.setText("Clear");

        // Set the text displayed by jButtonRefresh to "Refresh".
        jButtonRefresh.setText("Refresh");

        // Give jTableDepartments the supplied model, which holds its choices or table data.
        jTableDepartments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Department ID", "Department Name"
            }
        ) {
            // Declare canEdit with type boolean[]. Create an array of boolean values containing the listed entries
            // in order.
            boolean[] canEdit = new boolean [] {
                false, false
            };

            // Tell the table model whether the indicated cell accepts direct typing; false protects displayed
            // database values.
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                // Return `canEdit[columnIndex]` to the caller.
                return canEdit [columnIndex];
            }
        });
        // Place jTableDepartments inside jScrollPane1 so it can scroll when larger than the available space.
        jScrollPane1.setViewportView(jTableDepartments);

        // Set the text displayed by jLabelSelectedDepartment to "Selected department: None".
        jLabelSelectedDepartment.setText("Selected department: None");

        // Set the text displayed by jLabelCount to "Departments: 0".
        jLabelCount.setText("Departments: 0");

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
                        // Place jLabelDepartmentName in this layout group; any following size arguments give minimum,
                        // preferred and maximum sizes.
                        .addComponent(jLabelDepartmentName)
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(18, 18, 18)
                        // Place jTextFieldDepartmentName in this layout group; any following size arguments give minimum,
                        // preferred and maximum sizes.
                        .addComponent(jTextFieldDepartmentName))
                    // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jScrollPane1)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Place jLabelSelectedDepartment in this layout group; any following size arguments give minimum,
                            // preferred and maximum sizes.
                            .addComponent(jLabelSelectedDepartment)
                            // Place jLabelCount in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelCount))
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(0, 0, Short.MAX_VALUE))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Place jButtonAddDepartment in this layout group; any following size arguments give minimum,
                        // preferred and maximum sizes.
                        .addComponent(jButtonAddDepartment)
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        // Place jButtonUpdateDepartment in this layout group; any following size arguments give minimum,
                        // preferred and maximum sizes.
                        .addComponent(jButtonUpdateDepartment)
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        // Place jButtonClear in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonClear)
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(18, 18, 18)
                        // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonRefresh)))
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap())
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(111, 111, 111)
                // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelTitle)
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    // Place jLabelDepartmentName in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jLabelDepartmentName)
                    // Place jTextFieldDepartmentName in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jTextFieldDepartmentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jButtonAddDepartment in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jButtonAddDepartment)
                    // Place jButtonUpdateDepartment in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jButtonUpdateDepartment)
                    // Place jButtonClear in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonClear)
                    // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonRefresh))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Place jLabelSelectedDepartment in this layout group; any following size arguments give minimum,
                // preferred and maximum sizes.
                .addComponent(jLabelSelectedDepartment)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jLabelCount in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelCount)
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Only this class accesses this field directly. Declare jButtonAddDepartment with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonAddDepartment;
    // Only this class accesses this field directly. Declare jButtonClear with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonClear;
    // Only this class accesses this field directly. Declare jButtonRefresh with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonRefresh;
    // Only this class accesses this field directly. Declare jButtonUpdateDepartment with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonUpdateDepartment;
    // Only this class accesses this field directly. Declare jLabelCount with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelCount;
    // Only this class accesses this field directly. Declare jLabelDepartmentName with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelDepartmentName;
    // Only this class accesses this field directly. Declare jLabelSelectedDepartment with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelSelectedDepartment;
    // Only this class accesses this field directly. Declare jLabelTitle with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelTitle;
    // Only this class accesses this field directly. Declare jScrollPane1 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane1;
    // Only this class accesses this field directly. Declare jTableDepartments with type
    // javax.swing.JTable. Java initially uses null for this object reference.
    private javax.swing.JTable jTableDepartments;
    // Only this class accesses this field directly. Declare jTextFieldDepartmentName with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldDepartmentName;
    // End of variables declaration//GEN-END:variables
}
