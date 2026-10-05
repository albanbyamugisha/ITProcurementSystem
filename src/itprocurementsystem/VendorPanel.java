// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

/**
 * A JPanel is used because vendor management belongs inside MainFrame.
 * It groups the related controls without opening another window.
 * MainFrame provides the title bar and navigation for this panel.
 *
 * @author alban-byamugisha
 */
// Define VendorPanel as a JPanel: it groups this task's controls inside MainFrame rather than opening
// another window.
public class VendorPanel extends javax.swing.JPanel {
    // The DAO validates changes and checks staff permissions before accessing MySQL.
    private final ManagementDAO management = new ManagementDAO();
    // A sorter filters the visible table while retaining each vendor's original database ID.
    private javax.swing.table.TableRowSorter<javax.swing.table.TableModel> sorter;

    // A JPanel keeps vendor entry inside MainFrame, with the same navigation and login session.
    public VendorPanel() {
        // Create the controls arranged in NetBeans before connecting their actions.
        initComponents();
        // Save replaces the two older actions; the unused default button has no purpose on this screen.
        jButtonAddVendor.setVisible(false);
        jButtonUpdateVendor.setVisible(false);
        jButton8.setVisible(false);
        // Keep filtering and navigation based on the rows currently visible to the user.
        sorter = new javax.swing.table.TableRowSorter<javax.swing.table.TableModel>(jTableVendors.getModel());
        jTableVendors.setRowSorter(sorter);
        // Connect the existing dragged controls to the actions below.
        connect(btnsave, "save");
        connect(jButtonDelete, "delete");
        connect(jButtonFind, "find");
        connect(jButtonFirst, "first");
        connect(jButtonPrevious, "previous");
        connect(jButtonNext, "next");
        connect(jButtonLast, "last");
        connect(jButtonExit, "exit");
        connect(jButtonClear, "clear");
        connect(jButtonRefresh, "refresh");
        // Selecting a row, including through navigation, copies that vendor into the editing fields.
        jTableVendors.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent event) {
                // Wait until the selection settles rather than reacting to intermediate selection events.
                if (!event.getValueIsAdjusting()) {
                    // FormSupport converts visible row positions to model positions after sorting or filtering.
                    jTextFieldVendorName.setText(FormSupport.cell(jTableVendors, 1).toString());
                    jTextFieldContactPerson.setText(FormSupport.cell(jTableVendors, 2).toString());
                    jTextFieldPhone.setText(FormSupport.cell(jTableVendors, 3).toString());
                    jTextFieldEmail.setText(FormSupport.cell(jTableVendors, 4).toString());
                    jTextAreaAddress.setText(FormSupport.cell(jTableVendors, 5).toString());
                    // Prevent navigation beyond the first or last visible record.
                    updateButtons();
                }
            }
        });
        // Load the initial vendor list; report a connection or permission failure in this panel.
        try { reload(); }
        catch (Exception ex) { FormSupport.error(this, ex); }
        // An empty or unavailable list must leave Delete and navigation disabled.
        updateButtons();
    }

    // Each button supplies a short action name, avoiding a separate listener implementation for every button.
    private void connect(javax.swing.JButton button, final String action) {
        // Swing calls this listener when the connected button is clicked.
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Handle validation and database errors without closing the application.
                try {
                    // Save creates a vendor when no row is selected, otherwise it updates the selected ID.
                    if (action.equals("save")) { saveVendor(); }
                    // Delete requests confirmation before contacting the database.
                    else if (action.equals("delete")) { deleteVendor(); }
                    // Find asks for a name and filters the table without changing database records.
                    else if (action.equals("find")) { findVendor(); }
                    // The first visible record has index zero.
                    else if (action.equals("first")) { selectRow(0); }
                    // Move one visible record backward or forward from the current selection.
                    else if (action.equals("previous")) { selectRow(jTableVendors.getSelectedRow() - 1); }
                    else if (action.equals("next")) { selectRow(jTableVendors.getSelectedRow() + 1); }
                    // The final visible index is one less than the number of visible rows.
                    else if (action.equals("last")) { selectRow(jTableVendors.getRowCount() - 1); }
                    // Clear prepares a new entry; Refresh reloads the full saved list and removes a search.
                    else if (action.equals("clear")) { clearFields(); }
                    else if (action.equals("refresh")) { reload(); }
                    // Exit returns to the welcome panel without logging the user out.
                    else if (action.equals("exit")) { exitPanel(); }
                } catch (Exception ex) {
                    // Display the actual error beside the vendor controls.
                    FormSupport.error(VendorPanel.this, ex);
                }
            }
        });
    }

    // Refresh removes a search and replaces rows only after the database read succeeds.
    private void reload() throws java.sql.SQLException {
        // Read first so a failed query does not erase the existing table or typed values.
        java.util.ArrayList<Object[]> rows = management.vendors();
        // Remove the name filter so newly saved vendors can appear in the full list.
        sorter.setRowFilter(null);
        // Fill keeps the designer's headings and makes selection single-row only.
        FormSupport.fill(jTableVendors, rows);
        // No selected record means the next Save creates a new vendor.
        clearFields();
    }

    // Clear affects only the editing controls, never saved vendor records.
    private void clearFields() {
        // Remove the selected ID before clearing fields to enter a new vendor.
        jTableVendors.clearSelection();
        // Empty all five fields so values from the previous vendor are not reused.
        jTextFieldVendorName.setText("");
        jTextFieldContactPerson.setText("");
        jTextFieldPhone.setText("");
        jTextFieldEmail.setText("");
        jTextAreaAddress.setText("");
        // Update enabled buttons and move typing focus to the name field.
        updateButtons();
        jTextFieldVendorName.requestFocusInWindow();
    }

    // One Save action chooses Add or Update using the selected database ID, not the typed vendor name.
    private void saveVendor() throws java.sql.SQLException {
        // Zero tells the DAO to insert; selectedId safely handles sorted and filtered rows.
        int id = jTableVendors.getSelectedRow() < 0 ? 0 : FormSupport.selectedId(jTableVendors);
        // The DAO checks required fields, email format and staff permissions before saving.
        management.saveVendor(id, jTextFieldVendorName.getText(), jTextFieldContactPerson.getText(),
                jTextFieldPhone.getText(), jTextFieldEmail.getText(), jTextAreaAddress.getText());
        // Clear the selection immediately so an accidental second click cannot update the previous vendor.
        clearFields();
        // Confirm the save separately from the subsequent refresh, which could fail independently.
        javax.swing.JOptionPane.showMessageDialog(this, "Vendor saved successfully.");
        reload();
    }

    // Confirm the chosen vendor's identity before attempting deletion.
    private void deleteVendor() throws java.sql.SQLException {
        // Reject an absent selection and remember the exact ID before opening the confirmation dialog.
        int id = FormSupport.selectedId(jTableVendors);
        // Include the name and ID so vendors sharing a name remain distinguishable.
        int answer = javax.swing.JOptionPane.showConfirmDialog(this,
                "Delete vendor #" + id + " - " + FormSupport.cell(jTableVendors, 1) + "?",
                "Delete Vendor", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
        // No and closing the dialog both leave the saved record untouched.
        if (answer != javax.swing.JOptionPane.YES_OPTION) { return; }
        // Foreign keys prevent removing a supplier used by a quotation or delivery.
        management.deleteVendor(id);
        // Remove the deleted record from the editor, then refresh the saved list.
        clearFields();
        javax.swing.JOptionPane.showMessageDialog(this, "Vendor deleted successfully.");
        reload();
    }

    // A small input dialog avoids adding another search field or frame.
    private void findVendor() {
        // Cancel returns null and preserves the current search and selection.
        String name = javax.swing.JOptionPane.showInputDialog(this, "Enter a vendor name (leave blank to show all):", "Find Vendor", javax.swing.JOptionPane.QUESTION_MESSAGE);
        if (name == null) { return; }
        // Apply the search only after an answer is supplied.
        filterVendors(name);
    }

    // Package access allows a focused test to check filtering without opening an input dialog.
    void filterVendors(String name) {
        // Clear the old selection so Save cannot update a vendor hidden by the new filter.
        clearFields();
        // Quote treats punctuation as literal text; (?i) makes the search case-insensitive, and column 1 is the name.
        sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(name.trim()), 1));
        // Select the first match, if one exists, and show how many records remain visible.
        selectRow(0);
    }

    // Navigation uses visible indices, so First and Next also work correctly after Find or column sorting.
    private void selectRow(int row) {
        // Refuse negative or past-the-end positions, including navigation on an empty table.
        if (row >= 0 && row < jTableVendors.getRowCount()) {
            // Selecting the row fires the listener that fills the vendor fields.
            jTableVendors.setRowSelectionInterval(row, row);
            // Scroll the chosen record into view when the table is longer than its viewport.
            jTableVendors.scrollRectToVisible(jTableVendors.getCellRect(row, 0, true));
        }
        // Recalculate boundary buttons even when the requested row did not exist.
        updateButtons();
    }

    // Disable actions that have no valid record to operate on, rather than letting indices wrap around.
    private void updateButtons() {
        // JTable counts visible rows when a RowFilter is active.
        int count = jTableVendors.getRowCount();
        int row = jTableVendors.getSelectedRow();
        // First and Last can choose a starting record even when Clear removed the selection.
        jButtonFirst.setEnabled(count > 0 && row != 0);
        jButtonLast.setEnabled(count > 0 && row != count - 1);
        // Previous stops at the first row; Next also selects the first row from an unselected list.
        jButtonPrevious.setEnabled(row > 0);
        jButtonNext.setEnabled(count > 0 && row < count - 1);
        // Delete requires a selected record; Save remains available for new records.
        jButtonDelete.setEnabled(row >= 0);
        // Show visible and full counts so an empty search is not mistaken for an empty database.
        jLabelCount.setText("Vendors: " + count + " of " + jTableVendors.getModel().getRowCount());
    }

    // Return to the containing main window; no new frame or login session is needed.
    private void exitPanel() {
        // Find the top-level window that contains this panel, even when it is inside a scroll pane.
        java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
        // Only MainFrame owns the application's welcome panel.
        if (window instanceof MainFrame) {
            // Discard unsaved editor values when leaving the vendor task.
            clearFields();
            ((MainFrame) window).showWelcome();
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

        jLabelTitle = new javax.swing.JLabel();
        jLabelVendorName = new javax.swing.JLabel();
        jLabelContactName = new javax.swing.JLabel();
        jLabelPhone = new javax.swing.JLabel();
        jLabelEmail = new javax.swing.JLabel();
        jLabelAddress = new javax.swing.JLabel();
        jButtonAddVendor = new javax.swing.JButton();
        jButtonUpdateVendor = new javax.swing.JButton();
        jButtonClear = new javax.swing.JButton();
        jButtonRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableVendors = new javax.swing.JTable();
        jLabelCount = new javax.swing.JLabel();
        jTextFieldEmail = new javax.swing.JTextField();
        jTextFieldVendorName = new javax.swing.JTextField();
        jTextFieldContactPerson = new javax.swing.JTextField();
        jTextFieldPhone = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaAddress = new javax.swing.JTextArea();
        btnsave = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButtonFind = new javax.swing.JButton();
        jButtonFirst = new javax.swing.JButton();
        jButtonPrevious = new javax.swing.JButton();
        jButtonNext = new javax.swing.JButton();
        jButtonLast = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();

        jLabelTitle.setText("Vendor Management");

        jLabelVendorName.setText("Vendor Name:");

        jLabelContactName.setText("Contact person:");

        jLabelPhone.setText("Phone:");

        jLabelEmail.setText("Email:");

        jLabelAddress.setText("Address:");

        // Hide the superseded or unused designer control; Save handles both create and update.
        jButtonAddVendor.setVisible(false);
        jButtonAddVendor.setText("Add Vendor");

        // Hide the superseded or unused designer control; Save handles both create and update.
        jButtonUpdateVendor.setVisible(false);
        jButtonUpdateVendor.setText("Update Selected");

        jButtonClear.setText("Clear");

        jButtonRefresh.setText("Refresh");

        jTableVendors.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Vendor ID", "Vendor Name", "Contact Person", "Phone", "Email", "Address"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableVendors);

        jLabelCount.setText("Vendors: 0");

        jTextFieldVendorName.addActionListener(this::jTextFieldVendorNameActionPerformed);

        jTextAreaAddress.setColumns(20);
        jTextAreaAddress.setRows(5);
        jTextAreaAddress.setLineWrap(true);
        jTextAreaAddress.setWrapStyleWord(true);
        jScrollPane2.setViewportView(jTextAreaAddress);

        btnsave.setText("Save");

        jButtonDelete.setText("Delete");

        jButtonFind.setText("Find");

        jButtonFirst.setText("First");

        jButtonPrevious.setText("Previous");

        jButtonNext.setText("Next");

        jButtonLast.setText("Last");

        // Hide the superseded or unused designer control; Save handles both create and update.
        jButton8.setVisible(false);
        jButton8.setText("jButton8");

        jButtonExit.setText("Exit");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(249, 249, 249)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelAddress)
                                    .addComponent(jLabelEmail)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonAddVendor)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonUpdateVendor)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(54, 54, 54)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldEmail)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(jButtonClear)
                                .addGap(63, 63, 63)
                                .addComponent(jButtonRefresh)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jLabelCount)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelTitle)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelVendorName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldVendorName, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelContactName)
                                    .addComponent(jLabelPhone))
                                .addGap(2, 2, 2)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextFieldContactPerson, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                                    .addComponent(jTextFieldPhone))))))
                .addContainerGap())
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonPrevious)
                    .addComponent(btnsave))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonDelete)
                        .addGap(56, 56, 56)
                        .addComponent(jButtonFind))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonNext)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonLast)))
                .addGap(103, 103, 103)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonFirst)
                    .addComponent(jButton8)
                    .addComponent(jButtonExit))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelVendorName)
                    .addComponent(jLabelEmail)
                    .addComponent(jTextFieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldVendorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelContactName)
                            .addComponent(jLabelAddress)
                            .addComponent(jTextFieldContactPerson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelPhone)
                            .addComponent(jTextFieldPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonRefresh, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonAddVendor)
                        .addComponent(jButtonUpdateVendor)
                        .addComponent(jButtonClear)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelCount)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnsave)
                            .addComponent(jButtonDelete)
                            .addComponent(jButtonFind)
                            .addComponent(jButtonFirst))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonPrevious)
                            .addComponent(jButtonNext)
                            .addComponent(jButtonLast)
                            .addComponent(jButtonExit))
                        .addGap(27, 27, 27))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton8)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Handle the action event from jTextFieldVendorName; any statements below run when that action is
    // raised.
    private void jTextFieldVendorNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldVendorNameActionPerformed
        // No extra action is needed here; the form connects its buttons in the constructor.
    }//GEN-LAST:event_jTextFieldVendorNameActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnsave;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButtonAddVendor;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonFind;
    private javax.swing.JButton jButtonFirst;
    private javax.swing.JButton jButtonLast;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JButton jButtonPrevious;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JButton jButtonUpdateVendor;
    private javax.swing.JLabel jLabelAddress;
    private javax.swing.JLabel jLabelContactName;
    private javax.swing.JLabel jLabelCount;
    private javax.swing.JLabel jLabelEmail;
    private javax.swing.JLabel jLabelPhone;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelVendorName;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableVendors;
    private javax.swing.JTextArea jTextAreaAddress;
    private javax.swing.JTextField jTextFieldContactPerson;
    private javax.swing.JTextField jTextFieldEmail;
    private javax.swing.JTextField jTextFieldPhone;
    private javax.swing.JTextField jTextFieldVendorName;
    // End of variables declaration//GEN-END:variables
}
