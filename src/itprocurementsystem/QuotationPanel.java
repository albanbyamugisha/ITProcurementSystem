/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package itprocurementsystem;

// These classes hold supplier objects and let us explain database errors to the user.
import java.util.ArrayList;
import java.sql.SQLException;
import javax.swing.JOptionPane;
// Exact decimal arithmetic avoids rounding errors in money totals.
import java.math.BigDecimal;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.table.DefaultTableModel;

/**
 * We use a JPanel because quotation entry belongs inside the main window.
 * It groups the request, vendor and price controls into one screen.
 * A JPanel does not open a separate window; MainFrame will display it.
 * Extending JPanel gives this form the features of a Swing container.
 *
 * @author alban-byamugisha
 */
public class QuotationPanel extends javax.swing.JPanel {

    // Keep the IDs with the displayed names so we can save the correct supplier later.
    private ArrayList<Vendor> vendors = new ArrayList<Vendor>();

    // Lists preserve database IDs even though the controls show readable text.
    private ArrayList<Integer> requestIds = new ArrayList<Integer>();
    private ArrayList<QuotationItem> quotationItems = new ArrayList<QuotationItem>();
    // Zero means no request's items have been loaded yet.
    private int loadedRequestId;

    /**
     * Creates new form QuotationPanel
     */
    public QuotationPanel() {
        // Create the controls and layout arranged in NetBeans Design view.
        initComponents();

        // The total will be calculated from item prices; users should not type it.
        jTextFieldQuotationTotal.setEditable(false);
        // Wrap long notes at word boundaries so they remain readable.
        jTextAreaSpecs.setLineWrap(true);
        jTextAreaSpecs.setWrapStyleWord(true);
        // Correct the visible caption without changing the Design-view layout.
        jLabel1.setText("Quotation total:");

        // Read supplier names after the dropdown has been created.
        loadVendors();
        loadRequests();
        jButtonSaveQuotation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) { saveQuotation(); }
        });
        jButtonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // Confirm before discarding prices the purchaser has entered.
                if (JOptionPane.showConfirmDialog(QuotationPanel.this, "Clear this quotation?",
                        "Clear Quotation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    clearQuotation();
                }
            }
        });
        jTableQuotationItems.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        // Keep event code outside the generated layout so Design view stays usable.
        jButtonLoadItems.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) { loadItems(); }
        });
        jButtonSetPrice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) { setSelectedPrice(); }
        });
        // Changing the request invalidates previously loaded items and prices.
        jComboBoxRequest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) { resetItems(); }
        });
    }

    // Reset only the form; saved database records are not deleted.
    private void clearQuotation() {
        resetItems();
        jComboBoxRequest.setSelectedIndex(0);
        jComboBoxVendor.setSelectedIndex(0);
        jTextAreaSpecs.setText("");
    }

    // Pass the loaded request ID and item objects to the transaction in the DAO.
    private void saveQuotation() {
        int vendorIndex = jComboBoxVendor.getSelectedIndex() - 1;
        if (loadedRequestId <= 0 || vendorIndex < 0 || vendorIndex >= vendors.size()) {
            JOptionPane.showMessageDialog(this, "Load a request and select a vendor first.");
            return;
        }
        jButtonSaveQuotation.setEnabled(false);
        try {
            int id = new QuotationDAO().saveQuotation(loadedRequestId,
                    vendors.get(vendorIndex).getVendorId(), jTextAreaSpecs.getText().trim(), quotationItems);
            // Clear only after commit succeeds, so a failed attempt keeps the user's work.
            clearQuotation();
            JOptionPane.showMessageDialog(this, "Quotation #" + id + " saved. Request status: Quoted.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not confirm the save. Check the database and saved quotations before retrying. "
                    + "Your entered values have been kept.");
        } finally {
            jButtonSaveQuotation.setEnabled(true);
        }
    }

    // Fill the dropdown with request numbers that still accept quotations.
    private void loadRequests() {
        jComboBoxRequest.removeAllItems();
        jComboBoxRequest.addItem("Select request");
        try {
            requestIds = new QuotationDAO().getOpenRequestIds();
            for (int i = 0; i < requestIds.size(); i++) {
                jComboBoxRequest.addItem("Request #" + requestIds.get(i));
            }
        } catch (SQLException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Could not load requests: " + ex.getMessage());
        }
    }

    // Remove old prices whenever a different request is chosen or reloaded.
    private void resetItems() {
        loadedRequestId = 0;
        quotationItems.clear();
        ((DefaultTableModel) jTableQuotationItems.getModel()).setRowCount(0);
        jTextFieldQuotationTotal.setText("0.00");
        jTextFieldUnitPrice.setText("0.00");
    }

    // Read the chosen request's original items; quantities cannot be edited here.
    private void loadItems() {
        int index = jComboBoxRequest.getSelectedIndex() - 1;
        if (index < 0 || index >= requestIds.size()) {
            JOptionPane.showMessageDialog(this, "Select a request first.");
            return;
        }
        if (!quotationItems.isEmpty() && JOptionPane.showConfirmDialog(this,
                "Reload items and discard entered prices?", "Reload Items",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { return; }
        resetItems();
        try {
            int requestId = requestIds.get(index);
            quotationItems = new QuotationDAO().getRequestItems(requestId);
            DefaultTableModel model = (DefaultTableModel) jTableQuotationItems.getModel();
            for (int i = 0; i < quotationItems.size(); i++) {
                QuotationItem item = quotationItems.get(i);
                model.addRow(new Object[] {item.getDescription(), item.getQuantity(), null, null});
            }
            if (quotationItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No eligible items found. The request may have changed status.");
            } else { loadedRequestId = requestId; }
        } catch (SQLException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Could not load items: " + ex.getMessage());
        }
    }

    // Apply a price to one selected item and recalculate every line's total.
    private void setSelectedPrice() {
        int row = jTableQuotationItems.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an item row first.");
            return;
        }
        try {
            int index = jTableQuotationItems.convertRowIndexToModel(row);
            QuotationItem item = quotationItems.get(index);
            item.setUnitPrice(new BigDecimal(jTextFieldUnitPrice.getText().trim()));
            DefaultTableModel model = (DefaultTableModel) jTableQuotationItems.getModel();
            model.setValueAt(item.getUnitPrice(), index, 2);
            model.setValueAt(item.getLineTotal(), index, 3);
            BigDecimal total = new BigDecimal("0.00");
            for (int i = 0; i < quotationItems.size(); i++) {
                total = total.add(quotationItems.get(i).getLineTotal());
            }
            jTextFieldQuotationTotal.setText(total.toPlainString());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a numeric price, for example 1250.50.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Fill the vendor dropdown using the supplier records stored in MySQL.
    private void loadVendors() {
        vendors.clear();
        jComboBoxVendor.removeAllItems();
        // This first entry is an instruction, not a supplier we can save.
        jComboBoxVendor.addItem("Select vendor");
        jComboBoxVendor.setEnabled(false);
        try {
            VendorDAO vendorDAO = new VendorDAO();
            vendors = vendorDAO.getAllVendors();
            // Keep the names in the same order as the objects containing their IDs.
            // Dropdown index 1 corresponds to list index 0 because of the first entry.
            for (int i = 0; i < vendors.size(); i++) {
                jComboBoxVendor.addItem(vendors.get(i).getVendorName());
            }
            if (vendors.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No suppliers are saved yet. Add suppliers before recording a quotation.",
                        "Vendors", JOptionPane.INFORMATION_MESSAGE);
            } else {
                jComboBoxVendor.setEnabled(true);
            }
        } catch (SQLException ex) {
            // A disabled dropdown prevents using an incomplete supplier list.
            JOptionPane.showMessageDialog(this,
                    "Could not load suppliers. Check that MySQL is running and reopen the screen.",
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
        jLabelRequest = new javax.swing.JLabel();
        jLabelVendor = new javax.swing.JLabel();
        jComboBoxRequest = new javax.swing.JComboBox<>();
        jComboBoxVendor = new javax.swing.JComboBox<>();
        jButtonLoadItems = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableQuotationItems = new javax.swing.JTable();
        jLabelUnitPrice = new javax.swing.JLabel();
        jTextFieldUnitPrice = new javax.swing.JTextField();
        jButtonSetPrice = new javax.swing.JButton();
        jLabelSpecs = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaSpecs = new javax.swing.JTextArea();
        jButtonClear = new javax.swing.JButton();
        jButtonSaveQuotation = new javax.swing.JButton();
        jTextFieldQuotationTotal = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        jLabelTitle.setText("Record Quotation");

        jLabelRequest.setText("Request");

        jLabelVendor.setText("Vendor");

        jComboBoxRequest.setModel(new javax.swing.DefaultComboBoxModel<String>(
            new String[] {"Select request"}
        ));

        jComboBoxVendor.setModel(new javax.swing.DefaultComboBoxModel<String>(
            new String[] {"Select vendor"}
        ));

        jButtonLoadItems.setText("Load Items");

        jTableQuotationItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item Description", "Quantity", "Unit Price", "Line Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableQuotationItems);

        jLabelUnitPrice.setText("Unit Price of Selected Item");

        jTextFieldUnitPrice.setText("0.00");

        jButtonSetPrice.setText("Set Price");

        jLabelSpecs.setText("Specifications / notes (optional)");

        jTextAreaSpecs.setColumns(20);
        jTextAreaSpecs.setRows(5);
        jScrollPane2.setViewportView(jTextAreaSpecs);

        jButtonClear.setText("Clear");

        jButtonSaveQuotation.setText("Save Quotation");

        jTextFieldQuotationTotal.setText("0.00");

        jLabel1.setText("QoutationTotal:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(132, 132, 132)
                        .addComponent(jLabelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelSpecs)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelUnitPrice)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonSetPrice)
                                .addGap(30, 30, 30)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldQuotationTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabelVendor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabelRequest, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBoxVendor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jComboBoxRequest, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addComponent(jButtonLoadItems))
                            .addComponent(jScrollPane2)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonClear)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonSaveQuotation)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelRequest)
                    .addComponent(jComboBoxRequest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonLoadItems))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelVendor)
                    .addComponent(jComboBoxVendor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelUnitPrice)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonSetPrice)
                        .addComponent(jTextFieldQuotationTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelSpecs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonClear)
                    .addComponent(jButtonSaveQuotation))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonLoadItems;
    private javax.swing.JButton jButtonSaveQuotation;
    private javax.swing.JButton jButtonSetPrice;
    private javax.swing.JComboBox<String> jComboBoxRequest;
    private javax.swing.JComboBox<String> jComboBoxVendor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelRequest;
    private javax.swing.JLabel jLabelSpecs;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelUnitPrice;
    private javax.swing.JLabel jLabelVendor;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableQuotationItems;
    private javax.swing.JTextArea jTextAreaSpecs;
    private javax.swing.JTextField jTextFieldQuotationTotal;
    private javax.swing.JTextField jTextFieldUnitPrice;
    // End of variables declaration//GEN-END:variables
}
