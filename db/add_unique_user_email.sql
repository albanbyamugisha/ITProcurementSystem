-- Run once on an existing database after checking for duplicate emails.
-- The main schema already includes UNIQUE for a newly created database.
USE it_procurement_db;

-- This query should return no rows. Resolve duplicates before the ALTER below.
SELECT LOWER(TRIM(email)) AS email_value, COUNT(*) AS account_count
FROM users WHERE email IS NOT NULL AND TRIM(email) <> ''
GROUP BY LOWER(TRIM(email)) HAVING COUNT(*) > 1;

-- Prevent two accounts from using the same email, including simultaneous registrations.
-- NULL is still permitted for older accounts; registration code will require an email.
ALTER TABLE users ADD CONSTRAINT uq_users_email UNIQUE (email);
