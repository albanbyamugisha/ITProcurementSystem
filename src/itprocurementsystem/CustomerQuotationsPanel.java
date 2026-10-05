// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

/**
 * A JPanel is used because customer order confirmation belongs inside MainFrame.
 * It groups the related controls without opening another window.
 * MainFrame provides the title bar and navigation for this panel.
 *
 * @author alban-byamugisha
 */
// Define CustomerQuotationsPanel as a JPanel: it groups this task's controls inside MainFrame rather
// than opening another window.
public class CustomerQuotationsPanel extends javax.swing.JPanel {
    // This DAO checks ownership, roles and decision status before saving.
    // Only this class accesses this field directly. Declare decisions with type DecisionDAO. Create a
    // DecisionDAO object using the supplied constructor values. The reference or value cannot be
    // reassigned after initialisation.
    private final DecisionDAO decisions = new DecisionDAO();


    /**
     * Creates new form CustomerQuotationsPanel
     */
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public CustomerQuotationsPanel() {
        // Build the controls and layout saved in NetBeans Design view.
        // Create the controls and apply the layout stored by NetBeans before reading or changing any of those
        // controls.
        initComponents();
        // Right-click the request control to view its saved supporting documents.
        // Attach a right-click menu that opens supporting documents for the currently selected request.
        AttachmentDAO.addMenu(jComboBoxRequest, new AttachmentDAO.RequestChoice() {
            // Supply the currently selected request ID to the attachment menu without storing an outdated
            // selection.
            // Read the database ID from the selected dropdown entry, rejecting its initial prompt. Return the
            // resulting value to the caller.
            public int requestId() { return FormSupport.choice(jComboBoxRequest); }
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
        jButtonLoadQuotations.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Load the chosen request's order or quotation rows and reset details from any previous selection.
                try { loadQuotations(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonAccept.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Save the selected decision through the permission-checking DAO, then refresh the screen.
                try { saveDecision(true); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonDecline.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Save the selected decision through the permission-checking DAO, then refresh the screen.
                try { saveDecision(false); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Show the selected row in the editing fields.
        // Connect the table selection to valueChanged so selecting a row updates the displayed details.
        jTableQuotations.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            // Respond to a table-selection change; the event can fire several times while a selection is
            // adjusting.
            public void valueChanged(javax.swing.event.ListSelectionEvent event) {
                // Wait for a completed selection change before reading the selected table row.
                // Display the selected order or quotation's items, totals and decision details.
                if (!event.getValueIsAdjusting()) { showQuote(); }
            }
        });
        // Changing the request clears the old rows so they cannot be used accidentally.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jComboBoxRequest.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Clear displayed decision details so values from a previous request are not reused.
            public void actionPerformed(java.awt.event.ActionEvent event) { clearDetails(); }
        });
        // Read current saved records for this screen and refresh its choices, table or count.
        reload();

    }

    // Populate the choices with records that this user is allowed to view.
    // Read current saved records for this screen and refresh its choices, table or count.
    private void reload() {
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Rebuild the dropdown with its prompt followed by the supplied ID-and-description choices.
        // Clear displayed decision details so values from a previous request are not reused.
        try { FormSupport.choices(jComboBoxRequest, decisions.requests(false), "Select request"); clearDetails(); }
        // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
        // Show the caught error as a message instead of allowing the event action to fail silently.
        catch (Exception ex) { FormSupport.error(this, ex); }
    }
    // Clear displayed decision details so values from a previous request are not reused.
    private void clearDetails() {
        // Replace the displayed rows using the supplied table model data; keep the existing headings.
        FormSupport.fill(jTableQuotations, new java.util.ArrayList<Object[]>());
        // Replace the displayed rows using the supplied table model data; keep the existing headings.
        FormSupport.fill(jTableQuotationItems, new java.util.ArrayList<Object[]>());
        // Set the text displayed by jTextAreaDetails to empty text.
        // Set the text displayed by jTextAreaComments to empty text.
        jTextAreaDetails.setText(""); jTextAreaComments.setText("");
        // Set the text displayed by jLabelDecision to "Decision: Select an order".
        jLabelDecision.setText("Decision: Select an order");
    }
    // Load the chosen request's order or quotation rows and reset details from any previous selection.
    private void loadQuotations() throws java.sql.SQLException {
        // Declare id to hold a whole-number value. Read the database ID from the selected dropdown entry,
        // rejecting its initial prompt.
        int id = FormSupport.choice(jComboBoxRequest);
        // Replace the displayed rows using the supplied table model data; keep the existing headings.
        FormSupport.fill(jTableQuotations, decisions.quotations(id, false));
        // Set the text displayed by jTextAreaDetails to `decisions.details(id, 0, false)`.
        jTextAreaDetails.setText(decisions.details(id, 0, false));
        
    }
    // The selected order supplies saved customer prices, never internal supplier prices.
    // Display the selected order or quotation's items, totals and decision details.
    private void showQuote() {
        // Continue with this branch when (jTableQuotations.getSelectedRow() is less than 0).
        // Stop this method here; no further statements in this call are executed.
        if (jTableQuotations.getSelectedRow() < 0) { return; }
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Declare id to hold a whole-number value. Read the database ID from the selected dropdown entry,
            // rejecting its initial prompt.
            int id = FormSupport.choice(jComboBoxRequest);
            // Declare quote to hold a whole-number value. Read column zero of the selected table model row,
            // rejecting an absent selection.
            int quote = FormSupport.selectedId(jTableQuotations);
            // Replace the displayed rows using the supplied table model data; keep the existing headings.
            FormSupport.fill(jTableQuotationItems, decisions.items(id, quote, false));
            // Set the text displayed by jTextAreaDetails to `decisions.details(id, quote, false)`.
            jTextAreaDetails.setText(decisions.details(id, quote, false));
            // Set the text displayed by jLabelDecision to `"Order status: " + FormSupport.cell(jTableQuotations,
            // 3)`.
            jLabelDecision.setText("Order status: " + FormSupport.cell(jTableQuotations,3));
        // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
        // Show the caught error as a message instead of allowing the event action to fail silently.
        } catch (Exception ex) { FormSupport.error(this, ex); }
    }
    // Save the selected decision through the permission-checking DAO, then refresh the screen.
    private void saveDecision(boolean accept) throws java.sql.SQLException {
        // Declare id to hold a whole-number value. Read the database ID from the selected dropdown entry,
        // rejecting its initial prompt.
        int id = FormSupport.choice(jComboBoxRequest);
        // Declare quote to hold a whole-number value. Read column zero of the selected table model row,
        // rejecting an absent selection.
        int quote = FormSupport.selectedId(jTableQuotations);
        // Save the owner's acceptance or rejection of a fixed-price catalogue order once only.
        decisions.customerDecision(id, quote, accept, jTextAreaComments.getText());
        // Set the text displayed by jTextAreaComments to empty text.
        jTextAreaComments.setText("");
        // Load the chosen request's order or quotation rows and reset details from any previous selection.
        loadQuotations();
        // Display the supplied message in a Swing dialog, using the parent, title and message style when
        // provided.
        javax.swing.JOptionPane.showMessageDialog(this, "Decision saved.");
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
        // Set jLabelRequest from the following operation: create a non-editable text or image display.
        jLabelRequest = new javax.swing.JLabel();
        // Set jButtonLoadQuotations from the following operation: create a clickable action button.
        jButtonLoadQuotations = new javax.swing.JButton();
        // Set jButtonRefresh from the following operation: create a clickable action button.
        jButtonRefresh = new javax.swing.JButton();
        // Set jComboBoxRequest from the following operation: create a dropdown selection control.
        jComboBoxRequest = new javax.swing.JComboBox<>();
        // Set jScrollPane1 from the following operation: create a scrollable viewport for another control.
        jScrollPane1 = new javax.swing.JScrollPane();
        // Set jTableQuotations from the following operation: create a table displaying rows and columns
        // through a model.
        jTableQuotations = new javax.swing.JTable();
        // Set jLabelItems from the following operation: create a non-editable text or image display.
        jLabelItems = new javax.swing.JLabel();
        // Set jScrollPane2 from the following operation: create a scrollable viewport for another control.
        jScrollPane2 = new javax.swing.JScrollPane();
        // Set jTableQuotationItems from the following operation: create a table displaying rows and columns
        // through a model.
        jTableQuotationItems = new javax.swing.JTable();
        // Set jLabelDetails from the following operation: create a non-editable text or image display.
        jLabelDetails = new javax.swing.JLabel();
        // Set jScrollPane3 from the following operation: create a scrollable viewport for another control.
        jScrollPane3 = new javax.swing.JScrollPane();
        // Set jTextAreaDetails from the following operation: create a multi-line text display or input.
        jTextAreaDetails = new javax.swing.JTextArea();
        // Set jLabelComments from the following operation: create a non-editable text or image display.
        jLabelComments = new javax.swing.JLabel();
        // Set jScrollPane4 from the following operation: create a scrollable viewport for another control.
        jScrollPane4 = new javax.swing.JScrollPane();
        // Set jTextAreaComments from the following operation: create a multi-line text display or input.
        jTextAreaComments = new javax.swing.JTextArea();
        // Set jLabelDecision from the following operation: create a non-editable text or image display.
        jLabelDecision = new javax.swing.JLabel();
        // Set jButtonAccept from the following operation: create a clickable action button.
        jButtonAccept = new javax.swing.JButton();
        // Set jButtonDecline from the following operation: create a clickable action button.
        jButtonDecline = new javax.swing.JButton();

        // Set the text displayed by jLabelTitle to "My Orders".
        jLabelTitle.setText("My Orders");

        // Set the text displayed by jLabelRequest to "My request".
        jLabelRequest.setText("My request");

        // Set the text displayed by jButtonLoadQuotations to "Load Order".
        jButtonLoadQuotations.setText("Load Order");

        // Set the text displayed by jButtonRefresh to "Refresh".
        jButtonRefresh.setText("Refresh");

        // Give jComboBoxRequest the supplied model, which holds its choices or table data.
        jComboBoxRequest.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select request" }));

        // Give jTableQuotations the supplied model, which holds its choices or table data.
        jTableQuotations.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Order ID", "Price basis", "Total (UGX)", "Status"
            }
        ) {
            // Declare canEdit with type boolean[]. Create an array of boolean values containing the listed entries
            // in order.
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            // Tell the table model whether the indicated cell accepts direct typing; false protects displayed
            // database values.
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                // Return `canEdit[columnIndex]` to the caller.
                return canEdit [columnIndex];
            }
        });
        // Place jTableQuotations inside jScrollPane1 so it can scroll when larger than the available space.
        jScrollPane1.setViewportView(jTableQuotations);

        // Set the text displayed by jLabelItems to "Order items".
        jLabelItems.setText("Order items");

        // Give jTableQuotationItems the supplied model, which holds its choices or table data.
        jTableQuotationItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Description", "Quantity", "Unit price (UGX)", "Line total (UGX)"
            }
        ) {
            // Declare canEdit with type boolean[]. Create an array of boolean values containing the listed entries
            // in order.
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            // Tell the table model whether the indicated cell accepts direct typing; false protects displayed
            // database values.
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                // Return `canEdit[columnIndex]` to the caller.
                return canEdit [columnIndex];
            }
        });
        // Place jTableQuotationItems inside jScrollPane2 so it can scroll when larger than the available
        // space.
        jScrollPane2.setViewportView(jTableQuotationItems);

        // Set the text displayed by jLabelDetails to "Order details".
        jLabelDetails.setText("Order details");

        // Control whether wrapped text breaks at word boundaries rather than between letters.
        jTextAreaDetails.setWrapStyleWord(true);
        // Control whether jTextAreaDetails wraps long text onto another line.
        jTextAreaDetails.setLineWrap(true);
        // Set whether jTextAreaDetails allows direct typing using false; its value can still be changed by
        // code.
        jTextAreaDetails.setEditable(false);
        // Set the preferred text width of jTextAreaDetails to 20 columns; this is not a text-length limit.
        jTextAreaDetails.setColumns(20);
        // Set the preferred visible height of jTextAreaDetails to 5 text rows.
        jTextAreaDetails.setRows(5);
        // Set the text displayed by jTextAreaDetails to "Select an order to view its details.".
        jTextAreaDetails.setText("Select an order to view its details.");
        // Place jTextAreaDetails inside jScrollPane3 so it can scroll when larger than the available space.
        jScrollPane3.setViewportView(jTextAreaDetails);

        // Set the text displayed by jLabelComments to "Ythe comments (optional)".
        jLabelComments.setText("Your comments (optional)");

        // Control whether wrapped text breaks at word boundaries rather than between letters.
        jTextAreaComments.setWrapStyleWord(true);
        // Control whether jTextAreaComments wraps long text onto another line.
        jTextAreaComments.setLineWrap(true);
        // Set the preferred text width of jTextAreaComments to 20 columns; this is not a text-length limit.
        jTextAreaComments.setColumns(20);
        // Set the preferred visible height of jTextAreaComments to 5 text rows.
        jTextAreaComments.setRows(5);
        // Place jTextAreaComments inside jScrollPane4 so it can scroll when larger than the available space.
        jScrollPane4.setViewportView(jTextAreaComments);

        // Set the text displayed by jLabelDecision to "Decision: Select an order".
        jLabelDecision.setText("Decision: Select an order");

        // Set the text displayed by jButtonAccept to "Confirm Order".
        jButtonAccept.setText("Confirm Order");

        // Set the text displayed by jButtonDecline to "Decline Order".
        jButtonDecline.setText("Decline Order");

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
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelTitle)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Place jLabelRequest in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jLabelRequest)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(18, 18, 18)
                                // Place jComboBoxRequest in this layout group; any following size arguments give minimum, preferred
                                // and maximum sizes.
                                .addComponent(jComboBoxRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 168, Short.MAX_VALUE)
                                // Place jButtonLoadQuotations in this layout group; any following size arguments give minimum,
                                // preferred and maximum sizes.
                                .addComponent(jButtonLoadQuotations)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(46, 46, 46)
                                // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jButtonRefresh))
                            // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jScrollPane1)
                            // Place jLabelItems in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelItems)
                            // Place jLabelDetails in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelDetails)
                            // Place jLabelComments in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelComments)
                            // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jScrollPane2))
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(0, 0, Short.MAX_VALUE))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Place jLabelDecision in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jLabelDecision)
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        // Place jButtonDecline in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonDecline)
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(32, 32, 32)
                        // Place jButtonAccept in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonAccept))
                    // Place jScrollPane3 in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jScrollPane3)
                    // Place jScrollPane4 in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelRequest in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelRequest)
                    // Place jButtonLoadQuotations in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jButtonLoadQuotations)
                    // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonRefresh)
                    // Place jComboBoxRequest in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jComboBoxRequest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jLabelItems in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelItems)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jLabelDetails in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelDetails)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane3 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jLabelComments in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelComments)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane4 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelDecision in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelDecision)
                    // Place jButtonAccept in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonAccept)
                    // Place jButtonDecline in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonDecline))
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(7, 7, 7))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Only this class accesses this field directly. Declare jButtonAccept with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonAccept;
    // Only this class accesses this field directly. Declare jButtonDecline with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonDecline;
    // Only this class accesses this field directly. Declare jButtonLoadQuotations with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonLoadQuotations;
    // Only this class accesses this field directly. Declare jButtonRefresh with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonRefresh;
    // Only this class accesses this field directly. Declare jComboBoxRequest with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxRequest;
    // Only this class accesses this field directly. Declare jLabelComments with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelComments;
    // Only this class accesses this field directly. Declare jLabelDecision with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelDecision;
    // Only this class accesses this field directly. Declare jLabelDetails with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelDetails;
    // Only this class accesses this field directly. Declare jLabelItems with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelItems;
    // Only this class accesses this field directly. Declare jLabelRequest with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelRequest;
    // Only this class accesses this field directly. Declare jLabelTitle with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelTitle;
    // Only this class accesses this field directly. Declare jScrollPane1 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane1;
    // Only this class accesses this field directly. Declare jScrollPane2 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane2;
    // Only this class accesses this field directly. Declare jScrollPane3 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane3;
    // Only this class accesses this field directly. Declare jScrollPane4 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane4;
    // Only this class accesses this field directly. Declare jTableQuotationItems with type
    // javax.swing.JTable. Java initially uses null for this object reference.
    private javax.swing.JTable jTableQuotationItems;
    // Only this class accesses this field directly. Declare jTableQuotations with type javax.swing.JTable.
    // Java initially uses null for this object reference.
    private javax.swing.JTable jTableQuotations;
    // Only this class accesses this field directly. Declare jTextAreaComments with type
    // javax.swing.JTextArea. Java initially uses null for this object reference.
    private javax.swing.JTextArea jTextAreaComments;
    // Only this class accesses this field directly. Declare jTextAreaDetails with type
    // javax.swing.JTextArea. Java initially uses null for this object reference.
    private javax.swing.JTextArea jTextAreaDetails;
    // End of variables declaration//GEN-END:variables
}
