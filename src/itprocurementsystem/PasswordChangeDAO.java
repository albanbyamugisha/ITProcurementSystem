package itprocurementsystem;

import java.sql.Connection;
import java.sql.SQLException;

// Only a verified temporary-password session may replace that temporary password.
public class PasswordChangeDAO {
    public void change(String password, String confirmation) throws SQLException {
        if (password == null || password.length() < 8 || password.length() > 128) {
            throw new IllegalArgumentException("Choose a password of 8 to 128 characters.");
        }
        if (!password.equals(confirmation)) { throw new IllegalArgumentException("The passwords do not match."); }
        if (Session.getUserId() <= 0) { throw new IllegalArgumentException("Please log in first."); }
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                // Lock the account so another reset cannot race this password change.
                Object[] row = Database.one(c,"SELECT username,full_name,role,password_hash,session_version,IF(must_change_password,1,0) FROM users WHERE user_id=? FOR UPDATE",Session.getUserId());
                if (Database.id(row[4]) != Session.getVersion() || Database.id(row[5]) != 1) {
                    throw new IllegalArgumentException("This password-change session has expired. Please log in again.");
                }
                String hash = PasswordUtil.hashPassword(password);
                if (hash.equals(row[3])) { throw new IllegalArgumentException("Choose a different password from the temporary one."); }
                int nextVersion = Database.id(row[4]) + 1;
                Database.update(c,"UPDATE users SET password_hash=?,must_change_password=FALSE,session_version=? WHERE user_id=?",hash,nextVersion,Session.getUserId());
                Database.audit(c,"Changed temporary password","users",Session.getUserId());
                c.commit();
                // Give this login full access only after the database save succeeds.
                // Increasing the version also expires other temporary-password logins.
                Session.start(Session.getUserId(),row[0].toString(),row[1].toString(),row[2].toString(),nextVersion);
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }
}
