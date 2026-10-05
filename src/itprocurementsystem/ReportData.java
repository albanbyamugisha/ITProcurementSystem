// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Import ArrayList for a resizable list whose entries retain their order.
import java.util.ArrayList;

// A report holds plain data, making its permission checks independent of PDF drawing.
// Define ReportData as a class that groups its related data and methods.
public class ReportData {
    // Declare title to hold text. Java initially uses null for this object reference.
    public String title;
    // Declare reference to hold text. Java initially uses null for this object reference.
    public String reference;
    // Declare sections with type ArrayList<Section>. Create an initially empty resizable list.
    public ArrayList<Section> sections = new ArrayList<Section>();
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    // Copy the title parameter into this object's title field.
    // Copy the reference parameter into this object's reference field.
    public ReportData(String title, String reference) { this.title = title; this.reference = reference; }
    // Each section has a heading and a table. Empty tables display a helpful message.
    // Define Section as a class that groups its related data and methods.
    public static class Section {
        // Declare title to hold text. Java initially uses null for this object reference.
        public String title;
        // Declare columns to hold an ordered array of text values. Java initially uses null for this object
        // reference.
        public String[] columns;
        // Declare rows with type ArrayList<Object[]>. Java initially uses null for this object reference.
        public ArrayList<Object[]> rows;
        // Construct this object and initialise its fields or controls from the supplied starting values; a
        // constructor has no return type.
        Section(String title, String[] columns, ArrayList<Object[]> rows) {
            // Copy the title parameter into this object's title field.
            // Copy the columns parameter into this object's columns field.
            // Copy the rows parameter into this object's rows field.
            this.title = title; this.columns = columns; this.rows = rows;
        }
    }
    // Add a titled section with the supplied column headings and row data to this report.
    public void add(String title, String[] columns, ArrayList<Object[]> rows) {
        // Append `new Section(title, columns, rows)` to sections.
        sections.add(new Section(title, columns, rows));
    }
}
