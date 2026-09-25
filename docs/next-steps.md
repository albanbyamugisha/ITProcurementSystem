# Agreed work order

Completed:
- Registration with unique usernames/emails and required customer account type.
- Individuals have no department; organisations provide a name and may omit department.
- Equipment and Service request types are saved. Each request uses one type.
- Live quotation checks passed for both request types; temporary records were removed.

Current next step: create ApprovalPanel in NetBeans Design view, then implement manager review and decisions.

Still to design before connecting final decisions:
- Customer acceptance/decline is distinct from internal manager approval.
- Individuals must not be forced through an organisation department approval process.
- Organisation customers do not automatically require internal approval just because they are businesses.
- Approval data must identify the selected quotation, not just the request.

After this: delivery for equipment and completion for services. Services must not require serial numbers or inventory entries.
