-- These extra tables keep customer choices separate from staff approval.
-- Safe to run more than once: existing tables and data are kept.
-- Select it_procurement_db as the database for the statements that follow.
USE it_procurement_db;

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
