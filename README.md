# IT Procurement Request System

An individual Object Oriented Programming II project for Year 2, Semester 1 of the B.Sc. Software Engineering programme.

The project is being built step by step using Java, JDBC, MySQL and NetBeans. Code comments explain what each part does in simple language.

## Current progress

- Database schema and reverse-engineered ER diagram.
- Database login, session tracking, role-based navigation and logout.
- Request entry with database categories, input validation and exact money totals.
- Add Item, Remove Selected and Clear controls.
- Submit Request saves the request and its items in one transaction with Pending status.
- My Requests data query is ready; its NetBeans panel is the next design step.

## Tools

- Apache NetBeans IDE 31
- JDK 26 (current project configuration)
- XAMPP MySQL/MariaDB server
- MySQL Connector/J 26.7.0
- MySQL Workbench for the ER model

## Project files

- `src/itprocurementsystem/`: commented Java source code.
- `db/it_procurement_schema.sql`: database creation script.
- `db/test_user.sql`: repeatable setup for an IT department and requester test account.
- `db/it_procurement_er.mwb`: editable ER model.
- `db/it_procurement_er.png`: exported ER diagram.
- `db/`: PDF and SVG copies of the diagram.
- `nbproject/` and `build.xml`: NetBeans project configuration.

## Run the application

1. Open this project in NetBeans.
2. Start MySQL in XAMPP.
3. Execute `db/it_procurement_schema.sql` using your MySQL connection.
4. Download MySQL Connector/J 26.7.0 and put its JAR in a `lib` folder in this project. The driver is not included in Git.
5. Check Project Properties → Libraries. If necessary, add the JAR using Add JAR/Folder.
6. Run `ITProcurementSystem` to open the login window.

The local connection uses database `it_procurement_db`, server `localhost`, port `3306`, username `root` and an empty password. These are the local assignment settings; use appropriate credentials and connection security for any deployed system.

## Planned features

- Display submitted requests and their statuses.
- Vendor quotations and manager approvals.
- Delivery tracking and inventory records.

We will build the Swing screens using NetBeans Design view and commit progress as each step is completed.

## ER diagram

![IT procurement ER diagram](db/it_procurement_er.png)

## Try database login

1. For a fresh database, run `db/it_procurement_schema.sql`, then `db/test_user.sql` and `db/sample_categories.sql`.
2. Choose **Run Project** in NetBeans.
3. Sign in with username **requester** and password **Requester123!**.

The test account is an application user. It is separate from the MySQL root account, whose password is still empty. The seed script stores the SHA-256 hash and leaves any existing requester account unchanged. These are public demonstration credentials for this local assignment. SHA-256 follows the original brief; it is not a substitute for salted password hashing in a deployed application.

- Empty username, including spaces only: asks for a username.
- Empty password: asks for a password.
- Incorrect username or password: displays one generic failure message.
- Correct test credentials: displays a welcome message with the user's full name and role. The main window opens with the buttons allowed for that role.
- Database unavailable: displays a database error instead of saying the credentials are wrong.

The password field is cleared after each database login attempt. Passwords are compared exactly, including case and spaces.

## Checks for this step

All Java sources compiled with Java 8-compatible syntax and APIs. Eleven direct checks against the local database passed, covering correct credentials, username trimming, incorrect password, password case and spaces, missing user, SQL input, blank/missing values and hash length. Running the seed script twice left one IT department and one requester account. The Login button uses the same tested UserDAO method; the updated graphical flow should also be tried in NetBeans.

## Remembering the signed-in user

`Session.java` keeps the user ID, username, full name and role in private static fields. Simple getters let screens read those details. `UserDAO` fills them only after checking the password, and clears them before every new login attempt and on database errors. The Logout button calls `Session.clear()` before returning to login. Passwords are not stored in the session.

`static` makes these details shared within this one desktop application. `private` keeps other classes from assigning the fields directly. Closing the program loses the session; it does not change the database.
