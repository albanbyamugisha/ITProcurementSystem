# DepartmentPanel design
Create a JPanel Form named DepartmentPanel; it appears inside MainFrame.
Design work only. No event wiring or SQL changes now.

| Control | Variable name | Text |
| --- | --- | --- |
| Label | jLabelTitle | Department Management |
| Label | jLabelDepartmentName | Department name |
| Text Field | jTextFieldDepartmentName | Empty |
| Button | jButtonAddDepartment | Add Department |
| Button | jButtonUpdateDepartment | Update Selected |
| Button | jButtonClear | Clear |
| Button | jButtonRefresh | Refresh |
| Table | jTableDepartments | Department ID, Department Name |
| Label | jLabelSelectedDepartment | Selected department: None |
| Label | jLabelCount | Departments: 0 |

Two columns, zero rows, all not editable. Keep the table in a scroll pane.
MainFrame: add jButtonDepartments with text Departments.
These are shared department choices matching the current schema, not organisation membership administration.
Save All; implementation follows after all design work.

## Generation prompt
Generated with the built-in image-generation tool.

Create a crisp front-facing beginner Java Swing JPanel design reference. Outside caption 'DepartmentPanel - design reference'. Inside title 'Department Management'. Light gray background, navy text, standard rectangular Swing controls, no icons, no window chrome. Top row label 'Department name', wide empty text field. Next row buttons 'Add Department', 'Update Selected', 'Clear', 'Refresh'. Below a large empty table with exactly two column headings 'Department ID' and 'Department Name'. Below table left label 'Selected department: None', bottom label 'Departments: 0'. Balanced 800x500 proportions with generous spacing, readable text, simple JLabel JTextField JButton JTable JScrollPane. No delete button and no organisation selector.

