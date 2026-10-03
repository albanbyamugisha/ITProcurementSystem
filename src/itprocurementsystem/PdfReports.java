package itprocurementsystem;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

// iText draws reports from already-authorised data. It never queries account passwords.
public class PdfReports {
    private static final BaseColor NAVY = new BaseColor(24,47,70);
    private static final BaseColor TEAL = new BaseColor(0,117,120);

    public static void write(File file, ReportData report) throws Exception {
        // An embedded font keeps names readable on computers without our local fonts.
        byte[] fontBytes;
        try (java.io.InputStream input = PdfReports.class.getResourceAsStream("resources/DejaVuSans.ttf")) {
            if (input == null) { throw new java.io.IOException("The report font is missing from the application."); }
            java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[8192]; int count;
            while ((count = input.read(buffer)) != -1) { bytes.write(buffer,0,count); }
            fontBytes = bytes.toByteArray();
        }
        BaseFont base = BaseFont.createFont("DejaVuSans.ttf",BaseFont.IDENTITY_H,BaseFont.EMBEDDED,true,fontBytes,null);
        final Font body = new Font(base,9,Font.NORMAL,NAVY);
        final Font heading = new Font(base,12,Font.BOLD,TEAL);
        final Image logo = Image.getInstance(PdfReports.class.getResource("resources/system-logo.png"));
        logo.scaleToFit(190,60);
        final String generated = ZonedDateTime.now(java.time.ZoneId.of("Africa/Kampala"))
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm z"));
        // The user directory has wide email/name columns, so give it a landscape page.
        Rectangle pageSize = report.reference.equals("USERS") ? PageSize.A4.rotate() : PageSize.A4;
        Document document = new Document(pageSize,36,36,116,46);
        try (FileOutputStream output = new FileOutputStream(file)) {
            PdfWriter writer = PdfWriter.getInstance(document,output);
            // A page event repeats the brand and page number on long reports too.
            writer.setPageEvent(new PdfPageEventHelper() {
                public void onEndPage(PdfWriter w, Document d) {
                    try {
                        Image pageLogo = Image.getInstance(logo);
                        pageLogo.setAbsolutePosition(36,d.getPageSize().getHeight()-78);
                        w.getDirectContent().addImage(pageLogo);
                        ColumnText.showTextAligned(w.getDirectContent(),Element.ALIGN_LEFT,
                                new Phrase(report.reference + " | " + generated,body),36,d.getPageSize().getHeight()-98,0);
                        ColumnText.showTextAligned(w.getDirectContent(),Element.ALIGN_RIGHT,
                                new Phrase("Page " + w.getPageNumber(),body),d.right(),25,0);
                        ColumnText.showTextAligned(w.getDirectContent(),Element.ALIGN_LEFT,
                                new Phrase("2500603090 - BYAMUGISHA ALBAN - 2025/BSE/062/PS",body),36,25,0);
                    } catch (DocumentException ex) { throw new IllegalStateException("Could not draw the report header.",ex); }
                }
            });
            document.addTitle(report.title); document.addAuthor("IT Procurement Request System");
            document.open();
            Paragraph title = new Paragraph(report.title,new Font(base,18,Font.BOLD,NAVY));
            title.setSpacingAfter(12); document.add(title);
            for (ReportData.Section section : report.sections) {
                // Keep the section title with the header and first row where possible.
                PdfPTable table = new PdfPTable(section.columns.length);
                table.setWidthPercentage(100); table.setSpacingBefore(8); table.setSpacingAfter(12);
                if (section.columns.length == 2) { table.setWidths(new float[]{1,2.6f}); }
                if (section.columns.length == 6) {
                    table.setWidths(report.reference.equals("USERS") ? new float[]{.5f,1.5f,1.4f,2.5f,1,1} : new float[]{.7f,1.5f,1,1.4f,1.5f,1.4f});
                }
                if (section.columns.length == 5) { table.setWidths(new float[]{3,1.3f,.6f,1.4f,1.4f}); }
                PdfPCell caption = new PdfPCell(new Phrase(section.title,heading));
                caption.setColspan(section.columns.length); caption.setPadding(8); caption.setBorder(Rectangle.NO_BORDER);
                table.addCell(caption);
                for (String column : section.columns) {
                    PdfPCell cell = new PdfPCell(new Phrase(column,new Font(base,9,Font.BOLD,BaseColor.WHITE)));
                    cell.setBackgroundColor(NAVY); cell.setPadding(6); cell.setBorderColor(BaseColor.WHITE); table.addCell(cell);
                }
                table.setHeaderRows(2); table.setSplitLate(false); table.setKeepTogether(false);
                if (section.rows.isEmpty()) {
                    PdfPCell empty = new PdfPCell(new Phrase("No records available.",body));
                    empty.setColspan(section.columns.length); empty.setPadding(8); table.addCell(empty);
                }
                int index = 0;
                for (Object[] row : section.rows) {
                    for (int col=0;col<section.columns.length;col++) {
                        Object value = row[col];
                        String text = value == null ? "Not specified" : value.toString();
                        if (value instanceof java.math.BigDecimal) { text = new DecimalFormat("#,##0.00").format(value); }
                        PdfPCell cell = new PdfPCell(new Phrase(text,body));
                        cell.setPadding(6); cell.setBorderColor(new BaseColor(219,227,232));
                        if (index % 2 == 0) { cell.setBackgroundColor(new BaseColor(244,248,250)); }
                        table.addCell(cell);
                    }
                    index++;
                }
                document.add(table);
            }
            document.close();
        } finally { if (document.isOpen()) { document.close(); } }
    }
}
