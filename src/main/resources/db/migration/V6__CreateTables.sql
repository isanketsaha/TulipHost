
CREATE TABLE IF NOT EXISTS system_document (
  id bigint NOT NULL AUTO_INCREMENT,
  uid varchar(100) NOT NULL,
  file_name varchar(150) DEFAULT NULL,
  status varchar(50) DEFAULT NULL,
  file_type varchar(50) DEFAULT NULL,
  file_size int DEFAULT 0,
  document_category varchar(50) NOT NULL,  -- 'TEMPLATE', 'ACADEMIC_CALENDAR', 'HOLIDAY_LIST', 'BANNER'
  description text DEFAULT NULL,
  session_id bigint DEFAULT NULL,
  class_id bigint DEFAULT NULL,
  display_order int DEFAULT NULL,
  is_active boolean DEFAULT true,
  created_by varchar(50) DEFAULT NULL,
  last_modified_by varchar(50) DEFAULT NULL,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (session_id) REFERENCES session (id),
  FOREIGN KEY (class_id) REFERENCES class_details (id),
  PRIMARY KEY (id),
  UNIQUE KEY (uid)
);

CREATE TABLE IF NOT EXISTS leave_type (
  id bigint NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL,     -- 'Sick Leave', 'Casual Leave', 'Emergency Leave', etc.
  description text DEFAULT NULL,
  is_paid boolean DEFAULT true,  -- Whether this type of leave comes with pay
  is_active boolean DEFAULT true,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY (name)
);

-- Main Employee Leave table
CREATE TABLE IF NOT EXISTS employee_leave (
  id bigint NOT NULL AUTO_INCREMENT,
  employee_id bigint NOT NULL,
  leave_type_id bigint NOT NULL,
  start_date date NOT NULL,
  end_date date NOT NULL,
  total_days decimal(5,1) NOT NULL,
  reason text DEFAULT NULL,
  status varchar(20) NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'APPROVED', 'REJECTED'
  approved_by bigint DEFAULT NULL,
  approval_date timestamp DEFAULT NULL,
  comments text DEFAULT NULL,
  created_by varchar(50) DEFAULT NULL,
  last_modified_by varchar(50) DEFAULT NULL,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (employee_id) REFERENCES employee (emp_id),
  FOREIGN KEY (leave_type_id) REFERENCES leave_type (id),
  FOREIGN KEY (approved_by) REFERENCES employee (emp_id)
);

ALTER TABLE upload
ADD COLUMN leave_id bigint DEFAULT NULL,
ADD FOREIGN KEY (leave_id) REFERENCES employee_leave (id);

ALTER TABLE student
ADD COLUMN termination_reason text DEFAULT NULL AFTER termination_date,
ADD COLUMN tc_number varchar(25)  DEFAULT NULL AFTER termination_date;

ALTER TABLE employee
ADD COLUMN tid varchar(10) DEFAULT NULL UNIQUE AFTER emp_id;
