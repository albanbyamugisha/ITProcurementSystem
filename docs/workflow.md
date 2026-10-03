# Procurement workflow

1. The customer registers as an Individual or Organisation. Registration creates a Requester account and signs them in.
2. The customer chooses Equipment or Service, selects catalogue items and quantities, and submits the request with optional notes and attachments.
3. My Orders displays the saved selling prices. The customer confirms or declines the order.
4. A Purchaser records supplier quotations. Supplier costs stay internal and do not change customer prices.
5. A Manager selects a supplier and approves or rejects a customer-confirmed order. A rejection requires a reason.
6. Equipment is received through Deliveries, with one unique serial number per unit. Partial deliveries track the remaining quantity. Inventory is created when units arrive.
7. Services use work status, notes and completion dates instead of inventory and serial numbers.
8. Customers track their own requests and notifications. PDFs open in the default viewer; saving a copy is optional.

## Administration

Admin manages users, departments and catalogue prices. Public registration cannot create staff. Access is checked in database methods as well as navigation. Customers cannot access another customer's records.

## Password recovery

The registered username and full name produce a temporary password. Logging in with it requires a different personal password and matching confirmation before protected access. Cancelling signs out. The temporary password cannot be reused afterward. This limited identity check is suitable for the assignment, not a production identity-verification service.

## Data integrity

Transactions keep requests, item lines, attachments, approvals and deliveries consistent. Unique constraints protect usernames, email addresses and equipment serial numbers. Saved selling prices remain historical snapshots. Invalid or unavailable catalogue selections must be refreshed before submission. Database parameters keep entered values separate from SQL commands.
