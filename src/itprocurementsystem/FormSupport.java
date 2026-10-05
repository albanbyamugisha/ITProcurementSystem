// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Make the public types in javax.swing available by short names; this does not create objects.
import javax.swing.*;
// Import DefaultTableModel for the rows and columns displayed by a Swing table.
import javax.swing.table.DefaultTableModel;
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;

// Shared display helpers keep each form's event methods short and readable.
// Define FormSupport as a class that groups its related data and methods; final prevents other classes
// from extending it.
public final class FormSupport {
    // Replace rows without changing the headings designed in NetBeans.
    // Replace table rows through its model, keeping the existing column headings and allowing one selected
    // row.
    public static void fill(JTable table, ArrayList<Object[]> rows) {
        // Declare model with type DefaultTableModel. Read the model holding (DefaultTableModel)table data,
        // rather than the control's visual appearance.
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        // Set model to 0 rows; zero clears existing rows before reloading.
        model.setRowCount(0);
        // Process each entry in rows in turn, referring to the current entry as row.
        // Append the supplied values as one table row in column order.
        for (Object[] row : rows) { model.addRow(row); }
        // Allow only one table row to be selected at a time for unambiguous update or remove actions.
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    // Table sorting changes visible row numbers, so convert before reading the model.
    // Read the ID in column zero of the selected model row, accounting for any sorting in the visible
    // table.
    public static int selectedId(JTable table) {
        // Declare row to hold a whole-number value. Read the selected visible row index in table; minus one
        // means no row is selected.
        int row = table.getSelectedRow();
        // Continue with this branch when (row is less than 0).
        // Stop this operation with an exception: Select a row first.
        if (row < 0) { throw new IllegalArgumentException("Select a row first."); }
        // Convert this numeric value to a Java int for use as a record ID or count. Return the resulting value
        // to the caller.
        return ((Number) table.getModel().getValueAt(table.convertRowIndexToModel(row), 0)).intValue();
    }
    // Read a selected table cell; use empty text when nothing is selected or the stored value is null.
    public static Object cell(JTable table, int column) {
        // Declare row to hold a whole-number value. Read the selected visible row index in table; minus one
        // means no row is selected.
        int row = table.getSelectedRow();
        // Continue with this branch when (row is less than 0).
        // Return empty text to the caller.
        if (row < 0) { return ""; }
        // Declare value with type Object. Its initial value is
        // `table.getModel().getValueAt(table.convertRowIndexToModel(row), column)`.
        Object value = table.getModel().getValueAt(table.convertRowIndexToModel(row), column);
        // Return `value == null ? "" : value` to the caller.
        return value == null ? "" : value;
    }
    // Combo boxes show ID and description; index zero is always an instruction.
    // Rebuild a dropdown with a prompt first, followed by database IDs paired with readable descriptions.
    public static void choices(JComboBox<String> combo, ArrayList<Object[]> rows, String prompt) {
        // Remove the existing choices from combo before adding a fresh list.
        combo.removeAllItems();
        // Append prompt as a selectable entry in combo.
        combo.addItem(prompt);
        // Process each entry in rows in turn, referring to the current entry as row.
        // Append `row[0] + " - " + row[1]` as a selectable entry in combo.
        for (Object[] row : rows) { combo.addItem(row[0] + " - " + row[1]); }
    }
    // Reject the dropdown prompt and extract the database ID before the first " - " separator.
    public static int choice(JComboBox<String> combo) {
        // Continue with this branch when (combo.getSelectedIndex() is at most 0).
        // Stop this operation with an exception: Select an entry from the list first.
        if (combo.getSelectedIndex() <= 0) { throw new IllegalArgumentException("Select an entry from the list first."); }
        // Return `Integer.parseInt(combo.getSelectedItem().toString().split(" - ", 2)[0])` to the caller.
        return Integer.parseInt(combo.getSelectedItem().toString().split(" - ", 2)[0]);
    }
    // Show a friendly failure instead of letting a button crash the event thread.
    // Display the exception message in a Swing error dialog attached to the supplied parent component.
    public static void error(java.awt.Component parent, Exception error) {
        // Display the supplied message in a Swing dialog, using the parent, title and message style when
        // provided.
        JOptionPane.showMessageDialog(parent, error.getMessage(), "Could not complete action", JOptionPane.ERROR_MESSAGE);
    }
}
