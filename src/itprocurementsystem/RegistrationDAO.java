// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// JDBC provides database connections, statements and query results.
// Import Connection for a JDBC connection to the database.
import java.sql.Connection;
// Import PreparedStatement for SQL statements with separate value placeholders.
import java.sql.PreparedStatement;
// Import ResultSet for the rows returned by a database query.
import java.sql.ResultSet;
// Import SQLException for database errors that can be caught or passed to a caller.
import java.sql.SQLException;
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;
// Import Locale for consistent text conversion independent of the computer language.
import java.util.Locale;

// This class handles registration queries instead of putting SQL in the form.
// Define RegistrationDAO as a class that groups its related data and methods.
public class RegistrationDAO {
    // Each Department keeps a database ID with the name shown in the dropdown.
    // Define Department as a class that groups its related data and methods.
    public static class Department {
        // Only this class accesses this field directly. Declare id to hold a whole-number value. Java
        // initially uses zero.
        private int id;
        // Only this class accesses this field directly. Declare name to hold text. Java initially uses null
        // for this object reference.
        private String name;
        // Construct this object and initialise its fields or controls from the supplied starting values; a
        // constructor has no return type.
        // Copy the id parameter into this object's id field.
        // Copy the name parameter into this object's name field.
        public Department(int id, String name) { this.id = id; this.name = name; }
        // Return the stored id value without exposing a method that directly changes it.
        // Return id to the caller.
        public int getId() { return id; }
        // Return the stored name value without exposing a method that directly changes it.
        // Return name to the caller.
        public String getName() { return name; }
    }

