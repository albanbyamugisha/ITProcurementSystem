# RegisterFrame layout

Generated using the built-in image tool. Build the controls in Design view.

| Control | Name | Text |
| --- | --- | --- |
| Label | jLabelTitle | Create Account |
| Text Field | jTextFieldFullName | Empty |
| Text Field | jTextFieldUsername | Empty |
| Text Field | jTextFieldEmail | Empty |
| Combo Box | jComboBoxDepartment | Select department |
| Password Field | jPasswordFieldPassword | Empty |
| Password Field | jPasswordFieldConfirm | Empty |
| Label | jLabelRoleInfo | New accounts are registered as Requesters. |
| Button | jButtonBackToLogin | Back to Login |
| Button | jButtonCreateAccount | Create Account |

Add labels for Full name, Username, Email, Department, Password and Confirm password.
Use Password Field controls for both passwords. Set the combo model through its list editor.
If using Custom code, enter a complete expression:
`new javax.swing.DefaultComboBoxModel<String>(new String[] {"Select department"})`

In LoginFrame Design view also add a button named jButtonRegister with text Create Account.
Save All. The registration logic is not implemented yet.

## Generation prompt

Create a crisp front-facing Java Swing registration JFrame design reference, simple standard NetBeans controls, pale gray background and navy text. Title Create Account. Six aligned rows: Full name blank text field; Username blank text field; Email blank text field; Department combo box showing Select department; Password empty password field; Confirm password empty password field. Below small text 'New accounts are registered as Requesters.' Bottom buttons Back to Login and Create Account. Window title 'IT Procurement - Create Account'. Portrait window about 560x600, generous spacing, no icons, no role selector, no extra controls. Caption outside window 'RegisterFrame - design reference'. Readable typography.

