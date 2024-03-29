

-- db.holiday definition

CREATE TABLE IF NOT EXISTS  holiday (
  id bigint NOT NULL AUTO_INCREMENT,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  date date DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  occasion varchar(255) NOT NULL,
  created_by varchar(50) DEFAULT NULL,
  last_modified_by varchar(50) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (occasion)
);

CREATE TABLE IF NOT EXISTS bank(
id bigint NOT NULL AUTO_INCREMENT,
account_no bigint NOT NULL,
ifsc varchar(30) NOT NULL,
bank_name varchar(30) DEFAULT NULL,
created_by varchar(50) DEFAULT NULL,
last_modified_by varchar(50) DEFAULT NULL,
created_date timestamp DEFAULT CURRENT_TIMESTAMP,
last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY (id),
 UNIQUE KEY (account_no)
);

CREATE TABLE IF NOT EXISTS interview (
id bigint NOT NULL AUTO_INCREMENT,
interview_date date NOT NULL,
doj date NOT NULL,
salary int(7) DEFAULT NULL,
comments text DEFAULT NULL,
created_by varchar(50) DEFAULT NULL,
last_modified_by varchar(50) DEFAULT NULL,
created_date timestamp DEFAULT CURRENT_TIMESTAMP,
last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS audit (
id bigint NOT NULL AUTO_INCREMENT,
description text NOT NULL,
metadata text DEFAULT NULL,
type varchar(30) NOT NULL,  -- error or log
resolved bit(1) DEFAULT 0,
created_by varchar(50) DEFAULT NULL,
last_modified_by varchar(50) DEFAULT NULL,
created_date timestamp DEFAULT CURRENT_TIMESTAMP,
last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY (id)
);

-- db.credential definition

CREATE TABLE IF NOT EXISTS  credential (
  id bigint NOT NULL AUTO_INCREMENT,
  password varchar(150) NOT NULL,
  user_name varchar(20) NOT NULL,
  created_by varchar(50) DEFAULT NULL,
  last_modified_by varchar(50) DEFAULT NULL,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY (user_name)
) ;

CREATE TABLE IF NOT EXISTS  session (
    id bigint NOT NULL AUTO_INCREMENT,
    display_text varchar(10) NOT NULL,
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    created_by varchar(50) DEFAULT NULL,
    last_modified_by varchar(50) DEFAULT NULL,
    created_date timestamp DEFAULT  CURRENT_TIMESTAMP,
    last_modified_date timestamp DEFAULT  CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY (display_text)
);

-- db.user_group definition

CREATE TABLE IF NOT EXISTS  user_group (
  id bigint NOT NULL AUTO_INCREMENT,
  created_date timestamp DEFAULT  CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT  CURRENT_TIMESTAMP,
  authority ENUM('UG_ADMIN','UG_ADMIN_VIEW','UG_PRINCIPAL','UG_STAFF','UG_TEACHER','UG_STUDENT') NOT NULL,
  created_by varchar(50) DEFAULT NULL,
  last_modified_by varchar(50) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (authority)
) ;

-- db.employee definition

CREATE TABLE IF NOT EXISTS  employee (
  emp_id bigint NOT NULL AUTO_INCREMENT,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  active bit(1)  DEFAULT 1,
  address varchar(255) DEFAULT NULL,
  blood_group varchar(6) DEFAULT NULL,
  dob datetime DEFAULT NULL,
  whatsapp_available bit(1)  DEFAULT 0,
  experience varchar(255) DEFAULT NULL,
  gender ENUM('MALE', 'FEMALE','OTHERS') NOT NULL,
  leave_balance double(3,2) DEFAULT '1.00',
  locked bit(1) NOT NULL,
  name varchar(50) NOT NULL,
  reset_credential bit(1) NOT NULL,
  credential_id bigint DEFAULT NULL,
  phone_number varchar(20) NOT NULL,
  qualification varchar(20) DEFAULT NULL,
  religion varchar(20) DEFAULT NULL,
  group_id bigint NOT NULL,
  bank_id bigint DEFAULT NULL,
  interview_id bigint DEFAULT NULL,
  created_by varchar(50) DEFAULT NULL,
  last_modified_by varchar(50) DEFAULT NULL,
  PRIMARY KEY (emp_id),
  UNIQUE KEY (phone_number, name),
  FOREIGN KEY (group_id) REFERENCES user_group (id),
  FOREIGN KEY (credential_id) REFERENCES credential (id),
  FOREIGN KEY (bank_id) REFERENCES bank (id),
  FOREIGN KEY (interview_id) REFERENCES interview (id)
);


-- db.class_details definition

CREATE TABLE IF NOT EXISTS  class_details (
  id bigint NOT NULL AUTO_INCREMENT,
  std varchar(10) DEFAULT NULL,
  head_teacher_id bigint DEFAULT NULL,
  session_id bigint  NOT NULL,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  created_by varchar(50) DEFAULT NULL,
  last_modified_by varchar(50) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY(session_id,std),
  FOREIGN KEY (head_teacher_id) REFERENCES employee (emp_id),
   FOREIGN KEY (session_id) REFERENCES session (id)
) ;

-- db.student definition

CREATE TABLE IF NOT EXISTS  student (
  student_id bigint NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL,
  dob date NOT NULL,
  address varchar(255) DEFAULT NULL,
  blood_group varchar(6) DEFAULT NULL,
  gender ENUM('MALE', 'FEMALE','OTHERS') NOT NULL,
  active bit(1) DEFAULT 1,
  phone_number varchar(20) NOT NULL,
  whatsapp_available bit(1)  DEFAULT 0,
  evening_classes bit(1)  DEFAULT 0,
  previous_school varchar(50) DEFAULT NULL,
  termination_date date DEFAULT NULL,
  created_by varchar(50) DEFAULT NULL,
  religion varchar(20) DEFAULT NULL,
  last_modified_by varchar(50) DEFAULT NULL,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (student_id),
  UNIQUE KEY (phone_number, name)
) ;


-- db.parents_details definition

CREATE TABLE IF NOT EXISTS  dependent (
  id bigint NOT NULL AUTO_INCREMENT,
  contact varchar(18) DEFAULT NULL,
  name varchar(50) DEFAULT NULL,
  occupation varchar(50) DEFAULT NULL,
  qualification varchar(50) DEFAULT NULL,
  relationship varchar(20) DEFAULT NULL,
  aadhaar_no varchar(16) DEFAULT NULL,
  whatsapp_available bit(1)  DEFAULT 0,
  created_by varchar(50) DEFAULT NULL,
  last_modified_by varchar(50) DEFAULT NULL,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY(name, contact)
) ;

CREATE TABLE IF NOT EXISTS  student_to_dependent(
    dependent_id bigint NOT NULL,
    student_id bigint NOT NULL,
    PRIMARY KEY (dependent_id, student_id),
    FOREIGN KEY (dependent_id) REFERENCES dependent (id),
    FOREIGN KEY (student_id) REFERENCES student (student_id)
);

CREATE TABLE IF NOT EXISTS  employee_to_dependent(
    dependent_id bigint NOT NULL,
    emp_id bigint NOT  NULL,
    PRIMARY KEY (dependent_id,emp_id),
    FOREIGN KEY (dependent_id) REFERENCES dependent (id),
    FOREIGN KEY (emp_id) REFERENCES employee (emp_id)
);

CREATE TABLE IF NOT EXISTS  student_to_class(
    class_id bigint NOT NULL,
    student_id bigint NOT NULL,
    PRIMARY KEY (class_id, student_id),
    FOREIGN KEY (student_id) REFERENCES student (student_id),
    FOREIGN KEY (class_id) REFERENCES class_details (id)
);

CREATE TABLE IF NOT EXISTS product_catalog (
  id bigint NOT NULL AUTO_INCREMENT,
  item_name varchar(255) NOT NULL ,
  price double NOT NULL,
  tag varchar(10) DEFAULT NULL, -- BOY | GIRL
  std_id bigint DEFAULT NULL,
  category varchar(20) NOT NULL,
  active bit(1) DEFAULT 1,
  size varchar(20) DEFAULT NULL, -- 22,24,26
  created_by varchar(50) DEFAULT NULL,
  last_modified_by varchar(50) DEFAULT NULL,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (std_id) REFERENCES class_details(id)
) ;


CREATE TABLE IF NOT EXISTS fees_catalog (
  id bigint NOT NULL AUTO_INCREMENT,
  fees_name varchar(255) NOT NULL ,
  price double NOT NULL,
  applicable_rule ENUM('MONTHLY','YEARLY','HALF-YEARLY','OTHERS') NOT NULL,
  std_id bigint DEFAULT NULL,
  active bit(1) DEFAULT 1,
  created_by varchar(50) DEFAULT NULL,
  last_modified_by varchar(50) DEFAULT NULL,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY (fees_name, std_id),
  FOREIGN KEY (std_id) REFERENCES class_details(id)
);

CREATE TABLE IF NOT EXISTS  transactions (
    transaction_id bigint NOT NULL AUTO_INCREMENT,
    payment_mode varchar(20) NOT NULL,
    type varchar(20) NOT NULL,  -- FEES, PURCHASE, EXPENSE.
    student_id bigint DEFAULT NULL,
    amount double NOT NULL,
    discount int DEFAULT 0,
    after_discount double NOT NULL,
    comments varchar(100) DEFAULT NULL,
    created_by varchar(50) DEFAULT NULL,
    last_modified_by varchar(50) DEFAULT NULL,
    created_date datetime DEFAULT CURRENT_TIMESTAMP,
    last_modified_date datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (transaction_id),
    FOREIGN KEY (student_id) REFERENCES student (student_id)
) ;


-- Each fees line item.
CREATE TABLE IF NOT EXISTS fees_line_item(
    id bigint NOT NULL AUTO_INCREMENT,
    transaction_id bigint NOT NUll,
    fees_product_id bigint NOT NULL,
    unit_price double NOT NULL,
    month varchar(10) DEFAULT NULL,
    amount double NOT NULL,
    created_by varchar(50) DEFAULT NULL,
    last_modified_by varchar(50) DEFAULT NULL,
    created_date timestamp DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions (transaction_id),
    FOREIGN KEY (fees_product_id) REFERENCES fees_catalog (id),
    PRIMARY KEY (id)
);

-- each purchase line item.
CREATE TABLE IF NOT EXISTS purchase_line_item(
    id bigint NOT NULL AUTO_INCREMENT,
    unit_price double NOT NULL,
    qty int(11) NOT NULL,
    amount double NOT NULL,
    transaction_id bigint NOT NUll,
    product_id bigint NOT NULL,
    created_by varchar(50) DEFAULT NULL,
    last_modified_by varchar(50) DEFAULT NULL,
    created_date timestamp DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions (transaction_id),
    FOREIGN KEY (product_id) REFERENCES product_catalog (id),
    PRIMARY KEY (id)
);

-- this will store the stock and purchases we have made in history.
CREATE TABLE IF NOT EXISTS inventory (
    id bigint NOT NULL AUTO_INCREMENT,
    product_id bigint NOT NULL,
    unit_price double NOT NULL,
    qty int NOT NULL,
    discount_percent int DEFAULT 0,
    vendor varchar(50) DEFAULT NULL,
    created_by varchar(50) DEFAULT NULL,
    last_modified_by varchar(50) DEFAULT NULL,
    created_date timestamp DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product_catalog (id),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS expense (
    id bigint NOT NULL AUTO_INCREMENT,
    item_name varchar(100) DEFAULT NULL,
    category varchar(100) DEFAULT NULL, -- FUNCTION, OFFICE,
    amount double NOT NULL,
    qty varchar(10) NOT NULL,
    received_by varchar(100) DEFAULT NULL,
    transaction_id bigint NOT NUll,
    created_by varchar(50) DEFAULT NULL,
    last_modified_by varchar(50) DEFAULT NULL,
    created_date timestamp DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions (transaction_id),
    PRIMARY KEY (id)
);





