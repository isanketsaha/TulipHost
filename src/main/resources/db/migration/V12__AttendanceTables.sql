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
