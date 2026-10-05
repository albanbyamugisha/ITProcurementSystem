-- IT Procurement Request System
-- This database stores equipment requests, quotations, approvals and deliveries.

-- Create the database if it is not already there.
-- Create the database only when it is missing; this statement does not delete an existing database.
CREATE DATABASE IF NOT EXISTS it_procurement_db;

-- Use this database for all the tables below.
-- Select it_procurement_db as the database for the statements that follow.
USE it_procurement_db;

-- A primary key gives each record its own number.
-- AUTO_INCREMENT lets MySQL generate that number automatically.
-- A foreign key connects a record to a record in another table.
-- NOT NULL means a value must be provided.
-- Parent tables are created first because other tables refer to them.
-- Uses InnoDb engine for transaction support and foreign key constraints.


-- 1. Departments
-- A department can have several users and equipment requests.
-- Create departments only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS departments (
    -- Store department id in department_id. Use a whole number. MySQL generates the next ID automatically.
    -- This is the unique, non-NULL identifier for each row.
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store department name in department_name. Allow up to 100 characters. A value is required.
    department_name VARCHAR(100) NOT NULL
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE = InnoDB;


-- 2. Users
-- An organisation user may belong to a department; individuals need none.
-- The role can be Requester, Manager, Purchaser or Admin.
-- password_hash stores the result of password hashing, not the actual password.
-- Create users only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS users (
    -- Store user id in user_id. Use a whole number. MySQL generates the next ID automatically. This is the
    -- unique, non-NULL identifier for each row.
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store department id in department_id. Use a whole number. NULL is allowed when no value is supplied.
    department_id INT NULL,
    -- Individuals need no department; organisations may omit it too.
    -- Store whether the customer is an Individual or an Organisation in account_type. Allow up to 20
    -- characters. A value is required. Use 'Organisation' when an insert omits this column.
    account_type VARCHAR(20) NOT NULL DEFAULT 'Organisation',
    -- Store the organisation name when the account represents a business or organisation in
    -- organisation_name. Allow up to 150 characters. NULL is allowed when no value is supplied.
    organisation_name VARCHAR(150),
    -- Store username in username. Allow up to 50 characters. A value is required. Non-NULL values cannot
    -- be shared by two records.
    username VARCHAR(50) NOT NULL UNIQUE,
    -- Store the one-way password hash used during login, rather than the original password in
    -- password_hash. Allow up to 255 characters. A value is required.
    password_hash VARCHAR(255) NOT NULL,
    -- Store the optional gender of the account holder or organisation contact in gender. Allow up to 30
    -- characters. NULL is allowed when no value is supplied.
    gender VARCHAR(30),
    -- Store the account version used to invalidate older login sessions in session_version. Use a whole
    -- number. A value is required. Use 0 when an insert omits this column.
    session_version INT NOT NULL DEFAULT 0,
    -- Generated reset passwords must be replaced before the main screen opens.
    -- Store whether a temporary password must be replaced before normal access in must_change_password.
    -- Use a true-or-false value. A value is required. Use FALSE when an insert omits this column.
    must_change_password BOOLEAN NOT NULL DEFAULT FALSE,
    -- Store full name in full_name. Allow up to 100 characters. A value is required.
    full_name VARCHAR(100) NOT NULL,
    -- Each registered email must belong to only one account.
    -- Store email in email. Allow up to 100 characters. NULL is allowed when no value is supplied. Non-
    -- NULL values cannot be shared by two records.
    email VARCHAR(100) UNIQUE,
    -- Store the account permission role: Requester, Manager, Purchaser or Admin in role. Allow up to 20
    -- characters. A value is required.
    role VARCHAR(20) NOT NULL,

    -- Link department_id to departments.department_id. A non-NULL value must refer to an existing parent
    -- record.
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE = InnoDB;


-- 3. Categories
-- Categories group similar equipment, such as computers and printers.
-- Create categories only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS categories (
    -- Store category id in category_id. Use a whole number. MySQL generates the next ID automatically.
    -- This is the unique, non-NULL identifier for each row.
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store category name in category_name. Allow up to 100 characters. A value is required.
    category_name VARCHAR(100) NOT NULL,
    -- Store description in description. Allow up to 255 characters. NULL is allowed when no value is
    -- supplied.
    description VARCHAR(255)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE = InnoDB;


-- 4. Vendors
-- These are the suppliers who provide quotations and supply equipment.
-- Create vendors only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS vendors (
    -- Store vendor id in vendor_id. Use a whole number. MySQL generates the next ID automatically. This is
    -- the unique, non-NULL identifier for each row.
    vendor_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store vendor name in vendor_name. Allow up to 100 characters. A value is required.
    vendor_name VARCHAR(100) NOT NULL,
    -- Store contact person in contact_person. Allow up to 100 characters. NULL is allowed when no value is
    -- supplied.
    contact_person VARCHAR(100),
    -- Store phone in phone. Allow up to 30 characters. NULL is allowed when no value is supplied.
    phone VARCHAR(30),
    -- Store email in email. Allow up to 100 characters. NULL is allowed when no value is supplied.
    email VARCHAR(100),
    -- Store address in address. Allow up to 255 characters. NULL is allowed when no value is supplied.
    address VARCHAR(255)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE = InnoDB;


-- 5. Requests
-- This table stores the main details of each request.
-- requester_id identifies which user submitted it.
-- department_id records the department making the request.
-- Status can be Pending, Quoted, CustomerAccepted, Approved, Rejected, Delivered or Completed.
-- Create requests only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS requests (
    -- Store request id in request_id. Use a whole number. MySQL generates the next ID automatically. This
    -- is the unique, non-NULL identifier for each row.
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store the account that submitted the request in requester_id. Use a whole number. A value is
    -- required.
    requester_id INT NOT NULL,
    -- Store department id in department_id. Use a whole number. NULL is allowed when no value is supplied.
    department_id INT NULL,
    -- A request contains equipment OR services; use separate requests for each type.
    -- Store whether this request contains Equipment or Service items in request_type. Allow up to 20
    -- characters. A value is required. Use 'Equipment' when an insert omits this column.
    request_type VARCHAR(20) NOT NULL DEFAULT 'Equipment',
    -- Store the current stage of the request workflow in request_status. Allow up to 20 characters. A
    -- value is required. Use 'Pending' when an insert omits this column.
    request_status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    -- Store date created in date_created. Store a date and time. A value is required. Use
    -- CURRENT_TIMESTAMP when an insert omits this column.
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Store notes in notes. Allow a longer text value. NULL is allowed when no value is supplied.
    notes TEXT,

    -- Link requester_id to users.user_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (requester_id) REFERENCES users(user_id),
    -- Link department_id to departments.department_id. A non-NULL value must refer to an existing parent
    -- record.
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE = InnoDB;


-- 6. Request items
-- One request can contain several different items.
-- Each row describes one type of item and the quantity needed.
-- estimated_cost stores one unit's saved selling price; older requests retain their original estimate.
-- DECIMAL(12,2) stores an amount with two decimal places.
-- Fixed selling prices; supplier quotations remain a separate internal cost.
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

-- Create request_items only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS request_items (
    -- Store catalogue id in catalogue_id. Use a whole number. NULL is allowed when no value is supplied.
    catalogue_id INT NULL,
    -- Store the pricing unit, such as Each or Per computer in unit. Allow up to 50 characters. NULL is
    -- allowed when no value is supplied.
    unit VARCHAR(50),
    CONSTRAINT fk_request_catalogue FOREIGN KEY(catalogue_id) REFERENCES catalogue(catalogue_id),
    -- Store request item id in request_item_id. Use a whole number. MySQL generates the next ID
    -- automatically. This is the unique, non-NULL identifier for each row.
    request_item_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store request id in request_id. Use a whole number. A value is required.
    request_id INT NOT NULL,
    -- Store category id in category_id. Use a whole number. A value is required.
    category_id INT NOT NULL,
    -- Store item description in item_description. Allow up to 255 characters. A value is required.
    item_description VARCHAR(255) NOT NULL,
    -- Store the number of units on this item record in quantity. Use a whole number. A value is required.
    quantity INT NOT NULL,
    -- Store the saved selling price of one requested unit; older requests may contain their original
    -- estimate in estimated_cost. Use an exact decimal with ten digits before and two after the decimal
    -- point. A value is required.
    estimated_cost DECIMAL(12,2) NOT NULL,

    -- Link request_id to requests.request_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (request_id) REFERENCES requests(request_id),
    -- Link category_id to categories.category_id. A non-NULL value must refer to an existing parent
    -- record.
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE = InnoDB;


-- 7. Quotations
-- A vendor gives a quotation for a request.
-- One request can receive quotations from different vendors.
-- quoted_amount is the total price of all items in that quotation.
-- Status can be Submitted, Accepted, Declined or Not Selected.
-- Create quotations only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS quotations (
    -- Store quotation id in quotation_id. Use a whole number. MySQL generates the next ID automatically.
    -- This is the unique, non-NULL identifier for each row.
    quotation_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store request id in request_id. Use a whole number. A value is required.
    request_id INT NOT NULL,
    -- Store vendor id in vendor_id. Use a whole number. A value is required.
    vendor_id INT NOT NULL,
    -- Store the total supplier quotation amount in quoted_amount. Use an exact decimal with ten digits
    -- before and two after the decimal point. A value is required.
    quoted_amount DECIMAL(12,2) NOT NULL,
    -- Store specs in specs. Allow a longer text value. NULL is allowed when no value is supplied.
    specs TEXT,
    -- Store quotation status in quotation_status. Allow up to 20 characters. A value is required. Use
    -- 'Submitted' when an insert omits this column.
    quotation_status VARCHAR(20) NOT NULL DEFAULT 'Submitted',
    -- Store date submitted in date_submitted. Store a date and time. A value is required. Use
    -- CURRENT_TIMESTAMP when an insert omits this column.
    date_submitted TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Link request_id to requests.request_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (request_id) REFERENCES requests(request_id),
    -- Link vendor_id to vendors.vendor_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE = InnoDB;


-- 8. Quotation items
-- These are the individual prices that make up a quotation.
-- request_item_id shows which requested item is being priced.
-- The total for a line is unit_price multiplied by quantity.
-- Create quotation_items only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS quotation_items (
    -- Store quotation item id in quotation_item_id. Use a whole number. MySQL generates the next ID
    -- automatically. This is the unique, non-NULL identifier for each row.
    quotation_item_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store quotation id in quotation_id. Use a whole number. A value is required.
    quotation_id INT NOT NULL,
    -- Store request item id in request_item_id. Use a whole number. A value is required.
    request_item_id INT NOT NULL,
    -- Store item description in item_description. Allow up to 255 characters. A value is required.
    item_description VARCHAR(255) NOT NULL,
    -- Store the supplier price of one quoted item, separate from the customer selling price in unit_price.
    -- Use an exact decimal with ten digits before and two after the decimal point. A value is required.
    unit_price DECIMAL(12,2) NOT NULL,
    -- Store the number of units on this item record in quantity. Use a whole number. A value is required.
    quantity INT NOT NULL,

    -- Link quotation_id to quotations.quotation_id. A non-NULL value must refer to an existing parent
    -- record.
    FOREIGN KEY (quotation_id) REFERENCES quotations(quotation_id),
    -- Link request_item_id to request_items.request_item_id. A non-NULL value must refer to an existing
    -- parent record.
    FOREIGN KEY (request_item_id) REFERENCES request_items(request_item_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE = InnoDB;


-- 9. Approvals
-- A manager records a decision about a request here.
-- UNIQUE on request_id allows only one approval record per request.
-- Status can be Pending, Approved or Rejected.
-- approval_date stays empty until a decision is made.
-- Create approvals only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS approvals (
    -- Store approval id in approval_id. Use a whole number. MySQL generates the next ID automatically.
    -- This is the unique, non-NULL identifier for each row.
    approval_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store request id in request_id. Use a whole number. A value is required. Non-NULL values cannot be
    -- shared by two records.
    request_id INT NOT NULL UNIQUE,
    -- Store the Manager who recorded the approval decision in manager_id. Use a whole number. A value is
    -- required.
    manager_id INT NOT NULL,
    -- Store approval status in approval_status. Allow up to 20 characters. A value is required. Use
    -- 'Pending' when an insert omits this column.
    approval_status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    -- Store comments in comments. Allow a longer text value. NULL is allowed when no value is supplied.
    comments TEXT,
    -- Store approval date in approval_date. Store a date and time. NULL is allowed when no value is
    -- supplied.
    approval_date DATETIME,

    -- Link request_id to requests.request_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (request_id) REFERENCES requests(request_id),
    -- Link manager_id to users.user_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (manager_id) REFERENCES users(user_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE = InnoDB;


-- 10. Approval history
-- This table can keep a record of changes to an approval.
-- changed_by identifies the user who made the change.
-- Creating this table does not automatically record changes.
-- Create approval_history only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS approval_history (
    -- Store history id in history_id. Use a whole number. MySQL generates the next ID automatically. This
    -- is the unique, non-NULL identifier for each row.
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store approval id in approval_id. Use a whole number. A value is required.
    approval_id INT NOT NULL,
    -- Store old status in old_status. Allow up to 20 characters. NULL is allowed when no value is
    -- supplied.
    old_status VARCHAR(20),
    -- Store new status in new_status. Allow up to 20 characters. A value is required.
    new_status VARCHAR(20) NOT NULL,
    -- Store the staff account responsible for this approval change in changed_by. Use a whole number. A
    -- value is required.
    changed_by INT NOT NULL,
    -- Store change date in change_date. Store a date and time. A value is required. Use CURRENT_TIMESTAMP
    -- when an insert omits this column.
    change_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Link approval_id to approvals.approval_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (approval_id) REFERENCES approvals(approval_id),
    -- Link changed_by to users.user_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (changed_by) REFERENCES users(user_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE = InnoDB;


-- 11. Deliveries
-- This table records equipment deliveries for a request.
-- received_by identifies the user who receives the equipment.
-- The date and receiver can stay empty while delivery is pending.
-- Status can be Pending or Received.
-- Create deliveries only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS deliveries (
    -- Store delivery id in delivery_id. Use a whole number. MySQL generates the next ID automatically.
    -- This is the unique, non-NULL identifier for each row.
    delivery_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store request id in request_id. Use a whole number. A value is required.
    request_id INT NOT NULL,
    -- Store vendor id in vendor_id. Use a whole number. A value is required.
    vendor_id INT NOT NULL,
    -- Store delivery date in delivery_date. Store the calendar date without a time. NULL is allowed when
    -- no value is supplied.
    delivery_date DATE,
    -- Store the Purchaser who recorded the equipment delivery in received_by. Use a whole number. NULL is
    -- allowed when no value is supplied.
    received_by INT,
    -- Store delivery status in delivery_status. Allow up to 20 characters. A value is required. Use
    -- 'Pending' when an insert omits this column.
    delivery_status VARCHAR(20) NOT NULL DEFAULT 'Pending',

    -- Link request_id to requests.request_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (request_id) REFERENCES requests(request_id),
    -- Link vendor_id to vendors.vendor_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id),
    -- Link received_by to users.user_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (received_by) REFERENCES users(user_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE = InnoDB;


-- 12. Delivery items
-- This table lists the equipment received in a delivery.
-- request_item_id connects the delivered equipment to the requested item.
-- For equipment with serial numbers, enter one row per unit with quantity 1.
-- UNIQUE prevents us from recording the same serial number twice here.
-- Create delivery_items only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS delivery_items (
    -- Store delivery item id in delivery_item_id. Use a whole number. MySQL generates the next ID
    -- automatically. This is the unique, non-NULL identifier for each row.
    delivery_item_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store delivery id in delivery_id. Use a whole number. A value is required.
    delivery_id INT NOT NULL,
    -- Store request item id in request_item_id. Use a whole number. A value is required.
    request_item_id INT NOT NULL,
    -- Store item description in item_description. Allow up to 255 characters. A value is required.
    item_description VARCHAR(255) NOT NULL,
    -- Store the identifier of one physical equipment unit in serial_number. Allow up to 100 characters. A
    -- value is required. Non-NULL values cannot be shared by two records.
    serial_number VARCHAR(100) NOT NULL UNIQUE,
    -- Store the number of units on this item record in quantity. Use a whole number. A value is required.
    -- Use 1 when an insert omits this column.
    quantity INT NOT NULL DEFAULT 1,

    -- Link delivery_id to deliveries.delivery_id. A non-NULL value must refer to an existing parent
    -- record.
    FOREIGN KEY (delivery_id) REFERENCES deliveries(delivery_id),
    -- Link request_item_id to request_items.request_item_id. A non-NULL value must refer to an existing
    -- parent record.
    FOREIGN KEY (request_item_id) REFERENCES request_items(request_item_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE = InnoDB;


-- 13. Inventory
-- Inventory is the list of equipment received and available automaticallye.
-- Each row represents one physical unit with its own serial number.
-- delivery_item_id shows where that unit came from.
-- assigned_to can stay empty until the equipment is given to a user.
-- Create inventory only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS inventory (
    -- Store inventory id in inventory_id. Use a whole number. MySQL generates the next ID automatically.
    -- This is the unique, non-NULL identifier for each row.
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store delivery item id in delivery_item_id. Use a whole number. A value is required. Non-NULL values
    -- cannot be shared by two records.
    delivery_item_id INT NOT NULL UNIQUE,
    -- Store item description in item_description. Allow up to 255 characters. A value is required.
    item_description VARCHAR(255) NOT NULL,
    -- Store the identifier of one physical equipment unit in serial_number. Allow up to 100 characters. A
    -- value is required. Non-NULL values cannot be shared by two records.
    serial_number VARCHAR(100) NOT NULL UNIQUE,
    -- Store category id in category_id. Use a whole number. A value is required.
    category_id INT NOT NULL,
    -- Store the account currently assigned this equipment, or NULL when unassigned in assigned_to. Use a
    -- whole number. NULL is allowed when no value is supplied.
    assigned_to INT,
    -- Store date added in date_added. Store a date and time. A value is required. Use CURRENT_TIMESTAMP
    -- when an insert omits this column.
    date_added TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Link delivery_item_id to delivery_items.delivery_item_id. A non-NULL value must refer to an existing
    -- parent record.
    FOREIGN KEY (delivery_item_id) REFERENCES delivery_items(delivery_item_id),
    -- Link category_id to categories.category_id. A non-NULL value must refer to an existing parent
    -- record.
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    -- Link assigned_to to users.user_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (assigned_to) REFERENCES users(user_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE = InnoDB;


-- 14. Notifications
-- A notification is a short message for a particular user.
-- is_read uses 0 for unread and 1 for read.
-- Create notifications only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS notifications (
    -- Store notification id in notification_id. Use a whole number. MySQL generates the next ID
    -- automatically. This is the unique, non-NULL identifier for each row.
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store user id in user_id. Use a whole number. A value is required.
    user_id INT NOT NULL,
    -- Store the notification text shown to its recipient in message. Allow up to 255 characters. A value
    -- is required.
    message VARCHAR(255) NOT NULL,
    is_read TINYINT NOT NULL DEFAULT 0,
    -- Store date created in date_created. Store a date and time. A value is required. Use
    -- CURRENT_TIMESTAMP when an insert omits this column.
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Link user_id to users.user_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (user_id) REFERENCES users(user_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE = InnoDB;


-- 15. Audit logs
-- This table can record actions such as submitting a request.
-- table_affected and record_id describe which record was involved.
-- record_id is not a foreign key because it can refer to different tables.
-- Create audit_logs only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS audit_logs (
    -- Store log id in log_id. Use a whole number. MySQL generates the next ID automatically. This is the
    -- unique, non-NULL identifier for each row.
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store user id in user_id. Use a whole number. A value is required.
    user_id INT NOT NULL,
    -- Store the readable action recorded in the audit trail in action. Allow up to 255 characters. A value
    -- is required.
    action VARCHAR(255) NOT NULL,
    -- Store the table containing the record described by this audit entry in table_affected. Allow up to
    -- 50 characters. A value is required.
    table_affected VARCHAR(50) NOT NULL,
    -- Store the affected record ID in the table named by table_affected in record_id. Use a whole number.
    -- A value is required.
    record_id INT NOT NULL,
    -- Store date created in date_created. Store a date and time. A value is required. Use
    -- CURRENT_TIMESTAMP when an insert omits this column.
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Link user_id to users.user_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (user_id) REFERENCES users(user_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE = InnoDB;


-- 16. Attachments
-- This table stores details of files attached to a request.
-- file_path stores the file location, not the file itself.
-- Create attachments only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS attachments (
    -- Store attachment id in attachment_id. Use a whole number. MySQL generates the next ID automatically.
    -- This is the unique, non-NULL identifier for each row.
    attachment_id INT AUTO_INCREMENT PRIMARY KEY,
    -- Store request id in request_id. Use a whole number. A value is required.
    request_id INT NOT NULL,
    -- Store the original attachment filename shown in the selection list in file_name. Allow up to 255
    -- characters. A value is required.
    file_name VARCHAR(255) NOT NULL,
    -- Store the location of the managed attachment copy in file_path. Allow up to 500 characters. A value
    -- is required.
    file_path VARCHAR(500) NOT NULL,
    -- Store the account that uploaded the attachment in uploaded_by. Use a whole number. A value is
    -- required.
    uploaded_by INT NOT NULL,
    -- Store date uploaded in date_uploaded. Store a date and time. A value is required. Use
    -- CURRENT_TIMESTAMP when an insert omits this column.
    date_uploaded TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Link request_id to requests.request_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (request_id) REFERENCES requests(request_id),
    -- Link uploaded_by to users.user_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (uploaded_by) REFERENCES users(user_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE = InnoDB;


-- InnoDB lets MySQL enforce the foreign-key relationships above.
-- CURRENT_TIMESTAMP fills in the current date and time when a row is added.
-- We will check positive quantities, prices, roles and statuses in Java later.
-- This script creates structure only. Sample categories have a separate seed script.
-- IF NOT EXISTS skips existing tables; it does not update their structure.

-- 17-19. Customer decisions, chosen quotations and service progress.
-- These extra tables keep customer choices separate from staff approval.
-- Safe to run more than once: existing tables and data are kept.


-- Each customer decision belongs to one quotation and records their comments.
-- Create customer_decisions only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS customer_decisions (
    -- Store quotation id in quotation_id. Use a whole number. This is the unique, non-NULL identifier for
    -- each row.
    quotation_id INT PRIMARY KEY,
    -- Store the customer who made the order or quotation decision in customer_id. Use a whole number. A
    -- value is required.
    customer_id INT NOT NULL,
    -- Store decision in decision. Allow up to 20 characters. A value is required.
    decision VARCHAR(20) NOT NULL,
    -- Store comments in comments. Allow a longer text value. NULL is allowed when no value is supplied.
    comments TEXT,
    -- Store decision date in decision_date. Store a date and time. A value is required. Use
    -- CURRENT_TIMESTAMP when an insert omits this column.
    decision_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Link quotation_id to quotations.quotation_id. A non-NULL value must refer to an existing parent
    -- record.
    FOREIGN KEY (quotation_id) REFERENCES quotations(quotation_id),
    -- Link customer_id to users.user_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (customer_id) REFERENCES users(user_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE=InnoDB;

-- One request can have only one chosen quotation. The choice is final for this request.
-- Staff approvals already identify the request, so joining this table identifies
-- the exact quotation being approved. Customer acceptance alone is not staff approval.
-- Create request_selections only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS request_selections (
    -- Store request id in request_id. Use a whole number. This is the unique, non-NULL identifier for each
    -- row.
    request_id INT PRIMARY KEY,
    -- Store quotation id in quotation_id. Use a whole number. A value is required. Non-NULL values cannot
    -- be shared by two records.
    quotation_id INT NOT NULL UNIQUE,
    -- Link request_id to requests.request_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (request_id) REFERENCES requests(request_id),
    -- Link quotation_id to quotations.quotation_id. A non-NULL value must refer to an existing parent
    -- record.
    FOREIGN KEY (quotation_id) REFERENCES quotations(quotation_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE=InnoDB;

-- Services have progress notes and a completion date, rather than serial numbers.
-- Create service_progress only when it is missing. Its columns and relationships are defined below.
CREATE TABLE IF NOT EXISTS service_progress (
    -- Store request id in request_id. Use a whole number. This is the unique, non-NULL identifier for each
    -- row.
    request_id INT PRIMARY KEY,
    -- Store work status in work_status. Allow up to 20 characters. A value is required. Use 'Not Started'
    -- when an insert omits this column.
    work_status VARCHAR(20) NOT NULL DEFAULT 'Not Started',
    -- Store work notes in work_notes. Allow a longer text value. NULL is allowed when no value is
    -- supplied.
    work_notes TEXT,
    -- Store completion date in completion_date. Store the calendar date without a time. NULL is allowed
    -- when no value is supplied.
    completion_date DATE,
    -- Store the account that last saved the service progress in updated_by. Use a whole number. A value is
    -- required.
    updated_by INT NOT NULL,
    -- Store updated at in updated_at. Store a date and time. A value is required. Use CURRENT_TIMESTAMP
    -- when an insert omits this column. Refresh the timestamp automatically when the row changes.
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- Link request_id to requests.request_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (request_id) REFERENCES requests(request_id),
    -- Link updated_by to users.user_id. A non-NULL value must refer to an existing parent record.
    FOREIGN KEY (updated_by) REFERENCES users(user_id)
-- Use InnoDB so related records can use foreign keys and changes can be grouped into transactions.
) ENGINE=InnoDB;

-- Customer confirmation is separate from the internal supplier selection.
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

-- Show the tables so we can confirm that they were created.
SHOW TABLES;
