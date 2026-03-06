-- Level 1: classroom-level weekly attendance summary (1 row per classroom per week upload)
CREATE TABLE IF NOT EXISTS classroom_attendance (
  id bigint NOT NULL AUTO_INCREMENT,
  class_detail_id bigint NOT NULL,
  week_start_date date NOT NULL,
  total_students int NOT NULL DEFAULT 0,
  present_count int NOT NULL DEFAULT 0,
  absent_count int NOT NULL DEFAULT 0,
  leave_count int NOT NULL DEFAULT 0,
  total_working_days int NOT NULL DEFAULT 0,
  holiday_count int NOT NULL DEFAULT 0,
  file_uid varchar(255) DEFAULT NULL,  -- S3 reference for the uploaded Excel
  created_by varchar(50) DEFAULT NULL,
  last_modified_by varchar(50) DEFAULT NULL,
  created_date timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_classroom_attendance_class_week (class_detail_id, week_start_date),
  CONSTRAINT fk_classroom_attendance_class FOREIGN KEY (class_detail_id) REFERENCES class_details (id)
);

-- Level 2: per-student exception records (absent/leave only — present students are NOT stored)
-- ABSENT: date = the specific absent day, number_of_days = 1, classroom_attendance_id = parent upload
-- LEAVE:  date = leave start date, number_of_days = N, classroom_attendance_id = NULL (pre-approved, not tied to an upload)
CREATE TABLE IF NOT EXISTS student_attendance (
  id bigint NOT NULL AUTO_INCREMENT,
  classroom_attendance_id bigint DEFAULT NULL,
  student_id bigint NOT NULL,
  date date NOT NULL,
  number_of_days tinyint NOT NULL DEFAULT 1,
  status varchar(10) NOT NULL,  -- ABSENT | LEAVE
  remarks varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_student_attendance_classroom FOREIGN KEY (classroom_attendance_id) REFERENCES classroom_attendance (id),
  CONSTRAINT fk_student_attendance_student FOREIGN KEY (student_id) REFERENCES student (student_id)
);


-- 1. Predefined parameter library
CREATE TABLE appraisal_parameter (
  id               bigint NOT NULL AUTO_INCREMENT,
  name             varchar(100) NOT NULL,
  active           bit(1) NOT NULL DEFAULT 1,
  created_by       varchar(50),
  last_modified_by varchar(50),
  created_date     timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);
INSERT INTO appraisal_parameter (name) VALUES
  ('Punctuality'), ('Teaching Quality'), ('Leadership'),
  ('Communication'), ('Initiative'), ('Teamwork'),
  ('Discipline'), ('Subject Knowledge');

-- 2. Which parameters are active for a given session
CREATE TABLE session_appraisal_parameter (
  id           bigint NOT NULL AUTO_INCREMENT,
  session_id   bigint NOT NULL,
  parameter_id bigint NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_session_param (session_id, parameter_id),
  CONSTRAINT fk_sap_session FOREIGN KEY (session_id)   REFERENCES session (id),
  CONSTRAINT fk_sap_param   FOREIGN KEY (parameter_id) REFERENCES appraisal_parameter (id)
);

-- 3. One appraisal per employee per session
CREATE TABLE employee_appraisal (
  id               bigint NOT NULL AUTO_INCREMENT,
  employee_id      bigint NOT NULL,
  session_id       bigint NOT NULL,
  new_salary       int(7),
  final_rating     decimal(4,2) COMMENT 'Cached avg of all review ratings',
  status           varchar(10) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT | RELEASED',
  created_by       varchar(50),
  last_modified_by varchar(50),
  created_date     timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_appraisal_emp_session (employee_id, session_id),
  CONSTRAINT fk_appraisal_emp     FOREIGN KEY (employee_id) REFERENCES employee (emp_id),
  CONSTRAINT fk_appraisal_session FOREIGN KEY (session_id)  REFERENCES session (id)
);

-- 4. Multiple review cycles per appraisal
-- start_date: auto-set when review is opened; end_date: set when review is closed
CREATE TABLE appraisal_review (
  id               bigint NOT NULL AUTO_INCREMENT,
  appraisal_id     bigint NOT NULL,
  start_date       date NOT NULL COMMENT 'Auto-logged when review is created',
  end_date         date DEFAULT NULL COMMENT 'Set when review is closed',
  review_type      VARCHAR(20) NOT NULL DEFAULT 'MID_YEAR' COMMENT 'MID_YEAR or END_YEAR',
  review_status    VARCHAR(10) NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN or CLOSED',
  observations     text COMMENT 'Admin notes submitted on close',
  self_assessment  text COMMENT 'Employee self-report for this review cycle',
  self_assessment_attachments TEXT COMMENT 'Comma-separated uploaded file UUIDs from employee',
  rating           decimal(4,2) COMMENT 'Cached avg of parameter scores, calculated on close',
  created_by       varchar(50),
  last_modified_by varchar(50),
  created_date     timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_review_appraisal FOREIGN KEY (appraisal_id) REFERENCES employee_appraisal (id)
);

-- 5. Parameter scores per review (1-10)
CREATE TABLE review_parameter_score (
  id           bigint NOT NULL AUTO_INCREMENT,
  review_id    bigint NOT NULL,
  parameter_id bigint NOT NULL,
  score        tinyint NOT NULL COMMENT '1 to 10',
  PRIMARY KEY (id),
  UNIQUE KEY uq_review_param (review_id, parameter_id),
  CONSTRAINT fk_rps_review FOREIGN KEY (review_id)    REFERENCES appraisal_review (id),
  CONSTRAINT fk_rps_param  FOREIGN KEY (parameter_id) REFERENCES appraisal_parameter (id)
);

ALTER TABLE employee ADD COLUMN working_days_in_week INT DEFAULT NULL;

ALTER TABLE student ADD COLUMN weight DOUBLE;
ALTER TABLE student ADD COLUMN height DOUBLE;


-- Per-appraisal parameter selection (which parameters this specific employee is rated on)
CREATE TABLE appraisal_selected_parameter (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    appraisal_id BIGINT NOT NULL,
    parameter_id BIGINT NOT NULL,
    UNIQUE KEY uq_appraisal_param (appraisal_id, parameter_id),
    FOREIGN KEY (appraisal_id) REFERENCES employee_appraisal (id),
    FOREIGN KEY (parameter_id) REFERENCES appraisal_parameter (id)
);
