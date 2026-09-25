# Quotation verification

Live database checks passed for both Equipment and Service requests:

- Load the two original requested items.
- Reject items without entered prices.
- Reject a nonexistent vendor inside the save transaction; no quotation remains and the request stays Pending.
- Save two priced items with exact total 260.80 and quotation status Submitted.
- Change the parent request status to Quoted.
- Reject another quotation after the request is Approved.
- Reject reading the open request list when logged out.

The checks created temporary requests and removed their quotation items, quotations, request items and requests afterward. Existing customer requests were not edited.
These are DAO/database checks; they do not replace a visual test of the Swing windows.
