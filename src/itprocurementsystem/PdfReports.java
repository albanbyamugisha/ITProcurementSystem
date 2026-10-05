// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Make the public types in com.itextpdf.text available by short names; this does not create objects.
import com.itextpdf.text.*;
// Make the public types in com.itextpdf.text.pdf available by short names; this does not create
// objects.
import com.itextpdf.text.pdf.*;
// Import File for a file or folder location.
import java.io.File;
// Import FileOutputStream for writing bytes to a destination file.
import java.io.FileOutputStream;
// Import DecimalFormat for formatted decimal amounts with grouping and decimal places.
import java.text.DecimalFormat;
// Import ZonedDateTime for a date and time with an explicit time zone.
import java.time.ZonedDateTime;
// Import DateTimeFormatter for the pattern used to format the report generation time.
import java.time.format.DateTimeFormatter;

// iText draws reports from already-authorised data. It never queries account passwords.
// Define PdfReports as a class that groups its related data and methods.
public class PdfReports {
    // Only this class accesses this field directly. Declare NAVY with type BaseColor. Create a colthe from
    // red, green and blue channel values. This field is shared by all instances of the class. The
    // reference or value cannot be reassigned after initialisation.
    private static final BaseColor NAVY = new BaseColor(24,47,70);
    // Only this class accesses this field directly. Declare TEAL with type BaseColor. Create a colthe from
    // red, green and blue channel values. This field is shared by all instances of the class. The
    // reference or value cannot be reassigned after initialisation.
    private static final BaseColor TEAL = new BaseColor(0,117,120);

