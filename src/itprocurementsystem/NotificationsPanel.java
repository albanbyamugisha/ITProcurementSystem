// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

/**
 * A JPanel is used because notification viewing belongs inside MainFrame.
 * It groups the related controls without opening another window.
 * MainFrame provides the title bar and navigation for this panel.
 *
 * @author alban-byamugisha
 */
// Define NotificationsPanel as a JPanel: it groups this task's controls inside MainFrame rather than
// opening another window.
public class NotificationsPanel extends javax.swing.JPanel {
    // The DAO handles validation and SQL; the form handles display.
    // Only this class accesses this field directly. Declare management with type ManagementDAO. Create a
    // ManagementDAO object using the supplied constructor values. The reference or value cannot be
    // reassigned after initialisation.
    private final ManagementDAO management = new ManagementDAO();


    /**
     * Creates new form NotificationsPanel
     */
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public NotificationsPanel() {
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
        jButtonMarkRead.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Mark the selected notification as read; ID zero means all notifications belonging to this account.
                // Read current saved records for this screen and refresh its choices, table or count.
                try { management.markRead(FormSupport.selectedId(jTableNotifications)); reload(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Run this action when the user clicks the button.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonMarkAllRead.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Mark the selected notification as read; ID zero means all notifications belonging to this account.
                // Read current saved records for this screen and refresh its choices, table or count.
                try { management.markRead(0); reload(); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(null, ex); }
            }
        });
        // Show the selected row in the editing fields.
        // Connect the table selection to valueChanged so selecting a row updates the displayed details.
        jTableNotifications.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            // Respond to a table-selection change; the event can fire several times while a selection is
            // adjusting.
            public void valueChanged(javax.swing.event.ListSelectionEvent event) {
                // Wait for a completed selection change before reading the selected table row.
                // Set the text displayed by jTextAreaMessage to `FormSupport.cell(jTableNotifications, 2).toString()`.
                if (!event.getValueIsAdjusting()) { jTextAreaMessage.setText(FormSupport.cell(jTableNotifications,2).toString()); }
            }
        });
        // Changing the filter reloads only this user's messages.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jComboBoxFilter.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Read current saved records for this screen and refresh its choices, table or count.
            public void actionPerformed(java.awt.event.ActionEvent event) { reload(); }
        });
        // Read current saved records for this screen and refresh its choices, table or count.
        reload();

    }

    // Read the unread total separately because a filter may hide some messages.
    // Read current saved records for this screen and refresh its choices, table or count.
    private void reload() {
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Replace the displayed rows using the supplied table model data; keep the existing headings.
            FormSupport.fill(jTableNotifications, management.notifications(jComboBoxFilter.getSelectedItem().toString()));
            // Set the text displayed by jLabelUnreadCount to `"Unread: " + management.unreadCount()`.
            jLabelUnreadCount.setText("Unread: " + management.unreadCount());
            // Set the text displayed by jTextAreaMessage to empty text.
            jTextAreaMessage.setText("");
        // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
        // Show the caught error as a message instead of allowing the event action to fail silently.
        } catch (Exception ex) { FormSupport.error(this, ex); }
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
        // Set jLabelFilter from the following operation: create a non-editable text or image display.
        jLabelFilter = new javax.swing.JLabel();
        // Set jComboBoxFilter from the following operation: create a dropdown selection control.
        jComboBoxFilter = new javax.swing.JComboBox<>();
        // Set jButtonRefresh from the following operation: create a clickable action button.
        jButtonRefresh = new javax.swing.JButton();
        // Set jScrollPane1 from the following operation: create a scrollable viewport for another control.
        jScrollPane1 = new javax.swing.JScrollPane();
        // Set jTableNotifications from the following operation: create a table displaying rows and columns
        // through a model.
        jTableNotifications = new javax.swing.JTable();
        // Set jLabelMessage from the following operation: create a non-editable text or image display.
        jLabelMessage = new javax.swing.JLabel();
        // Set jScrollPane2 from the following operation: create a scrollable viewport for another control.
        jScrollPane2 = new javax.swing.JScrollPane();
        // Set jTextAreaMessage from the following operation: create a multi-line text display or input.
        jTextAreaMessage = new javax.swing.JTextArea();
        // Set jLabelUnreadCount from the following operation: create a non-editable text or image display.
        jLabelUnreadCount = new javax.swing.JLabel();
        // Set jButtonMarkRead from the following operation: create a clickable action button.
        jButtonMarkRead = new javax.swing.JButton();
        // Set jButtonMarkAllRead from the following operation: create a clickable action button.
        jButtonMarkAllRead = new javax.swing.JButton();

        // Set the text displayed by jLabelTitle to "My Notifications".
        jLabelTitle.setText("My Notifications");

        // Set the text displayed by jLabelFilter to "Show".
        jLabelFilter.setText("Show");

        // Give jComboBoxFilter the supplied model, which holds its choices or table data.
        jComboBoxFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All notifications", "Unread", "Read" }));

        // Set the text displayed by jButtonRefresh to "Refresh".
        jButtonRefresh.setText("Refresh");

        // Give jTableNotifications the supplied model, which holds its choices or table data.
        jTableNotifications.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Notification ID", "Date", "Message", "Status"
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
        // Place jTableNotifications inside jScrollPane1 so it can scroll when larger than the available space.
        jScrollPane1.setViewportView(jTableNotifications);

        // Set the text displayed by jLabelMessage to "Selected message".
        jLabelMessage.setText("Selected message");

        // Control whether wrapped text breaks at word boundaries rather than between letters.
        jTextAreaMessage.setWrapStyleWord(true);
        // Control whether jTextAreaMessage wraps long text onto another line.
        jTextAreaMessage.setLineWrap(true);
        // Set whether jTextAreaMessage allows direct typing using false; its value can still be changed by
        // code.
        jTextAreaMessage.setEditable(false);
        // Set the preferred text width of jTextAreaMessage to 20 columns; this is not a text-length limit.
        jTextAreaMessage.setColumns(20);
        // Set the preferred visible height of jTextAreaMessage to 5 text rows.
        jTextAreaMessage.setRows(5);
        // Place jTextAreaMessage inside jScrollPane2 so it can scroll when larger than the available space.
        jScrollPane2.setViewportView(jTextAreaMessage);

        // Set the text displayed by jLabelUnreadCount to "Unread: 0".
        jLabelUnreadCount.setText("Unread: 0");

        // Set the text displayed by jButtonMarkRead to "Mark Selected as Read".
        jButtonMarkRead.setText("Mark Selected as Read");

        // Set the text displayed by jButtonMarkAllRead to "Mark All as Read".
        jButtonMarkAllRead.setText("Mark All as Read");

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
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave spacing between the group and its enclosing container edge.
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jLabelTitle)
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(116, 116, 116))
                    // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave spacing between the group and its enclosing container edge.
                        .addContainerGap()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jScrollPane2)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                // Place jLabelFilter in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jLabelFilter)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(18, 18, 18)
                                // Place jComboBoxFilter in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jComboBoxFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jButtonRefresh)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(15, 15, 15))
                            // Nest a sequential group to place controls one after another.
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                // Place jLabelUnreadCount in this layout group; any following size arguments give minimum, preferred
                                // and maximum sizes.
                                .addComponent(jLabelUnreadCount)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                // Place jButtonMarkRead in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jButtonMarkRead)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                // Place jButtonMarkAllRead in this layout group; any following size arguments give minimum, preferred
                                // and maximum sizes.
                                .addComponent(jButtonMarkAllRead)))))
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap())
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap()
                // Place jLabelMessage in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelMessage)
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
                // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelTitle)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelFilter in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelFilter)
                    // Place jComboBoxFilter in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jComboBoxFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonRefresh))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jLabelMessage in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelMessage)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelUnreadCount in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jLabelUnreadCount)
                    // Place jButtonMarkRead in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonMarkRead)
                    // Place jButtonMarkAllRead in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jButtonMarkAllRead)))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Only this class accesses this field directly. Declare jButtonMarkAllRead with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonMarkAllRead;
    // Only this class accesses this field directly. Declare jButtonMarkRead with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonMarkRead;
    // Only this class accesses this field directly. Declare jButtonRefresh with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonRefresh;
    // Only this class accesses this field directly. Declare jComboBoxFilter with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxFilter;
    // Only this class accesses this field directly. Declare jLabelFilter with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelFilter;
    // Only this class accesses this field directly. Declare jLabelMessage with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelMessage;
    // Only this class accesses this field directly. Declare jLabelTitle with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelTitle;
    // Only this class accesses this field directly. Declare jLabelUnreadCount with type
    // javax.swing.JLabel. Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelUnreadCount;
    // Only this class accesses this field directly. Declare jScrollPane1 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane1;
    // Only this class accesses this field directly. Declare jScrollPane2 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane2;
    // Only this class accesses this field directly. Declare jTableNotifications with type
    // javax.swing.JTable. Java initially uses null for this object reference.
    private javax.swing.JTable jTableNotifications;
    // Only this class accesses this field directly. Declare jTextAreaMessage with type
    // javax.swing.JTextArea. Java initially uses null for this object reference.
    private javax.swing.JTextArea jTextAreaMessage;
    // End of variables declaration//GEN-END:variables
}
