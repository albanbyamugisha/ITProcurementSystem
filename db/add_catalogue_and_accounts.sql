-- Additions for the local MariaDB classroom project. Existing records are retained.
-- Select it_procurement_db as the database for the statements that follow.
USE it_procurement_db;
-- Optional contact-person gender; a higher session version expires older logins.
-- Change the existing table structure using the following definition; retain its saved rows.
ALTER TABLE users ADD COLUMN IF NOT EXISTS gender VARCHAR(30) NULL;
-- Change the existing table structure using the following definition; retain its saved rows.
ALTER TABLE users ADD COLUMN IF NOT EXISTS session_version INT NOT NULL DEFAULT 0;
-- Change the existing table structure using the following definition; retain its saved rows.
ALTER TABLE users ADD COLUMN IF NOT EXISTS must_change_password BOOLEAN NOT NULL DEFAULT FALSE;
-- Create catalogue only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS catalogue (
    -- Store catalogue id in catalogue_id. Use a whole number. MySQL generates the next ID automatically.
    -- This is the unique, non-NULL identifier for each row.
    catalogue_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store the stable code used to prevent duplicate startup catalogue entries in seed_code. Allow up to
    -- 20 characters. NULL is allowed when no value is supplied. Non-NULL values cannot be shared by two
    -- records.
    seed_code VARCHAR(20) UNIQUE,
    -- Store item name in item_name. Allow up to 100 characters. A value is required.
    item_name VARCHAR(100) NOT NULL,
    -- Store item type in item_type. Allow up to 20 characters. A value is required.
    item_type VARCHAR(20) NOT NULL,
    -- Store category id in category_id. Use a whole number. A value is required.
    category_id INT NOT NULL,
    -- Store the pricing unit, such as Each or Per computer in unit. Allow up to 50 characters. A value is
    -- required.
    unit VARCHAR(50) NOT NULL,
    -- Store description in description. Allow up to 255 characters. A value is required.
    description VARCHAR(255) NOT NULL,
    -- Store the preset selling price in UGX in price. Use an exact decimal with ten digits before and two
    -- after the decimal point. A value is required.
    price DECIMAL(12,2) NOT NULL,
    -- Store whether the catalogue item is available for new customer selections in active. Use a true-or-
    -- false value. A value is required. Use FALSE when an insert omits this column.
    active BOOLEAN NOT NULL DEFAULT FALSE,
    -- Link category_id to categories.category_id. A non-NULL value must refer to an existing parent
    -- record.
    FOREIGN KEY(category_id) REFERENCES categories(category_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE=InnoDB;
-- NULL marks legacy request items. Never reprice them during migration.
-- Change the existing table structure using the following definition; retain its saved rows.
ALTER TABLE request_items ADD COLUMN IF NOT EXISTS catalogue_id INT NULL;
-- Change the existing table structure using the following definition; retain its saved rows.
ALTER TABLE request_items ADD COLUMN IF NOT EXISTS unit VARCHAR(50) NULL;
-- Create order_decisions only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS order_decisions (
    -- Store request id in request_id. Use a whole number. This is the unique, non-NULL identifier for each
    -- row.
    request_id INT PRIMARY KEY,
    -- Store the customer who made the order or quotation decision in customer_id. Use a whole number. A
    -- value is required.
    customer_id INT NOT NULL,
    -- Store decision in decision. Allow up to 20 characters. A value is required.
    decision VARCHAR(20) NOT NULL,
    -- Store comments in comments. Allow a longer text value. NULL is allowed when no value is supplied.
    comments TEXT,
    -- Store decision date in decision_date. Store a date and time. NULL is allowed when no value is
    -- supplied. Use CURRENT_TIMESTAMP when an insert omits this column.
    decision_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Link request_id to requests.request_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY(request_id) REFERENCES requests(request_id),
    -- Link customer_id to users.user_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY(customer_id) REFERENCES users(user_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE=InnoDB;
-- Run Project completes the named foreign key and adds the 15 preset catalogue entries.
-- Java checks metadata before altering a table, so the application also supports MySQL.
