// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// Import BigDecimal for exact decimal values for prices and totals without floating-point rounding.
import java.math.BigDecimal;

// This data object links a supplier's price to an existing requested item.
// Define QuotationItem as a class that groups its related data and methods.
public class QuotationItem {
    // Keep database identity and requested details separate from the entered price.
    // Only this class accesses this field directly. Declare requestItemId to hold a whole-number value.
    // Java initially uses zero.
    private int requestItemId;
    // Only this class accesses this field directly. Declare description to hold text. Java initially uses
    // null for this object reference.
    private String description;
    // Only this class accesses this field directly. Declare quantity to hold a whole-number value. Java
    // initially uses zero.
    private int quantity;
    // Only this class accesses this field directly. Declare unitPrice to hold an exact decimal amount.
    // Java initially uses null for this object reference.
    private BigDecimal unitPrice;

    // No price is assigned until the purchaser explicitly enters one.
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public QuotationItem(int requestItemId, String description, int quantity) {
        // Copy the request item id parameter into this object's requestItemId field.
        this.requestItemId = requestItemId;
        // Copy the description parameter into this object's description field.
        this.description = description;
        // Copy the quantity parameter into this object's quantity field.
        this.quantity = quantity;
    }

    // Getters let the form display values without changing requested quantities.
    // Return the stored request item id value without exposing a method that directly changes it.
    // Return requestItemId to the caller.
    public int getRequestItemId() { return requestItemId; }
    // Return the stored description value without exposing a method that directly changes it.
    // Return description to the caller.
    public String getDescription() { return description; }
    // Return the stored quantity value without exposing a method that directly changes it.
    // Return quantity to the caller.
    public int getQuantity() { return quantity; }
    // Return the stored unit price value without exposing a method that directly changes it.
    // Return unitPrice to the caller.
    public BigDecimal getUnitPrice() { return unitPrice; }

    // Check the money value before replacing a previously entered price.
    // Validate a positive supplier price with at most two decimal places, then store it as a BigDecimal.
    public void setUnitPrice(BigDecimal price) {
        // Continue with this branch when (price equals no object (null) or price.compareTo(BigDecimal.ZERO) is
        // at most 0 or price.compareTo(new BigDecimal("9999999999.99")) is greater than 0 or
        // price.stripTrailingZeros().scale() is greater than 2).
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0
                || price.compareTo(new BigDecimal("9999999999.99")) > 0
                || price.stripTrailingZeros().scale() > 2) {
            // Stop this operation with an exception: Enter a price from 0.01 to 9999999999.99 with at most two
            // decimal places.
            throw new IllegalArgumentException("Enter a price from 0.01 to 9999999999.99 with at most two decimal places.");
        }
        // Set unitPrice from the following operation: represent price with 2 digits after the decimal point.
        unitPrice = price.setScale(2);
    }

    // Unpriced items contribute zero to the running total but are not ready to save.
    // Calculate the line total by multiplying the unit price by the quantity using exact decimal arithmetic.
    public BigDecimal getLineTotal() {
        // Continue with this branch when (unitPrice equals no object (null)).
        // Create an exact decimal value from the supplied number or text. Return the resulting value to the
        // caller.
        if (unitPrice == null) { return new BigDecimal("0.00"); }
        // Multiply unitPrice by `BigDecimal.valueOf(quantity)` and return a new exact decimal result. Return
        // the resulting value to the caller.
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
