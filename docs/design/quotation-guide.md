# QuotationPanel design guide

The image is a layout reference made with the built-in image-generation tool.
QuotationPanel is currently empty. Build its controls in NetBeans Design view.
Its purpose is to let a purchaser record a supplier's prices for an existing request.

| Control | Variable name | Text / model |
| --- | --- | --- |
| Label | jLabelTitle | Record Quotation |
| Label | jLabelRequest | Request |
| Combo Box | jComboBoxRequest | Select request |
| Button | jButtonLoadItems | Load Items |
| Label | jLabelVendor | Vendor |
| Combo Box | jComboBoxVendor | Select vendor |
| Table | jTableQuotationItems | Item Description, Quantity, Unit Price, Line Total |
| Label | jLabelUnitPrice | Unit price for selected item |
| Text Field | jTextFieldUnitPrice | 0.00 |
| Button | jButtonSetPrice | Set Price |
| Label | jLabelTotal | Quotation total: 0.00 |
| Label | jLabelSpecs | Specifications / notes (optional) |
| Text Area | jTextAreaSpecs | Empty |
| Button | jButtonClear | Clear |
| Button | jButtonSaveQuotation | Save Quotation |

Use zero rows and four non-editable columns in the table model.
Keep the table and text area in scroll panes.
Enable lineWrap and wrapStyleWord for the text area.
Use a single Label for the full total text; the image's bordered total amount is only styling.
Set the combo-box choices through the normal model list editor, not as bare Java code.
Save All. Event wiring and database saving will be added after the controls exist.

Planned workflow: select a request, load its items, choose a vendor, select each table row and set its quoted unit price, then save the entire quotation.
The item descriptions and quantities come from the request; the purchaser enters prices.
The total is calculated and is not editable. This workflow is not implemented yet.

## Generation prompt

Create a polished front-facing design reference for a beginner Java Swing JPanel called QuotationPanel. Flat standard rectangular Swing controls, pale gray background, navy text, white inputs, blue primary button, no icons, no window chrome. Panel roughly 800 by 640. Top heading 'Record Quotation'. Row label 'Request' and wide combo box 'Select request', button 'Load Items' to right. Next row label 'Vendor' and wide combo box 'Select vendor'. Below a wide empty table with exactly four columns 'Item Description', 'Quantity', 'Unit Price', 'Line Total'. Below table a line with label 'Unit price for selected item', text field '0.00', button 'Set Price'. Next right aligned label 'Quotation total: 0.00'. Below label 'Specifications / notes (optional)' and wide empty multiline text area. Bottom right buttons 'Clear' and 'Save Quotation'. Outside panel top caption 'QuotationPanel - design reference'. Clean readable typography, consistent spacing, controls easy to reproduce using NetBeans palette. No sample rows beyond blank grid guides.

