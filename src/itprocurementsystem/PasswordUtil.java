// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// These classes help the application turn password text into a hash.
// Import StandardCharsets for named character encodings such as UTF-8.
import java.nio.charset.StandardCharsets;
// Import MessageDigest for the algorithm used to calculate a password hash.
import java.security.MessageDigest;
// Import NoSuchAlgorithmException for the error raised if a requested hashing algorithm is
// unavailable.
import java.security.NoSuchAlgorithmException;

// Both account setup and login must use the same hashing method.
// Define PasswordUtil as a class that groups its related data and methods.
public class PasswordUtil {

    // A hash is a fixed-length result calculated from the password.
    // The application compares hashes during login instead of storing the original password.
    // SHA-256 follows the assignment brief; a deployed system needs salted password hashing.
    // Calculate the SHA-256 password hash and return it as hexadecimal text for storage or comparison.
    public static String hashPassword(String password) {
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Choose SHA-256 and convert the password into bytes using UTF-8.
            // Declare digest with type MessageDigest. Its initial value is `MessageDigest.getInstance("SHA-256")`.
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Declare hashBytes to hold an array of bytes. Its initial value is
            // `digest.digest(password.getBytes(StandardCharsets.UTF_8))`.
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convert each byte into two hexadecimal characters so the application can store the hash as text.
            // Declare hashText with type StringBuilder. Create a mutable text buffer for building a result piece
            // by piece.
            StringBuilder hashText = new StringBuilder();
            // Process each entry in hashBytes in turn, referring to the current entry as value.
            for (byte value : hashBytes) {
                // Append `String.format("%02x", value & 255)` to hashText.
                // Mask with 255 to treat the signed byte as an unsigned value from 0 to 255.
                // %02x writes exactly two lowercase hexadecimal digits, including a leading zero when needed.
                hashText.append(String.format("%02x", value & 255));
            }

            // Return the complete hash for saving or comparing.
            // Convert hashText to its text representation. Return the resulting value to the caller.
            return hashText.toString();
        // Handle NoSuchAlgorithmException ex from the preceding try block so the failure follows the recovery
        // steps below.
        } catch (NoSuchAlgorithmException ex) {
            // Java normally provides SHA-256. Stop if this installation cannot provide it.
            // Stop this operation with an exception: Password hashing is not available.
            throw new IllegalStateException("Password hashing is not available.", ex);
        }
    }
}