    // Draw report sections into a PDF with the embedded font, system logo and repeating page details.
    public static void write(File file, ReportData report) throws Exception {
        // An embedded font keeps names readable on computers without the local fonts.
        // Declare fontBytes to hold an array of bytes.
        byte[] fontBytes;
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare input with type java.io.InputStream. Open the packaged resource as a stream; null means the
        // resource was not found.
        try (java.io.InputStream input = PdfReports.class.getResourceAsStream("resources/DejaVuSans.ttf")) {
            // Continue with this branch when (input equals no object (null)).
            // Stop this operation with an exception: The report font is missing from the application.
            if (input == null) { throw new java.io.IOException("The report font is missing from the application."); }
            // Declare bytes with type java.io.ByteArrayOutputStream. Create a ByteArrayOutputStream object using
            // the supplied constructor values.
            java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
            // Declare buffer to hold an array of bytes. Create an array of byte values with 8192 positions,
            // indexed from zero.
            // Declare count to hold a whole-number value.
            byte[] buffer = new byte[8192]; int count;
            // Read the next buffer of bytes and continue until read returns -1, meaning the end of the stream.
            // Write the supplied bytes to the output stream using the stated offset and byte count.
            while ((count = input.read(buffer)) != -1) { bytes.write(buffer,0,count); }
            // Set fontBytes from the following operation: copy the collected bytes into a byte array for the next
            // operation.
            fontBytes = bytes.toByteArray();
        }
        // Declare base with type BaseFont. Load and embed the supplied font bytes with Unicode character
        // mapping so names render consistently in the PDF.
        BaseFont base = BaseFont.createFont("DejaVuSans.ttf",BaseFont.IDENTITY_H,BaseFont.EMBEDDED,true,fontBytes,null);
        // Declare body with type Font. Create a font with the supplied family or base font, size and style.
        // The reference or value cannot be reassigned after initialisation.
        final Font body = new Font(base,9,Font.NORMAL,NAVY);
        // Declare heading with type Font. Create a font with the supplied family or base font, size and style.
        // The reference or value cannot be reassigned after initialisation.
        final Font heading = new Font(base,12,Font.BOLD,TEAL);
        // Declare logo with type Image. Its initial value is
        // `Image.getInstance(PdfReports.class.getResource("resources/system-logo.png"))`. The reference or
        // value cannot be reassigned after initialisation.
        final Image logo = Image.getInstance(PdfReports.class.getResource("resources/system-logo.png"));
        // Resize the image to fit the supplied width and height while preserving its proportions.
        logo.scaleToFit(190,60);
        // Declare generated to hold text. Format the supplied number or date according to the configured
        // decimal or date pattern. The reference or value cannot be reassigned after initialisation.
        final String generated = ZonedDateTime.now(java.time.ZoneId.of("Africa/Kampala"))
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm z"));
        // The user directory has wide email/name columns, so give it a landscape page.
        // Declare pageSize with type Rectangle. Its initial value is `report.reference.equals("USERS") ?
        // PageSize.A4.rotate() : PageSize.A4`.
        Rectangle pageSize = report.reference.equals("USERS") ? PageSize.A4.rotate() : PageSize.A4;
        // Declare document with type Document. Create a Document object using the supplied constructor values.
        Document document = new Document(pageSize,36,36,116,46);
        // Open the declared resources for this block; try-with-resources closes them in reverse order even if
        // an error occurs.
        // Declare output with type FileOutputStream. Create a FileOutputStream object using the supplied
        // constructor values.
        try (FileOutputStream output = new FileOutputStream(file)) {
            // Declare writer with type PdfWriter. Its initial value is `PdfWriter.getInstance(document, output)`.
            PdfWriter writer = PdfWriter.getInstance(document,output);
            // A page event repeats the brand and page number on long reports too.
            // Register a PDF page callback that repeats the header, logo, footer and page number on each page.
            writer.setPageEvent(new PdfPageEventHelper() {
                // Draw the repeating logo, reference, generation time, page number and footer when iText finishes a
                // page.
                public void onEndPage(PdfWriter w, Document d) {
                    // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                    try {
                        // Declare pageLogo with type Image. Its initial value is `Image.getInstance(logo)`.
                        Image pageLogo = Image.getInstance(logo);
                        // Place the PDF image at the supplied x and y coordinates, measured in points from the page
                        // bottom-left.
                        pageLogo.setAbsolutePosition(36,d.getPageSize().getHeight()-78);
                        // Draw the positioned logo onto the current PDF page.
                        w.getDirectContent().addImage(pageLogo);
                        // Draw the supplied phrase at the stated PDF coordinates with the requested alignment and rotation.
                        ColumnText.showTextAligned(w.getDirectContent(),Element.ALIGN_LEFT,
                                new Phrase(report.reference + " | " + generated,body),36,d.getPageSize().getHeight()-98,0);
                        // Draw the supplied phrase at the stated PDF coordinates with the requested alignment and rotation.
                        ColumnText.showTextAligned(w.getDirectContent(),Element.ALIGN_RIGHT,
                                new Phrase("Page " + w.getPageNumber(),body),d.right(),25,0);
                        // Draw the supplied phrase at the stated PDF coordinates with the requested alignment and rotation.
                        ColumnText.showTextAligned(w.getDirectContent(),Element.ALIGN_LEFT,
                                new Phrase("2500603090 - BYAMUGISHA ALBAN - 2025/BSE/062/PS",body),36,25,0);
                    // Handle DocumentException ex from the preceding try block so the failure follows the recovery steps
                    // below.
                    // Stop this operation with an exception: Could not draw the report header.
                    } catch (DocumentException ex) { throw new IllegalStateException("Could not draw the report header.",ex); }
                }
            });
            // Store the report title in the PDF document metadata.
            // Store the application name as the PDF document author.
            document.addTitle(report.title); document.addAuthor("IT Procurement Request System");
            // Open the PDF document so paragraphs and tables can be written to its pages.
            document.open();
            // Declare title with type Paragraph. Create a paragraph of PDF text.
            Paragraph title = new Paragraph(report.title,new Font(base,18,Font.BOLD,NAVY));
            // Leave 12 PDF points after this paragraph or table.
            // Append title to document.
            title.setSpacingAfter(12); document.add(title);
            // Process each entry in report.sections in turn, referring to the current entry as section.
            for (ReportData.Section section : report.sections) {
                // Keep the section title with the header and first row where possible.
                // Declare table with type PdfPTable. Create a PDF table with the specified number of columns.
                PdfPTable table = new PdfPTable(section.columns.length);
                // Use 100 percent of the available PDF page width for this table.
                // Leave 8 PDF points before this table.
                // Leave 12 PDF points after this paragraph or table.
                table.setWidthPercentage(100); table.setSpacingBefore(8); table.setSpacingAfter(12);
                // Continue with this branch when (section.columns.length equals 2).
                // Set relative PDF column widths; larger numbers allocate more space to that column.
                if (section.columns.length == 2) { table.setWidths(new float[]{1,2.6f}); }
                // Continue with this branch when (section.columns.length equals 6).
                if (section.columns.length == 6) {
                    // Set relative PDF column widths; larger numbers allocate more space to that column.
                    table.setWidths(report.reference.equals("USERS") ? new float[]{.5f,1.5f,1.4f,2.5f,1,1} : new float[]{.7f,1.5f,1,1.4f,1.5f,1.4f});
                }
                // Continue with this branch when (section.columns.length equals 5).
                // Set relative PDF column widths; larger numbers allocate more space to that column.
                if (section.columns.length == 5) { table.setWidths(new float[]{3,1.3f,.6f,1.4f,1.4f}); }
                // Declare caption with type PdfPCell. Create a PDF table cell containing the supplied phrase.
                PdfPCell caption = new PdfPCell(new Phrase(section.title,heading));
                // Make this PDF cell span section.columns.length columns.
                // Leave 8 PDF points between the cell content and its border.
                // Remove the PDF cell border using Rectangle.NO_BORDER.
                caption.setColspan(section.columns.length); caption.setPadding(8); caption.setBorder(Rectangle.NO_BORDER);
                // Append this cell to the PDF table, continuing in column order.
                table.addCell(caption);
                // Process each entry in section.columns in turn, referring to the current entry as column.
                for (String column : section.columns) {
                    // Declare cell with type PdfPCell. Create a PDF table cell containing the supplied phrase.
                    PdfPCell cell = new PdfPCell(new Phrase(column,new Font(base,9,Font.BOLD,BaseColor.WHITE)));
                    // Fill this PDF cell with the specified background colour.
                    // Leave 6 PDF points between the cell content and its border.
                    // Draw this PDF cell's border using the specified colour.
                    // Append this cell to the PDF table, continuing in column order.
                    cell.setBackgroundColor(NAVY); cell.setPadding(6); cell.setBorderColor(BaseColor.WHITE); table.addCell(cell);
                }
                // Repeat the first 2 table rows at the top of each page occupied by the table.
                // Allow a long PDF table row to split without first forcing the whole row onto the next page.
                // Allow the PDF table to flow across pages instead of requiring every row to fit on one page.
                table.setHeaderRows(2); table.setSplitLate(false); table.setKeepTogether(false);
                // Continue with this branch when (section.rows is empty).
                if (section.rows.isEmpty()) {
                    // Declare empty with type PdfPCell. Create a PDF table cell containing the supplied phrase.
                    PdfPCell empty = new PdfPCell(new Phrase("No records available.",body));
                    // Make this PDF cell span section.columns.length columns.
                    // Leave 8 PDF points between the cell content and its border.
                    // Append this cell to the PDF table, continuing in column order.
                    empty.setColspan(section.columns.length); empty.setPadding(8); table.addCell(empty);
                }
                // Declare index to hold a whole-number value. Its initial value is 0.
                int index = 0;
                // Process each entry in section.rows in turn, referring to the current entry as row.
                for (Object[] row : section.rows) {
                    // Repeat while col is less than section.columns.length; initialise the counter once and update it
                    // after each pass.
                    for (int col=0;col<section.columns.length;col++) {
                        // Declare value with type Object. Its initial value is `row[col]`.
                        Object value = row[col];
                        // Declare text to hold text. Convert value == null ? "Not specified" : value to its text
                        // representation.
                        String text = value == null ? "Not specified" : value.toString();
                        // Continue with this branch when (value instanceof java.math.BigDecimal).
                        // Set text from the following operation: format the supplied number or date according to the
                        // configured decimal or date pattern.
                        if (value instanceof java.math.BigDecimal) { text = new DecimalFormat("#,##0.00").format(value); }
                        // Declare cell with type PdfPCell. Create a PDF table cell containing the supplied phrase.
                        PdfPCell cell = new PdfPCell(new Phrase(text,body));
                        // Leave 6 PDF points between the cell content and its border.
                        // Draw this PDF cell's border using the specified colour.
                        cell.setPadding(6); cell.setBorderColor(new BaseColor(219,227,232));
                        // Continue with this branch when (index % 2 equals 0).
                        // Fill this PDF cell with the specified background colour.
                        if (index % 2 == 0) { cell.setBackgroundColor(new BaseColor(244,248,250)); }
                        // Append this cell to the PDF table, continuing in column order.
                        table.addCell(cell);
                    }
                    // Increase index by one after this item or successful check.
                    index++;
                }
                // Append table to document.
                document.add(table);
            }
            // Close document and release its associated resources.
            document.close();
        // Continue with this branch when (document.isOpen()).
        // Close document and release its associated resources.
        } finally { if (document.isOpen()) { document.close(); } }
    }
}
