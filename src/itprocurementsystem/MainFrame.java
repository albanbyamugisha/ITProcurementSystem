// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// These classes allow the application to respond when the Logout button is clicked.
// Import ActionEvent for details of a button or dropdown action.
import java.awt.event.ActionEvent;
// Import ActionListener for the callback interface for button and dropdown actions.
import java.awt.event.ActionListener;

/**
 * A JFrame is used because this is the application's main window after login.
 * It holds the navigation buttons and the content area for the panels.
 * Keeping those panels in one window lets the user move between tasks easily.
 * Extending JFrame gives this class the basic features of a Swing window.
 *
 * @author alban-byamugisha
 */
// Define MainFrame as a JFrame: it owns a top-level window with a title bar and window controls.
public class MainFrame extends javax.swing.JFrame {
    // Reuse panels so leaving a screen does not discard unfinished entries.
    // Only this class accesses this field directly. Declare customerQuotationsPanel with type
    // CustomerQuotationsPanel. Java initially uses null for this object reference.
    private CustomerQuotationsPanel customerQuotationsPanel;
    // Only this class accesses this field directly. Declare approvalPanel with type ApprovalPanel. Java
    // initially uses null for this object reference.
    private ApprovalPanel approvalPanel;
    // Only this class accesses this field directly. Declare deliveryPanel with type DeliveryPanel. Java
    // initially uses null for this object reference.
    private DeliveryPanel deliveryPanel;
    // Only this class accesses this field directly. Declare serviceCompletionPanel with type
    // ServiceCompletionPanel. Java initially uses null for this object reference.
    private ServiceCompletionPanel serviceCompletionPanel;
    // Only this class accesses this field directly. Declare inventoryPanel with type InventoryPanel. Java
    // initially uses null for this object reference.
    private InventoryPanel inventoryPanel;
    // Only this class accesses this field directly. Declare vendorPanel with type VendorPanel. Java
    // initially uses null for this object reference.
    private VendorPanel vendorPanel;
    // Only this class accesses this field directly. Declare userManagementPanel with type
    // UserManagementPanel. Java initially uses null for this object reference.
    private UserManagementPanel userManagementPanel;
    // Only this class accesses this field directly. Declare departmentPanel with type DepartmentPanel.
    // Java initially uses null for this object reference.
    private DepartmentPanel departmentPanel;
    // Only this class accesses this field directly. Declare notificationsPanel with type
    // NotificationsPanel. Java initially uses null for this object reference.
    private NotificationsPanel notificationsPanel;
    // Keep one request panel so clicking Requests again preserves unfinished entries.
    // null means the application has not created the panel yet.
    // Only this class accesses this field directly. Declare requestPanel with type RequestPanel. Java
    // initially uses null for this object reference.
    private RequestPanel requestPanel;
    // Reuse the quotation form so navigating away does not discard entered prices.
    // Only this class accesses this field directly. Declare quotationPanel with type QuotationPanel. Java
    // initially uses null for this object reference.
    private QuotationPanel quotationPanel;

    // Reuse the history panel, but reload its saved requests each time it opens.
    // Only this class accesses this field directly. Declare myRequestsPanel with type MyRequestsPanel.
    // Java initially uses null for this object reference.
    private MyRequestsPanel myRequestsPanel;
    
