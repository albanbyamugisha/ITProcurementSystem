// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Make the public types in java.sql available by short names; this does not create objects.
import java.sql.*;
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;

// Run only against a disposable database using -Dprocurement.test.url=... .
// The tests create fictional accounts; they never need real account passwords.
// Define WorkflowTest as a class that groups its related data and methods.
public class WorkflowTest {
    // Only this class accesses this field directly. Declare admin to hold a whole-number value. Java
    // initially uses zero. This field is shared by all instances of the class.
    // Only this class accesses this field directly. Declare manager to hold a whole-number value. Java
    // initially uses zero. This field is shared by all instances of the class.
    // Only this class accesses this field directly. Declare purchaser to hold a whole-number value. Java
    // initially uses zero. This field is shared by all instances of the class.
    // Only this class accesses this field directly. Declare customer to hold a whole-number value. Java
    // initially uses zero. This field is shared by all instances of the class.
    // Only this class accesses this field directly. Declare other to hold a whole-number value. Java
    // initially uses zero. This field is shared by all instances of the class.
    private static int admin, manager, purchaser, customer, other;
    // Only this class accesses this field directly. Declare checks to hold a whole-number value. Java
    // initially uses zero. This field is shared by all instances of the class.
    private static int checks;
    // Fail the test immediately when its condition is false; otherwise increment the successful-check
    // counter.
    private static void check(boolean value, String message) {
        // Continue with this branch when (!value).
        // Pass the caught exception back to the caller so the original failure is not treated as success.
        if (!value) { throw new AssertionError(message); }
        // Increase checks by one after this item or successful check.
        checks++;
    }
    // Set a synthetic test session for a fixture account; this helper does not perform a real password
    // login.
    // Start the shared session with these verified account details and any supplied version or
    // password-change flag.
    private static void login(int id, String role) { Session.start(id, "test", "Test User", role); }
    // Insert a fictional fixture account into the isolated test database and return its generated ID.
    private static int user(Connection c, String name, String role) throws SQLException {
        // Insert a record into users; question marks receive separately bound values. Return the resulting
        // value to the caller.
        return Database.insert(c, "INSERT INTO users(username,password_hash,full_name,email,role,account_type) VALUES (?,?,?,?,?,'Individual')",
                name, "not-a-login-hash", name, name + "@example.invalid", role);
    }
    // Provide the entry point used when this Java class is launched directly.
    public static void main(String[] args) throws Exception {
        // Declare url to hold text. Its initial value is `System.getProperty("procurement.test.url", "")`.
        String url = System.getProperty("procurement.test.url", "");
        // Continue with this branch when (not
        // url.matches("jdbc:mysql://(127\\.0\\.0\\.1|localhost):[0-9]+/procurement_test(\\?.*)?")).
        // Stop this operation with an exception: Use a disposable procurement_test database.
        if (!url.matches("jdbc:mysql://(127\\.0\\.0\\.1|localhost):[0-9]+/procurement_test(\\?.*)?")) { throw new IllegalStateException("Use a disposable procurement_test database."); }
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Set admin from the following operation: insert a fictional fixture account into the isolated test
            // database and return its generated ID.
            admin = user(c,"test_admin","Admin");
            // Set manager from the following operation: insert a fictional fixture account into the isolated test
            // database and return its generated ID.
            // Set purchaser from the following operation: insert a fictional fixture account into the isolated
            // test database and return its generated ID.
            manager = user(c,"test_manager","Manager"); purchaser = user(c,"test_purchaser","Purchaser");
            // Set customer from the following operation: insert a fictional fixture account into the isolated test
            // database and return its generated ID.
            // Set other from the following operation: insert a fictional fixture account into the isolated test
            // database and return its generated ID.
            customer = user(c,"test_customer","Requester"); other = user(c,"test_other","Requester");
        }
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        // Add missing columns and supporting tables, then seed missing catalogue entries while retaining
        // existing records.
        try (Connection c = DBConnection.getConnection()) { DatabaseSetup.ensureWorkflowTables(c); }
        // Declare dao with type ManagementDAO. Create a ManagementDAO object using the supplied constructor
        // values.
        ManagementDAO dao = new ManagementDAO();
        // Use the fixture account and role as the current test session for the next checks.
        login(admin,"Admin");
        // Save a new or selected department after checking Admin access and duplicate names.
        dao.saveDepartment(0,"Engineering");
        // Declare department to hold a whole-number value. Convert a JDBC numeric value to an int, even when
        // the driver returned another Number type.
        int department = Database.id(dao.departments().get(0)[0]);
        // Save a new or selected department after checking Admin access and duplicate names.
        dao.saveDepartment(department,"Operations");
        // Verify this test expectation: Department update. A false result raises an AssertionError.
        check("Operations".equals(dao.departments().get(0)[1]),"Department update");
        // Validate supplier details, then insert a new vendor for ID zero or update the specified existing
        // vendor.
        dao.saveVendor(0,"Test Supplier","Contact","123","supplier@example.invalid","Test address");
        // Verify this test expectation: Vendor insert. A false result raises an AssertionError.
        check(dao.vendors().size()==1,"Vendor insert");
        // Change another account's role after locking Admin records to prevent conflicting role changes.
        dao.changeRole(other,"Purchaser");
        // Use the fixture account and role as the current test session for the next checks.
        login(other,"Requester"); // A stale session role must not bypass the database role check.
        // Verify this test expectation: Role notification. A false result raises an AssertionError.
        check(dao.notifications("Unread").size()==1,"Role notification");
        // Mark the selected notification as read; ID zero means all notifications belonging to this account.
        dao.markRead(0);
        // Verify this test expectation: Mark all read. A false result raises an AssertionError.
        check(dao.unreadCount()==0,"Mark all read");
        // Use the fixture account and role as the current test session for the next checks.
        login(customer,"Requester");
        // Declare refused to hold a true-or-false flag. Its initial value is false.
        boolean refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Change another account's role after locking Admin records to prevent conflicting role changes.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { dao.changeRole(customer,"Manager"); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Customer cannot promote own account. A false result raises an
        // AssertionError.
        check(refused,"Customer cannot promote own account");
        // Store false in refused for the remaining steps.
        refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Save a new or selected department after checking Admin access and duplicate names.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { dao.saveDepartment(0,"Forbidden"); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Customer cannot manage departments. A false result raises an
        // AssertionError.
        check(refused,"Customer cannot manage departments");
        // Use the fixture account and role as the current test session for the next checks.
        // Change another account's role after locking Admin records to prevent conflicting role changes.
        login(admin,"Admin"); dao.changeRole(other,"Requester");
        // Run the decisions test group against isolated fixtures.
        testDecisions();
        // Run the fulfilment test group against isolated fixtures.
        testFulfilment();
        // Run the attachments test group against isolated fixtures.
        testAttachments();
        // Run the registration test group against isolated fixtures.
        testRegistration();
        // Run the recovery test group against isolated fixtures.
        testRecovery();
        // Run the catalogue test group against isolated fixtures.
        testCatalogue();
        // Run the catalogue cleanup test group against isolated fixtures.
        testCatalogueCleanup();
        // Run the panels test group against isolated fixtures.
        testPanels();
        // Run the decline and rejection test group against isolated fixtures.
        testDeclineAndRejection();
        // Run the concurrent selection test group against isolated fixtures.
        testConcurrentSelection();
        // Run the concurrent deliveries test group against isolated fixtures.
        testConcurrentDeliveries();
        // Run the reports test group against isolated fixtures.
        testReports();
        // Print the completed test count to the terminal.
        System.out.println("Passed " + checks + " checks.");
    }
    // Inspect real PDFs and prove that exports enforce ownership outside the form.
    // Run the reports checks against fixtures in the isolated test database; failures stop the test run.
    private static void testReports() throws Exception {
        // Declare reports with type ReportDAO. Create a ReportDAO object using the supplied constructor
        // values.
        ReportDAO reports = new ReportDAO();
        // Use the fixture account and role as the current test session for the next checks.
        login(customer,"Requester");
        // Declare refused to hold a true-or-false flag. Its initial value is false.
        boolean refused = false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Build account-report sections from explicit public account fields, optionally including procurement
        // history.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { reports.account(other,true); } catch (IllegalArgumentException ex) { refused = true; }
        // Verify this test expectation: Customer cannot export another account. A false result raises an
        // AssertionError.
        check(refused,"Customer cannot export another account");
        // Store false in refused for the remaining steps.
        refused = false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Read account details for an Admin without selecting password hashes.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { reports.users(); } catch (IllegalArgumentException ex) { refused = true; }
        // Verify this test expectation: Customer cannot export user directory. A false result raises an
        // AssertionError.
        check(refused,"Customer cannot export user directory");
        // Declare request to hold a whole-number value.
        int request;
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Read requests records; WHERE limits the rows to the stated conditions and ORDER BY fixes their
            // display order; question marks receive separately bound values.
            request = Database.id(Database.one(c,"SELECT request_id FROM requests WHERE requester_id=? ORDER BY request_id LIMIT 1",customer)[0]);
        }
        // Use the fixture account and role as the current test session for the next checks.
        // Store false in refused for the remaining steps.
        login(other,"Requester"); refused = false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Build the request report through the DAO so the ownership and role checks are exercised.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { reports.request(request); } catch (IllegalArgumentException ex) { refused = true; }
        // Verify this test expectation: Customer cannot export another request. A false result raises an
        // AssertionError.
        check(refused,"Customer cannot export another request");
        // Use the fixture account and role as the current test session for the next checks.
        login(customer,"Requester");
        // Declare folder with type java.io.File. Create a file location without creating the file itself.
        // Create the required folder and any missing parent folders before writing files there.
        java.io.File folder = new java.io.File("/tmp/procurement-pdf-review"); folder.mkdirs();
        // Declare preview with type java.nio.file.Path. Write a temporary PDF for viewing and remove a failed
        // partial file before passing an error onward.
        java.nio.file.Path preview = ReportActions.createPreview(reports.request(request));
        // Verify this test expectation: Preview created before choosing a permanent destination. A false
        // result raises an AssertionError.
        check(java.nio.file.Files.size(preview)>0,"Preview created before choosing a permanent destination");
        // Delete the specified temporary or copied file during cleanup; deleteIfExists also accepts an already
        // absent file.
        java.nio.file.Files.delete(preview);
        // Draw report sections into a PDF with the embedded font, system logo and repeating page details.
        PdfReports.write(new java.io.File(folder,"request.pdf"),reports.request(request));
        // Draw report sections into a PDF with the embedded font, system logo and repeating page details.
        PdfReports.write(new java.io.File(folder,"account-history.pdf"),reports.account(customer,true));
        // Use the fixture account and role as the current test session for the next checks.
        login(admin,"Admin");
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Repeat while i is less than 75; initialise the counter once and update it after each pass.
            // Insert a fictional fixture account into the isolated test database and return its generated ID.
            for (int i=0;i<75;i++) { user(c,"pdf_example_" + i,"Requester"); }
        }
        // Draw report sections into a PDF with the embedded font, system logo and repeating page details.
        PdfReports.write(new java.io.File(folder,"users.pdf"),reports.users());
        // Declare reader with type com.itextpdf.text.pdf.PdfReader. Create a PdfReader object using the
        // supplied constructor values.
        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(new java.io.File(folder,"users.pdf").toString());
        // Verify this test expectation: User directory paginates. A false result raises an AssertionError.
        check(reader.getNumberOfPages()>1,"User directory paginates");
        // Repeat while page is at most reader.getNumberOfPages(); initialise the counter once and update it
        // after each pass.
        for (int page=1;page<=reader.getNumberOfPages();page++) {
            // Declare text to hold text. Its initial value is
            // `com.itextpdf.text.pdf.parser.PdfTextExtractor.getTextFromPage(reader, page)`.
            String text = com.itextpdf.text.pdf.parser.PdfTextExtractor.getTextFromPage(reader,page);
            // Verify this test expectation: Page numbers and table headers repeat. A false result raises an
            // AssertionError.
            check(text.contains("Page " + page) && text.contains("Username"),"Page numbers and table headers repeat");
            // Declare resources with type com.itextpdf.text.pdf.PdfDictionary. Its initial value is
            // `reader.getPageN(page).getAsDict(com.itextpdf.text.pdf.PdfName.RESOURCES)`.
            com.itextpdf.text.pdf.PdfDictionary resources = reader.getPageN(page).getAsDict(com.itextpdf.text.pdf.PdfName.RESOURCES);
            // Verify this test expectation: Logo present on each PDF page. A false result raises an
            // AssertionError.
            check(resources.getAsDict(com.itextpdf.text.pdf.PdfName.XOBJECT)!=null,"Logo present on each PDF page");
            // Verify this test expectation: Personal footer appears on every page. A false result raises an
            // AssertionError.
            check(text.contains("2500603090 - BYAMUGISHA ALBAN - 2025/BSE/062/PS") && !text.contains("Class project | UGX"),"Personal footer appears on every page");
            // Verify this test expectation: No password hashes in PDF. A false result raises an AssertionError.
            check(!text.contains("not-a-login-hash") && !text.contains("password_hash"),"No password hashes in PDF");
        }
        // Close reader and release its associated resources.
        reader.close();
        // Set reader from the following operation: create a PdfReader object using the supplied constructor
        // values.
        reader = new com.itextpdf.text.pdf.PdfReader(new java.io.File(folder,"request.pdf").toString());
        // Declare requestText to hold text. Its initial value is empty text.
        String requestText = "";
        // Repeat while page is at most reader.getNumberOfPages(); initialise the counter once and update it
        // after each pass.
        // Append the extracted PDF page text so assertions can inspect the complete report.
        for (int page=1;page<=reader.getNumberOfPages();page++) { requestText += com.itextpdf.text.pdf.parser.PdfTextExtractor.getTextFromPage(reader,page); }
        // Verify this test expectation: Request PDF excludes supplier unit price. A false result raises an
        // AssertionError.
        check(!requestText.contains("12.50") && requestText.contains("UGX"),"Request PDF excludes supplier unit price");
        // Close reader and release its associated resources.
        reader.close();
    }

    // Cosmetic cleanup must keep the catalogue price and Admin-authored descriptions intact.
    // Run the catalogue cleanup checks against fixtures in the isolated test database; failures stop the
    // test run.
    private static void testCatalogueCleanup() throws Exception {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Declare before to hold one row of values in SELECT or table-column order. Read catalogue records;
            // WHERE limits the rows to the stated conditions.
            Object[] before = Database.one(c,"SELECT catalogue_id,price FROM catalogue WHERE seed_code='SV-006'");
            // Update catalogue values only for rows matching the WHERE condition; question marks receive
            // separately bound values.
            Database.update(c,"UPDATE catalogue SET description=? WHERE catalogue_id=?",
                    "Configure one backup destination; storage and subscriptions excluded [Class demo]",before[0]);
            // Add missing columns and supporting tables, then seed missing catalogue entries while retaining
            // existing records.
            DatabaseSetup.ensureWorkflowTables(c);
            // Declare after to hold one row of values in SELECT or table-column order. Read catalogue records;
            // WHERE limits the rows to the stated conditions; question marks receive separately bound values.
            Object[] after = Database.one(c,"SELECT description,price FROM catalogue WHERE catalogue_id=?",before[0]);
            // Verify this test expectation: Development tag removed from saved catalogue. A false result raises an
            // AssertionError.
            check("Configure one backup destination; storage and subscriptions excluded".equals(after[0]),"Development tag removed from saved catalogue");
            // Verify this test expectation: Cleanup preserves preset prices. A false result raises an
            // AssertionError.
            check(before[1].equals(after[1]),"Cleanup preserves preset prices");
            // Update catalogue values only for rows matching the WHERE condition; question marks receive
            // separately bound values.
            Database.update(c,"UPDATE catalogue SET description='Custom backup specification' WHERE catalogue_id=?",before[0]);
            // Add missing columns and supporting tables, then seed missing catalogue entries while retaining
            // existing records.
            DatabaseSetup.ensureWorkflowTables(c);
            // Read catalogue records; WHERE limits the rows to the stated conditions; question marks receive
            // separately bound values.
            check("Custom backup specification".equals(Database.one(c,"SELECT description FROM catalogue WHERE catalogue_id=?",before[0])[0]),"Cleanup preserves Admin descriptions");
        }
    }

    // Customer prices must come from the catalogue and survive later Admin edits.
    // Run the catalogue checks against fixtures in the isolated test database; failures stop the test run.
    private static void testCatalogue() throws Exception {
        // Use the fixture account and role as the current test session for the next checks.
        login(customer,"Requester");
        // Declare items with type ArrayList<RequestItem>. Create an initially empty resizable list.
        ArrayList<RequestItem> items = new ArrayList<RequestItem>();
        // Declare original with type RequestItem. Read a seeded catalogue entry and create a request item with
        // its saved category, unit and price.
        RequestItem original = catalogueItem("Equipment",2);
        // Append original to items.
        items.add(original);
        // Declare request to hold a whole-number value. Validate and save a request, its fixed-price items and
        // optional attachment copies as one operation.
        int request = new RequestDAO().saveRequest(customer,"Price snapshot",items,"Equipment");
        // Declare refused to hold a true-or-false flag. Its initial value is false.
        boolean refused = false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Validate and save an Admin's catalogue changes, including the exact positive UGX price.
        try { new CatalogueDAO().save(0,"Forbidden","Equipment",original.getCategory().getCategoryId(),"Each","Test","1",true); }
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        catch (IllegalArgumentException ex) { refused = true; }
        // Verify this test expectation: Customer cannot change catalogue prices. A false result raises an
        // AssertionError.
        check(refused,"Customer cannot change catalogue prices");
        // Remove the entries from items while keeping the same collection object.
        items.clear();
        // Append `new RequestItem(original.getCatalogueId(), original.getCategory(),
        // original.getDescription(), original.getUnit(), 1, new java.math.BigDecimal("0.01"))` to items.
        items.add(new RequestItem(original.getCatalogueId(),original.getCategory(),original.getDescription(),
                original.getUnit(),1,new java.math.BigDecimal("0.01")));
        // Store false in refused for the remaining steps.
        refused = false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Validate and save a request, its fixed-price items and optional attachment copies as one operation.
        try { new RequestDAO().saveRequest(customer,"Spoofed price",items,"Equipment"); }
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        catch (IllegalArgumentException ex) { refused = true; }
        // Verify this test expectation: Spoofed customer price rejected. A false result raises an
        // AssertionError.
        check(refused,"Spoofed customer price rejected");
        // Use the fixture account and role as the current test session for the next checks.
        login(admin,"Admin");
        // Validate and save an Admin's catalogue changes, including the exact positive UGX price.
        new CatalogueDAO().save(original.getCatalogueId(),"Changed demo item","Equipment",original.getCategory().getCategoryId(),
                original.getUnit(),original.getDescription(),original.getUnitCost().add(java.math.BigDecimal.ONE).toPlainString(),true);
        // Use the fixture account and role as the current test session for the next checks.
        // Remove the entries from items while keeping the same collection object.
        // Append original to items.
        // Store false in refused for the remaining steps.
        login(customer,"Requester"); items.clear(); items.add(original); refused = false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Validate and save a request, its fixed-price items and optional attachment copies as one operation.
        try { new RequestDAO().saveRequest(customer,"Stale price",items,"Equipment"); }
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        catch (IllegalArgumentException ex) { refused = true; }
        // Verify this test expectation: Stale displayed price rejected. A false result raises an
        // AssertionError.
        check(refused,"Stale displayed price rejected");
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Declare saved with type java.math.BigDecimal. Read request_items records; WHERE limits the rows to
            // the stated conditions; question marks receive separately bound values.
            java.math.BigDecimal saved = (java.math.BigDecimal) Database.one(c,"SELECT estimated_cost FROM request_items WHERE request_id=?",request)[0];
            // Verify this test expectation: Historical selling price unchanged. A false result raises an
            // AssertionError.
            check(saved.compareTo(original.getUnitCost())==0,"Historical selling price unchanged");
            // Add missing columns and supporting tables, then seed missing catalogue entries while retaining
            // existing records.
            DatabaseSetup.ensureWorkflowTables(c);
            // Read catalogue records; WHERE limits the rows to the stated conditions; question marks receive
            // separately bound values.
            check("Changed demo item".equals(Database.one(c,"SELECT item_name FROM catalogue WHERE catalogue_id=?",original.getCatalogueId())[0]),"Startup preserves Admin edits");
        }
        // Use the fixture account and role as the current test session for the next checks.
        // Store false in refused for the remaining steps.
        login(manager,"Manager"); refused = false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Read account details for an Admin without selecting password hashes.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { new ManagementDAO().users(""); } catch (IllegalArgumentException ex) { refused = true; }
        // Verify this test expectation: Only Admin may list all users. A false result raises an
        // AssertionError.
        check(refused,"Only Admin may list all users");
        // Use the fixture account and role as the current test session for the next checks.
        login(customer,"Requester");
        // Declare order to hold one row of values in SELECT or table-column order. Read entry 0 from new
        // DecisionDAO().quotations(request, false); list positions start at zero.
        Object[] order = new DecisionDAO().quotations(request,false).get(0);
        // Verify this test expectation: Customer sees saved selling total. A false result raises an
        // AssertionError.
        check(((java.math.BigDecimal)order[2]).compareTo(original.getLineTotal())==0,"Customer sees saved selling total");
    }

    // Recovery changes only the password and expires sessions created before the reset.
    // Run the recovery checks against fixtures in the isolated test database; failures stop the test run.
    private static void testRecovery() throws Exception {
        // Declare oldPassword to hold text. Convert java.util.UUID.randomUUID() to its text representation.
        String oldPassword = java.util.UUID.randomUUID().toString();
        // Validate account details and create a Requester account; public registration cannot choose a staff
        // role.
        new RegistrationDAO().register("Recovery User", "recovery_user", "recovery@example.invalid", 0,
                oldPassword, oldPassword, "Individual", null, "Other");
        // Verify this test expectation: New registration can authenticate immediately. A false result raises
        // an AssertionError.
        check(new UserDAO().checkLogin("recovery_user", oldPassword), "New registration can authenticate immediately");
        // Declare id to hold a whole-number value. Its initial value is the signed-in account ID.
        int id = Session.getUserId();
        // Declare refused to hold a true-or-false flag. Its initial value is false.
        boolean refused = false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Match the registered username and full name, then save a random temporary password hash and expire
        // previous sessions.
        try { new PasswordResetDAO().reset("recovery_user", "Wrong Name"); }
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        catch (IllegalArgumentException ex) { refused = true; }
        // Verify this test expectation: Wrong recovery name rejected. A false result raises an AssertionError.
        check(refused, "Wrong recovery name rejected");
        // Declare replacement to hold text. Match the registered username and full name, then save a random
        // temporary password hash and expire previous sessions.
        String replacement = new PasswordResetDAO().reset("recovery_user", "Recovery User");
        // Store false in refused for the remaining steps.
        refused = false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Read only the signed-in account's notifications matching the selected read-status filter.
        try { new ManagementDAO().notifications("All notifications"); }
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        catch (IllegalArgumentException ex) { refused = true; }
        // Verify this test expectation: Reset expires an old session. A false result raises an AssertionError.
        check(refused, "Reset expires an old session");
        // Verify this test expectation: Old password no longer works. A false result raises an AssertionError.
        check(!new UserDAO().checkLogin("recovery_user", oldPassword), "Old password no longer works");
        // Verify this test expectation: Replacement password works. A false result raises an AssertionError.
        check(new UserDAO().checkLogin("recovery_user", replacement), "Replacement password works");
        // Verify this test expectation: Temporary login requires a password change. A false result raises an
        // AssertionError.
        check(Session.mustChangePassword(), "Temporary login requires a password change");
        // Store false in refused for the remaining steps.
        refused = false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Read only the signed-in account's notifications matching the selected read-status filter.
        try { new ManagementDAO().notifications("All notifications"); }
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        catch (IllegalArgumentException ex) { refused = true; }
        // Verify this test expectation: Temporary login cannot use protected features. A false result raises
        // an AssertionError.
        check(refused,"Temporary login cannot use protected features");
        // Clear all remembered login details so no previous account remains signed in.
        Session.clear();
        // Verify this test expectation: Required change survives another login. A false result raises an
        // AssertionError.
        check(new UserDAO().checkLogin("recovery_user", replacement) && Session.mustChangePassword(),"Required change survives another login");
        // Declare change with type PasswordChangeDAO. Create a PasswordChangeDAO object using the supplied
        // constructor values.
        PasswordChangeDAO change = new PasswordChangeDAO();
        // Store false in refused for the remaining steps.
        refused = false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Attempt the temporary-password replacement with these test inputs and exercise its validation rules.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { change.change(replacement,replacement); } catch (IllegalArgumentException ex) { refused = true; }
        // Verify this test expectation: Cannot keep generated password. A false result raises an
        // AssertionError.
        check(refused,"Cannot keep generated password");
        // Store false in refused for the remaining steps.
        refused = false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Attempt the temporary-password replacement with these test inputs and exercise its validation rules.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { change.change("short","short"); } catch (IllegalArgumentException ex) { refused = true; }
        // Verify this test expectation: Short personal password rejected. A false result raises an
        // AssertionError.
        check(refused,"Short personal password rejected");
        // Declare personal to hold text. Convert java.util.UUID.randomUUID() to its text representation.
        String personal = java.util.UUID.randomUUID().toString();
        // Store false in refused for the remaining steps.
        refused = false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Attempt the temporary-password replacement with these test inputs and exercise its validation rules.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { change.change(personal,"different"); } catch (IllegalArgumentException ex) { refused = true; }
        // Verify this test expectation: Confirmation must match. A false result raises an AssertionError.
        check(refused,"Confirmation must match");
        // Verify this test expectation: Invalid input does not dismiss required change. A false result raises
        // an AssertionError.
        check(Session.mustChangePassword(),"Invalid input does not dismiss required change");
        // Attempt the temporary-password replacement with these test inputs and exercise its validation rules.
        change.change(personal,personal);
        // Verify this test expectation: Saved personal password completes login. A false result raises an
        // AssertionError.
        check(!Session.mustChangePassword(),"Saved personal password completes login");
        // Read only the signed-in account's notifications matching the selected read-status filter.
        new ManagementDAO().notifications("All notifications");
        // Verify this test expectation: Temporary password stops working after change. A false result raises
        // an AssertionError.
        check(!new UserDAO().checkLogin("recovery_user",replacement),"Temporary password stops working after change");
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        // Add missing columns and supporting tables, then seed missing catalogue entries while retaining
        // existing records.
        try (Connection c = DBConnection.getConnection()) { DatabaseSetup.ensureWorkflowTables(c); }
        // Verify this test expectation: Personal password logs in normally after startup migration. A false
        // result raises an AssertionError.
        check(new UserDAO().checkLogin("recovery_user",personal) && !Session.mustChangePassword(),"Personal password logs in normally after startup migration");
        // Clear all remembered login details so no previous account remains signed in.
        // Store false in refused for the remaining steps.
        Session.clear(); refused = false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Attempt the temporary-password replacement with these test inputs and exercise its validation rules.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { change.change(personal,personal); } catch (IllegalArgumentException ex) { refused = true; }
        // Verify this test expectation: Cancelled or logged-out session cannot change a password. A false
        // result raises an AssertionError.
        check(refused,"Cancelled or logged-out session cannot change a password");

        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Declare row to hold one row of values in SELECT or table-column order. Read users records; WHERE
            // limits the rows to the stated conditions; question marks receive separately bound values.
            Object[] row = Database.one(c, "SELECT gender,role,password_hash FROM users WHERE user_id=?", id);
            // Verify this test expectation: Recovery preserves gender and role. A false result raises an
            // AssertionError.
            check("Other".equals(row[0]) && "Requester".equals(row[1]), "Recovery preserves gender and role");
            // Verify this test expectation: Only the password hash is stored. A false result raises an
            // AssertionError.
            check(!replacement.equals(row[2]), "Only the password hash is stored");
        }
    }

    // New requests must use an actual active catalogue item, including its saved description.
    // Read a seeded catalogue entry and create a request item with its saved category, unit and price.
    private static RequestItem catalogueItem(String type, int quantity) throws Exception {
        // Declare row to hold one row of values in SELECT or table-column order. Read entry 0 from new
        // CatalogueDAO().list("", type); list positions start at zero.
        Object[] row = new CatalogueDAO().list("",type).get(0);
        // Create a RequestItem object using the supplied constructor values. Return the resulting value to the
        // caller.
        return new RequestItem(Database.id(row[0]),new Category(Database.id(row[8]),row[3].toString()),
                row[7].toString(),row[4].toString(),quantity,(java.math.BigDecimal)row[5]);
    }
    // Build a real request and supplier quote using the application DAOs.
    // Create a fixture request and supplier quotation for later workflow checks.
    private static int[] quotedRequest(String type, int quantity) throws Exception {
        // Declare category to hold a whole-number value.
        // Declare vendor to hold a whole-number value.
        int category, vendor;
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Insert a record into categories.
            category = Database.insert(c,"INSERT INTO categories(category_name) VALUES ('Test category')");
            // Read vendors records.
            vendor = Database.id(Database.one(c,"SELECT vendor_id FROM vendors LIMIT 1")[0]);
        }
        // Use the fixture account and role as the current test session for the next checks.
        login(customer,"Requester");
        // Declare items with type ArrayList<RequestItem>. Create an initially empty resizable list.
        ArrayList<RequestItem> items = new ArrayList<RequestItem>();
        // Append `catalogueItem(type, quantity)` to items.
        items.add(catalogueItem(type, quantity));
        // Declare request to hold a whole-number value. Validate and save a request, its fixed-price items and
        // optional attachment copies as one operation.
        int request = new RequestDAO().saveRequest(customer,"Test request",items,type);
        // Use the fixture account and role as the current test session for the next checks.
        login(purchaser,"Purchaser");
        // Declare quotes with type QuotationDAO. Create a QuotationDAO object using the supplied constructor
        // values.
        QuotationDAO quotes = new QuotationDAO();
        // Declare prices with type ArrayList<QuotationItem>. Read the selected request's descriptions and
        // quantities into quotation items with prices initially unset.
        ArrayList<QuotationItem> prices = quotes.getRequestItems(request);
        // Process each entry in prices in turn, referring to the current entry as item.
        // Validate and store the supplier unit price in the selected quotation item.
        for (QuotationItem item : prices) { item.setUnitPrice(new java.math.BigDecimal("12.50")); }
        // Declare quote to hold a whole-number value. Save an internal supplier quotation and all its item
        // prices together after validating the request.
        int quote = quotes.saveQuotation(request,vendor,"Test quotation",prices);
        // Create an array of int values containing the listed entries in order. Return the resulting value to
        // the caller.
        return new int[] {request,quote,prices.get(0).getRequestItemId()};
    }
    // Run the decisions checks against fixtures in the isolated test database; failures stop the test run.
    private static void testDecisions() throws Exception {
        // Declare ids with type int[]. Create a fixture request and supplier quotation for later workflow
        // checks.
        int[] ids = quotedRequest("Equipment",2);
        // Declare decisions with type DecisionDAO. Create a DecisionDAO object using the supplied constructor
        // values.
        DecisionDAO decisions = new DecisionDAO();
        // Use the fixture account and role as the current test session for the next checks.
        login(other,"Requester");
        // Declare refused to hold a true-or-false flag. Its initial value is false.
        boolean refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Save the owner's acceptance or rejection of a fixed-price catalogue order once only.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { decisions.customerDecision(ids[0],ids[1],true,""); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Another account cannot accept a customer's quotation. A false result
        // raises an AssertionError.
        check(refused,"Another account cannot accept a customer's quotation");
        // Use the fixture account and role as the current test session for the next checks.
        login(customer,"Requester");
        // Save the owner's acceptance or rejection of a fixed-price catalogue order once only.
        decisions.customerDecision(ids[0],ids[1],true,"Please proceed");
        // Store false in refused for the remaining steps.
        refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Save the owner's acceptance or rejection of a fixed-price catalogue order once only.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { decisions.customerDecision(ids[0],ids[1],true,""); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Repeated customer decision refused. A false result raises an
        // AssertionError.
        check(refused,"Repeated customer decision refused");
        // Use the fixture account and role as the current test session for the next checks.
        login(manager,"Manager");
        // Store false in refused for the remaining steps.
        refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Save a manager's decision and internal supplier selection without changing the customer's saved
        // selling price.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { decisions.review(ids[0],ids[1]+999,true,""); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Cannot approve a different quotation. A false result raises an
        // AssertionError.
        check(refused,"Cannot approve a different quotation");
        // Save a manager's decision and internal supplier selection without changing the customer's saved
        // selling price.
        decisions.review(ids[0],ids[1],true,"Reviewed");
        // Verify this test expectation: One approval history entry. A false result raises an AssertionError.
        check(decisions.history(ids[0]).size()==1,"One approval history entry");
        // Store false in refused for the remaining steps.
        refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Save a manager's decision and internal supplier selection without changing the customer's saved
        // selling price.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { decisions.review(ids[0],ids[1],true,""); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Repeated staff approval refused. A false result raises an
        // AssertionError.
        check(refused,"Repeated staff approval refused");
    }

    // Create an accepted and approved fixture request for delivery or service checks.
    private static int[] approvedRequest(String type, int quantity) throws Exception {
        // Declare ids with type int[]. Create a fixture request and supplier quotation for later workflow
        // checks.
        int[] ids = quotedRequest(type,quantity);
        // Use the fixture account and role as the current test session for the next checks.
        // Save the owner's acceptance or rejection of a fixed-price catalogue order once only.
        login(customer,"Requester"); new DecisionDAO().customerDecision(ids[0],ids[1],true,"");
        // Use the fixture account and role as the current test session for the next checks.
        // Save a manager's decision and internal supplier selection without changing the customer's saved
        // selling price.
        login(manager,"Manager"); new DecisionDAO().review(ids[0],ids[1],true,"");
        // Use the fixture account and role as the current test session for the next checks.
        // Return ids to the caller.
        login(purchaser,"Purchaser"); return ids;
    }
    // Run the fulfilment checks against fixtures in the isolated test database; failures stop the test
    // run.
    private static void testFulfilment() throws Exception {
        // Declare dao with type FulfilmentDAO. Create a FulfilmentDAO object using the supplied constructor
        // values.
        FulfilmentDAO dao = new FulfilmentDAO();
        // Declare today to hold text. Convert java.time.LocalDate.now() to its text representation.
        String today = java.time.LocalDate.now().toString();
        // Declare equipment with type int[]. Create an accepted and approved fixture request for delivery or
        // service checks.
        int[] equipment = approvedRequest("Equipment",2);
        // Declare units with type ArrayList<DeliveryUnit>. Create an initially empty resizable list.
        ArrayList<DeliveryUnit> units = new ArrayList<DeliveryUnit>();
        // Append `new DeliveryUnit(equipment[2], "TEST-SERIAL-1")` to units.
        units.add(new DeliveryUnit(equipment[2],"TEST-SERIAL-1"));
        // Save each received serial-numbered unit and its inventory row together, preventing excess
        // deliveries.
        dao.saveDelivery(equipment[0],today,units);
        // Verify this test expectation: Partial delivery leaves one unit. A false result raises an
        // AssertionError.
        check(((Number)dao.equipmentItems(equipment[0]).get(0)[4]).intValue()==1,"Partial delivery leaves one unit");
        // Declare refused to hold a true-or-false flag. Its initial value is false.
        boolean refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Save each received serial-numbered unit and its inventory row together, preventing excess
        // deliveries.
        // Handle SQLException ex from the preceding try block so the failure follows the recovery steps below.
        // Store true in refused for the remaining steps.
        try { dao.saveDelivery(equipment[0],today,units); } catch (SQLException ex) { refused=true; }
        // Verify this test expectation: Duplicate serial rejected by database. A false result raises an
        // AssertionError.
        check(refused,"Duplicate serial rejected by database");
        // Verify this test expectation: Duplicate failure rolls back inventory. A false result raises an
        // AssertionError.
        check(dao.inventory("TEST-SERIAL").size()==1,"Duplicate failure rolls back inventory");
        // Remove the entries from units while keeping the same collection object.
        // Append `new DeliveryUnit(equipment[2], "TEST-SERIAL-2")` to units.
        // Append `new DeliveryUnit(equipment[2], "TEST-SERIAL-3")` to units.
        units.clear(); units.add(new DeliveryUnit(equipment[2],"TEST-SERIAL-2")); units.add(new DeliveryUnit(equipment[2],"TEST-SERIAL-3"));
        // Store false in refused for the remaining steps.
        refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Save each received serial-numbered unit and its inventory row together, preventing excess
        // deliveries.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { dao.saveDelivery(equipment[0],today,units); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Overdelivery rejected. A false result raises an AssertionError.
        check(refused,"Overdelivery rejected");
        // Verify this test expectation: Overdelivery rolls back all staged units. A false result raises an
        // AssertionError.
        check(dao.inventory("TEST-SERIAL").size()==1,"Overdelivery rolls back all staged units");
        // Remove entry 1 from units.
        // Save each received serial-numbered unit and its inventory row together, preventing excess
        // deliveries.
        units.remove(1); dao.saveDelivery(equipment[0],today,units);
        // Verify this test expectation: Final delivery creates exact inventory count. A false result raises an
        // AssertionError.
        check(dao.inventory("TEST-SERIAL").size()==2,"Final delivery creates exact inventory count");
        // Declare inventory to hold a whole-number value. Convert a JDBC numeric value to an int, even when
        // the driver returned another Number type.
        int inventory = Database.id(dao.inventory("TEST-SERIAL-1").get(0)[0]);
        // Update or clear an inventory assignment and save its audit record and optional user notification
        // together.
        dao.assign(inventory,customer);
        // Verify this test expectation: Inventory assignment. A false result raises an AssertionError.
        check("test_customer".equals(dao.inventory("TEST-SERIAL-1").get(0)[4]),"Inventory assignment");
        // Update or clear an inventory assignment and save its audit record and optional user notification
        // together.
        dao.assign(inventory,null);
        // Verify this test expectation: Inventory unassignment. A false result raises an AssertionError.
        check(dao.inventory("TEST-SERIAL-1").get(0)[4]==null,"Inventory unassignment");
        // Declare service with type int[]. Create an accepted and approved fixture request for delivery or
        // service checks.
        int[] service = approvedRequest("Service",1);
        // Store false in refused for the remaining steps.
        refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Save each received serial-numbered unit and its inventory row together, preventing excess
        // deliveries.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { dao.saveDelivery(service[0],today,units); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Service cannot create equipment delivery. A false result raises an
        // AssertionError.
        check(refused,"Service cannot create equipment delivery");
        // Store false in refused for the remaining steps.
        refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Validate and save service status, notes and completion date in one transaction.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { dao.saveProgress(service[0],"Completed","Done",""); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Completion date required. A false result raises an AssertionError.
        check(refused,"Completion date required");
        // Validate and save service status, notes and completion date in one transaction.
        dao.saveProgress(service[0],"In Progress","Working","");
        // Verify this test expectation: Service progress persists. A false result raises an AssertionError.
        check("In Progress".equals(dao.progress(service[0])[0]),"Service progress persists");
        // Validate and save service status, notes and completion date in one transaction.
        dao.saveProgress(service[0],"Completed","Done",today);
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Read requests records; WHERE limits the rows to the stated conditions; question marks receive
            // separately bound values.
            check("Completed".equals(Database.one(c,"SELECT request_status FROM requests WHERE request_id=?",service[0])[0]),"Service completes request");
            // Read requests records; WHERE limits the rows to the stated conditions; question marks receive
            // separately bound values.
            check("Delivered".equals(Database.one(c,"SELECT request_status FROM requests WHERE request_id=?",equipment[0])[0]),"Equipment completes request");
        }
        // Verify this test expectation: Service creates no inventory rows. A false result raises an
        // AssertionError.
        check(dao.inventory("TEST-SERIAL").size()==2,"Service creates no inventory rows");
        // Use the fixture account and role as the current test session for the next checks.
        // Store false in refused for the remaining steps.
        login(customer,"Requester"); refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Update or clear an inventory assignment and save its audit record and optional user notification
        // together.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { dao.assign(inventory,customer); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Customer cannot assign inventory. A false result raises an
        // AssertionError.
        check(refused,"Customer cannot assign inventory");
    }

    // Run the attachments checks against fixtures in the isolated test database; failures stop the test
    // run.
    private static void testAttachments() throws Exception {
        // Declare root with type java.nio.file.Path. Its initial value is
        // `java.nio.file.Files.createTempDirectory("procurement-attachments-test-")`.
        java.nio.file.Path root = java.nio.file.Files.createTempDirectory("procurement-attachments-test-");
        // Declare source with type java.nio.file.Path. Its initial value is `root.resolve("source.txt")`.
        java.nio.file.Path source = root.resolve("source.txt");
        // Write the supplied bytes to the output stream using the stated offset and byte count.
        java.nio.file.Files.write(source,"A test supporting document".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        // Declare store with type java.nio.file.Path. Its initial value is `root.resolve("stored")`.
        java.nio.file.Path store = root.resolve("stored");
        // Override this setting for the running test process so fixture files use isolated storage.
        System.setProperty("procurement.attachments.dir",store.toString());
        // Declare category to hold a whole-number value.
        int category;
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        // Read categories records.
        try (Connection c = DBConnection.getConnection()) { category = Database.id(Database.one(c,"SELECT category_id FROM categories LIMIT 1")[0]); }
        // Declare items with type ArrayList<RequestItem>. Create an initially empty resizable list.
        ArrayList<RequestItem> items = new ArrayList<RequestItem>();
        // Append `catalogueItem("Equipment", 1)` to items.
        items.add(catalogueItem("Equipment", 1));
        // Declare files with type ArrayList<java.io.File>. Create an initially empty resizable list.
        // Append `source.toFile()` to files.
        ArrayList<java.io.File> files = new ArrayList<java.io.File>(); files.add(source.toFile());
        // Use the fixture account and role as the current test session for the next checks.
        login(customer,"Requester");
        // Declare request to hold a whole-number value. Validate and save a request, its fixed-price items and
        // optional attachment copies as one operation.
        int request = new RequestDAO().saveRequest(customer,"Attachment test",items,"Equipment",files);
        // Declare saved with type ArrayList<Object[]>. List attachment names and paths only after checking the
        // request owner or authorised staff role.
        ArrayList<Object[]> saved = new AttachmentDAO().forRequest(request);
        // Verify this test expectation: Attachment metadata saved. A false result raises an AssertionError.
        check(saved.size()==1,"Attachment metadata saved");
        // Declare copy with type java.nio.file.Path. Read entry saved.get(0)[1].toString() from
        // java.nio.file.Paths; list positions start at zero.
        java.nio.file.Path copy = java.nio.file.Paths.get(saved.get(0)[1].toString());
        // Verify this test expectation: Document copied to managed storage. A false result raises an
        // AssertionError.
        check(java.nio.file.Files.exists(copy) && !copy.equals(source),"Document copied to managed storage");
        // Use the fixture account and role as the current test session for the next checks.
        // Declare refused to hold a true-or-false flag. Its initial value is false.
        login(other,"Requester"); boolean refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // List attachment names and paths only after checking the request owner or authorised staff role.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { new AttachmentDAO().forRequest(request); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Another customer cannot view attachments. A false result raises an
        // AssertionError.
        check(refused,"Another customer cannot view attachments");
        // Use the fixture account and role as the current test session for the next checks.
        // Store false in refused for the remaining steps.
        login(customer,"Requester"); refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Validate and save a request, its fixed-price items and optional attachment copies as one operation.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { new RequestDAO().saveRequest(other,"",items,"Equipment"); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Cannot submit as another user. A false result raises an
        // AssertionError.
        check(refused,"Cannot submit as another user");
        // Declare before to hold a whole-number value.
        int before;
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Read requests records and calculate a record count.
            before = Database.id(Database.one(c,"SELECT COUNT(*) FROM requests")[0]);
            // Force a late failure after copying a file to exercise cleanup, not just validation.
            // Run a prepared database change and return the number of affected rows; the caller controls the
            // transaction.
            Database.update(c,"CREATE TRIGGER fail_attachment BEFORE INSERT ON attachments FOR EACH ROW SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Test rollback'");
        }
        // Store false in refused for the remaining steps.
        refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Validate and save a request, its fixed-price items and optional attachment copies as one operation.
        // Handle SQLException ex from the preceding try block so the failure follows the recovery steps below.
        // Store true in refused for the remaining steps.
        try { new RequestDAO().saveRequest(customer,"",items,"Equipment",files); } catch (SQLException ex) { refused=true; }
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        // Run a prepared database change and return the number of affected rows; the caller controls the
        // transaction.
        finally { try (Connection c = DBConnection.getConnection()) { Database.update(c,"DROP TRIGGER fail_attachment"); } }
        // Verify this test expectation: Late attachment failure reported. A false result raises an
        // AssertionError.
        check(refused,"Late attachment failure reported");
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        // Read requests records and calculate a record count.
        try (Connection c = DBConnection.getConnection()) { check(before==Database.id(Database.one(c,"SELECT COUNT(*) FROM requests")[0]),"Failed attachment rolls back request"); }
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare stored with type java.util.stream.Stream<java.nio.file.Path>. Its initial value is
        // `java.nio.file.Files.list(store)`.
        // Verify this test expectation: Failed attachment copy cleaned up. A false result raises an
        // AssertionError.
        try (java.util.stream.Stream<java.nio.file.Path> stored = java.nio.file.Files.list(store)) { check(stored.count()==1,"Failed attachment copy cleaned up"); }
        // Delete the specified temporary or copied file during cleanup; deleteIfExists also accepts an already
        // absent file.
        java.nio.file.Files.delete(copy); java.nio.file.Files.delete(source); java.nio.file.Files.delete(store); java.nio.file.Files.delete(root);
    }
    // Run the registration checks against fixtures in the isolated test database; failures stop the test
    // run.
    private static void testRegistration() throws Exception {
        // Declare dao with type RegistrationDAO. Create a RegistrationDAO object using the supplied
        // constructor values.
        RegistrationDAO dao = new RegistrationDAO();
        // Generate a throwaway test password at runtime rather than publishing credentials.
        // Declare password to hold text. Convert java.util.UUID.randomUUID() to its text representation.
        String password = java.util.UUID.randomUUID().toString();
        // Validate account details and create a Requester account; public registration cannot choose a staff
        // role.
        dao.register("New Customer","new_customer","new@example.invalid",0,password,password,"Individual",null);
        // Declare refused to hold a true-or-false flag. Its initial value is false.
        boolean refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Validate account details and create a Requester account; public registration cannot choose a staff
        // role.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { dao.register("Duplicate","new_customer","different@example.invalid",0,password,password,"Individual",null); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Duplicate username refused. A false result raises an AssertionError.
        check(refused,"Duplicate username refused");
        // Store false in refused for the remaining steps.
        refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Validate account details and create a Requester account; public registration cannot choose a staff
        // role.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { dao.register("Duplicate","different_user","NEW@example.invalid",0,password,password,"Individual",null); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Duplicate email refused. A false result raises an AssertionError.
        check(refused,"Duplicate email refused");
        // Store false in refused for the remaining steps.
        refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Validate account details and create a Requester account; public registration cannot choose a staff
        // role.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { dao.register("No Type","no_type","no_type@example.invalid",0,password,password,"",null); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Account type required. A false result raises an AssertionError.
        check(refused,"Account type required");
        // Verify this test expectation: New account can log in. A false result raises an AssertionError.
        check(new UserDAO().checkLogin("new_customer",password),"New account can log in");
        // Verify this test expectation: Public registration cannot create staff. A false result raises an
        // AssertionError.
        check("Requester".equals(Session.getRole()),"Public registration cannot create staff");
    }
    // Real JPanel constructors can run without a desktop; JFrame windows still need a display.
    // Run the panels checks against fixtures in the isolated test database; failures stop the test run.
    private static void testPanels() throws Exception {
        // Allow this test to inspect a private designer field through reflection without changing its
        // visibility in the application.
        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
            // Execute the work supplied to this Runnable when its caller or event queue schedules it.
            public void run() {
                // Use the fixture account and role as the current test session for the next checks.
                login(customer,"Requester");
                // Create a RequestPanel object using the supplied constructor values.
                // Create a MyRequestsPanel object using the supplied constructor values.
                // Create a CustomerQuotationsPanel object using the supplied constructor values.
                // Create a NotificationsPanel object using the supplied constructor values.
                new RequestPanel(); new MyRequestsPanel(); new CustomerQuotationsPanel(); new NotificationsPanel();
                // Use the fixture account and role as the current test session for the next checks.
                login(manager,"Manager");
                // Create a ApprovalPanel object using the supplied constructor values.
                new ApprovalPanel();
                // Use the fixture account and role as the current test session for the next checks.
                // Create a DepartmentPanel object using the supplied constructor values.
                // Create a UserManagementPanel object using the supplied constructor values.
                login(admin,"Admin"); new DepartmentPanel(); new UserManagementPanel();
                // Declare catalogue with type CataloguePanel. Create a CataloguePanel object using the supplied
                // constructor values.
                CataloguePanel catalogue = new CataloguePanel();
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                try {
                    // Declare type with type java.lang.reflect.Field. Its initial value is
                    // `CataloguePanel.class.getDeclaredField("jComboBoxType")`.
                    // Allow this test to inspect a private designer field through reflection without changing its
                    // visibility in the application.
                    java.lang.reflect.Field type = CataloguePanel.class.getDeclaredField("jComboBoxType"); type.setAccessible(true);
                    // Verify this test expectation: Catalogue has both type choices. A false result raises an
                    // AssertionError.
                    check(((javax.swing.JComboBox<?>)type.get(catalogue)).getItemCount()==3,"Catalogue has both type choices");
                    // Use the fixture account and role as the current test session for the next checks.
                    // Set catalogue from the following operation: create a CataloguePanel object using the supplied
                    // constructor values.
                    login(customer,"Requester"); catalogue = new CataloguePanel();
                    // Declare editor with type java.lang.reflect.Field. Its initial value is
                    // `CataloguePanel.class.getDeclaredField("jPanelEditor")`.
                    // Allow this test to inspect a private designer field through reflection without changing its
                    // visibility in the application.
                    java.lang.reflect.Field editor = CataloguePanel.class.getDeclaredField("jPanelEditor"); editor.setAccessible(true);
                    // Verify this test expectation: Customer catalogue editor hidden. A false result raises an
                    // AssertionError.
                    check(!((javax.swing.JPanel)editor.get(catalogue)).isVisible(),"Customer catalogue editor hidden");
                // Handle ReflectiveOperationException ex from the preceding try block so the failure follows the
                // recovery steps below.
                // Pass the caught exception back to the caller so the original failure is not treated as success.
                } catch (ReflectiveOperationException ex) { throw new AssertionError(ex); }
                // Use the fixture account and role as the current test session for the next checks.
                login(purchaser,"Purchaser");
                // Create a QuotationPanel object using the supplied constructor values.
                // Create a DeliveryPanel object using the supplied constructor values.
                // Create a ServiceCompletionPanel object using the supplied constructor values.
                // Create a InventoryPanel object using the supplied constructor values.
                // Create a VendorPanel object using the supplied constructor values.
                new QuotationPanel(); new DeliveryPanel(); new ServiceCompletionPanel(); new InventoryPanel(); new VendorPanel();
                // Verify this test expectation: All 13 actual JPanel constructors load successfully. A false result
                // raises an AssertionError.
                check(true,"All 13 actual JPanel constructors load successfully");
            }
        });
    }

    // Run the decline and rejection checks against fixtures in the isolated test database; failures stop
    // the test run.
    private static void testDeclineAndRejection() throws Exception {
        // Declare ids with type int[]. Create a fixture request and supplier quotation for later workflow
        // checks.
        int[] ids = quotedRequest("Service",1);
        // Declare decisions with type DecisionDAO. Create a DecisionDAO object using the supplied constructor
        // values.
        DecisionDAO decisions = new DecisionDAO();
        // Use the fixture account and role as the current test session for the next checks.
        // Save the owner's acceptance or rejection of a fixed-price catalogue order once only.
        login(customer,"Requester"); decisions.customerDecision(ids[0],ids[1],false,"Too expensive");
        // Verify this test expectation: Customer decline persists. A false result raises an AssertionError.
        check("Declined".equals(decisions.quotations(ids[0],false).get(0)[3]),"Customer decline persists");
        // Use the fixture account and role as the current test session for the next checks.
        // Declare refused to hold a true-or-false flag. Its initial value is false.
        login(manager,"Manager"); boolean refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Save a manager's decision and internal supplier selection without changing the customer's saved
        // selling price.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { decisions.review(ids[0],ids[1],true,""); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Declined quote cannot be approved. A false result raises an
        // AssertionError.
        check(refused,"Declined quote cannot be approved");
        // Declare rejected with type int[]. Create a fixture request and supplier quotation for later workflow
        // checks.
        int[] rejected = quotedRequest("Equipment",1);
        // Use the fixture account and role as the current test session for the next checks.
        // Save the owner's acceptance or rejection of a fixed-price catalogue order once only.
        login(customer,"Requester"); decisions.customerDecision(rejected[0],rejected[1],true,"");
        // Use the fixture account and role as the current test session for the next checks.
        // Store false in refused for the remaining steps.
        login(manager,"Manager"); refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Save a manager's decision and internal supplier selection without changing the customer's saved
        // selling price.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { decisions.review(rejected[0],rejected[1],false,""); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Rejection needs a reason. A false result raises an AssertionError.
        check(refused,"Rejection needs a reason");
        // Save a manager's decision and internal supplier selection without changing the customer's saved
        // selling price.
        decisions.review(rejected[0],rejected[1],false,"Unable to fulfil");
        // Verify this test expectation: Rejection history persists. A false result raises an AssertionError.
        check("Rejected".equals(decisions.history(rejected[0]).get(0)[2]),"Rejection history persists");
        // Use the fixture account and role as the current test session for the next checks.
        // Store false in refused for the remaining steps.
        login(purchaser,"Purchaser"); refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Read ordered, received and outstanding quantities for the approved equipment request.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { new FulfilmentDAO().equipmentItems(rejected[0]); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Rejected request cannot be delivered. A false result raises an
        // AssertionError.
        check(refused,"Rejected request cannot be delivered");
        // Use the fixture account and role as the current test session for the next checks.
        // Change another account's role after locking Admin records to prevent conflicting role changes.
        login(admin,"Admin"); new ManagementDAO().changeRole(other,"Requester");
        // Use the fixture account and role as the current test session for the next checks.
        // Store false in refused for the remaining steps.
        login(other,"Purchaser"); refused=false;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // List requests still eligible for supplier quotations, after checking Purchaser access in the
        // database.
        // Handle IllegalArgumentException ex from the preceding try block so the failure follows the recovery
        // steps below.
        // Store true in refused for the remaining steps.
        try { new QuotationDAO().getOpenRequestIds(); } catch (IllegalArgumentException ex) { refused=true; }
        // Verify this test expectation: Stale purchaser session cannot read all requests. A false result
        // raises an AssertionError.
        check(refused,"Stale purchaser session cannot read all requests");
    }
    // Two simultaneous connections must still produce only one selected quotation.
    // Run the concurrent selection checks against fixtures in the isolated test database; failures stop
    // the test run.
    private static void testConcurrentSelection() throws Exception {
        // Declare first with type int[]. Create a fixture request and supplier quotation for later workflow
        // checks. The reference or value cannot be reassigned after initialisation.
        final int[] first = quotedRequest("Equipment",1);
        // Declare vendor to hold a whole-number value.
        int vendor;
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        // Read vendors records.
        try (Connection c = DBConnection.getConnection()) { vendor = Database.id(Database.one(c,"SELECT vendor_id FROM vendors LIMIT 1")[0]); }
        // Declare quotes with type QuotationDAO. Create a QuotationDAO object using the supplied constructor
        // values.
        QuotationDAO quotes = new QuotationDAO();
        // Declare prices with type ArrayList<QuotationItem>. Read the selected request's descriptions and
        // quantities into quotation items with prices initially unset.
        ArrayList<QuotationItem> prices = quotes.getRequestItems(first[0]);
        // Process each entry in prices in turn, referring to the current entry as item.
        // Validate and store the supplier unit price in the selected quotation item.
        for (QuotationItem item : prices) { item.setUnitPrice(new java.math.BigDecimal("11.00")); }
        // Declare second to hold a whole-number value. Save an internal supplier quotation and all its item
        // prices together after validating the request. The reference or value cannot be reassigned after
        // initialisation.
        final int second = quotes.saveQuotation(first[0],vendor,"Alternative",prices);
        // Use the fixture account and role as the current test session for the next checks.
        login(customer,"Requester");
        // Declare start with type java.util.concurrent.CountDownLatch. Create a CountDownLatch object using
        // the supplied constructor values. The reference or value cannot be reassigned after initialisation.
        final java.util.concurrent.CountDownLatch start = new java.util.concurrent.CountDownLatch(1);
        // Declare saved with type java.util.concurrent.atomic.AtomicInteger. Create a AtomicInteger object
        // using the supplied constructor values. The reference or value cannot be reassigned after
        // initialisation.
        final java.util.concurrent.atomic.AtomicInteger saved = new java.util.concurrent.atomic.AtomicInteger();
        // Declare unexpected with type java.util.ArrayList<Throwable>. Create an initially empty resizable
        // list. The reference or value cannot be reassigned after initialisation.
        final java.util.ArrayList<Throwable> unexpected = new java.util.ArrayList<Throwable>();
        // Declare threads with type Thread[]. Create an array of Thread values with 2 positions, indexed from
        // zero.
        Thread[] threads = new Thread[2];
        // Repeat while i is less than 2; initialise the counter once and update it after each pass.
        for (int i=0;i<2;i++) {
            // Declare quote to hold a whole-number value. Its initial value is `i == 0 ? first[1] : second`. The
            // reference or value cannot be reassigned after initialisation.
            final int quote = i==0 ? first[1] : second;
            // Store a worker thread at index i; its run method performs one competing workflow action.
            threads[i] = new Thread(new Runnable() {
                // Execute the work supplied to this Runnable when its caller or event queue schedules it.
                public void run() {
                    // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                    // Wait at the shared latch so the competing test threads begin their database actions together.
                    // Save the owner's acceptance or rejection of a fixed-price catalogue order once only.
                    // Atomically count one successful operation so concurrent threads cannot lose an increment.
                    try { start.await(); new DecisionDAO().customerDecision(first[0],quote,true,""); saved.incrementAndGet(); }
                    // Handle IllegalArgumentException expected from the preceding try block so the failure follows the
                    // recovery steps below.
                    catch (IllegalArgumentException expected) { /* The second decision must be refused. */ }
                    // Handle Throwable ex from the preceding try block so the failure follows the recovery steps below.
                    // Append ex to unexpected.
                    catch (Throwable ex) { synchronized(unexpected) { unexpected.add(ex); } }
                }
            });
            // Start this worker or external process so its operation can run.
            threads[i].start();
        }
        // Release the waiting test threads by reducing the shared latch count.
        start.countDown();
        // Process each entry in threads in turn, referring to the current entry as thread.
        // Wait for this test thread to finish before checking the combined outcome.
        for (Thread thread : threads) { thread.join(); }
        // Verify this test expectation: Concurrent decisions produce one winner. A false result raises an
        // AssertionError.
        check(unexpected.isEmpty() && saved.get()==1,"Concurrent decisions produce one winner");
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Read order_decisions records and calculate a record count; WHERE limits the rows to the stated
            // conditions; question marks receive separately bound values.
            check(Database.id(Database.one(c,"SELECT COUNT(*) FROM order_decisions WHERE request_id=?",first[0])[0])==1,"One selected quotation stored");
        }
    }

    // Run the concurrent deliveries checks against fixtures in the isolated test database; failures stop
    // the test run.
    private static void testConcurrentDeliveries() throws Exception {
        // Declare ids with type int[]. Create an accepted and approved fixture request for delivery or service
        // checks. The reference or value cannot be reassigned after initialisation.
        final int[] ids = approvedRequest("Equipment",3);
        // Declare start with type java.util.concurrent.CountDownLatch. Create a CountDownLatch object using
        // the supplied constructor values. The reference or value cannot be reassigned after initialisation.
        final java.util.concurrent.CountDownLatch start = new java.util.concurrent.CountDownLatch(1);
        // Declare saved with type java.util.concurrent.atomic.AtomicInteger. Create a AtomicInteger object
        // using the supplied constructor values. The reference or value cannot be reassigned after
        // initialisation.
        final java.util.concurrent.atomic.AtomicInteger saved = new java.util.concurrent.atomic.AtomicInteger();
        // Declare unexpected with type java.util.ArrayList<Throwable>. Create an initially empty resizable
        // list. The reference or value cannot be reassigned after initialisation.
        final java.util.ArrayList<Throwable> unexpected = new java.util.ArrayList<Throwable>();
        // Declare threads with type Thread[]. Create an array of Thread values with 2 positions, indexed from
        // zero.
        Thread[] threads = new Thread[2];
        // Repeat while i is less than 2; initialise the counter once and update it after each pass.
        for (int i=0;i<2;i++) {
            // Declare batch to hold a whole-number value. Its initial value is i. The reference or value cannot be
            // reassigned after initialisation.
            final int batch = i;
            // Store a worker thread at index i; its run method performs one competing workflow action.
            threads[i] = new Thread(new Runnable() {
                // Execute the work supplied to this Runnable when its caller or event queue schedules it.
                public void run() {
                    // Declare units with type ArrayList<DeliveryUnit>. Create an initially empty resizable list.
                    ArrayList<DeliveryUnit> units = new ArrayList<DeliveryUnit>();
                    // Append `new DeliveryUnit(ids[2], "RACE-" + batch + "-A")` to units.
                    units.add(new DeliveryUnit(ids[2],"RACE-"+batch+"-A"));
                    // Append `new DeliveryUnit(ids[2], "RACE-" + batch + "-B")` to units.
                    units.add(new DeliveryUnit(ids[2],"RACE-"+batch+"-B"));
                    // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                    try {
                        // Wait at the shared latch so the competing test threads begin their database actions together.
                        start.await();
                        // Save each received serial-numbered unit and its inventory row together, preventing excess
                        // deliveries.
                        new FulfilmentDAO().saveDelivery(ids[0],java.time.LocalDate.now().toString(),units);
                        // Atomically count one successful operation so concurrent threads cannot lose an increment.
                        saved.incrementAndGet();
                    // Handle IllegalArgumentException expected from the preceding try block so the failure follows the
                    // recovery steps below.
                    } catch (IllegalArgumentException expected) { /* Only one batch of two fits an order for three. */ }
                    // Handle Throwable ex from the preceding try block so the failure follows the recovery steps below.
                    // Append ex to unexpected.
                    catch (Throwable ex) { synchronized(unexpected) { unexpected.add(ex); } }
                }
            });
            // Start this worker or external process so its operation can run.
            threads[i].start();
        }
        // Release the waiting test threads by reducing the shared latch count.
        start.countDown();
        // Process each entry in threads in turn, referring to the current entry as thread.
        // Wait for this test thread to finish before checking the combined outcome.
        for (Thread thread : threads) { thread.join(); }
        // Verify this test expectation: Concurrent partial deliveries cannot exceed ordered quantity. A false
        // result raises an AssertionError.
        check(unexpected.isEmpty() && saved.get()==1,"Concurrent partial deliveries cannot exceed ordered quantity");
        // Verify this test expectation: Losing concurrent delivery creates no inventory. A false result raises
        // an AssertionError.
        check(new FulfilmentDAO().inventory("RACE-").size()==2,"Losing concurrent delivery creates no inventory");
    }

}
