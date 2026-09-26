# CustomerQuotationsPanel design

Design work only. Event wiring, customer-decision storage and database queries will be implemented later.
A JPanel is appropriate because this customer screen appears inside MainFrame, not as a separate window.
The image is a reference generated with the built-in image tool.

| Control | Variable name | Text / model |
| --- | --- | --- |
| Label | jLabelTitle | My Quotations |
| Label | jLabelRequest | My request |
| Combo Box | jComboBoxRequest | Select request |
| Button | jButtonLoadQuotations | Load Quotations |
| Button | jButtonRefresh | Refresh |
| Table | jTableQuotations | Quotation ID, Vendor, Total, Status |
| Label | jLabelItems | Selected quotation items |
| Table | jTableQuotationItems | Description, Quantity, Unit Price, Line Total |
| Label | jLabelDetails | Quotation details |
| Text Area | jTextAreaDetails | Select a quotation to view its details. |
| Label | jLabelComments | Your comments (optional) |
| Text Area | jTextAreaComments | Empty |
| Label | jLabelDecision | Decision: No quotation selected |
| Button | jButtonDecline | Decline Quotation |
| Button | jButtonAccept | Accept Quotation |

Both tables: four columns, zero rows, all columns not editable.
Both text areas: lineWrap and wrapStyleWord true; keep them in scroll panes.
Quotation details: editable false. Customer comments: editable true.
Keep both tables in scroll panes.
Combo-box model: one list item Select request. If using Custom code, use:
`new javax.swing.DefaultComboBoxModel<String>(new String[] {"Select request"})`

On MainFrame, add a sidebar Button named jButtonMyQuotations with text My Quotations, below My Requests.
Do not double-click buttons to write handlers yet. Save All when finished.

## Generation prompt

Create a crisp front-facing UI design reference for a simple Java Swing JPanel called CustomerQuotationsPanel. Standard NetBeans rectangular controls on pale gray panel, navy headings, no window chrome, no sidebar, no icons, generous readable spacing. Caption outside panel 'CustomerQuotationsPanel - design reference'. Heading inside 'My Quotations'. Top row label 'My request', dropdown 'Select request', buttons 'Load Quotations' and 'Refresh'. Below an empty table with four headings 'Quotation ID', 'Vendor', 'Total', 'Status'. Below label 'Selected quotation items' then second empty table with headings 'Description', 'Quantity', 'Unit Price', 'Line Total'. Below label 'Quotation details' then white read-only multiline area with text 'Select a quotation to view its details.' Next label 'Your comments (optional)' and empty multiline input. Bottom left label 'Decision: No quotation selected'. Bottom right buttons 'Decline Quotation' and 'Accept Quotation'. Balanced 900x700 proportions, all controls visible, no sample rows beyond empty space. Easy to reproduce with JLabel JComboBox JButton JTable JTextArea JScrollPane.

