# IT Procurement Request System

An individual Object Oriented Programming II project for Year 2, Semester 1 of the B.Sc. Software Engineering programme.

The system lets individuals and organisations request IT equipment or services, review supplier quotations and follow their requests through approval and fulfilment. It is intended for general use, rather than being limited to a college.

Built with Java Swing, JDBC and MySQL/MariaDB. NetBeans `.form` files remain editable in Design view. Simple comments explain the forms, event handlers, data objects and database operations.

## Project status

**All 15 planned forms and their application workflows are implemented.** Java compilation and the latest isolated database test run passed. A full desktop run against the normal local database and an updated Workbench ER model remain to be checked.

See [the current handoff notes](docs/next-steps.md) and [workflow documentation](docs/workflow.md).

## Implemented

- Registration for individuals and organisations, with required account type and unique usernames/emails.
- Login, logout and role-based navigation.
- Equipment and service requests, item validation, exact decimal totals and supporting documents.
- Supplier quotations, customer acceptance/decline and separate staff approval/rejection.
- Partial equipment deliveries, unique serial numbers, inventory and assignment.
- Service progress and completion dates, without inventory or serial numbers.
- Vendor, department and user-role management.
- Private notifications and audit entries for workflow decisions.

All 15 planned forms are designed and connected. The final automated run passed **49 checks** against a separate MariaDB test database, including transaction rollback, ownership, duplicate registration, concurrent decisions/deliveries and all 12 JPanel constructors. Full desktop window interaction on the user's normal database still needs a live run; automated panel tests do not replace that check.

## Requirements

| Tool | Project setup |
| --- | --- |
| Java Development Kit | JDK 26 in the saved NetBeans configuration |
| IDE | Apache NetBeans with Java Swing GUI Builder support |
| Database | Local MySQL or MariaDB; this project uses XAMPP |
| JDBC driver | MySQL Connector/J, configured as `lib/mysql-connector-j-26.7.0.jar` |
| ER modelling | MySQL Workbench |

The JDBC JAR is not committed to Git. Add it locally before building.

## Run in NetBeans

Open the project folder containing `build.xml` and `nbproject/`, then follow these steps.

1. Start MySQL using the XAMPP control panel. The application uses `localhost:3306`, database `it_procurement_db`, user `root` and an empty database password.
2. For a new database, run `db/it_procurement_schema.sql` and `db/sample_categories.sql`. Existing installations keep their data. The main application adds the three workflow tables automatically; `db/add_workflow_tables.sql` is the equivalent manual script.
3. Put MySQL Connector/J in `lib/` and check Project Properties → Libraries. The configured filename is `mysql-connector-j-26.7.0.jar`.
4. Run the **ITProcurementSystem** main class using Run Project. Create an account or use an existing application account.
5. Public registration always creates a Requester. To appoint the first trusted staff member, register that person normally, then use `db/promote_staff.sql` with their exact username and chosen staff role. Subsequent role changes can be made by a manager in Users. Log out and back in after a role change.

The MySQL connection settings are in [DBConnection.java](src/itprocurementsystem/DBConnection.java):

| Setting | Value |
| --- | --- |
| Host | `localhost` |
| Port | `3306` |
| Database | `it_procurement_db` |
| Database username | `root` |
| Database password | Empty |

The empty password applies to the **database connection**. People using the application still register and sign in with their own username and password.

For an existing project database, Run Project adds only the three new workflow tables if missing. It does not apply the earlier account-type, email or request-type migrations; those separate scripts in `db/` are needed only for older database versions that lack those changes.

The saved NetBeans build targets JDK 26. Source compilation was also checked with `javac --release 8`; changing the source target does not change the JDBC driver's own Java requirements.

## Who uses each screen?

| Role | Screens |
| --- | --- |
| Requester | Requests, My Requests, My Quotations, Notifications |
| Purchaser | Quotations, Deliveries, Services, Inventory, Vendors, Notifications |
| Manager | Quotation review, Inventory, Vendors, Users, Departments, Notifications |

A role controls staff access. An account type describes an individual or organisation customer. Selecting Organisation does not create a staff account or impose an organisation department approval process. Individuals have no department; organisations supply a name and may leave department empty.

## Try the complete workflow

1. **Requester:** add items and submit an Equipment or Service request. Each request uses one type.
2. **Purchaser:** open Quotations, load the request, select a vendor, price every item and save. Add vendors in Vendors when needed.
3. **Requester:** in My Quotations, select the request and load quotations. Select a quotation to inspect its items, then accept or decline. Acceptance chooses one final quotation for the request.
4. **Manager:** load the customer-accepted request, select that same quotation and approve or reject it. Rejection requires a reason. This is the procurement system's staff authorisation to fulfil, separate from the customer's decision.
5. **Purchaser, equipment:** load the approved request in Deliveries. Select an item and add one serial number per received unit. Save with the actual delivery date. Partial deliveries keep the request open; the final units mark it Delivered. Inventory records are created in the same transaction.
6. **Purchaser, service:** load the approved request in Services. Save work status and notes; Completed requires a real completion date and marks the request Completed.
7. **Requester:** refresh My Requests and Notifications to see updates.

Saved decisions are final for that request. A rejected request requires a new request. A declined quotation stays declined, but the purchaser may offer another quotation while the request is still Quoted. The application does not take payments.

