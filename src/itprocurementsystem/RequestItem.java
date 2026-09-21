package itprocurementsystem;

// BigDecimal keeps decimal money calculations exact, unlike double.
import java.math.BigDecimal;

// This data class represents one item before we save the request to MySQL.
public class RequestItem {
    // Private fields keep the item's related values together inside this object.
    private Category category;
    private String description;
    private int quantity;
    private BigDecimal unitCost;

    // The constructor checks the values before creating a usable request item.
    public RequestItem(Category category, String description, int quantity, BigDecimal unitCost) {
        // A real category is needed because the database links items by category ID.
        if (category == null || category.getCategoryId() <= 0) {
            throw new IllegalArgumentException("Please select a category.");
        }
        // trim removes spaces at the ends; the database allows at most 255 characters.
        if (description == null || description.trim().isEmpty()
                || description.trim().length() > 255) {
            throw new IllegalArgumentException("Enter an item description of 1 to 255 characters.");
        }
        // Quantity must be a positive whole number.
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        // DECIMAL(12,2) in our database allows ten digits before the decimal point.
        if (unitCost == null || unitCost.compareTo(BigDecimal.ZERO) <= 0
                || unitCost.compareTo(new BigDecimal("9999999999.99")) > 0) {
            throw new IllegalArgumentException("Unit cost must be between 0.01 and 9999999999.99.");
        }
        // Ignore trailing zeros when checking that the price has at most two decimal places.
        if (unitCost.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Unit cost must have at most two decimal places.");
        }
        // this distinguishes the object's fields from the constructor parameters.
        this.category = category;
        this.description = description.trim();
        this.quantity = quantity;
        this.unitCost = unitCost.setScale(2);
    }

    // Getters let the form read the values without changing the saved item directly.
    public Category getCategory() { return category; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitCost() { return unitCost; }

    // Multiply the price of one unit by the number of units requested.
    public BigDecimal getLineTotal() {
        return unitCost.multiply(BigDecimal.valueOf(quantity));
    }
}
