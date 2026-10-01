# RegisterFrame layout

Generated using the built-in image tool. Build the controls in Design view.

| Control | Name | Text |
| --- | --- | --- |
| Label | jLabelTitle | Create Account |
| Text Field | jTextFieldFullName | Empty |
| Text Field | jTextFieldUsername | Empty |
| Text Field | jTextFieldEmail | Empty |
| Combo Box | jComboBoxAccountType | Select account type; Individual customer; Organisation user |
| Text Field | jTextFieldOrgaisationName | Empty (existing variable spelling) |
| Combo Box | jComboBoxDepartment | No department (optional) |
| Password Field | jPasswordFieldPassword | Empty |
| Password Field | jPasswordFieldConfirm | Empty |
| Label | jLabelRoleInfo | Create an account to request IT equipment and services. |
| Button | jButtonBackToLogin | Back to Login |
| Button | jButtonCreateAccount | Create Account |

Add labels for Full name, Username, Email, Account type, Organisation name, Department, Password and Confirm password.
Use Password Field controls for both passwords. Set the combo model through its list editor.
If using Custom code, enter a complete expression:
`new javax.swing.DefaultComboBoxModel<String>(new String[] {"No department (optional)"})`

In LoginFrame Design view also add a button named jButtonRegister with text Create Account.
The registration logic is already implemented. Account type must be selected; organisation users provide an organisation name.

## Original generation prompt

This earlier image predates the account-type fields. The control list above describes the saved form.

Create a crisp front-facing Java Swing registration JFrame design reference, simple standard NetBeans controls, pale gray background and navy text. Title Create Account. Six aligned rows: Full name blank text field; Username blank text field; Email blank text field; Department combo box showing Select department; Password empty password field; Confirm password empty password field. Below small text 'New accounts are registered as Requesters.' Bottom buttons Back to Login and Create Account. Window title 'IT Procurement - Create Account'. Portrait window about 560x600, generous spacing, no icons, no role selector, no extra controls. Caption outside window 'RegisterFrame - design reference'. Readable typography.

