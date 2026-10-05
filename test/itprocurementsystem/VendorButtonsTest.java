package itprocurementsystem;

// These checks use the disposable database populated by WorkflowTest, never the assignment database.
public class VendorButtonsTest {
    // Count successful expectations for a clear terminal result.
    private static int checks;

    // Stop immediately when a feature behaves differently from its stated expectation.
    private static void check(boolean result, String description) {
        if (!result) { throw new AssertionError(description); }
        checks++;
    }

    // Read a private designer control without changing its visibility in the application code.
    private static Object control(VendorPanel panel, String name) throws Exception {
        java.lang.reflect.Field field = VendorPanel.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(panel);
    }

    // Invoke a real navigation button so its listener, row selection and field updates are exercised together.
    private static void click(VendorPanel panel, String name) throws Exception {
        ((javax.swing.JButton) control(panel, name)).doClick();
    }

    // The runner supplies the isolated test URL and runs WorkflowTest first to create fixture accounts.
    public static void main(String[] args) throws Exception {
        // Reject missing or non-test database addresses before changing any records.
        String url = System.getProperty("procurement.test.url", "");
        if (!url.matches("jdbc:mysql://(127\\.0\\.0\\.1|localhost):[0-9]+/procurement_test(\\?.*)?")) {
            throw new IllegalStateException("Use the disposable procurement_test database.");
        }
        // Read fixture identities and create three suppliers reserved for this test.
        int admin;
        int customer;
        int linked;
        try (java.sql.Connection c = DBConnection.getConnection()) {
            admin = Database.id(Database.one(c, "SELECT user_id FROM users WHERE username='test_admin'")[0]);
            customer = Database.id(Database.one(c, "SELECT user_id FROM users WHERE username='test_customer'")[0]);
            linked = Database.id(Database.one(c, "SELECT vendor_id FROM quotations LIMIT 1")[0]);
        }
        // Use the real Admin account's current version so permission checks remain active.
        try (java.sql.Connection c = DBConnection.getConnection()) {
            Session.start(admin, "test_admin", "Test Admin", "Admin", Database.id(Database.one(c,
                    "SELECT session_version FROM users WHERE user_id=?", admin)[0]));
        }
        ManagementDAO dao = new ManagementDAO();
        // ID zero inserts a vendor, matching Save on a cleared editor.
        dao.saveVendor(0, "Button Test .Alpha", "Contact", "100", "alpha@example.invalid", "Address");
        dao.saveVendor(0, "Button Test Beta", "Contact", "200", "beta@example.invalid", "Address");
        dao.saveVendor(0, "Button Test Gamma", "Contact", "300", "gamma@example.invalid", "Address");
        // Run Swing operations on its event dispatch thread, even without a displayed window.
        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    VendorPanel panel = new VendorPanel();
                    javax.swing.JTable table = (javax.swing.JTable) control(panel, "jTableVendors");
                    // Limit navigation to the three named fixtures, then confirm the first result is selected.
                    panel.filterVendors("button test");
                    check(table.getRowCount() == 3 && table.getSelectedRow() == 0, "Find selects the first name match");
                    check(!((javax.swing.JButton) control(panel, "jButtonPrevious")).isEnabled(), "Previous stops at the first row");
                    click(panel, "jButtonLast");
                    check(table.getSelectedRow() == 2, "Last selects the final visible row");
                    check(!((javax.swing.JButton) control(panel, "jButtonNext")).isEnabled(), "Next stops at the last row");
                    click(panel, "jButtonPrevious");
                    check(table.getSelectedRow() == 1, "Previous moves one row back");
                    click(panel, "jButtonNext");
                    check(table.getSelectedRow() == 2, "Next moves one row forward");
                    click(panel, "jButtonFirst");
                    check(table.getSelectedRow() == 0, "First returns to the first row");
                    check(((javax.swing.JTextField) control(panel, "jTextFieldVendorName")).getText().equals("Button Test .Alpha"), "Navigation fills the name field");
                    // Reverse name sorting and ensure visible selection still supplies the matching database record.
                    table.getRowSorter().toggleSortOrder(1);
                    table.getRowSorter().toggleSortOrder(1);
                    click(panel, "jButtonFirst");
                    check(FormSupport.cell(table, 1).equals("Button Test Gamma"), "Navigation respects sorted rows");
                    panel.filterVendors(".");
                    check(table.getRowCount() == 1 && FormSupport.cell(table, 1).equals("Button Test .Alpha"), "Search punctuation is literal");
                    panel.filterVendors("no matching vendor 987654");
                    check(table.getRowCount() == 0, "No-match search shows zero rows");
                    check(!((javax.swing.JButton) control(panel, "jButtonDelete")).isEnabled(), "Empty search disables Delete");
                    check(!((javax.swing.JButton) control(panel, "jButtonFirst")).isEnabled(), "Empty search disables First");
                    panel.filterVendors("");
                    check(table.getRowCount() == table.getModel().getRowCount(), "Blank search restores all rows");
                    click(panel, "jButtonClear");
                    check(table.getSelectedRow() == -1 && ((javax.swing.JTextField) control(panel, "jTextFieldVendorName")).getText().isEmpty(), "Clear prepares a new entry");
                    check(!((javax.swing.JButton) control(panel, "jButton8")).isVisible(), "Default placeholder is hidden");
                    check(!((javax.swing.JButton) control(panel, "jButtonAddVendor")).isVisible(), "Save replaces the old Add button");
                } catch (Exception ex) {
                    // Forward UI failures to the test runner rather than swallowing them on the event thread.
                    throw new RuntimeException(ex);
                }
            }
        });
        // A supplier already referenced by a quotation must remain in the database.
        boolean refused = false;
        try { dao.deleteVendor(linked); }
        catch (IllegalArgumentException expected) { refused = true; }
        check(refused, "Linked supplier cannot be deleted");
        int unused;
        try (java.sql.Connection c = DBConnection.getConnection()) {
            unused = Database.id(Database.one(c, "SELECT vendor_id FROM vendors WHERE vendor_name='Button Test Beta'")[0]);
        }
        // A positive selected ID updates the existing vendor rather than creating another row.
        dao.saveVendor(unused, "Button Test Updated", "New Contact", "250", "updated@example.invalid", "New Address");
        try (java.sql.Connection c = DBConnection.getConnection()) {
            check(Database.one(c, "SELECT vendor_name FROM vendors WHERE vendor_id=?", unused)[0].equals("Button Test Updated"), "Save updates the selected vendor");
        }
        // A requester cannot delete vendors even by calling the DAO directly.
        Session.start(customer, "test_customer", "Test Customer", "Requester");
        refused = false;
        try { dao.deleteVendor(unused); }
        catch (IllegalArgumentException expected) { refused = true; }
        check(refused, "Requester cannot delete a vendor");
        // Restore the Admin session and remove the unused fixture through the real deletion method.
        Session.start(admin, "test_admin", "Test Admin", "Admin");
        dao.deleteVendor(unused);
        try (java.sql.Connection c = DBConnection.getConnection()) {
            check(Database.rows(c, "SELECT vendor_id FROM vendors WHERE vendor_id=?", unused).isEmpty(), "Unused vendor is removed");
            check(!Database.rows(c, "SELECT log_id FROM audit_logs WHERE action='Deleted unused vendor' AND record_id=?", unused).isEmpty(), "Deletion is recorded in the audit trail");
            check(!Database.rows(c, "SELECT vendor_id FROM vendors WHERE vendor_id=?", linked).isEmpty(), "Blocked deletion preserves the linked vendor");
        }
        // Print the total only after every expectation has succeeded.
        System.out.println("Passed " + checks + " vendor button checks.");
    }
}
