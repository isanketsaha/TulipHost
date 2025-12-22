CREATE TABLE IF NOT EXISTS outbound_communication (
  id bigint NOT NULL AUTO_INCREMENT,
  channel varchar(20) NOT NULL,
  recipient varchar(255) NOT NULL,
  entity_type varchar(50) NOT NULL,
  entity_id bigint DEFAULT NULL,
  subject varchar(255) DEFAULT NULL,
  content longtext,
  status varchar(20) NOT NULL DEFAULT 'PENDING',
  provider_message_id varchar(255) DEFAULT NULL,
  error text DEFAULT NULL,
  sent_date timestamp NULL DEFAULT NULL,
  created_by varchar(50) DEFAULT NULL,
  last_modified_by varchar(50) DEFAULT NULL,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

ALTER TABLE employee
    ADD COLUMN email varchar(255) DEFAULT NULL AFTER experience,
    ADD COLUMN doc_submitted text DEFAULT NULL AFTER email;
