-- A test account for our local assignment project.
-- Run this after it_procurement_schema.sql has created the tables.
-- Test username: requester
-- Test password: Requester123!
-- This is an application account, separate from MySQL's root account.

USE it_procurement_db;

-- Create the IT department only if it is not already there.
-- SELECT supplies the value; NOT EXISTS checks that it has not been added before.
INSERT INTO departments (department_name)
SELECT 'IT'
WHERE NOT EXISTS (
    SELECT department_id FROM departments WHERE department_name = 'IT'
);

-- Add a requester account and connect it to the IT department.
-- The long text below is the SHA-256 hash of the test password.
-- It matches the result of PasswordUtil.hashPassword("Requester123!").
-- We save the hash in the table, not the original password.
INSERT INTO users (
    department_id, username, password_hash, full_name, email, role
)
SELECT department_id, 'requester',
       'a37e73c1817fba0aadb05392957c7aaaba5ef4b53d27527e66b368f1596e07ab',
       'Test Requester', 'requester@example.test', 'Requester'
FROM departments
WHERE department_name = 'IT'
  AND NOT EXISTS (
      SELECT user_id FROM users WHERE username = 'requester'
  )
ORDER BY department_id
LIMIT 1;

-- LIMIT 1 chooses just one department if duplicate IT departments already exist.
-- Running this script again will not duplicate or change the existing test account.
