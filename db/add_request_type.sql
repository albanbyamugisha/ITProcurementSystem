-- Run once on the existing database. Earlier requests were equipment requests.
-- Select it_procurement_db as the database for the statements that follow.
USE it_procurement_db;
-- Change the existing table structure using the following definition; retain its saved rows.
ALTER TABLE requests ADD request_type VARCHAR(20) NOT NULL DEFAULT 'Equipment';
-- Provide a service category without changing existing equipment categories.
-- Insert values into the named columns; the following SELECT supplies the row to add.
INSERT INTO categories (category_name,description)
-- Read the listed values; this query does not modify saved records.
SELECT 'IT Services','Installation, maintenance, support and other IT services'
-- Insert only if the inner query finds no existing record with the same name.
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE category_name='IT Services');
