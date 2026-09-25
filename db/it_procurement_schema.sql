-- IT Procurement Request System
-- This database stores equipment requests, quotations, approvals and deliveries.

-- Create our database if it is not already there.
CREATE DATABASE IF NOT EXISTS it_procurement_db;

-- Use this database for all the tables below.
USE it_procurement_db;

-- A primary key gives each record its own number.
-- AUTO_INCREMENT lets MySQL generate that number for us.
-- A foreign key connects a record to a record in another table.
-- NOT NULL means we must provide a value.
-- We create the parent tables first because other tables refer to them.
-- Uses InnoDb engine for transaction support and foreign key constraints.


-- 1. Departments
-- A department can have several users and equipment requests.
CREATE TABLE IF NOT EXISTS departments (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL
) ENGINE = InnoDB;


-- 2. Users
-- Each user belongs to one department.
-- The role will be Requester, Manager or Purchaser.
-- password_hash stores the result of password hashing, not the actual password.
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    department_id INT NULL,
    -- Individuals need no department; organisations may omit it too.
    account_type VARCHAR(20) NOT NULL DEFAULT 'Organisation',
    organisation_name VARCHAR(150),
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    -- Each registered email must belong to only one account.
    email VARCHAR(100) UNIQUE,
    role VARCHAR(20) NOT NULL,

    FOREIGN KEY (department_id) REFERENCES departments(department_id)
) ENGINE = InnoDB;


-- 3. Categories
-- Categories group similar equipment, such as computers and printers.
CREATE TABLE IF NOT EXISTS categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
) ENGINE = InnoDB;


-- 4. Vendors
-- These are the suppliers who give us quotations and supply equipment.
CREATE TABLE IF NOT EXISTS vendors (
    vendor_id INT AUTO_INCREMENT PRIMARY KEY,
    vendor_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(30),
    email VARCHAR(100),
    address VARCHAR(255)
) ENGINE = InnoDB;


-- 5. Requests
-- This table stores the main details of each request.
-- requester_id tells us which user submitted it.
-- department_id records the department making the request.
-- Status can be Pending, Quoted, Approved, Rejected or Delivered.
CREATE TABLE IF NOT EXISTS requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    requester_id INT NOT NULL,
    department_id INT NULL,
    -- A request contains equipment OR services; use separate requests for each type.
    request_type VARCHAR(20) NOT NULL DEFAULT 'Equipment',
    request_status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,

    FOREIGN KEY (requester_id) REFERENCES users(user_id),
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
) ENGINE = InnoDB;


-- 6. Request items
-- One request can contain several different items.
-- Each row describes one type of item and the quantity needed.
-- estimated_cost is the estimated price of ONE unit.
-- DECIMAL(12,2) stores an amount with two decimal places.
CREATE TABLE IF NOT EXISTS request_items (
    request_item_id INT AUTO_INCREMENT PRIMARY KEY,
    request_id INT NOT NULL,
    category_id INT NOT NULL,
    item_description VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    estimated_cost DECIMAL(12,2) NOT NULL,

    FOREIGN KEY (request_id) REFERENCES requests(request_id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
) ENGINE = InnoDB;


-- 7. Quotations
-- A vendor gives a quotation for a request.
-- One request can receive quotations from different vendors.
-- quoted_amount is the total price of all items in that quotation.
-- Status can be Submitted, Accepted or Rejected.
CREATE TABLE IF NOT EXISTS quotations (
    quotation_id INT AUTO_INCREMENT PRIMARY KEY,
    request_id INT NOT NULL,
    vendor_id INT NOT NULL,
    quoted_amount DECIMAL(12,2) NOT NULL,
    specs TEXT,
    quotation_status VARCHAR(20) NOT NULL DEFAULT 'Submitted',
    date_submitted TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (request_id) REFERENCES requests(request_id),
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id)
) ENGINE = InnoDB;


-- 8. Quotation items
-- These are the individual prices that make up a quotation.
-- request_item_id shows which requested item is being priced.
-- The total for a line is unit_price multiplied by quantity.
CREATE TABLE IF NOT EXISTS quotation_items (
    quotation_item_id INT AUTO_INCREMENT PRIMARY KEY,
    quotation_id INT NOT NULL,
    request_item_id INT NOT NULL,
    item_description VARCHAR(255) NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    quantity INT NOT NULL,

    FOREIGN KEY (quotation_id) REFERENCES quotations(quotation_id),
    FOREIGN KEY (request_item_id) REFERENCES request_items(request_item_id)
) ENGINE = InnoDB;


