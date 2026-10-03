package itprocurementsystem;

import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.Path;

// Every PDF button opens a preview first. The PDF viewer provides Save As / Save a Copy.
// A temporary file is necessary for the viewer, but no permanent save location is requested.
public class ReportActions {
    public static void export(java.awt.Component parent, String kind, int id) {
        Path preview = null;
        try {
            ReportDAO dao = new ReportDAO();
            ReportData report;
            if (kind.equals("users")) { report = dao.users(); }
            else if (kind.equals("request")) { report = dao.request(id); }
            else if (kind.equals("history")) { report = dao.history(id); }
            else { report = dao.account(id,kind.equals("selected")); }
            preview = createPreview(report);
            // Desktop asks the operating system to open its normal PDF viewer.
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(preview.toFile());
            } else if (System.getProperty("os.name").toLowerCase(java.util.Locale.ROOT).contains("linux")) {
                // Some Linux Java installations do not support Desktop.OPEN.
                // ProcessBuilder passes the path as one argument; it does not use shell commands.
                Process opener = new ProcessBuilder("xdg-open",preview.toString())
                        .redirectOutput(new java.io.File("/dev/null"))
                        .redirectError(new java.io.File("/dev/null")).start();
                if (opener.waitFor(2,java.util.concurrent.TimeUnit.SECONDS) && opener.exitValue()!=0) {
                    throw new java.io.IOException("Could not open the PDF. Set a default PDF viewer and try again.");
                }
            } else {
                throw new java.io.IOException("No PDF viewer is available. Set a default PDF viewer and try again.");
            }
            // Keep the preview available while the user reads or saves a copy in the viewer.
            preview = null;
        } catch (Exception ex) { FormSupport.error(parent,ex); }
        finally {
            if (preview != null) {
                try { Files.deleteIfExists(preview); } catch (java.io.IOException ignored) { preview.toFile().deleteOnExit(); }
            }
        }
    }

    // Separate creation from opening so tests can inspect a preview without launching windows.
    static Path createPreview(ReportData report) throws Exception {
        Path preview = Files.createTempFile("procurement-" + report.reference.toLowerCase(java.util.Locale.ROOT) + "-", ".pdf");
        preview.toFile().deleteOnExit();
        try { PdfReports.write(preview.toFile(),report); return preview; }
        catch (Exception ex) { Files.deleteIfExists(preview); throw ex; }
    }
}
