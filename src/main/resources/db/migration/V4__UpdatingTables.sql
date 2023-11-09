
ALTER TABLE student
ADD COLUMN picture_id bigint  DEFAULT NULL,
ADD COLUMN aadhaar varchar(20)  DEFAULT NULL,
Add COLUMN letter_id bigint  DEFAULT NULL,
ADD FOREIGN KEY (picture_id) REFERENCES upload (id),
ADD FOREIGN KEY (letter_id) REFERENCES upload (id);

ALTER TABLE employee
ADD COLUMN picture_id bigint  DEFAULT NULL,
ADD COLUMN termination_date DATETIME DEFAULT NULL,
ADD COLUMN aadhaar varchar(20)  DEFAULT NULL,
Add COLUMN letter_id bigint  DEFAULT NULL,
MODIFY COLUMN reset_credential bit(1) DEFAULT 1,
ADD FOREIGN KEY (picture_id) REFERENCES upload (id),
ADD FOREIGN KEY (letter_id) REFERENCES upload (id);

ALTER TABLE transactions
ADD COLUMN invoice_id bigint  DEFAULT NULL,
ADD FOREIGN KEY (invoice_id) REFERENCES upload (id);


CREATE TABLE IF NOT EXISTS  transport_catalog (
  id bigint NOT NULL AUTO_INCREMENT,
  location varchar(255) NOT NULL,
  amount double NOT NULL,
  session_id bigint NOT NULL,
  distance int  DEFAULT NULL,
  created_by varchar(50) DEFAULT NULL,
  last_modified_by DATETIME ON UPDATE CURRENT_TIMESTAMP,
  created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  last_modified_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY (location, session_id),
  FOREIGN KEY (session_id) REFERENCES session (id)
);

CREATE TABLE IF NOT EXISTS  student_to_transport (
    transport_id bigint NOT NULL,
    student_id bigint NOT NULL,
    created_date timestamp DEFAULT CURRENT_TIMESTAMP,
    start_date timestamp  DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (transport_id, student_id),
    FOREIGN KEY (student_id) REFERENCES student (student_id),
    FOREIGN KEY (transport_id) REFERENCES transport_catalog (id)
);

ALTER TABLE fees_catalog
MODIFY COLUMN price double DEFAULT NULL,
ADD COLUMN transport_id bigint  DEFAULT NULL,
ADD FOREIGN KEY (transport_id) REFERENCES transport_catalog (id);