-- 9. Approvals
-- A manager records a decision about a request here.
-- UNIQUE on request_id allows only one approval record per request.
-- Status can be Pending, Approved or Rejected.
-- approval_date stays empty until a decision is made.
CREATE TABLE IF NOT EXISTS approvals (
    approval_id INT AUTO_INCREMENT PRIMARY KEY,
    request_id INT NOT NULL UNIQUE,
    manager_id INT NOT NULL,
    approval_status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    comments TEXT,
    approval_date DATETIME,

    FOREIGN KEY (request_id) REFERENCES requests(request_id),
    FOREIGN KEY (manager_id) REFERENCES users(user_id)
) ENGINE = InnoDB;


-- 10. Approval history
-- This table can keep a record of changes to an approval.
-- changed_by identifies the user who made the change.
-- Creating this table does not automatically record changes.
CREATE TABLE IF NOT EXISTS approval_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    approval_id INT NOT NULL,
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    changed_by INT NOT NULL,
    change_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (approval_id) REFERENCES approvals(approval_id),
    FOREIGN KEY (changed_by) REFERENCES users(user_id)
) ENGINE = InnoDB;


-- 11. Deliveries
-- This table records equipment deliveries for a request.
-- received_by identifies the user who receives the equipment.
-- The date and receiver can stay empty while delivery is pending.
-- Status can be Pending or Received.
CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id INT AUTO_INCREMENT PRIMARY KEY,
    request_id INT NOT NULL,
    vendor_id INT NOT NULL,
    delivery_date DATE,
    received_by INT,
    delivery_status VARCHAR(20) NOT NULL DEFAULT 'Pending',

    FOREIGN KEY (request_id) REFERENCES requests(request_id),
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id),
    FOREIGN KEY (received_by) REFERENCES users(user_id)
) ENGINE = InnoDB;


-- 12. Delivery items
-- This table lists the equipment received in a delivery.
-- request_item_id connects the delivered equipment to the requested item.
-- For equipment with serial numbers, enter one row per unit with quantity 1.
-- UNIQUE prevents us from recording the same serial number twice here.
CREATE TABLE IF NOT EXISTS delivery_items (
    delivery_item_id INT AUTO_INCREMENT PRIMARY KEY,
    delivery_id INT NOT NULL,
    request_item_id INT NOT NULL,
    item_description VARCHAR(255) NOT NULL,
    serial_number VARCHAR(100) NOT NULL UNIQUE,
    quantity INT NOT NULL DEFAULT 1,

    FOREIGN KEY (delivery_id) REFERENCES deliveries(delivery_id),
    FOREIGN KEY (request_item_id) REFERENCES request_items(request_item_id)
) ENGINE = InnoDB;


-- 13. Inventory
-- Inventory is our list of equipment received and available for use.
-- Each row represents one physical unit with its own serial number.
-- delivery_item_id shows where that unit came from.
-- assigned_to can stay empty until the equipment is given to a user.
CREATE TABLE IF NOT EXISTS inventory (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    delivery_item_id INT NOT NULL UNIQUE,
    item_description VARCHAR(255) NOT NULL,
    serial_number VARCHAR(100) NOT NULL UNIQUE,
    category_id INT NOT NULL,
    assigned_to INT,
    date_added TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (delivery_item_id) REFERENCES delivery_items(delivery_item_id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (assigned_to) REFERENCES users(user_id)
) ENGINE = InnoDB;


-- 14. Notifications
-- A notification is a short message for a particular user.
-- is_read uses 0 for unread and 1 for read.
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message VARCHAR(255) NOT NULL,
    is_read TINYINT NOT NULL DEFAULT 0,
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE = InnoDB;


-- 15. Audit logs
-- This table can record actions such as submitting a request.
-- table_affected and record_id describe which record was involved.
-- record_id is not a foreign key because it can refer to different tables.
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    action VARCHAR(255) NOT NULL,
    table_affected VARCHAR(50) NOT NULL,
    record_id INT NOT NULL,
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE = InnoDB;


-- 16. Attachments
-- This table stores details of files attached to a request.
-- file_path stores the file location, not the file itself.
CREATE TABLE IF NOT EXISTS attachments (
    attachment_id INT AUTO_INCREMENT PRIMARY KEY,
    request_id INT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    uploaded_by INT NOT NULL,
    date_uploaded TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (request_id) REFERENCES requests(request_id),
    FOREIGN KEY (uploaded_by) REFERENCES users(user_id)
) ENGINE = InnoDB;


-- InnoDB lets MySQL enforce the foreign-key relationships above.
-- CURRENT_TIMESTAMP fills in the current date and time when a row is added.
-- We will check positive quantities, prices, roles and statuses in Java later.
-- For now, we are creating the structure only. Sample data will come later.
-- IF NOT EXISTS skips existing tables; it does not update their structure.

-- Show the tables so we can confirm that they were created.
SHOW TABLES;
