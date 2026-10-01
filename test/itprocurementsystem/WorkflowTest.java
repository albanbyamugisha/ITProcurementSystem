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
        System.out.println("Passed " + checks + " checks.");
    }
}
