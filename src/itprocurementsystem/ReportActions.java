package itprocurementsystem;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// Existing buttons share one ordinary save dialog, so no extra report form is needed.
public class ReportActions {
    public static void export(java.awt.Component parent, String kind, int id) {
        Path temporary = null;
        try {
            ReportDAO dao = new ReportDAO();
            ReportData report;
            if (kind.equals("users")) { report = dao.users(); }
            else if (kind.equals("request")) { report = dao.request(id); }
            else if (kind.equals("history")) { report = dao.history(id); }
            else { report = dao.account(id,kind.equals("selected")); }
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF documents","pdf"));
            chooser.setSelectedFile(new File(report.reference.toLowerCase() + ".pdf"));
            if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) { return; }
            File file = chooser.getSelectedFile().getAbsoluteFile();
            if (!file.getName().toLowerCase(java.util.Locale.ROOT).endsWith(".pdf")) { file = new File(file + ".pdf"); }
            boolean replace = file.exists();
            if (replace && JOptionPane.showConfirmDialog(parent,"Replace " + file.getName() + "?","File exists",JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { return; }
            // Finish a temporary document first; a failed export must not damage an old PDF.
            temporary = Files.createTempFile(file.toPath().getParent(),"procurement-", ".pdf");
            PdfReports.write(temporary.toFile(),report);
            if (replace) { Files.move(temporary,file.toPath(),StandardCopyOption.REPLACE_EXISTING); }
            else { Files.move(temporary,file.toPath()); }
            temporary = null;
            JOptionPane.showMessageDialog(parent,"PDF saved to:\n" + file);
        } catch (Exception ex) { FormSupport.error(parent,ex); }
        finally {
            if (temporary != null) {
                try { Files.deleteIfExists(temporary); } catch (java.io.IOException ignored) { temporary.toFile().deleteOnExit(); }
            }
        }
    }
}
