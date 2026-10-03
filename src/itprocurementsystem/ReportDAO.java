package itprocurementsystem;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

// Every report checks the database role and ownership before returning any data.
// Explicit SELECT lists deliberately exclude password_hash and internal supplier costs.
public class ReportDAO {
    private void allowUser(Connection c, int user) throws SQLException {
        Database.require(c,"Requester","Manager","Purchaser","Admin");
        if (user != Session.getUserId()) { Database.require(c,"Admin"); }
    }
    public ReportData account(int user, boolean includeHistory) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            allowUser(c,user);
            ReportData report = new ReportData("Account details","ACCOUNT-" + user);
            Object[] values = Database.one(c,"SELECT u.user_id,u.full_name,u.username,u.email,u.gender,u.account_type,u.organisation_name,d.department_name,u.role FROM users u LEFT JOIN departments d ON d.department_id=u.department_id WHERE u.user_id=?",user);
            String[] labels = {"User ID","Full name","Username","Email","Gender","Account type","Organisation","Department","Role"};
            ArrayList<Object[]> rows = new ArrayList<Object[]>();
            for (int i=0;i<labels.length;i++) { rows.add(new Object[]{labels[i],values[i]}); }
            report.add("Account information",new String[]{"Field","Details"},rows);
            if (includeHistory) { addHistory(c,report,user); }
            return report;
        }
    }
    public ReportData history(int user) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            allowUser(c,user);
            ReportData report = new ReportData("Procurement history","HISTORY-" + user);
            Object[] owner = Database.one(c,"SELECT full_name,username FROM users WHERE user_id=?",user);
            ArrayList<Object[]> identity = new ArrayList<Object[]>(); identity.add(owner);
            report.add("Account",new String[]{"Full name","Username"},identity);
            addHistory(c,report,user);
            return report;
        }
    }
    private void addHistory(Connection c, ReportData report, int user) throws SQLException {
        report.add("Requests and orders (UGX)",new String[]{"Request","Date","Type","Status","Total (UGX)","Price basis"},
                Database.rows(c,"SELECT r.request_id,r.date_created,r.request_type,r.request_status,COALESCE(SUM(i.quantity*i.estimated_cost),0),IF(COUNT(i.catalogue_id)=COUNT(i.request_item_id) AND COUNT(i.request_item_id)>0,'Saved catalogue','Legacy estimate') FROM requests r LEFT JOIN request_items i ON i.request_id=r.request_id WHERE r.requester_id=? GROUP BY r.request_id,r.date_created,r.request_type,r.request_status ORDER BY r.request_id DESC",user));
        report.add("Order decisions",new String[]{"Request","Decision","Date"},Database.rows(c,"SELECT d.request_id,d.decision,d.decision_date FROM order_decisions d JOIN requests r ON r.request_id=d.request_id WHERE r.requester_id=? ORDER BY d.request_id DESC",user));
        // Preserve decisions made before catalogue orders were introduced, without showing supplier costs.
        report.add("Legacy customer decisions",new String[]{"Request","Decision","Date"},Database.rows(c,"SELECT q.request_id,d.decision,d.decision_date FROM customer_decisions d JOIN quotations q ON q.quotation_id=d.quotation_id JOIN requests r ON r.request_id=q.request_id WHERE r.requester_id=? ORDER BY d.decision_date DESC",user));
        report.add("Deliveries",new String[]{"Request","Delivery","Date","Status"},Database.rows(c,"SELECT r.request_id,d.delivery_id,d.delivery_date,d.delivery_status FROM deliveries d JOIN requests r ON r.request_id=d.request_id WHERE r.requester_id=? ORDER BY d.delivery_id DESC",user));
        report.add("Services",new String[]{"Request","Work status","Completion date"},Database.rows(c,"SELECT s.request_id,s.work_status,s.completion_date FROM service_progress s JOIN requests r ON r.request_id=s.request_id WHERE r.requester_id=? ORDER BY s.request_id DESC",user));
    }
    public ReportData users() throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Database.require(c,"Admin");
            ReportData report = new ReportData("User directory","USERS");
            report.add("Registered accounts",new String[]{"ID","Full name","Username","Email","Type","Role"},
                    Database.rows(c,"SELECT user_id,full_name,username,email,account_type,role FROM users ORDER BY full_name,user_id"));
            return report;
        }
    }
    public ReportData request(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Database.require(c,"Requester","Admin");
            Object[] request = Database.one(c,"SELECT requester_id,request_type,request_status,date_created,notes FROM requests WHERE request_id=?",id);
            allowUser(c,Database.id(request[0]));
            ReportData report = new ReportData("Procurement request","REQUEST-" + id);
            ArrayList<Object[]> summary = new ArrayList<Object[]>();
            Object[] customer = Database.one(c,"SELECT full_name,username FROM users WHERE user_id=?",request[0]);
            summary.add(new Object[]{"Customer",customer[0] + " (" + customer[1] + ")"});
            summary.add(new Object[]{"Type",request[1]}); summary.add(new Object[]{"Status",request[2]});
            summary.add(new Object[]{"Submitted",request[3]}); summary.add(new Object[]{"Notes",request[4]});
            report.add("Request #" + id,new String[]{"Field","Details"},summary);
            report.add("Requested items",new String[]{"Description","Unit","Qty","Unit price (UGX)","Total (UGX)"},
                    Database.rows(c,"SELECT item_description,COALESCE(unit,'Legacy'),quantity,estimated_cost,quantity*estimated_cost FROM request_items WHERE request_id=? ORDER BY request_item_id",id));
            report.add("Total",new String[]{"Price basis","Amount (UGX)"},Database.rows(c,"SELECT IF(COUNT(catalogue_id)=COUNT(*) AND COUNT(*)>0,'Saved catalogue prices','Legacy estimates - not repriced'),COALESCE(SUM(quantity*estimated_cost),0) FROM request_items WHERE request_id=?",id));
            report.add("Customer decision",new String[]{"Decision","Comments","Date"},Database.rows(c,"SELECT decision,comments,decision_date FROM order_decisions WHERE request_id=?",id));
            if (Database.rows(c,"SELECT request_id FROM order_decisions WHERE request_id=?",id).isEmpty()) {
                report.add("Legacy customer decisions",new String[]{"Decision","Comments","Date"},Database.rows(c,"SELECT d.decision,d.comments,d.decision_date FROM customer_decisions d JOIN quotations q ON q.quotation_id=d.quotation_id WHERE q.request_id=?",id));
            }
            // Staff comments and supplier prices stay internal; the outcome is enough here.
            report.add("Staff approval",new String[]{"Decision","Date"},Database.rows(c,"SELECT approval_status,approval_date FROM approvals WHERE request_id=?",id));
            if ("Equipment".equals(request[1])) { report.add("Equipment received",new String[]{"Description","Serial number","Date"},Database.rows(c,"SELECT i.item_description,i.serial_number,d.delivery_date FROM delivery_items i JOIN deliveries d ON d.delivery_id=i.delivery_id WHERE d.request_id=? ORDER BY i.delivery_item_id",id)); }
            if ("Service".equals(request[1])) { report.add("Service progress",new String[]{"Status","Notes","Completion date"},Database.rows(c,"SELECT work_status,work_notes,completion_date FROM service_progress WHERE request_id=?",id)); }
            return report;
        }
    }
}
