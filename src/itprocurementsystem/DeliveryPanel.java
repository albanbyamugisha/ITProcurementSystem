package itprocurementsystem;

/**
 * We use a JPanel because equipment delivery entry belongs inside MainFrame.
 * It groups the related controls without opening another window.
 * MainFrame provides the title bar and navigation for this panel.
 *
 * @author alban-byamugisha
 */
public class DeliveryPanel extends javax.swing.JPanel {
    // Keep fulfilment SQL and validation separate from the displayed controls.
    private final FulfilmentDAO fulfilment = new FulfilmentDAO();
    private final java.util.ArrayList<DeliveryUnit> units = new java.util.ArrayList<DeliveryUnit>();


    /**
     * Creates new form DeliveryPanel
     */
    public DeliveryPanel() {
        // Build the controls and layout saved in NetBeans Design view.
        initComponents();
        // Run this action when the user clicks the button.
        jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                try { reload(); }
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        jButtonLoadItems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                try { loadItems(); }
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        jButtonAddUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                try { addUnit(); }
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        jButtonRemoveUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                try { removeUnit(); }
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                try { clearDelivery(); }
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        jButtonSaveDelivery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                try { saveDelivery(); }
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // A different request starts a fresh, unsaved delivery list.
        jComboBoxRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) { clearDelivery(); }
        });
        reload();

    }

    // Only approved equipment requests appear in this dropdown.
    private void reload() {
        try { FormSupport.choices(jComboBoxRequest, fulfilment.requests("Equipment"), "Select request"); clearDelivery(); }
        catch (Exception ex) { FormSupport.error(this, ex); }
    }
    private void clearDelivery() {
        units.clear();
        FormSupport.fill(jTableReceivedUnits, new java.util.ArrayList<Object[]>());
        FormSupport.fill(jTableRequestItems, new java.util.ArrayList<Object[]>());
        jTextFieldSerialNumber.setText(""); jTextFieldDeliveryDate.setText("");
        jLabelSupplier.setText("Supplier: Select a request");
    }
    private void loadItems() throws java.sql.SQLException {
        int request = FormSupport.choice(jComboBoxRequest);
        FormSupport.fill(jTableRequestItems, fulfilment.equipmentItems(request));
        jLabelSupplier.setText("Supplier: " + fulfilment.supplier(request,"Equipment"));
    }
    // Each unit needs its own serial number even when several units share an item ID.
    private void addUnit() {
        int item = FormSupport.selectedId(jTableRequestItems);
        DeliveryUnit unit = new DeliveryUnit(item, jTextFieldSerialNumber.getText());
        int staged = 0;
        for (DeliveryUnit existing : units) {
            if (existing.getSerialNumber().equalsIgnoreCase(unit.getSerialNumber())) { throw new IllegalArgumentException("That serial number is already in the list."); }
            if (existing.getItemId() == item) { staged++; }
        }
        int remaining = ((Number) FormSupport.cell(jTableRequestItems,4)).intValue();
        if (staged >= remaining) { throw new IllegalArgumentException("All remaining units for this item are already in the list."); }
        units.add(unit);
        ((javax.swing.table.DefaultTableModel) jTableReceivedUnits.getModel()).addRow(new Object[] {item,FormSupport.cell(jTableRequestItems,1),unit.getSerialNumber()});
        jTextFieldSerialNumber.setText("");
    }
    private void removeUnit() {
        int row = jTableReceivedUnits.getSelectedRow();
        if (row < 0) { throw new IllegalArgumentException("Select a received unit first."); }
        row = jTableReceivedUnits.convertRowIndexToModel(row);
        units.remove(row);
        ((javax.swing.table.DefaultTableModel) jTableReceivedUnits.getModel()).removeRow(row);
    }
    private void saveDelivery() throws java.sql.SQLException {
        fulfilment.saveDelivery(FormSupport.choice(jComboBoxRequest), jTextFieldDeliveryDate.getText(), units);
        reload();
        javax.swing.JOptionPane.showMessageDialog(this,"Delivery and inventory saved.");
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
        jComboBoxRequest = new javax.swing.JComboBox<>();
        jLabelRequest = new javax.swing.JLabel();
        jButtonLoadItems = new javax.swing.JButton();
        jButtonRefresh = new javax.swing.JButton();
        jLabelSupplier = new javax.swing.JLabel();
        jLabelDeliveryDate = new javax.swing.JLabel();
        jTextFieldDeliveryDate = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableRequestItems = new javax.swing.JTable();
        jLabelSelectedItemSerialNumber = new javax.swing.JLabel();
        jTextFieldSerialNumber = new javax.swing.JTextField();
        jButtonAddUnit = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableReceivedUnits = new javax.swing.JTable();
        jButtonRemoveUnit = new javax.swing.JButton();
        jLabelHint = new javax.swing.JLabel();
        jButtonClear = new javax.swing.JButton();
        jButtonSaveDelivery = new javax.swing.JButton();


        jLabelTitle.setText("Equipment Deliveries");

        jComboBoxRequest.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Request" }));

        jLabelRequest.setText("Request");

        jButtonLoadItems.setText("Load Items");

        jButtonRefresh.setText("Refresh");

        jLabelSupplier.setText("Supplier: Select a request");

        jLabelDeliveryDate.setText("Delivery date (YYYY-MM-DD)");

        jTableRequestItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item ID", "Description", "Ordered Qty", "Received Qty", "Remaining Qty"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableRequestItems);

        jLabelSelectedItemSerialNumber.setText("Selected Item Serial Number");

        jButtonAddUnit.setText("Add Unit");

        jTableReceivedUnits.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item ID", "Description", "Serial Number"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTableReceivedUnits);

        jButtonRemoveUnit.setText("Remove Selected Unit");

        jLabelHint.setText("Enter one serial number for each equipment unit received.");

        jButtonClear.setText("Clear");

        jButtonSaveDelivery.setText("Save Delivery");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelSelectedItemSerialNumber)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldSerialNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(jButtonAddUnit)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabelTitle)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabelRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(jComboBoxRequest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jLabelSupplier))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(39, 39, 39)
                                        .addComponent(jButtonLoadItems)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButtonRefresh)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelDeliveryDate)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jTextFieldDeliveryDate))))))
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonRemoveUnit)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelHint)
                                .addGap(27, 27, 27)
                                .addComponent(jButtonClear)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonSaveDelivery)))
                        .addGap(0, 40, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxRequest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelRequest)
                    .addComponent(jButtonLoadItems)
                    .addComponent(jButtonRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSupplier)
                    .addComponent(jLabelDeliveryDate)
                    .addComponent(jTextFieldDeliveryDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSelectedItemSerialNumber)
                    .addComponent(jTextFieldSerialNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAddUnit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonRemoveUnit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelHint)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonClear)
                        .addComponent(jButtonSaveDelivery)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddUnit;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonLoadItems;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JButton jButtonRemoveUnit;
    private javax.swing.JButton jButtonSaveDelivery;
    private javax.swing.JComboBox<String> jComboBoxRequest;
    private javax.swing.JLabel jLabelDeliveryDate;
    private javax.swing.JLabel jLabelHint;
    private javax.swing.JLabel jLabelRequest;
    private javax.swing.JLabel jLabelSelectedItemSerialNumber;
    private javax.swing.JLabel jLabelSupplier;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableReceivedUnits;
    private javax.swing.JTable jTableRequestItems;
    private javax.swing.JTextField jTextFieldDeliveryDate;
    private javax.swing.JTextField jTextFieldSerialNumber;
    // End of variables declaration//GEN-END:variables
}