Panels preserve unfinished entries when navigating. Use Refresh on list screens to read recent database changes. Returning to an empty Quotation screen refreshes requests and vendors; Clear also reloads those choices.

## Supporting files

Requests accept up to ten documents, each at most 10 MB: PDF, PNG, JPG/JPEG, TXT, DOCX and XLSX. Choose File and Remove File work on the pending list. Submitting copies documents into `~/.it-procurement/attachments`; the originals are unchanged. Failed saves remove their new copies.

To open saved documents, select a request and right-click the request table in My Requests, or the request dropdown in quotation/review screens, then choose **View request attachments**. Purchasers must load the request first. A customer can view only their own request's documents. This is a local desktop assignment: another computer needs access to the stored files as well as the database.

## Forms

| Form type | Forms | Why it is used |
| --- | --- | --- |
| JFrame | LoginFrame, RegisterFrame, MainFrame | Each needs its own window with a title bar and close button. |
| JPanel | RequestPanel, MyRequestsPanel, QuotationPanel, CustomerQuotationsPanel, ApprovalPanel | Request and quotation screens share MainFrame's navigation. |
| JPanel | DeliveryPanel, InventoryPanel, ServiceCompletionPanel | Fulfilment screens appear inside the same main window. |
| JPanel | VendorPanel, DepartmentPanel, UserManagementPanel, NotificationsPanel | Supporting screens also use the main window. |

Keep each form's `.java` and `.form` files together so NetBeans can open its design. Application event code is kept outside the generated layout block.

## Object-oriented concepts used

- **Classes and objects:** a `RequestItem`, `QuotationItem` or `DeliveryUnit` represents one piece of application data.
- **Encapsulation:** private fields hold data, while constructors and methods validate and expose it.
- **Inheritance:** form classes extend Swing's `JFrame` or `JPanel` to use their existing window and container behaviour.
- **Interfaces:** event listeners implement methods that Swing calls when the user interacts with a control.
- **Separation of responsibilities:** forms display controls, model objects hold values, and DAO classes handle database operations. DAO means Data Access Object.

## Code map

- `Session`, `UserDAO`, `RegistrationDAO`: authentication, registration and remembered login.
- `RequestItem`, `QuotationItem`, `DeliveryUnit`: data objects with validation.
- `RequestDAO`, `QuotationDAO`, `DecisionDAO`, `FulfilmentDAO`, `ManagementDAO`, `AttachmentDAO`: SQL and business rules.
- `Database`: small JDBC helpers for parameters, rows, access checks, audit and notification records.
- `FormSupport`: shared table, dropdown and error-dialog code.
- `DatabaseSetup`: creates only the new workflow tables if missing.
- `*Frame`: independent windows. `*Panel`: screens displayed inside MainFrame.

## Database and ER model

The schema now has 19 tables. `customer_decisions` records accept/decline comments, `request_selections` identifies the one quotation chosen for a request, and `service_progress` stores service work. Joining approvals to request selections identifies the precise quotation reviewed.

The earlier Workbench `.mwb`, PNG, PDF and SVG files in `db/` are snapshots from before those three tables were added. Reverse-engineer the updated database to refresh that editable Workbench model. The current added relationships are documented in [workflow.md](docs/workflow.md).

## Tests

`test/itprocurementsystem/WorkflowTest.java` exercises real DAO transactions and panel construction. Use `bash test/run-tests.sh` with a separate local MySQL/MariaDB instance on port **3307**. The runner resets only the database named `procurement_test` on that test instance. It refuses port 3306. The JDBC JAR must be present in `lib/`.

The tests use fictional users and generate a registration password at runtime. The local staff credential script is ignored by Git. Database test data never goes into the user's normal `it_procurement_db`.


## Project folders

| Location | Contents |
| --- | --- |
| `src/itprocurementsystem/` | Java classes and NetBeans form designs |
| `db/` | Schema, migration scripts, sample categories and earlier ER exports |
| `docs/design/` | Design references, guides and the design review |
| `docs/workflow.md` | Request lifecycle, new relationships and transaction explanations |
| `test/` | Integration tests and their runner |
| `nbproject/` | NetBeans project configuration |
| `lib/` | Locally supplied JDBC driver |

## Common setup problems

- **Database connection fails:** start MySQL in XAMPP and check the database name and port. Run Project must be able to connect before the login window opens.
- **JDBC driver is missing:** add the configured Connector/J JAR under Project Properties → Libraries.
- **No categories appear:** run `db/sample_categories.sql` against the project database.
- **No suppliers appear:** a Manager or Purchaser should add a vendor in Vendors, then return to an empty quotation form or clear it to reload the choices.
- **A new role is not reflected in navigation:** log out and back in after the role change.
- **A saved attachment cannot open:** check that its file still exists in the application's attachment folder and that the computer has an application for that document type.

## Remaining manual checks

1. Start the normal MySQL service and run the application in NetBeans.
2. Follow the complete workflow above using the three roles, checking the windows and controls on your screen.
3. Reverse-engineer the updated database in Workbench and save fresh ER exports for all 19 tables.

Future changes should keep the code beginner-friendly, include explanatory comments, and be committed and pushed after each completed step.
