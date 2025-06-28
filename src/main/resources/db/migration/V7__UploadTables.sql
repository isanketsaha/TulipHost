
ALTER TABLE system_document
DROP COLUMN uid,
DROP COLUMN file_name,
DROP COLUMN status,
DROP COLUMN file_type,
DROP COLUMN file_size,
DROP COLUMN display_order,
ADD name varchar(150) NOT NULL;


create table if not exists s3_upload (
    id bigint NOT NULL AUTO_INCREMENT,
    uid varchar(100) NOT NULL,
    file_name varchar(150) DEFAULT NULL,
    status varchar(50) DEFAULT NULL,
    file_type varchar(50) DEFAULT NULL,
    file_size int DEFAULT 0,
    system_document_id bigint DEFAULT NULL,
    created_by varchar(50) DEFAULT NULL,
    last_modified_by varchar(50) DEFAULT NULL,
    created_date timestamp DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (system_document_id) REFERENCES system_document (id)
);

ALTER TABLE leave_type
ADD count SMALLINT DEFAULT 0,
ADD session_id BIGINT DEFAULT 0 NOT NULL,
ADD CONSTRAINT fk_leave_type_session FOREIGN KEY (session_id)
REFERENCES session (id) ON DELETE RESTRICT ON UPDATE CASCADE;
