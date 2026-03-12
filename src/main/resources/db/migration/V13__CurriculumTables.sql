-- ── Assessment parameters (used for behaviour scoring) ───────────
CREATE TABLE IF NOT EXISTS assessment_parameter (
  id                 bigint NOT NULL AUTO_INCREMENT,
  name               varchar(100) NOT NULL,
  active             bit(1) NOT NULL DEFAULT 1,
  created_by         varchar(50),
  last_modified_by   varchar(50),
  created_date       timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_assessment_param_name (name)
);
INSERT INTO assessment_parameter (name) VALUES
  ('Classwork Completion'), ('Participation'), ('Homework Completion'),
  ('Classroom Behaviour'), ('Cleanliness'), ('Life Skills'),
  ('Discipline'), ('Subject Understanding'),
  ('Creativity'), ('Critical Thinking'), ('Collaboration');

-- ── Per-classroom subject configuration ──────────────────────────
CREATE TABLE IF NOT EXISTS class_subject (
  id                 bigint NOT NULL AUTO_INCREMENT,
  class_detail_id    bigint NOT NULL,
  subject_key        varchar(50) NOT NULL,
  display_name       varchar(100) DEFAULT NULL,
  included           bit(1) NOT NULL DEFAULT 1,
  term_mark_type     varchar(10) DEFAULT NULL,  -- FULL (80 ext + 10 int) | HALF (50 ext only)
  created_by         varchar(50),
  last_modified_by   varchar(50),
  created_date       timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_class_subject (class_detail_id, subject_key),
  CONSTRAINT fk_class_subject_class FOREIGN KEY (class_detail_id) REFERENCES class_details (id)
);

-- Exam types enabled for a class-subject (CT, TERM)
CREATE TABLE IF NOT EXISTS class_subject_exam (
  class_subject_id bigint NOT NULL,
  exam_type        varchar(20) NOT NULL,
  PRIMARY KEY (class_subject_id, exam_type),
  CONSTRAINT fk_cse_subject FOREIGN KEY (class_subject_id) REFERENCES class_subject (id)
);

-- Assessment parameters assigned to a class-subject (drives behaviour scoring template)
CREATE TABLE IF NOT EXISTS class_subject_assessment_param (
  class_subject_id        bigint NOT NULL,
  assessment_parameter_id bigint NOT NULL,
  PRIMARY KEY (class_subject_id, assessment_parameter_id),
  CONSTRAINT fk_csap_subject FOREIGN KEY (class_subject_id)        REFERENCES class_subject (id),
  CONSTRAINT fk_csap_param   FOREIGN KEY (assessment_parameter_id) REFERENCES assessment_parameter (id)
);

-- ── Existing table alterations ────────────────────────────────────

-- Link books/materials to a subject
ALTER TABLE product_catalog ADD COLUMN subject varchar(50) DEFAULT NULL;

-- Add upload_type to s3_upload for path categorization
ALTER TABLE s3_upload ADD COLUMN upload_type varchar(30) DEFAULT NULL;

-- MIME types can exceed 50 chars
ALTER TABLE s3_upload MODIFY COLUMN file_type VARCHAR(255) DEFAULT NULL;

-- Replace raw file_uid on attendance with FK to s3_upload
ALTER TABLE classroom_attendance
    DROP COLUMN file_uid,
    ADD COLUMN s3_upload_id bigint DEFAULT NULL,
    ADD CONSTRAINT fk_classroom_attendance_s3 FOREIGN KEY (s3_upload_id) REFERENCES s3_upload (id);

-- Tag documents to a subject (optional)
ALTER TABLE system_document ADD COLUMN subject_key varchar(50) DEFAULT NULL;

-- ── Academic uploads (behaviour + planner) ────────────────────────
-- One record per classroom × subject × week × upload type
CREATE TABLE IF NOT EXISTS classroom_academic_upload (
    id                 bigint NOT NULL AUTO_INCREMENT,
    class_detail_id    bigint NOT NULL,
    subject_key        varchar(50) NOT NULL,
    week_start_date    date NOT NULL,
    upload_type        varchar(20) NOT NULL,  -- 'BEHAVIOUR' | 'PLANNER'
    s3_upload_id       bigint DEFAULT NULL,
    created_by         varchar(50) DEFAULT NULL,
    last_modified_by   varchar(50) DEFAULT NULL,
    created_date       timestamp DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_academic_upload (class_detail_id, subject_key, week_start_date, upload_type),
    CONSTRAINT fk_aupload_class FOREIGN KEY (class_detail_id) REFERENCES class_details (id),
    CONSTRAINT fk_aupload_s3   FOREIGN KEY (s3_upload_id)    REFERENCES s3_upload (id)
);

