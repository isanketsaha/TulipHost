CREATE TABLE if not exists state_machine (
 machine_id VARCHAR(255) NOT NULL,
 state VARCHAR(255),
 state_machine_context BLOB,
 created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 PRIMARY KEY (machine_id)
);

-- Add indexes for better performance
CREATE INDEX idx_state_machine_state ON state_machine(state);
CREATE INDEX idx_state_machine_created_date ON state_machine(created_date);


CREATE TABLE if not exists state_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    machine_id VARCHAR(255) NOT NULL,
    from_state VARCHAR(255),
    to_state VARCHAR(255),
    event VARCHAR(255) NOT NULL,
    success BOOLEAN NOT NULL DEFAULT TRUE,
    error_message VARCHAR(1000),
    created_by VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Add indexes for better performance
CREATE INDEX idx_state_audit_machine_id ON state_history(machine_id);
CREATE INDEX idx_state_audit_created_date ON state_history(created_date);
CREATE INDEX idx_state_audit_success ON state_history(success);

CREATE TABLE if not exists action_notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(40) NOT NULL,
    entity_id BIGINT NOT NULL,
    machine_id VARCHAR(120) NOT NULL,
    requested_user VARCHAR(50),
    current_state VARCHAR(40) NOT NULL,
    approver_role VARCHAR(30) NOT NULL,
    approver_user_id VARCHAR(50),
    status VARCHAR(15) NOT NULL DEFAULT 'PENDING',
    created_by VARCHAR(50) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Add indexes for better performance
CREATE INDEX idx_action_notification_role_status ON action_notification(approver_role, status);
CREATE INDEX idx_action_notification_user_status ON action_notification(approver_user_id, status);
CREATE INDEX idx_action_notification_machine ON action_notification(machine_id);
CREATE INDEX idx_action_notification_status ON action_notification(status);
CREATE INDEX idx_action_notification_created_date ON action_notification(created_date);

alter table employee_leave MODIFY approved_by VARCHAR(60);
