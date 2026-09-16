package itprocurementsystem;

// These classes help us turn password text into a hash.
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Both account setup and login must use the same hashing method.
public class PasswordUtil {

    // A hash is a fixed-length result calculated from the password.
    // We compare hashes during login instead of storing the original password.
    // SHA-256 follows our assignment brief; a deployed system needs salted password hashing.
    public static String hashPassword(String password) {
        try {
            // Choose SHA-256 and convert the password into bytes using UTF-8.
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convert each byte into two hexadecimal characters so we can store the hash as text.
            StringBuilder hashText = new StringBuilder();
            for (byte value : hashBytes) {
                hashText.append(String.format("%02x", value & 255));
            }

            // Return the complete hash for saving or comparing.
            return hashText.toString();
        } catch (NoSuchAlgorithmException ex) {
            // Java normally provides SHA-256. Stop if this installation cannot provide it.
            throw new IllegalStateException("Password hashing is not available.", ex);
        }
    }
}
