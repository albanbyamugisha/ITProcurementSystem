// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Import Desktop for the operating system action for opening a file in its default application.
import java.awt.Desktop;
// Import Files for file creation, copying, reading and deletion.
import java.nio.file.Files;
// Import Path for a filesystem path without opening the file.
import java.nio.file.Path;

// Every PDF button opens a preview first. The PDF viewer provides Save As / Save a Copy.
// A temporary file is necessary for the viewer, but no permanent save location is requested.
// Define ReportActions as a class that groups its related data and methods.
public class ReportActions {
    // Fetch authorised report data, create a temporary PDF and open it in the system PDF viewer.
    public static void export(java.awt.Component parent, String kind, int id) {
        // Declare preview with type Path. Its initial value is no value (null).
        Path preview = null;
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Declare dao with type ReportDAO. Create a ReportDAO object using the supplied constructor values.
            ReportDAO dao = new ReportDAO();
            // Declare report with type ReportData.
            ReportData report;
            // Continue with this branch when (kind.equals("users")).
            // Set report from the following operation: read account details for an Admin without selecting
            // password hashes.
            if (kind.equals("users")) { report = dao.users(); }
            // Continue with this branch when (kind.equals("request")).
            // Set report from the following operation: build an owner-or-Admin request report, including saved
            // selling prices and fulfilment outcomes.
            else if (kind.equals("request")) { report = dao.request(id); }
            // Continue with this branch when (kind.equals("history")).
            // Set report from the following operation: build an authorised account's procurement-history report
            // with identity and workflow sections.
            else if (kind.equals("history")) { report = dao.history(id); }
            // Set report from the following operation: build account-report sections from explicit public account
            // fields, optionally including procurement history.
            else { report = dao.account(id,kind.equals("selected")); }
            // Set preview from the following operation: write a temporary PDF for viewing and remove a failed
            // partial file before passing an error onward.
            preview = createPreview(report);
            // Desktop asks the operating system to open its normal PDF viewer.
            // Continue with this branch when (Desktop.isDesktopSupported() and
            // Desktop.getDesktop().isSupported(Desktop.Action.OPEN)).
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                // Ask the operating system to open this file using its registered default application.
                Desktop.getDesktop().open(preview.toFile());
            // Continue with this branch when
            // (System.getProperty("os.name").toLowerCase(java.util.Locale.ROOT).contains("linux")).
            } else if (System.getProperty("os.name").toLowerCase(java.util.Locale.ROOT).contains("linux")) {
                // Some Linux Java installations do not support Desktop.OPEN.
                // ProcessBuilder passes the path as one argument; it does not use shell commands.
                // Declare opener with type Process. Start this worker or external process so its operation can run.
                Process opener = new ProcessBuilder("xdg-open",preview.toString())
                        .redirectOutput(new java.io.File("/dev/null"))
                        .redirectError(new java.io.File("/dev/null")).start();
                // Continue with this branch when (opener.waitFor(2, java.util.concurrent.TimeUnit.SECONDS) and
                // opener.exitValue() is not equal to 0).
                if (opener.waitFor(2,java.util.concurrent.TimeUnit.SECONDS) && opener.exitValue()!=0) {
                    // Stop this operation with an exception: Could not open the PDF. Set a default PDF viewer and try
                    // again.
                    throw new java.io.IOException("Could not open the PDF. Set a default PDF viewer and try again.");
                }
            } else {
                // Stop this operation with an exception: No PDF viewer is available. Set a default PDF viewer and try
                // again.
                throw new java.io.IOException("No PDF viewer is available. Set a default PDF viewer and try again.");
            }
            // Keep the preview available while the user reads or saves a copy in the viewer.
            // Store no value (null) in preview for the remaining steps.
            preview = null;
        // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
        // Show the caught error as a message instead of allowing the event action to fail silently.
        } catch (Exception ex) { FormSupport.error(parent,ex); }
        finally {
            // Clean up a preview still owned by this method; a successfully opened preview has already cleared
            // this reference.
            if (preview != null) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                // Delete the specified temporary or copied file during cleanup; deleteIfExists also accepts an already
                // absent file.
                // Handle java.io.IOException ignored from the preceding try block so the failure follows the recovery
                // steps below.
                // Arrange for this temporary file to be deleted when Java exits normally.
                try { Files.deleteIfExists(preview); } catch (java.io.IOException ignored) { preview.toFile().deleteOnExit(); }
            }
        }
    }

    // Separate creation from opening so tests can inspect a preview without launching windows.
    // Write a temporary PDF for viewing and remove a failed partial file before passing an error onward.
    static Path createPreview(ReportData report) throws Exception {
        // Declare preview with type Path. Create a uniquely named temporary file for this operation.
        Path preview = Files.createTempFile("procurement-" + report.reference.toLowerCase(java.util.Locale.ROOT) + "-", ".pdf");
        // Arrange for this temporary file to be deleted when Java exits normally.
        preview.toFile().deleteOnExit();
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        // Draw report sections into a PDF with the embedded font, system logo and repeating page details.
        // Return preview to the caller.
        try { PdfReports.write(preview.toFile(),report); return preview; }
        // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
        // Delete the specified temporary or copied file during cleanup; deleteIfExists also accepts an already
        // absent file.
        // Pass the caught exception back to the caller so the original failure is not treated as success.
        catch (Exception ex) { Files.deleteIfExists(preview); throw ex; }
    }
}
