// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// A vendor is a supplier who provides a quotation for equipment.
// This is a data class, not a form: it holds values without displaying a window.
// Define Vendor as a class that groups its related data and methods.
public class Vendor {
    // Keep the database ID and the supplier's name together in one object.
    // private prevents other classes from changing these fields directly.
    // Only this class accesses this field directly. Declare vendorId to hold a whole-number value. Java
    // initially uses zero.
    private int vendorId;
    // Only this class accesses this field directly. Declare vendorName to hold text. Java initially uses
    // null for this object reference.
    private String vendorName;

    // The constructor receives the values read from the vendors table.
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public Vendor(int vendorId, String vendorName) {
        // this identifies the object's field when the parameter has the same name.
        // Copy the vendor id parameter into this object's vendorId field.
        this.vendorId = vendorId;
        // Copy the vendor name parameter into this object's vendorName field.
        this.vendorName = vendorName;
    }

    // The quotation will use this ID to refer to the correct supplier in MySQL.
    // Return the stored vendor id value without exposing a method that directly changes it.
    public int getVendorId() {
        // Return vendorId to the caller.
        return vendorId;
    }

    // The dropdown will display this readable name instead of the database ID.
    // Return the stored vendor name value without exposing a method that directly changes it.
    public String getVendorName() {
        // Return vendorName to the caller.
        return vendorName;
    }
}
