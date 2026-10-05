# IT Procurement Request System

An individual Object Oriented Programming II project for Year 2, Semester 1 of B.Sc. Software Engineering. Individuals and organisations can request equipment and IT services at fixed UGX prices.

Built with Java Swing, JDBC and MySQL/MariaDB. Beginner-friendly comments explain the forms, event handlers and database operations. NetBeans `.java` and `.form` pairs remain editable in Design view.

## Author

BYAMUGISHA ALBAN

Student number: 2500603090

Registration number: 2025/BSE/062/PS

## Features

Account registration and login, equipment/service requests, a preset UGX catalogue, customer order confirmation, staff approvals, deliveries, inventory, service progress, account administration, notifications and PDF reports. The interface uses 17 NetBeans forms and a mandatory password-change dialog.

## Run in NetBeans

1. Start the local XAMPP MySQL service on port 3306. Database: `it_procurement_db`; database user: `root`; database password: empty.
2. On a fresh installation, run `db/it_procurement_schema.sql`. Run Project automatically applies missing catalogue/account/workflow additions and inserts missing catalogue entries. It preserves existing records and Admin edits. Much older installations may also need the earlier account-type/email/request-type migrations in `db/`.
3. Supply `lib/mysql-connector-j-26.7.0.jar` and `lib/itextpdf-5.5.5.jar`. Both are configured with relative paths and ignored by Git. The iText sources, Javadoc, PDF/A and Xtra JARs are not needed for these reports.
4. Use **Run Project**, starting `ITProcurementSystem`. Registration opens MainFrame automatically after the account is saved and its password is verified.
5. To appoint the first Admin, register that person normally, then set their exact application username and role `Admin` in `db/promote_staff.sql`. Its empty username deliberately changes nothing. An Admin can subsequently assign staff roles through Users. Log out and back in after changing a role.

The project uses JDK 26 in NetBeans. The application source also compiles with `javac --release 8`; that does not lower the JDBC driver's Java runtime requirement. The empty database password is separate from users' application passwords.

## Accounts and access

Registration requires an explicit Individual or Organisation account type. Usernames and emails are unique. Organisations provide a name and may choose a department. Gender is optional; for an organisation it describes the contact person. Public registration always creates a Requester, never staff.

| Role | Main screens |
| --- | --- |
| Requester | Requests, My Requests, My Orders |
| Purchaser | Supplier quotations, Deliveries, Services, Inventory, Vendors |
| Manager | Supplier review/approval, Inventory, Vendors |
| Admin | Users, Departments, Vendors, Inventory, catalogue editing and user reports |
| All four | Catalogue browsing, own notifications, own account/history PDF, Logout |

Navigation intentionally shows only permitted actions. The sidebar scrolls on small screens. DAO methods also check the saved role and session version, so hiding buttons is not the access control itself.

## Password recovery

Forgot Password checks the registered username and full name, creates a random **replacement** password, saves its hash and displays the replacement only after saving succeeds. The old password stops working and earlier sessions cannot perform protected actions. After login with this temporary password, a mandatory dialog asks for a new password and confirmation (8–128 characters). It rejects a mismatch or reuse of the temporary password. Closing/cancelling signs out; the user cannot enter MainFrame or access protected data until the change is saved. The requirement persists between runs, including earlier recorded resets. Roles do not change. The original password cannot be retrieved from its hash.

This deliberately simple classroom flow is not suitable for real deployment: knowledge of a person's username and full name is not proof of identity. The existing SHA-256 classroom password scheme remains in use. Passwords and hashes never appear in PDFs, account tables, notifications or audit messages.

## Catalogue and prices

The 15 starter entries cover computers, displays, printing, networking, power, storage, installation, maintenance, support, backups and training. See `docs/catalogue.md` for the preset prices and specifications.

Admin can add, edit or deactivate items. Customers select an active item and quantity; description and price fields are read-only. Submission re-reads and locks the catalogue record in MySQL. If its price or specification changed, the customer must remove and re-add the item to review the new details. Submitted descriptions, units and prices remain unchanged when the catalogue is later edited. Historical pre-catalogue records keep their original estimates and are labelled as legacy records.

No taxes, discounts or delivery charges are invented. Payment recording, receipts and online payments are outside this round.

## Complete workflow

1. **Requester:** select Equipment or Service, select catalogue items, enter quantities and add them. Optionally add notes/documents, then submit.
2. **Requester:** open My Orders, load the request and select its saved order row. Review the fixed prices, then confirm or decline. A decision is final; a declined/rejected order needs a new request.
3. **Purchaser:** collect internal supplier quotations for the request. These costs do not replace customer selling prices and are not shown on the customer screen/PDF.
4. **Manager:** load a customer-confirmed request, select a supplier quotation and approve or reject. Rejection requires a reason. Supplier collection may happen before customer confirmation, but approval requires confirmation.
5. **Purchaser, equipment:** record actual delivery dates and one unique serial number per unit. Partial deliveries leave outstanding quantities; final delivery completes the request. Inventory is created in the same transaction.
6. **Purchaser, service:** update work status and notes. Completed requires a completion date. Services do not create inventory or serial numbers.
7. **Requester:** use My Requests, My Orders and Notifications to follow progress and export the selected request.

