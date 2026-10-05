// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// A session remembers the user who is currently signed in to this desktop application.
// These values stay in memory only; closing the application clears them.
// The session never keeps the user's password here.
// Define Session as a class that groups its related data and methods.
public class Session {

    // private means other classes cannot change these fields directly.
    // static means all screens read the same signed-in user's details.
    // A user ID of 0 means that nobody is currently signed in.
    // Only this class accesses this field directly. Declare userId to hold a whole-number value. Its
    // initial value is 0. This field is shared by all instances of the class.
    private static int userId = 0;

    // These fields start empty because no user has logged in yet.
    // Only this class accesses this field directly. Declare username to hold text. Its initial value is
    // empty text. This field is shared by all instances of the class.
    private static String username = "";
    // Only this class accesses this field directly. Declare fullName to hold text. Its initial value is
    // empty text. This field is shared by all instances of the class.
    private static String fullName = "";
    // Only this class accesses this field directly. Declare role to hold text. Its initial value is empty
    // text. This field is shared by all instances of the class.
    private static String role = "";
    // A reset increases this number in MySQL, making older sessions expire.
    // Only this class accesses this field directly. Declare version to hold a whole-number value. Its
    // initial value is 0. This field is shared by all instances of the class.
    private static int version = 0;
    // A temporary-password login may only open the mandatory password dialog.
    // Only this class accesses this field directly. Declare passwordChangeRequired to hold a true-or-false
    // flag. Its initial value is false. This field is shared by all instances of the class.
    private static boolean passwordChangeRequired = false;
    // Return whether this login must replace its temporary password before accessing the main application.
    // Return passwordChangeRequired to the caller.
    public static boolean mustChangePassword() { return passwordChangeRequired; }
    // Return the session version captured at login so database changes can invalidate older logins.
    // Return version to the caller.
    public static int getVersion() { return version; }

    // Call this only after the username and password have been verified.
    // Each parameter contains a value read from the matching database record.
    // Copy verified account details into the shared session; overloads supply defaults for omitted values.
    public static void start(int id, String loginName, String name, String userRole) {
        // Copy verified account details into the shared session; overloads supply defaults for omitted values.
        start(id, loginName, name, userRole, 0);
    }

    // Login reads this version together with the verified account details.
    // Copy verified account details into the shared session; overloads supply defaults for omitted values.
    public static void start(int id, String loginName, String name, String userRole, int sessionVersion) {
        // Copy verified account details into the shared session; overloads supply defaults for omitted values.
        start(id, loginName, name, userRole, sessionVersion, false);
    }

    // Copy verified account details into the shared session; overloads supply defaults for omitted values.
    public static void start(int id, String loginName, String name, String userRole,
            int sessionVersion, boolean mustChange) {
        // Remember whether this verified login is restricted to choosing a new password until the session is replaced or cleared.
        passwordChangeRequired = mustChange;
        // Remember the database session version used for later expiry checks until the session is replaced or cleared.
        version = sessionVersion;
        // Remember the verified database account ID used for ownership checks until the session is replaced or cleared.
        userId = id;
        // Remember the verified login name until the session is replaced or cleared.
        username = loginName;
        // Remember the verified name displayed in the welcome message until the session is replaced or cleared.
        fullName = name;
        // Remember the verified permission role used to choose navigation options until the session is replaced or cleared.
        role = userRole;
    }

    // A getter returns a value without allowing another class to change the field directly.
    // Other screens will use this ID when saving the user's requests or decisions.
    // Return the stored user id value without exposing a method that directly changes it.
    public static int getUserId() {
        // Return userId to the caller.
        return userId;
    }

    // Return the username that was stored in the database.
    // Return the stored username value without exposing a method that directly changes it.
    public static String getUsername() {
        // Return username to the caller.
        return username;
    }

    // Return the person's full name so the application can display a welcome message.
    // Return the stored full name value without exposing a method that directly changes it.
    public static String getFullName() {
        // Return fullName to the caller.
        return fullName;
    }

    // Return Requester, Manager, Purchaser or Admin so navigation can show the appropriate screens.
    // Return the stored role value without exposing a method that directly changes it.
    public static String getRole() {
        // Return role to the caller.
        return role;
    }

    // Clear all remembered details when logging out or starting a new login attempt.
    // Reset the remembered account details so the application no longer considers anyone signed in.
    public static void clear() {
        // Reset userId to its signed-out default.
        userId = 0;
        // Reset version to its signed-out default.
        version = 0;
        // Reset passwordChangeRequired to its signed-out default.
        passwordChangeRequired = false;
        // Remove the previous account login name from memory.
        username = "";
        // Remove the previous account display name from memory.
        fullName = "";
        // Remove the previous permission role so it cannot be reused after logout.
        role = "";
    }
}
