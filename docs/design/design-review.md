# Design review — 1 October 2026

The 15 planned forms are designed. No additional dragging is required for the current scope.

| Forms | Purpose |
| --- | --- |
| LoginFrame, RegisterFrame | Sign in and create an account |
| MainFrame | Main window and navigation |
| RequestPanel, MyRequestsPanel | Create and view requests |
| QuotationPanel, CustomerQuotationsPanel, ApprovalPanel | Record quotations, customer decisions and staff review |
| DeliveryPanel, InventoryPanel | Equipment delivery and inventory |
| ServiceCompletionPanel | Service progress and completion |
| VendorPanel, DepartmentPanel, UserManagementPanel | Manage supporting records |
| NotificationsPanel | View notifications |

## Checks completed

- All 15 NetBeans form files parse, and their component names match Java fields.
- All 17 designed tables start with zero sample rows and have non-editable model columns.
- Temporary previews of all 15 saved layouts were rendered and visually inspected. These isolated previews do not run database actions or test navigation.
- Java sources compile using `javac --release 8`; only obsolete source/target warnings were reported.
- Removed unused designer controls; corrected captions, initial registration choices, text wrapping and read-only detail fields.
- Each form explains why it uses a JFrame or JPanel in its class comment.

This confirms the design stage, not completion of application functionality. Navigation, permissions, database operations and resizing with live data still need implementation or integration checks.
