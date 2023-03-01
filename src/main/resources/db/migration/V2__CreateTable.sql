--


CREATE TABLE IF NOT EXISTS  upload (
  id bigint NOT NULL AUTO_INCREMENT,
  uid varchar(100) NOT NULL,
  file_name varchar(150) DEFAULT NULL,
  status varchar(50) DEFAULT NULL,
  file_type varchar(50) DEFAULT NULL,
  file_size int DEFAULT 0,
  document_type varchar(50) DEFAULT NULL,
  student_id bigint DEFAULT NULL,
  dependent_id bigint DEFAULT NULL,
  employee_id bigint DEFAULT NULL,
 created_by varchar(50) DEFAULT NULL,
 last_modified_by varchar(50) DEFAULT NULL,
 created_date timestamp DEFAULT CURRENT_TIMESTAMP,
 last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (student_id) REFERENCES student (student_id),
  FOREIGN KEY (dependent_id) REFERENCES dependent (id),
  FOREIGN KEY (employee_id) REFERENCES employee (emp_id),
  PRIMARY KEY (id),
  UNIQUE KEY (uid)
);
