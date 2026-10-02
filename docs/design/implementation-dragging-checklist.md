# Complete NetBeans dragging checklist

Prepared: 2 October 2026. Design preparation only; do not write event handlers yet.

## Before starting

- Create all forms in package `itprocurementsystem`.
- If a form/control below already exists, reuse it. Do not create duplicates.
- Change **Variable Name** using Change Variable Name or Properties → Code. Changing the visible Text property does not rename a variable.
- Use Swing controls, not the AWT controls at the bottom of the Palette.
- Ordinary text/password fields start empty unless a default is specified.
- For each table: open its model editor, set the listed columns in that order, set row count to 0, and uncheck Editable for every column. Default column types can remain Object; code will supply data.
- Keep tables and text areas inside scroll panes. Set text areas lineWrap=true and wrapStyleWord=true.
- A field marked read-only uses editable=false, not enabled=false, so its contents can still be selected/copied.
- Add readable labels for every field. Names explicitly listed below must match exactly. Other static caption labels can keep their generated names.
- No Absolute Layout is needed. Align controls with NetBeans' normal layout guides.
- Save All after each form. Do not rename existing variables unless explicitly requested below.
- Code will load records, fill the logo, centre windows, show the correct role's controls and explain each part in comments. Do not type live catalogue prices, user accounts or passwords into Design view.

## New forms: one JFrame and six JPanels

### 1. ForgotPasswordFrame — JFrame Form

Purpose: reset is available before login, so this form needs its own window. Reuse it if already created. Suggested size: 500 × 330.

| Control | Variable name | Text / property |
| --- | --- | --- |
| Label | jLabelTitle | Reset Password |
| Text Field | jTextFieldUsername | Empty; caption Username |
| Text Field | jTextFieldFullName | Empty; caption Full name |
| Button | jButtonResetPassword | Reset Password |
| Text Field | jTextFieldNewPassword | Empty; read-only; caption Your new password |
| Label | jLabelStatus | Enter your registered username and full name. |
| Button | jButtonBackToLogin | Back to Login |

Use a normal Text Field for the displayed replacement password, as requested. This is a classroom demonstration using username/full-name matching, not email recovery. Remove unused email/code controls if you already drew the previous design. The form will display a newly reset password, not retrieve the original hash.

### 2. HomePanel — JPanel Form

Purpose: show a useful first screen inside MainFrame. Suggested size: 850 × 550.

| Control | Variable name | Text / property |
| --- | --- | --- |
| Label | jLabelLogo | Leave text empty; reserve about 240 × 90 for the existing logo |
| Label | jLabelTitle | Home |
| Label | jLabelWelcome | Welcome |
| Label | jLabelRole | Role |
| Label | jLabelRequestCount | 0; caption Requests |
| Label | jLabelPendingCount | 0; caption Awaiting action |
| Label | jLabelCompletedCount | 0; caption Fulfilled requests |
| Label | jLabelUnreadCount | 0; caption Unread notifications |
| Table | jTableRecentRequests | Request ID, Customer, Type, Status, Date |
| Button | jButtonRefresh | Refresh |

Arrange the four counts in one row or two rows above the table. Code will show only records allowed for the current role.

### 3. ProfilePanel — JPanel Form

Purpose: view/update the current user's profile inside MainFrame. Suggested size: 750 × 550.

| Control | Variable name | Text / property |
| --- | --- | --- |
| Label | jLabelTitle | My Profile |
| Text Field | jTextFieldUsername | Read-only; caption Username |
| Text Field | jTextFieldFullName | Caption Full name |
| Text Field | jTextFieldEmail | Caption Email |
| Combo Box | jComboBoxGender | Select gender (optional), Female, Male, Other, Prefer not to say |
| Text Field | jTextFieldAccountType | Read-only; caption Account type |
| Text Field | jTextFieldRole | Read-only; caption Role |
| Text Field | jTextFieldOrganisationName | Caption Organisation name |
| Combo Box | jComboBoxDepartment | No department (optional) |
| Button | jButtonSaveProfile | Save Changes |
| Button | jButtonRefresh | Refresh |
| Button | jButtonExportProfile | Save Profile PDF |
| Label | jLabelStatus | Empty |

Do not add a password field or role selector. Code will enable organisation fields only when applicable and will keep customer identity/role fields protected. Gender describes the contact person for organisation accounts.

### 4. CataloguePanel — JPanel Form

Purpose: browse active products/services and their fixed customer prices. Suggested size: 900 × 550.

| Control | Variable name | Text / property |
| --- | --- | --- |
| Label | jLabelTitle | Products and Services |
| Text Field | jTextFieldSearch | Caption Search |
| Combo Box | jComboBoxType | All types, Equipment, Service |
| Combo Box | jComboBoxCategory | All categories |
| Button | jButtonSearch | Search |
| Button | jButtonShowAll | Show All |
| Button | jButtonRefresh | Refresh |
| Table | jTableCatalogue | Item ID, Item Name, Type, Category, Unit, Price (UGX) |
| Text Area | jTextAreaDescription | Read-only; caption Selected item details |
| Label | jLabelCount | Items: 0 |

