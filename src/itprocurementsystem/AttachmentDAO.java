// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Import File for a file or folder location.
import java.io.File;
// Make the public types in java.sql available by short names; this does not create objects.
import java.sql.*;
// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;

// Attachments are copied into application storage; the database keeps their locations.
// Define AttachmentDAO as a class that groups its related data and methods.
public class AttachmentDAO {
    // Keep supporting documents small and restrict them to ordinary document/image types.
    // Check that the attachment is a readable ordinary file with an allowed extension, filename length and
    // size.
    public static void validate(File file) {
        // Continue with this branch when (file equals no object (null) or not file.isFile() or not
        // file.canRead()).
        // Stop this operation with an exception: Choose a readable file.
        if (file == null || !file.isFile() || !file.canRead()) { throw new IllegalArgumentException("Choose a readable file."); }
        // Declare name to hold text. Its initial value is `file.getName().toLowerCase(java.util.Locale.ROOT)`.
        String name = file.getName().toLowerCase(java.util.Locale.ROOT);
        // Continue with this branch when (not name.matches(".+\\.(pdf|png|jpg|jpeg|txt|docx|xlsx)")).
        // Stop this operation with an exception: Choose a PDF, image, text, DOCX or XLSX document.
        if (!name.matches(".+\\.(pdf|png|jpg|jpeg|txt|docx|xlsx)")) { throw new IllegalArgumentException("Choose a PDF, image, text, DOCX or XLSX document."); }
        // Continue with this branch when (file.length() is greater than 10 * 1024 * 1024).
        // Stop this operation with an exception: Each attachment must be 10 MB or smaller.
        if (file.length() > 10 * 1024 * 1024) { throw new IllegalArgumentException("Each attachment must be 10 MB or smaller."); }
        // Validate file.getName() as "File name" with a maximum of 255 characters; a value is required.
        Database.text(file.getName(),"File name",255,true);
    }
    // List attachment names and paths only after checking the request owner or authorised staff role.
    public ArrayList<Object[]> forRequest(int request) throws SQLException {
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare c with type Connection. Open a JDBC connection; the test URL property can select an isolated
        // database instead of the normal one.
        try (Connection c = DBConnection.getConnection()) {
            // Recheck the database session and allow only the listed roles to continue.
            Database.require(c,"Requester","Admin","Manager","Purchaser","Admin");
            // Declare owner to hold one row of values in SELECT or table-column order. Read requests records with
            // matching information from related tables; WHERE limits the rows to the stated conditions; question
            // marks receive separately bound values.
            // Result columns in order: 0: r.requester_id; 1: u.role. Object[] positions start at zero; JDBC column
            // numbers start at one.
            Object[] owner = Database.one(c,"SELECT r.requester_id,u.role FROM requests r JOIN users u ON u.user_id=? WHERE r.request_id=?",Session.getUserId(),request);
            // Check the session identity before continuing so a missing or different account follows this branch.
            // Stop this operation with an exception: This request belongs to another customer.
            if ("Requester".equals(owner[1]) && Database.id(owner[0]) != Session.getUserId()) { throw new IllegalArgumentException("This request belongs to another customer."); }
            // Read attachments records; WHERE limits the rows to the stated conditions and ORDER BY fixes their
            // display order; question marks receive separately bound values. Return the resulting value to the
            // caller.
            // Result columns in order: 0: file_name; 1: file_path. Object[] positions start at zero; JDBC column
            // numbers start at one.
            return Database.rows(c,"SELECT file_name,file_path FROM attachments WHERE request_id=? ORDER BY attachment_id",request);
        }
    }
    // A normal Swing popup supplies an attachment action without requiring another designed form.
    // Define the RequestChoice contract; callers provide an implementation of its declared method.
    // Supply the currently selected request ID to the attachment menu without storing an outdated
    // selection.
    public interface RequestChoice { int requestId(); }
    // Attach a right-click menu that opens supporting documents for the currently selected request.
    public static void addMenu(javax.swing.JComponent component, final RequestChoice choice) {
        // Declare menu with type javax.swing.JPopupMenu. Create a JPopupMenu object using the supplied
        // constructor values.
        javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu();
        // Declare open with type javax.swing.JMenuItem. Create a JMenuItem object using the supplied
        // constructor values.
        javax.swing.JMenuItem open = new javax.swing.JMenuItem("View request attachments");
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        open.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Offer the authorised request's attachments and open the chosen file with the operating system.
                try { showFiles(component,choice.requestId()); }
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                catch (Exception ex) { FormSupport.error(component,ex); }
            }
        });
        // Append open to menu.
        // Attach the supplied right-click menu to component.
        menu.add(open); component.setComponentPopupMenu(menu);
        // Show this help text when the pointer rests over component.
        component.setToolTipText("Select a request, then right-click to view its attachments.");
    }
    // Open only the document the user explicitly selected from this authorised request.
    // Offer the authorised request's attachments and open the chosen file with the operating system.
    private static void showFiles(java.awt.Component parent, int request) throws Exception {
        // Declare rows with type ArrayList<Object[]>. List attachment names and paths only after checking the
        // request owner or authorised staff role.
        ArrayList<Object[]> rows = new AttachmentDAO().forRequest(request);
        // Continue with this branch when (rows is empty).
        // Display the supplied message in a Swing dialog, using the parent, title and message style when
        // provided.
        // Stop this method here; no further statements in this call are executed.
        if (rows.isEmpty()) { javax.swing.JOptionPane.showMessageDialog(parent,"This request has no attachments."); return; }
        // Declare names to hold an ordered array of text values. Create an array of String values with
        // rows.size() positions, indexed from zero.
        String[] names = new String[rows.size()];
        // Repeat while i is less than the number of entries in rows; initialise the counter once and update it
        // after each pass.
        // Store a numbered display name for the attachment at index i; the number identifies its saved path
        // later.
        for (int i=0;i<rows.size();i++) { names[i] = (i+1) + " - " + rows.get(i)[0]; }
        // Declare selected with type Object. Display an input or selection dialog and return its answer, or
        // null when cancelled.
        Object selected = javax.swing.JOptionPane.showInputDialog(parent,"Choose a document to open:","Request attachments",javax.swing.JOptionPane.PLAIN_MESSAGE,null,names,names[0]);
        // Continue with this branch when (selected equals no object (null)).
        // Stop this method here; no further statements in this call are executed.
        if (selected == null) { return; }
        // Declare index to hold a whole-number value. Its initial value is
        // `Integer.parseInt(selected.toString().split(" - ", 2)[0]) - 1`.
        int index = Integer.parseInt(selected.toString().split(" - ",2)[0])-1;
        // Declare file with type File. Create a file location without creating the file itself.
        File file = new File(rows.get(index)[1].toString());
        // Check the file type, readability, name length and size before accepting or opening it.
        validate(file);
        // Continue with this branch when (not java.awt.Desktop.isDesktopSupported() or not
        // java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.OPEN)).
        // Stop this operation with an exception: Open the file manually:
        if (!java.awt.Desktop.isDesktopSupported() || !java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.OPEN)) { throw new IllegalArgumentException("Open the file manually: " + file.getAbsolutePath()); }
        // Ask the operating system to open this file using its registered default application.
        java.awt.Desktop.getDesktop().open(file);
    }
}
