-- Sample equipment categories for our local assignment project.
-- Run this after it_procurement_schema.sql has created the categories table.
USE it_procurement_db;

-- Add each category only if its name is not already saved.
-- This lets us run the script again without adding the same sample names twice.
-- Existing categories and their descriptions are left unchanged.
INSERT INTO categories (category_name, description)
SELECT 'Computers', 'Desktop computers and laptops'
WHERE NOT EXISTS (
    SELECT category_id FROM categories WHERE category_name = 'Computers'
);

-- Printers includes equipment used to print documents.
INSERT INTO categories (category_name, description)
SELECT 'Printers', 'Printers and multifunction printing equipment'
WHERE NOT EXISTS (
    SELECT category_id FROM categories WHERE category_name = 'Printers'
);

-- Networking equipment connects computers and other devices.
INSERT INTO categories (category_name, description)
SELECT 'Networking Equipment', 'Routers, switches and wireless access points'
WHERE NOT EXISTS (
    SELECT category_id FROM categories WHERE category_name = 'Networking Equipment'
);

-- Show the saved IDs and names in the same order used by our dropdown.
SELECT category_id, category_name FROM categories ORDER BY category_name, category_id;
