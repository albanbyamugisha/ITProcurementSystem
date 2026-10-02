# FINAL reduced dragging checklist

Agreed reduced scope: two new forms plus small changes to six existing forms. This file replaces the earlier seven-form checklist. Prepare only the controls listed here. No separate HomePanel, ProfilePanel, CatalogueManagementPanel, ReportsPanel or PaymentsPanel is needed. Payment recording and payment receipts are postponed.

## General settings

Use package `itprocurementsystem` and Swing controls. Reuse existing controls rather than adding duplicates. Set the exact Variable Name, not just the visible Text. Leave fields empty unless specified. Add ordinary caption labels beside inputs; their variable names do not matter unless listed. Keep event handlers empty for now.

Tables start with zero rows, the specified column order and no editable columns. Put the table and text area inside scroll panes. Read-only text fields use editable=false. Save All when finished.

## 1. ForgotPasswordFrame — new JFrame Form

This is a JFrame because it opens before login. Reuse it if already drawn.

| Control | Variable name | Text / setting |
| --- | --- | --- |
| Text Field | jTextFieldUsername | Empty; caption Username |
| Text Field | jTextFieldFullName | Empty; caption Full name |
| Button | jButtonResetPassword | Reset Password |
| Text Field | jTextFieldNewPassword | Empty, editable=false; caption New password |
| Label | jLabelStatus | Enter your registered username and full name. |
| Button | jButtonBackToLogin | Back to Login |

Add a title label saying Reset Password. The output is a normal Text Field so the new replacement password can be read and copied. No email/code controls are required. The old hashed password is not retrieved. This username/full-name reset is a limited classroom demonstration.

## 2. CataloguePanel — new JPanel Form

One shared catalogue is sufficient. A JPanel fits inside MainFrame. Everyone permitted to browse sees the list; only Admin sees the editor and can change data.

### Top/list section

| Control | Variable name | Text / setting |
| --- | --- | --- |
| Label | jLabelTitle | Products and Services |
| Text Field | jTextFieldSearch | Empty; caption Search |
| Button | jButtonSearch | Search |
| Button | jButtonRefresh | Refresh |
| Table | jTableCatalogue | Item ID, Item Name, Type, Category, Unit, Price (UGX), Status |

### Admin editor underneath the table

Drag a Panel and name it `jPanelEditor`. Put ALL the following controls and their captions inside it, so code can hide the whole editor from customers.

| Control | Variable name | Text / setting |
| --- | --- | --- |
| Text Field | jTextFieldItemName | Empty; caption Item name |
| Combo Box | jComboBoxType | Select type; Equipment; Service |
| Combo Box | jComboBoxCategory | Select category |
| Text Field | jTextFieldUnit | Empty; caption Unit, e.g. Each or Per computer |
| Text Field | jTextFieldPrice | Empty; caption Price (UGX) |
| Text Area | jTextAreaDescription | Empty; caption Description/specification; wrap lines and words |
| Check Box | jCheckBoxActive | Active; selected=false |
| Button | jButtonAddItem | Add Item |
| Button | jButtonUpdateItem | Update Selected |
| Button | jButtonClear | Clear |

Do not add delete, online-payment or PDF-preview controls. Unpriced draft entries stay inactive until Admin sets a price and specification. Customers never receive access to the editor's save operations.

## 3. Existing LoginFrame

Add one Button:

- Variable: `jButtonForgotPassword`
- Text: `Forgot Password?`

## 4. Existing RegisterFrame

Add a label saying `Gender (optional)` and a Combo Box named `jComboBoxGender`.

Model entries, one per line:

```text
Select gender (optional)
Female
Male
Other
Prefer not to say
```

Do not add a staff-role selector. Automatic login after successful registration is code only, with no new control. Leave the current account-type and organisation controls in place.

## 5. Existing MainFrame

Add only these three buttons to jPanelSidebar:

| Variable name | Text |
| --- | --- |
| jButtonCatalogue | Catalogue |
| jButtonAccountPdf | My Account PDF |
| jButtonHistoryPdf | My History PDF |

Keep all existing buttons and jPanelContent. No separate profile or report screen is needed. Code will apply role visibility, keep navigation reachable and use existing labels/content space for the welcome/logo presentation.

## 6. Existing RequestPanel

Add a label saying `Product / Service` and a Combo Box named `jComboBoxCatalogueItem`, containing only `Select product/service` initially. Place it near the category and request-type controls.

Change these existing properties, preserving variable names:

| Control | Change |
| --- | --- |
| jTextFieldUnitCost | editable=false |
| jLabelUnitCost | Text = Unit price (UGX) |
| jTextFieldDescription | editable=false |
| jLabelTotal | Text = Total (UGX) |

Keep quantity editable and keep all existing item/attachment controls. Code will populate descriptions and prices from the selected catalogue item, show the unit, calculate totals and save the historical price. Do not create another request form or additional price input.

## 7. Existing MyRequestsPanel

Add a Button named `jButtonRequestPdf`, Text `Save Request PDF`.

It will export the selected request and its available order/decision/delivery/service details as a consolidated document. The existing request table is sufficient; no report selector or new panel is needed.

## 8. Existing UserManagementPanel

Change jComboBoxRole's model to:

```text
Select role
Requester
Manager
Purchaser
Admin
```

Add two buttons:

| Variable name | Text |
| --- | --- |
| jButtonUsersPdf | Save Users PDF |
| jButtonSelectedUserPdf | Selected User PDF |

Only Admin will have the all-user screen/export permissions. The selected-user document can include that account and its procurement history; it must omit passwords/hashes. Keep the existing table and role-update controls.

## What the assistant will handle after design inspection

- Add Admin permissions and appoint the specified first Admin.
- Reuse the single CataloguePanel with its role-controlled editor.
- Add the catalogue/gender fields and preserve historic prices during data migration.
- Keep supplier costs internal and customer amounts fixed to saved catalogue prices. Reuse existing customer/staff quotation screens; update their captions and table headings in code and .form as needed, without more dragging.
- Implement automatic login after successful account creation.
- Implement the agreed simple replacement-password flow with comments.
- Produce branded account, history, user-directory and consolidated request PDFs through the new buttons; use the existing logo and standard file-save dialogs.
- Keep permitted sidebar controls reachable and avoid an empty opening screen using existing components.
- Compile, test, document, commit and push each completed step.

No payment records, payment receipts, email recovery, online payments or additional forms are included in this round. Delivery/service records can appear in the consolidated request PDF, but these are not proof of payment.

## User handoff

After saving the designs, provide the existing APPLICATION USERNAME to appoint as the first Admin, not a password. The Admin can fill actual UGX prices in the catalogue after implementation. Draft entries remain inactive until then; no invented prices or extra charges will be applied.
