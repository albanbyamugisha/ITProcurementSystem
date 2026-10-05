-- Sample equipment categories for the local assignment project.
-- Run this after it_procurement_schema.sql has created the categories table.
-- Select it_procurement_db as the database for the statements that follow.
USE it_procurement_db;

-- Add each category only if its name is not already saved.
-- This allows rerunning the script again without adding the same sample names twice.
-- Existing categories and their descriptions are left unchanged.
-- Insert values into the named columns; the following SELECT supplies the row to add.
INSERT INTO categories (category_name, description)
-- Read the listed values; this query does not modify saved records.
SELECT 'Computers', 'Desktop computers and laptops'
-- Insert only if the inner query finds no existing record with the same name.
WHERE NOT EXISTS (
    -- Read the listed values; this query does not modify saved records.
    SELECT category_id FROM categories WHERE category_name = 'Computers'
);

-- Printers includes equipment used to print documents.
-- Insert values into the named columns; the following SELECT supplies the row to add.
INSERT INTO categories (category_name, description)
-- Read the listed values; this query does not modify saved records.
SELECT 'Printers', 'Printers and multifunction printing equipment'
-- Insert only if the inner query finds no existing record with the same name.
WHERE NOT EXISTS (
    -- Read the listed values; this query does not modify saved records.
    SELECT category_id FROM categories WHERE category_name = 'Printers'
);

-- Networking equipment connects computers and other devices.
-- Insert values into the named columns; the following SELECT supplies the row to add.
INSERT INTO categories (category_name, description)
-- Read the listed values; this query does not modify saved records.
SELECT 'Networking Equipment', 'Routers, switches and wireless access points'
-- Insert only if the inner query finds no existing record with the same name.
WHERE NOT EXISTS (
    -- Read the listed values; this query does not modify saved records.
    SELECT category_id FROM categories WHERE category_name = 'Networking Equipment'
);

-- Show the saved IDs and names in the same order used by the dropdown.
-- Read the listed values; this query does not modify saved records.
SELECT category_id, category_name FROM categories ORDER BY category_name, category_id;
