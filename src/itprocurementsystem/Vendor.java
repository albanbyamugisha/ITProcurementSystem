package itprocurementsystem;

// A vendor is a supplier who provides a quotation for equipment.
// This is a data class, not a form: it holds values without displaying a window.
public class Vendor {
    // Keep the database ID and the supplier's name together in one object.
    // private prevents other classes from changing these fields directly.
    private int vendorId;
    private String vendorName;

    // The constructor receives the values read from the vendors table.
    public Vendor(int vendorId, String vendorName) {
        // this identifies the object's field when the parameter has the same name.
        this.vendorId = vendorId;
        this.vendorName = vendorName;
    }

    // The quotation will use this ID to refer to the correct supplier in MySQL.
    public int getVendorId() {
        return vendorId;
    }

    // The dropdown will display this readable name instead of the database ID.
    public String getVendorName() {
        return vendorName;
    }
}
