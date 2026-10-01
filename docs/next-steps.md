# Current status

The design stage and planned application implementation are complete in code.

Implemented: navigation, registration, requests, attachments, quotations, customer decisions, staff review, deliveries, inventory, services, supporting-record management and private notifications.

Validation: Java compilation passes and the isolated MariaDB suite passes 49 checks, including all 12 actual JPanel constructors. The three JFrame windows and real desktop interactions still need a live run in NetBeans.

## Local handoff

- Start MySQL in XAMPP. Automatic startup was blocked because sudo requires the user's administrator password.
- Run Project. The main class creates the three new workflow tables without deleting data.
- If no Manager exists, register the intended staff member, then appoint them using `db/promote_staff.sql`. Only a trusted local administrator should do this.
- Follow the workflow in README.md with Requester, Purchaser and Manager accounts.
- Refresh the earlier Workbench ER model from the updated database; it predates the three new workflow tables.

Keep code beginner-friendly, explain new parts in comments, and commit and push each completed future change. Do not publish local credential scripts.