Legacy approved/selected quotations remain intact. Unconfirmed legacy estimates cannot be confirmed as fixed-price catalogue orders; create a new catalogue request instead.

## PDF documents

- **My Account PDF:** current account details, including optional gender and organisation.
- **My History PDF:** the user's requests, saved totals, order decisions, deliveries and services.
- **View Request PDF:** selected request, items, customer decision, staff outcome and applicable delivery/service details.
- **View Users PDF:** Admin-only user directory.
- **Selected User PDF:** Admin-only selected account plus its procurement history.

All documents have the system logo on every page, document reference, Kampala generation time, UGX labels, repeated table headings and page numbers. The user directory uses landscape A4. Clicking a PDF button immediately creates a temporary preview and opens the default PDF viewer. Use the viewer’s Save As / Save a Copy command to keep it, or close it without saving. Temporary previews are removed when the application exits. A default PDF viewer must be installed. Every footer reads `2500603090 - BYAMUGISHA ALBAN - 2025/BSE/062/PS`. Reports are not proof of payment. Customer reports omit internal supplier costs and all reports omit passwords/hashes.

The bundled DejaVu font makes report rendering portable; its licence is beside it in `src/itprocurementsystem/resources/`. The supplied iText core JAR remains a local dependency.

## Supporting documents

Requests accept up to ten attachments, each at most 10 MB: PDF, PNG, JPG/JPEG, TXT, DOCX and XLSX. Originals stay untouched; submitted copies go to `~/.it-procurement/attachments`. A failed save removes its new copies.

Right-click a saved request in My Requests or the request selector in quotation/review screens to view its attachments. Purchasers load the request first. Customers can access only their own documents. Another computer needs access to both the database and stored attachment files.

## Forms and code

JFrame is used for LoginFrame, RegisterFrame, ForgotPasswordFrame and MainFrame because they need their own windows. The other 13 forms are JPanel screens inside MainFrame. Each form explains that choice in its comments. Keep `.java` and `.form` files together.

- `Session`, `UserDAO`, `RegistrationDAO`, `PasswordResetDAO`: account access.
- `RequestItem`, `QuotationItem`, `DeliveryUnit`: encapsulated data and validation.
- `CatalogueDAO`, `CatalogueSeed`: catalogue operations and preset starter entries.
- `RequestDAO`, `QuotationDAO`, `DecisionDAO`, `FulfilmentDAO`: request lifecycle.
- `ManagementDAO`, `AttachmentDAO`: supporting records and documents.
- `ReportDAO`, `ReportData`, `PdfReports`, `ReportActions`: authorised data, PDF drawing and preview opening.
- `Database`, `DatabaseSetup`, `FormSupport`: shared JDBC/setup/display helpers.

These demonstrate classes/objects, private fields and getters, inheritance from Swing, event-listener interfaces, and separation of form code from database code.

The Java source, SQL scripts and test runner include detailed comments in general English. Comments describe declarations, meaningful statements, validation, SQL column order, transactions, event callbacks and layout operations. Statements sharing a short line have their explanations grouped above that line; closing braces and continuation lines belong to the explanation of the enclosing statement.

For a first reading, follow `ITProcurementSystem` → `DBConnection` → `LoginFrame` → `UserDAO` → `Session` → `MainFrame`. Then follow a request through `RequestPanel`, `RequestItem` and `RequestDAO`. Form constructors connect actions; DAO classes validate and save; data classes hold related values. The comments above `initComponents` explain layout constants. NetBeans can replace comments inside generated sections when Design view regenerates them, so keep the handwritten explanations outside those sections as well.

## Database and ER diagram

There are now **21 tables**. The additions are `catalogue` and `order_decisions`; users also gain `gender` and `session_version`, and request items gain catalogue links and saved units. `customer_decisions` remains for legacy quotation decisions. `request_selections` records the internally selected supplier, and `service_progress` stores service work.

Run Project performs additive updates through `DatabaseSetup`. `db/add_catalogue_and_accounts.sql` provides the MariaDB manual additions; Run Project completes the named catalogue foreign key and catalogue seed. Backups in `db/backups/` are private and Git-ignored. Earlier Workbench exports are historical snapshots: reverse-engineer the current database and save an updated `.mwb` and PDF.

## Verification

Run `bash test/run-tests.sh` with a separate local test server on port **3307**. The script resets only `procurement_test` and refuses port 3306. Both JARs must be in `lib/`. It checks registration/duplicates, recovery and session expiry, role/ownership restrictions, fixed-price tampering/staleness, historical snapshots, order decisions, concurrent decisions/deliveries, attachment rollback and JPanel construction. PDF tests check permissions, pagination, repeated headings/logo resources and excluded password/supplier data. Sample PDFs are written under `/tmp/procurement-pdf-review/` for inspection.

The latest automated run passed 105 checks. PDF layouts and the login/password-change windows were also checked.

The comment-only revision was verified by compiling before and after with debug metadata disabled: all 175 class files were identical. Executable Java and SQL tokens were unchanged, all 17 form XML files parsed, and the test runner passed shell syntax validation. No application behaviour was changed by this revision.
