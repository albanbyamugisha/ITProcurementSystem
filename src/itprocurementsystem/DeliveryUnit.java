package itprocurementsystem;

// One object represents one physical unit received, not the whole requested quantity.
public class DeliveryUnit {
    private final int itemId;
    private final String serialNumber;
    public DeliveryUnit(int itemId, String serialNumber) {
        if (itemId <= 0) { throw new IllegalArgumentException("Select a requested item first."); }
        this.itemId = itemId;
        this.serialNumber = Database.text(serialNumber,"Serial number",100,true);
    }
    public int getItemId() { return itemId; }
    public String getSerialNumber() { return serialNumber; }
}
