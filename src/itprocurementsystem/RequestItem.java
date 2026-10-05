// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// BigDecimal keeps decimal money calculations exact, unlike double.
// Import BigDecimal for exact decimal values for prices and totals without floating-point rounding.
import java.math.BigDecimal;

// This data class represents one item before the application saves the request to MySQL.
// Define RequestItem as a class that groups its related data and methods.
public class RequestItem {
    // Private fields keep the item's related values together inside this object.
    // Only this class accesses this field directly. Declare category with type Category. Java initially
    // uses null for this object reference.
    private Category category;
    // Only this class accesses this field directly. Declare catalogueId to hold a whole-number value. Java
    // initially uses zero.
    private int catalogueId;
    // Only this class accesses this field directly. Declare unit to hold text. Java initially uses null
    // for this object reference.
    private String unit;
    // Return the stored catalogue id value without exposing a method that directly changes it.
    // Return catalogueId to the caller.
    public int getCatalogueId() { return catalogueId; }
    // Return the stored unit value without exposing a method that directly changes it.
    // Return unit to the caller.
    public String getUnit() { return unit; }

    // The selection includes the displayed price; the DAO checks it again before saving.
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public RequestItem(int catalogueId, Category category, String description, String unit,
            int quantity, BigDecimal price) {
        // Call the other constructor first so the shared validation and field setup are reused.
        this(category, description, quantity, price);
        // Copy the catalogue id parameter into this object's catalogueId field.
        this.catalogueId = catalogueId;
        // Copy the unit parameter into this object's unit field.
        this.unit = unit;
    }
    // Only this class accesses this field directly. Declare description to hold text. Java initially uses
    // null for this object reference.
    private String description;
    // Only this class accesses this field directly. Declare quantity to hold a whole-number value. Java
    // initially uses zero.
    private int quantity;
    // Only this class accesses this field directly. Declare unitCost to hold an exact decimal amount. Java
    // initially uses null for this object reference.
    private BigDecimal unitCost;

    // The constructor checks the values before creating a usable request item.
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public RequestItem(Category category, String description, int quantity, BigDecimal unitCost) {
        // A real category is needed because the database links items by category ID.
        // Continue with this branch when (category equals no object (null) or category.getCategoryId() is at
        // most 0).
        if (category == null || category.getCategoryId() <= 0) {
            // Stop this operation with an exception: Please select a category.
            throw new IllegalArgumentException("Please select a category.");
        }
        // trim removes spaces at the ends; the database allows at most 255 characters.
        // Continue with this branch when (description equals no object (null) or description.trim().isEmpty()
        // or description.trim().length() is greater than 255).
        if (description == null || description.trim().isEmpty()
                || description.trim().length() > 255) {
            // Stop this operation with an exception: Enter an item description of 1 to 255 characters.
            throw new IllegalArgumentException("Enter an item description of 1 to 255 characters.");
        }
        // Quantity must be a positive whole number.
        // Continue with this branch when (quantity is at most 0).
        if (quantity <= 0) {
            // Stop this operation with an exception: Quantity must be greater than zero.
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        // DECIMAL(12,2) in the database allows ten digits before the decimal point.
        // Continue with this branch when (unitCost equals no object (null) or
        // unitCost.compareTo(BigDecimal.ZERO) is at most 0 or unitCost.compareTo(new
        // BigDecimal("9999999999.99")) is greater than 0).
        if (unitCost == null || unitCost.compareTo(BigDecimal.ZERO) <= 0
                || unitCost.compareTo(new BigDecimal("9999999999.99")) > 0) {
            // Stop this operation with an exception: Unit cost must be between 0.01 and 9999999999.99.
            throw new IllegalArgumentException("Unit cost must be between 0.01 and 9999999999.99.");
        }
        // Ignore trailing zeros when checking that the price has at most two decimal places.
        // Continue with this branch when (unitCost.stripTrailingZeros().scale() is greater than 2).
        if (unitCost.stripTrailingZeros().scale() > 2) {
            // Stop this operation with an exception: Unit cost must have at most two decimal places.
            throw new IllegalArgumentException("Unit cost must have at most two decimal places.");
        }
        // this distinguishes the object's fields from the constructor parameters.
        // Copy the category parameter into this object's category field.
        this.category = category;
        // Set description from the following operation: remove spaces and other trim characters from both ends
        // of description without changing text in the middle.
        this.description = description.trim();
        // Copy the quantity parameter into this object's quantity field.
        this.quantity = quantity;
        // Set unitCost from the following operation: represent unitCost with 2 digits after the decimal point.
        this.unitCost = unitCost.setScale(2);
    }

    // Getters let the form read the values without changing the saved item directly.
    // Return the stored category value without exposing a method that directly changes it.
    // Return category to the caller.
    public Category getCategory() { return category; }
    // Return the stored description value without exposing a method that directly changes it.
    // Return description to the caller.
    public String getDescription() { return description; }
    // Return the stored quantity value without exposing a method that directly changes it.
    // Return quantity to the caller.
    public int getQuantity() { return quantity; }
    // Return the stored unit cost value without exposing a method that directly changes it.
    // Return unitCost to the caller.
    public BigDecimal getUnitCost() { return unitCost; }

    // Multiply the price of one unit by the number of units requested.
    // Calculate the line total by multiplying the unit price by the quantity using exact decimal arithmetic.
    public BigDecimal getLineTotal() {
        // Multiply unitCost by `BigDecimal.valueOf(quantity)` and return a new exact decimal result. Return
        // the resulting value to the caller.
        return unitCost.multiply(BigDecimal.valueOf(quantity));
    }
}
