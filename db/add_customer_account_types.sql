-- Run once on the existing database to support individual and organisation customers.
USE it_procurement_db;
-- Existing accounts keep their department and are treated as organisation users.
ALTER TABLE users
    MODIFY department_id INT NULL,
    ADD account_type VARCHAR(20) NOT NULL DEFAULT 'Organisation',
    ADD organisation_name VARCHAR(150) NULL;
-- A personal request does not belong to a department either.
ALTER TABLE requests MODIFY department_id INT NULL;
