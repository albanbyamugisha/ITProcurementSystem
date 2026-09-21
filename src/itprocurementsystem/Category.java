package itprocurementsystem;

// A Category object keeps one database category's ID and name together.
// This is a normal class, not a form: it stores data and does not display controls.
public class Category {

    // private means other classes read these values through our getter methods.
    // int stores the whole-number ID; String stores the category's text name.
    private int categoryId;
    private String categoryName;

    // The constructor gives a new Category object its starting values.
    public Category(int categoryId, String categoryName) {
        // this refers to the current object's field, rather than the parameter.
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // Use the ID when connecting a request item to its database category.
    public int getCategoryId() {
        return categoryId;
    }

    // Use the name when displaying this category to the user.
    public String getCategoryName() {
        return categoryName;
    }
}
