package com.tulip.host.web.rest;

import com.tulip.host.data.AppraisalParameterDTO;
import com.tulip.host.domain.EmployeeAppraisal;
import com.tulip.host.service.AppraisalService;
import com.tulip.host.web.rest.vm.AppraisalReviewVM;
import com.tulip.host.web.rest.vm.AppraisalVM;
import com.tulip.host.web.rest.vm.CloseReviewVM;
import com.tulip.host.web.rest.vm.ReleaseAppraisalVM;
import com.tulip.host.web.rest.vm.SelfAssessmentVM;
import com.tulip.host.web.rest.vm.SessionParameterVM;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appraisal")
@RequiredArgsConstructor
public class AppraisalController {

    private final AppraisalService appraisalService;

    @GetMapping("/parameters")
    public List<AppraisalParameterDTO> getParameters() {
        return appraisalService.getParameters();
    }

    @GetMapping("/session/{sessionId}/parameters")
    public List<AppraisalParameterDTO> getSessionParameters(@PathVariable Long sessionId) {
        return appraisalService.getSessionParameters(sessionId);
    }

    @PreAuthorize("hasAuthority('UG_PRINCIPAL') or hasAuthority('UG_ADMIN')")
    @PostMapping("/session/parameters")
    public ResponseEntity setSessionParameters(@Valid @RequestBody SessionParameterVM vm) {
        appraisalService.setSessionParameters(vm);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('UG_PRINCIPAL') or hasAuthority('UG_ADMIN')")
    @PostMapping
    public ResponseEntity<Long> saveAppraisal(@Valid @RequestBody AppraisalVM vm) {
        EmployeeAppraisal appraisal = appraisalService.saveAppraisal(vm);
        return ResponseEntity.ok(appraisal.getId());
    }

    @PreAuthorize("hasAuthority('UG_PRINCIPAL') or hasAuthority('UG_ADMIN')")
    @PostMapping("/{id}/review")
    public ResponseEntity addReview(@PathVariable Long id, @Valid @RequestBody AppraisalReviewVM vm) {
        vm.setAppraisalId(id);
        appraisalService.addReview(vm);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('UG_PRINCIPAL') or hasAuthority('UG_ADMIN')")
    @PutMapping("/review/{id}/close")
    public ResponseEntity closeReview(@PathVariable Long id, @Valid @RequestBody CloseReviewVM vm) {
        appraisalService.closeReview(id, vm);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/review/{id}/self-assessment")
    public ResponseEntity submitSelfAssessment(@PathVariable Long id, @Valid @RequestBody SelfAssessmentVM vm) {
        vm.setReviewId(id);
        appraisalService.submitSelfAssessment(vm);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('UG_PRINCIPAL') or hasAuthority('UG_ADMIN')")
    @PutMapping("/{id}/release")
    public ResponseEntity releaseAppraisal(@PathVariable Long id, @RequestBody(required = false) ReleaseAppraisalVM vm) {
        appraisalService.releaseAppraisal(id, vm);
        return ResponseEntity.ok().build();
    }
}
