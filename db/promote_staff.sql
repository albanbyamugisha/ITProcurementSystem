-- Public registration always creates a Requester account.
-- A trusted local database administrator can use this to appoint the first staff member.
-- Register the intended person normally first, then enter their exact username below.
-- Leaving the username empty deliberately changes nothing. No password is included here.
-- Select it_procurement_db as the database for the statements that follow.
USE it_procurement_db;
-- Keep this empty until the intended existing username is supplied; empty input deliberately matches
-- no update.
SET @staff_username = '';
-- Choose the staff role to assign to the selected existing account.
SET @staff_role = 'Admin'; -- Choose Admin, Manager or Purchaser.

-- Update the selected account; the following WHERE conditions restrict the target and allowed role.
UPDATE users
-- Copy the chosen staff role into the matched account's role column.
SET role = @staff_role
-- Match only the exact username supplied in the staff_username variable.
WHERE username = @staff_username
  -- Prevent any role change when no username was supplied.
  AND @staff_username <> ''
  -- Accept only the three listed staff roles for this promotion script.
  AND @staff_role IN ('Admin', 'Manager', 'Purchaser');

-- Check the result, then ask the person to log out and back in.
-- Read the listed values; this query does not modify saved records.
SELECT user_id, username, full_name, role
-- Read the named table and apply the stated conditions to choose matching rows.
FROM users WHERE username = @staff_username;
