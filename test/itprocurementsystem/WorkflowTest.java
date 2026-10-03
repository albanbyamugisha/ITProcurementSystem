package itprocurementsystem;

import java.sql.*;
import java.util.ArrayList;

// Run only against a disposable database using -Dprocurement.test.url=... .
// The tests create fictional accounts; they never need real account passwords.
public class WorkflowTest {
    private static int admin, manager, purchaser, customer, other;
    private static int checks;
    private static void check(boolean value, String message) {
        if (!value) { throw new AssertionError(message); }
        checks++;
    }
    private static void login(int id, String role) { Session.start(id, "test", "Test User", role); }
    private static int user(Connection c, String name, String role) throws SQLException {
        return Database.insert(c, "INSERT INTO users(username,password_hash,full_name,email,role,account_type) VALUES (?,?,?,?,?,'Individual')",
                name, "not-a-login-hash", name, name + "@example.invalid", role);
    }
    public static void main(String[] args) throws Exception {
        String url = System.getProperty("procurement.test.url", "");
        if (!url.matches("jdbc:mysql://(127\\.0\\.0\\.1|localhost):[0-9]+/procurement_test(\\?.*)?")) { throw new IllegalStateException("Use a disposable procurement_test database."); }
        try (Connection c = DBConnection.getConnection()) {
            admin = user(c,"test_admin","Admin");
            manager = user(c,"test_manager","Manager"); purchaser = user(c,"test_purchaser","Purchaser");
            customer = user(c,"test_customer","Requester"); other = user(c,"test_other","Requester");
        }
        try (Connection c = DBConnection.getConnection()) { DatabaseSetup.ensureWorkflowTables(c); }
        ManagementDAO dao = new ManagementDAO();
        login(admin,"Admin");
        dao.saveDepartment(0,"Engineering");
        int department = Database.id(dao.departments().get(0)[0]);
        dao.saveDepartment(department,"Operations");
        check("Operations".equals(dao.departments().get(0)[1]),"Department update");
        dao.saveVendor(0,"Test Supplier","Contact","123","supplier@example.invalid","Test address");
        check(dao.vendors().size()==1,"Vendor insert");
        dao.changeRole(other,"Purchaser");
        login(other,"Requester"); // A stale session role must not bypass the database role check.
        check(dao.notifications("Unread").size()==1,"Role notification");
        dao.markRead(0);
        check(dao.unreadCount()==0,"Mark all read");
        login(customer,"Requester");
        boolean refused=false;
        try { dao.changeRole(customer,"Manager"); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Customer cannot promote own account");
        refused=false;
        try { dao.saveDepartment(0,"Forbidden"); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Customer cannot manage departments");
        login(admin,"Admin"); dao.changeRole(other,"Requester");
        testDecisions();
        testFulfilment();
        testAttachments();
        testRegistration();
        testRecovery();
        testCatalogue();
        testPanels();
        testDeclineAndRejection();
        testConcurrentSelection();
        testConcurrentDeliveries();
        System.out.println("Passed " + checks + " checks.");
    }
    // Customer prices must come from the catalogue and survive later Admin edits.
    private static void testCatalogue() throws Exception {
        login(customer,"Requester");
        ArrayList<RequestItem> items = new ArrayList<RequestItem>();
        RequestItem original = catalogueItem("Equipment",2);
        items.add(original);
        int request = new RequestDAO().saveRequest(customer,"Price snapshot",items,"Equipment");
        boolean refused = false;
        try { new CatalogueDAO().save(0,"Forbidden","Equipment",original.getCategory().getCategoryId(),"Each","Test","1",true); }
        catch (IllegalArgumentException ex) { refused = true; }
        check(refused,"Customer cannot change catalogue prices");
        items.clear();
        items.add(new RequestItem(original.getCatalogueId(),original.getCategory(),original.getDescription(),
                original.getUnit(),1,new java.math.BigDecimal("0.01")));
        refused = false;
        try { new RequestDAO().saveRequest(customer,"Spoofed price",items,"Equipment"); }
        catch (IllegalArgumentException ex) { refused = true; }
        check(refused,"Spoofed customer price rejected");
        login(admin,"Admin");
        new CatalogueDAO().save(original.getCatalogueId(),"Changed demo item","Equipment",original.getCategory().getCategoryId(),
                original.getUnit(),original.getDescription(),original.getUnitCost().add(java.math.BigDecimal.ONE).toPlainString(),true);
        login(customer,"Requester"); items.clear(); items.add(original); refused = false;
        try { new RequestDAO().saveRequest(customer,"Stale price",items,"Equipment"); }
        catch (IllegalArgumentException ex) { refused = true; }
        check(refused,"Stale displayed price rejected");
        try (Connection c = DBConnection.getConnection()) {
            java.math.BigDecimal saved = (java.math.BigDecimal) Database.one(c,"SELECT estimated_cost FROM request_items WHERE request_id=?",request)[0];
            check(saved.compareTo(original.getUnitCost())==0,"Historical selling price unchanged");
            DatabaseSetup.ensureWorkflowTables(c);
            check("Changed demo item".equals(Database.one(c,"SELECT item_name FROM catalogue WHERE catalogue_id=?",original.getCatalogueId())[0]),"Startup preserves Admin edits");
        }
        login(manager,"Manager"); refused = false;
        try { new ManagementDAO().users(""); } catch (IllegalArgumentException ex) { refused = true; }
        check(refused,"Only Admin may list all users");
        login(customer,"Requester");
        Object[] order = new DecisionDAO().quotations(request,false).get(0);
        check(((java.math.BigDecimal)order[2]).compareTo(original.getLineTotal())==0,"Customer sees saved selling total");
    }

    // Recovery changes only the password and expires sessions created before the reset.
    private static void testRecovery() throws Exception {
        String oldPassword = java.util.UUID.randomUUID().toString();
        new RegistrationDAO().register("Recovery User", "recovery_user", "recovery@example.invalid", 0,
                oldPassword, oldPassword, "Individual", null, "Other");
        check(new UserDAO().checkLogin("recovery_user", oldPassword), "New registration can authenticate immediately");
        int id = Session.getUserId();
        boolean refused = false;
        try { new PasswordResetDAO().reset("recovery_user", "Wrong Name"); }
        catch (IllegalArgumentException ex) { refused = true; }
        check(refused, "Wrong recovery name rejected");
        String replacement = new PasswordResetDAO().reset("recovery_user", "Recovery User");
        refused = false;
        try { new ManagementDAO().notifications("All notifications"); }
        catch (IllegalArgumentException ex) { refused = true; }
        check(refused, "Reset expires an old session");
        check(!new UserDAO().checkLogin("recovery_user", oldPassword), "Old password no longer works");
        check(new UserDAO().checkLogin("recovery_user", replacement), "Replacement password works");
        try (Connection c = DBConnection.getConnection()) {
            Object[] row = Database.one(c, "SELECT gender,role,password_hash FROM users WHERE user_id=?", id);
            check("Other".equals(row[0]) && "Requester".equals(row[1]), "Recovery preserves gender and role");
            check(!replacement.equals(row[2]), "Only the password hash is stored");
        }
    }

    // New requests must use an actual active catalogue item, including its saved description.
    private static RequestItem catalogueItem(String type, int quantity) throws Exception {
        Object[] row = new CatalogueDAO().list("",type).get(0);
        return new RequestItem(Database.id(row[0]),new Category(Database.id(row[8]),row[3].toString()),
                row[7].toString(),row[4].toString(),quantity,(java.math.BigDecimal)row[5]);
    }
    // Build a real request and supplier quote using the application DAOs.
    private static int[] quotedRequest(String type, int quantity) throws Exception {
        int category, vendor;
        try (Connection c = DBConnection.getConnection()) {
            category = Database.insert(c,"INSERT INTO categories(category_name) VALUES ('Test category')");
            vendor = Database.id(Database.one(c,"SELECT vendor_id FROM vendors LIMIT 1")[0]);
        }
        login(customer,"Requester");
        ArrayList<RequestItem> items = new ArrayList<RequestItem>();
        items.add(catalogueItem(type, quantity));
        int request = new RequestDAO().saveRequest(customer,"Test request",items,type);
        login(purchaser,"Purchaser");
        QuotationDAO quotes = new QuotationDAO();
        ArrayList<QuotationItem> prices = quotes.getRequestItems(request);
        for (QuotationItem item : prices) { item.setUnitPrice(new java.math.BigDecimal("12.50")); }
        int quote = quotes.saveQuotation(request,vendor,"Test quotation",prices);
        return new int[] {request,quote,prices.get(0).getRequestItemId()};
    }
    private static void testDecisions() throws Exception {
        int[] ids = quotedRequest("Equipment",2);
        DecisionDAO decisions = new DecisionDAO();
        login(other,"Requester");
        boolean refused=false;
        try { decisions.customerDecision(ids[0],ids[1],true,""); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Another account cannot accept a customer's quotation");
        login(customer,"Requester");
        decisions.customerDecision(ids[0],ids[1],true,"Please proceed");
        refused=false;
        try { decisions.customerDecision(ids[0],ids[1],true,""); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Repeated customer decision refused");
        login(manager,"Manager");
        refused=false;
        try { decisions.review(ids[0],ids[1]+999,true,""); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Cannot approve a different quotation");
        decisions.review(ids[0],ids[1],true,"Reviewed");
        check(decisions.history(ids[0]).size()==1,"One approval history entry");
        refused=false;
        try { decisions.review(ids[0],ids[1],true,""); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Repeated staff approval refused");
    }

    private static int[] approvedRequest(String type, int quantity) throws Exception {
        int[] ids = quotedRequest(type,quantity);
        login(customer,"Requester"); new DecisionDAO().customerDecision(ids[0],ids[1],true,"");
        login(manager,"Manager"); new DecisionDAO().review(ids[0],ids[1],true,"");
        login(purchaser,"Purchaser"); return ids;
    }
    private static void testFulfilment() throws Exception {
        FulfilmentDAO dao = new FulfilmentDAO();
        String today = java.time.LocalDate.now().toString();
        int[] equipment = approvedRequest("Equipment",2);
        ArrayList<DeliveryUnit> units = new ArrayList<DeliveryUnit>();
        units.add(new DeliveryUnit(equipment[2],"TEST-SERIAL-1"));
        dao.saveDelivery(equipment[0],today,units);
        check(((Number)dao.equipmentItems(equipment[0]).get(0)[4]).intValue()==1,"Partial delivery leaves one unit");
        boolean refused=false;
        try { dao.saveDelivery(equipment[0],today,units); } catch (SQLException ex) { refused=true; }
        check(refused,"Duplicate serial rejected by database");
        check(dao.inventory("TEST-SERIAL").size()==1,"Duplicate failure rolls back inventory");
        units.clear(); units.add(new DeliveryUnit(equipment[2],"TEST-SERIAL-2")); units.add(new DeliveryUnit(equipment[2],"TEST-SERIAL-3"));
        refused=false;
        try { dao.saveDelivery(equipment[0],today,units); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Overdelivery rejected");
        check(dao.inventory("TEST-SERIAL").size()==1,"Overdelivery rolls back all staged units");
        units.remove(1); dao.saveDelivery(equipment[0],today,units);
        check(dao.inventory("TEST-SERIAL").size()==2,"Final delivery creates exact inventory count");
        int inventory = Database.id(dao.inventory("TEST-SERIAL-1").get(0)[0]);
        dao.assign(inventory,customer);
        check("test_customer".equals(dao.inventory("TEST-SERIAL-1").get(0)[4]),"Inventory assignment");
        dao.assign(inventory,null);
        check(dao.inventory("TEST-SERIAL-1").get(0)[4]==null,"Inventory unassignment");
        int[] service = approvedRequest("Service",1);
        refused=false;
        try { dao.saveDelivery(service[0],today,units); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Service cannot create equipment delivery");
        refused=false;
        try { dao.saveProgress(service[0],"Completed","Done",""); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Completion date required");
        dao.saveProgress(service[0],"In Progress","Working","");
        check("In Progress".equals(dao.progress(service[0])[0]),"Service progress persists");
        dao.saveProgress(service[0],"Completed","Done",today);
        try (Connection c = DBConnection.getConnection()) {
            check("Completed".equals(Database.one(c,"SELECT request_status FROM requests WHERE request_id=?",service[0])[0]),"Service completes request");
            check("Delivered".equals(Database.one(c,"SELECT request_status FROM requests WHERE request_id=?",equipment[0])[0]),"Equipment completes request");
        }
        check(dao.inventory("TEST-SERIAL").size()==2,"Service creates no inventory rows");
        login(customer,"Requester"); refused=false;
        try { dao.assign(inventory,customer); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Customer cannot assign inventory");
    }

    private static void testAttachments() throws Exception {
        java.nio.file.Path root = java.nio.file.Files.createTempDirectory("procurement-attachments-test-");
        java.nio.file.Path source = root.resolve("source.txt");
        java.nio.file.Files.write(source,"A test supporting document".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        java.nio.file.Path store = root.resolve("stored");
        System.setProperty("procurement.attachments.dir",store.toString());
        int category;
        try (Connection c = DBConnection.getConnection()) { category = Database.id(Database.one(c,"SELECT category_id FROM categories LIMIT 1")[0]); }
        ArrayList<RequestItem> items = new ArrayList<RequestItem>();
        items.add(catalogueItem("Equipment", 1));
        ArrayList<java.io.File> files = new ArrayList<java.io.File>(); files.add(source.toFile());
        login(customer,"Requester");
        int request = new RequestDAO().saveRequest(customer,"Attachment test",items,"Equipment",files);
        ArrayList<Object[]> saved = new AttachmentDAO().forRequest(request);
        check(saved.size()==1,"Attachment metadata saved");
        java.nio.file.Path copy = java.nio.file.Paths.get(saved.get(0)[1].toString());
        check(java.nio.file.Files.exists(copy) && !copy.equals(source),"Document copied to managed storage");
        login(other,"Requester"); boolean refused=false;
        try { new AttachmentDAO().forRequest(request); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Another customer cannot view attachments");
        login(customer,"Requester"); refused=false;
        try { new RequestDAO().saveRequest(other,"",items,"Equipment"); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Cannot submit as another user");
        int before;
        try (Connection c = DBConnection.getConnection()) {
            before = Database.id(Database.one(c,"SELECT COUNT(*) FROM requests")[0]);
            // Force a late failure after copying a file to exercise cleanup, not just validation.
            Database.update(c,"CREATE TRIGGER fail_attachment BEFORE INSERT ON attachments FOR EACH ROW SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Test rollback'");
        }
        refused=false;
        try { new RequestDAO().saveRequest(customer,"",items,"Equipment",files); } catch (SQLException ex) { refused=true; }
        finally { try (Connection c = DBConnection.getConnection()) { Database.update(c,"DROP TRIGGER fail_attachment"); } }
        check(refused,"Late attachment failure reported");
        try (Connection c = DBConnection.getConnection()) { check(before==Database.id(Database.one(c,"SELECT COUNT(*) FROM requests")[0]),"Failed attachment rolls back request"); }
        try (java.util.stream.Stream<java.nio.file.Path> stored = java.nio.file.Files.list(store)) { check(stored.count()==1,"Failed attachment copy cleaned up"); }
        java.nio.file.Files.delete(copy); java.nio.file.Files.delete(source); java.nio.file.Files.delete(store); java.nio.file.Files.delete(root);
    }
    private static void testRegistration() throws Exception {
        RegistrationDAO dao = new RegistrationDAO();
        // Generate a throwaway test password at runtime rather than publishing credentials.
        String password = java.util.UUID.randomUUID().toString();
        dao.register("New Customer","new_customer","new@example.invalid",0,password,password,"Individual",null);
        boolean refused=false;
        try { dao.register("Duplicate","new_customer","different@example.invalid",0,password,password,"Individual",null); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Duplicate username refused");
        refused=false;
        try { dao.register("Duplicate","different_user","NEW@example.invalid",0,password,password,"Individual",null); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Duplicate email refused");
        refused=false;
        try { dao.register("No Type","no_type","no_type@example.invalid",0,password,password,"",null); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Account type required");
        check(new UserDAO().checkLogin("new_customer",password),"New account can log in");
        check("Requester".equals(Session.getRole()),"Public registration cannot create staff");
    }
    // Real JPanel constructors can run without a desktop; JFrame windows still need a display.
    private static void testPanels() throws Exception {
        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                login(customer,"Requester");
                new RequestPanel(); new MyRequestsPanel(); new CustomerQuotationsPanel(); new NotificationsPanel();
                login(manager,"Manager");
                new ApprovalPanel();
                login(admin,"Admin"); new DepartmentPanel(); new UserManagementPanel(); new CataloguePanel();
                login(purchaser,"Purchaser");
                new QuotationPanel(); new DeliveryPanel(); new ServiceCompletionPanel(); new InventoryPanel(); new VendorPanel();
                check(true,"All 12 actual JPanel constructors load successfully");
            }
        });
    }

    private static void testDeclineAndRejection() throws Exception {
        int[] ids = quotedRequest("Service",1);
        DecisionDAO decisions = new DecisionDAO();
        login(customer,"Requester"); decisions.customerDecision(ids[0],ids[1],false,"Too expensive");
        check("Declined".equals(decisions.quotations(ids[0],false).get(0)[3]),"Customer decline persists");
        login(manager,"Manager"); boolean refused=false;
        try { decisions.review(ids[0],ids[1],true,""); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Declined quote cannot be approved");
        int[] rejected = quotedRequest("Equipment",1);
        login(customer,"Requester"); decisions.customerDecision(rejected[0],rejected[1],true,"");
        login(manager,"Manager"); refused=false;
        try { decisions.review(rejected[0],rejected[1],false,""); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Rejection needs a reason");
        decisions.review(rejected[0],rejected[1],false,"Unable to fulfil");
        check("Rejected".equals(decisions.history(rejected[0]).get(0)[2]),"Rejection history persists");
        login(purchaser,"Purchaser"); refused=false;
        try { new FulfilmentDAO().equipmentItems(rejected[0]); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Rejected request cannot be delivered");
        login(admin,"Admin"); new ManagementDAO().changeRole(other,"Requester");
        login(other,"Purchaser"); refused=false;
        try { new QuotationDAO().getOpenRequestIds(); } catch (IllegalArgumentException ex) { refused=true; }
        check(refused,"Stale purchaser session cannot read all requests");
    }
    // Two simultaneous connections must still produce only one selected quotation.
    private static void testConcurrentSelection() throws Exception {
        final int[] first = quotedRequest("Equipment",1);
        int vendor;
        try (Connection c = DBConnection.getConnection()) { vendor = Database.id(Database.one(c,"SELECT vendor_id FROM vendors LIMIT 1")[0]); }
        QuotationDAO quotes = new QuotationDAO();
        ArrayList<QuotationItem> prices = quotes.getRequestItems(first[0]);
        for (QuotationItem item : prices) { item.setUnitPrice(new java.math.BigDecimal("11.00")); }
        final int second = quotes.saveQuotation(first[0],vendor,"Alternative",prices);
        login(customer,"Requester");
        final java.util.concurrent.CountDownLatch start = new java.util.concurrent.CountDownLatch(1);
        final java.util.concurrent.atomic.AtomicInteger saved = new java.util.concurrent.atomic.AtomicInteger();
        final java.util.ArrayList<Throwable> unexpected = new java.util.ArrayList<Throwable>();
        Thread[] threads = new Thread[2];
        for (int i=0;i<2;i++) {
            final int quote = i==0 ? first[1] : second;
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    try { start.await(); new DecisionDAO().customerDecision(first[0],quote,true,""); saved.incrementAndGet(); }
                    catch (IllegalArgumentException expected) { /* The second decision must be refused. */ }
                    catch (Throwable ex) { synchronized(unexpected) { unexpected.add(ex); } }
                }
            });
            threads[i].start();
        }
        start.countDown();
        for (Thread thread : threads) { thread.join(); }
        check(unexpected.isEmpty() && saved.get()==1,"Concurrent decisions produce one winner");
        try (Connection c = DBConnection.getConnection()) {
            check(Database.id(Database.one(c,"SELECT COUNT(*) FROM order_decisions WHERE request_id=?",first[0])[0])==1,"One selected quotation stored");
        }
    }

    private static void testConcurrentDeliveries() throws Exception {
        final int[] ids = approvedRequest("Equipment",3);
        final java.util.concurrent.CountDownLatch start = new java.util.concurrent.CountDownLatch(1);
        final java.util.concurrent.atomic.AtomicInteger saved = new java.util.concurrent.atomic.AtomicInteger();
        final java.util.ArrayList<Throwable> unexpected = new java.util.ArrayList<Throwable>();
        Thread[] threads = new Thread[2];
        for (int i=0;i<2;i++) {
            final int batch = i;
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    ArrayList<DeliveryUnit> units = new ArrayList<DeliveryUnit>();
                    units.add(new DeliveryUnit(ids[2],"RACE-"+batch+"-A"));
                    units.add(new DeliveryUnit(ids[2],"RACE-"+batch+"-B"));
                    try {
                        start.await();
                        new FulfilmentDAO().saveDelivery(ids[0],java.time.LocalDate.now().toString(),units);
                        saved.incrementAndGet();
                    } catch (IllegalArgumentException expected) { /* Only one batch of two fits an order for three. */ }
                    catch (Throwable ex) { synchronized(unexpected) { unexpected.add(ex); } }
                }
            });
            threads[i].start();
        }
        start.countDown();
        for (Thread thread : threads) { thread.join(); }
        check(unexpected.isEmpty() && saved.get()==1,"Concurrent partial deliveries cannot exceed ordered quantity");
        check(new FulfilmentDAO().inventory("RACE-").size()==2,"Losing concurrent delivery creates no inventory");
    }

}
