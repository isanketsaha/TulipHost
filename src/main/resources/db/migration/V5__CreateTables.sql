alter TABLE upload MODIFY COLUMN file_type VARCHAR(200);

alter table bank MODIFY COLUMN account_no VARCHAR(20);

drop table IF EXISTS holiday;

CREATE TABLE IF NOT EXISTS potential_admission (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    parent_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    current_school VARCHAR(100),
    whatsapp_available bit(1)  DEFAULT 0,
    std VARCHAR(20) NOT NULL,
    notes TEXT,
    address VARCHAR(255),
    gender ENUM('MALE', 'FEMALE','OTHERS') NOT NULL,
    admission_status ENUM('Pending', 'Admitted', 'Declined') DEFAULT 'Pending',
    created_by varchar(50) DEFAULT NULL,
	last_modified_by varchar(50) DEFAULT NULL,
	created_date timestamp DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS academic_calendar (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    event_type VARCHAR(50) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    recurring_pattern VARCHAR(50),
    organizer bigint,
    created_by varchar(50) DEFAULT NULL,
	last_modified_by varchar(50) DEFAULT NULL,
	created_date timestamp DEFAULT CURRENT_TIMESTAMP,
	last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (organizer) REFERENCES employee (emp_id)
);

CREATE TABLE IF NOT EXISTS coupons (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    discount_type ENUM('PERCENTAGE', 'FIXED_AMOUNT') NOT NULL,
    discount_value DECIMAL(10, 2) NOT NULL,
    min_purchase_amount DECIMAL(10, 2) DEFAULT 0.00,
    max_discount_amount DECIMAL(10, 2),
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    usage_limit INT DEFAULT 1,
    created_by varchar(50) DEFAULT NULL,
    last_modified_by varchar(50) DEFAULT NULL,
    created_date timestamp DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE transactions
ADD COLUMN coupon_id bigint  DEFAULT NULL,
ADD FOREIGN KEY (coupon_id) REFERENCES coupons (id)
