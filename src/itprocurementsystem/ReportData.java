package itprocurementsystem;

import java.util.ArrayList;

// A report holds plain data, making its permission checks independent of PDF drawing.
public class ReportData {
    public String title;
    public String reference;
    public ArrayList<Section> sections = new ArrayList<Section>();
    public ReportData(String title, String reference) { this.title = title; this.reference = reference; }
    // Each section has a heading and a table. Empty tables display a helpful message.
    public static class Section {
        public String title;
        public String[] columns;
        public ArrayList<Object[]> rows;
        Section(String title, String[] columns, ArrayList<Object[]> rows) {
            this.title = title; this.columns = columns; this.rows = rows;
        }
    }
    public void add(String title, String[] columns, ArrayList<Object[]> rows) {
        sections.add(new Section(title, columns, rows));
    }
}
