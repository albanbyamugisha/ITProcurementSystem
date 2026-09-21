package itprocurementsystem;

// These types represent exact money amounts and the database's date and time.
import java.math.BigDecimal;
import java.sql.Timestamp;

// This is a data class, not a form. One object holds one row for My Requests.
public class RequestSummary {
    // Private fields keep each request's details together and prevent direct changes.
    private int requestId;
    private Timestamp dateCreated;
    private String status;
    private BigDecimal total;
    private String notes;

    // The DAO supplies these values after reading a saved request from MySQL.
    public RequestSummary(int requestId, Timestamp dateCreated, String status,
            BigDecimal total, String notes) {
        this.requestId = requestId;
        this.dateCreated = dateCreated;
        this.status = status;
        this.total = total;
        this.notes = notes;
    }

    // Each getter returns one value for the table to display.
    public int getRequestId() { return requestId; }
    public Timestamp getDateCreated() { return dateCreated; }
    public String getStatus() { return status; }
    public BigDecimal getTotal() { return total; }
    public String getNotes() { return notes; }
}
