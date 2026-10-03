package itprocurementsystem;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

// Attachments are copied into application storage; the database keeps their locations.
public class AttachmentDAO {
    // Keep supporting documents small and restrict them to ordinary document/image types.
    public static void validate(File file) {
        if (file == null || !file.isFile() || !file.canRead()) { throw new IllegalArgumentException("Choose a readable file."); }
        String name = file.getName().toLowerCase(java.util.Locale.ROOT);
        if (!name.matches(".+\\.(pdf|png|jpg|jpeg|txt|docx|xlsx)")) { throw new IllegalArgumentException("Choose a PDF, image, text, DOCX or XLSX document."); }
        if (file.length() > 10 * 1024 * 1024) { throw new IllegalArgumentException("Each attachment must be 10 MB or smaller."); }
        Database.text(file.getName(),"File name",255,true);
    }
    public ArrayList<Object[]> forRequest(int request) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Database.require(c,"Requester","Admin","Manager","Purchaser","Admin");
            Object[] owner = Database.one(c,"SELECT r.requester_id,u.role FROM requests r JOIN users u ON u.user_id=? WHERE r.request_id=?",Session.getUserId(),request);
            if ("Requester".equals(owner[1]) && Database.id(owner[0]) != Session.getUserId()) { throw new IllegalArgumentException("This request belongs to another customer."); }
            return Database.rows(c,"SELECT file_name,file_path FROM attachments WHERE request_id=? ORDER BY attachment_id",request);
        }
    }
    // A normal Swing popup supplies an attachment action without requiring another designed form.
    public interface RequestChoice { int requestId(); }
    public static void addMenu(javax.swing.JComponent component, final RequestChoice choice) {
        javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem open = new javax.swing.JMenuItem("View request attachments");
        open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                try { showFiles(component,choice.requestId()); }
                catch (Exception ex) { FormSupport.error(component,ex); }
            }
        });
        menu.add(open); component.setComponentPopupMenu(menu);
        component.setToolTipText("Select a request, then right-click to view its attachments.");
    }
    // Open only the document the user explicitly selected from this authorised request.
    private static void showFiles(java.awt.Component parent, int request) throws Exception {
        ArrayList<Object[]> rows = new AttachmentDAO().forRequest(request);
        if (rows.isEmpty()) { javax.swing.JOptionPane.showMessageDialog(parent,"This request has no attachments."); return; }
        String[] names = new String[rows.size()];
        for (int i=0;i<rows.size();i++) { names[i] = (i+1) + " - " + rows.get(i)[0]; }
        Object selected = javax.swing.JOptionPane.showInputDialog(parent,"Choose a document to open:","Request attachments",javax.swing.JOptionPane.PLAIN_MESSAGE,null,names,names[0]);
        if (selected == null) { return; }
        int index = Integer.parseInt(selected.toString().split(" - ",2)[0])-1;
        File file = new File(rows.get(index)[1].toString());
        validate(file);
        if (!java.awt.Desktop.isDesktopSupported() || !java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.OPEN)) { throw new IllegalArgumentException("Open the file manually: " + file.getAbsolutePath()); }
        java.awt.Desktop.getDesktop().open(file);
    }
}
