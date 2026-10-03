package itprocurementsystem;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

// This username/full-name check is only a classroom recovery demonstration.
// Anyone who knows both details could reset the account; it is not identity proof.
// The original password cannot be read from its hash, so we create a replacement.
public class PasswordResetDAO {
    public String reset(String username, String fullName) throws SQLException {
        username = Database.text(username, "Username", 50, true);
        fullName = Database.text(fullName, "Full name", 100, true);
        // SecureRandom chooses unpredictable characters without storing a default password.
        String letters = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) { password.append(letters.charAt(random.nextInt(letters.length()))); }
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                // Lock the account until its new hash and audit record are saved together.
                ArrayList<Object[]> matches = Database.rows(c,
                        "SELECT user_id,full_name FROM users WHERE username=? FOR UPDATE", username);
                if (matches.isEmpty() || !fullName.equalsIgnoreCase(matches.get(0)[1].toString().trim())) {
                    throw new IllegalArgumentException("The username and full name do not match an account.");
                }
                int user = Database.id(matches.get(0)[0]);
                Database.update(c, "UPDATE users SET password_hash=?,session_version=session_version+1 WHERE user_id=?",
                        PasswordUtil.hashPassword(password.toString()), user);
                // Never put the replacement password or its hash in notifications or logs.
                Database.update(c, "INSERT INTO audit_logs(user_id,action,table_affected,record_id) VALUES (?,'Classroom password reset','users',?)", user, user);
                Database.notify(c, user, "Your password was reset using the classroom recovery form.");
                c.commit();
                return password.toString();
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }
}
