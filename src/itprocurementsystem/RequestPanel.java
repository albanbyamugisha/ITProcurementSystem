// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// These classes help the application keep category objects and report database problems.
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;
// Import JOptionPane for standard Swing message, confirmation and input dialogs.
import javax.swing.JOptionPane;
// These classes handle money, button clicks and rows in the table.
// Import BigDecimal for exact decimal values for prices and totals without floating-point rounding.
import java.math.BigDecimal;
// Import ActionListener for the callback interface for button and dropdown actions.
import java.awt.event.ActionListener;
// Import ActionEvent for details of a button or dropdown action.
import java.awt.event.ActionEvent;
// Import DefaultTableModel for the rows and columns displayed by a Swing table.
import javax.swing.table.DefaultTableModel;

/**
 * A JPanel is used because the request form will sit inside MainFrame.
 * A panel groups related controls, such as the request fields and buttons.
 * It does not create a separate window or have its own title bar.
 * Extending JPanel allows the application to build this form as a reusable part of the main window.
 *
 * @author alban-byamugisha
 */
// Define RequestPanel as a JPanel: it groups this task's controls inside MainFrame rather than opening
// another window.
public class RequestPanel extends javax.swing.JPanel {
    // Files remain pending until the complete request is saved.
    // Only this class accesses this field directly. Declare attachments with type ArrayList<java.io.File>.
    // Create an initially empty resizable list. The reference or value cannot be reassigned after
    // initialisation.
    private final ArrayList<java.io.File> attachments = new ArrayList<java.io.File>();

    // Keep the category IDs as well as the names displayed by the String combo box.
    // Only this class accesses this field directly. Declare catalogue with type ArrayList<Object[]>.
    // Create an initially empty resizable list.
    private ArrayList<Object[]> catalogue = new ArrayList<Object[]>();
    // Only this class accesses this field directly. Declare categories with type ArrayList<Category>.
    // Create an initially empty resizable list.
    private ArrayList<Category> categories = new ArrayList<Category>();

    // Store the actual items, including category IDs, until the request is submitted.
    // Only this class accesses this field directly. Declare requestItems with type ArrayList<RequestItem>.
    // Create an initially empty resizable list.
    private ArrayList<RequestItem> requestItems = new ArrayList<RequestItem>();

