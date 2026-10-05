-- Run once on an existing database after checking for duplicate emails.
-- The main schema already includes UNIQUE for a newly created database.
-- Select it_procurement_db as the database for the statements that follow.
USE it_procurement_db;

-- This query should return no rows. Resolve duplicates before the ALTER below.
-- Normalise email text and count accounts so duplicate addresses can be found before adding
-- uniqueness.
SELECT LOWER(TRIM(email)) AS email_value, COUNT(*) AS account_count
-- Read the named table and apply the stated conditions to choose matching rows.
FROM users WHERE email IS NOT NULL AND TRIM(email) <> ''
-- Group equal normalised email addresses and show only groups containing more than one account.
GROUP BY LOWER(TRIM(email)) HAVING COUNT(*) > 1;

-- Prevent two accounts from using the same email, including simultaneous registrations.
-- NULL is still permitted for older accounts; registration code will require an email.
-- Change the existing table structure using the following definition; retain its saved rows.
ALTER TABLE users ADD CONSTRAINT uq_users_email UNIQUE (email);
