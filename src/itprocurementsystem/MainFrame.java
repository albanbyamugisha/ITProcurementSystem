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
    // Reuse panels so leaving a screen does not discard unfinished entries.
    private CustomerQuotationsPanel customerQuotationsPanel;
    private ApprovalPanel approvalPanel;
    private DeliveryPanel deliveryPanel;
    private ServiceCompletionPanel serviceCompletionPanel;
    private InventoryPanel inventoryPanel;
    private VendorPanel vendorPanel;
    private UserManagementPanel userManagementPanel;
    private DepartmentPanel departmentPanel;
    private NotificationsPanel notificationsPanel;
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

        // Customers use request screens; managers maintain records and review decisions.
        // Purchasers arrange quotations, equipment deliveries and service completion.
        boolean customer = "Requester".equals(Session.getRole());
        boolean manager = "Manager".equals(Session.getRole());
        boolean purchaser = "Purchaser".equals(Session.getRole());
        jButtonRequests.setVisible(customer);
        jButtonMyRequests.setVisible(customer);
        jButtonMyQuotations.setVisible(customer);
        jButtonQuotes.setVisible(manager || purchaser);
        jButtonDeliveries.setVisible(purchaser);
        jButtonServices.setVisible(purchaser);
        jButtonInventory.setVisible(manager || purchaser);
        jButtonVendors.setVisible(manager || purchaser);
        jButtonUsers.setVisible(manager);
        jButtonDepartments.setVisible(manager);
        jButtonNotifications.setVisible(true);

        // Each listener opens one panel. SQL methods also check access before saving.
        jButtonLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) { logout(); }
        });
        jButtonMyRequests.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) { showMyRequests(); }
        });
        jButtonQuotes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if ("Manager".equals(Session.getRole())) {
                    if (approvalPanel == null) { approvalPanel = new ApprovalPanel(); }
                    showPanel(approvalPanel);
                }
                else if ("Purchaser".equals(Session.getRole())) {
                    if (quotationPanel == null) { quotationPanel = new QuotationPanel(); }
                    quotationPanel.refreshChoicesIfEmpty();
                    showPanel(quotationPanel);
                }
            }
        });
        jButtonMyQuotations.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (customerQuotationsPanel == null) {
                    customerQuotationsPanel = new CustomerQuotationsPanel();
                }
                showPanel(customerQuotationsPanel);
            }
        });
        jButtonDeliveries.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (deliveryPanel == null) {
                    deliveryPanel = new DeliveryPanel();
                }
                showPanel(deliveryPanel);
            }
        });
        jButtonServices.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (serviceCompletionPanel == null) {
                    serviceCompletionPanel = new ServiceCompletionPanel();
                }
                showPanel(serviceCompletionPanel);
            }
        });
        jButtonInventory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (inventoryPanel == null) {
                    inventoryPanel = new InventoryPanel();
                }
                showPanel(inventoryPanel);
            }
        });
        jButtonVendors.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (vendorPanel == null) {
                    vendorPanel = new VendorPanel();
                }
                showPanel(vendorPanel);
            }
        });
        jButtonUsers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (userManagementPanel == null) {
                    userManagementPanel = new UserManagementPanel();
                }
                showPanel(userManagementPanel);
            }
        });
        jButtonDepartments.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (departmentPanel == null) {
                    departmentPanel = new DepartmentPanel();
                }
                showPanel(departmentPanel);
            }
        });
        jButtonNotifications.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (notificationsPanel == null) {
                    notificationsPanel = new NotificationsPanel();
                }
                showPanel(notificationsPanel);
            }
        });
        // Make room for large panels, while keeping scrollbars on smaller screens.
        java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.min(1200, screen.width - 60), Math.min(850, screen.height - 80));

        // Open this window in the middle of the screen.
        setLocationRelativeTo(null);
    }

    // A scroll pane keeps every control reachable when a panel is taller than the window.
    private void showPanel(javax.swing.JPanel panel) {
        jPanelContent.removeAll();
        jPanelContent.setLayout(new java.awt.BorderLayout());
        jPanelContent.add(new javax.swing.JScrollPane(panel), java.awt.BorderLayout.CENTER);
        jPanelContent.revalidate();
        jPanelContent.repaint();
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
        jButtonMyQuotations = new javax.swing.JButton();
        jButtonVendors = new javax.swing.JButton();
        jButtonServices = new javax.swing.JButton();
        jButtonUsers = new javax.swing.JButton();
        jButtonDepartments = new javax.swing.JButton();
        jButtonNotifications = new javax.swing.JButton();
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

        jButtonMyQuotations.setText("My Quotations");

        jButtonVendors.setText("Vendors");

        jButtonServices.setText("Services");

        jButtonUsers.setText("Users");

        jButtonDepartments.setText("Departments");

        jButtonNotifications.setText("Notifications");

        javax.swing.GroupLayout jPanelSidebarLayout = new javax.swing.GroupLayout(jPanelSidebar);
        jPanelSidebar.setLayout(jPanelSidebarLayout);
        jPanelSidebarLayout.setHorizontalGroup(
            jPanelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSidebarLayout.createSequentialGroup()
                .addGroup(jPanelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonRequests, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonMyRequests, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonQuotes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonDeliveries, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonInventory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonMyQuotations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonVendors, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonServices, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonUsers, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelSidebarLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelWelcome, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelRole, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jButtonDepartments, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonNotifications, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelSidebarLayout.setVerticalGroup(
            jPanelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSidebarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelWelcome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelRole)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRequests)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonMyRequests)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonQuotes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDeliveries)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonInventory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonMyQuotations)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonVendors)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonServices)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonUsers)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDepartments)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonNotifications)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addComponent(jButtonLogout)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelContentLayout = new javax.swing.GroupLayout(jPanelContent);
        jPanelContent.setLayout(jPanelContentLayout);
        jPanelContentLayout.setHorizontalGroup(
            jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
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
                .addGap(18, 18, 18)
                .addComponent(jPanelContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(385, Short.MAX_VALUE)
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
        // No extra action is needed here; the form connects its buttons in the constructor.
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
    private javax.swing.JButton jButtonDepartments;
    private javax.swing.JButton jButtonInventory;
    private javax.swing.JButton jButtonLogout;
    private javax.swing.JButton jButtonMyQuotations;
    private javax.swing.JButton jButtonMyRequests;
    private javax.swing.JButton jButtonNotifications;
    private javax.swing.JButton jButtonQuotes;
    private javax.swing.JButton jButtonRequests;
    private javax.swing.JButton jButtonServices;
    private javax.swing.JButton jButtonUsers;
    private javax.swing.JButton jButtonVendors;
    private javax.swing.JLabel jLabelRole;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelWelcome;
    private javax.swing.JPanel jPanelContent;
    private javax.swing.JPanel jPanelSidebar;
    // End of variables declaration//GEN-END:variables
}
