# Agreed work order

## Design stage complete

All 15 planned forms are designed and reviewed. See [design review](design/design-review.md).
No further manual dragging is currently required. Keep the NetBeans .form and Java files together.

## Already implemented

- Registration with unique usernames/emails and required customer account type.
- Individuals have no department; organisations provide a name and may omit department.
- Equipment and Service request types are saved. Each request uses one type.
- Request lists and quotation entry; earlier live quotation checks passed for both request types.

## Next: implementation, one step at a time

1. Connect the remaining navigation and decide which staff can access management screens.
2. Implement customer quotation decisions and staff approval as separate actions. Store the selected quotation. Being an organisation must not automatically force an internal approval process.
3. Implement equipment deliveries and inventory, and service progress separately. Services must not require serial numbers or inventory entries.
4. Implement vendor, department and user management, notifications and request attachments.
5. Test the complete workflows, permissions, validation and panel sizing with realistic data.

Use beginner-friendly Java with comments explaining each new part. Commit and push every completed step. Keep local credential scripts out of Git.