-- Per-student behaviour scores (1–10 per assessment parameter per week)
CREATE TABLE IF NOT EXISTS student_behaviour_score (
    id                      bigint NOT NULL AUTO_INCREMENT,
    academic_upload_id      bigint NOT NULL,
    student_id              bigint NOT NULL,
    assessment_parameter_id bigint NOT NULL,
    score                   tinyint NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_bscore_upload  FOREIGN KEY (academic_upload_id)     REFERENCES classroom_academic_upload (id),
    CONSTRAINT fk_bscore_student FOREIGN KEY (student_id)              REFERENCES student (student_id),
    CONSTRAINT fk_bscore_param   FOREIGN KEY (assessment_parameter_id) REFERENCES assessment_parameter (id)
);

-- ── Exam marks ────────────────────────────────────────────────────
-- One upload record per classroom × exam type × exam name
CREATE TABLE IF NOT EXISTS exam_marks_upload (
  id                 bigint NOT NULL AUTO_INCREMENT,
  class_detail_id    bigint NOT NULL,
  exam_type          varchar(10) NOT NULL,  -- 'CT' | 'TERM'
  exam_name          varchar(50) NOT NULL,  -- 'CT 1' | 'Mid Term' | 'End Term'
  s3_upload_id       bigint DEFAULT NULL,
  created_by         varchar(50) DEFAULT NULL,
  last_modified_by   varchar(50) DEFAULT NULL,
  created_date       timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_exam_upload (class_detail_id, exam_type, exam_name),
  CONSTRAINT fk_eup_class FOREIGN KEY (class_detail_id) REFERENCES class_details (id),
  CONSTRAINT fk_eup_s3    FOREIGN KEY (s3_upload_id)    REFERENCES s3_upload (id)
);

-- Per-student exam scores: CT → TOTAL (/10); TERM → INTERNAL (/10) + EXTERNAL (/80 or /50)
CREATE TABLE IF NOT EXISTS student_exam_score (
  id                 bigint NOT NULL AUTO_INCREMENT,
  exam_upload_id     bigint NOT NULL,
  student_id         bigint NOT NULL,
  subject_key        varchar(50) NOT NULL,
  score_type         varchar(10) NOT NULL,  -- 'TOTAL' | 'INTERNAL' | 'EXTERNAL'
  marks              smallint NOT NULL,
  created_by         varchar(50) DEFAULT NULL,
  last_modified_by   varchar(50) DEFAULT NULL,
  created_date       timestamp DEFAULT CURRENT_TIMESTAMP,
  last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_exam_score (exam_upload_id, student_id, subject_key, score_type),
  CONSTRAINT fk_escore_upload  FOREIGN KEY (exam_upload_id) REFERENCES exam_marks_upload (id),
  CONSTRAINT fk_escore_student FOREIGN KEY (student_id)     REFERENCES student (student_id)
);

-- ── Weekly planner entries ────────────────────────────────────────
-- Parsed values from teacher-filled planner template (one row per field per upload)
-- Fixed fields: topic_subtopic | teaching_aids | activity_explanation |
--               activity_learning_objective | learning_objective | learning_outcome |
--               evaluation | homework_worksheet
CREATE TABLE IF NOT EXISTS planner_entry (
    id                 bigint NOT NULL AUTO_INCREMENT,
    academic_upload_id bigint NOT NULL,
    field_key          varchar(100) NOT NULL,
    field_value        text,
    created_by         varchar(50) DEFAULT NULL,
    last_modified_by   varchar(50) DEFAULT NULL,
    created_date       timestamp DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamp DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_planner_entry (academic_upload_id, field_key),
    CONSTRAINT fk_pentry_upload FOREIGN KEY (academic_upload_id) REFERENCES classroom_academic_upload (id)
);
