// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// These classes hold supplier objects and allow the application to explain database errors to the user.
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;
// Import JOptionPane for standard Swing message, confirmation and input dialogs.
import javax.swing.JOptionPane;
// Exact decimal arithmetic avoids rounding errors in money totals.
// Import BigDecimal for exact decimal values for prices and totals without floating-point rounding.
import java.math.BigDecimal;
// Import ActionListener for the callback interface for button and dropdown actions.
import java.awt.event.ActionListener;
// Import ActionEvent for details of a button or dropdown action.
import java.awt.event.ActionEvent;
// Import DefaultTableModel for the rows and columns displayed by a Swing table.
import javax.swing.table.DefaultTableModel;

/**
 * A JPanel is used because quotation entry belongs inside the main window.
 * It groups the request, vendor and price controls into one screen.
 * A JPanel does not open a separate window; MainFrame will display it.
 * Extending JPanel gives this form the features of a Swing container.
 *
 * @author alban-byamugisha
 */
// Define QuotationPanel as a JPanel: it groups this task's controls inside MainFrame rather than
// opening another window.
public class QuotationPanel extends javax.swing.JPanel {

    // Keep the IDs with the displayed names so the application can save the correct supplier later.
    // Only this class accesses this field directly. Declare vendors with type ArrayList<Vendor>. Create an
    // initially empty resizable list.
    private ArrayList<Vendor> vendors = new ArrayList<Vendor>();

    // Lists preserve database IDs even though the controls show readable text.
    // Only this class accesses this field directly. Declare requestIds with type ArrayList<Integer>.
    // Create an initially empty resizable list.
    private ArrayList<Integer> requestIds = new ArrayList<Integer>();
    // Only this class accesses this field directly. Declare quotationItems with type
    // ArrayList<QuotationItem>. Create an initially empty resizable list.
    private ArrayList<QuotationItem> quotationItems = new ArrayList<QuotationItem>();
    // Zero means no request's items have been loaded yet.
    // Only this class accesses this field directly. Declare loadedRequestId to hold a whole-number value.
    // Java initially uses zero.
    private int loadedRequestId;

