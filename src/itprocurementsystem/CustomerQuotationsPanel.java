/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package itprocurementsystem;

/**
 * We use a JPanel because customer quotation review belongs inside MainFrame.
 * It groups the related controls without opening another window.
 * MainFrame provides the title bar and navigation for this panel.
 *
 * @author alban-byamugisha
 */
public class CustomerQuotationsPanel extends javax.swing.JPanel {
    // This DAO checks ownership, roles and decision status before saving.
    private final DecisionDAO decisions = new DecisionDAO();


    /**
     * Creates new form CustomerQuotationsPanel
     */
    public CustomerQuotationsPanel() {
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
        jButtonLoadQuotations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                try { loadQuotations(); }
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        jButtonAccept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                try { saveDecision(true); }
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        jButtonDecline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                try { saveDecision(false); }
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Show the selected row in the editing fields.
        jTableQuotations.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) { showQuote(); }
            }
        });
        // Changing the request clears the old rows so they cannot be used accidentally.
        jComboBoxRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) { clearDetails(); }
        });
        reload();

    }

    // Populate the choices with records that this user is allowed to view.
    private void reload() {
        try { FormSupport.choices(jComboBoxRequest, decisions.requests(false), "Select request"); clearDetails(); }
        catch (Exception ex) { FormSupport.error(this, ex); }
    }
    private void clearDetails() {
        FormSupport.fill(jTableQuotations, new java.util.ArrayList<Object[]>());
        FormSupport.fill(jTableQuotationItems, new java.util.ArrayList<Object[]>());
        jTextAreaDetails.setText(""); jTextAreaComments.setText("");
        jLabelDecision.setText("Decision: No quotation selected");
    }
    private void loadQuotations() throws java.sql.SQLException {
        int id = FormSupport.choice(jComboBoxRequest);
        FormSupport.fill(jTableQuotations, decisions.quotations(id, false));
        jTextAreaDetails.setText(decisions.details(id, 0, false));
        
    }
    // A selected quotation supplies the item prices and notes shown below it.
    private void showQuote() {
        if (jTableQuotations.getSelectedRow() < 0) { return; }
        try {
            int id = FormSupport.choice(jComboBoxRequest);
            int quote = FormSupport.selectedId(jTableQuotations);
            FormSupport.fill(jTableQuotationItems, decisions.items(id, quote, false));
            jTextAreaDetails.setText(decisions.details(id, quote, false));
            jLabelDecision.setText("Quotation status: " + FormSupport.cell(jTableQuotations,3));
        } catch (Exception ex) { FormSupport.error(this, ex); }
    }
    private void saveDecision(boolean accept) throws java.sql.SQLException {
        int id = FormSupport.choice(jComboBoxRequest);
        int quote = FormSupport.selectedId(jTableQuotations);
        decisions.customerDecision(id, quote, accept, jTextAreaComments.getText());
        jTextAreaComments.setText("");
        loadQuotations();
        javax.swing.JOptionPane.showMessageDialog(this, "Decision saved.");
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
        jButtonLoadQuotations = new javax.swing.JButton();
        jButtonRefresh = new javax.swing.JButton();
        jComboBoxRequest = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableQuotations = new javax.swing.JTable();
        jLabelItems = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableQuotationItems = new javax.swing.JTable();
        jLabelDetails = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaDetails = new javax.swing.JTextArea();
        jLabelComments = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextAreaComments = new javax.swing.JTextArea();
        jLabelDecision = new javax.swing.JLabel();
        jButtonAccept = new javax.swing.JButton();
        jButtonDecline = new javax.swing.JButton();

        jLabelTitle.setText("My Quotations");

        jLabelRequest.setText("My request");

        jButtonLoadQuotations.setText("Load Quotations");

        jButtonRefresh.setText("Refresh");

        jComboBoxRequest.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select request" }));

        jTableQuotations.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Quotation ID", "Vendor", "Total", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableQuotations);

        jLabelItems.setText("Selected quotation items");

        jTableQuotationItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Description", "Quantity", "Unit Price", "Line Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTableQuotationItems);

        jLabelDetails.setText("Quotation details");

        jTextAreaDetails.setWrapStyleWord(true);
        jTextAreaDetails.setLineWrap(true);
        jTextAreaDetails.setEditable(false);
        jTextAreaDetails.setColumns(20);
        jTextAreaDetails.setRows(5);
        jTextAreaDetails.setText("Select a quotation to view its details.");
        jScrollPane3.setViewportView(jTextAreaDetails);

        jLabelComments.setText("Your comments (optional)");

        jTextAreaComments.setWrapStyleWord(true);
        jTextAreaComments.setLineWrap(true);
        jTextAreaComments.setColumns(20);
        jTextAreaComments.setRows(5);
        jScrollPane4.setViewportView(jTextAreaComments);

        jLabelDecision.setText("Decision: No quotation selected");

        jButtonAccept.setText("Accept Quotation");

        jButtonDecline.setText("Decline Quotation");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelTitle)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelRequest)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBoxRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 168, Short.MAX_VALUE)
                                .addComponent(jButtonLoadQuotations)
                                .addGap(46, 46, 46)
                                .addComponent(jButtonRefresh))
                            .addComponent(jScrollPane1)
                            .addComponent(jLabelItems)
                            .addComponent(jLabelDetails)
                            .addComponent(jLabelComments)
                            .addComponent(jScrollPane2))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelDecision)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonDecline)
                        .addGap(32, 32, 32)
                        .addComponent(jButtonAccept))
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabelTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelRequest)
                    .addComponent(jButtonLoadQuotations)
                    .addComponent(jButtonRefresh)
                    .addComponent(jComboBoxRequest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelItems)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelDetails)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelComments)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDecision)
                    .addComponent(jButtonAccept)
                    .addComponent(jButtonDecline))
                .addGap(7, 7, 7))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAccept;
    private javax.swing.JButton jButtonDecline;
    private javax.swing.JButton jButtonLoadQuotations;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JComboBox<String> jComboBoxRequest;
    private javax.swing.JLabel jLabelComments;
    private javax.swing.JLabel jLabelDecision;
    private javax.swing.JLabel jLabelDetails;
    private javax.swing.JLabel jLabelItems;
    private javax.swing.JLabel jLabelRequest;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTableQuotationItems;
    private javax.swing.JTable jTableQuotations;
    private javax.swing.JTextArea jTextAreaComments;
    private javax.swing.JTextArea jTextAreaDetails;
    // End of variables declaration//GEN-END:variables
}
