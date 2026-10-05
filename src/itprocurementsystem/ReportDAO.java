// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Import Connection for a JDBC connection to the database.
import java.sql.Connection;
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;

// Every report checks the database role and ownership before returning any data.
// Explicit SELECT lists deliberately exclude password_hash and internal supplier costs.
// Define ReportDAO as a class that groups its related data and methods.
public class ReportDAO {
    // Allow a user to read their own report; require Admin access when the report belongs to another
    // account.
    private void allowUser(Connection c, int user) throws SQLException {
        // Recheck the database session and allow only the listed roles to continue.
        Database.require(c,"Requester","Manager","Purchaser","Admin");
        // Check the session identity before continuing so a missing or different account follows this branch.
        // Recheck the database session and allow only the listed role to continue.
        if (user != Session.getUserId()) { Database.require(c,"Admin"); }
    }
    // Build account-report sections from explicit public account fields, optionally including procurement
    // history.
    public ReportData account(int user, boolean includeHistory) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Allow a user to read their own report; require Admin access when the report belongs to another
            // account.
            allowUser(c,user);
            // Declare report with type ReportData. Create a report with the supplied title and reference.
            ReportData report = new ReportData("Account details","ACCOUNT-" + user);
            // Declare values to hold one row of values in SELECT or table-column order. Read users records; LEFT
            // JOIN keeps base rows even when related records are missing; WHERE limits the rows to the stated
            // conditions; question marks receive separately bound values.
            // Result columns in order: 0: u.user_id; 1: u.full_name; 2: u.username; 3: u.email; 4: u.gender; 5:
            // u.account_type; 6: u.organisation_name; 7: d.department_name; 8: u.role. Object[] positions start at
            // zero; JDBC column numbers start at one.
            Object[] values = Database.one(c,"SELECT u.user_id,u.full_name,u.username,u.email,u.gender,u.account_type,u.organisation_name,d.department_name,u.role FROM users u LEFT JOIN departments d ON d.department_id=u.department_id WHERE u.user_id=?",user);
            // Declare labels to hold an ordered array of text values. The following expression supplies its
            // initial value.
            String[] labels = {"User ID","Full name","Username","Email","Gender","Account type","Organisation","Department","Role"};
            // Declare rows with type ArrayList<Object[]>. Create an initially empty resizable list.
            ArrayList<Object[]> rows = new ArrayList<Object[]>();
            // Repeat while i is less than labels.length; initialise the counter once and update it after each
            // pass.
            // Append `new Object[]{labels[i], values[i]}` to rows.
            for (int i=0;i<labels.length;i++) { rows.add(new Object[]{labels[i],values[i]}); }
            // Append `"Account information", new String[]{"Field", "Details"}, rows` to report.
            report.add("Account information",new String[]{"Field","Details"},rows);
            // Continue with this branch when (includeHistory).
            // Append request, decision, delivery and service history for the specified account to the report.
            if (includeHistory) { addHistory(c,report,user); }
            // Return report to the caller.
            return report;
        }
    }
    // Build an authorised account's procurement-history report with identity and workflow sections.
    public ReportData history(int user) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Allow a user to read their own report; require Admin access when the report belongs to another
            // account.
            allowUser(c,user);
            // Declare report with type ReportData. Create a report with the supplied title and reference.
            ReportData report = new ReportData("Procurement history","HISTORY-" + user);
            // Declare owner to hold one row of values in SELECT or table-column order. Read users records; WHERE
            // limits the rows to the stated conditions; question marks receive separately bound values.
            // Result columns in order: 0: full_name; 1: username. Object[] positions start at zero; JDBC column
            // numbers start at one.
            Object[] owner = Database.one(c,"SELECT full_name,username FROM users WHERE user_id=?",user);
            // Declare identity with type ArrayList<Object[]>. Create an initially empty resizable list.
            // Append owner to identity.
            ArrayList<Object[]> identity = new ArrayList<Object[]>(); identity.add(owner);
            // Append `"Account", new String[]{"Full name", "Username"}, identity` to report.
            report.add("Account",new String[]{"Full name","Username"},identity);
            // Append request, decision, delivery and service history for the specified account to the report.
            addHistory(c,report,user);
            // Return report to the caller.
            return report;
        }
    }
    // Append request, decision, delivery and service history for the specified account to the report.
    private void addHistory(Connection c, ReportData report, int user) throws SQLException {
        // Read requests records and calculate a record count and add the matching amounts or quantities; LEFT
        // JOIN keeps base rows even when related records are missing; WHERE limits the rows to the stated
        // conditions and ORDER BY fixes their display order; question marks receive separately bound values.
        report.add("Requests and orders (UGX)",new String[]{"Request","Date","Type","Status","Total (UGX)","Price basis"},
                // Result columns in order: 0: r.request_id; 1: r.date_created; 2: r.request_type; 3: r.request_status;
                // 4: COALESCE(SUM(i.quantity*i.estimated_cost),0); 5:
                // IF(COUNT(i.catalogue_id)=COUNT(i.request_item_id) AND COUNT(i.request_item_id)>0,'Saved
                // catalogue','Legacy estimate'). Object[] positions start at zero; JDBC column numbers start at one.
                Database.rows(c,"SELECT r.request_id,r.date_created,r.request_type,r.request_status,COALESCE(SUM(i.quantity*i.estimated_cost),0),IF(COUNT(i.catalogue_id)=COUNT(i.request_item_id) AND COUNT(i.request_item_id)>0,'Saved catalogue','Legacy estimate') FROM requests r LEFT JOIN request_items i ON i.request_id=r.request_id WHERE r.requester_id=? GROUP BY r.request_id,r.date_created,r.request_type,r.request_status ORDER BY r.request_id DESC",user));
        // Read order_decisions records with matching information from related tables; WHERE limits the rows to
        // the stated conditions and ORDER BY fixes their display order; question marks receive separately
        // bound values.
        // Result columns in order: 0: d.request_id; 1: d.decision; 2: d.decision_date. Object[] positions
        // start at zero; JDBC column numbers start at one.
        report.add("Order decisions",new String[]{"Request","Decision","Date"},Database.rows(c,"SELECT d.request_id,d.decision,d.decision_date FROM order_decisions d JOIN requests r ON r.request_id=d.request_id WHERE r.requester_id=? ORDER BY d.request_id DESC",user));
        // Preserve decisions made before catalogue orders were introduced, without showing supplier costs.
        // Read customer_decisions records with matching information from related tables; WHERE limits the rows
        // to the stated conditions and ORDER BY fixes their display order; question marks receive separately
        // bound values.
        // Result columns in order: 0: q.request_id; 1: d.decision; 2: d.decision_date. Object[] positions
        // start at zero; JDBC column numbers start at one.
        report.add("Legacy customer decisions",new String[]{"Request","Decision","Date"},Database.rows(c,"SELECT q.request_id,d.decision,d.decision_date FROM customer_decisions d JOIN quotations q ON q.quotation_id=d.quotation_id JOIN requests r ON r.request_id=q.request_id WHERE r.requester_id=? ORDER BY d.decision_date DESC",user));
        // Read deliveries records with matching information from related tables; WHERE limits the rows to the
        // stated conditions and ORDER BY fixes their display order; question marks receive separately bound
        // values.
        // Result columns in order: 0: r.request_id; 1: d.delivery_id; 2: d.delivery_date; 3:
        // d.delivery_status. Object[] positions start at zero; JDBC column numbers start at one.
        report.add("Deliveries",new String[]{"Request","Delivery","Date","Status"},Database.rows(c,"SELECT r.request_id,d.delivery_id,d.delivery_date,d.delivery_status FROM deliveries d JOIN requests r ON r.request_id=d.request_id WHERE r.requester_id=? ORDER BY d.delivery_id DESC",user));
        // Read service_progress records with matching information from related tables; WHERE limits the rows
        // to the stated conditions and ORDER BY fixes their display order; question marks receive separately
        // bound values.
        // Result columns in order: 0: s.request_id; 1: s.work_status; 2: s.completion_date. Object[] positions
        // start at zero; JDBC column numbers start at one.
        report.add("Services",new String[]{"Request","Work status","Completion date"},Database.rows(c,"SELECT s.request_id,s.work_status,s.completion_date FROM service_progress s JOIN requests r ON r.request_id=s.request_id WHERE r.requester_id=? ORDER BY s.request_id DESC",user));
    }
    // Read account details for an Admin without selecting password hashes.
    public ReportData users() throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed role to continue.
            Database.require(c,"Admin");
            // Declare report with type ReportData. Create a report with the supplied title and reference.
            ReportData report = new ReportData("User directory","USERS");
            // Read users records and ORDER BY fixes their display order.
            report.add("Registered accounts",new String[]{"ID","Full name","Username","Email","Type","Role"},
                    // Result columns in order: 0: user_id; 1: full_name; 2: username; 3: email; 4: account_type; 5: role.
                    // Object[] positions start at zero; JDBC column numbers start at one.
                    Database.rows(c,"SELECT user_id,full_name,username,email,account_type,role FROM users ORDER BY full_name,user_id"));
            // Return report to the caller.
            return report;
        }
    }
    // Build an owner-or-Admin request report, including saved selling prices and fulfilment outcomes.
    public ReportData request(int id) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed roles to continue.
            Database.require(c,"Requester","Admin");
            // Declare request to hold one row of values in SELECT or table-column order. Read requests records;
            // WHERE limits the rows to the stated conditions; question marks receive separately bound values.
            // Result columns in order: 0: requester_id; 1: request_type; 2: request_status; 3: date_created; 4:
            // notes. Object[] positions start at zero; JDBC column numbers start at one.
            Object[] request = Database.one(c,"SELECT requester_id,request_type,request_status,date_created,notes FROM requests WHERE request_id=?",id);
            // Allow a user to read their own report; require Admin access when the report belongs to another
            // account.
            allowUser(c,Database.id(request[0]));
            // Declare report with type ReportData. Create a report with the supplied title and reference.
            ReportData report = new ReportData("Procurement request","REQUEST-" + id);
            // Declare summary with type ArrayList<Object[]>. Create an initially empty resizable list.
            ArrayList<Object[]> summary = new ArrayList<Object[]>();
            // Declare customer to hold one row of values in SELECT or table-column order. Read users records;
            // WHERE limits the rows to the stated conditions; question marks receive separately bound values.
            // Result columns in order: 0: full_name; 1: username. Object[] positions start at zero; JDBC column
            // numbers start at one.
            Object[] customer = Database.one(c,"SELECT full_name,username FROM users WHERE user_id=?",request[0]);
            // Append `new Object[]{"Customer", customer[0] + " (" + customer[1] + ")"}` to summary.
            summary.add(new Object[]{"Customer",customer[0] + " (" + customer[1] + ")"});
            // Append `new Object[]{"Type", request[1]}` to summary.
            // Append `new Object[]{"Status", request[2]}` to summary.
            summary.add(new Object[]{"Type",request[1]}); summary.add(new Object[]{"Status",request[2]});
            // Append `new Object[]{"Submitted", request[3]}` to summary.
            // Append `new Object[]{"Notes", request[4]}` to summary.
            summary.add(new Object[]{"Submitted",request[3]}); summary.add(new Object[]{"Notes",request[4]});
            // Append `"Request #" + id, new String[]{"Field", "Details"}, summary` to report.
            report.add("Request #" + id,new String[]{"Field","Details"},summary);
            // Read request_items records; WHERE limits the rows to the stated conditions and ORDER BY fixes their
            // display order; question marks receive separately bound values.
            report.add("Requested items",new String[]{"Description","Unit","Qty","Unit price (UGX)","Total (UGX)"},
                    // Result columns in order: 0: item_description; 1: COALESCE(unit,'Legacy'); 2: quantity; 3:
                    // estimated_cost; 4: quantity*estimated_cost. Object[] positions start at zero; JDBC column numbers
                    // start at one.
                    Database.rows(c,"SELECT item_description,COALESCE(unit,'Legacy'),quantity,estimated_cost,quantity*estimated_cost FROM request_items WHERE request_id=? ORDER BY request_item_id",id));
            // Read request_items records and calculate a record count and add the matching amounts or quantities;
            // WHERE limits the rows to the stated conditions; question marks receive separately bound values.
            // Result columns in order: 0: IF(COUNT(catalogue_id)=COUNT(*) AND COUNT(*)>0,'Saved catalogue
            // prices','Legacy estimates - not repriced'); 1: COALESCE(SUM(quantity*estimated_cost),0). Object[]
            // positions start at zero; JDBC column numbers start at one.
            report.add("Total",new String[]{"Price basis","Amount (UGX)"},Database.rows(c,"SELECT IF(COUNT(catalogue_id)=COUNT(*) AND COUNT(*)>0,'Saved catalogue prices','Legacy estimates - not repriced'),COALESCE(SUM(quantity*estimated_cost),0) FROM request_items WHERE request_id=?",id));
            // Read order_decisions records; WHERE limits the rows to the stated conditions; question marks receive
            // separately bound values.
            // Result columns in order: 0: decision; 1: comments; 2: decision_date. Object[] positions start at
            // zero; JDBC column numbers start at one.
            report.add("Customer decision",new String[]{"Decision","Comments","Date"},Database.rows(c,"SELECT decision,comments,decision_date FROM order_decisions WHERE request_id=?",id));
            // Continue with this branch when (Database.rows(c, "SELECT request_id FROM order_decisions WHERE
            // request_id=?", id).isEmpty()).
            // Result columns in order: 0: request_id. Object[] positions start at zero; JDBC column numbers start
            // at one.
            if (Database.rows(c,"SELECT request_id FROM order_decisions WHERE request_id=?",id).isEmpty()) {
                // Read customer_decisions records with matching information from related tables; WHERE limits the rows
                // to the stated conditions; question marks receive separately bound values.
                // Result columns in order: 0: d.decision; 1: d.comments; 2: d.decision_date. Object[] positions start
                // at zero; JDBC column numbers start at one.
                report.add("Legacy customer decisions",new String[]{"Decision","Comments","Date"},Database.rows(c,"SELECT d.decision,d.comments,d.decision_date FROM customer_decisions d JOIN quotations q ON q.quotation_id=d.quotation_id WHERE q.request_id=?",id));
            }
            // Staff comments and supplier prices stay internal; the outcome is enough here.
            // Read approvals records; WHERE limits the rows to the stated conditions; question marks receive
            // separately bound values.
            // Result columns in order: 0: approval_status; 1: approval_date. Object[] positions start at zero;
            // JDBC column numbers start at one.
            report.add("Staff approval",new String[]{"Decision","Date"},Database.rows(c,"SELECT approval_status,approval_date FROM approvals WHERE request_id=?",id));
            // Continue with this branch when ("Equipment".equals(request[1])).
            // Read delivery_items records with matching information from related tables; WHERE limits the rows to
            // the stated conditions and ORDER BY fixes their display order; question marks receive separately
            // bound values.
            // Result columns in order: 0: i.item_description; 1: i.serial_number; 2: d.delivery_date. Object[]
            // positions start at zero; JDBC column numbers start at one.
            if ("Equipment".equals(request[1])) { report.add("Equipment received",new String[]{"Description","Serial number","Date"},Database.rows(c,"SELECT i.item_description,i.serial_number,d.delivery_date FROM delivery_items i JOIN deliveries d ON d.delivery_id=i.delivery_id WHERE d.request_id=? ORDER BY i.delivery_item_id",id)); }
            // Continue with this branch when ("Service".equals(request[1])).
            // Read service_progress records; WHERE limits the rows to the stated conditions; question marks
            // receive separately bound values.
            // Result columns in order: 0: work_status; 1: work_notes; 2: completion_date. Object[] positions start
            // at zero; JDBC column numbers start at one.
            if ("Service".equals(request[1])) { report.add("Service progress",new String[]{"Status","Notes","Completion date"},Database.rows(c,"SELECT work_status,work_notes,completion_date FROM service_progress WHERE request_id=?",id)); }
            // Return report to the caller.
            return report;
        }
    }
}