    /**
     * Creates new form RequestPanel
     */
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public RequestPanel() {
        // Create the controls and layout arranged in NetBeans Design view.
        // Create the controls and apply the layout stored by NetBeans before reading or changing any of those
        // controls.
        initComponents();
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonChooseFile.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Let the requester select and validate a supporting document before staging it with the unsaved
                // request.
                try { chooseFile(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonRemoveFile.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Remove a selected staged attachment without deleting the original file from the computer.
                try { removeFile(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });


        // Fill the dropdown from MySQL after NetBeans has created its controls.
        // Load category objects so the screen can show readable names and retain their database IDs.
        loadCategories();
        // Set whether jComboBoxCategory accepts input or actions using false; false disables it.
        jComboBoxCategory.setEnabled(false);
        // Set whether jTextFieldDescription allows direct typing using false; its value can still be changed
        // by code.
        jTextFieldDescription.setEditable(false);
        // Set whether jTextFieldUnitCost allows direct typing using false; its value can still be changed by
        // code.
        jTextFieldUnitCost.setEditable(false);
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jComboBoxRequestType.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Load active catalogue items matching the selected request type and rebuild the dropdown.
            public void actionPerformed(ActionEvent event) { loadCatalogue(); }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jComboBoxCatalogueItem.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Display the chosen catalogue item's category, specification, unit and preset selling price.
            public void actionPerformed(ActionEvent event) { showCatalogueItem(); }
        });
        // Load active catalogue items matching the selected request type and rebuild the dropdown.
        loadCatalogue();

        // Connect the button outside the layout code maintained by NetBeans.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonAddItem.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Ask the compiler to verify that this method overrides an inherited method.
            @Override
            public void actionPerformed(ActionEvent event) {
                // Run these steps whenever the user clicks Add Item.
                // Create a validated item from the selected catalogue entry and quantity, then update the unsaved
                // table and total.
                addItem();
            }
        });

        // Send the whole request to MySQL when Submit Request is clicked.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonSubmit.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Ask the compiler to verify that this method overrides an inherited method.
            @Override
            public void actionPerformed(ActionEvent event) {
                // Read and validate the request form, save all items and attachments, then reset it after success.
                submitRequest();
            }
        });

        // One selected row makes it clear which item Remove Selected will remove.
        // Allow only one table row to be selected at a time for unambiguous update or remove actions.
        jTableItems.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonRemoveItem.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Ask the compiler to verify that this method overrides an inherited method.
            @Override
            public void actionPerformed(ActionEvent event) {
                // Remove the selected unsaved request item from the list and table and recalculate the total.
                removeSelectedItem();
            }
        });

        // Clear resets the unsaved request only after the user confirms.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonClear.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Ask the compiler to verify that this method overrides an inherited method.
            @Override
            public void actionPerformed(ActionEvent event) {
                // Declare answer to hold a whole-number value. Ask for confirmation and return the selected option so
                // the caller can decide whether to continue.
                int answer = JOptionPane.showConfirmDialog(RequestPanel.this,
                        "Clear all items, notes and entered values in this request?",
                        "Clear Request", JOptionPane.YES_NO_OPTION);
                // No or closing the dialog leaves the request unchanged.
                // Continue with this branch when (answer equals JOptionPane.YES_OPTION).
                if (answer == JOptionPane.YES_OPTION) {
                    // Reset unsaved request fields, items, attachments and totals to their initial state.
                    clearRequest();
                }
            }
        });
    }

    // Reload after choosing Equipment or Service; customers see active entries only.
    // Load active catalogue items matching the selected request type and rebuild the dropdown.
    private void loadCatalogue() {
        // Remove the existing choices from jComboBoxCatalogueItem before adding a fresh list.
        jComboBoxCatalogueItem.removeAllItems();
        // Append "Select product/service" as a selectable entry in jComboBoxCatalogueItem.
        jComboBoxCatalogueItem.addItem("Select product/service");
        // Remove the entries from catalogue while keeping the same collection object.
        catalogue.clear();
        // Continue with this branch when (jComboBoxRequestType.getSelectedIndex() is at most 0).
        // Stop this method here; no further statements in this call are executed.
        if (jComboBoxRequestType.getSelectedIndex() <= 0) { return; }
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Set catalogue from the following operation: search catalogue names or descriptions, showing inactive
            // entries only to an Admin.
            catalogue = new CatalogueDAO().list("", (String)jComboBoxRequestType.getSelectedItem());
            // Process each entry in catalogue in turn, referring to the current entry as row.
            // Append `row[1] + " \u2014 " + row[4]` as a selectable entry in jComboBoxCatalogueItem.
            for (Object[] row : catalogue) { jComboBoxCatalogueItem.addItem(row[1] + " — " + row[4]); }
        // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
        // Show the caught error as a message instead of allowing the event action to fail silently.
        } catch (Exception ex) { FormSupport.error(this,ex); }
    }
    // Display the chosen catalogue item's category, specification, unit and preset selling price.
    private void showCatalogueItem() {
        // Declare index to hold a whole-number value. Its initial value is
        // `jComboBoxCatalogueItem.getSelectedIndex() - 1`.
        int index = jComboBoxCatalogueItem.getSelectedIndex()-1;
        // Continue with this branch when (index is less than 0 or index is at least the number of entries in
        // catalogue).
        if (index < 0 || index >= catalogue.size()) {
            // Set the text displayed by jTextFieldDescription to empty text.
            // Set the text displayed by jTextFieldUnitCost to "0.00".
            jTextFieldDescription.setText(""); jTextFieldUnitCost.setText("0.00");
            // Continue with this branch when (jComboBoxCategory.getItemCount() is greater than 0).
            // Select position 0 in jComboBoxCategory; dropdown positions start at zero.
            if (jComboBoxCategory.getItemCount() > 0) { jComboBoxCategory.setSelectedIndex(0); }
            // Stop this method here; no further statements in this call are executed.
            return;
        }
        // Declare row to hold one row of values in SELECT or table-column order. Read entry index from
        // catalogue; list positions start at zero.
        Object[] row = catalogue.get(index);
        // Set the text displayed by jTextFieldDescription to `row[7].toString()`.
        jTextFieldDescription.setText(row[7].toString());
        // Set the text displayed by jTextFieldUnitCost to `row[5].toString()`.
        jTextFieldUnitCost.setText(row[5].toString());
        // Repeat while i is less than the number of entries in categories; initialise the counter once and
        // update it after each pass.
        for (int i=0; i<categories.size(); i++) {
            // Continue with this branch when (categories.get(i).getCategoryId() equals Database.id(row[8])).
            // Select position i + 1 in jComboBoxCategory; dropdown positions start at zero.
            // Leave the current loop once the required result has been found.
            if (categories.get(i).getCategoryId() == Database.id(row[8])) { jComboBoxCategory.setSelectedIndex(i+1); break; }
        }
    }

    // Let the user choose supporting documents without changing the original files.
    // Let the requester select and validate a supporting document before staging it with the unsaved
    // request.
    private void chooseFile() {
        // Continue with this branch when (the number of entries in attachments is at least 10).
        // Stop this operation with an exception: Choose at most 10 attachments.
        if (attachments.size() >= 10) { throw new IllegalArgumentException("Choose at most 10 attachments."); }
        // Declare chooser with type javax.swing.JFileChooser. Create a JFileChooser object using the supplied
        // constructor values.
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        // Limit the file chooser display to the supported document and image extensions.
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Supporting documents", "pdf", "png", "jpg", "jpeg", "txt", "docx", "xlsx"));
        // Continue with this branch when (chooser.showOpenDialog(this) is not equal to
        // javax.swing.JFileChooser.APPROVE_OPTION).
        // Stop this method here; no further statements in this call are executed.
        if (chooser.showOpenDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) { return; }
        // Declare file with type java.io.File. Its initial value is
        // `chooser.getSelectedFile().getAbsoluteFile()`.
        java.io.File file = chooser.getSelectedFile().getAbsoluteFile();
        // Check the file type, readability, name length and size before accepting or opening it.
        AttachmentDAO.validate(file);
        // Continue with this branch when (attachments.contains(file)).
        // Stop this operation with an exception: That file has already been added.
        if (attachments.contains(file)) { throw new IllegalArgumentException("That file has already been added."); }
        // Append file to attachments.
        attachments.add(file);
        // Append the supplied values as one table row in column order.
        ((DefaultTableModel) jTableAttachments.getModel()).addRow(new Object[] {file.getName(),file.getAbsolutePath()});
    }
    // Remove a selected staged attachment without deleting the original file from the computer.
    private void removeFile() {
        // Declare row to hold a whole-number value. Read the selected visible row index in jTableAttachments;
        // minus one means no row is selected.
        int row = jTableAttachments.getSelectedRow();
        // Continue with this branch when (row is less than 0).
        // Stop this operation with an exception: Select a file first.
        if (row < 0) { throw new IllegalArgumentException("Select a file first."); }
        // Set row from the following operation: convert the visible row index to the underlying model index so
        // sorting does not select the wrong record.
        row = jTableAttachments.convertRowIndexToModel(row);
        // Remove entry row from attachments.
        attachments.remove(row);
        // Remove row row from the table model so it no longer appears.
        ((DefaultTableModel) jTableAttachments.getModel()).removeRow(row);
    }

    // Save the complete request only when its items have been added to the table.
    // Read and validate the request form, save all items and attachments, then reset it after success.
    private void submitRequest() {
        // Continue with this branch when (jComboBoxRequestType.getSelectedIndex() is at most 0).
        if (jComboBoxRequestType.getSelectedIndex() <= 0) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, "Select a request type first.");
            // Stop this method here; no further statements in this call are executed.
            return;
        }
        // A logged-out user must log in again before submitting a request.
        // Check the session identity before continuing so a missing or different account follows this branch.
        if (Session.getUserId() == 0 || !"Requester".equals(Session.getRole())) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, "Please log in as a requester first.");
            // Stop this method here; no further statements in this call are executed.
            return;
        }
        // Continue with this branch when (requestItems is empty).
        if (requestItems.isEmpty()) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, "Add at least one item before submitting.");
            // Stop this method here; no further statements in this call are executed.
            return;
        }
        // Avoid silently losing an item that was typed but not added to the table.
        // Continue with this branch when (jComboBoxCategory.getSelectedIndex() is greater than 0 or not
        // jTextFieldDescription.getText().trim().isEmpty() or !"1".equals(jTextFieldQuantity.getText().trim())
        // or !"0.00".equals(jTextFieldUnitCost.getText().trim())).
        if (jComboBoxCategory.getSelectedIndex() > 0
                || !jTextFieldDescription.getText().trim().isEmpty()
                || !"1".equals(jTextFieldQuantity.getText().trim())
                || !"0.00".equals(jTextFieldUnitCost.getText().trim())) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this,
                    "You have unfinished item fields. Add the item or reset those fields first.");
            // Stop this method here; no further statements in this call are executed.
            return;
        }

        // Disable this button while saving to avoid a second submission.
        // Set whether jButtonSubmit accepts input or actions using false; false disables it.
        jButtonSubmit.setEnabled(false);
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Declare requestDAO with type RequestDAO. Create a RequestDAO object using the supplied constructor
            // values.
            RequestDAO requestDAO = new RequestDAO();
            // Declare requestId to hold a whole-number value. Validate and save a request, its fixed-price items
            // and optional attachment copies as one operation.
            int requestId = requestDAO.saveRequest(Session.getUserId(),
                    jTextAreaNotes.getText(), requestItems,
                    (String) jComboBoxRequestType.getSelectedItem(), attachments);
            // Clear only after saving succeeds. Failed attempts keep the entered data.
            // Reset unsaved request fields, items, attachments and totals to their initial state.
            clearRequest();
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this,
                    "Request #" + requestId + " submitted successfully. Status: Pending.",
                    "Request Saved", JOptionPane.INFORMATION_MESSAGE);
        // Handle SQLException ex from the preceding try block so the failure follows the recovery steps below.
        } catch (SQLException ex) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this,
                    "Could not confirm the save. Your form has been kept. "
                    + "Check the database connection and saved requests before trying again.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        } catch (IllegalArgumentException ex) {
            // Show the validation message supplied by the data access class.
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Check Request", JOptionPane.WARNING_MESSAGE);
        } finally {
            // finally runs whether saving succeeded or an error occurred.
            // Set whether jButtonSubmit accepts input or actions using true; false disables it.
            jButtonSubmit.setEnabled(true);
        }
    }

    // Remove the selected item from both the object list and the visible table.
    // Remove the selected unsaved request item from the list and table and recalculate the total.
    private void removeSelectedItem() {
        // Declare selectedRow to hold a whole-number value. Read the selected visible row index in
        // jTableItems; minus one means no row is selected.
        int selectedRow = jTableItems.getSelectedRow();
        // A value of -1 means the user has not selected a row.
        // Continue with this branch when (selectedRow equals -1).
        if (selectedRow == -1) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, "Select an item in the table first.");
            // Stop this method here; no further statements in this call are executed.
            return;
        }
        // Convert the displayed row number to its data position if sorting is used later.
        // Declare itemIndex to hold a whole-number value. Convert the visible row index to the underlying
        // model index so sorting does not select the wrong record.
        int itemIndex = jTableItems.convertRowIndexToModel(selectedRow);
        // Remove entry itemIndex from requestItems.
        requestItems.remove(itemIndex);
        // Set whether jComboBoxRequestType accepts input or actions using `requestItems.isEmpty()`; false
        // disables it.
        jComboBoxRequestType.setEnabled(requestItems.isEmpty());
        // Declare model with type DefaultTableModel. Read the model holding (DefaultTableModel)jTableItems
        // data, rather than the control's visual appearance.
        DefaultTableModel model = (DefaultTableModel) jTableItems.getModel();
        // Remove row itemIndex from the table model so it no longer appears.
        model.removeRow(itemIndex);
        // Add the staged request-item totals using BigDecimal and show the total with two decimal places.
        updateTotal();
    }

    // Reset the unsaved request. This method does not delete anything from MySQL.
    // Reset unsaved request fields, items, attachments and totals to their initial state.
    private void clearRequest() {
        // Remove the entries from attachments while keeping the same collection object.
        attachments.clear();
        // Set ((DefaultTableModel)jTableAttachments.getModel()) to 0 rows; zero clears existing rows before
        // reloading.
        ((DefaultTableModel) jTableAttachments.getModel()).setRowCount(0);
        // Remove the entries from requestItems while keeping the same collection object.
        requestItems.clear();
        // Set whether jComboBoxRequestType accepts input or actions using true; false disables it.
        jComboBoxRequestType.setEnabled(true);
        // Select position 0 in jComboBoxRequestType; dropdown positions start at zero.
        jComboBoxRequestType.setSelectedIndex(0);
        // Setting the row count to zero removes every visible item row.
        // Declare model with type DefaultTableModel. Read the model holding (DefaultTableModel)jTableItems
        // data, rather than the control's visual appearance.
        DefaultTableModel model = (DefaultTableModel) jTableItems.getModel();
        // Set model to 0 rows; zero clears existing rows before reloading.
        model.setRowCount(0);
        // Clear the selected row or entry in jTableItems without deleting its data.
        jTableItems.clearSelection();
        // Select position 0 in jComboBoxCatalogueItem; dropdown positions start at zero.
        jComboBoxCatalogueItem.setSelectedIndex(0);
        // Select position 0 in jComboBoxCategory; dropdown positions start at zero.
        jComboBoxCategory.setSelectedIndex(0);
        // Set the text displayed by jTextFieldDescription to empty text.
        jTextFieldDescription.setText("");
        // Set the text displayed by jTextFieldQuantity to "1".
        jTextFieldQuantity.setText("1");
        // Set the text displayed by jTextFieldUnitCost to "0.00".
        jTextFieldUnitCost.setText("0.00");
        // Set the text displayed by jTextAreaNotes to empty text.
        jTextAreaNotes.setText("");
        // The now-empty item list produces a total of 0.00.
        // Add the staged request-item totals using BigDecimal and show the total with two decimal places.
        updateTotal();
        // Move keyboard focus to jTextFieldDescription so the next typing goes to that control.
        jTextFieldDescription.requestFocusInWindow();
    }

    // Validate the fields before adding anything to the list or table.
    // Create a validated item from the selected catalogue entry and quantity, then update the unsaved
    // table and total.
    private void addItem() {
        // One request uses one type. Services use quantity as the number of service units.
        // Continue with this branch when (jComboBoxRequestType.getSelectedIndex() is at most 0).
        if (jComboBoxRequestType.getSelectedIndex() <= 0) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, "Select Equipment or Service first.");
            // Stop this method here; no further statements in this call are executed.
            return;
        }
        // Declare selectedIndex to hold a whole-number value. Read the selected zero-based position in
        // jComboBoxCategory; minus one means no selection.
        int selectedIndex = jComboBoxCategory.getSelectedIndex();
        // Index zero is the instruction, not a category from the database.
        // Continue with this branch when (selectedIndex is at most 0 or selectedIndex is greater than the
        // number of entries in categories).
        if (selectedIndex <= 0 || selectedIndex > categories.size()) {
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, "Please select a category.");
            // Move keyboard focus to jComboBoxCategory so the next typing goes to that control.
            jComboBoxCategory.requestFocusInWindow();
            // Stop this method here; no further statements in this call are executed.
            return;
        }

        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Convert text to numbers. Invalid text causes NumberFormatException.
            // Declare quantity to hold a whole-number value. Its initial value is
            // `Integer.parseInt(jTextFieldQuantity.getText().trim())`.
            int quantity = Integer.parseInt(jTextFieldQuantity.getText().trim());

            // Subtract one because the dropdown starts with the instruction option.
            // Declare category with type Category. Read entry selectedIndex - 1 from categories; list positions
            // start at zero.
            Category category = categories.get(selectedIndex - 1);
            // Declare choice to hold a whole-number value. Its initial value is
            // `jComboBoxCatalogueItem.getSelectedIndex() - 1`.
            int choice = jComboBoxCatalogueItem.getSelectedIndex() - 1;
            // Continue with this branch when (choice is less than 0 or choice is at least the number of entries in
            // catalogue).
            // Stop this operation with an exception: Select a catalogue item.
            if (choice < 0 || choice >= catalogue.size()) { throw new IllegalArgumentException("Select a catalogue item."); }
            // Declare entry to hold one row of values in SELECT or table-column order. Read entry choice from
            // catalogue; list positions start at zero.
            Object[] entry = catalogue.get(choice);
            // Declare item with type RequestItem. Create a RequestItem object using the supplied constructor
            // values.
            RequestItem item = new RequestItem(Database.id(entry[0]), category,
                    entry[7].toString(), entry[4].toString(), quantity, (BigDecimal) entry[5]);

            // Keep the object for later saving, then show its values in a new table row.
            // Append item to requestItems.
            requestItems.add(item);
            // Keep the type fixed while items exist to avoid relabelling a filled request.
            // Set whether jComboBoxRequestType accepts input or actions using false; false disables it.
            jComboBoxRequestType.setEnabled(false);
            // Declare model with type DefaultTableModel. Read the model holding (DefaultTableModel)jTableItems
            // data, rather than the control's visual appearance.
            DefaultTableModel model = (DefaultTableModel) jTableItems.getModel();
            // Append the supplied values as one table row in column order.
            model.addRow(new Object[] {category.getCategoryName(), item.getDescription(),
                item.getQuantity(), item.getUnitCost(), item.getLineTotal()});
            // Add the staged request-item totals using BigDecimal and show the total with two decimal places.
            updateTotal();

            // Clear only the item fields so the user can enter another item.
            // The request's notes and previously added rows remain in place.
            // Select position 0 in jComboBoxCatalogueItem; dropdown positions start at zero.
            jComboBoxCatalogueItem.setSelectedIndex(0);
        // Select position 0 in jComboBoxCategory; dropdown positions start at zero.
        jComboBoxCategory.setSelectedIndex(0);
            // Set the text displayed by jTextFieldDescription to empty text.
            jTextFieldDescription.setText("");
            // Set the text displayed by jTextFieldQuantity to "1".
            jTextFieldQuantity.setText("1");
            // Set the text displayed by jTextFieldUnitCost to "0.00".
            jTextFieldUnitCost.setText("0.00");
            // Move keyboard focus to jTextFieldDescription so the next typing goes to that control.
            jTextFieldDescription.requestFocusInWindow();
        // Handle NumberFormatException ex from the preceding try block so the failure follows the recovery
        // steps below.
        } catch (NumberFormatException ex) {
            // Handle conversion errors before the more general validation errors below.
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this,
                    "Enter a whole-number quantity and a numeric unit cost, for example 1250.50.",
                    "Check Item", JOptionPane.WARNING_MESSAGE);
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        } catch (IllegalArgumentException ex) {
            // The RequestItem constructor supplies a simple message for invalid values.
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Check Item", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Recalculate the total from the item objects rather than text typed into the table.
    // Add the staged request-item totals using BigDecimal and show the total with two decimal places.
    private void updateTotal() {
        // Declare total to hold an exact decimal amount. Create an exact decimal value from the supplied
        // number or text.
        BigDecimal total = new BigDecimal("0.00");
        // Repeat while i is less than the number of entries in requestItems; initialise the counter once and
        // update it after each pass.
        for (int i = 0; i < requestItems.size(); i++) {
            // BigDecimal.add returns a new value, so assign it back to total.
            // Set total from the following operation: add the supplied amount to total and return the new
            // BigDecimal value.
            total = total.add(requestItems.get(i).getLineTotal());
        }
        // toPlainString displays ordinary decimal notation, with no exponent.
        // Set the text displayed by jTextFieldTotal to `total.toPlainString()`.
        jTextFieldTotal.setText(total.toPlainString());
    }

    // Read the saved categories and display their names in the dropdown.
    // Load category objects so the screen can show readable names and retain their database IDs.
    private void loadCategories() {
        // Remove old choices and keep the first choice as an instruction only.
        // Remove the entries from categories while keeping the same collection object.
        categories.clear();
        // Remove the existing choices from jComboBoxCategory before adding a fresh list.
        jComboBoxCategory.removeAllItems();
        // Append "Select category" as a selectable entry in jComboBoxCategory.
        jComboBoxCategory.addItem("Select category");
        // Set whether jComboBoxCategory accepts input or actions using false; false disables it.
        jComboBoxCategory.setEnabled(false);

        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Ask the DAO to read the database instead of putting SQL in the form.
            // Declare categoryDAO with type CategoryDAO. Create a CategoryDAO object using the supplied
            // constructor values.
            CategoryDAO categoryDAO = new CategoryDAO();
            // Set categories from the following operation: read category IDs and names for the dropdown while
            // preserving their database identities.
            categories = categoryDAO.getAllCategories();

            // Add names in the same order as the objects in the list.
            // Combo-box index 1 matches list index 0 because of "Select category".
            // Repeat while i is less than the number of entries in categories; initialise the counter once and
            // update it after each pass.
            for (int i = 0; i < categories.size(); i++) {
                // Append `categories.get(i).getCategoryName()` as a selectable entry in jComboBoxCategory.
                jComboBoxCategory.addItem(categories.get(i).getCategoryName());
            }

            // Only allow a choice when the database has categories to choose from.
            // Continue with this branch when (categories is empty).
            if (categories.isEmpty()) {
                // Display the supplied message in a Swing dialog, using the parent, title and message style when
                // provided.
                JOptionPane.showMessageDialog(this,
                        "No categories are available. Please contact the administrator.",
                        "Categories", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Set whether jComboBoxCategory accepts input or actions using true; false disables it.
                jComboBoxCategory.setEnabled(true);
            }
        // Handle SQLException ex from the preceding try block so the failure follows the recovery steps below.
        } catch (SQLException ex) {
            // Leave the dropdown disabled so an unsuccessful load cannot be used.
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this,
                    "Could not load categories. Check that MySQL is running, then log in again.",
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
        // Set jComboBoxCategory from the following operation: create a dropdown selection control.
        jComboBoxCategory = new javax.swing.JComboBox<>();
        // Set jLabelCategory from the following operation: create a non-editable text or image display.
        jLabelCategory = new javax.swing.JLabel();
        // Set jLabelDescription from the following operation: create a non-editable text or image display.
        jLabelDescription = new javax.swing.JLabel();
        // Set jLabelQuantity from the following operation: create a non-editable text or image display.
        jLabelQuantity = new javax.swing.JLabel();
        // Set jLabelUnitCost from the following operation: create a non-editable text or image display.
        jLabelUnitCost = new javax.swing.JLabel();
        // Set jTextFieldDescription from the following operation: create a single-line text input.
        jTextFieldDescription = new javax.swing.JTextField();
        // Set jTextFieldUnitCost from the following operation: create a single-line text input.
        jTextFieldUnitCost = new javax.swing.JTextField();
        // Set jTextFieldQuantity from the following operation: create a single-line text input.
        jTextFieldQuantity = new javax.swing.JTextField();
        // Set jButtonRemoveItem from the following operation: create a clickable action button.
        jButtonRemoveItem = new javax.swing.JButton();
        // Set jButtonAddItem from the following operation: create a clickable action button.
        jButtonAddItem = new javax.swing.JButton();
        // Set jScrollPane1 from the following operation: create a scrollable viewport for another control.
        jScrollPane1 = new javax.swing.JScrollPane();
        // Set jTableItems from the following operation: create a table displaying rows and columns through a
        // model.
        jTableItems = new javax.swing.JTable();
        // Set jLabelTotal from the following operation: create a non-editable text or image display.
        jLabelTotal = new javax.swing.JLabel();
        // Set jTextFieldTotal from the following operation: create a single-line text input.
        jTextFieldTotal = new javax.swing.JTextField();
        // Set jLabelNotes from the following operation: create a non-editable text or image display.
        jLabelNotes = new javax.swing.JLabel();
        // Set jScrollPane2 from the following operation: create a scrollable viewport for another control.
        jScrollPane2 = new javax.swing.JScrollPane();
        // Set jTextAreaNotes from the following operation: create a multi-line text display or input.
        jTextAreaNotes = new javax.swing.JTextArea();
        // Set jButtonClear from the following operation: create a clickable action button.
        jButtonClear = new javax.swing.JButton();
        // Set jButtonSubmit from the following operation: create a clickable action button.
        jButtonSubmit = new javax.swing.JButton();
        // Set jLabelRequestType from the following operation: create a non-editable text or image display.
        jLabelRequestType = new javax.swing.JLabel();
        // Set jComboBoxRequestType from the following operation: create a dropdown selection control.
        jComboBoxRequestType = new javax.swing.JComboBox<>();
        // Set jLabelAttachments from the following operation: create a non-editable text or image display.
        jLabelAttachments = new javax.swing.JLabel();
        // Set jButtonChooseFile from the following operation: create a clickable action button.
        jButtonChooseFile = new javax.swing.JButton();
        // Set jButtonRemoveFile from the following operation: create a clickable action button.
        jButtonRemoveFile = new javax.swing.JButton();
        // Set jScrollPane3 from the following operation: create a scrollable viewport for another control.
        jScrollPane3 = new javax.swing.JScrollPane();
        // Set jTableAttachments from the following operation: create a table displaying rows and columns
        // through a model.
        jTableAttachments = new javax.swing.JTable();
        // Set jLabelProductsAndServices from the following operation: create a non-editable text or image
        // display.
        jLabelProductsAndServices = new javax.swing.JLabel();
        // Set jComboBoxCatalogueItem from the following operation: create a dropdown selection control.
        jComboBoxCatalogueItem = new javax.swing.JComboBox<>();

        // Set the text displayed by jLabelTitle to "New Procurement Request".
        jLabelTitle.setText("New Procurement Request");

        // Give jComboBoxCategory the supplied model, which holds its choices or table data.
        jComboBoxCategory.setModel(new javax.swing.DefaultComboBoxModel<String>(
            new String[] {"Select category"}
        ));

        // Set the text displayed by jLabelCategory to "Category".
        jLabelCategory.setText("Category");

        // Set the text displayed by jLabelDescription to "Description".
        jLabelDescription.setText("Description");

        // Set the text displayed by jLabelQuantity to "Quantity".
        jLabelQuantity.setText("Quantity");

        // Set the text displayed by jLabelUnitCost to "Unit price (UGX)".
        jLabelUnitCost.setText("Unit price (UGX)");

        // Set whether jTextFieldDescription allows direct typing using false; its value can still be changed
        // by code.
        jTextFieldDescription.setEditable(false);

        // Set whether jTextFieldUnitCost allows direct typing using false; its value can still be changed by
        // code.
        jTextFieldUnitCost.setEditable(false);
        // Set the text displayed by jTextFieldUnitCost to "Unit price (UGX)".
        jTextFieldUnitCost.setText("Unit price (UGX)");

        // Set the text displayed by jTextFieldQuantity to "1".
        jTextFieldQuantity.setText("1");
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jTextFieldQuantity.addActionListener(this::jTextFieldQuantityActionPerformed);

        // Set the text displayed by jButtonRemoveItem to "Remove Selected".
        jButtonRemoveItem.setText("Remove Selected");

        // Set the text displayed by jButtonAddItem to "Add Item".
        jButtonAddItem.setText("Add Item");

        // Give jTableItems the supplied model, which holds its choices or table data.
        jTableItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Category", "Description", "Quantity", "Unit price (UGX)", "Line total (UGX)"
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
        // Place jTableItems inside jScrollPane1 so it can scroll when larger than the available space.
        jScrollPane1.setViewportView(jTableItems);

        // Set the text displayed by jLabelTotal to "Total (UGX)".
        jLabelTotal.setText("Total (UGX)");

        // Set whether jTextFieldTotal allows direct typing using false; its value can still be changed by
        // code.
        jTextFieldTotal.setEditable(false);
        // Set the text displayed by jTextFieldTotal to "0.00".
        jTextFieldTotal.setText("0.00");

        // Set the text displayed by jLabelNotes to "Notes (optional)".
        jLabelNotes.setText("Notes (optional)");

        // Set the preferred text width of jTextAreaNotes to 20 columns; this is not a text-length limit.
        jTextAreaNotes.setColumns(20);
        // Control whether jTextAreaNotes wraps long text onto another line.
        jTextAreaNotes.setLineWrap(true);
        // Set the preferred visible height of jTextAreaNotes to 5 text rows.
        jTextAreaNotes.setRows(5);
        // Control whether wrapped text breaks at word boundaries rather than between letters.
        jTextAreaNotes.setWrapStyleWord(true);
        // Place jTextAreaNotes inside jScrollPane2 so it can scroll when larger than the available space.
        jScrollPane2.setViewportView(jTextAreaNotes);

        // Set the text displayed by jButtonClear to "Clear".
        jButtonClear.setText("Clear");

        // Set the text displayed by jButtonSubmit to "Submit Request".
        jButtonSubmit.setText("Submit Request");

        // Set the text displayed by jLabelRequestType to "Request type".
        jLabelRequestType.setText("Request type");

        // Give jComboBoxRequestType the supplied model, which holds its choices or table data.
        jComboBoxRequestType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select request type", "Equipment", "Service" }));

        // Set the text displayed by jLabelAttachments to "Supporting files (optional)".
        jLabelAttachments.setText("Supporting files (optional)");

        // Set the text displayed by jButtonChooseFile to "Choose File".
        jButtonChooseFile.setText("Choose File");

        // Set the text displayed by jButtonRemoveFile to "Remove Selected File".
        jButtonRemoveFile.setText("Remove Selected File");

        // Give jTableAttachments the supplied model, which holds its choices or table data.
        jTableAttachments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "File Name", "File Path"
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
        // Place jTableAttachments inside jScrollPane3 so it can scroll when larger than the available space.
        jScrollPane3.setViewportView(jTableAttachments);

        // Set the text displayed by jLabelProductsAndServices to "Product / Service".
        jLabelProductsAndServices.setText("Product / Service");

        // Give jComboBoxCatalogueItem the supplied model, which holds its choices or table data.
        jComboBoxCatalogueItem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select product/service" }));

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane1)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(15, 15, 15))
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap()
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jScrollPane2)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Place jLabelNotes in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jLabelNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(0, 614, Short.MAX_VALUE))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    // Place jLabelQuantity in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jLabelQuantity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    // Place jLabelDescription in this layout group; any following size arguments give minimum, preferred
                                    // and maximum sizes.
                                    .addComponent(jLabelDescription, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    // Place jLabelCategory in this layout group; any following size arguments give minimum, preferred and
                                    // maximum sizes.
                                    .addComponent(jLabelCategory, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    // Nest a sequential group to place controls one after another.
                                    .addGroup(layout.createSequentialGroup()
                                        // Nest a parallel group so controls share this horizontal or vertical region.
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                                            // maximum sizes.
                                            .addComponent(jLabelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            // Place jLabelRequestType in this layout group; any following size arguments give minimum, preferred
                                            // and maximum sizes.
                                            .addComponent(jLabelRequestType, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    // Place jComboBoxCategory in this layout group; any following size arguments give minimum, preferred
                                    // and maximum sizes.
                                    .addComponent(jComboBoxCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    // Place jTextFieldDescription in this layout group; any following size arguments give minimum,
                                    // preferred and maximum sizes.
                                    .addComponent(jTextFieldDescription)
                                    // Nest a sequential group to place controls one after another.
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        // Place jTextFieldQuantity in this layout group; any following size arguments give minimum, preferred
                                        // and maximum sizes.
                                        .addComponent(jTextFieldQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                                        // Place jLabelUnitCost in this layout group; any following size arguments give minimum, preferred and
                                        // maximum sizes.
                                        .addComponent(jLabelUnitCost, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        // Place jTextFieldUnitCost in this layout group; any following size arguments give minimum, preferred
                                        // and maximum sizes.
                                        .addComponent(jTextFieldUnitCost, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    // Place jComboBoxRequestType in this layout group; any following size arguments give minimum,
                                    // preferred and maximum sizes.
                                    .addComponent(jComboBoxRequestType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(298, 298, 298)
                                // Place jButtonAddItem in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jButtonAddItem, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(32, 32, 32)
                                // Place jButtonRemoveItem in this layout group; any following size arguments give minimum, preferred
                                // and maximum sizes.
                                .addComponent(jButtonRemoveItem, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(0, 0, Short.MAX_VALUE)))
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(15, 15, 15))))
            // Nest a sequential group to place controls one after another.
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                // Place jLabelTotal in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Place jTextFieldTotal in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jTextFieldTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(107, 107, 107))
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave spacing between the group and its enclosing container edge.
                        .addContainerGap()
                        // Place jLabelAttachments in this layout group; any following size arguments give minimum, preferred
                        // and maximum sizes.
                        .addComponent(jLabelAttachments))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(80, 80, 80)
                        // Place jButtonChooseFile in this layout group; any following size arguments give minimum, preferred
                        // and maximum sizes.
                        .addComponent(jButtonChooseFile)
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        // Place jButtonRemoveFile in this layout group; any following size arguments give minimum, preferred
                        // and maximum sizes.
                        .addComponent(jButtonRemoveFile))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(16, 16, 16)
                        // Place jScrollPane3 in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 734, javax.swing.GroupLayout.PREFERRED_SIZE))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(217, 217, 217)
                        // Place jButtonClear in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonClear, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(117, 117, 117)
                        // Place jButtonSubmit in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave spacing between the group and its enclosing container edge.
                        .addContainerGap()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(308, 308, 308)
                                // Place jComboBoxCatalogueItem in this layout group; any following size arguments give minimum,
                                // preferred and maximum sizes.
                                .addComponent(jComboBoxCatalogueItem, javax.swing.GroupLayout.PREFERRED_SIZE, 514, javax.swing.GroupLayout.PREFERRED_SIZE))
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Place jLabelProductsAndServices in this layout group; any following size arguments give minimum,
                                // preferred and maximum sizes.
                                .addComponent(jLabelProductsAndServices)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))))
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
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(1, 1, 1)
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    // Place jLabelRequestType in this layout group; any following size arguments give minimum, preferred
                                    // and maximum sizes.
                                    .addComponent(jLabelRequestType)
                                    // Place jComboBoxRequestType in this layout group; any following size arguments give minimum,
                                    // preferred and maximum sizes.
                                    .addComponent(jComboBoxRequestType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                // Place jLabelCategory in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jLabelCategory))
                            // Place jComboBoxCategory in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jComboBoxCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            // Place jLabelProductsAndServices in this layout group; any following size arguments give minimum,
                            // preferred and maximum sizes.
                            .addComponent(jLabelProductsAndServices)
                            // Place jComboBoxCatalogueItem in this layout group; any following size arguments give minimum,
                            // preferred and maximum sizes.
                            .addComponent(jComboBoxCatalogueItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            // Place jLabelDescription in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jLabelDescription)
                            // Place jTextFieldDescription in this layout group; any following size arguments give minimum,
                            // preferred and maximum sizes.
                            .addComponent(jTextFieldDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(18, 18, 18)
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            // Place jLabelQuantity in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelQuantity)
                            // Place jLabelUnitCost in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelUnitCost)
                            // Place jTextFieldQuantity in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jTextFieldQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            // Place jTextFieldUnitCost in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jTextFieldUnitCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(90, 90, 90)
                        // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            // Place jLabelTotal in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelTotal)
                            // Place jTextFieldTotal in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jTextFieldTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(13, 13, 13)
                                // Place jLabelNotes in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jLabelNotes)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                // Place jLabelAttachments in this layout group; any following size arguments give minimum, preferred
                                // and maximum sizes.
                                .addComponent(jLabelAttachments))
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(150, 150, 150)
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    // Place jButtonChooseFile in this layout group; any following size arguments give minimum, preferred
                                    // and maximum sizes.
                                    .addComponent(jButtonChooseFile)
                                    // Place jButtonRemoveFile in this layout group; any following size arguments give minimum, preferred
                                    // and maximum sizes.
                                    .addComponent(jButtonRemoveFile))))
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        // Place jScrollPane3 in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            // Place jButtonClear in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jButtonClear)
                            // Place jButtonSubmit in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jButtonSubmit)))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(183, 183, 183)
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            // Place jButtonAddItem in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jButtonAddItem)
                            // Place jButtonRemoveItem in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jButtonRemoveItem))))
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Handle the action event from jTextFieldQuantity; any statements below run when that action is
    // raised.
    private void jTextFieldQuantityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldQuantityActionPerformed
        // No extra action is needed here; the form connects its buttons in the constructor.
    }//GEN-LAST:event_jTextFieldQuantityActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Only this class accesses this field directly. Declare jButtonAddItem with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonAddItem;
    // Only this class accesses this field directly. Declare jButtonChooseFile with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonChooseFile;
    // Only this class accesses this field directly. Declare jButtonClear with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonClear;
    // Only this class accesses this field directly. Declare jButtonRemoveFile with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonRemoveFile;
    // Only this class accesses this field directly. Declare jButtonRemoveItem with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonRemoveItem;
    // Only this class accesses this field directly. Declare jButtonSubmit with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonSubmit;
    // Only this class accesses this field directly. Declare jComboBoxCatalogueItem with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxCatalogueItem;
    // Only this class accesses this field directly. Declare jComboBoxCategory with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxCategory;
    // Only this class accesses this field directly. Declare jComboBoxRequestType with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxRequestType;
    // Only this class accesses this field directly. Declare jLabelAttachments with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelAttachments;
    // Only this class accesses this field directly. Declare jLabelCategory with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelCategory;
    // Only this class accesses this field directly. Declare jLabelDescription with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelDescription;
    // Only this class accesses this field directly. Declare jLabelNotes with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelNotes;
    // Only this class accesses this field directly. Declare jLabelProductsAndServices with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelProductsAndServices;
    // Only this class accesses this field directly. Declare jLabelQuantity with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelQuantity;
    // Only this class accesses this field directly. Declare jLabelRequestType with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelRequestType;
    // Only this class accesses this field directly. Declare jLabelTitle with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelTitle;
    // Only this class accesses this field directly. Declare jLabelTotal with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelTotal;
    // Only this class accesses this field directly. Declare jLabelUnitCost with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelUnitCost;
    // Only this class accesses this field directly. Declare jScrollPane1 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane1;
    // Only this class accesses this field directly. Declare jScrollPane2 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane2;
    // Only this class accesses this field directly. Declare jScrollPane3 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane3;
    // Only this class accesses this field directly. Declare jTableAttachments with type
    // javax.swing.JTable. Java initially uses null for this object reference.
    private javax.swing.JTable jTableAttachments;
    // Only this class accesses this field directly. Declare jTableItems with type javax.swing.JTable. Java
    // initially uses null for this object reference.
    private javax.swing.JTable jTableItems;
    // Only this class accesses this field directly. Declare jTextAreaNotes with type
    // javax.swing.JTextArea. Java initially uses null for this object reference.
    private javax.swing.JTextArea jTextAreaNotes;
    // Only this class accesses this field directly. Declare jTextFieldDescription with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldDescription;
    // Only this class accesses this field directly. Declare jTextFieldQuantity with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldQuantity;
    // Only this class accesses this field directly. Declare jTextFieldTotal with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldTotal;
    // Only this class accesses this field directly. Declare jTextFieldUnitCost with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldUnitCost;
    // End of variables declaration//GEN-END:variables
}
