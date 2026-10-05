// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// These classes handle button clicks, database errors and the table's rows.
// Import ActionEvent for details of a button or dropdown action.
import java.awt.event.ActionEvent;
// Import ActionListener for the callback interface for button and dropdown actions.
import java.awt.event.ActionListener;
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;
// Import JOptionPane for standard Swing message, confirmation and input dialogs.
import javax.swing.JOptionPane;
// Import DefaultTableModel for the rows and columns displayed by a Swing table.
import javax.swing.table.DefaultTableModel;

/**
 * A JPanel is used because this request list appears inside MainFrame.
 * It groups the request table, Refresh button and request count in one screen.
 * A JPanel has no separate title bar or window; MainFrame provides the window.
 * Extending JPanel gives this class the features of a Swing container.
 *
 * @author alban-byamugisha
 */
// Define MyRequestsPanel as a JPanel: it groups this task's controls inside MainFrame rather than
// opening another window.
public class MyRequestsPanel extends javax.swing.JPanel {

    /**
     * Creates new form MyRequestsPanel
     */
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public MyRequestsPanel() {
        // Create the controls and layout arranged in NetBeans Design view.
        // Create the controls and apply the layout stored by NetBeans before reading or changing any of those
        // controls.
        initComponents();
        // These buttons open authorised PDF previews; the PDF viewer provides Save As or Save a Copy.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonRequestPdf.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Build the authorised report and open its temporary PDF preview; saving a copy is handled by the PDF
                // viewer.
                try { ReportActions.export(MyRequestsPanel.this,"request",FormSupport.selectedId(jTableRequests)); }
                // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
                // steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (IllegalArgumentException ex) { FormSupport.error(MyRequestsPanel.this,ex); }
            }
        });

        // Right-click the request control to view its saved supporting documents.
        // Attach a right-click menu that opens supporting documents for the currently selected request.
        AttachmentDAO.addMenu(jTableRequests, new AttachmentDAO.RequestChoice() {
            // Supply the currently selected request ID to the attachment menu without storing an outdated
            // selection.
            // Read column zero of the selected table model row, rejecting an absent selection. Return the
            // resulting value to the caller.
            public int requestId() { return FormSupport.selectedId(jTableRequests); }
        });


        // Connect Refresh outside the layout code maintained by NetBeans.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonRefresh.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Ask the compiler to verify that this method overrides an inherited method.
            @Override
            public void actionPerformed(ActionEvent event) {
                // Refresh the eligible request choices or saved request table for this screen.
                loadRequests();
            }
        });

        // Show the current user's saved requests as soon as this panel is created.
        // Refresh the eligible request choices or saved request table for this screen.
        loadRequests();
    }

    // Read the database again so the table reflects newly submitted requests.
    // Refresh the eligible request choices or saved request table for this screen.
    public void loadRequests() {
        // Clear old rows first so repeated refreshes cannot duplicate them.
        // Declare model with type DefaultTableModel. Read the model holding (DefaultTableModel)jTableRequests
        // data, rather than the control's visual appearance.
        DefaultTableModel model = (DefaultTableModel) jTableRequests.getModel();
        // Set model to 0 rows; zero clears existing rows before reloading.
        model.setRowCount(0);
        // Set the text displayed by jLabelCount to "Loading requests...".
        jLabelCount.setText("Loading requests...");
        // Set whether jButtonRefresh accepts input or actions using false; false disables it.
        jButtonRefresh.setEnabled(false);

        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // The DAO reads only requests belonging to the signed-in requester.
            // Declare requestDAO with type RequestDAO. Create a RequestDAO object using the supplied constructor
            // values.
            RequestDAO requestDAO = new RequestDAO();
            // Declare requests with type ArrayList<RequestSummary>. Read summaries belonging only to the signed-in
            // requester, including the total of saved item prices.
            ArrayList<RequestSummary> requests = requestDAO.getMyRequests();

            // Each summary becomes one row, in the same order as the column headings.
            // Repeat while i is less than the number of entries in requests; initialise the counter once and
            // update it after each pass.
            for (int i = 0; i < requests.size(); i++) {
                // Declare request with type RequestSummary. Read entry i from requests; list positions start at zero.
                RequestSummary request = requests.get(i);
                // Append the supplied values as one table row in column order.
                model.addRow(new Object[] {request.getRequestId(),
                    request.getDateCreated(), request.getStatus(),
                    request.getTotal().setScale(2), request.getNotes()});
            }
            // An empty list is normal when the user has not submitted anything yet.
            // Set the text displayed by jLabelCount to `"Requests: " + requests.size()`.
            jLabelCount.setText("Requests: " + requests.size());
        // Handle SQLException ex from the preceding try block so the failure follows the recovery steps below.
        } catch (SQLException ex) {
            // Do not show a misleading count of zero when the database could not be read.
            // Set the text displayed by jLabelCount to "Requests unavailable".
            jLabelCount.setText("Requests unavailable");
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this,
                    "Could not load requests. Check that MySQL is running, then click Refresh.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        } catch (IllegalArgumentException ex) {
            // The DAO rejects access when there is no signed-in requester.
            // Set the text displayed by jLabelCount to "Please log in as a requester".
            jLabelCount.setText("Please log in as a requester");
            // Display the supplied message in a Swing dialog, using the parent, title and message style when
            // provided.
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Login Required", JOptionPane.WARNING_MESSAGE);
        } finally {
            // Allow another refresh after success or failure.
            // Set whether jButtonRefresh accepts input or actions using true; false disables it.
            jButtonRefresh.setEnabled(true);
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
        // Set jButtonRefresh from the following operation: create a clickable action button.
        jButtonRefresh = new javax.swing.JButton();
        // Set jScrollPane1 from the following operation: create a scrollable viewport for another control.
        jScrollPane1 = new javax.swing.JScrollPane();
        // Set jTableRequests from the following operation: create a table displaying rows and columns through
        // a model.
        jTableRequests = new javax.swing.JTable();
        // Set jLabelCount from the following operation: create a non-editable text or image display.
        jLabelCount = new javax.swing.JLabel();
        // Set jButtonRequestPdf from the following operation: create a clickable action button.
        jButtonRequestPdf = new javax.swing.JButton();

        // Set the text displayed by jLabelTitle to "My Requests".
        jLabelTitle.setText("My Requests");

        // Set the text displayed by jButtonRefresh to "Refresh".
        jButtonRefresh.setText("Refresh");

        // Give jTableRequests the supplied model, which holds its choices or table data.
        jTableRequests.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Request ID", "Date Created", "Status", "Total (UGX)", "Notes"
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
        // Place jTableRequests inside jScrollPane1 so it can scroll when larger than the available space.
        jScrollPane1.setViewportView(jTableRequests);

        // Set the text displayed by jLabelCount to "Requests: 0".
        jLabelCount.setText("Requests: 0");

        // Set the text displayed by jButtonRequestPdf to "View Request PDF".
        jButtonRequestPdf.setText("View Request PDF");

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonRefresh)
                    // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 544, javax.swing.GroupLayout.PREFERRED_SIZE))
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(0, 12, Short.MAX_VALUE))
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap()
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jLabelCount in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelCount))
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            // Nest a sequential group to place controls one after another.
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                // Place jButtonRequestPdf in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jButtonRequestPdf)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(156, 156, 156))
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
                .addComponent(jLabelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jButtonRefresh)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(18, 18, 18)
                // Place jLabelCount in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelCount)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(42, 42, 42)
                // Place jButtonRequestPdf in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jButtonRequestPdf)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(112, 112, 112))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Only this class accesses this field directly. Declare jButtonRefresh with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonRefresh;
    // Only this class accesses this field directly. Declare jButtonRequestPdf with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonRequestPdf;
    // Only this class accesses this field directly. Declare jLabelCount with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelCount;
    // Only this class accesses this field directly. Declare jLabelTitle with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelTitle;
    // Only this class accesses this field directly. Declare jScrollPane1 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane1;
    // Only this class accesses this field directly. Declare jTableRequests with type javax.swing.JTable.
    // Java initially uses null for this object reference.
    private javax.swing.JTable jTableRequests;
    // End of variables declaration//GEN-END:variables
}
