package com.tulip.host.web.rest;

import com.tulip.host.data.AcademicUploadDTO;
import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.ClassListDTO;
import com.tulip.host.data.ClassroomAttendanceDTO;
import com.tulip.host.data.EmployeeBasicDTO;
import com.tulip.host.data.ExamMarksUploadDTO;
import com.tulip.host.data.FeesCatalogDTO;
import com.tulip.host.data.PlannerDataDTO;
import com.tulip.host.enums.ExamType;
import com.tulip.host.service.AttendanceService;
import com.tulip.host.service.BehaviourService;
import com.tulip.host.service.ClassroomService;
import com.tulip.host.service.MarksService;
import com.tulip.host.service.PlannerService;
import com.tulip.host.web.rest.vm.FeesFilterVM;
import com.tulip.host.web.rest.vm.PromoteStudentVM;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/classroom")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;
    private final AttendanceService attendanceService;
    private final BehaviourService behaviourService;
    private final PlannerService plannerService;
    private final MarksService marksService;

    @GetMapping("/all")
    List<ClassListDTO> fetch(@RequestParam Optional<Integer> sessionId) {
        return classroomService.fetchAllClasses(sessionId.orElseGet(() -> 0));
    }

    @GetMapping("/details/{classroomId}")
    public ClassDetailDTO fetchStudentList(@PathVariable Long classroomId) {
        return classroomService.fetchClassDetails(classroomId);
    }

    @PostMapping("/feesByRule")
    public Map<String, List<FeesCatalogDTO>> getFees(@Valid @RequestBody FeesFilterVM rule) {
        return classroomService.getFees(rule);
    }

    @PostMapping("/student-promote")
    public void promoteStudents(@Valid @RequestBody PromoteStudentVM promoteStudentVM)
        throws ValidationException, jakarta.xml.bind.ValidationException {
        classroomService.promoteStudents(promoteStudentVM);
    }

    @GetMapping("/available-teachers")
    public List<EmployeeBasicDTO> fetchAvailableTeachers(@RequestParam Long classroomId) {
        return classroomService.fetchAvailableTeachers(classroomId);
    }

    @PutMapping("/head-teacher")
    public void updateHeadTeacher(@RequestParam Long classroomId, @RequestParam Long employeeId) {
        classroomService.updateHeadTeacher(classroomId, employeeId);
    }

    @GetMapping("/id")
    public Long fetchClassId(@RequestParam(value = "std") String std, @RequestParam(name = "sessionId") Long sessionId) {
        return classroomService.fetchClassDetails(std, sessionId);
    }

    // ── Attendance ────────────────────────────────────────────────

    @GetMapping("/attendance/template")
    public ResponseEntity<byte[]> downloadAttendanceTemplate(@RequestParam Long classroomId, @RequestParam LocalDate weekStart)
        throws Exception {
        byte[] template = attendanceService.generateTemplate(classroomId, weekStart);
        ClassDetailDTO classDetails = classroomService.fetchClassDetails(classroomId);
        String filename = "attendance_" + classDetails.getStd() + "_" + weekStart + ".xlsx";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(template);
    }

    @PostMapping("/attendance/upload")
    public ClassroomAttendanceDTO uploadAttendance(
        @RequestParam Long classroomId,
        @RequestParam LocalDate weekStart,
        @RequestParam("file") MultipartFile file
    ) throws Exception {
        return attendanceService.processUpload(classroomId, weekStart, file);
    }

    @GetMapping("/attendance")
    public List<ClassroomAttendanceDTO> getAttendanceHistory(@RequestParam Long classroomId) {
        return attendanceService.getClassroomAttendanceHistory(classroomId);
    }

    // ── Behaviour Assessment ──────────────────────────────────────

    @GetMapping("/behaviour/template")
    public ResponseEntity<byte[]> downloadBehaviourTemplate(
        @RequestParam Long classroomId,
        @RequestParam String subjectKey,
        @RequestParam LocalDate weekStart
    ) throws Exception {
        byte[] template = behaviourService.generateTemplate(classroomId, subjectKey, weekStart);
        ClassDetailDTO classDetails = classroomService.fetchClassDetails(classroomId);
        String filename = "behaviour_" + classDetails.getStd() + "_" + subjectKey + "_" + weekStart + ".xlsx";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(template);
    }

    @PostMapping("/behaviour/upload")
    public AcademicUploadDTO uploadBehaviour(@RequestParam Long classroomId, @RequestParam("file") MultipartFile file) throws Exception {
        return behaviourService.processUpload(classroomId, file);
    }

    @GetMapping("/behaviour/history")
    public List<AcademicUploadDTO> getBehaviourHistory(@RequestParam Long classroomId) {
        return behaviourService.getHistory(classroomId);
    }

    // ── Weekly Planner ────────────────────────────────────────────

    @GetMapping("/planner/template")
    public ResponseEntity<byte[]> downloadPlannerTemplate(
        @RequestParam Long classroomId,
        @RequestParam String subjectKey,
        @RequestParam LocalDate weekStart
    ) throws Exception {
        byte[] template = plannerService.generateTemplate(classroomId, subjectKey, weekStart);
        ClassDetailDTO classDetails = classroomService.fetchClassDetails(classroomId);
        String filename = "planner_" + classDetails.getStd() + "_" + subjectKey + "_" + weekStart + ".xlsx";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(template);
    }

    @PostMapping("/planner/upload")
    public AcademicUploadDTO uploadPlanner(@RequestParam Long classroomId, @RequestParam("file") MultipartFile file) throws Exception {
        return plannerService.processUpload(classroomId, file);
    }

    @GetMapping("/planner/history")
    public List<AcademicUploadDTO> getPlannerHistory(@RequestParam Long classroomId) {
        return plannerService.getHistory(classroomId);
    }

    @GetMapping("/planner/data")
    public PlannerDataDTO getPlannerData(
        @RequestParam Long classroomId,
        @RequestParam String subjectKey,
        @RequestParam LocalDate weekStart
    ) {
        return plannerService.getPlannerData(classroomId, subjectKey, weekStart);
    }

    // ── Exam Marks ───────────────────────────────────────────────

    @GetMapping("/marks/template")
    public ResponseEntity<byte[]> downloadMarksTemplate(
        @RequestParam Long classroomId,
        @RequestParam ExamType examType,
        @RequestParam String examName
    ) throws Exception {
        byte[] template = marksService.generateTemplate(classroomId, examType, examName);
        ClassDetailDTO classDetails = classroomService.fetchClassDetails(classroomId);
        String filename = "marks_" + classDetails.getStd() + "_" + examName.replace(" ", "_") + ".xlsx";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(template);
    }

    @GetMapping("/marks/history")
    public List<ExamMarksUploadDTO> getMarksHistory(@RequestParam Long classroomId) {
        return marksService.getExamHistory(classroomId);
    }

    @PostMapping("/marks/upload")
    public void uploadMarks(
        @RequestParam Long classroomId,
        @RequestParam ExamType examType,
        @RequestParam String examName,
        @RequestParam("file") MultipartFile file
    ) throws Exception {
        marksService.processUpload(classroomId, examType, examName, file);
    }
}
