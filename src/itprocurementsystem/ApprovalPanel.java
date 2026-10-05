// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

/**
 * A JPanel is used because staff review takes place inside MainFrame.
 * This panel will group quotation choices, item details and review comments.
 * It does not open another window; MainFrame supplies the title bar and navigation.
 * Staff review chooses a supplier after the customer confirms the fixed-price order.
 *
 * @author alban-byamugisha
 */
// Define ApprovalPanel as a JPanel: it groups this task's controls inside MainFrame rather than
// opening another window.
public class ApprovalPanel extends javax.swing.JPanel {
    // This DAO checks ownership, roles and decision status before saving.
    // Only this class accesses this field directly. Declare decisions with type DecisionDAO. Create a
    // DecisionDAO object using the supplied constructor values. The reference or value cannot be
    // reassigned after initialisation.
    private final DecisionDAO decisions = new DecisionDAO();


    /**
     * Creates new form ApprovalPanel
     */
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public ApprovalPanel() {
        // Create the controls and layout arranged in NetBeans Design view.
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
        jButtonApprove.addActionListener(new java.awt.event.ActionListener() {
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
        jButtonReject.addActionListener(new java.awt.event.ActionListener() {
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
        try { FormSupport.choices(jComboBoxRequest, decisions.requests(true), "Select request"); clearDetails(); }
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
        // Set the text displayed by jTextAreaRequestDetails to empty text.
        // Set the text displayed by jTextAreaComments to empty text.
        jTextAreaRequestDetails.setText(""); jTextAreaComments.setText("");
        // Replace the displayed rows using the supplied table model data; keep the existing headings.
        FormSupport.fill(jTableApprovalHistory, new java.util.ArrayList<Object[]>());
    }
    // Load the chosen request's order or quotation rows and reset details from any previous selection.
    private void loadQuotations() throws java.sql.SQLException {
        // Declare id to hold a whole-number value. Read the database ID from the selected dropdown entry,
        // rejecting its initial prompt.
        int id = FormSupport.choice(jComboBoxRequest);
        // Replace the displayed rows using the supplied table model data; keep the existing headings.
        FormSupport.fill(jTableQuotations, decisions.quotations(id, true));
        // Set the text displayed by jTextAreaRequestDetails to `decisions.details(id, 0, true)`.
        jTextAreaRequestDetails.setText(decisions.details(id, 0, true));
        // Replace the displayed rows using the supplied table model data; keep the existing headings.
        FormSupport.fill(jTableApprovalHistory, decisions.history(id));
    }
    // A selected quotation supplies the item prices and notes shown below it.
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
            FormSupport.fill(jTableQuotationItems, decisions.items(id, quote, true));
            // Set the text displayed by jTextAreaRequestDetails to `decisions.details(id, quote, true)`.
            jTextAreaRequestDetails.setText(decisions.details(id, quote, true));
            
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
        // Save a manager's decision and internal supplier selection without changing the customer's saved
        // selling price.
        decisions.review(id, quote, accept, jTextAreaComments.getText());
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
        // Set jComboBoxRequest from the following operation: create a dropdown selection control.
        jComboBoxRequest = new javax.swing.JComboBox<>();
        // Set jButtonLoadQuotations from the following operation: create a clickable action button.
        jButtonLoadQuotations = new javax.swing.JButton();
        // Set jButtonRefresh from the following operation: create a clickable action button.
        jButtonRefresh = new javax.swing.JButton();
        // Set jLabelRequestDetails from the following operation: create a non-editable text or image display.
        jLabelRequestDetails = new javax.swing.JLabel();
        // Set jScrollPane1 from the following operation: create a scrollable viewport for another control.
        jScrollPane1 = new javax.swing.JScrollPane();
        // Set jTextAreaRequestDetails from the following operation: create a multi-line text display or input.
        jTextAreaRequestDetails = new javax.swing.JTextArea();
        // Set jScrollPane2 from the following operation: create a scrollable viewport for another control.
        jScrollPane2 = new javax.swing.JScrollPane();
        // Set jTableQuotations from the following operation: create a table displaying rows and columns
        // through a model.
        jTableQuotations = new javax.swing.JTable();
        // Set jLabelItems from the following operation: create a non-editable text or image display.
        jLabelItems = new javax.swing.JLabel();
        // Set jScrollPane4 from the following operation: create a scrollable viewport for another control.
        jScrollPane4 = new javax.swing.JScrollPane();
        // Set jTableQuotationItems from the following operation: create a table displaying rows and columns
        // through a model.
        jTableQuotationItems = new javax.swing.JTable();
        // Set jLabelComments from the following operation: create a non-editable text or image display.
        jLabelComments = new javax.swing.JLabel();
        // Set jScrollPane5 from the following operation: create a scrollable viewport for another control.
        jScrollPane5 = new javax.swing.JScrollPane();
        // Set jTextAreaComments from the following operation: create a multi-line text display or input.
        jTextAreaComments = new javax.swing.JTextArea();
        // Set jLabelReviewInfo from the following operation: create a non-editable text or image display.
        jLabelReviewInfo = new javax.swing.JLabel();
        // Set jButtonReject from the following operation: create a clickable action button.
        jButtonReject = new javax.swing.JButton();
        // Set jButtonApprove from the following operation: create a clickable action button.
        jButtonApprove = new javax.swing.JButton();
        // Set jLabelHistory from the following operation: create a non-editable text or image display.
        jLabelHistory = new javax.swing.JLabel();
        // Set jScrollPane6 from the following operation: create a scrollable viewport for another control.
        jScrollPane6 = new javax.swing.JScrollPane();
        // Set jTableApprovalHistory from the following operation: create a table displaying rows and columns
        // through a model.
        jTableApprovalHistory = new javax.swing.JTable();


        // Set the text displayed by jLabelTitle to "Review Quotations".
        jLabelTitle.setText("Review Quotations");

        // Set the text displayed by jLabelRequest to "Request".
        jLabelRequest.setText("Request");

        // Give jComboBoxRequest the supplied model, which holds its choices or table data.
        jComboBoxRequest.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select request" }));

        // Set the text displayed by jButtonLoadQuotations to "Load Quotations".
        jButtonLoadQuotations.setText("Load Quotations");

        // Set the text displayed by jButtonRefresh to "Refresh".
        jButtonRefresh.setText("Refresh");

        // Set the text displayed by jLabelRequestDetails to "Request Details".
        jLabelRequestDetails.setText("Request Details");

        // Control whether wrapped text breaks at word boundaries rather than between letters.
        jTextAreaRequestDetails.setWrapStyleWord(true);
        // Control whether jTextAreaRequestDetails wraps long text onto another line.
        jTextAreaRequestDetails.setLineWrap(true);
        // Set whether jTextAreaRequestDetails allows direct typing using false; its value can still be changed
        // by code.
        jTextAreaRequestDetails.setEditable(false);
        // Set the preferred text width of jTextAreaRequestDetails to 20 columns; this is not a text-length
        // limit.
        jTextAreaRequestDetails.setColumns(20);
        // Set the preferred visible height of jTextAreaRequestDetails to 5 text rows.
        jTextAreaRequestDetails.setRows(5);
        // Set the text displayed by jTextAreaRequestDetails to "Select a request to view its details.".
        jTextAreaRequestDetails.setText("Select a request to view its details.");
        // Place jTextAreaRequestDetails inside jScrollPane1 so it can scroll when larger than the available
        // space.
        jScrollPane1.setViewportView(jTextAreaRequestDetails);

        // Give jTableQuotations the supplied model, which holds its choices or table data.
        jTableQuotations.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Quotation ID", "Vendor", "Total", "Status"
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
        // Place jTableQuotations inside jScrollPane2 so it can scroll when larger than the available space.
        jScrollPane2.setViewportView(jTableQuotations);

        // Set the text displayed by jLabelItems to "Selected Quotation Items".
        jLabelItems.setText("Selected Quotation Items");

        // Give jTableQuotationItems the supplied model, which holds its choices or table data.
        jTableQuotationItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Description", "Quantity", "Unit Price", "Line Total"
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
        // Place jTableQuotationItems inside jScrollPane4 so it can scroll when larger than the available
        // space.
        jScrollPane4.setViewportView(jTableQuotationItems);

        // Set the text displayed by jLabelComments to "Review Comments".
        jLabelComments.setText("Review Comments");

        // Control whether wrapped text breaks at word boundaries rather than between letters.
        jTextAreaComments.setWrapStyleWord(true);
        // Control whether jTextAreaComments wraps long text onto another line.
        jTextAreaComments.setLineWrap(true);
        // Set the preferred text width of jTextAreaComments to 20 columns; this is not a text-length limit.
        jTextAreaComments.setColumns(20);
        // Set the preferred visible height of jTextAreaComments to 5 text rows.
        jTextAreaComments.setRows(5);
        // Place jTextAreaComments inside jScrollPane5 so it can scroll when larger than the available space.
        jScrollPane5.setViewportView(jTextAreaComments);

        // Set the text displayed by jLabelReviewInfo to "Staff review is separate from customer acceptance.".
        jLabelReviewInfo.setText("Staff review is separate from customer acceptance.");

        // Set the text displayed by jButtonReject to "Reject Request".
        jButtonReject.setText("Reject Request");

        // Set the text displayed by jButtonApprove to "Approve Selected Quotation".
        jButtonApprove.setText("Approve Selected Quotation");

        // Set the text displayed by jLabelHistory to "Approval history".
        jLabelHistory.setText("Approval history");

        // Give jTableApprovalHistory the supplied model, which holds its choices or table data.
        jTableApprovalHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "History ID", "Previous Status", "New Status", "Changed By", "Date"
            }
        ) {
            // Declare canEdit with type boolean[]. Create an array of boolean values containing the listed entries
            // in order.
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            // Tell the table model whether the indicated cell accepts direct typing; false protects displayed
            // database values.
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                // Return `canEdit[columnIndex]` to the caller.
                return canEdit [columnIndex];
            }
        });
        // Place jTableApprovalHistory inside jScrollPane6 so it can scroll when larger than the available
        // space.
        jScrollPane6.setViewportView(jTableApprovalHistory);

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
                    // Place jScrollPane4 in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
                    // Place jScrollPane5 in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Place jLabelReviewInfo in this layout group; any following size arguments give minimum, preferred
                        // and maximum sizes.
                        .addComponent(jLabelReviewInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        // Place jButtonReject in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonReject)
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        // Place jButtonApprove in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonApprove)
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(24, 24, 24))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave spacing between the group and its enclosing container edge.
                        .addContainerGap()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 666, Short.MAX_VALUE)
                            // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Place jLabelRequest in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jLabelRequest)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(18, 18, 18)
                                // Place jComboBoxRequest in this layout group; any following size arguments give minimum, preferred
                                // and maximum sizes.
                                .addComponent(jComboBoxRequest, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(18, 18, 18)
                                // Place jButtonLoadQuotations in this layout group; any following size arguments give minimum,
                                // preferred and maximum sizes.
                                .addComponent(jButtonLoadQuotations)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jButtonRefresh))
                            // Place jScrollPane6 in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jScrollPane6)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    // Place jLabelRequestDetails in this layout group; any following size arguments give minimum,
                                    // preferred and maximum sizes.
                                    .addComponent(jLabelRequestDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jLabelTitle)
                                    // Place jLabelComments in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jLabelComments)
                                    // Place jLabelItems in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jLabelItems, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    // Place jLabelHistory in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jLabelHistory))
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(0, 0, Short.MAX_VALUE)))))
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
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap()
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
                    // Place jComboBoxRequest in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jComboBoxRequest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jButtonLoadQuotations in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jButtonLoadQuotations)
                    // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonRefresh))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jLabelRequestDetails in this layout group; any following size arguments give minimum,
                // preferred and maximum sizes.
                .addComponent(jLabelRequestDetails)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jLabelItems in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelItems)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane4 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jLabelComments in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelComments)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane5 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelReviewInfo in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jLabelReviewInfo)
                    // Place jButtonReject in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonReject)
                    // Place jButtonApprove in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonApprove))
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(18, 18, 18)
                // Place jLabelHistory in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelHistory)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane6 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(0, 6, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Only this class accesses this field directly. Declare jButtonApprove with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonApprove;
    // Only this class accesses this field directly. Declare jButtonLoadQuotations with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonLoadQuotations;
    // Only this class accesses this field directly. Declare jButtonRefresh with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonRefresh;
    // Only this class accesses this field directly. Declare jButtonReject with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonReject;
    // Only this class accesses this field directly. Declare jComboBoxRequest with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxRequest;
    // Only this class accesses this field directly. Declare jLabelComments with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelComments;
    // Only this class accesses this field directly. Declare jLabelHistory with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelHistory;
    // Only this class accesses this field directly. Declare jLabelItems with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelItems;
    // Only this class accesses this field directly. Declare jLabelRequest with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelRequest;
    // Only this class accesses this field directly. Declare jLabelRequestDetails with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelRequestDetails;
    // Only this class accesses this field directly. Declare jLabelReviewInfo with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelReviewInfo;
    // Only this class accesses this field directly. Declare jLabelTitle with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelTitle;
    // Only this class accesses this field directly. Declare jScrollPane1 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane1;
    // Only this class accesses this field directly. Declare jScrollPane2 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane2;
    // Only this class accesses this field directly. Declare jScrollPane4 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane4;
    // Only this class accesses this field directly. Declare jScrollPane5 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane5;
    // Only this class accesses this field directly. Declare jScrollPane6 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane6;
    // Only this class accesses this field directly. Declare jTableApprovalHistory with type
    // javax.swing.JTable. Java initially uses null for this object reference.
    private javax.swing.JTable jTableApprovalHistory;
    // Only this class accesses this field directly. Declare jTableQuotationItems with type
    // javax.swing.JTable. Java initially uses null for this object reference.
    private javax.swing.JTable jTableQuotationItems;
    // Only this class accesses this field directly. Declare jTableQuotations with type javax.swing.JTable.
    // Java initially uses null for this object reference.
    private javax.swing.JTable jTableQuotations;
    // Only this class accesses this field directly. Declare jTextAreaComments with type
    // javax.swing.JTextArea. Java initially uses null for this object reference.
    private javax.swing.JTextArea jTextAreaComments;
    // Only this class accesses this field directly. Declare jTextAreaRequestDetails with type
    // javax.swing.JTextArea. Java initially uses null for this object reference.
    private javax.swing.JTextArea jTextAreaRequestDetails;
    // End of variables declaration//GEN-END:variables
}
