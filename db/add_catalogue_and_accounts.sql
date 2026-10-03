-- Additions for the local MariaDB classroom project. Existing records are retained.
USE it_procurement_db;
-- Optional contact-person gender; a higher session version expires older logins.
ALTER TABLE users ADD COLUMN IF NOT EXISTS gender VARCHAR(30) NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS session_version INT NOT NULL DEFAULT 0;
ALTER TABLE users ADD COLUMN IF NOT EXISTS must_change_password BOOLEAN NOT NULL DEFAULT FALSE;
CREATE TABLE IF NOT EXISTS catalogue (catalogue_id INT AUTO_INCREMENT PRIMARY KEY, seed_code VARCHAR(20) UNIQUE, item_name VARCHAR(100) NOT NULL, item_type VARCHAR(20) NOT NULL, category_id INT NOT NULL, unit VARCHAR(50) NOT NULL, description VARCHAR(255) NOT NULL, price DECIMAL(12,2) NOT NULL, active BOOLEAN NOT NULL DEFAULT FALSE, FOREIGN KEY(category_id) REFERENCES categories(category_id)) ENGINE=InnoDB;
-- NULL marks legacy request items. Never reprice them during migration.
ALTER TABLE request_items ADD COLUMN IF NOT EXISTS catalogue_id INT NULL;
ALTER TABLE request_items ADD COLUMN IF NOT EXISTS unit VARCHAR(50) NULL;
CREATE TABLE IF NOT EXISTS order_decisions (request_id INT PRIMARY KEY, customer_id INT NOT NULL, decision VARCHAR(20) NOT NULL, comments TEXT, decision_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(request_id) REFERENCES requests(request_id), FOREIGN KEY(customer_id) REFERENCES users(user_id)) ENGINE=InnoDB;
-- Run Project completes the named foreign key and adds the 15 demo catalogue entries.
-- Java checks metadata before altering a table, so the application also supports MySQL.