    // Return existing departments in alphabetical order for the registration form.
    // Read department IDs and names in alphabetical order for account registration.
    public ArrayList<Department> getDepartments() throws SQLException {
        // Declare departments with type ArrayList<Department>. Create an initially empty resizable list.
        ArrayList<Department> departments = new ArrayList<Department>();
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection();
                // Declare s with type PreparedStatement. Read departments records and ORDER BY fixes their display
                // order.
                // Result columns in order: 0: department_id; 1: department_name. Object[] positions start at zero;
                // JDBC column numbers start at one.
                PreparedStatement s = c.prepareStatement("SELECT department_id,department_name FROM departments ORDER BY department_name,department_id");
                // Declare r with type ResultSet. Execute the prepared SELECT and return its result rows for reading.
                ResultSet r = s.executeQuery()) {
            // Move to the next database result row and repeat while another row is available.
            // Append `new Department(r.getInt(1), r.getString(2))` to departments.
            while (r.next()) { departments.add(new Department(r.getInt(1), r.getString(2))); }
        }
        // Return departments to the caller.
        return departments;
    }

    // Validate here too so other callers cannot skip the form's input checks.
    // Validate account details and create a Requester account; public registration cannot choose a staff
    // role.
    public void register(String fullName, String username, String email, int departmentId,
            String password, String confirmation) throws SQLException {
        // Older callers must supply the account type using the overload below.
        // Stop this operation with an exception: An account type is required. Use the registration method with
        // account type and organisation name.
        throw new IllegalArgumentException("An account type is required. Use the registration method with account type and organisation name.");
    }

    // Account type describes the customer; role still controls staff permissions.
    // Validate account details and create a Requester account; public registration cannot choose a staff
    // role.
    public void register(String fullName, String username, String email, int departmentId,
            String password, String confirmation, String accountType, String organisationName)
            throws SQLException {
        // Validate account details and create a Requester account; public registration cannot choose a staff
        // role.
        register(fullName, username, email, departmentId, password, confirmation, accountType, organisationName, null);
    }

    // Gender is optional; for an organisation it describes the contact person.
    // Validate account details and create a Requester account; public registration cannot choose a staff
    // role.
    public void register(String fullName, String username, String email, int departmentId,
            String password, String confirmation, String accountType, String organisationName,
            String gender) throws SQLException {
        // Continue with this branch when (gender is not equal to no object (null) and not
        // gender.equals("Female") and not gender.equals("Male") and not gender.equals("Other") and not
        // gender.equals("Prefer not to say")).
        if (gender != null && !gender.equals("Female") && !gender.equals("Male")
                && !gender.equals("Other") && !gender.equals("Prefer not to say")) {
            // Stop this operation with an exception: Choose a listed gender or leave it blank.
            throw new IllegalArgumentException("Choose a listed gender or leave it blank.");
        }
        // Continue with this branch when (!"Individual".equals(accountType) and
        // !"Organisation".equals(accountType)).
        if (!"Individual".equals(accountType) && !"Organisation".equals(accountType)) {
            // Stop this operation with an exception: Select an account type.
            throw new IllegalArgumentException("Select an account type.");
        }
        // Continue with this branch when ("Organisation".equals(accountType) and (organisationName equals no
        // object (null) or organisationName.trim().isEmpty() or organisationName.trim().length() is greater
        // than 150)).
        if ("Organisation".equals(accountType) && (organisationName == null
                || organisationName.trim().isEmpty() || organisationName.trim().length() > 150)) {
            // Stop this operation with an exception: Enter an organisation name of 1 to 150 characters.
            throw new IllegalArgumentException("Enter an organisation name of 1 to 150 characters.");
        }
        // Individuals never inherit a department or organisation from stale form values.
        // Continue with this branch when ("Individual".equals(accountType)).
        // Store 0 in departmentId for the remaining steps.
        // Store no value (null) in organisationName for the remaining steps.
        if ("Individual".equals(accountType)) { departmentId = 0; organisationName = null; }
        // Continue with this branch when (fullName equals no object (null) or fullName.trim().isEmpty() or
        // fullName.trim().length() is greater than 100).
        if (fullName == null || fullName.trim().isEmpty() || fullName.trim().length() > 100) {
            // Stop this operation with an exception: Enter a full name of 1 to 100 characters.
            throw new IllegalArgumentException("Enter a full name of 1 to 100 characters.");
        }
        // Continue with this branch when (username equals no object (null) or not
        // username.trim().matches("[A-Za-z0-9_]{3,50}")).
        if (username == null || !username.trim().matches("[A-Za-z0-9_]{3,50}")) {
            // Stop this operation with an exception: Username must contain 3 to 50 letters, numbers or
            // underscores.
            throw new IllegalArgumentException("Username must contain 3 to 50 letters, numbers or underscores.");
        }
        // A simple email format check catches common typing mistakes, not ownership.
        // Continue with this branch when (email equals no object (null) or email.trim().length() is greater
        // than 100 or not email.trim().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")).
        if (email == null || email.trim().length() > 100
                || !email.trim().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            // Stop this operation with an exception: Enter a valid email address.
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        // Continue with this branch when (departmentId is less than 0).
        // Stop this operation with an exception: Invalid department.
        if (departmentId < 0) { throw new IllegalArgumentException("Invalid department."); }
        // Continue with this branch when (password equals no object (null) or password.length() is less than 8
        // or password.length() is greater than 128).
        if (password == null || password.length() < 8 || password.length() > 128) {
            // Stop this operation with an exception: Use a password of 8 to 128 characters.
            throw new IllegalArgumentException("Use a password of 8 to 128 characters.");
        }
        // Continue with this branch when (not password.equals(confirmation)).
        if (!password.equals(confirmation)) {
            // Stop this operation with an exception: The passwords do not match.
            throw new IllegalArgumentException("The passwords do not match.");
        }
        // Trim names and normalize email. Never trim the password.
        // Set username from the following operation: remove spaces and other trim characters from both ends of
        // username without changing text in the middle.
        username = username.trim();
        // Store `email.trim().toLowerCase(Locale.ROOT)` in email for the remaining steps.
        email = email.trim().toLowerCase(Locale.ROOT);
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Check the saved username and normalised email separately so each duplicate receives a clear error.
            checkDuplicates(c, username, email);
            // Open the declared resources for this block; try-with-resources closes them in reverse order even if
            // an error occurs.
            // Declare s with type PreparedStatement. Insert a record into users; question marks receive separately
            // bound values.
            try (PreparedStatement s = c.prepareStatement(
                    "INSERT INTO users (department_id,username,password_hash,full_name,email,role,account_type,organisation_name,gender) VALUES (?,?,?,?,?,'Requester',?,?,?)")) {
                // SQL NULL means no department, rather than an invalid department ID of zero.
                // Continue with this branch when (departmentId equals 0).
                // Bind SQL NULL to placeholder 1 with the supplied JDBC column type; NULL means no value.
                if (departmentId == 0) { s.setNull(1, java.sql.Types.INTEGER); }
                // Bind the whole-number value departmentId to SQL placeholder 1.
                else { s.setInt(1, departmentId); }
                // Bind username as text to SQL placeholder 2.
                s.setString(2, username);
                // Bind `PasswordUtil.hashPassword(password)` as text to SQL placeholder 3.
                s.setString(3, PasswordUtil.hashPassword(password));
                // Bind `fullName.trim()` as text to SQL placeholder 4.
                s.setString(4, fullName.trim());
                // Bind email as text to SQL placeholder 5.
                s.setString(5, email);
                // Bind accountType as text to SQL placeholder 6.
                s.setString(6, accountType);
                // Bind `organisationName == null ? null : organisationName.trim()` as text to SQL placeholder 7.
                s.setString(7, organisationName == null ? null : organisationName.trim());
                // Bind gender as text to SQL placeholder 8.
                s.setString(8, gender);
                // The role is fixed in SQL: registration cannot create privileged accounts.
                // Execute the prepared database change and report how many rows were affected.
                s.executeUpdate();
            // Handle SQLException ex from the preceding try block so the failure follows the recovery steps below.
            } catch (SQLException ex) {
                // MySQL error 1062 means a UNIQUE constraint rejected a duplicate.
                // Recheck because another registration may have finished since the first check.
                // Continue with this branch when (ex.getErrorCode() equals 1062).
                if (ex.getErrorCode() == 1062) {
                    // Check the saved username and normalised email separately so each duplicate receives a clear error.
                    checkDuplicates(c, username, email);
                    // Stop this operation with an exception: That username or email is already registered.
                    throw new IllegalArgumentException("That username or email is already registered.");
                }
                // Pass the caught exception back to the caller so the original failure is not treated as success.
                throw ex;
            }
        }
    }

    // Give a specific friendly message before attempting the INSERT.
    // Check the saved username and normalised email separately so each duplicate receives a clear error.
    private void checkDuplicates(Connection c, String username, String email) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare s with type PreparedStatement. Read users records; WHERE limits the rows to the stated
        // conditions; question marks receive separately bound values.
        // Result columns in order: 0: user_id. Object[] positions start at zero; JDBC column numbers start at
        // one.
        try (PreparedStatement s = c.prepareStatement("SELECT user_id FROM users WHERE username=?")) {
            // Bind username as text to SQL placeholder 1.
            s.setString(1, username);
            // Open the declared resources for this block; try-with-resources closes them in reverse order even if
            // an error occurs.
            // Declare r with type ResultSet. Execute the prepared SELECT and return its result rows for reading.
            try (ResultSet r = s.executeQuery()) {
                // Check whether the result contains a row before reading its columns or generated ID.
                // Stop this operation with an exception: That username is already taken. Please choose another.
                if (r.next()) { throw new IllegalArgumentException("That username is already taken. Please choose another."); }
            }
        }
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare s with type PreparedStatement. Read users records; WHERE limits the rows to the stated
        // conditions; question marks receive separately bound values.
        // Result columns in order: 0: user_id. Object[] positions start at zero; JDBC column numbers start at
        // one.
        try (PreparedStatement s = c.prepareStatement("SELECT user_id FROM users WHERE LOWER(TRIM(email))=?")) {
            // Bind email as text to SQL placeholder 1.
            s.setString(1, email);
            // Open the declared resources for this block; try-with-resources closes them in reverse order even if
            // an error occurs.
            // Declare r with type ResultSet. Execute the prepared SELECT and return its result rows for reading.
            try (ResultSet r = s.executeQuery()) {
                // Check whether the result contains a row before reading its columns or generated ID.
                // Stop this operation with an exception: That email address is already registered. Please log in or
                // use another email.
                if (r.next()) { throw new IllegalArgumentException("That email address is already registered. Please log in or use another email."); }
            }
        }
    }
}
