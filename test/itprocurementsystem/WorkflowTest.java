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
        login(manager,"Manager"); dao.changeRole(other,"Requester");
        testDecisions();
        testFulfilment();
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

}
