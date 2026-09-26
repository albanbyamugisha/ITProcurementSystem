# VendorPanel design

Design only; implementation and wiring are deferred.
Use a JPanel because supplier management appears inside MainFrame.
Create VendorPanel using New > JPanel Form.

| Control | Variable name | Text |
| --- | --- | --- |
| Label | jLabelTitle | Vendor Management |
| Text Field | jTextFieldVendorName | Empty |
| Text Field | jTextFieldContactPerson | Empty |
| Text Field | jTextFieldPhone | Empty |
| Text Field | jTextFieldEmail | Empty |
| Text Area | jTextAreaAddress | Empty |
| Button | jButtonAddVendor | Add Vendor |
| Button | jButtonUpdateVendor | Update Selected |
| Button | jButtonClear | Clear |
| Button | jButtonRefresh | Refresh |
| Table | jTableVendors | See below |
| Label | jLabelCount | Vendors: 0 |

Add descriptive labels: Vendor name, Contact person, Phone, Email, Address.
Table: Vendor ID, Vendor Name, Contact Person, Phone, Email, Address.
Use six columns, zero rows, all not editable.
Keep table and address area in scroll panes; enable lineWrap and wrapStyleWord for the address.
In MainFrame add a sidebar button named jButtonVendors, text Vendors.
Save All. Do not add event code.

## Generation prompt

Image generated with the built-in image-generation tool.

Create a crisp front-facing beginner Java Swing JPanel layout reference, caption outside 'VendorPanel - design reference'. Plain light gray panel, navy text, standard rectangular NetBeans Swing controls, no icons, no window chrome. Inside title 'Vendor Management'. Upper form two columns: left labels 'Vendor name', 'Contact person', 'Phone' with empty text fields; right 'Email' empty field and 'Address' empty multiline text area. Below form a horizontal row of buttons 'Add Vendor', 'Update Selected', 'Clear', 'Refresh'. Below buttons an empty table with six headers 'Vendor ID', 'Vendor Name', 'Contact Person', 'Phone', 'Email', 'Address'. Bottom left label 'Vendors: 0'. Balanced spacious 900x600 proportions, easily built using JLabel JTextField JTextArea JButton JTable JScrollPane, no delete button, no sample records. Legible exact text.

