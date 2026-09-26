# UserManagementPanel design

Create New > JPanel Form named UserManagementPanel. This panel belongs inside MainFrame.
Design only. No role-changing code or permissions are implemented in this step.

| Control | Variable name | Text/model |
| --- | --- | --- |
| Label | jLabelTitle | User Management |
| Label | jLabelSearch | Search users |
| Text Field | jTextFieldSearch | Empty |
| Button | jButtonSearch | Search |
| Button | jButtonShowAll | Show All |
| Button | jButtonRefresh | Refresh |
| Table | jTableUsers | Columns below |
| Label | jLabelSelectedUser | Selected user: None |
| Label | jLabelRole | Role |
| Combo Box | jComboBoxRole | Select role; Requester; Manager; Purchaser |
| Button | jButtonUpdateRole | Update Role |
| Label | jLabelCount | Users: 0 |
| Label | jLabelHint | Role changes are restricted to authorised staff. |

Table: User ID, Full Name, Username, Email, Account Type, Organisation, Role.
Seven columns, zero rows, all not editable. Keep it inside a scroll pane.
Put each combo choice on a separate line in the normal model editor.
MainFrame: add jButtonUsers with text Users.
Do not add the outside image caption as an application label.
Save All. Privileged account administration policy must be implemented before enabling role changes.
No passwords or hashes belong in this table.

## Generation prompt
Generated using the built-in image-generation tool.

Create a clear beginner Java Swing JPanel design reference, caption 'UserManagementPanel - design reference'. Pale gray background navy text, standard rectangular Swing controls, no icons or window chrome. Inside heading 'User Management'. Top row label 'Search users', empty text field, buttons 'Search', 'Show All', 'Refresh'. Large empty table with seven headings 'User ID', 'Full Name', 'Username', 'Email', 'Account Type', 'Organisation', 'Role'. Below label 'Selected user: None'. Next row label 'Role', combo box 'Select role', button 'Update Role'. Bottom left label 'Users: 0'. Bottom small hint 'Role changes are restricted to authorised staff.' Wide 1000x600 layout with readable text and generous spacing. No password field, no delete button, no create user button because users register on separate form.

