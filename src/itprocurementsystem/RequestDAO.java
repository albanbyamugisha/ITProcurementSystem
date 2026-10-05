// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// JDBC classes allow the application to save records and read the new request's generated ID.
// Import Connection for a JDBC connection to the database.
import java.sql.Connection;
// Import PreparedStatement for SQL statements with separate value placeholders.
import java.sql.PreparedStatement;
// Import ResultSet for the rows returned by a database query.
import java.sql.ResultSet;
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;
// Import Statement for JDBC constants, including the option to return generated IDs.
import java.sql.Statement;
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;

// This data access class keeps the saving SQL separate from the Swing form.
// Define RequestDAO as a class that groups its related data and methods.
public class RequestDAO {

    // Read only requests belonging to the currently signed-in requester.
    // Read summaries belonging only to the signed-in requester, including the total of saved item prices.
    public ArrayList<RequestSummary> getMyRequests() throws SQLException {
        // Check the session here as well as controlling which buttons are visible.
        // Check the session identity before continuing so a missing or different account follows this branch.
        if (Session.getUserId() <= 0 || !"Requester".equals(Session.getRole())) {
            // Stop this operation with an exception: Please log in as a requester first.
            throw new IllegalArgumentException("Please log in as a requester first.");
        }
        // Declare requests with type ArrayList<RequestSummary>. Create an initially empty resizable list.
        ArrayList<RequestSummary> requests = new ArrayList<RequestSummary>();

        // LEFT JOIN keeps a request visible even if it has no items.
        // SUM adds the line totals; COALESCE uses zero when there are no item values.
        // The WHERE condition ensures the query does not return another requester's records.
        // Declare sql to hold text. Read requests records and add the matching amounts or quantities; LEFT
        // JOIN keeps base rows even when related records are missing; WHERE limits the rows to the stated
        // conditions and ORDER BY fixes their display order; question marks receive separately bound values.
        String sql = "SELECT r.request_id, r.date_created, r.request_status, r.notes, "
                + "COALESCE(SUM(i.quantity * i.estimated_cost), 0) AS total "
                + "FROM requests r LEFT JOIN request_items i ON i.request_id = r.request_id "
                + "WHERE r.requester_id = ? "
                + "GROUP BY r.request_id, r.date_created, r.request_status, r.notes "
                + "ORDER BY r.date_created DESC, r.request_id DESC";

        // Automatically close the connection, statement and results after reading.
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare connection with type Connection. Open a JDBC connection; the test URL property can select an
        // isolated database instead of the normal one.
        try (Connection connection = DBConnection.getConnection();
                // Declare statement with type PreparedStatement. Prepare the SQL on this connection; supplied values
                // will be bound separately to its placeholders.
                PreparedStatement statement = connection.prepareStatement(sql)) {
            // Recheck the database session and allow only the listed role to continue.
            Database.require(connection, "Requester");
            // Bind the whole-number value the signed-in account ID to SQL placeholder 1.
            statement.setInt(1, Session.getUserId());
            // Open the declared resources for this block; try-with-resources closes them in reverse order even if
            // an error occurs.
            // Declare result with type ResultSet. Execute the prepared SELECT and return its result rows for
            // reading.
            try (ResultSet result = statement.executeQuery()) {
                // Create one summary per request, with the newest requests first.
                // Move to the next database result row and repeat while another row is available.
                while (result.next()) {
                    // Append `new RequestSummary(result.getInt("request_id"), result.getTimestamp("date_created"),
                    // result.getString("request_status"), result.getBigDecimal("total"), result.getString("notes"))` to
                    // requests.
                    requests.add(new RequestSummary(result.getInt("request_id"),
                            result.getTimestamp("date_created"),
                            result.getString("request_status"), result.getBigDecimal("total"),
                            result.getString("notes")));
                }
            }
        }
        // Return requests to the caller.
        return requests;
    }


    // Return the new request ID only after all its items have been saved.
    // Validate and save a request, its fixed-price items and optional attachment copies as one operation.
    public int saveRequest(int requesterId, String notes, ArrayList<RequestItem> items,
            String requestType) throws SQLException {
        // Older callers can submit without attachments using this simpler overload.
        // Validate and save a request, its fixed-price items and optional attachment copies as one operation.
        // Return the resulting value to the caller.
        return saveRequest(requesterId, notes, items, requestType, new ArrayList<java.io.File>());
    }

