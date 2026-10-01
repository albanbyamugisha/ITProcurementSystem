-- Public registration always creates a Requester account.
-- A trusted local database administrator can use this to appoint the first staff member.
-- Register the intended person normally first, then enter their exact username below.
-- Leaving the username empty deliberately changes nothing. No password is included here.
USE it_procurement_db;
SET @staff_username = '';
SET @staff_role = 'Manager'; -- Choose Manager or Purchaser.

UPDATE users
SET role = @staff_role
WHERE username = @staff_username
  AND @staff_username <> ''
  AND @staff_role IN ('Manager', 'Purchaser');

-- Check the result, then ask the person to log out and back in.
SELECT user_id, username, full_name, role
FROM users WHERE username = @staff_username;
