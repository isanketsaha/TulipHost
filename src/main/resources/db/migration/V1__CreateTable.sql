

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
  authority varchar(20) NOT NULL,
  created_by varchar(50) DEFAULT NULL,
  last_modified_by varchar(50) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (authority)
) ;


-- db.employee definition

CREATE TABLE IF NOT EXISTS  employee (
  id bigint NOT NULL AUTO_INCREMENT,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  active bit(1) NOT NULL,
  address varchar(255) DEFAULT NULL,
  blood_group varchar(4) DEFAULT NULL,
  dob datetime DEFAULT NULL,
  experience varchar(255) DEFAULT NULL,
  father varchar(50) DEFAULT NULL,
  gender varchar(6) DEFAULT NULL,
  leave_balance double(3,2) DEFAULT '1.00',
  locked bit(1) NOT NULL,
  name varchar(50) NOT NULL,
  phone_number varchar(20) NOT NULL,
  qualification varchar(20) DEFAULT NULL,
  religion varchar(20) DEFAULT NULL,
  group_id bigint NOT NULL,
  created_by varchar(50) DEFAULT NULL,
  last_modified_by varchar(50) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (phone_number),
  FOREIGN KEY (group_id) REFERENCES user_group (id)
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
  FOREIGN KEY (head_teacher_id) REFERENCES employee (id),
   FOREIGN KEY (session_id) REFERENCES session (id)
) ;



-- db.credential definition

CREATE TABLE IF NOT EXISTS  credential (
  id bigint NOT NULL AUTO_INCREMENT,
  password varchar(150) NOT NULL,
  reset_password bit(1) NOT NULL,
  user_name varchar(20) NOT NULL,
  employee_id bigint DEFAULT NULL,
  created_by varchar(50) DEFAULT NULL,
    last_modified_by varchar(50) DEFAULT NULL,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY (user_name),
  FOREIGN KEY (employee_id) REFERENCES employee (id)
) ;


-- db.student definition

CREATE TABLE IF NOT EXISTS  student (
  student_id bigint NOT NULL,
  name varchar(50) NOT NULL,
  dob date NOT NULL,
  address varchar(255) DEFAULT NULL,
   class_details_id bigint NOT NULL,
  blood_group varchar(2) NOT NULL,
  gender varchar(6) NOT NULL,
  is_active bit(1) NOT NULL,
  previous_school varchar(50) DEFAULT NULL,
  termination_date date DEFAULT NULL,
  created_by varchar(50) DEFAULT NULL,
    last_modified_by varchar(50) DEFAULT NULL,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (student_id),
  FOREIGN KEY (class_details_id) REFERENCES class_details (id)
) ;


-- db.fees_history definition




-- db.parents_details definition

CREATE TABLE IF NOT EXISTS  parents_details (
  id bigint NOT NULL AUTO_INCREMENT,
  contact varchar(20) DEFAULT NULL,
  name varchar(50) DEFAULT NULL,
  occupation varchar(20) DEFAULT NULL,
  qualification varchar(20) DEFAULT NULL,
  relationship varchar(20) DEFAULT NULL,
  aadhar_no varchar(15) NOT NULL,
  created_by varchar(50) DEFAULT NULL,
      last_modified_by varchar(50) DEFAULT NULL,
   created_date timestamp DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  student_id bigint NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (student_id) REFERENCES student (student_id)
) ;


CREATE TABLE IF NOT EXISTS payment_mode (
id bigint NOT NULL AUTO_INCREMENT,
name varchar(20) NOT NULL,
created_by varchar(50) DEFAULT NULL,
    last_modified_by varchar(50) DEFAULT NULL,
 PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS catalog (
  id bigint NOT NULL AUTO_INCREMENT,
  item_name varchar(255) NOT NULL ,
  cost_price double NOT NULL,
  sell_price double NOT NULL,
  description varchar(255) DEFAULT NULL,
  type varchar(255) NOT NULL,
  tag varchar(255) NOT NULL,
  std bigint DEFAULT NULL,
  session_id bigint NOT NULL,
  size varchar(20) DEFAULT NULL,
  created_by varchar(50) DEFAULT NULL,
  last_modified_by varchar(50) DEFAULT NULL,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY (item_name),
  FOREIGN KEY (session_id) REFERENCES session (id),
  FOREIGN KEY (std) REFERENCES class_details(id)
) ;




CREATE TABLE IF NOT EXISTS  fees (
  id bigint NOT NULL AUTO_INCREMENT,
  amount double NOT NULL,
  from_month varchar(10) DEFAULT NULL,
  to_month varchar(10) DEFAULT NULL,
  payment_mode_id bigint DEFAULT NULL,
  created_by varchar(50) DEFAULT NULL,
      last_modified_by varchar(50) DEFAULT NULL,
    created_date timestamp DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
 FOREIGN KEY (payment_mode_id) REFERENCES payment_mode (id)
);

-- db.stationery_item definition

CREATE TABLE IF NOT EXISTS purchase_order (
  id bigint NOT NULL AUTO_INCREMENT,
  amount double NOT NULL,
  purchase int(11) NOT NULL,
  payment_mode_id bigint DEFAULT NULL,
  created_by varchar(50) DEFAULT NULL,
      last_modified_by varchar(50) DEFAULT NULL,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (payment_mode_id) REFERENCES payment_mode (id)
) ;


CREATE TABLE IF NOT EXISTS  transaction_history (
  transaction_id bigint NOT NULL,
  payment_mode_id bigint NOT NULL,
  purchase_order_id bigint DEFAULT NULL,
  fees_id bigint DEFAULT NULL,
  total_amount double NOT NULL,
  student_id bigint NOT NULL,
  comments varchar(100) DEFAULT NULL,
  discount int DEFAULT 0,
  after_discount double NOT NULL,
  session_id bigint NOT NULL,
  created_by varchar(50) DEFAULT NULL,
      last_modified_by varchar(50) DEFAULT NULL,
  created_date datetime DEFAULT CURRENT_TIMESTAMP,
  last_modified_date datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (transaction_id),
  FOREIGN KEY (student_id) REFERENCES student (student_id),
  FOREIGN KEY (purchase_order_id) REFERENCES purchase_order (id),
   FOREIGN KEY (fees_id) REFERENCES fees (id),
    FOREIGN KEY (session_id) REFERENCES session (id)
) ;


CREATE TABLE IF NOT EXISTS line_item(
id bigint NOT NULL AUTO_INCREMENT,
 unit_price double NOT NULL,
 qty int(11) NOT NULL,
 amount double NOT NULL,
purchase_order_id bigint NOT NUll,
product_id bigint NOT NULL,
created_by varchar(50) DEFAULT NULL,
    last_modified_by varchar(50) DEFAULT NULL,
created_date timestamp DEFAULT CURRENT_TIMESTAMP,
 last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (purchase_order_id) REFERENCES purchase_order (id),
  FOREIGN KEY (product_id) REFERENCES catalog (id),
   PRIMARY KEY (id)
);




