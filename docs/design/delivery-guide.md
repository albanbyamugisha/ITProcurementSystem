# DeliveryPanel design
Design only; no event wiring yet. Use a JPanel because this screen appears inside MainFrame.
Create New > JPanel Form named DeliveryPanel.

| Control | Variable name | Text/model |
| --- | --- | --- |
| Label | jLabelTitle | Equipment Deliveries |
| Combo Box | jComboBoxRequest | Select request |
| Button | jButtonLoadItems | Load Items |
| Button | jButtonRefresh | Refresh |
| Label | jLabelSupplier | Supplier: Select a request |
| Text Field | jTextFieldDeliveryDate | Empty |
| Table | jTableRequestItems | Item ID, Description, Ordered Qty, Received Qty, Remaining Qty |
| Text Field | jTextFieldSerialNumber | Empty |
| Button | jButtonAddUnit | Add Unit |
| Table | jTableReceivedUnits | Item ID, Description, Serial Number |
| Button | jButtonRemoveUnit | Remove Selected Unit |
| Button | jButtonClear | Clear |
| Button | jButtonSaveDelivery | Save Delivery |
| Label | jLabelHint | Enter one serial number for each equipment unit received. |

Add descriptive labels Request, Delivery date (YYYY-MM-DD), Selected item serial number.
Both tables: zero rows, all columns not editable, inside scroll panes.
Use a normal Text Field for the date; validation comes during implementation.
The first table shows saved received quantities; the second holds units for the current delivery.
This follows the current serial-number-based equipment schema. Service completion has its own panel.
MainFrame already has a Deliveries button; do not add a duplicate.
Save All.

## Generation prompt
Generated with the built-in image tool.

Create a crisp front-facing beginner Java Swing JPanel UI reference titled outside 'DeliveryPanel - design reference'. Standard rectangular NetBeans controls, pale gray panel, navy headings, no icons or window chrome. Inside title 'Equipment Deliveries'. Top row 'Request' combo 'Select request', button 'Load Items', button 'Refresh'. Next row label 'Supplier: Select a request', then label 'Delivery date (YYYY-MM-DD)' and blank text field. Below an empty table with five headers 'Item ID', 'Description', 'Ordered Qty', 'Received Qty', 'Remaining Qty'. Below table row label 'Selected item serial number', blank text field, button 'Add Unit'. Next empty table three headers 'Item ID', 'Description', 'Serial Number'. Below button 'Remove Selected Unit'. Bottom right buttons 'Clear' and 'Save Delivery'. Bottom left small caption 'Enter one serial number for each equipment unit received.' Wide 950x700 proportions, spacious readable design, no sample records, all controls visible. All tables read only; quantity received is computed from individual units. No service fields.