    // Save document copies and their records in the same request operation.
    // Validate and save a request, its fixed-price items and optional attachment copies as one operation.
    public int saveRequest(int requesterId, String notes, ArrayList<RequestItem> items,
            String requestType, ArrayList<java.io.File> attachments) throws SQLException {
        // Check the session identity before continuing so a missing or different account follows this branch.
        // Stop this operation with an exception: You can submit only ythe own request.
        if (requesterId != Session.getUserId() || requesterId <= 0) { throw new IllegalArgumentException("You can submit only your own request."); }
        // Continue with this branch when (attachments equals no object (null) or the number of entries in
        // attachments is greater than 10).
        // Stop this operation with an exception: Choose at most 10 attachments.
        if (attachments == null || attachments.size() > 10) { throw new IllegalArgumentException("Choose at most 10 attachments."); }
        // Process each entry in attachments in turn, referring to the current entry as file.
        // Check the file type, readability, name length and size before accepting or opening it.
        for (java.io.File file : attachments) { AttachmentDAO.validate(file); }
        // Set notes from the following operation: validate notes as "Request notes" with a maximum of 4000
        // characters; the final argument controls whether a value is required.
        notes = Database.text(notes,"Request notes",4000,false);
        // Keep copied paths so a failed database save can remove just its own copies.
        // Declare copied with type ArrayList<java.nio.file.Path>. Create an initially empty resizable list.
        ArrayList<java.nio.file.Path> copied = new ArrayList<java.nio.file.Path>();
        // The prompt is not a type. Validate here as well as in the form.
        // Continue with this branch when (!"Equipment".equals(requestType) and
        // !"Service".equals(requestType)).
        if (!"Equipment".equals(requestType) && !"Service".equals(requestType)) {
            // Stop this operation with an exception: Select Equipment or Service.
            throw new IllegalArgumentException("Select Equipment or Service.");
        }
        // Check the essential values even if another screen calls this method later.
        // Continue with this branch when (requesterId is at most 0 or items equals no object (null) or items
        // is empty).
        if (requesterId <= 0 || items == null || items.isEmpty()) {
            // Stop this operation with an exception: Log in and add at least one item first.
            throw new IllegalArgumentException("Log in and add at least one item first.");
        }
        // Repeat while i is less than the number of entries in items; initialise the counter once and update
        // it after each pass.
        for (int i = 0; i < items.size(); i++) {
            // Continue with this branch when (items.get(i) equals no object (null)).
            if (items.get(i) == null) {
                // Stop this operation with an exception: The request contains an empty item.
                throw new IllegalArgumentException("The request contains an empty item.");
            }
        }

        // Automatically close this connection when saving finishes or fails.
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare connection with type Connection. Open a JDBC connection; the test URL property can select an
        // isolated database instead of the normal one.
        try (Connection connection = DBConnection.getConnection()) {
            // A transaction groups the request and its items into one complete change.
            // Turning off auto-commit prevents each INSERT from being saved separately.
            // Begin a transaction so later database changes are saved together rather than one statement at a
            // time.
            connection.setAutoCommit(false);
            // Recheck the database session and allow only the listed role to continue.
            Database.require(connection, "Requester");
            // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
            try {
                // Declare departmentId to hold a whole number that can also be null.
                Integer departmentId;
                // Read the department and role from MySQL, not from user-entered fields.
                // Declare userSql to hold text. Read users records; WHERE limits the rows to the stated conditions;
                // question marks receive separately bound values.
                // Result columns in order: 0: department_id; 1: role. Object[] positions start at zero; JDBC column
                // numbers start at one.
                String userSql = "SELECT department_id, role FROM users WHERE user_id = ?";
                // Open the declared resources for this block; try-with-resources closes them in reverse order even if
                // an error occurs.
                // Declare statement with type PreparedStatement. Prepare the SQL on this connection; supplied values
                // will be bound separately to its placeholders.
                try (PreparedStatement statement = connection.prepareStatement(userSql)) {
                    // Bind the whole-number value requesterId to SQL placeholder 1.
                    statement.setInt(1, requesterId);
                    // Open the declared resources for this block; try-with-resources closes them in reverse order even if
                    // an error occurs.
                    // Declare result with type ResultSet. Execute the prepared SELECT and return its result rows for
                    // reading.
                    try (ResultSet result = statement.executeQuery()) {
                        // Check whether the result contains a row before reading its columns or generated ID.
                        if (!result.next() || !"Requester".equals(result.getString("role"))) {
                            // Stop this operation with an exception: A requester account is required.
                            throw new SQLException("A requester account is required.");
                        }
                        // Set departmentId from the following operation: read database column "department_id" as an int; JDBC
                        // returns zero for SQL NULL, which can be checked with wasNull().
                        departmentId = result.getInt("department_id");
                        // getInt returns zero for SQL NULL, so preserve the missing value explicitly.
                        // Continue with this branch when (result.wasNull()).
                        // Store no value (null) in departmentId for the remaining steps.
                        if (result.wasNull()) { departmentId = null; }
                    }
                }

                // Declare requestId to hold a whole-number value.
                int requestId;
                // MySQL supplies the creation time. Every new request starts as Pending.
                // Declare requestSql to hold text. Insert a record into requests; question marks receive separately
                // bound values.
                String requestSql = "INSERT INTO requests "
                        + "(requester_id, department_id, request_status, notes, request_type) VALUES (?, ?, 'Pending', ?, ?)";
                // Open the declared resources for this block; try-with-resources closes them in reverse order even if
                // an error occurs.
                // Declare statement with type PreparedStatement. Prepare the SQL on this connection; supplied values
                // will be bound separately to its placeholders.
                try (PreparedStatement statement = connection.prepareStatement(
                        requestSql, Statement.RETURN_GENERATED_KEYS)) {
                    // Place values in the question-mark placeholders safely.
                    // Bind the whole-number value requesterId to SQL placeholder 1.
                    statement.setInt(1, requesterId);
                    // Continue with this branch when (departmentId equals no object (null)).
                    // Bind SQL NULL to placeholder 2 with the supplied JDBC column type; NULL means no value.
                    if (departmentId == null) { statement.setNull(2, java.sql.Types.INTEGER); }
                    // Bind the whole-number value departmentId to SQL placeholder 2.
                    else { statement.setInt(2, departmentId); }
                    // Bind `notes == null ? "" : notes.trim()` as text to SQL placeholder 3.
                    statement.setString(3, notes == null ? "" : notes.trim());
                    // Bind requestType as text to SQL placeholder 4.
                    statement.setString(4, requestType);
                    // Execute the prepared database change and report how many rows were affected.
                    statement.executeUpdate();
                    // AUTO_INCREMENT creates an ID. Use it to link every item below.
                    // Open the declared resources for this block; try-with-resources closes them in reverse order even if
                    // an error occurs.
                    // Declare keys with type ResultSet. Read the generated ID values from the most recent INSERT on this
                    // statement.
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        // Check whether the result contains a row before reading its columns or generated ID.
                        if (!keys.next()) {
                            // Stop this operation with an exception: The new request ID was not returned.
                            throw new SQLException("The new request ID was not returned.");
                        }
                        // Set requestId from the following operation: read database column 1 as an int; JDBC returns zero for
                        // SQL NULL, which can be checked with wasNull().
                        requestId = keys.getInt(1);
                    }
                }

                // Declare itemSql to hold text. Insert a record into request_items; question marks receive separately
                // bound values.
                String itemSql = "INSERT INTO request_items "
                        + "(request_id, category_id, item_description, quantity, estimated_cost, catalogue_id, unit) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                // Open the declared resources for this block; try-with-resources closes them in reverse order even if
                // an error occurs.
                // Declare statement with type PreparedStatement. Prepare the SQL on this connection; supplied values
                // will be bound separately to its placeholders.
                try (PreparedStatement statement = connection.prepareStatement(itemSql)) {
                    // Reuse the statement with each item's values, one row at a time.
                    // Repeat while i is less than the number of entries in items; initialise the counter once and update
                    // it after each pass.
                    for (int i = 0; i < items.size(); i++) {
                        // Declare item with type RequestItem. Read entry i from items; list positions start at zero.
                        RequestItem item = items.get(i);
                        // Declare current to hold one row of values in SELECT or table-column order. Lock and compare the
                        // current catalogue entry with the selected snapshot so changed prices require a fresh selection.
                        Object[] current = CatalogueDAO.validate(connection, item, requestType);
                        // Bind the whole-number value requestId to SQL placeholder 1.
                        statement.setInt(1, requestId);
                        // Bind the whole-number value `Database.id(current[0])` to SQL placeholder 2.
                        statement.setInt(2, Database.id(current[0]));
                        // Bind `current[2].toString()` as text to SQL placeholder 3.
                        statement.setString(3, current[2].toString());
                        // Bind the whole-number value `item.getQuantity()` to SQL placeholder 4.
                        statement.setInt(4, item.getQuantity());
                        // estimated_cost stores one unit's price, not the line total.
                        // Bind `(java.math.BigDecimal)current[4]` as an exact decimal amount to SQL placeholder 5.
                        statement.setBigDecimal(5, (java.math.BigDecimal) current[4]);
                        // Bind the whole-number value `item.getCatalogueId()` to SQL placeholder 6.
                        statement.setInt(6, item.getCatalogueId());
                        // Bind `current[3].toString()` as text to SQL placeholder 7.
                        statement.setString(7, current[3].toString());
                        // Execute the prepared database change and report how many rows were affected.
                        statement.executeUpdate();
                    }
                }

                // A short unique storage name avoids overwrites and filesystem filename limits.
                // Declare folder with type java.nio.file.Path. Read entry
                // System.getProperty("procurement.attachments.dir", System.getProperty("user.home") +
                // "/.it-procurement/attachments") from java.nio.file.Paths; list positions start at zero.
                java.nio.file.Path folder = java.nio.file.Paths.get(System.getProperty("procurement.attachments.dir",
                        System.getProperty("user.home") + "/.it-procurement/attachments"));
                // Process each entry in attachments in turn, referring to the current entry as file.
                for (java.io.File file : attachments) {
                    // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                    try {
                        // Create the required folder and any missing parent folders before writing files there.
                        java.nio.file.Files.createDirectories(folder);
                        // Declare extension to hold text. Its initial value is
                        // `file.getName().substring(file.getName().lastIndexOf('.'))`.
                        String extension = file.getName().substring(file.getName().lastIndexOf('.'));
                        // Declare target with type java.nio.file.Path. Its initial value is
                        // `folder.resolve(java.util.UUID.randomUUID().toString() + extension)`.
                        java.nio.file.Path target = folder.resolve(java.util.UUID.randomUUID().toString() + extension);
                        // Validate target.toAbsolutePath().toString() as "Stored file path" with a maximum of 500 characters;
                        // a value is required.
                        Database.text(target.toAbsolutePath().toString(),"Stored file path",500,true);
                        // Copy the selected source document to its unique managed storage path without replacing the original.
                        java.nio.file.Files.copy(file.toPath(),target);
                        // Append target to copied.
                        copied.add(target);
                        // Check the copied size too, in case the original changed while being copied.
                        // Continue with this branch when (java.nio.file.Files.size(target) is greater than 10 * 1024 * 1024).
                        // Stop this operation with an exception: The copied attachment exceeds 10 MB.
                        if (java.nio.file.Files.size(target) > 10 * 1024 * 1024) { throw new IllegalArgumentException("The copied attachment exceeds 10 MB."); }
                        // Insert a record into attachments; question marks receive separately bound values.
                        Database.update(connection,"INSERT INTO attachments(request_id,file_name,file_path,uploaded_by) VALUES (?,?,?,?)",
                                requestId,file.getName(),target.toAbsolutePath().toString(),requesterId);
                    // Handle java.io.IOException ex from the preceding try block so the failure follows the recovery steps
                    // below.
                    // Stop this operation with an exception: Could not copy an attachment. The request was not saved.
                    } catch (java.io.IOException ex) { throw new SQLException("Could not copy an attachment. The request was not saved.",ex); }
                }
                // Add an audit entry identifying the current user, action and affected record in this transaction.
                Database.audit(connection,"Submitted " + requestType + " request","requests",requestId);
                // Save the notification message for the specified account in the current transaction.
                Database.notify(connection,requesterId,"Request #" + requestId + " submitted successfully.");
                // Process each entry in Database.rows(connection, "SELECT user_id FROM users WHERE role='Purchaser'")
                // in turn, referring to the current entry as purchaser.
                // Result columns in order: 0: user_id. Object[] positions start at zero; JDBC column numbers start at
                // one.
                for (Object[] purchaser : Database.rows(connection,"SELECT user_id FROM users WHERE role='Purchaser'")) {
                    // Save the notification message for the specified account in the current transaction.
                    Database.notify(connection,Database.id(purchaser[0]),"New " + requestType + " request #" + requestId + " needs quotations.");
                }

                // Save everything together only after every INSERT succeeds.
                // Commit the transaction, making all its successful changes permanent together.
                connection.commit();
                // Return requestId to the caller.
                return requestId;
            // Handle SQLException | RuntimeException ex from the preceding try block so the failure follows the
            // recovery steps below.
            } catch (SQLException | RuntimeException ex) {
                // Undo this transaction if any step fails, avoiding incomplete requests.
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                try {
                    // Roll back the transaction so its unfinished database changes are not kept.
                    connection.rollback();
                // Handle SQLException rollbackError from the preceding try block so the failure follows the recovery
                // steps below.
                } catch (SQLException rollbackError) {
                    // Keep the original error while recording a rollback failure too.
                    // Attach the secondary cleanup error to the original exception so neither failure is lost.
                    ex.addSuppressed(rollbackError);
                }
                // Filesystem copies are outside MySQL, so clean them up explicitly on failure.
                // Process each entry in copied in turn, referring to the current entry as path.
                for (java.nio.file.Path path : copied) {
                    // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                    // Delete the specified temporary or copied file during cleanup; deleteIfExists also accepts an already
                    // absent file.
                    try { java.nio.file.Files.deleteIfExists(path); }
                    // Handle java.io.IOException cleanupError from the preceding try block so the failure follows the
                    // recovery steps below.
                    // Attach the secondary cleanup error to the original exception so neither failure is lost.
                    catch (java.io.IOException cleanupError) { ex.addSuppressed(cleanupError); }
                }
                // Pass the caught exception back to the caller so the original failure is not treated as success.
                throw ex;
            }
        }
    }
}