There is no editable customer price field. The existing RequestPanel will be used to request catalogue items.

### 5. CatalogueManagementPanel — JPanel Form

Purpose: Admin maintains specifications and selling prices. Suggested size: 950 × 700.

| Control | Variable name | Text / property |
| --- | --- | --- |
| Label | jLabelTitle | Manage Catalogue |
| Text Field | jTextFieldSearch | Caption Search catalogue |
| Button | jButtonSearch | Search |
| Button | jButtonRefresh | Refresh |
| Table | jTableCatalogue | Item ID, Item Name, Type, Category, Unit, Price (UGX), Status |
| Label | jLabelSelectedItem | Selected item: None |
| Text Field | jTextFieldItemName | Caption Item name |
| Combo Box | jComboBoxType | Select type, Equipment, Service |
| Combo Box | jComboBoxCategory | Select category |
| Text Field | jTextFieldUnit | Caption Unit, e.g. Each or Per computer |
| Text Field | jTextFieldPrice | Empty; caption Selling price (UGX) |
| Text Area | jTextAreaDescription | Caption Specification / service scope |
| Check Box | jCheckBoxActive | Active; selected=false |
| Button | jButtonAddItem | Add Item |
| Button | jButtonUpdateItem | Update Selected |
| Button | jButtonClear | Clear |
| Label | jLabelStatus | Empty |

Place the list above the editing fields, with action buttons at the bottom. The Active checkbox handles activation/deactivation, so no Delete button is required. A draft may stay inactive without a price. Activation requires a valid price and specification. Later price edits must not alter historical order amounts.

### 6. ReportsPanel — JPanel Form

Purpose: one shared PDF screen; report choices and data access depend on role. Suggested size: 850 × 620.

| Control | Variable name | Text / property |
| --- | --- | --- |
| Label | jLabelTitle | Reports and Documents |
| Combo Box | jComboBoxReportType | See choices below |
| Combo Box | jComboBoxUser | My account |
| Combo Box | jComboBoxRequest | Select request |
| Combo Box | jComboBoxPayment | Select payment |
| Text Field | jTextFieldDateFrom | Empty; caption From date (YYYY-MM-DD, optional) |
| Text Field | jTextFieldDateTo | Empty; caption To date (YYYY-MM-DD, optional) |
| Button | jButtonLoadPreview | Load Preview |
| Button | jButtonRefresh | Refresh Choices |
| Table | jTableReport | Detail, Value (temporary headings; code adapts them per report) |
| Button | jButtonExportPdf | Save PDF |
| Button | jButtonOpenPdf | Open Last PDF |
| Label | jLabelStatus | Select a report type. |

Report-type choices, one per line in the model editor:

```text
Select report
Account Summary
User Directory
Request Form
Customer Order Summary
Procurement History
Delivery Acknowledgement
Service Completion Record
Inventory Summary
Payment Receipt
Payment History
```

Code will hide unavailable report types and disable irrelevant filters. Only Admin gets all-user reporting. Customer reports are restricted to that customer's account, even if they alter a user ID. The payment selector identifies the exact saved payment for a receipt. No manually dragged File Chooser, PDF viewer or extra report JFrame is needed: Java opens the standard Save dialog and the saved PDF.

### 7. PaymentsPanel — JPanel Form

Purpose: customers view their payments; Admin records actual payments and produces receipts. This does not process online payments. Suggested size: 900 × 700.

| Control | Variable name | Text / property |
| --- | --- | --- |
| Label | jLabelTitle | Payments |
| Combo Box | jComboBoxRequest | Select request |
| Button | jButtonLoadPayments | Load Payments |
| Button | jButtonRefresh | Refresh |
| Label | jLabelCustomer | Customer: — |
| Text Field | jTextFieldOrderTotal | 0.00; read-only; caption Order total (UGX) |
| Text Field | jTextFieldTotalPaid | 0.00; read-only; caption Paid (UGX) |
| Text Field | jTextFieldBalance | 0.00; read-only; caption Balance (UGX) |
| Table | jTablePayments | Payment ID, Date, Amount (UGX), Method, Reference, Recorded By |
| Panel | jPanelPaymentEntry | Container for the six entry controls below |
| Text Field | jTextFieldAmount | Inside entry panel; caption Amount received (UGX) |
| Combo Box | jComboBoxMethod | Inside entry panel; Select method, Cash, Mobile Money, Bank Transfer |
| Text Field | jTextFieldPaymentDate | Inside entry panel; caption Payment date (YYYY-MM-DD) |
| Text Field | jTextFieldReference | Inside entry panel; caption Reference |
| Button | jButtonRecordPayment | Inside entry panel; Record Payment |
| Button | jButtonClear | Inside entry panel; Clear Entry |
| Button | jButtonExportReceipt | Outside entry panel; Save Selected Receipt |
| Label | jLabelStatus | Empty |

