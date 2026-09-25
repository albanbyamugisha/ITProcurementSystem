-- Run once on the existing database. Earlier requests were equipment requests.
USE it_procurement_db;
ALTER TABLE requests ADD request_type VARCHAR(20) NOT NULL DEFAULT 'Equipment';
-- Provide a service category without changing existing equipment categories.
INSERT INTO categories (category_name,description)
SELECT 'IT Services','Installation, maintenance, support and other IT services'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE category_name='IT Services');
