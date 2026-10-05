// Place this class in the itprocurementsystem package so related application classes share one
// namespace.
package itprocurementsystem;

/**
 * A JPanel keeps catalogue browsing and the Admin editor inside MainFrame.
 * @author alban-byamugisha
 */
// Define CataloguePanel as a JPanel: it groups this task's controls inside MainFrame rather than
// opening another window.
public class CataloguePanel extends javax.swing.JPanel {

    /**
     * Creates new form CataloguePanel
     */
    // Construct this object and initialise its fields or controls from the supplied starting values; a
    // constructor has no return type.
    public CataloguePanel() {
        // Create the controls and apply the layout stored by NetBeans before reading or changing any of those
        // controls.
        initComponents();
        // Set whether jPanelEditor is shown using `"Admin".equals(Session.getRole())`.
        jPanelEditor.setVisible("Admin".equals(Session.getRole()));
        // Set the text displayed by jLabeltitle to "Products and Services (UGX)".
        jLabeltitle.setText("Products and Services (UGX)");
        // Control whether jTextAreaDescription wraps long text onto another line.
        jTextAreaDescription.setLineWrap(true);
        // Control whether wrapped text breaks at word boundaries rather than between letters.
        jTextAreaDescription.setWrapStyleWord(true);
        // Give jTableCatalogue the supplied model, which holds its choices or table data.
        jTableCatalogue.setModel(new javax.swing.table.DefaultTableModel(
                new String[]{"ID","Item name","Type","Category","Unit","Price (UGX)","Status"},0) {
            // Tell the table model whether the indicated cell accepts direct typing; false protects displayed
            // database values.
            // Return false to the caller.
            public boolean isCellEditable(int row, int column) { return false; }
        });
        // Named actions keep event code outside NetBeans-generated layout code.
        // Attach the catalogue action named by the command to the supplied button.
        connect(jButtonSearch,"search"); connect(jButtonRefresh,"search");
        // Attach the catalogue action named by the command to the supplied button.
        connect(jButtonAddItem,"add"); connect(jButtonUpdateItem,"update"); connect(jButtonClear,"clear");
        // Connect the table selection to valueChanged so selecting a row updates the displayed details.
        jTableCatalogue.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            // Respond to a table-selection change; the event can fire several times while a selection is
            // adjusting.
            public void valueChanged(javax.swing.event.ListSelectionEvent event) {
                // Wait for a completed selection change before reading the selected table row.
                // Copy the selected catalogue row into the editor while preserving its database ID.
                if (!event.getValueIsAdjusting()) { showSelected(); }
            }
        });
        // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
        try {
            // Declare categories with type java.util.ArrayList<Object[]>. Create an initially empty resizable
            // list.
            java.util.ArrayList<Object[]> categories = new java.util.ArrayList<Object[]>();
            // Process each entry in new CategoryDAO().getAllCategories() in turn, referring to the current entry
            // as category.
            for (Category category : new CategoryDAO().getAllCategories()) {
                // Append `new Object[]{category.getCategoryId(), category.getCategoryName()}` to categories.
                categories.add(new Object[]{category.getCategoryId(),category.getCategoryName()});
            }
            // Rebuild the dropdown with its prompt followed by the supplied ID-and-description choices.
            FormSupport.choices(jComboBoxCategory,categories,"Select category");
            // Clear the catalogue editing controls and restore their initial selection state.
            // Read current saved records for this screen and refresh its choices, table or count.
            clearEditor(); reload();
        // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
        // Show the caught error as a message instead of allowing the event action to fail silently.
        } catch (Exception ex) { FormSupport.error(this,ex); }
    }

    // Keep complete rows, including descriptions which are not columns in the list.
    // Only this class accesses this field directly. Declare catalogueRows with type
    // java.util.ArrayList<Object[]>. Create an initially empty resizable list.
    private java.util.ArrayList<Object[]> catalogueRows = new java.util.ArrayList<Object[]>();
    // Attach the catalogue action named by the command to the supplied button.
    private void connect(javax.swing.JButton button, final String action) {
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        button.addActionListener(new java.awt.event.ActionListener() {
            // Handle the action event raised by the control connected to this listener.
            public void actionPerformed(java.awt.event.ActionEvent event) {
                // Attempt these operations; the following catch or finally blocks handle failures or cleanup.
                try {
                    // Continue with this branch when (action.equals("clear")).
                    // Clear the catalogue editing controls and restore their initial selection state.
                    // Stop this method here; no further statements in this call are executed.
                    if (action.equals("clear")) { clearEditor(); return; }
                    // Continue with this branch when (not action.equals("search")).
                    if (!action.equals("search")) {
                        // Declare id to hold a whole-number value. Read the ID in column zero of the selected model row,
                        // accounting for any sorting in the visible table.
                        int id = action.equals("add") ? 0 : FormSupport.selectedId(jTableCatalogue);
                        // Validate and save an Admin's catalogue changes, including the exact positive UGX price.
                        new CatalogueDAO().save(id,jTextFieldItemName.getText(),(String)jComboBoxType.getSelectedItem(),
                                FormSupport.choice(jComboBoxCategory),jTextFieldUnit.getText(),jTextAreaDescription.getText(),
                                jTextFieldPrice.getText(),jCheckBoxActive.isSelected());
                        // Clear the catalogue editing controls and restore their initial selection state.
                        clearEditor();
                    }
                    // Read current saved records for this screen and refresh its choices, table or count.
                    reload();
                // Handle Exception ex from the preceding try block so the failure follows the recovery steps below.
                // Show the caught error as a message instead of allowing the event action to fail silently.
                } catch (Exception ex) { FormSupport.error(CataloguePanel.this,ex); }
            }
        });
    }
    // Read current saved records for this screen and refresh its choices, table or count.
    private void reload() throws java.sql.SQLException {
        // Set catalogueRows from the following operation: search catalogue names or descriptions, showing
        // inactive entries only to an Admin.
        catalogueRows = new CatalogueDAO().list(jTextFieldSearch.getText(), "");
        // Replace the displayed rows using the supplied table model data; keep the existing headings.
        FormSupport.fill(jTableCatalogue,catalogueRows);
    }
    // Clear the catalogue editing controls and restore their initial selection state.
    private void clearEditor() {
        // Clear the selected row or entry in jTableCatalogue without deleting its data.
        jTableCatalogue.clearSelection();
        // Set the text displayed by jLabel3 to "Select an item to view its specification.".
        jLabel3.setText("Select an item to view its specification.");
        // Set the text displayed by jTextFieldItemName to empty text.
        // Set the text displayed by jTextFieldPrice to empty text.
        // Set the text displayed by jTextFieldUnit to empty text.
        jTextFieldItemName.setText(""); jTextFieldPrice.setText(""); jTextFieldUnit.setText("");
        // Set the text displayed by jTextAreaDescription to empty text.
        // Set whether the checkbox is ticked using false.
        jTextAreaDescription.setText(""); jCheckBoxActive.setSelected(false);
        // Select position 0 in jComboBoxType; dropdown positions start at zero.
        // Select position 0 in jComboBoxCategory; dropdown positions start at zero.
        jComboBoxType.setSelectedIndex(0); jComboBoxCategory.setSelectedIndex(0);
    }
    // Copy the selected catalogue row into the editor while preserving its database ID.
    private void showSelected() {
        // Declare index to hold a whole-number value. Read the selected visible row index in jTableCatalogue;
        // minus one means no row is selected.
        int index = jTableCatalogue.getSelectedRow();
        // Continue with this branch when (index is less than 0).
        // Stop this method here; no further statements in this call are executed.
        if (index < 0) { return; }
        // Declare row to hold one row of values in SELECT or table-column order. Read entry
        // jTableCatalogue.convertRowIndexToModel(index) from catalogueRows; list positions start at zero.
        Object[] row = catalogueRows.get(jTableCatalogue.convertRowIndexToModel(index));
        // Escape user text before putting it inside Swing's small HTML label.
        // Declare details to hold text. Its initial value is `(row[1] + ": " + row[7]).replace("&",
        // "&amp;").replace("<", "&lt;").replace(">", "&gt;")`.
        String details = (row[1] + ": " + row[7]).replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
        // Set the text displayed by jLabel3 to `"<html><div style='width:500px'>" + details +
        // "</div></html>"`.
        jLabel3.setText("<html><div style='width:500px'>" + details + "</div></html>");
        // Set the text displayed by jTextFieldItemName to `row[1].toString()`.
        // Select the entry matching `row[2]` in jComboBoxType.
        jTextFieldItemName.setText(row[1].toString()); jComboBoxType.setSelectedItem(row[2]);
        // Set the text displayed by jTextFieldUnit to `row[4].toString()`.
        // Set the text displayed by jTextFieldPrice to `row[5].toString()`.
        jTextFieldUnit.setText(row[4].toString()); jTextFieldPrice.setText(row[5].toString());
        // Set whether the checkbox is ticked using `"Active".equals(row[6])`.
        // Set the text displayed by jTextAreaDescription to `row[7].toString()`.
        jCheckBoxActive.setSelected("Active".equals(row[6])); jTextAreaDescription.setText(row[7].toString());
        // Repeat while i is less than jComboBoxCategory.getItemCount(); initialise the counter once and update
        // it after each pass.
        for (int i=1; i<jComboBoxCategory.getItemCount(); i++) {
            // Continue with this branch when (jComboBoxCategory.getItemAt(i).startsWith(row[8] + " - ")).
            // Select position i in jComboBoxCategory; dropdown positions start at zero.
            // Leave the current loop once the required result has been found.
            if (jComboBoxCategory.getItemAt(i).startsWith(row[8] + " - ")) { jComboBoxCategory.setSelectedIndex(i); break; }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * Horizontal groups control widths and left/right positions; vertical groups control heights.
     * LEADING aligns starts, TRAILING aligns ends, and BASELINE aligns text baselines.
     * PREFERRED_SIZE uses the component's preferred size; DEFAULT_SIZE asks the layout for a default.
     * Short.MAX_VALUE permits a large flexible size; it is not the actual window size.
     * Comments within the generated method may be replaced when Design view regenerates it.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // Create and arrange the controls described by the matching NetBeans .form file. Design view
    // regenerates this method.
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        // Set jButton3 from the following operation: create a clickable action button.
        jButton3 = new javax.swing.JButton();
        // Set jLabel8 from the following operation: create a non-editable text or image display.
        jLabel8 = new javax.swing.JLabel();
        // Set jButton7 from the following operation: create a clickable action button.
        jButton7 = new javax.swing.JButton();
        // Set jTextField6 from the following operation: create a single-line text input.
        jTextField6 = new javax.swing.JTextField();
        // Set jLabel12 from the following operation: create a non-editable text or image display.
        jLabel12 = new javax.swing.JLabel();
        // Set jLabeltitle from the following operation: create a non-editable text or image display.
        jLabeltitle = new javax.swing.JLabel();
        // Set jLabelSearch from the following operation: create a non-editable text or image display.
        jLabelSearch = new javax.swing.JLabel();
        // Set jTextFieldSearch from the following operation: create a single-line text input.
        jTextFieldSearch = new javax.swing.JTextField();
        // Set jButtonRefresh from the following operation: create a clickable action button.
        jButtonRefresh = new javax.swing.JButton();
        // Set jButtonSearch from the following operation: create a clickable action button.
        jButtonSearch = new javax.swing.JButton();
        // Set jScrollPane1 from the following operation: create a scrollable viewport for another control.
        jScrollPane1 = new javax.swing.JScrollPane();
        // Set jTableCatalogue from the following operation: create a table displaying rows and columns through
        // a model.
        jTableCatalogue = new javax.swing.JTable();
        // Set jPanelEditor from the following operation: create a container for controls inside an existing
        // window.
        jPanelEditor = new javax.swing.JPanel();
        // Set jLabelItemName from the following operation: create a non-editable text or image display.
        jLabelItemName = new javax.swing.JLabel();
        // Set jLabelCategory from the following operation: create a non-editable text or image display.
        jLabelCategory = new javax.swing.JLabel();
        // Set jLabelPrice from the following operation: create a non-editable text or image display.
        jLabelPrice = new javax.swing.JLabel();
        // Set jLabel7 from the following operation: create a non-editable text or image display.
        jLabel7 = new javax.swing.JLabel();
        // Set jScrollPane2 from the following operation: create a scrollable viewport for another control.
        jScrollPane2 = new javax.swing.JScrollPane();
        // Set jTextAreaDescription from the following operation: create a multi-line text display or input.
        jTextAreaDescription = new javax.swing.JTextArea();
        // Set jButtonAddItem from the following operation: create a clickable action button.
        jButtonAddItem = new javax.swing.JButton();
        // Set jButtonUpdateItem from the following operation: create a clickable action button.
        jButtonUpdateItem = new javax.swing.JButton();
        // Set jButtonClear from the following operation: create a clickable action button.
        jButtonClear = new javax.swing.JButton();
        // Set jTextFieldItemName from the following operation: create a single-line text input.
        jTextFieldItemName = new javax.swing.JTextField();
        // Set jTextFieldPrice from the following operation: create a single-line text input.
        jTextFieldPrice = new javax.swing.JTextField();
        // Set jLabelType from the following operation: create a non-editable text or image display.
        jLabelType = new javax.swing.JLabel();
        // Set jLabelUnit from the following operation: create a non-editable text or image display.
        jLabelUnit = new javax.swing.JLabel();
        // Set jTextFieldUnit from the following operation: create a single-line text input.
        jTextFieldUnit = new javax.swing.JTextField();
        // Set jCheckBoxActive from the following operation: create a JCheckBox object using the supplied
        // constructor values.
        jCheckBoxActive = new javax.swing.JCheckBox();
        // Set jComboBoxCategory from the following operation: create a dropdown selection control.
        jComboBoxCategory = new javax.swing.JComboBox<>();
        // Set jComboBoxType from the following operation: create a dropdown selection control.
        jComboBoxType = new javax.swing.JComboBox<>();
        // Set jLabel3 from the following operation: create a non-editable text or image display.
        jLabel3 = new javax.swing.JLabel();

        // Set the text displayed by jButton3 to empty text.
        jButton3.setText("");

        // Set the text displayed by jLabel8 to empty text.
        jLabel8.setText("");

        // Set the text displayed by jButton7 to empty text.
        jButton7.setText("");

        // Set the text displayed by jTextField6 to empty text.
        jTextField6.setText("");

        // Set the text displayed by jLabel12 to empty text.
        jLabel12.setText("");

        // Set the text displayed by jLabeltitle to "Products and Services".
        jLabeltitle.setText("Products and Services");

        // Set the text displayed by jLabelSearch to "Search".
        jLabelSearch.setText("Search");

        // Set the text displayed by jButtonRefresh to "Refresh".
        jButtonRefresh.setText("Refresh");

        // Set the text displayed by jButtonSearch to "Search".
        jButtonSearch.setText("Search");

        // Give jTableCatalogue the supplied model, which holds its choices or table data.
        jTableCatalogue.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item ID", "Item Name", "Type", "Category", "Unit", "Price(UGX)", "Status"
            }
        ) {
            // Declare canEdit with type boolean[]. Create an array of boolean values containing the listed entries
            // in order.
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            // Tell the table model whether the indicated cell accepts direct typing; false protects displayed
            // database values.
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                // Return `canEdit[columnIndex]` to the caller.
                return canEdit [columnIndex];
            }
        });
        // Place jTableCatalogue inside jScrollPane1 so it can scroll when larger than the available space.
        jScrollPane1.setViewportView(jTableCatalogue);

        // Set the text displayed by jLabelItemName to "Item name".
        jLabelItemName.setText("Item name");

        // Set the text displayed by jLabelCategory to "Category".
        jLabelCategory.setText("Category");

        // Set the text displayed by jLabelPrice to "Price (UGX)".
        jLabelPrice.setText("Price (UGX)");

        // Set the text displayed by jLabel7 to "Description / specification".
        jLabel7.setText("Description / specification");

        // Set the preferred text width of jTextAreaDescription to 20 columns; this is not a text-length limit.
        jTextAreaDescription.setColumns(20);
        // Set the preferred visible height of jTextAreaDescription to 5 text rows.
        jTextAreaDescription.setRows(5);
        // Place jTextAreaDescription inside jScrollPane2 so it can scroll when larger than the available
        // space.
        jScrollPane2.setViewportView(jTextAreaDescription);

        // Set the text displayed by jButtonAddItem to "Add Item".
        jButtonAddItem.setText("Add Item");

        // Set the text displayed by jButtonUpdateItem to "Update Selected".
        jButtonUpdateItem.setText("Update Selected");
        // Connect this control to its actionPerformed callback, which runs when an action is raised.
        jButtonUpdateItem.addActionListener(this::jButtonUpdateItemActionPerformed);

        // Set the text displayed by jButtonClear to "Clear".
        jButtonClear.setText("Clear");

        // Set the text displayed by jLabelType to "Type".
        jLabelType.setText("Type");

        // Set the text displayed by jLabelUnit to "Unit".
        jLabelUnit.setText("Unit");

        // Set the text displayed by jCheckBoxActive to "Active".
        jCheckBoxActive.setText("Active");

        // Give jComboBoxCategory the supplied model, which holds its choices or table data.
        jComboBoxCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select category" }));

        // Give jComboBoxType the supplied model, which holds its choices or table data.
        jComboBoxType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select type", "Equipment", "Service" }));

        // Declare jPanelEditorLayout with type javax.swing.GroupLayout. Create a layout manager describing
        // horizontal and vertical arrangements separately.
        javax.swing.GroupLayout jPanelEditorLayout = new javax.swing.GroupLayout(jPanelEditor);
        // Assign the supplied layout manager to jPanelEditor to control child-component positions and sizes.
        jPanelEditor.setLayout(jPanelEditorLayout);
        // Define the left-to-right arrangement and widths; the vertical group separately controls heights.
        jPanelEditorLayout.setHorizontalGroup(
            jPanelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            // Nest a sequential group to place controls one after another.
            .addGroup(jPanelEditorLayout.createSequentialGroup()
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap()
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(jPanelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jScrollPane2)
                    // Place jLabel7 in this layout group; any following size arguments give minimum, preferred and maximum
                    // sizes.
                    .addComponent(jLabel7)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(jPanelEditorLayout.createSequentialGroup()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(jPanelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Place jLabelItemName in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelItemName)
                            // Place jLabelCategory in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelCategory)
                            // Place jLabelPrice in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jLabelPrice))
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(18, 18, 18)
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(jPanelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            // Place jComboBoxCategory in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jComboBoxCategory, 0, 171, Short.MAX_VALUE)
                            // Place jTextFieldItemName in this layout group; any following size arguments give minimum, preferred
                            // and maximum sizes.
                            .addComponent(jTextFieldItemName)
                            // Place jTextFieldPrice in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jTextFieldPrice))
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(62, 62, 62)
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(jPanelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(jPanelEditorLayout.createSequentialGroup()
                                // Place jLabelUnit in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jLabelUnit)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                // Place jTextFieldUnit in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jTextFieldUnit))
                            // Nest a sequential group to place controls one after another.
                            .addGroup(jPanelEditorLayout.createSequentialGroup()
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(90, 90, 90)
                                // Place jCheckBoxActive in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jCheckBoxActive)
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(0, 0, Short.MAX_VALUE))
                            // Nest a sequential group to place controls one after another.
                            .addGroup(jPanelEditorLayout.createSequentialGroup()
                                // Place jLabelType in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jLabelType)
                                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                // Place jComboBoxType in this layout group; any following size arguments give minimum, preferred and
                                // maximum sizes.
                                .addComponent(jComboBoxType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap())
            // Nest a sequential group to place controls one after another.
            .addGroup(jPanelEditorLayout.createSequentialGroup()
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(124, 124, 124)
                // Place jButtonAddItem in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jButtonAddItem)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(58, 58, 58)
                // Place jButtonUpdateItem in this layout group; any following size arguments give minimum, preferred
                // and maximum sizes.
                .addComponent(jButtonUpdateItem)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(44, 44, 44)
                // Place jButtonClear in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jButtonClear)
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap(73, Short.MAX_VALUE))
        );
        // Define the top-to-bottom arrangement and heights; the horizontal group separately controls widths.
        jPanelEditorLayout.setVerticalGroup(
            jPanelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            // Nest a sequential group to place controls one after another.
            .addGroup(jPanelEditorLayout.createSequentialGroup()
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap()
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(jPanelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelItemName in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelItemName)
                    // Place jTextFieldItemName in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jTextFieldItemName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jLabelType in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelType)
                    // Place jComboBoxType in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jComboBoxType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(jPanelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Nest a parallel group so controls share this horizontal or vertical region.
                    .addGroup(jPanelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        // Place jLabelCategory in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jLabelCategory)
                        // Place jComboBoxCategory in this layout group; any following size arguments give minimum, preferred
                        // and maximum sizes.
                        .addComponent(jComboBoxCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    // Nest a parallel group so controls share this horizontal or vertical region.
                    .addGroup(jPanelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        // Place jLabelUnit in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jLabelUnit)
                        // Place jTextFieldUnit in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jTextFieldUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(jPanelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelPrice in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelPrice)
                    // Place jTextFieldPrice in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jTextFieldPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jCheckBoxActive in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jCheckBoxActive))
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(18, 18, 18)
                // Place jLabel7 in this layout group; any following size arguments give minimum, preferred and maximum
                // sizes.
                .addComponent(jLabel7)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane2 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(18, 18, 18)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(jPanelEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jButtonAddItem in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonAddItem)
                    // Place jButtonUpdateItem in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jButtonUpdateItem)
                    // Place jButtonClear in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonClear))
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        // Set the text displayed by jLabel3 to "Select an item to view its specification.".
        jLabel3.setText("Select an item to view its specification.");

        // Declare layout with type javax.swing.GroupLayout. Create a layout manager describing horizontal and
        // vertical arrangements separately.
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        // Assign the supplied layout manager to this to control child-component positions and sizes.
        this.setLayout(layout);
        // Define the left-to-right arrangement and widths; the vertical group separately controls heights.
        layout.setHorizontalGroup(
            // Start a parallel group whose children occupy the same region on this axis, using the selected
            // alignment.
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave spacing between the group and its enclosing container edge.
                        .addContainerGap()
                        // Place jLabelSearch in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jLabelSearch)
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        // Place jTextFieldSearch in this layout group; any following size arguments give minimum, preferred
                        // and maximum sizes.
                        .addComponent(jTextFieldSearch)
                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                        .addGap(18, 18, 18)
                        // Place jButtonSearch in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonSearch)
                        // Insert the look-and-feel's recommended spacing between neighbouring controls.
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                        // maximum sizes.
                        .addComponent(jButtonRefresh))
                    // Nest a sequential group to place controls one after another.
                    .addGroup(layout.createSequentialGroup()
                        // Leave spacing between the group and its enclosing container edge.
                        .addContainerGap()
                        // Nest a parallel group so controls share this horizontal or vertical region.
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                            // maximum sizes.
                            .addComponent(jScrollPane1)
                            // Nest a sequential group to place controls one after another.
                            .addGroup(layout.createSequentialGroup()
                                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                .addGap(6, 6, 6)
                                // Nest a parallel group so controls share this horizontal or vertical region.
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    // Nest a sequential group to place controls one after another.
                                    .addGroup(layout.createSequentialGroup()
                                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                        .addGap(6, 6, 6)
                                        // Place jPanelEditor in this layout group; any following size arguments give minimum, preferred and
                                        // maximum sizes.
                                        .addComponent(jPanelEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    // Nest a sequential group to place controls one after another.
                                    .addGroup(layout.createSequentialGroup()
                                        // Place jLabel3 in this layout group; any following size arguments give minimum, preferred and maximum
                                        // sizes.
                                        .addComponent(jLabel3)
                                        // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                                        .addGap(0, 0, Short.MAX_VALUE)))))))
                // Leave spacing between the group and its enclosing container edge.
                .addContainerGap())
            // Nest a sequential group to place controls one after another.
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(0, 0, Short.MAX_VALUE)
                // Place jLabeltitle in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabeltitle)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(233, 233, 233))
        );
        // Define the top-to-bottom arrangement and heights; the horizontal group separately controls widths.
        layout.setVerticalGroup(
            // Start a parallel group whose children occupy the same region on this axis, using the selected
            // alignment.
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            // Nest a sequential group to place controls one after another.
            .addGroup(layout.createSequentialGroup()
                // Place jLabeltitle in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jLabeltitle)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Nest a parallel group so controls share this horizontal or vertical region.
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    // Place jLabelSearch in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jLabelSearch)
                    // Place jTextFieldSearch in this layout group; any following size arguments give minimum, preferred
                    // and maximum sizes.
                    .addComponent(jTextFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    // Place jButtonRefresh in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonRefresh)
                    // Place jButtonSearch in this layout group; any following size arguments give minimum, preferred and
                    // maximum sizes.
                    .addComponent(jButtonSearch))
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jScrollPane1 in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                // Insert the look-and-feel's recommended spacing between neighbouring controls.
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                // Place jLabel3 in this layout group; any following size arguments give minimum, preferred and maximum
                // sizes.
                .addComponent(jLabel3)
                // Leave the stated gap in this layout; multiple values specify minimum, preferred and maximum spacing.
                .addGap(12, 12, 12)
                // Place jPanelEditor in this layout group; any following size arguments give minimum, preferred and
                // maximum sizes.
                .addComponent(jPanelEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Handle the action event from jButtonUpdateItem; any statements below run when that action is raised.
    private void jButtonUpdateItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateItemActionPerformed
        // Updating is handled by connect(jButtonUpdateItem, "update") in the constructor.
    }//GEN-LAST:event_jButtonUpdateItemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Only this class accesses this field directly. Declare jButton3 with type javax.swing.JButton. Java
    // initially uses null for this object reference.
    private javax.swing.JButton jButton3;
    // Only this class accesses this field directly. Declare jButton7 with type javax.swing.JButton. Java
    // initially uses null for this object reference.
    private javax.swing.JButton jButton7;
    // Only this class accesses this field directly. Declare jButtonAddItem with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonAddItem;
    // Only this class accesses this field directly. Declare jButtonClear with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonClear;
    // Only this class accesses this field directly. Declare jButtonRefresh with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonRefresh;
    // Only this class accesses this field directly. Declare jButtonSearch with type javax.swing.JButton.
    // Java initially uses null for this object reference.
    private javax.swing.JButton jButtonSearch;
    // Only this class accesses this field directly. Declare jButtonUpdateItem with type
    // javax.swing.JButton. Java initially uses null for this object reference.
    private javax.swing.JButton jButtonUpdateItem;
    // Only this class accesses this field directly. Declare jCheckBoxActive with type
    // javax.swing.JCheckBox. Java initially uses null for this object reference.
    private javax.swing.JCheckBox jCheckBoxActive;
    // Only this class accesses this field directly. Declare jComboBoxCategory with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxCategory;
    // Only this class accesses this field directly. Declare jComboBoxType with type
    // javax.swing.JComboBox<String>. Java initially uses null for this object reference.
    private javax.swing.JComboBox<String> jComboBoxType;
    // Only this class accesses this field directly. Declare jLabel12 with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabel12;
    // Only this class accesses this field directly. Declare jLabel3 with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabel3;
    // Only this class accesses this field directly. Declare jLabel7 with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabel7;
    // Only this class accesses this field directly. Declare jLabel8 with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabel8;
    // Only this class accesses this field directly. Declare jLabelCategory with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelCategory;
    // Only this class accesses this field directly. Declare jLabelItemName with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelItemName;
    // Only this class accesses this field directly. Declare jLabelPrice with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelPrice;
    // Only this class accesses this field directly. Declare jLabelSearch with type javax.swing.JLabel.
    // Java initially uses null for this object reference.
    private javax.swing.JLabel jLabelSearch;
    // Only this class accesses this field directly. Declare jLabelType with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelType;
    // Only this class accesses this field directly. Declare jLabelUnit with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabelUnit;
    // Only this class accesses this field directly. Declare jLabeltitle with type javax.swing.JLabel. Java
    // initially uses null for this object reference.
    private javax.swing.JLabel jLabeltitle;
    // Only this class accesses this field directly. Declare jPanelEditor with type javax.swing.JPanel.
    // Java initially uses null for this object reference.
    private javax.swing.JPanel jPanelEditor;
    // Only this class accesses this field directly. Declare jScrollPane1 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane1;
    // Only this class accesses this field directly. Declare jScrollPane2 with type
    // javax.swing.JScrollPane. Java initially uses null for this object reference.
    private javax.swing.JScrollPane jScrollPane2;
    // Only this class accesses this field directly. Declare jTableCatalogue with type javax.swing.JTable.
    // Java initially uses null for this object reference.
    private javax.swing.JTable jTableCatalogue;
    // Only this class accesses this field directly. Declare jTextAreaDescription with type
    // javax.swing.JTextArea. Java initially uses null for this object reference.
    private javax.swing.JTextArea jTextAreaDescription;
    // Only this class accesses this field directly. Declare jTextField6 with type javax.swing.JTextField.
    // Java initially uses null for this object reference.
    private javax.swing.JTextField jTextField6;
    // Only this class accesses this field directly. Declare jTextFieldItemName with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldItemName;
    // Only this class accesses this field directly. Declare jTextFieldPrice with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldPrice;
    // Only this class accesses this field directly. Declare jTextFieldSearch with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldSearch;
    // Only this class accesses this field directly. Declare jTextFieldUnit with type
    // javax.swing.JTextField. Java initially uses null for this object reference.
    private javax.swing.JTextField jTextFieldUnit;
    // End of variables declaration//GEN-END:variables
}
