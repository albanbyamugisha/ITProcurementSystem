/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package itprocurementsystem;

// These classes let us respond when the Logout button is clicked.
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * We use a JFrame because this is the application's main window after login.
 * It holds the navigation buttons and the content area for our panels.
 * Keeping those panels in one window lets the user move between tasks easily.
 * Extending JFrame gives this class the basic features of a Swing window.
 *
 * @author alban-byamugisha
 */
public class MainFrame extends javax.swing.JFrame {
    // Keep one request panel so clicking Requests again preserves unfinished entries.
    // null means we have not created the panel yet.
    private RequestPanel requestPanel;
    // Reuse the quotation form so navigating away does not discard entered prices.
    private QuotationPanel quotationPanel;

    // Reuse the history panel, but reload its saved requests each time it opens.
    private MyRequestsPanel myRequestsPanel;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        // Create the components arranged in NetBeans Design view.
        initComponents();

        // Do not open the main window without a verified login.
        if (Session.getUserId() == 0) {
            dispose();
            throw new IllegalStateException("Please log in before opening the main window.");
        }

        // Display the details remembered after a successful database login.
        jLabelWelcome.setText("Welcome, " + Session.getFullName());
        jLabelRole.setText("Role: " + Session.getRole());

        // Begin with all navigation buttons hidden, then show the ones for this role.
        jButtonRequests.setVisible(false);
        jButtonMyRequests.setVisible(false);
        jButtonQuotes.setVisible(false);
        jButtonDeliveries.setVisible(false);

        // A requester submits requests; a manager reviews quotations and approvals.
        if ("Requester".equals(Session.getRole())) {
            jButtonRequests.setVisible(true);
            jButtonMyRequests.setVisible(true);
        } else if ("Manager".equals(Session.getRole())) {
            jButtonQuotes.setVisible(true);
        } else if ("Purchaser".equals(Session.getRole())) {
            // A purchaser works with both quotations and deliveries.
            jButtonQuotes.setVisible(true);
            jButtonDeliveries.setVisible(true);
        }

