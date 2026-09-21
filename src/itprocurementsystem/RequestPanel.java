/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package itprocurementsystem;

// These classes help us keep category objects and report database problems.
import java.util.ArrayList;
import java.sql.SQLException;
import javax.swing.JOptionPane;
// These classes handle money, button clicks and rows in our table.
import java.math.BigDecimal;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.table.DefaultTableModel;

/**
 * We use a JPanel because the request form will sit inside MainFrame.
 * A panel groups related controls, such as the request fields and buttons.
 * It does not create a separate window or have its own title bar.
 * Extending JPanel lets us build this form as a reusable part of the main window.
 *
 * @author alban-byamugisha
 */
public class RequestPanel extends javax.swing.JPanel {

    // Keep the category IDs as well as the names displayed by our String combo box.
    private ArrayList<Category> categories = new ArrayList<Category>();

    // Store the actual items, including category IDs, until the request is submitted.
    private ArrayList<RequestItem> requestItems = new ArrayList<RequestItem>();

    /**
     * Creates new form RequestPanel
     */
    public RequestPanel() {
        // Create the controls and layout arranged in NetBeans Design view.
        initComponents();

        // Fill the dropdown from MySQL after NetBeans has created its controls.
        loadCategories();

        // Connect the button outside the layout code maintained by NetBeans.
        jButtonAddItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // Run these steps whenever the user clicks Add Item.
                addItem();
            }
        });

        // Send the whole request to MySQL when Submit Request is clicked.
        jButtonSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                submitRequest();
            }
        });

        // One selected row makes it clear which item Remove Selected will remove.
        jTableItems.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jButtonRemoveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                removeSelectedItem();
            }
        });

        // Clear resets the unsaved request only after the user confirms.
        jButtonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int answer = JOptionPane.showConfirmDialog(RequestPanel.this,
                        "Clear all items, notes and entered values in this request?",
                        "Clear Request", JOptionPane.YES_NO_OPTION);
                // No or closing the dialog leaves the request unchanged.
                if (answer == JOptionPane.YES_OPTION) {
                    clearRequest();
                }
            }
        });
    }

    // Save the complete request only when its items have been added to the table.
    private void submitRequest() {
        // A logged-out user must log in again before submitting a request.
        if (Session.getUserId() == 0 || !"Requester".equals(Session.getRole())) {
            JOptionPane.showMessageDialog(this, "Please log in as a requester first.");
            return;
        }
        if (requestItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Add at least one item before submitting.");
            return;
        }
        // Avoid silently losing an item that was typed but not added to the table.
        if (jComboBoxCategory.getSelectedIndex() > 0
                || !jTextFieldDescription.getText().trim().isEmpty()
                || !"1".equals(jTextFieldQuantity.getText().trim())
                || !"0.00".equals(jTextFieldUnitCost.getText().trim())) {
            JOptionPane.showMessageDialog(this,
                    "You have unfinished item fields. Add the item or reset those fields first.");
            return;
        }

        // Disable this button while saving to avoid a second submission.
        jButtonSubmit.setEnabled(false);
        try {
            RequestDAO requestDAO = new RequestDAO();
            int requestId = requestDAO.saveRequest(Session.getUserId(),
                    jTextAreaNotes.getText(), requestItems);
            // Clear only after saving succeeds. Failed attempts keep the entered data.
            clearRequest();
            JOptionPane.showMessageDialog(this,
                    "Request #" + requestId + " submitted successfully. Status: Pending.",
                    "Request Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not confirm the save. Your form has been kept. "
                    + "Check the database connection and saved requests before trying again.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            // Show the validation message supplied by our data access class.
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Check Request", JOptionPane.WARNING_MESSAGE);
        } finally {
            // finally runs whether saving succeeded or an error occurred.
            jButtonSubmit.setEnabled(true);
        }
    }

    // Remove the selected item from both our object list and the visible table.
    private void removeSelectedItem() {
        int selectedRow = jTableItems.getSelectedRow();
        // A value of -1 means the user has not selected a row.
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an item in the table first.");
            return;
        }
        // Convert the displayed row number to its data position if sorting is used later.
        int itemIndex = jTableItems.convertRowIndexToModel(selectedRow);
        requestItems.remove(itemIndex);
        DefaultTableModel model = (DefaultTableModel) jTableItems.getModel();
        model.removeRow(itemIndex);
        updateTotal();
    }

    // Reset the unsaved request. This method does not delete anything from MySQL.
    private void clearRequest() {
        requestItems.clear();
        // Setting the row count to zero removes every visible item row.
        DefaultTableModel model = (DefaultTableModel) jTableItems.getModel();
        model.setRowCount(0);
        jTableItems.clearSelection();
        jComboBoxCategory.setSelectedIndex(0);
        jTextFieldDescription.setText("");
        jTextFieldQuantity.setText("1");
        jTextFieldUnitCost.setText("0.00");
        jTextAreaNotes.setText("");
        // The now-empty item list produces a total of 0.00.
        updateTotal();
        jTextFieldDescription.requestFocusInWindow();
    }

    // Validate the fields before adding anything to the list or table.
    private void addItem() {
        int selectedIndex = jComboBoxCategory.getSelectedIndex();
        // Index zero is the instruction, not a category from the database.
        if (selectedIndex <= 0 || selectedIndex > categories.size()) {
            JOptionPane.showMessageDialog(this, "Please select a category.");
            jComboBoxCategory.requestFocusInWindow();
            return;
        }

        try {
            // Convert text to numbers. Invalid text causes NumberFormatException.
            int quantity = Integer.parseInt(jTextFieldQuantity.getText().trim());
            BigDecimal unitCost = new BigDecimal(jTextFieldUnitCost.getText().trim());

            // Subtract one because the dropdown starts with our instruction option.
            Category category = categories.get(selectedIndex - 1);
            RequestItem item = new RequestItem(category,
                    jTextFieldDescription.getText(), quantity, unitCost);

            // Keep the object for later saving, then show its values in a new table row.
            requestItems.add(item);
            DefaultTableModel model = (DefaultTableModel) jTableItems.getModel();
            model.addRow(new Object[] {category.getCategoryName(), item.getDescription(),
                item.getQuantity(), item.getUnitCost(), item.getLineTotal()});
            updateTotal();

            // Clear only the item fields so the user can enter another item.
            // The request's notes and previously added rows remain in place.
            jComboBoxCategory.setSelectedIndex(0);
            jTextFieldDescription.setText("");
            jTextFieldQuantity.setText("1");
            jTextFieldUnitCost.setText("0.00");
            jTextFieldDescription.requestFocusInWindow();
        } catch (NumberFormatException ex) {
            // Handle conversion errors before the more general validation errors below.
            JOptionPane.showMessageDialog(this,
                    "Enter a whole-number quantity and a numeric unit cost, for example 1250.50.",
                    "Check Item", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            // The RequestItem constructor supplies a simple message for invalid values.
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Check Item", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Recalculate the total from the item objects rather than text typed into the table.
    private void updateTotal() {
        BigDecimal total = new BigDecimal("0.00");
        for (int i = 0; i < requestItems.size(); i++) {
            // BigDecimal.add returns a new value, so assign it back to total.
            total = total.add(requestItems.get(i).getLineTotal());
        }
        // toPlainString displays ordinary decimal notation, with no exponent.
        jTextFieldTotal.setText(total.toPlainString());
    }

    // Read the saved categories and display their names in the dropdown.
    private void loadCategories() {
        // Remove old choices and keep the first choice as an instruction only.
        categories.clear();
        jComboBoxCategory.removeAllItems();
        jComboBoxCategory.addItem("Select category");
        jComboBoxCategory.setEnabled(false);

        try {
            // Ask the DAO to read the database instead of putting SQL in the form.
            CategoryDAO categoryDAO = new CategoryDAO();
            categories = categoryDAO.getAllCategories();

            // Add names in the same order as the objects in our list.
            // Combo-box index 1 matches list index 0 because of "Select category".
            for (int i = 0; i < categories.size(); i++) {
                jComboBoxCategory.addItem(categories.get(i).getCategoryName());
            }

            // Only allow a choice when the database has categories to choose from.
            if (categories.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No categories are saved. Run db/sample_categories.sql, then log in again.",
                        "Categories", JOptionPane.INFORMATION_MESSAGE);
            } else {
                jComboBoxCategory.setEnabled(true);
            }
        } catch (SQLException ex) {
            // Leave the dropdown disabled so an unsuccessful load cannot be used.
            JOptionPane.showMessageDialog(this,
                    "Could not load categories. Check that MySQL is running, then log in again.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelTitle = new javax.swing.JLabel();
        jComboBoxCategory = new javax.swing.JComboBox<>();
        jLabelCategory = new javax.swing.JLabel();
        jLabelDescription = new javax.swing.JLabel();
        jLabelQuantity = new javax.swing.JLabel();
        jLabelUnitCost = new javax.swing.JLabel();
        jTextFieldDescription = new javax.swing.JTextField();
        jTextFieldUnitCost = new javax.swing.JTextField();
        jTextFieldQuantity = new javax.swing.JTextField();
        jButtonRemoveItem = new javax.swing.JButton();
        jButtonAddItem = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableItems = new javax.swing.JTable();
        jLabelTotal = new javax.swing.JLabel();
        jTextFieldTotal = new javax.swing.JTextField();
        jLabelNotes = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaNotes = new javax.swing.JTextArea();
        jButtonClear = new javax.swing.JButton();
        jButtonSubmit = new javax.swing.JButton();

        jLabelTitle.setText("New Procurement Request");

        jComboBoxCategory.setModel(new javax.swing.DefaultComboBoxModel<String>(
            new String[] {"Select category"}
        ));

        jLabelCategory.setText("Category");

        jLabelDescription.setText("Description");

        jLabelQuantity.setText("Quantity");

        jLabelUnitCost.setText("Estimated Unit Cost");

        jTextFieldUnitCost.setText("0.00");

        jTextFieldQuantity.setText("1");
        jTextFieldQuantity.addActionListener(this::jTextFieldQuantityActionPerformed);

        jButtonRemoveItem.setText("Remove Selected");

        jButtonAddItem.setText("Add Item");

        jTableItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Category", "Description", "Quantity", "Unit Cost", "Line Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableItems);

        jLabelTotal.setText("Total");

        jTextFieldTotal.setEditable(false);
        jTextFieldTotal.setText("0.00");

        jLabelNotes.setText("Notes (optional)");

        jTextAreaNotes.setColumns(20);
        jTextAreaNotes.setLineWrap(true);
        jTextAreaNotes.setRows(5);
        jTextAreaNotes.setWrapStyleWord(true);
        jScrollPane2.setViewportView(jTextAreaNotes);

        jButtonClear.setText("Clear");

        jButtonSubmit.setText("Submit Request");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelQuantity, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelCategory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBoxCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextFieldDescription)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButtonAddItem, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabelUnitCost, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jTextFieldUnitCost, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(23, 23, 23)
                                        .addComponent(jButtonRemoveItem, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addGap(15, 15, 15))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextFieldTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(82, 82, 82))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(343, 343, 343)
                        .addComponent(jButtonClear, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                        .addComponent(jButtonSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2)))
                    .addComponent(jScrollPane1))
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitle)
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelCategory))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDescription)
                    .addComponent(jTextFieldDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelQuantity)
                    .addComponent(jLabelUnitCost)
                    .addComponent(jTextFieldQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldUnitCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAddItem)
                    .addComponent(jButtonRemoveItem))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTotal)
                    .addComponent(jTextFieldTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabelNotes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonClear)
                    .addComponent(jButtonSubmit))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldQuantityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldQuantityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldQuantityActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddItem;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonRemoveItem;
    private javax.swing.JButton jButtonSubmit;
    private javax.swing.JComboBox<String> jComboBoxCategory;
    private javax.swing.JLabel jLabelCategory;
    private javax.swing.JLabel jLabelDescription;
    private javax.swing.JLabel jLabelNotes;
    private javax.swing.JLabel jLabelQuantity;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JLabel jLabelUnitCost;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableItems;
    private javax.swing.JTextArea jTextAreaNotes;
    private javax.swing.JTextField jTextFieldDescription;
    private javax.swing.JTextField jTextFieldQuantity;
    private javax.swing.JTextField jTextFieldTotal;
    private javax.swing.JTextField jTextFieldUnitCost;
    // End of variables declaration//GEN-END:variables
}
