package com.tulip.host.service;

import com.tulip.host.data.AppraisalParameterDTO;
import com.tulip.host.data.EmployeeAppraisalDTO;
import com.tulip.host.domain.AppraisalParameter;
import com.tulip.host.domain.AppraisalReview;
import com.tulip.host.domain.AppraisalSelectedParameter;
import com.tulip.host.domain.Employee;
import com.tulip.host.domain.EmployeeAppraisal;
import com.tulip.host.domain.ReviewParameterScore;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.SessionAppraisalParameter;
import com.tulip.host.mapper.EmployeeAppraisalMapper;
import com.tulip.host.repository.AppraisalParameterRepository;
import com.tulip.host.repository.AppraisalReviewRepository;
import com.tulip.host.repository.AppraisalSelectedParameterRepository;
import com.tulip.host.repository.EmployeeAppraisalRepository;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.repository.SessionAppraisalParameterRepository;
import com.tulip.host.repository.SessionRepository;
import com.tulip.host.web.rest.vm.AppraisalReviewVM;
import com.tulip.host.web.rest.vm.AppraisalVM;
import com.tulip.host.web.rest.vm.CloseReviewVM;
import com.tulip.host.web.rest.vm.ReleaseAppraisalVM;
import com.tulip.host.web.rest.vm.SelfAssessmentVM;
import com.tulip.host.web.rest.vm.SessionParameterVM;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppraisalService {

    private final AppraisalParameterRepository parameterRepository;
    private final SessionAppraisalParameterRepository sessionParamRepository;
    private final AppraisalSelectedParameterRepository selectedParamRepository;
    private final EmployeeAppraisalRepository appraisalRepository;
    private final AppraisalReviewRepository reviewRepository;
    private final SessionRepository sessionRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeAppraisalMapper appraisalMapper;

    public List<AppraisalParameterDTO> getParameters() {
        return parameterRepository
            .findByActiveTrue()
            .stream()
            .map(p -> new AppraisalParameterDTO(p.getId(), p.getName()))
            .collect(Collectors.toList());
    }

    public List<AppraisalParameterDTO> getSessionParameters(Long sessionId) {
        return sessionParamRepository
            .findBySessionId(sessionId)
            .stream()
            .map(sp -> new AppraisalParameterDTO(sp.getParameter().getId(), sp.getParameter().getName()))
            .collect(Collectors.toList());
    }

    @Transactional
    public void setSessionParameters(SessionParameterVM vm) {
        sessionParamRepository.deleteBySessionId(vm.getSessionId());
        Session session = sessionRepository.findById(vm.getSessionId()).orElseThrow();
        vm
            .getParameterIds()
            .forEach(pid -> {
                AppraisalParameter param = parameterRepository.findById(pid).orElseThrow();
                sessionParamRepository.save(SessionAppraisalParameter.builder().session(session).parameter(param).build());
            });
    }

    @Transactional
    public EmployeeAppraisal saveAppraisal(AppraisalVM vm) {
        Session session = sessionRepository.findById(vm.getSessionId()).orElseThrow();
        EmployeeAppraisal appraisal = appraisalRepository
            .findByEmployeeIdAndSessionId(vm.getEmployeeId(), vm.getSessionId())
            .orElse(EmployeeAppraisal.builder().employeeId(vm.getEmployeeId()).session(session).status("DRAFT").build());
        appraisal = appraisalRepository.save(appraisal);

        // Replace selected parameters for this appraisal
        selectedParamRepository.deleteByAppraisalId(appraisal.getId());
        final Long appraisalId = appraisal.getId();
        if (vm.getParameterIds() != null) {
            vm
                .getParameterIds()
                .forEach(pid -> {
                    AppraisalParameter param = parameterRepository.findById(pid).orElseThrow();
                    selectedParamRepository.save(AppraisalSelectedParameter.builder().appraisalId(appraisalId).parameter(param).build());
                });
        }
        return appraisal;
    }

    @Transactional
    public void addReview(AppraisalReviewVM vm) {
        EmployeeAppraisal appraisal = appraisalRepository.findById(vm.getAppraisalId()).orElseThrow();
        validateReviewAuthority(appraisal.getEmployeeId());
        AppraisalReview review = AppraisalReview.builder()
            .appraisal(appraisal)
            .startDate(LocalDate.now())
            .reviewType(vm.getReviewType())
            .reviewStatus("OPEN")
            .build();
        reviewRepository.save(review);
    }

    @Transactional
    public void closeReview(Long reviewId, CloseReviewVM vm) {
        AppraisalReview review = reviewRepository.findById(reviewId).orElseThrow();
        validateReviewAuthority(review.getAppraisal().getEmployeeId());
        review.setEndDate(LocalDate.now());
        review.setReviewStatus("CLOSED");
        review.setObservations(vm.getObservations());
        review.getScores().clear();
        if (vm.getScores() != null) {
            vm
                .getScores()
                .forEach(s -> {
                    AppraisalParameter param = parameterRepository.findById(s.getParameterId()).orElseThrow();
                    review.getScores().add(ReviewParameterScore.builder().review(review).parameter(param).score(s.getScore()).build());
                });
        }
        review.setRating(calculateAvgScore(review.getScores()));
        reviewRepository.save(review);
        recalculateFinalRating(review.getAppraisal());
    }

    @Transactional
    public void submitSelfAssessment(SelfAssessmentVM vm) {
        AppraisalReview review = reviewRepository.findById(vm.getReviewId()).orElseThrow();
        review.setSelfAssessment(vm.getSelfAssessment());
        if (vm.getAttachmentUuids() != null && !vm.getAttachmentUuids().isEmpty()) {
            review.setSelfAssessmentAttachments(String.join(",", vm.getAttachmentUuids()));
        }
        reviewRepository.save(review);
    }

    @Transactional
    public void releaseAppraisal(Long appraisalId, ReleaseAppraisalVM vm) {
        EmployeeAppraisal appraisal = appraisalRepository.findById(appraisalId).orElseThrow();
        appraisal.setStatus("RELEASED");
        if (vm != null && vm.getNewSalary() != null) {
            appraisal.setNewSalary(vm.getNewSalary());
        }
        appraisalRepository.save(appraisal);
    }

    @Transactional(readOnly = true)
    public List<EmployeeAppraisalDTO> getAppraisalsForEmployee(Long employeeId) {
        return appraisalRepository
            .findByEmployeeIdOrderByCreatedDateDesc(employeeId)
            .stream()
            .map(appraisal -> {
                EmployeeAppraisalDTO dto = appraisalMapper.toDto(appraisal);
                dto.setSelectedParameters(appraisalMapper.toParameterDtoList(selectedParamRepository.findByAppraisalId(appraisal.getId())));
                dto.setReviews(appraisalMapper.toReviewDtoList(reviewRepository.findByAppraisalIdOrderByStartDateAsc(appraisal.getId())));
                return dto;
            })
            .collect(Collectors.toList());
    }

    /**
     * Enforces who can open/close a review based on the employee's role:
     *  - TEACHER / STAFF employee → UG_PRINCIPAL or UG_ADMIN
     *  - PRINCIPAL employee       → UG_ADMIN only
     */
    private void validateReviewAuthority(Long employeeId) {
        Employee employee = employeeRepository.search(employeeId);
        String employeeRole = employee.getGroup().getAuthority();
        String requesterRole = SecurityContextHolder.getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .findFirst()
            .map(a -> a.getAuthority())
            .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));
        if ("UG_PRINCIPAL".equals(employeeRole) && !"UG_ADMIN".equals(requesterRole)) {
            throw new AccessDeniedException("Only administrators can manage reviews for a principal");
        }
    }

    private Double calculateAvgScore(List<ReviewParameterScore> scores) {
        if (scores == null || scores.isEmpty()) return null;
        return scores.stream().mapToInt(ReviewParameterScore::getScore).average().orElse(0.0);
    }

    private void recalculateFinalRating(EmployeeAppraisal appraisal) {
        List<AppraisalReview> reviews = reviewRepository.findByAppraisalIdOrderByStartDateAsc(appraisal.getId());
        if (!reviews.isEmpty()) {
            double avg = reviews.stream().filter(r -> r.getRating() != null).mapToDouble(AppraisalReview::getRating).average().orElse(0.0);
            appraisal.setFinalRating(Math.round(avg * 100.0) / 100.0);
        } else {
            appraisal.setFinalRating(null);
        }
        appraisalRepository.save(appraisal);
    }
}