    /**
     * Creates new form QuotationPanel
     */
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public QuotationPanel() {
        // Create the controls and layout arranged in NetBeans Design view.
        // Create the controls and apply the layout stored by NetBeans before reading or changing any of those
        // controls.
        initComponents();
        // Right-click the request control to view its saved supporting documents.
        // Attach a right-click menu that opens supporting documents for the currently selected request.
        AttachmentDAO.addMenu(jComboBoxRequest, new AttachmentDAO.RequestChoice() {
            // Supply the currently selected request ID to the attachment menu without storing an outdated
            // selection.
            // Return loadedRequestId to the caller.
            public int requestId() { return loadedRequestId; }
        });


        // The total will be calculated from item prices; users should not type it.
        // Set whether jTextFieldQuotationTotal allows direct typing using false; its value can still be
        // changed by code.
        jTextFieldQuotationTotal.setEditable(false);
        // Wrap long notes at word boundaries so they remain readable.
        // Control whether jTextAreaSpecs wraps long text onto another line.
        jTextAreaSpecs.setLineWrap(true);
        // Control whether wrapped text breaks at word boundaries rather than between letters.
        jTextAreaSpecs.setWrapStyleWord(true);
        // Correct the visible caption without changing the Design-view layout.
        // Set the text displayed by jLabel1 to "Quotation total:".
        jLabel1.setText("Quotation total:");

        // Read supplier names after the dropdown has been created.
        // Load supplier objects and display their names while retaining the database IDs.
        loadVendors();
        // Refresh the eligible request choices or saved request table for this screen.
        loadRequests();
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonSaveQuotation.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Ask the compiler to verify that this method overrides an inherited method.
            @Override
            // Save an internal supplier quotation and all its item prices together after validating the request.
            public void actionPerformed(ActionEvent event) { saveQuotation(); }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonClear.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Ask the compiler to verify that this method overrides an inherited method.
            @Override
            public void actionPerformed(ActionEvent event) {
                // Confirm before discarding prices the purchaser has entered.
                // Continue with this branch when (JOptionPane.showConfirmDialog(QuotationPanel.this, "Clear this
                // quotation?", "Clear Quotation", JOptionPane.YES_NO_OPTION) equals JOptionPane.YES_OPTION).
                if (JOptionPane.showConfirmDialog(QuotationPanel.this, "Clear this quotation?",
                        "Clear Quotation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    // Reset the quotation screen after confirmation so unsaved item prices are discarded deliberately.
                    clearQuotation();
                }
            }
        });
        // Allow only one table row to be selected at a time for unambiguous update or remove actions.
        jTableQuotationItems.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        // Keep event code outside the generated layout so Design view stays usable.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonLoadItems.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Ask the compiler to verify that this method overrides an inherited method.
            @Override
            // Load the selected request's saved items for the current quotation or delivery screen.
            public void actionPerformed(ActionEvent event) { loadItems(); }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonSetPrice.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Ask the compiler to verify that this method overrides an inherited method.
            @Override
            // Validate the purchaser's supplier unit price for the selected quotation item and refresh calculated
            // totals.
            public void actionPerformed(ActionEvent event) { setSelectedPrice(); }
        });
        // Changing the request invalidates previously loaded items and prices.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jComboBoxRequest.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Ask the compiler to verify that this method overrides an inherited method.
            @Override
            // Clear the quotation item list and model and reset its displayed total.
            public void actionPerformed(ActionEvent event) { resetItems(); }
        });
    }

    // New requests and vendors become available when returning to an empty form.
    // An unfinished quotation is preserved until the user saves or clears it.
    // Reload missing request or vendor choices without discarding an already populated quotation.
    public void refreshChoicesIfEmpty() {
        // Continue with this branch when (quotationItems is empty and
        // jTextAreaSpecs.getText().trim().isEmpty()).
        if (quotationItems.isEmpty() && jTextAreaSpecs.getText().trim().isEmpty()) {
            // Refresh the eligible request choices or saved request table for this screen.
            // Load supplier objects and display their names while retaining the database IDs.
            loadRequests(); loadVendors();
        }
    }

    // Reset only the form; saved database records are not deleted.
    // Reset the quotation screen after confirmation so unsaved item prices are discarded deliberately.
    private void clearQuotation() {
        // Clear the quotation item list and model and reset its displayed total.
        resetItems();
        // Select position 0 in jComboBoxRequest; dropdown positions start at zero.
        jComboBoxRequest.setSelectedIndex(0);
        // Select position 0 in jComboBoxVendor; dropdown positions start at zero.
        jComboBoxVendor.setSelectedIndex(0);
        // Set the text displayed by jTextAreaSpecs to empty text.
        jTextAreaSpecs.setText("");
        // Refresh the eligible request choices or saved request table for this screen.
        // Load supplier objects and display their names while retaining the database IDs.
        loadRequests(); loadVendors();
    }

    // Pass the loaded request ID and item objects to the transaction in the DAO.
    // Save an internal supplier quotation and all its item prices together after validating the request.
    private void saveQuotation() {
        // Declare vendorIndex to hold a whole-number value. Its initial value is
        // `jComboBoxVendor.getSelectedIndex() - 1`.
        int vendorIndex = jComboBoxVendor.getSelectedIndex() - 1;
        // Continue with this branch when (loadedRequestId is at most 0 or vendorIndex is less than 0 or
        // vendorIndex is at least the number of entries in vendors).
        if (loadedRequestId <= 0 || vendorIndex < 0 || vendorIndex >= vendors.size()) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, "Load a request and select a vendor first.");
            // Stop this method here; no further statements in this call are executed.
            return;
        }
        // Set whether jButtonSaveQuotation accepts input or actions using false; false disables it.
        jButtonSaveQuotation.setEnabled(false);
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Declare id to hold a whole-number value. Save an internal supplier quotation and all its item prices
            // together after validating the request.
            int id = new QuotationDAO().saveQuotation(loadedRequestId,
                    vendors.get(vendorIndex).getVendorId(), jTextAreaSpecs.getText().trim(), quotationItems);
            // Clear only after commit succeeds, so a failed attempt keeps the user's work.
            // Reset the quotation screen after confirmation so unsaved item prices are discarded deliberately.
            clearQuotation();
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, "Quotation #" + id + " saved. Request status: Quoted.");
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        } catch (IllegalArgumentException ex) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, ex.getMessage());
        // Handle SQLException ex from the preceding try block so the failure follows the recovery steps below.
        } catch (SQLException ex) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this,
                    "Could not confirm the save. Check the database and saved quotations before retrying. "
                    + "Your entered values have been kept.");
        } finally {
            // Set whether jButtonSaveQuotation accepts input or actions using true; false disables it.
            jButtonSaveQuotation.setEnabled(true);
        }
    }

    // Fill the dropdown with request numbers that still accept quotations.
    // Refresh the eligible request choices or saved request table for this screen.
    private void loadRequests() {
        // Remove the existing choices from jComboBoxRequest before adding a fresh list.
        jComboBoxRequest.removeAllItems();
        // Append "Select request" as a selectable entry in jComboBoxRequest.
        jComboBoxRequest.addItem("Select request");
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Set requestIds from the following operation: list requests still eligible for supplier quotations,
            // after checking Purchaser access in the database.
            requestIds = new QuotationDAO().getOpenRequestIds();
            // Repeat while i is less than the number of entries in requestIds; initialise the counter once and
            // update it after each pass.
            for (int i = 0; i < requestIds.size(); i++) {
                // Append `"Request #" + requestIds.get(i)` as a selectable entry in jComboBoxRequest.
                jComboBoxRequest.addItem("Request #" + requestIds.get(i));
            }
        // Handle SQLException | IllegalArgumentException ex from the preceding try block so the failure
        // follows the recovery steps below.
        } catch (SQLException | IllegalArgumentException ex) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, "Could not load requests: " + ex.getMessage());
        }
    }

    // Remove old prices whenever a different request is chosen or reloaded.
    // Clear the quotation item list and model and reset its displayed total.
    private void resetItems() {
        // Store 0 in loadedRequestId for the remaining steps.
        loadedRequestId = 0;
        // Remove the entries from quotationItems while keeping the same collection object.
        quotationItems.clear();
        // Set ((DefaultTableModel)jTableQuotationItems.getModel()) to 0 rows; zero clears existing rows before
        // reloading.
        ((DefaultTableModel) jTableQuotationItems.getModel()).setRowCount(0);
        // Set the text displayed by jTextFieldQuotationTotal to "0.00".
        jTextFieldQuotationTotal.setText("0.00");
        // Set the text displayed by jTextFieldUnitPrice to "0.00".
        jTextFieldUnitPrice.setText("0.00");
    }

    // Read the chosen request's original items; quantities cannot be edited here.
    // Load the selected request's saved items for the current quotation or delivery screen.
    private void loadItems() {
        // Declare index to hold a whole-number value. Its initial value is
        // `jComboBoxRequest.getSelectedIndex() - 1`.
        int index = jComboBoxRequest.getSelectedIndex() - 1;
        // Continue with this branch when (index is less than 0 or index is at least the number of entries in
        // requestIds).
        if (index < 0 || index >= requestIds.size()) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, "Select a request first.");
            // Stop this method here; no further statements in this call are executed.
            return;
        }
        // Continue with this branch when (not quotationItems is empty and JOptionPane.showConfirmDialog(this,
        // "Reload items and discard entered prices?", "Reload Items", JOptionPane.YES_NO_OPTION) is not equal
        // to JOptionPane.YES_OPTION).
        if (!quotationItems.isEmpty() && JOptionPane.showConfirmDialog(this,
                "Reload items and discard entered prices?", "Reload Items",
                // Stop this method here; no further statements in this call are executed.
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { return; }
        // Clear the quotation item list and model and reset its displayed total.
        resetItems();
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Declare requestId to hold a whole-number value. Read entry index from requestIds; list positions
            // start at zero.
            int requestId = requestIds.get(index);
            // Set quotationItems from the following operation: read the selected request's descriptions and
            // quantities into quotation items with prices initially unset.
            quotationItems = new QuotationDAO().getRequestItems(requestId);
            // Declare model with type DefaultTableModel. Read the model holding
            // (DefaultTableModel)jTableQuotationItems data, rather than the control's visual appearance.
            DefaultTableModel model = (DefaultTableModel) jTableQuotationItems.getModel();
            // Repeat while i is less than the number of entries in quotationItems; initialise the counter once and
            // update it after each pass.
            for (int i = 0; i < quotationItems.size(); i++) {
                // Declare item with type QuotationItem. Read entry i from quotationItems; list positions start at
                // zero.
                QuotationItem item = quotationItems.get(i);
                // Append the supplied values as one table row in column order.
                model.addRow(new Object[] {item.getDescription(), item.getQuantity(), null, null});
            }
            // Continue with this branch when (quotationItems is empty).
            if (quotationItems.isEmpty()) {
                // Display the supplied message in a Swing dialog, using the parent, title and message style when
                // provided.
                JOptionPane.showMessageDialog(this, "No eligible items found. The request may have changed status.");
            // Store requestId in loadedRequestId for the remaining steps.
            } else { loadedRequestId = requestId; }
        // Handle SQLException | IllegalArgumentException ex from the preceding try block so the failure
        // follows the recovery steps below.
        } catch (SQLException | IllegalArgumentException ex) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, "Could not load items: " + ex.getMessage());
        }
    }

    // Apply a price to one selected item and recalculate every line's total.
    // Validate the purchaser's supplier unit price for the selected quotation item and refresh calculated
    // totals.
    private void setSelectedPrice() {
        // Declare row to hold a whole-number value. Read the selected visible row index in
        // jTableQuotationItems; minus one means no row is selected.
        int row = jTableQuotationItems.getSelectedRow();
        // Continue with this branch when (row is less than 0).
        if (row < 0) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, "Select an item row first.");
            // Stop this method here; no further statements in this call are executed.
            return;
        }
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Declare index to hold a whole-number value. Convert the visible row index to the underlying model
            // index so sorting does not select the wrong record.
            int index = jTableQuotationItems.convertRowIndexToModel(row);
            // Declare item with type QuotationItem. Read entry index from quotationItems; list positions start at
            // zero.
            QuotationItem item = quotationItems.get(index);
            // Validate and store the supplier unit price in the selected quotation item.
            item.setUnitPrice(new BigDecimal(jTextFieldUnitPrice.getText().trim()));
            // Declare model with type DefaultTableModel. Read the model holding
            // (DefaultTableModel)jTableQuotationItems data, rather than the control's visual appearance.
            DefaultTableModel model = (DefaultTableModel) jTableQuotationItems.getModel();
            // Replace the stated table-model cell using its row and column indices; both indices start at zero.
            model.setValueAt(item.getUnitPrice(), index, 2);
            // Replace the stated table-model cell using its row and column indices; both indices start at zero.
            model.setValueAt(item.getLineTotal(), index, 3);
            // Declare total to hold an exact decimal amount. Create an exact decimal value from the supplied
            // number or text.
            BigDecimal total = new BigDecimal("0.00");
            // Repeat while i is less than the number of entries in quotationItems; initialise the counter once and
            // update it after each pass.
            for (int i = 0; i < quotationItems.size(); i++) {
                // Set total from the following operation: add the supplied amount to total and return the new
                // BigDecimal value.
                total = total.add(quotationItems.get(i).getLineTotal());
            }
            // Set the text displayed by jTextFieldQuotationTotal to `total.toPlainString()`.
            jTextFieldQuotationTotal.setText(total.toPlainString());
        // Handle NumberFormatException ex from the preceding try block so the failure follows the recovery
        // steps below.
        } catch (NumberFormatException ex) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, "Enter a numeric price, for example 1250.50.");
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        } catch (IllegalArgumentException ex) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Fill the vendor dropdown using the supplier records stored in MySQL.
    // Load supplier objects and display their names while retaining the database IDs.
    private void loadVendors() {
        // Remove the entries from vendors while keeping the same collection object.
        vendors.clear();
        // Remove the existing choices from jComboBoxVendor before adding a fresh list.
        jComboBoxVendor.removeAllItems();
        // This first entry is an instruction, not a valid supplier to save.
        // Append "Select vendor" as a selectable entry in jComboBoxVendor.
        jComboBoxVendor.addItem("Select vendor");
        // Set whether jComboBoxVendor accepts input or actions using false; false disables it.
        jComboBoxVendor.setEnabled(false);
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Declare vendorDAO with type VendorDAO. Create a VendorDAO object using the supplied constructor
            // values.
            VendorDAO vendorDAO = new VendorDAO();
            // Set vendors from the following operation: read supplier IDs and names so the form can display names
            // while saving the correct foreign key.
            vendors = vendorDAO.getAllVendors();
            // Keep the names in the same order as the objects containing their IDs.
            // Dropdown index 1 corresponds to list index 0 because of the first entry.
            // Repeat while i is less than the number of entries in vendors; initialise the counter once and update
            // it after each pass.
            for (int i = 0; i < vendors.size(); i++) {
                // Append `vendors.get(i).getVendorName()` as a selectable entry in jComboBoxVendor.
                jComboBoxVendor.addItem(vendors.get(i).getVendorName());
            }
            // Continue with this branch when (vendors is empty).
            if (vendors.isEmpty()) {
                // Display the supplied message in a Swing dialog, using the parent, title and message style when
                // provided.
                JOptionPane.showMessageDialog(this,
                        "No suppliers are saved yet. Add suppliers before recording a quotation.",
                        "Vendors", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Set whether jComboBoxVendor accepts input or actions using true; false disables it.
                jComboBoxVendor.setEnabled(true);
            }
        // Handle SQLException ex from the preceding try block so the failure follows the recovery steps below.
        } catch (SQLException ex) {
            // A disabled dropdown prevents using an incomplete supplier list.
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this,
                    "Could not load suppliers. Check that MySQL is running and reopen the screen.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
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
        // Set jLabelVendor from the following operation: create a non-editable text or image display.
        jLabelVendor = new javax.swing.JLabel();
        // Set jComboBoxRequest from the following operation: create a dropdown selection control.
        jComboBoxRequest = new javax.swing.JComboBox<>();
        // Set jComboBoxVendor from the following operation: create a dropdown selection control.
        jComboBoxVendor = new javax.swing.JComboBox<>();
        // Set jButtonLoadItems from the following operation: create a clickable action button.
        jButtonLoadItems = new javax.swing.JButton();
        // Set jScrollPane1 from the following operation: create a scrollable viewport for another control.
        jScrollPane1 = new javax.swing.JScrollPane();
        // Set jTableQuotationItems from the following operation: create a table displaying rows and columns
        // through a model.
        jTableQuotationItems = new javax.swing.JTable();
        // Set jLabelUnitPrice from the following operation: create a non-editable text or image display.
        jLabelUnitPrice = new javax.swing.JLabel();
        // Set jTextFieldUnitPrice from the following operation: create a single-line text input.
        jTextFieldUnitPrice = new javax.swing.JTextField();
        // Set jButtonSetPrice from the following operation: create a clickable action button.
        jButtonSetPrice = new javax.swing.JButton();
        // Set jLabelSpecs from the following operation: create a non-editable text or image display.
        jLabelSpecs = new javax.swing.JLabel();
        // Set jScrollPane2 from the following operation: create a scrollable viewport for another control.
        jScrollPane2 = new javax.swing.JScrollPane();
        // Set jTextAreaSpecs from the following operation: create a multi-line text display or input.
        jTextAreaSpecs = new javax.swing.JTextArea();
        // Set jButtonClear from the following operation: create a clickable action button.
        jButtonClear = new javax.swing.JButton();
        // Set jButtonSaveQuotation from the following operation: create a clickable action button.
        jButtonSaveQuotation = new javax.swing.JButton();
        // Set jTextFieldQuotationTotal from the following operation: create a single-line text input.
        jTextFieldQuotationTotal = new javax.swing.JTextField();
        // Set jLabel1 from the following operation: create a non-editable text or image display.
        jLabel1 = new javax.swing.JLabel();

        // Set the text displayed by jLabelTitle to "Record Quotation".
        jLabelTitle.setText("Record Quotation");

        // Set the text displayed by jLabelRequest to "Request".
        jLabelRequest.setText("Request");

        // Set the text displayed by jLabelVendor to "Vendor".
        jLabelVendor.setText("Vendor");

        // Give jComboBoxRequest the supplied model, which holds its choices or table data.
        jComboBoxRequest.setModel(new javax.swing.DefaultComboBoxModel<String>(
            new String[] {"Select request"}
        ));

        // Give jComboBoxVendor the supplied model, which holds its choices or table data.
        jComboBoxVendor.setModel(new javax.swing.DefaultComboBoxModel<String>(
            new String[] {"Select vendor"}
        ));

        // Set the text displayed by jButtonLoadItems to "Load Items".
        jButtonLoadItems.setText("Load Items");

        // Give jTableQuotationItems the supplied model, which holds its choices or table data.
        jTableQuotationItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item Description", "Quantity", "Unit Price", "Line Total"
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
        // Place jTableQuotationItems inside jScrollPane1 so it can scroll when larger than the available
        // space.
        jScrollPane1.setViewportView(jTableQuotationItems);

        // Set the text displayed by jLabelUnitPrice to "Unit Price of Selected Item".
        jLabelUnitPrice.setText("Unit Price of Selected Item");

        // Set the text displayed by jTextFieldUnitPrice to "0.00".
        jTextFieldUnitPrice.setText("0.00");

        // Set the text displayed by jButtonSetPrice to "Set Price".
        jButtonSetPrice.setText("Set Price");

        // Set the text displayed by jLabelSpecs to "Specifications / notes (optional)".
        jLabelSpecs.setText("Specifications / notes (optional)");

        // Control whether wrapped text breaks at word boundaries rather than between letters.
        jTextAreaSpecs.setWrapStyleWord(true);
        // Control whether jTextAreaSpecs wraps long text onto another line.
        jTextAreaSpecs.setLineWrap(true);
        // Set the preferred text width of jTextAreaSpecs to 20 columns; this is not a text-length limit.
        jTextAreaSpecs.setColumns(20);
        // Set the preferred visible height of jTextAreaSpecs to 5 text rows.
        jTextAreaSpecs.setRows(5);
        // Place jTextAreaSpecs inside jScrollPane2 so it can scroll when larger than the available space.
        jScrollPane2.setViewportView(jTextAreaSpecs);

        // Set the text displayed by jButtonClear to "Clear".
        jButtonClear.setText("Clear");

        // Set the text displayed by jButtonSaveQuotation to "Save Quotation".
        jButtonSaveQuotation.setText("Save Quotation");

        // Set whether jTextFieldQuotationTotal allows direct typing using false; its value can still be
        // changed by code.
        jTextFieldQuotationTotal.setEditable(false);
        // Set the text displayed by jTextFieldQuotationTotal to "0.00".
        jTextFieldQuotationTotal.setText("0.00");

        // Set the text displayed by jLabel1 to "Quotation total:".
        jLabel1.setText("Quotation total:");

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
                        .addGap(132, 132, 132)
                        // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jLabelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave spacing between the group and its enclosing container edge.
                        .addContainerGap()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Place jLabelSpecs in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jLabelSpecs)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(0, 0, Short.MAX_VALUE))
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Place jLabelUnitPrice in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jLabelUnitPrice)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                // Place jTextFieldUnitPrice in this layout group; any following size arguments give minimum, preferred
                                // and maximum sizes.
                                .addComponent(jTextFieldUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                // Place jButtonSetPrice in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jButtonSetPrice)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(30, 30, 30)
                                // Place jLabel1 in this layout group; any following size arguments give minimum, preferred and maximum
                                // sizes.
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                // Place jTextFieldQuotationTotal in this layout group; any following size arguments give minimum,
                                // preferred and maximum sizes.
                                .addComponent(jTextFieldQuotationTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave spacing between the group and its enclosing container edge.
                        .addContainerGap()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    // Place jLabelVendor in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jLabelVendor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    // Place jLabelRequest in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jLabelRequest, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(18, 18, 18)
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    // Place jComboBoxVendor in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jComboBoxVendor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    // Place jComboBoxRequest in this layout group; any following size arguments give minimum, preferred
                                    // and maximum sizes.
                                    .addComponent(jComboBoxRequest, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(18, 18, 18)
                                // Place jButtonLoadItems in this layout group; any following size arguments give minimum, preferred
                                // and maximum sizes.
                                .addComponent(jButtonLoadItems))
                            // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jScrollPane2)))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(0, 0, Short.MAX_VALUE)
                        // Place jButtonClear in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonClear)
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(18, 18, 18)
                        // Place jButtonSaveQuotation in this layout group; any following size arguments give minimum,
                        // preferred and maximum sizes.
                        .addComponent(jButtonSaveQuotation)))
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
                    // Place jButtonLoadItems in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jButtonLoadItems))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelVendor in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelVendor)
                    // Place jComboBoxVendor in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jComboBoxVendor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Place jLabelUnitPrice in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelUnitPrice)
                    // Nest a parallel group so controls share this horizontal or vertical region.
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        // Place jTextFieldUnitPrice in this layout group; any following size arguments give minimum, preferred
                        // and maximum sizes.
                        .addComponent(jTextFieldUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        // Place jButtonSetPrice in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonSetPrice)
                        // Place jTextFieldQuotationTotal in this layout group; any following size arguments give minimum,
                        // preferred and maximum sizes.
                        .addComponent(jTextFieldQuotationTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        // Place jLabel1 in this layout group; any following size arguments give minimum, preferred and maximum
                        // sizes.
                        .addComponent(jLabel1)))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jLabelSpecs in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelSpecs)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(2, 2, 2)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jButtonClear in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonClear)
                    // Place jButtonSaveQuotation in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jButtonSaveQuotation))
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Only this class accesses this field directly. Declare jButtonClear with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonClear;
    // Only this class accesses this field directly. Declare jButtonLoadItems with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonLoadItems;
    // Only this class accesses this field directly. Declare jButtonSaveQuotation with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonSaveQuotation;
    // Only this class accesses this field directly. Declare jButtonSetPrice with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonSetPrice;
    // Only this class accesses this field directly. Declare jComboBoxRequest with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxRequest;
    // Only this class accesses this field directly. Declare jComboBoxVendor with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxVendor;
    // Only this class accesses this field directly. Declare jLabel1 with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabel1;
    // Only this class accesses this field directly. Declare jLabelRequest with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelRequest;
    // Only this class accesses this field directly. Declare jLabelSpecs with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelSpecs;
    // Only this class accesses this field directly. Declare jLabelTitle with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelTitle;
    // Only this class accesses this field directly. Declare jLabelUnitPrice with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelUnitPrice;
    // Only this class accesses this field directly. Declare jLabelVendor with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelVendor;
    // Only this class accesses this field directly. Declare jScrollPane1 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane1;
    // Only this class accesses this field directly. Declare jScrollPane2 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane2;
    // Only this class accesses this field directly. Declare jTableQuotationItems with type
    // javax.swing.JTable. Java initially uses null for this object reference.
    private javax.swing.JTable jTableQuotationItems;
    // Only this class accesses this field directly. Declare jTextAreaSpecs with type
    // javax.swing.JTextArea. Java initially uses null for this object reference.
    private javax.swing.JTextArea jTextAreaSpecs;
    // Only this class accesses this field directly. Declare jTextFieldQuotationTotal with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldQuotationTotal;
    // Only this class accesses this field directly. Declare jTextFieldUnitPrice with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldUnitPrice;
    // End of variables declaration//GEN-END:variables
}
