// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// One object represents one physical unit received, not the whole requested quantity.
// Define DeliveryUnit as a class that groups its related data and methods.
public class DeliveryUnit {
    // Only this class accesses this field directly. Declare itemId to hold a whole-number value. Java
    // initially uses zero. The reference or value cannot be reassigned after initialisation.
    private final int itemId;
    // Only this class accesses this field directly. Declare serialNumber to hold text. Java initially uses
    // null for this object reference. The reference or value cannot be reassigned after initialisation.
    private final String serialNumber;
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public DeliveryUnit(int itemId, String serialNumber) {
        // Continue with this branch when (itemId is at most 0).
        // Stop this operation with an exception: Select a requested item first.
        if (itemId <= 0) { throw new IllegalArgumentException("Select a requested item first."); }
        // Copy the item id parameter into this object's itemId field.
        this.itemId = itemId;
        // Set serialNumber from the following operation: validate serialNumber as "Serial number" with a
        // maximum of 100 characters; a value is required.
        this.serialNumber = Database.text(serialNumber,"Serial number",100,true);
    }
    // Return the stored item id value without exposing a method that directly changes it.
    // Return itemId to the caller.
    public int getItemId() { return itemId; }
    // Return the stored serial number value without exposing a method that directly changes it.
    // Return serialNumber to the caller.
    public String getSerialNumber() { return serialNumber; }
}
