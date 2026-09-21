# RequestPanel design guide

This panel will appear inside MainFrame. Build the controls in NetBeans Design view.
The image is a visual reference generated with the built-in image-generation tool.
Use a JLabel for the entire "Total: 0.00" display, even though the image gives the amount a border.
Blank table rows in the image illustrate spacing; set the actual table row count to zero.

## Controls

| Palette control | Variable name | Text or setting |
| --- | --- | --- |
| Label | jLabelTitle | New Procurement Request |
| Label | jLabelCategory | Category |
| Combo Box | jComboBoxCategory | Model: Select category |
| Label | jLabelDescription | Item description |
| Text Field | jTextFieldDescription | Empty |
| Label | jLabelQuantity | Quantity |
| Text Field | jTextFieldQuantity | 1 |
| Label | jLabelUnitCost | Estimated unit cost |
| Text Field | jTextFieldUnitCost | 0.00 |
| Button | jButtonAddItem | Add Item |
| Button | jButtonRemoveItem | Remove Selected |
| Table | jTableItems | Category, Description, Quantity, Unit Cost, Line Total |
| Label | jLabelTotal | Total: 0.00 |
| Label | jLabelNotes | Notes (optional) |
| Text Area | jTextAreaNotes | Empty; lineWrap and wrapStyleWord true |
| Button | jButtonClear | Clear |
| Button | jButtonSubmit | Submit Request |

Use Swing controls. Keep the table and text area inside scroll panes.
Arrange controls as in request-panel-reference.png, approximately 760 by 620 pixels.
Rename controls with Change Variable Name. Set visible wording using the text property.
In the table model editor set five columns with the headings above, zero rows, and disable editing for each column.
Do not add event code yet.

## Planned behaviour

Add Item will add an item to the on-screen list. Submit Request will save the request and all its items together.
Estimated unit cost means the price of one unit. Line Total is quantity multiplied by unit cost.
The total display is calculated, not typed by the user.
Categories will come from the database in a later step.
Requester identity and department will come from the signed-in user's database record.
Request status and creation time will be filled automatically.
These behaviours are planned; this step only supplies the design guide.

## Generation prompt

Create a crisp front-facing UI design reference image for a beginner Java Swing JPanel called RequestPanel, for a general IT procurement desktop application. Show ONLY a rectangular light gray panel, no desktop window chrome, no sidebar. Simple ordinary Swing controls, navy text, white inputs, muted blue primary buttons, generous consistent spacing, no icons or decorative graphics. Heading 'New Procurement Request'. Form rows: label 'Category' with wide combo box showing 'Select category'; label 'Item description' with wide empty text field; next row 'Quantity' with small text field showing '1' and 'Estimated unit cost' with text field showing '0.00'. Below aligned buttons 'Add Item' and 'Remove Selected'. Below a table with column headers exactly 'Category', 'Description', 'Quantity', 'Unit Cost', 'Line Total', and three blank rows. Below table right aligned label 'Total: 0.00'. Below label 'Notes (optional)' and a wide empty multiline text area. Bottom right buttons 'Clear' and 'Submit Request'. Panel approximately 760 wide by 620 tall proportions. Above panel outside caption 'RequestPanel - design reference'. All text very readable, no sample items and no extra functionality. This is a polished but easily reproducible standard NetBeans Swing form design.

