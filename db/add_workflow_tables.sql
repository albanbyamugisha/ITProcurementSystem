-- These extra tables keep customer choices separate from staff approval.
-- Safe to run more than once: existing tables and data are kept.
USE it_procurement_db;

-- Each customer decision belongs to one quotation and records their comments.
CREATE TABLE IF NOT EXISTS customer_decisions (
    quotation_id INT PRIMARY KEY,
    customer_id INT NOT NULL,
    decision VARCHAR(20) NOT NULL,
    comments TEXT,
    decision_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (quotation_id) REFERENCES quotations(quotation_id),
    FOREIGN KEY (customer_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- One request can have only one chosen quotation. The choice is final for this request.
-- Staff approvals already identify the request, so joining this table identifies
-- the exact quotation being approved. Customer acceptance alone is not staff approval.
CREATE TABLE IF NOT EXISTS request_selections (
    request_id INT PRIMARY KEY,
    quotation_id INT NOT NULL UNIQUE,
    FOREIGN KEY (request_id) REFERENCES requests(request_id),
    FOREIGN KEY (quotation_id) REFERENCES quotations(quotation_id)
) ENGINE=InnoDB;

-- Services have progress notes and a completion date, rather than serial numbers.
CREATE TABLE IF NOT EXISTS service_progress (
    request_id INT PRIMARY KEY,
    work_status VARCHAR(20) NOT NULL DEFAULT 'Not Started',
    work_notes TEXT,
    completion_date DATE,
    updated_by INT NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (request_id) REFERENCES requests(request_id),
    FOREIGN KEY (updated_by) REFERENCES users(user_id)
) ENGINE=InnoDB;
