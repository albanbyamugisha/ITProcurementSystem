// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

// A Category object keeps one database category's ID and name together.
// This is a normal class, not a form: it stores data and does not display controls.
// Define Category as a class that groups its related data and methods.
public class Category {

    // private means other classes read these values through the getter methods.
    // int stores the whole-number ID; String stores the category's text name.
    // Only this class accesses this field directly. Declare categoryId to hold a whole-number value. Java
    // initially uses zero.
    private int categoryId;
    // Only this class accesses this field directly. Declare categoryName to hold text. Java initially uses
    // null for this object reference.
    private String categoryName;

    // The constructor gives a new Category object its starting values.
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public Category(int categoryId, String categoryName) {
        // this refers to the current object's field, rather than the parameter.
        // Copy the category id parameter into this object's categoryId field.
        this.categoryId = categoryId;
        // Copy the category name parameter into this object's categoryName field.
        this.categoryName = categoryName;
    }

    // Use the ID when connecting a request item to its database category.
    // Return the stored category id value without exposing a method that directly changes it.
    public int getCategoryId() {
        // Return categoryId to the caller.
        return categoryId;
    }

    // Use the name when displaying this category to the user.
    // Return the stored category name value without exposing a method that directly changes it.
    public String getCategoryName() {
        // Return categoryName to the caller.
        return categoryName;
    }
}