Code hides jPanelPaymentEntry from customers; it also checks permission in the database method. Admin enters payments already received; clicking Record Payment is not proof that a bank/mobile transfer happened. Customers can view/export their own saved receipts. Do not add editable rows, Delete Payment or Change Balance buttons.

## Changes to existing forms

### LoginFrame

Add a Button: `jButtonForgotPassword`, Text `Forgot Password?`, near the login/register actions. Reuse it if already added.

### RegisterFrame

Add a Label: `jLabelGender`, Text `Gender (optional)`.

Add a Combo Box: `jComboBoxGender`, with:

```text
Select gender (optional)
Female
Male
Other
Prefer not to say
```

Keep existing account-type controls. Do not add a staff-role selector. Automatic login after successful registration is a code change, with no new form/control needed. Keep the existing variable spelling jTextFieldOrgaisationName until code is updated; do not rename it manually.

### MainFrame

Add these six buttons to jPanelSidebar. Do not remove or rename existing buttons.

| Variable name | Text |
| --- | --- |
| jButtonHome | Home |
| jButtonProfile | My Profile |
| jButtonCatalogue | Catalogue |
| jButtonManageCatalogue | Manage Catalogue |
| jButtonReports | Reports |
| jButtonPayments | Payments |

Use the same width and spacing as existing navigation. It is fine for Design view to show all buttons. Code will choose the role's allowed buttons, show HomePanel on login, and make navigation scrollable if required. A customer will not see admin/staff controls.

### RequestPanel

Add a Label `jLabelCatalogueItem`, Text `Product / Service`, and a Combo Box `jComboBoxCatalogueItem`, initially containing `Select product/service`. Place them after the request type/category controls.

Add a Label `jLabelUnit`, Text `Unit`, and a Label `jLabelUnitValue`, Text `—`, next to the quantity control.

Keep existing variables; change only these properties:

| Existing control | Change |
| --- | --- |
| jLabelUnitCost | Text = Unit price (UGX) |
| jTextFieldUnitCost | editable = false; keep name |
| jTextFieldDescription | editable = false; populated from the catalogue |
| jLabelTotal | Text = Total (UGX) |
| jTextFieldTotal | Keep read-only |

Keep category/type dropdowns, quantity, notes, item buttons and attachments. Preserve the existing jTableItems column order; change only the visible price headings to Unit Price (UGX) and Line Total (UGX) if needed. Do not create another request form.

### UserManagementPanel

Change the jComboBoxRole model to:

```text
Select role
Requester
Manager
Purchaser
Admin
```

Do not add a second users screen. The existing table can still list accounts; role permissions will move to the agreed Admin role in code. No password column is to be added.

### CustomerQuotationsPanel — reuse for customer order confirmation

Keep its class and component variable names. Change these visible texts:

| Control | New text |
| --- | --- |
| jLabelTitle | My Orders |
| jButtonLoadQuotations | Load Order |
| jLabelItems | Order items |
| jLabelDetails | Order details |
| jButtonAccept | Confirm Order |
| jButtonDecline | Decline Order |
| jLabelDecision | Decision: No order selected |

Keep four columns in jTableQuotations, with headings `Order ID`, `Total (UGX)`, `Decision`, `Date`. Keep four columns in jTableQuotationItems: `Description`, `Quantity`, `Unit Price (UGX)`, `Line Total (UGX)`.

In MainFrame, change only the visible text of jButtonMyQuotations to `My Orders`. Do not rename that variable.

This separates the customer order at saved catalogue prices from internal supplier quotations. Implementation must preserve existing historical amounts and revise the decision relationships accordingly. A customer will never see a supplier cost substituted for their agreed selling price.

### QuotationPanel and ApprovalPanel

Retain all existing controls. They remain internal staff screens. Make price captions/table headings explicitly UGX, without reordering columns or changing variable names. Use `Supplier Quotation` wording for titles where appropriate. Manager will choose the supplier quotation internally; the customer's separate order confirmation concerns the fixed customer total.

## Design order

1. ForgotPasswordFrame, then the LoginFrame/RegisterFrame additions.
2. HomePanel and ProfilePanel.
3. CataloguePanel and CatalogueManagementPanel, then RequestPanel additions.
4. ReportsPanel and PaymentsPanel.
5. MainFrame additions, UserManagementPanel role choice, customer-order captions and staff price captions.

No additional form is needed for the logo, PDF saving, auto-login or the Admin role itself. The existing logo will be loaded in code. For any controls already drawn, check their names instead of adding them again.

## Information needed later, not before dragging

- Exact existing application username to appoint as the first Admin.
- Approved item specifications/units and actual UGX selling prices. The draft catalogue remains inactive until these are entered.
- Any extra-charge/tax policy if required. No invented charges or online-payment integration are planned.

Save All when finished. The next step is to inspect the saved designs, then implement the agreed behavior with beginner-friendly comments.
