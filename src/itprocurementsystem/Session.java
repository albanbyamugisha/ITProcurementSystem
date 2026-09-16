package itprocurementsystem;

// A session remembers the user who is currently signed in to this desktop application.
// These values stay in memory only; closing the application clears them.
// We never keep the user's password here.
public class Session {

    // private means other classes cannot change these fields directly.
    // static means all screens read the same signed-in user's details.
    // A user ID of 0 means that nobody is currently signed in.
    private static int userId = 0;

    // These fields start empty because no user has logged in yet.
    private static String username = "";
    private static String fullName = "";
    private static String role = "";

    // Call this only after the username and password have been verified.
    // Each parameter contains a value read from the matching database record.
    public static void start(int id, String loginName, String name, String userRole) {
        userId = id;
        username = loginName;
        fullName = name;
        role = userRole;
    }

    // A getter returns a value without allowing another class to change the field directly.
    // Other screens will use this ID when saving the user's requests or decisions.
    public static int getUserId() {
        return userId;
    }

    // Return the username that was stored in the database.
    public static String getUsername() {
        return username;
    }

    // Return the person's full name so we can display a welcome message.
    public static String getFullName() {
        return fullName;
    }

    // Return Requester, Manager or Purchaser so we can choose the correct screen later.
    public static String getRole() {
        return role;
    }

    // Clear all remembered details when logging out or starting a new login attempt.
    public static void clear() {
        userId = 0;
        username = "";
        fullName = "";
        role = "";
    }
}
