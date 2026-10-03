package itprocurementsystem;

// JDBC classes let us save records and read the new request's generated ID.
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

// This data access class keeps the saving SQL separate from the Swing form.
public class RequestDAO {

    // Read only requests belonging to the currently signed-in requester.
    public ArrayList<RequestSummary> getMyRequests() throws SQLException {
        // Check the session here as well as controlling which buttons are visible.
        if (Session.getUserId() <= 0 || !"Requester".equals(Session.getRole())) {
            throw new IllegalArgumentException("Please log in as a requester first.");
        }
        ArrayList<RequestSummary> requests = new ArrayList<RequestSummary>();

        // LEFT JOIN keeps a request visible even if it has no items.
        // SUM adds the line totals; COALESCE uses zero when there are no item values.
        // The WHERE condition ensures we do not return another requester's records.
        String sql = "SELECT r.request_id, r.date_created, r.request_status, r.notes, "
                + "COALESCE(SUM(i.quantity * i.estimated_cost), 0) AS total "
                + "FROM requests r LEFT JOIN request_items i ON i.request_id = r.request_id "
                + "WHERE r.requester_id = ? "
                + "GROUP BY r.request_id, r.date_created, r.request_status, r.notes "
                + "ORDER BY r.date_created DESC, r.request_id DESC";

        // Automatically close the connection, statement and results after reading.
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            Database.require(connection, "Requester");
            statement.setInt(1, Session.getUserId());
            try (ResultSet result = statement.executeQuery()) {
                // Create one summary per request, with the newest requests first.
                while (result.next()) {
                    requests.add(new RequestSummary(result.getInt("request_id"),
                            result.getTimestamp("date_created"),
                            result.getString("request_status"), result.getBigDecimal("total"),
                            result.getString("notes")));
                }
            }
        }
        return requests;
    }


    // Return the new request ID only after all its items have been saved.
    public int saveRequest(int requesterId, String notes, ArrayList<RequestItem> items,
            String requestType) throws SQLException {
        // Older callers can submit without attachments using this simpler overload.
        return saveRequest(requesterId, notes, items, requestType, new ArrayList<java.io.File>());
    }

    // Save document copies and their records in the same request operation.
    public int saveRequest(int requesterId, String notes, ArrayList<RequestItem> items,
            String requestType, ArrayList<java.io.File> attachments) throws SQLException {
        if (requesterId != Session.getUserId() || requesterId <= 0) { throw new IllegalArgumentException("You can submit only your own request."); }
        if (attachments == null || attachments.size() > 10) { throw new IllegalArgumentException("Choose at most 10 attachments."); }
        for (java.io.File file : attachments) { AttachmentDAO.validate(file); }
        notes = Database.text(notes,"Request notes",4000,false);
        // Keep copied paths so a failed database save can remove just its own copies.
        ArrayList<java.nio.file.Path> copied = new ArrayList<java.nio.file.Path>();
        // The prompt is not a type. Validate here as well as in the form.
        if (!"Equipment".equals(requestType) && !"Service".equals(requestType)) {
            throw new IllegalArgumentException("Select Equipment or Service.");
        }
        // Check the essential values even if another screen calls this method later.
        if (requesterId <= 0 || items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Log in and add at least one item first.");
        }
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == null) {
                throw new IllegalArgumentException("The request contains an empty item.");
            }
        }

        // Automatically close this connection when saving finishes or fails.
        try (Connection connection = DBConnection.getConnection()) {
            // A transaction groups the request and its items into one complete change.
            // Turning off auto-commit prevents each INSERT from being saved separately.
            connection.setAutoCommit(false);
            Database.require(connection, "Requester");
            try {
                Integer departmentId;
                // Read the department and role from MySQL, not from user-entered fields.
                String userSql = "SELECT department_id, role FROM users WHERE user_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(userSql)) {
                    statement.setInt(1, requesterId);
                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next() || !"Requester".equals(result.getString("role"))) {
                            throw new SQLException("A requester account is required.");
                        }
                        departmentId = result.getInt("department_id");
                        // getInt returns zero for SQL NULL, so preserve the missing value explicitly.
                        if (result.wasNull()) { departmentId = null; }
                    }
                }

                int requestId;
                // MySQL supplies the creation time. Every new request starts as Pending.
                String requestSql = "INSERT INTO requests "
                        + "(requester_id, department_id, request_status, notes, request_type) VALUES (?, ?, 'Pending', ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(
                        requestSql, Statement.RETURN_GENERATED_KEYS)) {
                    // Place values in the question-mark placeholders safely.
                    statement.setInt(1, requesterId);
                    if (departmentId == null) { statement.setNull(2, java.sql.Types.INTEGER); }
                    else { statement.setInt(2, departmentId); }
                    statement.setString(3, notes == null ? "" : notes.trim());
                    statement.setString(4, requestType);
                    statement.executeUpdate();
                    // AUTO_INCREMENT creates an ID. Use it to link every item below.
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("The new request ID was not returned.");
                        }
                        requestId = keys.getInt(1);
                    }
                }

                String itemSql = "INSERT INTO request_items "
                        + "(request_id, category_id, item_description, quantity, estimated_cost) "
                        + "VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(itemSql)) {
                    // Reuse the statement with each item's values, one row at a time.
                    for (int i = 0; i < items.size(); i++) {
                        RequestItem item = items.get(i);
                        statement.setInt(1, requestId);
                        statement.setInt(2, item.getCategory().getCategoryId());
                        statement.setString(3, item.getDescription());
                        statement.setInt(4, item.getQuantity());
                        // estimated_cost stores one unit's price, not the line total.
                        statement.setBigDecimal(5, item.getUnitCost());
                        statement.executeUpdate();
                    }
                }

                // A short unique storage name avoids overwrites and filesystem filename limits.
                java.nio.file.Path folder = java.nio.file.Paths.get(System.getProperty("procurement.attachments.dir",
                        System.getProperty("user.home") + "/.it-procurement/attachments"));
                for (java.io.File file : attachments) {
                    try {
                        java.nio.file.Files.createDirectories(folder);
                        String extension = file.getName().substring(file.getName().lastIndexOf('.'));
                        java.nio.file.Path target = folder.resolve(java.util.UUID.randomUUID().toString() + extension);
                        Database.text(target.toAbsolutePath().toString(),"Stored file path",500,true);
                        java.nio.file.Files.copy(file.toPath(),target);
                        copied.add(target);
                        // Check the copied size too, in case the original changed while being copied.
                        if (java.nio.file.Files.size(target) > 10 * 1024 * 1024) { throw new IllegalArgumentException("The copied attachment exceeds 10 MB."); }
                        Database.update(connection,"INSERT INTO attachments(request_id,file_name,file_path,uploaded_by) VALUES (?,?,?,?)",
                                requestId,file.getName(),target.toAbsolutePath().toString(),requesterId);
                    } catch (java.io.IOException ex) { throw new SQLException("Could not copy an attachment. The request was not saved.",ex); }
                }
                Database.audit(connection,"Submitted " + requestType + " request","requests",requestId);
                Database.notify(connection,requesterId,"Request #" + requestId + " submitted successfully.");
                for (Object[] purchaser : Database.rows(connection,"SELECT user_id FROM users WHERE role='Purchaser'")) {
                    Database.notify(connection,Database.id(purchaser[0]),"New " + requestType + " request #" + requestId + " needs quotations.");
                }

                // Save everything together only after every INSERT succeeds.
                connection.commit();
                return requestId;
            } catch (SQLException | RuntimeException ex) {
                // Undo this transaction if any step fails, avoiding incomplete requests.
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    // Keep the original error while recording a rollback failure too.
                    ex.addSuppressed(rollbackError);
                }
                // Filesystem copies are outside MySQL, so clean them up explicitly on failure.
                for (java.nio.file.Path path : copied) {
                    try { java.nio.file.Files.deleteIfExists(path); }
                    catch (java.io.IOException cleanupError) { ex.addSuppressed(cleanupError); }
                }
                throw ex;
            }
        }
    }
}
