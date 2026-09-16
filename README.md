# IT Procurement Request System

An individual Object Oriented Programming II project for Year 2, Semester 1 of the B.Sc. Software Engineering programme.

The project is being built step by step using Java, JDBC, MySQL and NetBeans. Code comments explain what each part does in simple language.

## Current progress

- Created the database schema with 16 related tables.
- Reverse-engineered the database into an ER diagram using MySQL Workbench.
- Added a shared `DBConnection` class.
- Tested the Java connection successfully with a message window.

The centered login form checks required input and verifies credentials against the users table. PasswordUtil calculates the password hash, and UserDAO performs the database query using PreparedStatement. Successful login currently displays a message; session tracking, role-based screens and procurement features are still to come.

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

## Run the current connection test

1. Open this project in NetBeans.
2. Start MySQL in XAMPP.
3. Execute `db/it_procurement_schema.sql` using your MySQL connection.
4. Download MySQL Connector/J 26.7.0 and put its JAR in a `lib` folder in this project. The driver is not included in Git.
5. Check Project Properties → Libraries. If necessary, add the JAR using Add JAR/Folder.
6. Run `ITProcurementSystem`. A successful test displays **Database connected successfully!**

The local connection uses database `it_procurement_db`, server `localhost`, port `3306`, username `root` and an empty password. These are the local assignment settings; use appropriate credentials and connection security for any deployed system.

## Planned features

- Remember the logged-in user and open the screen for their role.
- Equipment requests containing multiple items.
- Vendor quotations and manager approvals.
- Delivery tracking and inventory records.

We will build the Swing screens using NetBeans Design view and commit progress as each step is completed.

## ER diagram

![IT procurement ER diagram](db/it_procurement_er.png)

## Try database login

1. For a fresh database, run `db/it_procurement_schema.sql`, then `db/test_user.sql`.
2. Right-click `LoginFrame.java` in NetBeans and choose **Run File**. The project's main class still runs the separate database connection test.
3. Sign in with username **requester** and password **Requester123!**.

The test account is an application user. It is separate from the MySQL root account, whose password is still empty. The seed script stores the SHA-256 hash and leaves any existing requester account unchanged. These are public demonstration credentials for this local assignment. SHA-256 follows the original brief; it is not a substitute for salted password hashing in a deployed application.

- Empty username, including spaces only: asks for a username.
- Empty password: asks for a password.
- Incorrect username or password: displays one generic failure message.
- Correct test credentials: displays **Login successful!** The next screen has not been built yet.
- Database unavailable: displays a database error instead of saying the credentials are wrong.

The password field is cleared after each database login attempt. Passwords are compared exactly, including case and spaces.

## Checks for this step

All Java sources compiled with Java 8-compatible syntax and APIs. Eleven direct checks against the local database passed, covering correct credentials, username trimming, incorrect password, password case and spaces, missing user, SQL input, blank/missing values and hash length. Running the seed script twice left one IT department and one requester account. The Login button uses the same tested UserDAO method; the updated graphical flow should also be tried in NetBeans.