    // Only this class accesses this field directly. Declare logger with type java.util.logging.Logger. Its
    // initial value is `java.util.logging.Logger.getLogger(MainFrame.class.getName())`. This field is
    // shared by all instances of the class. The reference or value cannot be reassigned after
    // initialisation.
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());

    /**
     * Creates new form MainFrame
     */
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public MainFrame() {
        // Create the components arranged in NetBeans Design view.
        // Create the controls and apply the layout stored by NetBeans before reading or changing any of those
        // controls.
        initComponents();
        // These buttons open authorised PDF previews; the PDF viewer provides Save As or Save a Copy.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonAccountPdf.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Build the authorised report and open its temporary PDF preview; saving a copy is handled by the PDF
                // viewer.
                try { ReportActions.export(MainFrame.this,"account",Session.getUserId()); }
                // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
                // steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (IllegalArgumentException ex) { FormSupport.error(MainFrame.this,ex); }
            }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonHistoryPdf.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Build the authorised report and open its temporary PDF preview; saving a copy is handled by the PDF
                // viewer.
                try { ReportActions.export(MainFrame.this,"history",Session.getUserId()); }
                // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
                // steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (IllegalArgumentException ex) { FormSupport.error(MainFrame.this,ex); }
            }
        });


        // Do not open the main window without a verified login.
        // Check the session identity before continuing so a missing or different account follows this branch.
        if (Session.getUserId() == 0) {
            // Close this window and release its native resources without clearing other application windows.
            dispose();
            // Stop this operation with an exception: Please log in before opening the main window.
            throw new IllegalStateException("Please log in before opening the main window.");
        }

        // Check the database too: a temporary-password session cannot open this frame directly.
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type java.sql.Connection. Open a JDBC connection; the test URL property can select an
        // isolated database instead of the normal one.
        try (java.sql.Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed roles to continue.
            Database.require(c,"Requester","Manager","Purchaser","Admin");
        // Handle java.sql.SQLException | IllegalArgumentException ex from the preceding try block so the
        // failure follows the recovery steps below.
        } catch (java.sql.SQLException | IllegalArgumentException ex) {
            // Close this window and release its native resources without clearing other application windows.
            dispose();
            // Pass the caught exception back to the caller so the original failure is not treated as success.
            throw new IllegalStateException(ex.getMessage(),ex);
        }
        // Display the details remembered after a successful database login.
        // Set the text displayed by jLabelWelcome to `"Welcome, " + Session.getFullName() + " (" +
        // Session.getRole() + ")"`.
        jLabelWelcome.setText("Welcome, " + Session.getFullName() + " (" + Session.getRole() + ")");

        // Customers use request screens; managers maintain records and review decisions.
        // Purchasers arrange quotations, equipment deliveries and service completion.
        // Declare admin to hold a true-or-false flag. Its initial value is
        // `"Admin".equals(Session.getRole())`.
        boolean admin = "Admin".equals(Session.getRole());
        // Declare customer to hold a true-or-false flag. Its initial value is
        // `"Requester".equals(Session.getRole())`.
        boolean customer = "Requester".equals(Session.getRole());
        // Declare manager to hold a true-or-false flag. Its initial value is
        // `"Manager".equals(Session.getRole())`.
        boolean manager = "Manager".equals(Session.getRole());
        // Declare purchaser to hold a true-or-false flag. Its initial value is
        // `"Purchaser".equals(Session.getRole())`.
        boolean purchaser = "Purchaser".equals(Session.getRole());
        // Set whether jButtonRequests is shown using customer.
        jButtonRequests.setVisible(customer);
        // Set whether jButtonMyRequests is shown using customer.
        jButtonMyRequests.setVisible(customer);
        // Set whether jButtonMyQuotations is shown using customer.
        jButtonMyQuotations.setVisible(customer);
        // Set whether jButtonQuotes is shown using `manager || purchaser`.
        jButtonQuotes.setVisible(manager || purchaser);
        // Set whether jButtonDeliveries is shown using purchaser.
        jButtonDeliveries.setVisible(purchaser);
        // Set whether jButtonServices is shown using purchaser.
        jButtonServices.setVisible(purchaser);
        // Set whether jButtonInventory is shown using `admin || manager || purchaser`.
        jButtonInventory.setVisible(admin || manager || purchaser);
        // Set whether jButtonVendors is shown using `admin || manager || purchaser`.
        jButtonVendors.setVisible(admin || manager || purchaser);
        // Set whether jButtonUsers is shown using admin.
        jButtonUsers.setVisible(admin);
        // Set whether jButtonDepartments is shown using admin.
        jButtonDepartments.setVisible(admin);
        // Set whether jButtonNotifications is shown using true.
        jButtonNotifications.setVisible(true);

        // The shared catalogue hides its editor unless the logged-in role is Admin.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonCatalogue.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Replace the main content area with the supplied panel and refresh the layout and display.
            public void actionPerformed(ActionEvent event) { showPanel(new CataloguePanel()); }
        });
        // Each listener opens one panel. SQL methods also check access before saving.
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonLogout.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Confirm logout, clear the shared session and return to a new login window.
            public void actionPerformed(ActionEvent event) { logout(); }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonMyRequests.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            // Reuse the request-history panel, refresh its saved rows and display it in the main content area.
            public void actionPerformed(ActionEvent event) { showMyRequests(); }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonQuotes.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(ActionEvent event) {
                // Continue with this branch when ("Manager".equals(Session.getRole())).
                if ("Manager".equals(Session.getRole())) {
                    // Create the panel only on its first visit; later visits reuse it and keep unfinished entries.
                    // Set approvalPanel from the following operation: create a ApprovalPanel object using the supplied
                    // constructor values.
                    if (approvalPanel == null) { approvalPanel = new ApprovalPanel(); }
                    // Replace the main content area with the supplied panel and refresh the layout and display.
                    showPanel(approvalPanel);
                }
                // Continue with this branch when ("Purchaser".equals(Session.getRole())).
                else if ("Purchaser".equals(Session.getRole())) {
                    // Create the panel only on its first visit; later visits reuse it and keep unfinished entries.
                    // Set quotationPanel from the following operation: create a QuotationPanel object using the supplied
                    // constructor values.
                    if (quotationPanel == null) { quotationPanel = new QuotationPanel(); }
                    // Reload missing request or vendor choices without discarding an already populated quotation.
                    quotationPanel.refreshChoicesIfEmpty();
                    // Replace the main content area with the supplied panel and refresh the layout and display.
                    showPanel(quotationPanel);
                }
            }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonMyQuotations.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(ActionEvent event) {
                // Create the panel only on its first visit; later visits reuse it and keep unfinished entries.
                if (customerQuotationsPanel == null) {
                    // Set customerQuotationsPanel from the following operation: create a CustomerQuotationsPanel object
                    // using the supplied constructor values.
                    customerQuotationsPanel = new CustomerQuotationsPanel();
                }
                // Replace the main content area with the supplied panel and refresh the layout and display.
                showPanel(customerQuotationsPanel);
            }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonDeliveries.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(ActionEvent event) {
                // Create the panel only on its first visit; later visits reuse it and keep unfinished entries.
                if (deliveryPanel == null) {
                    // Set deliveryPanel from the following operation: create a DeliveryPanel object using the supplied
                    // constructor values.
                    deliveryPanel = new DeliveryPanel();
                }
                // Replace the main content area with the supplied panel and refresh the layout and display.
                showPanel(deliveryPanel);
            }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonServices.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(ActionEvent event) {
                // Create the panel only on its first visit; later visits reuse it and keep unfinished entries.
                if (serviceCompletionPanel == null) {
                    // Set serviceCompletionPanel from the following operation: create a ServiceCompletionPanel object
                    // using the supplied constructor values.
                    serviceCompletionPanel = new ServiceCompletionPanel();
                }
                // Replace the main content area with the supplied panel and refresh the layout and display.
                showPanel(serviceCompletionPanel);
            }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonInventory.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(ActionEvent event) {
                // Create the panel only on its first visit; later visits reuse it and keep unfinished entries.
                if (inventoryPanel == null) {
                    // Set inventoryPanel from the following operation: create a InventoryPanel object using the supplied
                    // constructor values.
                    inventoryPanel = new InventoryPanel();
                }
                // Replace the main content area with the supplied panel and refresh the layout and display.
                showPanel(inventoryPanel);
            }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonVendors.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(ActionEvent event) {
                // Create the panel only on its first visit; later visits reuse it and keep unfinished entries.
                if (vendorPanel == null) {
                    // Set vendorPanel from the following operation: create a VendorPanel object using the supplied
                    // constructor values.
                    vendorPanel = new VendorPanel();
                }
                // Replace the main content area with the supplied panel and refresh the layout and display.
                showPanel(vendorPanel);
            }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonUsers.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(ActionEvent event) {
                // Create the panel only on its first visit; later visits reuse it and keep unfinished entries.
                if (userManagementPanel == null) {
                    // Set userManagementPanel from the following operation: create a UserManagementPanel object using the
                    // supplied constructor values.
                    userManagementPanel = new UserManagementPanel();
                }
                // Replace the main content area with the supplied panel and refresh the layout and display.
                showPanel(userManagementPanel);
            }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonDepartments.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(ActionEvent event) {
                // Create the panel only on its first visit; later visits reuse it and keep unfinished entries.
                if (departmentPanel == null) {
                    // Set departmentPanel from the following operation: create a DepartmentPanel object using the supplied
                    // constructor values.
                    departmentPanel = new DepartmentPanel();
                }
                // Replace the main content area with the supplied panel and refresh the layout and display.
                showPanel(departmentPanel);
            }
        });
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonNotifications.addActionListener(new ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(ActionEvent event) {
                // Create the panel only on its first visit; later visits reuse it and keep unfinished entries.
                if (notificationsPanel == null) {
                    // Set notificationsPanel from the following operation: create a NotificationsPanel object using the
                    // supplied constructor values.
                    notificationsPanel = new NotificationsPanel();
                }
                // Replace the main content area with the supplied panel and refresh the layout and display.
                showPanel(notificationsPanel);
            }
        });
        // Reuse the dragged controls in a scrollable sidebar at runtime.
        // Place visible navigation buttons in a vertical scrollable sidebar so smaller screens can reach every
        // option.
        arrangeNavigation();
        // Build the initial welcome content using the packaged system logo and introductory text.
        showWelcome();
        // Make room for large panels, while keeping scrollbars on smaller screens.
        // Declare screen with type java.awt.Dimension. Its initial value is
        // `java.awt.Toolkit.getDefaultToolkit().getScreenSize()`.
        java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        // Set the window width and height using the supplied limits so it fits the available screen.
        setSize(Math.min(1200, screen.width - 60), Math.min(850, screen.height - 80));

        // Open this window in the middle of the screen.
        // Centre this window on the screen; null means there is no parent window to centre against.
        setLocationRelativeTo(null);
    }

    // BoxLayout stacks only visible buttons, removing the large empty designer gaps.
    // JScrollPane makes the last buttons reachable on a small laptop screen.
    // Place visible navigation buttons in a vertical scrollable sidebar so smaller screens can reach every
    // option.
    private void arrangeNavigation() {
        // Remove all child controls from jPanelSidebar before rebuilding its contents.
        jPanelSidebar.removeAll();
        // Assign the supplied layout manager to jPanelSidebar to control child-component positions and sizes.
        jPanelSidebar.setLayout(new javax.swing.BoxLayout(jPanelSidebar,javax.swing.BoxLayout.Y_AXIS));
        // Apply the supplied border or inner spacing to jPanelSidebar.
        jPanelSidebar.setBorder(javax.swing.BorderFactory.createEmptyBorder(12,10,12,10));
        // Declare buttons with type javax.swing.JButton[]. The following expression supplies its initial
        // value.
        javax.swing.JButton[] buttons = {jButtonRequests,jButtonMyRequests,jButtonMyQuotations,
            jButtonQuotes,jButtonDeliveries,jButtonServices,jButtonInventory,jButtonVendors,
            jButtonUsers,jButtonDepartments,jButtonCatalogue,jButtonNotifications,
            jButtonAccountPdf,jButtonHistoryPdf,jButtonLogout};
        // Process each entry in buttons in turn, referring to the current entry as button.
        for (javax.swing.JButton button : buttons) {
            // Align button horizontally within its parent layout using the supplied alignment value.
            button.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            // Set the maximum layout size of button using the supplied width and height.
            button.setMaximumSize(new java.awt.Dimension(225,36));
            // Give the layout manager the preferred width and height for button.
            button.setPreferredSize(new java.awt.Dimension(225,36));
            // Append button to jPanelSidebar.
            jPanelSidebar.add(button);
            // Continue with this branch when (button.isVisible()).
            // Append `javax.swing.Box.createVerticalStrut(8)` to jPanelSidebar.
            if (button.isVisible()) { jPanelSidebar.add(javax.swing.Box.createVerticalStrut(8)); }
        }
        // Declare heading with type javax.swing.JPanel. Create a container for controls inside an existing
        // window.
        javax.swing.JPanel heading = new javax.swing.JPanel(new java.awt.GridLayout(2,1,0,8));
        // Apply the supplied border or inner spacing to heading.
        heading.setBorder(javax.swing.BorderFactory.createEmptyBorder(12,16,12,16));
        // Append jLabelTitle to heading.
        // Append jLabelWelcome to heading.
        heading.add(jLabelTitle); heading.add(jLabelWelcome);
        // Remove all child controls from getContentPane() before rebuilding its contents.
        getContentPane().removeAll();
        // Assign the supplied layout manager to getContentPane() to control child-component positions and
        // sizes.
        getContentPane().setLayout(new java.awt.BorderLayout());
        // Append `heading, java.awt.BorderLayout.NORTH` to getContentPane().
        getContentPane().add(heading,java.awt.BorderLayout.NORTH);
        // Declare navigation with type javax.swing.JScrollPane. Create a scrollable viewport for another
        // control.
        javax.swing.JScrollPane navigation = new javax.swing.JScrollPane(jPanelSidebar);
        // Give the layout manager the preferred width and height for navigation.
        navigation.setPreferredSize(new java.awt.Dimension(260,500));
        // Scroll by 18 pixels for each small scrollbar movement.
        navigation.getVerticalScrollBar().setUnitIncrement(18);
        // Append `navigation, java.awt.BorderLayout.WEST` to getContentPane().
        getContentPane().add(navigation,java.awt.BorderLayout.WEST);
        // Append `jPanelContent, java.awt.BorderLayout.CENTER` to getContentPane().
        getContentPane().add(jPanelContent,java.awt.BorderLayout.CENTER);
    }

    // A few labels fill the existing content area; there is no additional HomePanel form.
    // Build the initial welcome content using the packaged system logo and introductory text.
    private void showWelcome() {
        // Declare welcome with type javax.swing.JPanel. Create a container for controls inside an existing
        // window.
        javax.swing.JPanel welcome = new javax.swing.JPanel(new java.awt.BorderLayout(12,24));
        // Apply the supplied border or inner spacing to welcome.
        welcome.setBorder(javax.swing.BorderFactory.createEmptyBorder(35,25,35,25));
        // Declare image with type java.net.URL. Its initial value is
        // `getClass().getResource("resources/system-logo.png")`.
        java.net.URL image = getClass().getResource("resources/system-logo.png");
        // Continue with this branch when (image is not equal to no object (null)).
        if (image != null) {
            // Declare icon with type javax.swing.ImageIcon. Create a ImageIcon object using the supplied
            // constructor values.
            javax.swing.ImageIcon icon = new javax.swing.ImageIcon(image);
            // Append `new javax.swing.JLabel(new javax.swing.ImageIcon(icon.getImage().getScaledInstance(440, 165,
            // java.awt.Image.SCALE_SMOOTH))), java.awt.BorderLayout.NORTH` to welcome.
            welcome.add(new javax.swing.JLabel(new javax.swing.ImageIcon(icon.getImage().getScaledInstance(440,165,java.awt.Image.SCALE_SMOOTH))),java.awt.BorderLayout.NORTH);
        }
        // Declare message with type javax.swing.JTextArea. Create a multi-line text display or input.
        javax.swing.JTextArea message = new javax.swing.JTextArea("Welcome to the IT Procurement Request System.\n\n"
                + "Use the menu to open your permitted screens. Browse Catalogue for equipment and services with fixed UGX prices.\n\n"
                + "Open My Account PDF or My History PDF to view your details and save a copy.\n\n"
                + "Your role: " + Session.getRole());
        // Set whether message allows direct typing using false; its value can still be changed by code.
        // Control whether message wraps long text onto another line.
        // Control whether wrapped text breaks at word boundaries rather than between letters.
        message.setEditable(false); message.setLineWrap(true); message.setWrapStyleWord(true);
        // Use the supplied font family, style and point size for message.
        // Control whether the component paints its background; false lets the parent background show through.
        message.setFont(jLabelWelcome.getFont()); message.setOpaque(false);
        // Append `message, java.awt.BorderLayout.CENTER` to welcome.
        welcome.add(message,java.awt.BorderLayout.CENTER);
        // Replace the main content area with the supplied panel and refresh the layout and display.
        showPanel(welcome);
    }

    // A scroll pane keeps every control reachable when a panel is taller than the window.
    // Replace the main content area with the supplied panel and refresh the layout and display.
    private void showPanel(javax.swing.JPanel panel) {
        // Remove all child controls from jPanelContent before rebuilding its contents.
        jPanelContent.removeAll();
        // Assign the supplied layout manager to jPanelContent to control child-component positions and sizes.
        jPanelContent.setLayout(new java.awt.BorderLayout());
        // Append `new javax.swing.JScrollPane(panel), java.awt.BorderLayout.CENTER` to jPanelContent.
        jPanelContent.add(new javax.swing.JScrollPane(panel), java.awt.BorderLayout.CENTER);
        // Ask Swing to recalculate jPanelContent layout after components or sizes have changed.
        jPanelContent.revalidate();
        // Ask Swing to redraw jPanelContent so the visible screen reflects the updated state.
        jPanelContent.repaint();
    }

    // Display submitted requests in the same content area used by the request form.
    // Reuse the request-history panel, refresh its saved rows and display it in the main content area.
    private void showMyRequests() {
        // Check access here too, rather than relying only on a hidden button.
        // Check the session identity before continuing so a missing or different account follows this branch.
        if (Session.getUserId() <= 0 || !"Requester".equals(Session.getRole())) {
            // Stop this method here; no further statements in this call are executed.
            return;
        }

        // Create the panel only on its first visit; later visits reuse it and keep unfinished entries.
        if (myRequestsPanel == null) {
            // The constructor loads the list when this panel is created for the first time.
            // Set myRequestsPanel from the following operation: create a MyRequestsPanel object using the supplied
            // constructor values.
            myRequestsPanel = new MyRequestsPanel();
        } else {
            // Read MySQL again to include requests submitted since the last visit.
            // Refresh the eligible request choices or saved request table for this screen.
            myRequestsPanel.loadRequests();
        }

        // Replace the visible content without discarding the cached request-entry panel.
        // This keeps unfinished item entries available when the user returns to Requests.
        // Remove all child controls from jPanelContent before rebuilding its contents.
        jPanelContent.removeAll();
        // Assign the supplied layout manager to jPanelContent to control child-component positions and sizes.
        jPanelContent.setLayout(new java.awt.BorderLayout());
        // Declare scrollPane with type javax.swing.JScrollPane. Create a scrollable viewport for another
        // control.
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(myRequestsPanel);
        // Append `scrollPane, java.awt.BorderLayout.CENTER` to jPanelContent.
        jPanelContent.add(scrollPane, java.awt.BorderLayout.CENTER);

        // Recalculate component positions and redraw the new screen.
        // Ask Swing to recalculate jPanelContent layout after components or sizes have changed.
        jPanelContent.revalidate();
        // Ask Swing to redraw jPanelContent so the visible screen reflects the updated state.
        jPanelContent.repaint();
    }

    // Forget the signed-in user and return to the login window.
    // Confirm logout, clear the shared session and return to a new login window.
    private void logout() {
        // Clear all remembered login details so no previous account remains signed in.
        Session.clear();

        // Show a fresh login form with empty input fields.
        // Declare loginFrame with type LoginFrame. Create a LoginFrame object using the supplied constructor
        // values.
        LoginFrame loginFrame = new LoginFrame();
        // Set whether loginFrame is shown using true.
        loginFrame.setVisible(true);

        // Close only this window. The application continues with LoginFrame.
        // Close this window and release its native resources without clearing other application windows.
        dispose();
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
        // Set jPanelSidebar from the following operation: create a container for controls inside an existing
        // window.
        jPanelSidebar = new javax.swing.JPanel();
        // Set jLabelWelcome from the following operation: create a non-editable text or image display.
        jLabelWelcome = new javax.swing.JLabel();
        // Set jButtonLogout from the following operation: create a clickable action button.
        jButtonLogout = new javax.swing.JButton();
        // Set jButtonDeliveries from the following operation: create a clickable action button.
        jButtonDeliveries = new javax.swing.JButton();
        // Set jButtonRequests from the following operation: create a clickable action button.
        jButtonRequests = new javax.swing.JButton();
        // Set jButtonMyRequests from the following operation: create a clickable action button.
        jButtonMyRequests = new javax.swing.JButton();
        // Set jButtonQuotes from the following operation: create a clickable action button.
        jButtonQuotes = new javax.swing.JButton();
        // Set jButtonInventory from the following operation: create a clickable action button.
        jButtonInventory = new javax.swing.JButton();
        // Set jButtonMyQuotations from the following operation: create a clickable action button.
        jButtonMyQuotations = new javax.swing.JButton();
        // Set jButtonVendors from the following operation: create a clickable action button.
        jButtonVendors = new javax.swing.JButton();
        // Set jButtonServices from the following operation: create a clickable action button.
        jButtonServices = new javax.swing.JButton();
        // Set jButtonUsers from the following operation: create a clickable action button.
        jButtonUsers = new javax.swing.JButton();
        // Set jButtonDepartments from the following operation: create a clickable action button.
        jButtonDepartments = new javax.swing.JButton();
        // Set jButtonNotifications from the following operation: create a clickable action button.
        jButtonNotifications = new javax.swing.JButton();
        // Set jButtonCatalogue from the following operation: create a clickable action button.
        jButtonCatalogue = new javax.swing.JButton();
        // Set jButtonAccountPdf from the following operation: create a clickable action button.
        jButtonAccountPdf = new javax.swing.JButton();
        // Set jButtonHistoryPdf from the following operation: create a clickable action button.
        jButtonHistoryPdf = new javax.swing.JButton();
        // Set jPanelContent from the following operation: create a container for controls inside an existing
        // window.
        jPanelContent = new javax.swing.JPanel();

        // Choose what the window close button does: EXIT_ON_CLOSE stops the application; DISPOSE_ON_CLOSE
        // closes only this window.
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // Set the text displayed by jLabelTitle to "IT Procurement Request System".
        jLabelTitle.setText("IT Procurement Request System");

        // Set the text displayed by jLabelWelcome to "Welcome".
        jLabelWelcome.setText("Welcome");

        // Set the text displayed by jButtonLogout to "Logout".
        jButtonLogout.setText("Logout");
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonLogout.addActionListener(this::jButtonLogoutActionPerformed);

        // Set the text displayed by jButtonDeliveries to "Deliveries".
        jButtonDeliveries.setText("Deliveries");

        // Set the text displayed by jButtonRequests to "Requests".
        jButtonRequests.setText("Requests");
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonRequests.addActionListener(this::jButtonRequestsActionPerformed);

        // Set the text displayed by jButtonMyRequests to "My Requests".
        jButtonMyRequests.setText("My Requests");

        // Set the text displayed by jButtonQuotes to "Quotations & Approvals".
        jButtonQuotes.setText("Quotations & Approvals");

        // Set the text displayed by jButtonInventory to "Inventory".
        jButtonInventory.setText("Inventory");

        // Set the text displayed by jButtonMyQuotations to "My Orders".
        jButtonMyQuotations.setText("My Orders");

        // Set the text displayed by jButtonVendors to "Vendors".
        jButtonVendors.setText("Vendors");

        // Set the text displayed by jButtonServices to "Services".
        jButtonServices.setText("Services");

        // Set the text displayed by jButtonUsers to "Users".
        jButtonUsers.setText("Users");

        // Set the text displayed by jButtonDepartments to "Departments".
        jButtonDepartments.setText("Departments");

        // Set the text displayed by jButtonNotifications to "Notifications".
        jButtonNotifications.setText("Notifications");

        // Set the text displayed by jButtonCatalogue to "Catalogue".
        jButtonCatalogue.setText("Catalogue");

        // Set the text displayed by jButtonAccountPdf to "My Account PDF".
        jButtonAccountPdf.setText("My Account PDF");

        // Set the text displayed by jButtonHistoryPdf to "My History PDF".
        jButtonHistoryPdf.setText("My History PDF");

        // Declare jPanelSidebarLayout with type javax.swing.GroupLayout. Create a layout manager describing
        // horizontal and vertical arrangements separately.
        javax.swing.GroupLayout jPanelSidebarLayout = new javax.swing.GroupLayout(jPanelSidebar);
        // Assign the supplied layout manager to jPanelSidebar to control child-component positions and sizes.
        jPanelSidebar.setLayout(jPanelSidebarLayout);
        // Define the left-to-right arrangement and widths; the vertical group separately controls heights.
        jPanelSidebarLayout.setHorizontalGroup(
            jPanelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            // Nest a sequential group to place controls one after another.
            .addGroup(jPanelSidebarLayout.createSequentialGroup()
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(jPanelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Place jButtonLogout in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    // Place jButtonRequests in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonRequests, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    // Place jButtonDeliveries in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jButtonDeliveries, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    // Place jButtonInventory in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jButtonInventory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    // Place jButtonMyQuotations in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jButtonMyQuotations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    // Place jButtonServices in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonServices, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    // Place jButtonUsers in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonUsers, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    // Place jButtonDepartments in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jButtonDepartments, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    // Place jButtonNotifications in this layout group; any following size arguments give minimum,
                    // preferred and maximum sizes.
                    .addComponent(jButtonNotifications, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    // Place jButtonCatalogue in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jButtonCatalogue, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    // Place jButtonAccountPdf in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jButtonAccountPdf, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(jPanelSidebarLayout.createSequentialGroup()
                        // Leave spacing between the group and its enclosing container edge.
                        .addContainerGap()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(jPanelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(jPanelSidebarLayout.createSequentialGroup()
                                // Place jLabelWelcome in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jLabelWelcome, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(0, 0, Short.MAX_VALUE))
                            // Place jButtonMyRequests in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jButtonMyRequests, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            // Place jButtonQuotes in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jButtonQuotes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            // Place jButtonVendors in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jButtonVendors, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    // Place jButtonHistoryPdf in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jButtonHistoryPdf, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap())
        );
        // Define the top-to-bottom arrangement and heights; the horizontal group separately controls widths.
        jPanelSidebarLayout.setVerticalGroup(
            jPanelSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            // Nest a sequential group to place controls one after another.
            .addGroup(jPanelSidebarLayout.createSequentialGroup()
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap()
                // Place jLabelWelcome in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelWelcome)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jButtonRequests in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jButtonRequests)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jButtonMyRequests in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jButtonMyRequests)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jButtonQuotes in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jButtonQuotes)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jButtonDeliveries in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jButtonDeliveries)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Place jButtonInventory in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jButtonInventory)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jButtonMyQuotations in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jButtonMyQuotations)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jButtonVendors in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jButtonVendors)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jButtonServices in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jButtonServices)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jButtonUsers in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jButtonUsers)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jButtonDepartments in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jButtonDepartments)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jButtonNotifications in this layout group; any following size arguments give minimum,
                // preferred and maximum sizes.
                .addComponent(jButtonNotifications)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jButtonCatalogue in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jButtonCatalogue)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jButtonAccountPdf in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jButtonAccountPdf)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jButtonHistoryPdf in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jButtonHistoryPdf)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                // Place jButtonLogout in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jButtonLogout)
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap())
        );

        // Declare jPanelContentLayout with type javax.swing.GroupLayout. Create a layout manager describing
        // horizontal and vertical arrangements separately.
        javax.swing.GroupLayout jPanelContentLayout = new javax.swing.GroupLayout(jPanelContent);
        // Assign the supplied layout manager to jPanelContent to control child-component positions and sizes.
        jPanelContent.setLayout(jPanelContentLayout);
        // Define the left-to-right arrangement and widths; the vertical group separately controls heights.
        jPanelContentLayout.setHorizontalGroup(
            jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
            .addGap(0, 0, Short.MAX_VALUE)
        );
        // Define the top-to-bottom arrangement and heights; the horizontal group separately controls widths.
        jPanelContentLayout.setVerticalGroup(
            jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
            .addGap(0, 0, Short.MAX_VALUE)
        );

        // Declare layout with type javax.swing.GroupLayout. Create a layout manager describing horizontal and
        // vertical arrangements separately.
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        // Assign the supplied layout manager to getContentPane() to control child-component positions and
        // sizes.
        getContentPane().setLayout(layout);
        // Define the left-to-right arrangement and widths; the vertical group separately controls heights.
        layout.setHorizontalGroup(
            // Start a parallel group whose children occupy the same region on this axis, using the selected
            // alignment.
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap()
                // Place jPanelSidebar in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jPanelSidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(18, 18, 18)
                // Place jPanelContent in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jPanelContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap())
            // Nest a sequential group to place controls one after another.
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap(385, Short.MAX_VALUE)
                // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelTitle)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(224, 224, 224))
        );
        // Define the top-to-bottom arrangement and heights; the horizontal group separately controls widths.
        layout.setVerticalGroup(
            // Start a parallel group whose children occupy the same region on this axis, using the selected
            // alignment.
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(12, 12, 12)
                // Place jLabelTitle in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabelTitle)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Place jPanelContent in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jPanelContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    // Place jPanelSidebar in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jPanelSidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap())
        );

        // Calculate the window size from the preferred sizes of its controls and layout.
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Handle the action event from jButtonRequests; any statements below run when that action is raised.
    private void jButtonRequestsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRequestsActionPerformed
        // Check the role before opening the form, as well as hiding its button.
        // Continue with this branch when (!"Requester".equals(Session.getRole())).
        if (!"Requester".equals(Session.getRole())) {
            // Stop this method here; no further statements in this call are executed.
            return;
        }

        // Create the form only on the first click. Later clicks reuse the same object.
        // Create the panel only on its first visit; later visits reuse it and keep unfinished entries.
        if (requestPanel == null) {
            // Set requestPanel from the following operation: create a RequestPanel object using the supplied
            // constructor values.
            requestPanel = new RequestPanel();
        }

        // Remove the previous screen from the content area, leaving the sidebar alone.
        // Remove all child controls from jPanelContent before rebuilding its contents.
        jPanelContent.removeAll();

        // BorderLayout lets the new screen fill the available content area.
        // Assign the supplied layout manager to jPanelContent to control child-component positions and sizes.
        jPanelContent.setLayout(new java.awt.BorderLayout());

        // A scroll pane keeps every field reachable when the window is small.
        // Declare scrollPane with type javax.swing.JScrollPane. Create a scrollable viewport for another
        // control.
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(requestPanel);
        // Append `scrollPane, java.awt.BorderLayout.CENTER` to jPanelContent.
        jPanelContent.add(scrollPane, java.awt.BorderLayout.CENTER);

        // Recalculate the layout, then redraw the content area to show the form.
        // Ask Swing to recalculate jPanelContent layout after components or sizes have changed.
        jPanelContent.revalidate();
        // Ask Swing to redraw jPanelContent so the visible screen reflects the updated state.
        jPanelContent.repaint();
    }//GEN-LAST:event_jButtonRequestsActionPerformed

    // Handle the action event from jButtonLogout; any statements below run when that action is raised.
    private void jButtonLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLogoutActionPerformed
        // No extra action is needed here; the form connects its buttons in the constructor.
    }//GEN-LAST:event_jButtonLogoutActionPerformed

    /**
     * @param args the command line arguments
     */
    // Provide the entry point used when this Java class is launched directly.
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Process each entry in javax.swing.UIManager.getInstalledLookAndFeels() in turn, referring to the
            // current entry as info.
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                // Continue with this branch when ("Nimbus".equals(info.getName())).
                if ("Nimbus".equals(info.getName())) {
                    // Apply the chosen installed Swing appearance to subsequently created controls.
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    // Leave the current loop once the required result has been found.
                    break;
                }
            }
        // Handle ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex from the
        // preceding try block so the failure follows the recovery steps below.
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            // Record the exception at SEVERE level so a look-and-feel setup failure can be diagnosed.
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        // Swing opens windows on its event thread so screen updates run in order.
        // Schedule this work on Swing's event dispatch thread so window updates run on the UI thread.
        java.awt.EventQueue.invokeLater(new Runnable() {
            // Execute the work supplied to this Runnable when its caller or event queue schedules it.
            // Ask the compiler to verify that this method overrides an inherited method.
            @Override
            public void run() {
                // Run File must also require login rather than opening the main window directly.
                // Check the session identity before continuing so a missing or different account follows this branch.
                if (Session.getUserId() == 0) {
                    // Set whether new LoginFrame() is shown using true.
                    new LoginFrame().setVisible(true);
                } else {
                    // Set whether new MainFrame() is shown using true.
                    new MainFrame().setVisible(true);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Only this class accesses this field directly. Declare jButtonAccountPdf with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonAccountPdf;
    // Only this class accesses this field directly. Declare jButtonCatalogue with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonCatalogue;
    // Only this class accesses this field directly. Declare jButtonDeliveries with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonDeliveries;
    // Only this class accesses this field directly. Declare jButtonDepartments with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonDepartments;
    // Only this class accesses this field directly. Declare jButtonHistoryPdf with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonHistoryPdf;
    // Only this class accesses this field directly. Declare jButtonInventory with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonInventory;
    // Only this class accesses this field directly. Declare jButtonLogout with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonLogout;
    // Only this class accesses this field directly. Declare jButtonMyQuotations with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonMyQuotations;
    // Only this class accesses this field directly. Declare jButtonMyRequests with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonMyRequests;
    // Only this class accesses this field directly. Declare jButtonNotifications with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonNotifications;
    // Only this class accesses this field directly. Declare jButtonQuotes with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonQuotes;
    // Only this class accesses this field directly. Declare jButtonRequests with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonRequests;
    // Only this class accesses this field directly. Declare jButtonServices with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonServices;
    // Only this class accesses this field directly. Declare jButtonUsers with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonUsers;
    // Only this class accesses this field directly. Declare jButtonVendors with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonVendors;
    // Only this class accesses this field directly. Declare jLabelTitle with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelTitle;
    // Only this class accesses this field directly. Declare jLabelWelcome with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelWelcome;
    // Only this class accesses this field directly. Declare jPanelContent with type javax.swing.JPanel.
    // Java initially uses null for this object reference.
    private javax.swing.JPanel jPanelContent;
    // Only this class accesses this field directly. Declare jPanelSidebar with type javax.swing.JPanel.
    // Java initially uses null for this object reference.
    private javax.swing.JPanel jPanelSidebar;
    // End of variables declaration//GEN-END:variables
}
