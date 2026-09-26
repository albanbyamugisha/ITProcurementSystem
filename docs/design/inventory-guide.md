# InventoryPanel design
Create a JPanel Form named InventoryPanel. It will display inside MainFrame.
Design only; no implementation or event wiring at this stage.

| Control | Variable name | Text/model |
| --- | --- | --- |
| Label | jLabelTitle | Equipment Inventory |
| Text Field | jTextFieldSearch | Empty |
| Button | jButtonSearch | Search |
| Button | jButtonShowAll | Show All |
| Button | jButtonRefresh | Refresh |
| Table | jTableInventory | Columns below |
| Label | jLabelSelectedEquipment | Selected equipment: None |
| Combo Box | jComboBoxAssignedTo | Select user |
| Button | jButtonAssign | Assign Selected |
| Button | jButtonUnassign | Unassign Selected |
| Label | jLabelCount | Equipment units: 0 |

Add labels Search and Assign to.
Table columns: Inventory ID, Description, Serial Number, Category, Assigned To, Date Added, Request ID.
Seven columns, zero rows, all not editable, in a scroll pane.
Keep the entire selected-equipment caption in one label, and the count in one label.
MainFrame: add jButtonInventory with text Inventory. Save All.
Equipment records will come from deliveries. Assignment permissions and customer ownership checks will be implemented later; this design does not grant access to any role.

## Generation prompt
Generated using the built-in image-generation tool.

Create a crisp front-facing UI design reference for a beginner Java Swing JPanel. Outside caption 'InventoryPanel - design reference'. Inside title 'Equipment Inventory'. Pale gray background, navy text, simple rectangular Swing controls, no icons or window chrome. Top row label 'Search', empty text field, buttons 'Search', 'Show All', 'Refresh'. Below a wide empty table with seven column headers 'Inventory ID', 'Description', 'Serial Number', 'Category', 'Assigned To', 'Date Added', 'Request ID'. Below label 'Selected equipment: None'. Next row label 'Assign to', combo box 'Select user', buttons 'Assign Selected', 'Unassign Selected'. Bottom left label 'Equipment units: 0'. Wide 1000x600 proportions. No add or delete inventory buttons: equipment comes from recorded deliveries. Standard JLabel JTextField JComboBox JButton JTable JScrollPane, all text readable, generous spacing.

