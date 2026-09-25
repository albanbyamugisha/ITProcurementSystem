package itprocurementsystem;

// JDBC provides database connections, statements and query results.
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

// This class handles registration queries instead of putting SQL in the form.
public class RegistrationDAO {
    // Each Department keeps a database ID with the name shown in the dropdown.
    public static class Department {
        private int id;
        private String name;
        public Department(int id, String name) { this.id = id; this.name = name; }
        public int getId() { return id; }
        public String getName() { return name; }
    }

    // Return existing departments in alphabetical order for the registration form.
    public ArrayList<Department> getDepartments() throws SQLException {
        ArrayList<Department> departments = new ArrayList<Department>();
        try (Connection c = DBConnection.getConnection();
                PreparedStatement s = c.prepareStatement("SELECT department_id,department_name FROM departments ORDER BY department_name,department_id");
                ResultSet r = s.executeQuery()) {
            while (r.next()) { departments.add(new Department(r.getInt(1), r.getString(2))); }
        }
        return departments;
    }

    // Validate here too so other callers cannot skip the form's input checks.
    public void register(String fullName, String username, String email, int departmentId,
            String password, String confirmation) throws SQLException {
        if (fullName == null || fullName.trim().isEmpty() || fullName.trim().length() > 100) {
            throw new IllegalArgumentException("Enter a full name of 1 to 100 characters.");
        }
        if (username == null || !username.trim().matches("[A-Za-z0-9_]{3,50}")) {
            throw new IllegalArgumentException("Username must contain 3 to 50 letters, numbers or underscores.");
        }
        // A simple email format check catches common typing mistakes, not ownership.
        if (email == null || email.trim().length() > 100
                || !email.trim().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (departmentId <= 0) { throw new IllegalArgumentException("Select a department."); }
        if (password == null || password.length() < 8 || password.length() > 128) {
            throw new IllegalArgumentException("Use a password of 8 to 128 characters.");
        }
        if (!password.equals(confirmation)) {
            throw new IllegalArgumentException("The passwords do not match.");
        }
        // Trim names and normalize email. Never trim the password.
        username = username.trim();
        email = email.trim().toLowerCase(Locale.ROOT);
        try (Connection c = DBConnection.getConnection()) {
            checkDuplicates(c, username, email);
            try (PreparedStatement s = c.prepareStatement(
                    "INSERT INTO users (department_id,username,password_hash,full_name,email,role) VALUES (?,?,?,?,?,'Requester')")) {
                s.setInt(1, departmentId);
                s.setString(2, username);
                s.setString(3, PasswordUtil.hashPassword(password));
                s.setString(4, fullName.trim());
                s.setString(5, email);
                // The role is fixed in SQL: registration cannot create privileged accounts.
                s.executeUpdate();
            } catch (SQLException ex) {
                // MySQL error 1062 means a UNIQUE constraint rejected a duplicate.
                // Recheck because another registration may have finished since our first check.
                if (ex.getErrorCode() == 1062) {
                    checkDuplicates(c, username, email);
                    throw new IllegalArgumentException("That username or email is already registered.");
                }
                throw ex;
            }
        }
    }

    // Give a specific friendly message before attempting the INSERT.
    private void checkDuplicates(Connection c, String username, String email) throws SQLException {
        try (PreparedStatement s = c.prepareStatement("SELECT user_id FROM users WHERE username=?")) {
            s.setString(1, username);
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) { throw new IllegalArgumentException("That username is already taken. Please choose another."); }
            }
        }
        try (PreparedStatement s = c.prepareStatement("SELECT user_id FROM users WHERE LOWER(TRIM(email))=?")) {
            s.setString(1, email);
            try (ResultSet r = s.executeQuery()) {
                if (r.next()) { throw new IllegalArgumentException("That email address is already registered. Please log in or use another email."); }
            }
        }
    }
}