        // A listener waits for a button click and runs actionPerformed when it happens.
        // We connect it here because no Logout handler was created in Design view.
        // The code stays outside NetBeans' generated layout block.
        jButtonLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // Keep the logout steps in a separate method so they are easy to read.
                logout();
            }
        });

        // Connect the Design-view button without changing NetBeans' generated layout.
        jButtonMyRequests.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showMyRequests();
            }
        });

        // Purchasers record quotations; the manager's approval screen comes later.
        jButtonQuotes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (!"Purchaser".equals(Session.getRole())) {
                    javax.swing.JOptionPane.showMessageDialog(MainFrame.this,
                            "The manager approval screen will be added in the next stage.");
                    return;
                }
                if (quotationPanel == null) { quotationPanel = new QuotationPanel(); }
                // Replace the content area while keeping the sidebar visible.
                jPanelContent.removeAll();
                jPanelContent.setLayout(new java.awt.BorderLayout());
                jPanelContent.add(new javax.swing.JScrollPane(quotationPanel), java.awt.BorderLayout.CENTER);
                jPanelContent.revalidate();
                jPanelContent.repaint();
            }
        });

        // Open this window in the middle of the screen.
        setLocationRelativeTo(null);
    }

    // Display submitted requests in the same content area used by the request form.
    private void showMyRequests() {
        // Check access here too, rather than relying only on a hidden button.
        if (Session.getUserId() <= 0 || !"Requester".equals(Session.getRole())) {
            return;
        }

        if (myRequestsPanel == null) {
            // The constructor loads the list when we create this panel for the first time.
            myRequestsPanel = new MyRequestsPanel();
        } else {
            // Read MySQL again to include requests submitted since the last visit.
            myRequestsPanel.loadRequests();
        }

        // Replace the visible content without discarding the cached request-entry panel.
        // This keeps unfinished item entries available when the user returns to Requests.
        jPanelContent.removeAll();
        jPanelContent.setLayout(new java.awt.BorderLayout());
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(myRequestsPanel);
        jPanelContent.add(scrollPane, java.awt.BorderLayout.CENTER);

        // Recalculate component positions and redraw the new screen.
        jPanelContent.revalidate();
        jPanelContent.repaint();
    }

    // Forget the signed-in user and return to the login window.
    private void logout() {
        Session.clear();

        // Show a fresh login form with empty input fields.
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);

        // Close only this window. The application continues with LoginFrame.
        dispose();
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
        jPanelSidebar = new javax.swing.JPanel();
        jLabelWelcome = new javax.swing.JLabel();
        jLabelRole = new javax.swing.JLabel();
        jButtonLogout = new javax.swing.JButton();
        jButtonDeliveries = new javax.swing.JButton();
        jButtonRequests = new javax.swing.JButton();
        jButtonMyRequests = new javax.swing.JButton();
        jButtonQuotes = new javax.swing.JButton();
        jButtonInventory = new javax.swing.JButton();
        jPanelContent = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabelTitle.setText("IT Procurement Request System");

        jLabelWelcome.setText("Welcome");

        jLabelRole.setText("Role");

        jButtonLogout.setText("Logout");
        jButtonLogout.addActionListener(this::jButtonLogoutActionPerformed);

        jButtonDeliveries.setText("Deliveries");

        jButtonRequests.setText("Requests");
        jButtonRequests.addActionListener(this::jButtonRequestsActionPerformed);

        jButtonMyRequests.setText("My Requests");

        jButtonQuotes.setText("Quotations & Approvals");

        jButtonInventory.setText("Inventory");

        javax.swing.GroupLayout jPanelSidebarLayout = new javax.swing.GroupLayout(jPanelSidebar);
        jPanelSidebar.setLayout(jPanelSidebarLayout);
        jPanelSidebarLayout.setHorizontalGroup(
            jPanelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSidebarLayout.createSequentialGroup()
                .addGroup(jPanelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelWelcome, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelRole, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanelSidebarLayout.createSequentialGroup()
                .addGroup(jPanelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonLogout, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonRequests, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelSidebarLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButtonQuotes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jButtonMyRequests, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonDeliveries, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanelSidebarLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jButtonInventory)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelSidebarLayout.setVerticalGroup(
            jPanelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSidebarLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabelWelcome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelRole)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 102, Short.MAX_VALUE)
                .addComponent(jButtonRequests)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonMyRequests)
                .addGap(18, 18, 18)
                .addComponent(jButtonQuotes)
                .addGap(21, 21, 21)
                .addComponent(jButtonDeliveries)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonInventory)
                .addGap(53, 53, 53)
                .addComponent(jButtonLogout)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelContentLayout = new javax.swing.GroupLayout(jPanelContent);
        jPanelContent.setLayout(jPanelContentLayout);
        jPanelContentLayout.setHorizontalGroup(
            jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 504, Short.MAX_VALUE)
        );
        jPanelContentLayout.setVerticalGroup(
            jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelSidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                .addComponent(jPanelContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelTitle)
                .addGap(224, 224, 224))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabelTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelSidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(30, 30, 30))
                    .addComponent(jPanelContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonRequestsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRequestsActionPerformed
        // Check the role before opening the form, as well as hiding its button.
        if (!"Requester".equals(Session.getRole())) {
            return;
        }

        // Create the form only on the first click. Later clicks reuse the same object.
        if (requestPanel == null) {
            requestPanel = new RequestPanel();
        }

        // Remove the previous screen from the content area, leaving the sidebar alone.
        jPanelContent.removeAll();

        // BorderLayout lets the new screen fill the available content area.
        jPanelContent.setLayout(new java.awt.BorderLayout());

        // A scroll pane keeps every field reachable when the window is small.
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(requestPanel);
        jPanelContent.add(scrollPane, java.awt.BorderLayout.CENTER);

        // Recalculate the layout, then redraw the content area to show the form.
        jPanelContent.revalidate();
        jPanelContent.repaint();
    }//GEN-LAST:event_jButtonRequestsActionPerformed

    private void jButtonLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLogoutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonLogoutActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        // Swing opens windows on its event thread so screen updates run in order.
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Run File must also require login rather than opening the main window directly.
                if (Session.getUserId() == 0) {
                    new LoginFrame().setVisible(true);
                } else {
                    new MainFrame().setVisible(true);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDeliveries;
    private javax.swing.JButton jButtonInventory;
    private javax.swing.JButton jButtonLogout;
    private javax.swing.JButton jButtonMyRequests;
    private javax.swing.JButton jButtonQuotes;
    private javax.swing.JButton jButtonRequests;
    private javax.swing.JLabel jLabelRole;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelWelcome;
    private javax.swing.JPanel jPanelContent;
    private javax.swing.JPanel jPanelSidebar;
    // End of variables declaration//GEN-END:variables
}
