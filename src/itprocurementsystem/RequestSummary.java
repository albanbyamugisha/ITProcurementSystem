// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// These types represent exact money amounts and the database's date and time.
// Import BigDecimal for exact decimal values for prices and totals without floating-point rounding.
import java.math.BigDecimal;
// Import Timestamp for a date and time value read from JDBC.
import java.sql.Timestamp;

// This is a data class, not a form. One object holds one row for My Requests.
// Define RequestSummary as a class that groups its related data and methods.
public class RequestSummary {
    // Private fields keep each request's details together and prevent direct changes.
    // Only this class accesses this field directly. Declare requestId to hold a whole-number value. Java
    // initially uses zero.
    private int requestId;
    // Only this class accesses this field directly. Declare dateCreated to hold a database date and time.
    // Java initially uses null for this object reference.
    private Timestamp dateCreated;
    // Only this class accesses this field directly. Declare status to hold text. Java initially uses null
    // for this object reference.
    private String status;
    // Only this class accesses this field directly. Declare total to hold an exact decimal amount. Java
    // initially uses null for this object reference.
    private BigDecimal total;
    // Only this class accesses this field directly. Declare notes to hold text. Java initially uses null
    // for this object reference.
    private String notes;

    // The DAO supplies these values after reading a saved request from MySQL.
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public RequestSummary(int requestId, Timestamp dateCreated, String status,
            BigDecimal total, String notes) {
        // Copy the request id parameter into this object's requestId field.
        this.requestId = requestId;
        // Copy the date created parameter into this object's dateCreated field.
        this.dateCreated = dateCreated;
        // Copy the status parameter into this object's status field.
        this.status = status;
        // Copy the total parameter into this object's total field.
        this.total = total;
        // Copy the notes parameter into this object's notes field.
        this.notes = notes;
    }

    // Each getter returns one value for the table to display.
    // Return the stored request id value without exposing a method that directly changes it.
    // Return requestId to the caller.
    public int getRequestId() { return requestId; }
    // Return the stored date created value without exposing a method that directly changes it.
    // Return dateCreated to the caller.
    public Timestamp getDateCreated() { return dateCreated; }
    // Return the stored status value without exposing a method that directly changes it.
    // Return status to the caller.
    public String getStatus() { return status; }
    // Return the stored total value without exposing a method that directly changes it.
    // Return total to the caller.
    public BigDecimal getTotal() { return total; }
    // Return the stored notes value without exposing a method that directly changes it.
    // Return notes to the caller.
    public String getNotes() { return notes; }
}
