# Fixes and additions — discussion list

Started: 2 October 2026.

Status: planning only. Items below distinguish requests from recommendations. No application or database changes are authorised by this list alone; agree on the full list before implementation. The explicitly requested logo has been generated and saved as a design asset.

## Decisions accepted on 2 October 2026

The user said "Use your recommendations" and asked for the catalogue to be created. Record these as agreed direction: separate Admin role, fixed customer catalogue prices with internal supplier costs, no passwords/hashes in PDFs, optional gender choices, and manually recorded payments/receipts without online payment integration. A draft catalogue is saved in [catalogue-draft.md](catalogue-draft.md); its entries stay inactive until actual prices and specifications are set.

The user is still discussing additions before implementation. The first Admin username, actual UGX prices, tax/delivery-charge policy and email-delivery configuration remain outstanding. Do not infer these from the user's email or invent live values.

## 1. PDF documents and reports

Requested: use the added iText libraries to produce useful PDFs with the system logo on every document. Admin can view/export all users; a customer can view/export only their own records.

Proposed documents:

| Document | Suggested contents | Access |
| --- | --- | --- |
| Account summary | Name, username, email, gender if provided, role, account type, organisation and department where applicable | Own account; authorised admin can select another account |
| Request form | Request number/date, customer, products/services, quantities, saved UGX unit prices, totals and status | Customer's own; authorised staff |
| Customer quotation/order summary | Catalogue selling prices, customer decision and relevant staff decision | Customer's own; authorised staff |
| Delivery acknowledgement | Request, delivered items, quantities, serials and date; clearly labelled as goods received, not payment received | Customer's own; authorised staff |
| Service completion record | Services, status, work notes and completion date | Customer's own; authorised staff |
| Procurement history | Requests, accepted prices, decisions, deliveries and service completion over a date range | Customer's own; admin-wide report |
| User directory and operational reports | Selected user fields, requests, inventory and service summaries | Authorised admin/staff only |

Common template recommendation: logo, system name, document title/number, generation date and time, page numbers, readable tables, repeated table headings, and explicit UGX currency labels. Use a Save dialog and avoid overwriting a file without confirmation. Show empty reports clearly. Check access again when querying report data, not only when displaying buttons.

Requested inclusion of passwords needs correction: the current system stores a one-way SHA-256 hash, not the original password. Recommendation: exclude passwords and password hashes from every PDF, including admin reports. Do not add plain-text password storage or ask users to enter a password for printing. Show ordinary account details, and consider password change/reset as a separate future feature if needed. The user has accepted this recommendation; it is not implemented yet.

Payment receipts: the system currently has no payment records. Use procurement history, order summaries and delivery acknowledgements initially. If actual payment receipts are wanted, first specify payment recording, amount, method, payment date, reference, partial payments and outstanding balance. Do not label an unpaid request as paid.

## 2. System logo

Requested: generate and save an original system logo and put it on all PDFs.

