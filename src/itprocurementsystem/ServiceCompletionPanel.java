// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

/**
 * A JPanel is used because service progress entry belongs inside MainFrame.
 * It groups the related controls without opening another window.
 * MainFrame provides the title bar and navigation for this panel.
 *
 * @author alban-byamugisha
 */
// Define ServiceCompletionPanel as a JPanel: it groups this task's controls inside MainFrame rather
// than opening another window.
public class ServiceCompletionPanel extends javax.swing.JPanel {
    // Keep fulfilment SQL and validation separate from the displayed controls.
    // Only this class accesses this field directly. Declare fulfilment with type FulfilmentDAO. Create a
    // FulfilmentDAO object using the supplied constructor values. The reference or value cannot be
    // reassigned after initialisation.
    private final FulfilmentDAO fulfilment = new FulfilmentDAO();


    /**
     * Creates new form ServiceCompletionPanel
     */
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public ServiceCompletionPanel() {
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
        jButtonLoadDetails.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Load the selected service request's items, provider and any saved work progress.
                try { loadDetails(); }
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
                // Clear previously displayed service notes, completion date, items and status.
                try { clearProgress(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonSaveProgress.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Validate and save service status, notes and completion date in one transaction.
                // Read current saved records for this screen and refresh its choices, table or count.
                // Display the supplied message in a Swing dialog, using the parent, title and message style when
                // provided.
                try { fulfilment.saveProgress(FormSupport.choice(jComboBoxRequest), jComboBoxWorkStatus.getSelectedItem().toString(), jTextAreaWorkNotes.getText(), jTextFieldCompletionDate.getText()); reload(); javax.swing.JOptionPane.showMessageDialog(null,"Service progress saved."); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jComboBoxRequest.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Clear previously displayed service notes, completion date, items and status.
            public void actionPerformed(java.awt.event.ActionEvent event) { clearProgress(); }
        });
        // Read current saved records for this screen and refresh its choices, table or count.
        reload();

    }

    // Services use notes and a date, never inventory or serial numbers.
    // Read current saved records for this screen and refresh its choices, table or count.
    private void reload() {
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Rebuild the dropdown with its prompt followed by the supplied ID-and-description choices.
        // Clear previously displayed service notes, completion date, items and status.
        try { FormSupport.choices(jComboBoxRequest, fulfilment.requests("Service"), "Select request"); clearProgress(); }
        // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
        // Show the caught error as a message instead of allowing the event action to fail silently.
        catch (Exception ex) { FormSupport.error(this, ex); }
    }
    // Clear previously displayed service notes, completion date, items and status.
    private void clearProgress() {
        // Replace the displayed rows using the supplied table model data; keep the existing headings.
        FormSupport.fill(jTableServiceItems, new java.util.ArrayList<Object[]>());
        // Set the text displayed by jTextAreaWorkNotes to empty text.
        // Set the text displayed by jTextFieldCompletionDate to empty text.
        jTextAreaWorkNotes.setText(""); jTextFieldCompletionDate.setText("");
        // Select position 0 in jComboBoxWorkStatus; dropdown positions start at zero.
        // Set the text displayed by jLabelProvider to "Provider: Select a request".
        jComboBoxWorkStatus.setSelectedIndex(0); jLabelProvider.setText("Provider: Select a request");
    }
    // Load the selected service request's items, provider and any saved work progress.
    private void loadDetails() throws java.sql.SQLException {
        // Declare request to hold a whole-number value. Read the database ID from the selected dropdown entry,
        // rejecting its initial prompt.
        int request = FormSupport.choice(jComboBoxRequest);
        // Replace the displayed rows using the supplied table model data; keep the existing headings.
        FormSupport.fill(jTableServiceItems, fulfilment.serviceItems(request));
        // Set the text displayed by jLabelProvider to `"Provider: " + fulfilment.supplier(request,
        // "Service")`.
        jLabelProvider.setText("Provider: " + fulfilment.supplier(request,"Service"));
        // Declare progress to hold one row of values in SELECT or table-column order. Read saved service
        // progress, or return initial values when no progress record exists yet.
        Object[] progress = fulfilment.progress(request);
        // Select the entry matching `progress[0]` in jComboBoxWorkStatus.
        jComboBoxWorkStatus.setSelectedItem(progress[0]);
        // Set the text displayed by jTextAreaWorkNotes to `progress[1] == null ? "" : progress[1].toString()`.
        jTextAreaWorkNotes.setText(progress[1] == null ? "" : progress[1].toString());
        // Set the text displayed by jTextFieldCompletionDate to `progress[2] == null ? "" :
        // progress[2].toString()`.
        jTextFieldCompletionDate.setText(progress[2] == null ? "" : progress[2].toString());
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
        // Set jLabelSelectReqeust from the following operation: create a non-editable text or image display.
        jLabelSelectReqeust = new javax.swing.JLabel();
        // Set jComboBoxRequest from the following operation: create a dropdown selection control.
        jComboBoxRequest = new javax.swing.JComboBox<>();
        // Set jButtonLoadDetails from the following operation: create a clickable action button.
        jButtonLoadDetails = new javax.swing.JButton();
        // Set jButtonRefresh from the following operation: create a clickable action button.
        jButtonRefresh = new javax.swing.JButton();
        // Set jLabelProvider from the following operation: create a non-editable text or image display.
        jLabelProvider = new javax.swing.JLabel();
        // Set jLabelRequestedServices from the following operation: create a non-editable text or image
        // display.
        jLabelRequestedServices = new javax.swing.JLabel();
        // Set jScrollPane1 from the following operation: create a scrollable viewport for another control.
        jScrollPane1 = new javax.swing.JScrollPane();
        // Set jTableServiceItems from the following operation: create a table displaying rows and columns
        // through a model.
        jTableServiceItems = new javax.swing.JTable();
        // Set jLabelWorkStatus from the following operation: create a non-editable text or image display.
        jLabelWorkStatus = new javax.swing.JLabel();
        // Set jComboBoxWorkStatus from the following operation: create a dropdown selection control.
        jComboBoxWorkStatus = new javax.swing.JComboBox<>();
        // Set jLabelCompletionDate from the following operation: create a non-editable text or image display.
        jLabelCompletionDate = new javax.swing.JLabel();
        // Set jTextFieldCompletionDate from the following operation: create a single-line text input.
        jTextFieldCompletionDate = new javax.swing.JTextField();
        // Set jLabel1 from the following operation: create a non-editable text or image display.
        jLabel1 = new javax.swing.JLabel();
        // Set jScrollPane2 from the following operation: create a scrollable viewport for another control.
        jScrollPane2 = new javax.swing.JScrollPane();
        // Set jTextAreaWorkNotes from the following operation: create a multi-line text display or input.
        jTextAreaWorkNotes = new javax.swing.JTextArea();
        // Set jButtonClear from the following operation: create a clickable action button.
        jButtonClear = new javax.swing.JButton();
        // Set jButtonSaveProgress from the following operation: create a clickable action button.
        jButtonSaveProgress = new javax.swing.JButton();
        // Set jLabelHint from the following operation: create a non-editable text or image display.
        jLabelHint = new javax.swing.JLabel();


        // Set the text displayed by jLabelTitle to "Service Completion".
        jLabelTitle.setText("Service Completion");

        // Set the text displayed by jLabelSelectReqeust to "Select Request:".
        jLabelSelectReqeust.setText("Select Request:");

        // Give jComboBoxRequest the supplied model, which holds its choices or table data.
        jComboBoxRequest.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select request" }));

        // Set the text displayed by jButtonLoadDetails to "Load Details".
        jButtonLoadDetails.setText("Load Details");

        // Set the text displayed by jButtonRefresh to "Refresh".
        jButtonRefresh.setText("Refresh");

        // Set the text displayed by jLabelProvider to "Provider: Select a request".
        jLabelProvider.setText("Provider: Select a request");

        // Set the text displayed by jLabelRequestedServices to "Requested Services:".
        jLabelRequestedServices.setText("Requested Services:");

        // Give jTableServiceItems the supplied model, which holds its choices or table data.
        jTableServiceItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item ID", "Description", "Quantity"
            }
        ) {
            // Declare canEdit with type boolean[]. Create an array of boolean values containing the listed entries
            // in order.
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            // Tell the table model whether the indicated cell accepts direct typing; false protects displayed
            // database values.
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                // Return `canEdit[columnIndex]` to the caller.
                return canEdit [columnIndex];
            }
        });
        // Place jTableServiceItems inside jScrollPane1 so it can scroll when larger than the available space.
        jScrollPane1.setViewportView(jTableServiceItems);

        // Set the text displayed by jLabelWorkStatus to "Work Status:".
        jLabelWorkStatus.setText("Work Status:");

        // Give jComboBoxWorkStatus the supplied model, which holds its choices or table data.
        jComboBoxWorkStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select status", "Not Started", "In Progress", "Completed" }));

        // Set the text displayed by jLabelCompletionDate to "Completion date (YYYY-MM-DD):".
        jLabelCompletionDate.setText("Completion date (YYYY-MM-DD):");

        // Set the text displayed by jLabel1 to "Work notes:".
        jLabel1.setText("Work notes:");

        // Control whether wrapped text breaks at word boundaries rather than between letters.
        jTextAreaWorkNotes.setWrapStyleWord(true);
        // Control whether jTextAreaWorkNotes wraps long text onto another line.
        jTextAreaWorkNotes.setLineWrap(true);
        // Set the preferred text width of jTextAreaWorkNotes to 20 columns; this is not a text-length limit.
        jTextAreaWorkNotes.setColumns(20);
        // Set the preferred visible height of jTextAreaWorkNotes to 5 text rows.
        jTextAreaWorkNotes.setRows(5);
        // Place jTextAreaWorkNotes inside jScrollPane2 so it can scroll when larger than the available space.
        jScrollPane2.setViewportView(jTextAreaWorkNotes);

        // Set the text displayed by jButtonClear to "Clear".
        jButtonClear.setText("Clear");

        // Set the text displayed by jButtonSaveProgress to "Save Progress".
        jButtonSaveProgress.setText("Save Progress");

        // Set the text displayed by jLabelHint to "Enter a completion date only when the service is
        // completed.".
        jLabelHint.setText("Enter a completion date only when the service is completed.");

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
                    // Nest a parallel group so controls share this horizontal or vertical region.
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jLabelTitle)
                        // Nest a sequential group to place controls one after another.
                        .addGroup(layout.createSequentialGroup()
                            // Place jLabelSelectReqeust in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jLabelSelectReqeust)
                            // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                            .addGap(18, 18, 18)
                            // Place jComboBoxRequest in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jComboBoxRequest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                            .addGap(44, 44, 44)
                            // Place jButtonLoadDetails in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jButtonLoadDetails)
                            // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                            .addGap(18, 18, 18)
                            // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jButtonRefresh))
                        // Place jLabelProvider in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jLabelProvider)
                        // Place jLabelRequestedServices in this layout group; any following size arguments give minimum,
                        // preferred and maximum sizes.
                        .addComponent(jLabelRequestedServices)
                        // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jScrollPane1))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Place jLabelWorkStatus in this layout group; any following size arguments give minimum, preferred
                        // and maximum sizes.
                        .addComponent(jLabelWorkStatus)
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        // Place jComboBoxWorkStatus in this layout group; any following size arguments give minimum, preferred
                        // and maximum sizes.
                        .addComponent(jComboBoxWorkStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Place jLabelCompletionDate in this layout group; any following size arguments give minimum,
                        // preferred and maximum sizes.
                        .addComponent(jLabelCompletionDate)
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        // Place jTextFieldCompletionDate in this layout group; any following size arguments give minimum,
                        // preferred and maximum sizes.
                        .addComponent(jTextFieldCompletionDate, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                    // Place jLabel1 in this layout group; any following size arguments give minimum, preferred and maximum
                    // sizes.
                    .addComponent(jLabel1)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(6, 6, 6)
                        // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 501, javax.swing.GroupLayout.PREFERRED_SIZE))))
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(109, 109, 109)
                // Place jButtonClear in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jButtonClear)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(87, 87, 87)
                // Place jButtonSaveProgress in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jButtonSaveProgress))
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(56, 56, 56)
                // Place jLabelHint in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelHint))
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
                    // Place jLabelSelectReqeust in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jLabelSelectReqeust)
                    // Place jComboBoxRequest in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jComboBoxRequest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jButtonLoadDetails in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jButtonLoadDetails)
                    // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonRefresh))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jLabelProvider in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelProvider)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jLabelRequestedServices in this layout group; any following size arguments give minimum,
                // preferred and maximum sizes.
                .addComponent(jLabelRequestedServices)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelWorkStatus in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jLabelWorkStatus)
                    // Place jComboBoxWorkStatus in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jComboBoxWorkStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelCompletionDate in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jLabelCompletionDate)
                    // Place jTextFieldCompletionDate in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jTextFieldCompletionDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jLabel1 in this layout group; any following size arguments give minimum, preferred and maximum
                // sizes.
                .addComponent(jLabel1)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jButtonClear in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonClear)
                    // Place jButtonSaveProgress in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jButtonSaveProgress))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jLabelHint in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelHint)
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap(10, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Only this class accesses this field directly. Declare jButtonClear with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonClear;
    // Only this class accesses this field directly. Declare jButtonLoadDetails with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonLoadDetails;
    // Only this class accesses this field directly. Declare jButtonRefresh with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonRefresh;
    // Only this class accesses this field directly. Declare jButtonSaveProgress with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonSaveProgress;
    // Only this class accesses this field directly. Declare jComboBoxRequest with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxRequest;
    // Only this class accesses this field directly. Declare jComboBoxWorkStatus with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxWorkStatus;
    // Only this class accesses this field directly. Declare jLabel1 with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabel1;
    // Only this class accesses this field directly. Declare jLabelCompletionDate with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelCompletionDate;
    // Only this class accesses this field directly. Declare jLabelHint with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelHint;
    // Only this class accesses this field directly. Declare jLabelProvider with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelProvider;
    // Only this class accesses this field directly. Declare jLabelRequestedServices with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelRequestedServices;
    // Only this class accesses this field directly. Declare jLabelSelectReqeust with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelSelectReqeust;
    // Only this class accesses this field directly. Declare jLabelTitle with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelTitle;
    // Only this class accesses this field directly. Declare jLabelWorkStatus with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelWorkStatus;
    // Only this class accesses this field directly. Declare jScrollPane1 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane1;
    // Only this class accesses this field directly. Declare jScrollPane2 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane2;
    // Only this class accesses this field directly. Declare jTableServiceItems with type
    // javax.swing.JTable. Java initially uses null for this object reference.
    private javax.swing.JTable jTableServiceItems;
    // Only this class accesses this field directly. Declare jTextAreaWorkNotes with type
    // javax.swing.JTextArea. Java initially uses null for this object reference.
    private javax.swing.JTextArea jTextAreaWorkNotes;
    // Only this class accesses this field directly. Declare jTextFieldCompletionDate with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldCompletionDate;
    // End of variables declaration//GEN-END:variables
}
