package itprocurementsystem;

import java.sql.*;
import java.util.ArrayList;

// Run only against a disposable database using -Dprocurement.test.url=... .
// The tests create fictional accounts; they never need real account passwords.
public class WorkflowTest {
    private static int manager, purchaser, customer, other;
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
        if (!url.contains("/procurement_test")) { throw new IllegalStateException("Use a disposable procurement_test database."); }
        try (Connection c = DBConnection.getConnection()) {
            manager = user(c,"test_manager","Manager"); purchaser = user(c,"test_purchaser","Purchaser");
            customer = user(c,"test_customer","Requester"); other = user(c,"test_other","Requester");
        }
        try (Connection c = DBConnection.getConnection()) { DatabaseSetup.ensureWorkflowTables(c); }
        ManagementDAO dao = new ManagementDAO();
        login(manager,"Manager");
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
        testDecisions();
        System.out.println("Passed " + checks + " checks.");
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
        items.add(new RequestItem(new Category(category,"Test category"),"Test item",quantity,new java.math.BigDecimal("10.00")));
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

}
