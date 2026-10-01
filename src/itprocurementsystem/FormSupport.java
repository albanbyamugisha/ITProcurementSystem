package itprocurementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

// Shared display helpers keep each form's event methods short and readable.
public final class FormSupport {
    // Replace rows without changing the headings designed in NetBeans.
    public static void fill(JTable table, ArrayList<Object[]> rows) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Object[] row : rows) { model.addRow(row); }
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    // Table sorting changes visible row numbers, so convert before reading the model.
    public static int selectedId(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { throw new IllegalArgumentException("Select a row first."); }
        return ((Number) table.getModel().getValueAt(table.convertRowIndexToModel(row), 0)).intValue();
    }
    public static Object cell(JTable table, int column) {
        int row = table.getSelectedRow();
        if (row < 0) { return ""; }
        Object value = table.getModel().getValueAt(table.convertRowIndexToModel(row), column);
        return value == null ? "" : value;
    }
    // Combo boxes show ID and description; index zero is always an instruction.
    public static void choices(JComboBox<String> combo, ArrayList<Object[]> rows, String prompt) {
        combo.removeAllItems();
        combo.addItem(prompt);
        for (Object[] row : rows) { combo.addItem(row[0] + " - " + row[1]); }
    }
    public static int choice(JComboBox<String> combo) {
        if (combo.getSelectedIndex() <= 0) { throw new IllegalArgumentException("Select an entry from the list first."); }
        return Integer.parseInt(combo.getSelectedItem().toString().split(" - ", 2)[0]);
    }
    // Show a friendly failure instead of letting a button crash the event thread.
    public static void error(java.awt.Component parent, Exception error) {
        JOptionPane.showMessageDialog(parent, error.getMessage(), "Could not complete action", JOptionPane.ERROR_MESSAGE);
    }
}
