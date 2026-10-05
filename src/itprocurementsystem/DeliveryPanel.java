// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

/**
 * A JPanel is used because equipment delivery entry belongs inside MainFrame.
 * It groups the related controls without opening another window.
 * MainFrame provides the title bar and navigation for this panel.
 *
 * @author alban-byamugisha
 */
// Define DeliveryPanel as a JPanel: it groups this task's controls inside MainFrame rather than
// opening another window.
public class DeliveryPanel extends javax.swing.JPanel {
    // Keep fulfilment SQL and validation separate from the displayed controls.
    // Only this class accesses this field directly. Declare fulfilment with type FulfilmentDAO. Create a
    // FulfilmentDAO object using the supplied constructor values. The reference or value cannot be
    // reassigned after initialisation.
    private final FulfilmentDAO fulfilment = new FulfilmentDAO();
    // Only this class accesses this field directly. Declare units with type
    // java.util.ArrayList<DeliveryUnit>. Create an initially empty resizable list. The reference or value
    // cannot be reassigned after initialisation.
    private final java.util.ArrayList<DeliveryUnit> units = new java.util.ArrayList<DeliveryUnit>();


    /**
     * Creates new form DeliveryPanel
     */
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public DeliveryPanel() {
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
        jButtonLoadItems.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Load the selected request's saved items for the current quotation or delivery screen.
                try { loadItems(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonAddUnit.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Validate a serial-numbered unit and stage it in the unsaved delivery list without exceeding the
                // outstanding quantity.
                try { addUnit(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonRemoveUnit.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Remove the selected unsaved delivery unit from both the list and table model.
                try { removeUnit(); }
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
                // Discard unsaved received units and clear the delivery fields and tables.
                try { clearDelivery(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonSaveDelivery.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Save each received serial-numbered unit and its inventory row together, preventing excess
                // deliveries.
                try { saveDelivery(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // A different request starts a fresh, unsaved delivery list.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jComboBoxRequest.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Discard unsaved received units and clear the delivery fields and tables.
            public void actionPerformed(java.awt.event.ActionEvent event) { clearDelivery(); }
        });
        // Read current saved records for this screen and refresh its choices, table or count.
        reload();

    }

    // Only approved equipment requests appear in this dropdown.
    // Read current saved records for this screen and refresh its choices, table or count.
    private void reload() {
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Rebuild the dropdown with its prompt followed by the supplied ID-and-description choices.
        // Discard unsaved received units and clear the delivery fields and tables.
        try { FormSupport.choices(jComboBoxRequest, fulfilment.requests("Equipment"), "Select request"); clearDelivery(); }
        // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
        // Show the caught error as a message instead of allowing the event action to fail silently.
        catch (Exception ex) { FormSupport.error(this, ex); }
    }
    // Discard unsaved received units and clear the delivery fields and tables.
    private void clearDelivery() {
        // Remove the entries from units while keeping the same collection object.
        units.clear();
        // Replace the displayed rows using the supplied table model data; keep the existing headings.
        FormSupport.fill(jTableReceivedUnits, new java.util.ArrayList<Object[]>());
        // Replace the displayed rows using the supplied table model data; keep the existing headings.
        FormSupport.fill(jTableRequestItems, new java.util.ArrayList<Object[]>());
        // Set the text displayed by jTextFieldSerialNumber to empty text.
        // Set the text displayed by jTextFieldDeliveryDate to empty text.
        jTextFieldSerialNumber.setText(""); jTextFieldDeliveryDate.setText("");
        // Set the text displayed by jLabelSupplier to "Supplier: Select a request".
        jLabelSupplier.setText("Supplier: Select a request");
    }
    // Load the selected request's saved items for the current quotation or delivery screen.
    private void loadItems() throws java.sql.SQLException {
        // Declare request to hold a whole-number value. Read the database ID from the selected dropdown entry,
        // rejecting its initial prompt.
        int request = FormSupport.choice(jComboBoxRequest);
        // Replace the displayed rows using the supplied table model data; keep the existing headings.
        FormSupport.fill(jTableRequestItems, fulfilment.equipmentItems(request));
        // Set the text displayed by jLabelSupplier to `"Supplier: " + fulfilment.supplier(request,
        // "Equipment")`.
        jLabelSupplier.setText("Supplier: " + fulfilment.supplier(request,"Equipment"));
    }
    // Each unit needs its own serial number even when several units share an item ID.
    // Validate a serial-numbered unit and stage it in the unsaved delivery list without exceeding the
    // outstanding quantity.
    private void addUnit() {
        // Declare item to hold a whole-number value. Read column zero of the selected table model row,
        // rejecting an absent selection.
        int item = FormSupport.selectedId(jTableRequestItems);
        // Declare unit with type DeliveryUnit. Create a DeliveryUnit object using the supplied constructor
        // values.
        DeliveryUnit unit = new DeliveryUnit(item, jTextFieldSerialNumber.getText());
        // Declare staged to hold a whole-number value. Its initial value is 0.
        int staged = 0;
        // Process each entry in units in turn, referring to the current entry as existing.
        for (DeliveryUnit existing : units) {
            // Continue with this branch when
            // (existing.getSerialNumber().equalsIgnoreCase(unit.getSerialNumber())).
            // Stop this operation with an exception: That serial number is already in the list.
            if (existing.getSerialNumber().equalsIgnoreCase(unit.getSerialNumber())) { throw new IllegalArgumentException("That serial number is already in the list."); }
            // Continue with this branch when (existing.getItemId() equals item).
            // Increase staged by one after this item or successful check.
            if (existing.getItemId() == item) { staged++; }
        }
        // Declare remaining to hold a whole-number value. Convert this numeric value to a Java int for use as
        // a record ID or count.
        int remaining = ((Number) FormSupport.cell(jTableRequestItems,4)).intValue();
        // Continue with this branch when (staged is at least remaining).
        // Stop this operation with an exception: All remaining units for this item are already in the list.
        if (staged >= remaining) { throw new IllegalArgumentException("All remaining units for this item are already in the list."); }
        // Append unit to units.
        units.add(unit);
        // Append the supplied values as one table row in column order.
        ((javax.swing.table.DefaultTableModel) jTableReceivedUnits.getModel()).addRow(new Object[] {item,FormSupport.cell(jTableRequestItems,1),unit.getSerialNumber()});
        // Set the text displayed by jTextFieldSerialNumber to empty text.
        jTextFieldSerialNumber.setText("");
    }
    // Remove the selected unsaved delivery unit from both the list and table model.
    private void removeUnit() {
        // Declare row to hold a whole-number value. Read the selected visible row index in
        // jTableReceivedUnits; minus one means no row is selected.
        int row = jTableReceivedUnits.getSelectedRow();
        // Continue with this branch when (row is less than 0).
        // Stop this operation with an exception: Select a received unit first.
        if (row < 0) { throw new IllegalArgumentException("Select a received unit first."); }
        // Set row from the following operation: convert the visible row index to the underlying model index so
        // sorting does not select the wrong record.
        row = jTableReceivedUnits.convertRowIndexToModel(row);
        // Remove entry row from units.
        units.remove(row);
        // Remove row row from the table model so it no longer appears.
        ((javax.swing.table.DefaultTableModel) jTableReceivedUnits.getModel()).removeRow(row);
    }
    // Save each received serial-numbered unit and its inventory row together, preventing excess
    // deliveries.
    private void saveDelivery() throws java.sql.SQLException {
        // Save each received serial-numbered unit and its inventory row together, preventing excess
        // deliveries.
        fulfilment.saveDelivery(FormSupport.choice(jComboBoxRequest), jTextFieldDeliveryDate.getText(), units);
        // Read current saved records for this screen and refresh its choices, table or count.
        reload();
        // Display the supplied message in a Swing dialog, using the parent, title and message style when
        // provided.
        javax.swing.JOptionPane.showMessageDialog(this,"Delivery and inventory saved.");
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
        // Set jComboBoxRequest from the following operation: create a dropdown selection control.
        jComboBoxRequest = new javax.swing.JComboBox<>();
        // Set jLabelRequest from the following operation: create a non-editable text or image display.
        jLabelRequest = new javax.swing.JLabel();
        // Set jButtonLoadItems from the following operation: create a clickable action button.
        jButtonLoadItems = new javax.swing.JButton();
        // Set jButtonRefresh from the following operation: create a clickable action button.
        jButtonRefresh = new javax.swing.JButton();
        // Set jLabelSupplier from the following operation: create a non-editable text or image display.
        jLabelSupplier = new javax.swing.JLabel();
        // Set jLabelDeliveryDate from the following operation: create a non-editable text or image display.
        jLabelDeliveryDate = new javax.swing.JLabel();
        // Set jTextFieldDeliveryDate from the following operation: create a single-line text input.
        jTextFieldDeliveryDate = new javax.swing.JTextField();
        // Set jScrollPane1 from the following operation: create a scrollable viewport for another control.
        jScrollPane1 = new javax.swing.JScrollPane();
        // Set jTableRequestItems from the following operation: create a table displaying rows and columns
        // through a model.
        jTableRequestItems = new javax.swing.JTable();
        // Set jLabelSelectedItemSerialNumber from the following operation: create a non-editable text or image
        // display.
        jLabelSelectedItemSerialNumber = new javax.swing.JLabel();
        // Set jTextFieldSerialNumber from the following operation: create a single-line text input.
        jTextFieldSerialNumber = new javax.swing.JTextField();
        // Set jButtonAddUnit from the following operation: create a clickable action button.
        jButtonAddUnit = new javax.swing.JButton();
        // Set jScrollPane2 from the following operation: create a scrollable viewport for another control.
        jScrollPane2 = new javax.swing.JScrollPane();
        // Set jTableReceivedUnits from the following operation: create a table displaying rows and columns
        // through a model.
        jTableReceivedUnits = new javax.swing.JTable();
        // Set jButtonRemoveUnit from the following operation: create a clickable action button.
        jButtonRemoveUnit = new javax.swing.JButton();
        // Set jLabelHint from the following operation: create a non-editable text or image display.
        jLabelHint = new javax.swing.JLabel();
        // Set jButtonClear from the following operation: create a clickable action button.
        jButtonClear = new javax.swing.JButton();
        // Set jButtonSaveDelivery from the following operation: create a clickable action button.
        jButtonSaveDelivery = new javax.swing.JButton();


        // Set the text displayed by jLabelTitle to "Equipment Deliveries".
        jLabelTitle.setText("Equipment Deliveries");

        // Give jComboBoxRequest the supplied model, which holds its choices or table data.
        jComboBoxRequest.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Request" }));

        // Set the text displayed by jLabelRequest to "Request".
        jLabelRequest.setText("Request");

        // Set the text displayed by jButtonLoadItems to "Load Items".
        jButtonLoadItems.setText("Load Items");

        // Set the text displayed by jButtonRefresh to "Refresh".
        jButtonRefresh.setText("Refresh");

        // Set the text displayed by jLabelSupplier to "Supplier: Select a request".
        jLabelSupplier.setText("Supplier: Select a request");

        // Set the text displayed by jLabelDeliveryDate to "Delivery date (YYYY-MM-DD)".
        jLabelDeliveryDate.setText("Delivery date (YYYY-MM-DD)");

        // Give jTableRequestItems the supplied model, which holds its choices or table data.
        jTableRequestItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item ID", "Description", "Ordered Qty", "Received Qty", "Remaining Qty"
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
        // Place jTableRequestItems inside jScrollPane1 so it can scroll when larger than the available space.
        jScrollPane1.setViewportView(jTableRequestItems);

        // Set the text displayed by jLabelSelectedItemSerialNumber to "Selected Item Serial Number".
        jLabelSelectedItemSerialNumber.setText("Selected Item Serial Number");

        // Set the text displayed by jButtonAddUnit to "Add Unit".
        jButtonAddUnit.setText("Add Unit");

        // Give jTableReceivedUnits the supplied model, which holds its choices or table data.
        jTableReceivedUnits.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item ID", "Description", "Serial Number"
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
        // Place jTableReceivedUnits inside jScrollPane2 so it can scroll when larger than the available space.
        jScrollPane2.setViewportView(jTableReceivedUnits);

        // Set the text displayed by jButtonRemoveUnit to "Remove Selected Unit".
        jButtonRemoveUnit.setText("Remove Selected Unit");

        // Set the text displayed by jLabelHint to "Enter one serial number for each equipment unit received.".
        jLabelHint.setText("Enter one serial number for each equipment unit received.");

        // Set the text displayed by jButtonClear to "Clear".
        jButtonClear.setText("Clear");

        // Set the text displayed by jButtonSaveDelivery to "Save Delivery".
        jButtonSaveDelivery.setText("Save Delivery");

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
                        // Leave spacing between the group and its enclosing container edge.
                        .addContainerGap()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Place jLabelSelectedItemSerialNumber in this layout group; any following size arguments give
                                // minimum, preferred and maximum sizes.
                                .addComponent(jLabelSelectedItemSerialNumber)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                // Place jTextFieldSerialNumber in this layout group; any following size arguments give minimum,
                                // preferred and maximum sizes.
                                .addComponent(jTextFieldSerialNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(34, 34, 34)
                                // Place jButtonAddUnit in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jButtonAddUnit)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(0, 0, Short.MAX_VALUE))
                            // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    // Nest a parallel group so controls share this horizontal or vertical region.
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                                        // maximum sizes.
                                        .addComponent(jLabelTitle)
                                        // Nest a sequential group to place controls one after another.
                                        .addGroup(layout.createSequentialGroup()
                                            // Place jLabelRequest in this layout group; any following size arguments give minimum, preferred and
                                            // maximum sizes.
                                            .addComponent(jLabelRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                            .addGap(18, 18, 18)
                                            // Place jComboBoxRequest in this layout group; any following size arguments give minimum, preferred
                                            // and maximum sizes.
                                            .addComponent(jComboBoxRequest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    // Place jLabelSupplier in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jLabelSupplier))
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    // Nest a sequential group to place controls one after another.
                                    .addGroup(layout.createSequentialGroup()
                                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                        .addGap(39, 39, 39)
                                        // Place jButtonLoadItems in this layout group; any following size arguments give minimum, preferred
                                        // and maximum sizes.
                                        .addComponent(jButtonLoadItems)
                                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                        .addGap(18, 18, 18)
                                        // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                                        // maximum sizes.
                                        .addComponent(jButtonRefresh)
                                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    // Nest a sequential group to place controls one after another.
                                    .addGroup(layout.createSequentialGroup()
                                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        // Place jLabelDeliveryDate in this layout group; any following size arguments give minimum, preferred
                                        // and maximum sizes.
                                        .addComponent(jLabelDeliveryDate)
                                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        // Place jTextFieldDeliveryDate in this layout group; any following size arguments give minimum,
                                        // preferred and maximum sizes.
                                        .addComponent(jTextFieldDeliveryDate))))))
                    // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jScrollPane2)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Place jButtonRemoveUnit in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jButtonRemoveUnit)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Place jLabelHint in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jLabelHint)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(27, 27, 27)
                                // Place jButtonClear in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jButtonClear)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(18, 18, 18)
                                // Place jButtonSaveDelivery in this layout group; any following size arguments give minimum, preferred
                                // and maximum sizes.
                                .addComponent(jButtonSaveDelivery)))
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(0, 40, Short.MAX_VALUE)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jComboBoxRequest in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jComboBoxRequest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jLabelRequest in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelRequest)
                    // Place jButtonLoadItems in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jButtonLoadItems)
                    // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonRefresh))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelSupplier in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelSupplier)
                    // Place jLabelDeliveryDate in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jLabelDeliveryDate)
                    // Place jTextFieldDeliveryDate in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jTextFieldDeliveryDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelSelectedItemSerialNumber in this layout group; any following size arguments give
                    // minimum, preferred and maximum sizes.
                    .addComponent(jLabelSelectedItemSerialNumber)
                    // Place jTextFieldSerialNumber in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jTextFieldSerialNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jButtonAddUnit in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonAddUnit))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Place jButtonRemoveUnit in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jButtonRemoveUnit)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Place jLabelHint in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelHint)
                    // Nest a parallel group so controls share this horizontal or vertical region.
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        // Place jButtonClear in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonClear)
                        // Place jButtonSaveDelivery in this layout group; any following size arguments give minimum, preferred
                        // and maximum sizes.
                        .addComponent(jButtonSaveDelivery)))
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap(14, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Only this class accesses this field directly. Declare jButtonAddUnit with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonAddUnit;
    // Only this class accesses this field directly. Declare jButtonClear with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonClear;
    // Only this class accesses this field directly. Declare jButtonLoadItems with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonLoadItems;
    // Only this class accesses this field directly. Declare jButtonRefresh with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonRefresh;
    // Only this class accesses this field directly. Declare jButtonRemoveUnit with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonRemoveUnit;
    // Only this class accesses this field directly. Declare jButtonSaveDelivery with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonSaveDelivery;
    // Only this class accesses this field directly. Declare jComboBoxRequest with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxRequest;
    // Only this class accesses this field directly. Declare jLabelDeliveryDate with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelDeliveryDate;
    // Only this class accesses this field directly. Declare jLabelHint with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelHint;
    // Only this class accesses this field directly. Declare jLabelRequest with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelRequest;
    // Only this class accesses this field directly. Declare jLabelSelectedItemSerialNumber with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelSelectedItemSerialNumber;
    // Only this class accesses this field directly. Declare jLabelSupplier with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelSupplier;
    // Only this class accesses this field directly. Declare jLabelTitle with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelTitle;
    // Only this class accesses this field directly. Declare jScrollPane1 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane1;
    // Only this class accesses this field directly. Declare jScrollPane2 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane2;
    // Only this class accesses this field directly. Declare jTableReceivedUnits with type
    // javax.swing.JTable. Java initially uses null for this object reference.
    private javax.swing.JTable jTableReceivedUnits;
    // Only this class accesses this field directly. Declare jTableRequestItems with type
    // javax.swing.JTable. Java initially uses null for this object reference.
    private javax.swing.JTable jTableRequestItems;
    // Only this class accesses this field directly. Declare jTextFieldDeliveryDate with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldDeliveryDate;
    // Only this class accesses this field directly. Declare jTextFieldSerialNumber with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldSerialNumber;
    // End of variables declaration//GEN-END:variables
}
