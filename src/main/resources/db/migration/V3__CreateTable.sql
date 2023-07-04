ALTER TABLE IF NOT EXISTS upload
ADD transaction_id bigint,
ADD FOREIGN KEY (transaction_id) REFERENCES transactions (transaction_id);

ALTER TABLE transactions
ADD COLUMN due_opted bit(1)  DEFAULT 0;

ALTER TABLE expense
ADD unit_price double NOT NULL;

CREATE TABLE IF NOT EXISTS dues (
  id BIGINT AUTO_INCREMENT NOT NULL,
   transaction_id BIGINT NULL,
   due_date datetime NOT NULL,
   amount DOUBLE NOT NULL,
   approved_by VARCHAR(255) NOT NULL,
   status VARCHAR(255) NOT NULL,
   penalty DOUBLE NOT NULL,
   created_by VARCHAR(50) NOT NULL,
   created_date datetime NULL,
   last_modified_by VARCHAR(50) NULL,
   last_modified_date datetime NULL,
   PRIMARY KEY (id),
   FOREIGN KEY (transaction_id) REFERENCES transactions (transaction_id)
);

CREATE TABLE IF NOT EXISTS dues_payment (
  id BIGINT AUTO_INCREMENT NOT NULL,
   amount DOUBLE NOT NULL,
   penalty DOUBLE NOT NULL,
   payment_mode VARCHAR(50) NOT NULL,
   due_id BIGINT NOT NULL,
   created_by VARCHAR(50) NOT NULL,
   created_date datetime NULL,
   last_modified_by VARCHAR(50) NULL,
   last_modified_date datetime NULL,
  	PRIMARY KEY (id),
  	FOREIGN KEY (due_id) REFERENCES dues (id)
);
