package itprocurementsystem;

import java.math.BigDecimal;

// This data object links a supplier's price to an existing requested item.
public class QuotationItem {
    // Keep database identity and requested details separate from the entered price.
    private int requestItemId;
    private String description;
    private int quantity;
    private BigDecimal unitPrice;

    // No price is assigned until the purchaser explicitly enters one.
    public QuotationItem(int requestItemId, String description, int quantity) {
        this.requestItemId = requestItemId;
        this.description = description;
        this.quantity = quantity;
    }

    // Getters let the form display values without changing requested quantities.
    public int getRequestItemId() { return requestItemId; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }

    // Check the money value before replacing a previously entered price.
    public void setUnitPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0
                || price.compareTo(new BigDecimal("9999999999.99")) > 0
                || price.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Enter a price from 0.01 to 9999999999.99 with at most two decimal places.");
        }
        unitPrice = price.setScale(2);
    }

    // Unpriced items contribute zero to the running total but are not ready to save.
    public BigDecimal getLineTotal() {
        if (unitPrice == null) { return new BigDecimal("0.00"); }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
