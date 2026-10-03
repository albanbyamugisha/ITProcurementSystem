package itprocurementsystem;

import java.sql.Connection;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.ArrayList;

// The database, not the customer's text fields, supplies the selling price.
public class CatalogueDAO {
    public ArrayList<Object[]> list(String search, String type) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Database.require(c, "Requester", "Admin", "Manager", "Purchaser");
            boolean admin = "Admin".equals(Database.one(c,"SELECT role FROM users WHERE user_id=?",Session.getUserId())[0]);
            return Database.rows(c, "SELECT k.catalogue_id,k.item_name,k.item_type,g.category_name,k.unit,k.price,IF(k.active,'Active','Inactive'),k.description,k.category_id FROM catalogue k JOIN categories g ON g.category_id=k.category_id WHERE (k.active=TRUE OR ?) AND (k.item_name LIKE ? OR k.description LIKE ?) AND (?='' OR k.item_type=?) ORDER BY k.item_name",
                    admin, "%" + search.trim() + "%", "%" + search.trim() + "%", type, type);
        }
    }
    public void save(int id, String name, String type, int category, String unit, String description,
            String amount, boolean active) throws SQLException {
        name = Database.text(name,"Item name",100,true);
        unit = Database.text(unit,"Unit",50,true);
        description = Database.text(description,"Description",255,true);
        if (!"Equipment".equals(type) && !"Service".equals(type)) { throw new IllegalArgumentException("Select Equipment or Service."); }
        BigDecimal price;
        try { price = new BigDecimal(amount.trim()); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Enter a valid UGX price."); }
        if (price.signum() <= 0 || price.compareTo(new BigDecimal("9999999999.99")) > 0 || price.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Enter a positive price with at most two decimal places.");
        }
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                Database.require(c,"Admin");
                if (id == 0) {
                    id = Database.insert(c,"INSERT INTO catalogue(item_name,item_type,category_id,unit,description,price,active) VALUES (?,?,?,?,?,?,?)", name,type,category,unit,description,price,active);
                } else if (Database.update(c,"UPDATE catalogue SET item_name=?,item_type=?,category_id=?,unit=?,description=?,price=?,active=? WHERE catalogue_id=?",name,type,category,unit,description,price,active,id) != 1) {
                    throw new IllegalArgumentException("That item no longer exists. Refresh the list.");
                }
                Database.audit(c,"Saved catalogue entry","catalogue",id);
                c.commit();
            } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        }
    }

    // Re-read and lock each selected item during request submission.
    // A changed price or specification requires the customer to remove and re-add that item.
    static Object[] validate(Connection c, RequestItem item, String type) throws SQLException {
        if (item.getCatalogueId() <= 0) { throw new IllegalArgumentException("Choose an item from the catalogue."); }
        Object[] row = Database.one(c,"SELECT category_id,item_name,description,unit,price,item_type,active FROM catalogue WHERE catalogue_id=? FOR UPDATE",item.getCatalogueId());
        boolean active = Boolean.TRUE.equals(row[6]) || "1".equals(row[6].toString());
        if (!active || !type.equals(row[5])) { throw new IllegalArgumentException("This catalogue item is unavailable for this request type."); }
        if (((BigDecimal)row[4]).compareTo(item.getUnitCost()) != 0 || !row[2].equals(item.getDescription())
                || !row[3].equals(item.getUnit()) || Database.id(row[0]) != item.getCategory().getCategoryId()) {
            throw new IllegalArgumentException("Catalogue details changed. Remove and re-add " + row[1] + " to review the current price.");
        }
        return row;
    }
}
