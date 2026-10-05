-- Run once on the existing database to support individual and organisation customers.
-- Select it_procurement_db as the database for the statements that follow.
USE it_procurement_db;
-- Existing accounts keep their department and are treated as organisation users.
-- Change the existing table structure using the following definition; retain its saved rows.
ALTER TABLE users
    -- Store department id in department_id. Use a whole number. NULL is allowed when no value is supplied.
    MODIFY department_id INT NULL,
    -- Store whether the customer is an Individual or an Organisation in account_type. Allow up to 20
    -- characters. A value is required. Use 'Organisation' when an insert omits this column.
    ADD account_type VARCHAR(20) NOT NULL DEFAULT 'Organisation',
    -- Store the organisation name when the account represents a business or organisation in
    -- organisation_name. Allow up to 150 characters. NULL is allowed when no value is supplied.
    ADD organisation_name VARCHAR(150) NULL;
-- A personal request does not belong to a department either.
-- Change the existing table structure using the following definition; retain its saved rows.
ALTER TABLE requests MODIFY department_id INT NULL;
