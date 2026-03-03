# Attendance System Architecture

## Overview

This document outlines the architecture for student attendance tracking in TulipHost.

## Requirements

| Requirement             | Decision                                   |
| ----------------------- | ------------------------------------------ |
| Granularity             | Once per day per class                     |
| Status values           | Present / Absent only                      |
| Editing past attendance | Not allowed                                |
| Reports                 | Class monthly summary, student period view |
| Notifications           | Monthly SMS to parents                     |

---

## Database Schema

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  ┌────────────────────┐       ┌────────────────────┐       │
│  │  attendance_record │       │  student_absence   │       │
│  ├────────────────────┤       ├────────────────────┤       │
│  │ id (PK)            │       │ id (PK)            │       │
│  │ class_id (FK)      │◄──────│ record_id (FK)     │       │
│  │ attendance_date    │       │ student_id (FK)    │       │
│  │ marked_by (FK)     │       │ reason (nullable)  │       │
│  │ total_present      │       └─────────┬──────────┘       │
│  │ total_absent       │                 │                  │
│  │ created_date       │                 ▼                  │
│  └─────────┬──────────┘       ┌────────────────────┐       │
│            │                  │      student       │       │
│            ▼                  └────────────────────┘       │
│  ┌────────────────────┐                                    │
│  │   class_details    │                                    │
│  └────────────────────┘                                    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Table: attendance_record

One row per class per day. Confirms attendance was taken.

| Column          | Type      | Notes                        |
| --------------- | --------- | ---------------------------- |
| id              | BIGINT PK | Auto-increment               |
| class_id        | BIGINT FK | → class_details              |
| attendance_date | DATE      |                              |
| marked_by       | BIGINT FK | → employee (teacher)         |
| total_present   | INT       | Denormalized for quick stats |
| total_absent    | INT       | Denormalized for quick stats |
| created_date    | TIMESTAMP | Audit                        |
| **UNIQUE**      |           | (class_id, attendance_date)  |

### Table: student_absence

Only stores absent students. If a student is not in this table for a given record, they were present.

| Column     | Type         | Notes                   |
| ---------- | ------------ | ----------------------- |
| id         | BIGINT PK    | Auto-increment          |
| record_id  | BIGINT FK    | → attendance_record     |
| student_id | BIGINT FK    | → student               |
| reason     | VARCHAR(100) | Optional                |
| **UNIQUE** |              | (record_id, student_id) |

---

## Storage Estimation

| Scenario                                   | Rows/Year                                  |
| ------------------------------------------ | ------------------------------------------ |
| Naive approach (1 row per student per day) | 200 × 240 = **48,000**                     |
| This design (absences only ~8%)            | 240 records + ~3,840 absences = **~4,100** |

**~12x reduction in storage**

---

## Workflow

```
Teacher opens Class 5 attendance for today
            │
            ▼
┌─────────────────────────────────┐
│  Load all active students       │
│  in class (from student_to_class)│
│  Default: all checkboxes = ✓    │
└─────────────────────────────────┘
            │
            ▼
┌─────────────────────────────────┐
│  Teacher unchecks absent        │
│  students, clicks Submit        │
└─────────────────────────────────┘
            │
            ▼
┌─────────────────────────────────┐
│  INSERT attendance_record       │
│  (class_id, date, totals)       │
│                                 │
│  INSERT student_absence         │
│  (only for unchecked students)  │
└─────────────────────────────────┘
```

---

## Query Patterns

### 1. Was attendance taken for Class 5 today?

```sql
SELECT * FROM attendance_record
WHERE class_id = 5 AND attendance_date = CURRENT_DATE
```

### 2. Who was absent on a specific day?

```sql
SELECT s.name FROM student_absence sa
JOIN student s ON sa.student_id = s.id
JOIN attendance_record ar ON sa.record_id = ar.id
WHERE ar.class_id = 5 AND ar.attendance_date = '2026-03-01'
```

### 3. Monthly summary for a class

```sql
SELECT
  SUM(total_present) as total_present,
  SUM(total_absent) as total_absent,
  COUNT(*) as days_recorded
FROM attendance_record
WHERE class_id = 5
  AND attendance_date BETWEEN '2026-03-01' AND '2026-03-31'
```

### 4. Student's attendance for a period

```sql
SELECT
  ar.attendance_date,
  CASE WHEN sa.id IS NULL THEN 'PRESENT' ELSE 'ABSENT' END as status
FROM attendance_record ar
LEFT JOIN student_absence sa ON ar.id = sa.record_id AND sa.student_id = ?
WHERE ar.class_id = ?
  AND ar.attendance_date BETWEEN ? AND ?
ORDER BY ar.attendance_date
```

---

## Monthly Parent SMS Integration

Leverage existing `OutboundCommunicationService` + `SmsCommunicationStrategy`:

```
┌──────────────────────────────────────────────────────────┐
│  ScheduledService (1st of every month)                   │
│                                                          │
│  1. For each class, compute monthly summary per student  │
│  2. Build message: "Your ward X attended 22/24 days      │
│     (91.6%) in Feb 2026"                                 │
│  3. Queue via OutboundCommunicationService               │
│  4. Send via SmsCommunicationStrategy                    │
└──────────────────────────────────────────────────────────┘
```

---

## Integration with Existing Entities

| Existing Entity      | Attendance Relationship                        |
| -------------------- | ---------------------------------------------- |
| `ClassDetail`        | Links to `attendance_record.class_id`          |
| `Student`            | Links to `student_absence.student_id`          |
| `Employee` (teacher) | `marked_by` in `attendance_record`             |
| `Session`            | Filter by session.fromDate/toDate              |
| `AcademicCalendar`   | Exclude holidays from working days calculation |

---

## New Components

### Domain Entities

```
com.tulip.host.domain.AttendanceRecord
com.tulip.host.domain.StudentAbsence
```

### DTOs

```
AttendanceMarkDTO         - Input: list of absent student IDs
ClassAttendanceSummaryDTO - Output: monthly class summary
StudentAttendanceDTO      - Output: student's attendance view
```

### Services

```
AttendanceService
  - markAttendance(classId, date, absentStudentIds, teacherId)
  - getClassSummary(classId, month)
  - getStudentAttendance(studentId, fromDate, toDate)
  - isAttendanceMarked(classId, date)
```

---

## Edge Cases

| Case                     | Handling                                                  |
| ------------------------ | --------------------------------------------------------- |
| All students present     | 1 row in `attendance_record`, 0 rows in `student_absence` |
| Attendance not taken     | No row in `attendance_record` for that date               |
| Student joins mid-month  | Only count days after enrollment                          |
| Student leaves mid-month | Only count days before termination                        |
| Holidays                 | Skip via `AcademicCalendar` where `eventType = HOLIDAY`   |

---

## Future Considerations

- **Data retention**: Archive detailed records older than 2 years to summary tables
- **Period-wise attendance**: If needed later, add `period` column to `attendance_record`
- **Biometric integration**: Can feed into this system via API
