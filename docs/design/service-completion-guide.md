# ServiceCompletionPanel design
Create a JPanel Form named ServiceCompletionPanel. It belongs inside MainFrame.
Design only: saving, validation and database changes come later.

| Control | Variable name | Text/model |
| --- | --- | --- |
| Label | jLabelTitle | Service Completion |
| Combo Box | jComboBoxRequest | Select request |
| Button | jButtonLoadDetails | Load Details |
| Button | jButtonRefresh | Refresh |
| Label | jLabelProvider | Provider: Select a request |
| Table | jTableServiceItems | Item ID, Description, Quantity |
| Combo Box | jComboBoxWorkStatus | Select status; Not Started; In Progress; Completed |
| Text Field | jTextFieldCompletionDate | Empty |
| Text Area | jTextAreaWorkNotes | Empty |
| Button | jButtonClear | Clear |
| Button | jButtonSaveProgress | Save Progress |
| Label | jLabelHint | Enter a completion date only when the service is completed. |

Add descriptive labels Service request, Requested services, Work status, Completion date (YYYY-MM-DD), Work notes.
Table: three columns, zero rows, not editable, inside a scroll pane.
Work notes: scroll pane, lineWrap and wrapStyleWord true.
Combo model choices are separate list items, not semicolon-separated text.
MainFrame: add button jButtonServices with text Services.
Save All; no event wiring yet.

## Generation prompt
Generated with the built-in image-generation tool.

Create a crisp front-facing beginner Java Swing JPanel design reference, title outside 'ServiceCompletionPanel - design reference'. Plain pale gray panel with navy headings, ordinary rectangular Swing controls, no icons, no window chrome. Inside heading 'Service Completion'. Top row label 'Service request', dropdown 'Select request', buttons 'Load Details' and 'Refresh'. Below label 'Provider: Select a request'. Next label 'Requested services', empty table with headers 'Item ID', 'Description', 'Quantity'. Below label 'Work status', combo 'Select status'. Next label 'Completion date (YYYY-MM-DD)', blank text field. Below label 'Work notes', large empty multiline text area. Bottom buttons 'Clear' and 'Save Progress'. Small bottom hint 'Enter a completion date only when the service is completed.' 850x650 proportions with generous spacing, legible text, all controls visible. No serial number or inventory controls.

