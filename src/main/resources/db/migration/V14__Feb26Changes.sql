-- ── Global documents (school-wide, no class/session constraint) ───────────
CREATE TABLE IF NOT EXISTS global_document (
  id                 bigint NOT NULL AUTO_INCREMENT,
  name               varchar(255) NOT NULL,
  description        longtext,
  category           varchar(50) NOT NULL,
  s3_key             varchar(500) NOT NULL,
  is_active          bit(1) NOT NULL DEFAULT 1,
  created_by         varchar(50),
  last_modified_by   varchar(50),
  created_date       timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Staff inventory request workflow ─────────────────────────────────────
CREATE TABLE IF NOT EXISTS inventory_request (
  id                 bigint         NOT NULL AUTO_INCREMENT,
  employee_id        bigint         NOT NULL,
  product_catalog_id bigint         NOT NULL,
  qty                int            NOT NULL,
  justification      longtext,
  status             varchar(20)    NOT NULL DEFAULT 'PENDING',
  machine_id         varchar(120),
  approved_by        varchar(100),
  approved_date      datetime,
  remarks            varchar(500),
  created_by         varchar(50),
  last_modified_by   varchar(50),
  created_date       timestamp      DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_inv_req_employee    FOREIGN KEY (employee_id)        REFERENCES employee (emp_id),
  CONSTRAINT fk_inv_req_product     FOREIGN KEY (product_catalog_id) REFERENCES product_catalog (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Move price off product_catalog (price lives on inventory.mrp) ─────────
ALTER TABLE product_catalog DROP COLUMN price;
