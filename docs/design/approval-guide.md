# ApprovalPanel layout

Build this panel in NetBeans Design view, following approval-reference.png.
The image was produced using the built-in image-generation tool.
This is the staff review screen; customer acceptance remains a separate workflow.

| Control | Variable name | Text or setting |
| --- | --- | --- |
| Label | jLabelTitle | Review Quotations |
| Label | jLabelRequest | Request |
| Combo Box | jComboBoxRequest | Select request |
| Button | jButtonLoadQuotations | Load Quotations |
| Button | jButtonRefresh | Refresh |
| Label | jLabelRequestDetails | Request details |
| Text Area | jTextAreaRequestDetails | Select a request to view its details. |
| Table | jTableQuotations | Quotation ID, Vendor, Total, Status |
| Label | jLabelItems | Selected quotation items |
| Table | jTableQuotationItems | Description, Quantity, Unit Price, Line Total |
| Label | jLabelComments | Review comments |
| Text Area | jTextAreaComments | Empty |
| Label | jLabelReviewInfo | Staff review is separate from customer acceptance. |
| Button | jButtonReject | Reject Request |
| Button | jButtonApprove | Approve Selected Quotation |

Set both tables to four columns and zero rows. Disable editing for every column.
Keep both tables and both text areas inside scroll panes.
Set request details editable to false. Comments remain editable.
Enable lineWrap and wrapStyleWord for both text areas.
Set the combo-box model through the normal list editor, with one item: Select request.
Save All; event handlers and database code will be connected after these controls exist.

## Generation prompt

Create a clear high fidelity beginner Java Swing JPanel design reference. Title outside panel 'ApprovalPanel - design reference'. Flat pale gray panel 900x700 proportions, navy text, standard rectangular Swing controls, no window chrome or sidebar. Inside heading 'Review Quotations'. Top row label 'Request', dropdown 'Select request', button 'Load Quotations', button 'Refresh'. Below label 'Request details' then white read-only multiline area showing 'Select a request to view its details.' Next table with headers exactly 'Quotation ID', 'Vendor', 'Total', 'Status', empty body. Below label 'Selected quotation items', second empty table with headers 'Description', 'Quantity', 'Unit Price', 'Line Total'. Below label 'Review comments', wide empty multiline text area. Bottom left small text 'Staff review is separate from customer acceptance.' Bottom right two buttons 'Reject Request' and 'Approve Selected Quotation'. Spacious legible balanced layout, no sample data no extra controls. Simple to recreate with NetBeans palette.