Design direction: navy (#17324D) and teal (#008577), a technology/document/approval symbol, and the existing name IT Procurement Request System. Transparent PNG for use on white report pages. General business identity, with no college branding. Generated logo saved as `src/itprocurementsystem/resources/system-logo.png`. Adding the image to report code remains a later implementation step.

## 3. Admin access and customer privacy

Current roles are Requester, Manager and Purchaser; there is no separate Admin role. Manager currently manages users and departments.

Recommendation for discussion: introduce a distinct Admin role for user administration, catalogue/prices and all-user reporting, while Manager retains procurement review and Purchaser handles fulfilment. Moving existing management permissions requires an explicit role matrix and a safe way to appoint the first Admin. Registration must never allow self-selection of a staff role.

Customers should get My Profile and My Reports and see only their own details and history. Selecting another user ID must not bypass ownership checks. Admin-wide exports must omit credentials. Agree which operational reports each staff role needs.

## 4. Buttons and opening screen

Observed screenshot: Requester is signed in and sees Requests, My Requests, My Quotations, Notifications and Logout. MainFrame deliberately hides the staff buttons for this role. The screenshot is consistent with that logic, rather than evidence that every button is broken.

Recommended improvement: define each role's menu, add the agreed customer Profile, Catalogue and Reports actions, and open a useful home panel after login instead of an empty content area. Keep restricted staff actions unavailable to customers. Test that every permitted button stays reachable at normal and smaller window sizes. Do not simply show every staff button to everyone.

## 5. Preset product/service prices in UGX

Requested: the system sets prices; customers must not enter their own prices.

Recommendation: an admin-maintained catalogue containing an ID, name, Equipment/Service type, category, description, unit, UGX selling price and active/inactive state. Customers select catalogue entries and positive quantities. The application reads prices from the database and displays a read-only unit price and calculated total. Validate the current catalogue price on submission, even if a user tampers with the form.

Store the product/service name, unit and charged price on each submitted request item as a historical snapshot. Later catalogue price changes must not rewrite old requests or PDFs. Keep referenced catalogue items by deactivating them rather than deleting history. Show price changes clearly before a customer confirms a submission. Continue using BigDecimal for money. Proposed display: UGX 150,000; decide whether fractional UGX will be allowed before enforcing rounding.

Important workflow decision: customer selling prices and supplier quotations are different amounts. Recommended model: the catalogue fixes the customer price; Purchaser records supplier costs internally. Customer-facing documents must use the saved selling price, not silently replace it with a vendor cost. The existing customer quotation screen and acceptance step must be revised to show a customer offer/order at catalogue prices, or simplified if acceptance is unnecessary. Do not implement both price models without deciding their relationship.

Actual catalogue entries, units and UGX prices have not been supplied. Do not invent live prices. Any development sample amounts must be labelled as demo data and kept separate from the approved catalogue. Discuss whether tax, discounts, delivery charges or services priced by time/quantity are required before adding them.

## 6. Gender during registration

Requested: add gender when creating an account and include it in appropriate account details.

Recommendation: a dropdown with Female, Male, Other and Prefer not to say, with an initial prompt. Make disclosure optional, and leave existing users unspecified until they choose to update their profile. For an Organisation account this describes the contact person, not the organisation. Gender must not affect prices or permissions. Confirm the options and whether the course requires a mandatory selection before implementation.

## 7. PDF library configuration

Confirmed in nbproject/project.properties: iText 5.5.5 core, pdfa and xtra JARs, plus sources and Javadoc JARs, are on the compilation path using absolute machine-specific paths.

Recommendation: use the core runtime JAR for the initial text/table/logo PDFs; attach source/Javadoc archives as IDE references rather than runtime dependencies. Keep extra modules only when a chosen feature requires them. Use portable local lib/ references, and verify the chosen runtime version with the project JDK before implementation. No library configuration was changed during this planning step.

Official module reference: https://kb.itextpdf.com/it5kb/installing-itext-5-toolbox-for-java-developers

## 8. Forgotten-password recovery

New question: what happens if a user forgets the password?

Proposed addition: a Forgot Password action opens a pre-login recovery form. The user requests a one-time code at their registered email address, enters that code and a new password twice, and then signs in using the new password. The old password is never recovered, printed or emailed.

Use random, expiring, single-use codes; store only code hashes, limit attempts/resends, give neutral account-existence messages, and invalidate old sessions after reset. Proposed expiry is 10 minutes. Email requires a sender/provider configured locally. If unavailable, discuss verified admin-assisted recovery rather than an insecure public reset shortcut. No email credentials have been supplied or requested in chat.

See [the recovery form plan](design/password-recovery-guide.md). This is a new recommendation; email configuration and the precise delivery route remain to be settled.

## 9. Automatic login after registration

Explicitly requested: after successful account creation, open MainFrame directly rather than sending the new user to LoginFrame.

Plan: confirm registration committed, authenticate the newly created account using the supplied credentials while they are still in memory, populate Session from the saved account, open MainFrame and close registration. Clear temporary password values afterward. Never open a session before creation succeeds; duplicate-user, validation and database errors must not create a session. If creation succeeds but automatic login fails, say the account was created and offer ordinary login instead of encouraging duplicate registration. The new account remains a Requester.

Automatic login after registration is distinct from password recovery: recommend normal login after a reset. No new registration-success form is needed.

## Proposed implementation order, after discussion

1. Agree on roles, customer selling prices versus supplier costs, report scope and gender options.
2. Update data structure for catalogue, saved prices, gender, payments and secure password reset/session invalidation; migrate existing data without rewriting historic amounts.
3. Prepare the remaining form designs; implement registration auto-login, password recovery, profile, catalogue, request entry and navigation, keeping NetBeans forms editable.
4. Update quotation/acceptance logic and access checks to fit the chosen price model.
5. Add the logo-based PDF template and individual report actions.
6. Add the agreed manual payment records and receipts; never mark a document paid before a payment is saved.
7. Test access restrictions, price integrity, old records, gender choices, PDF pagination and smaller-screen navigation; update documentation and commit each completed step.

More fixes and additions can be appended to this list before implementation starts.
