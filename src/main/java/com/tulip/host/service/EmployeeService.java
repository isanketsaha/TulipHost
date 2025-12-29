package com.tulip.host.service;

import com.tulip.host.data.EmployeeBasicDTO;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.data.JoiningLetterDTO;
import com.tulip.host.data.LetterClause;
import com.tulip.host.domain.Credential;
import com.tulip.host.domain.Dependent;
import com.tulip.host.domain.Employee;
import com.tulip.host.domain.Upload;
import com.tulip.host.domain.UserGroup;
import com.tulip.host.enums.UserRoleEnum;
import com.tulip.host.mapper.CredentialMapper;
import com.tulip.host.mapper.DependentMapper;
import com.tulip.host.mapper.EmployeeMapper;
import com.tulip.host.mapper.PasswordEncoderMapper;
import com.tulip.host.mapper.UploadMapper;
import com.tulip.host.repository.CredentialRepository;
import com.tulip.host.repository.DependentRepository;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.repository.SessionRepository;
import com.tulip.host.repository.UserGroupRepository;
import com.tulip.host.repository.UserToDependentRepository;
import com.tulip.host.service.communication.CommunicationRequest;
import com.tulip.host.service.communication.OutboundCommunicationService;
import com.tulip.host.utils.CommonUtils;
import com.tulip.host.web.rest.vm.CredentialVM;
import com.tulip.host.web.rest.vm.FileUploadVM;
import com.tulip.host.web.rest.vm.OnboardingVM;
import com.tulip.host.web.rest.vm.UserEditVM;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tulip.host.config.Constants.AADHAAR_CARD;
import static com.tulip.host.config.Constants.DEFAULT_PASSWORD;
import static com.tulip.host.config.Constants.HIGHEST_QUALIFICATION;
import static com.tulip.host.config.Constants.JASPER_FOLDER;
import static com.tulip.host.config.Constants.JOINING_LETTER;
import static com.tulip.host.config.Constants.PAN_CARD;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CredentialMapper credentialMapper;
    private final UploadService uploadService;
    private final PasswordEncoderMapper encoderMapper;
    private final UserGroupRepository userGroupRepository;
    private final DependentRepository dependentRepository;
    private final DependentMapper dependentMapper;
    private final UserToDependentRepository userToDependentRepository;

    private final JasperService jasperService;

    private final EmployeeMapper employeeMapper;
    private final CredentialRepository credentialRepository;
    private final SessionRepository sessionRepository;
    private final OutboundCommunicationService outboundCommunicationService;
    private final MailService mailService;
    private final UploadMapper uploadMapper;

    @Transactional
    public List<EmployeeBasicDTO> fetchAllEmployee(boolean isActive, UserRoleEnum role) {
        Long id = sessionRepository.fetchCurrentSession()
            .getId();
        List<Employee> employees = employeeRepository.fetchAll(isActive, role == null ? CommonUtils.findEligibleUG() : Arrays.asList(role));
        return employeeMapper.toBasicEntityList(employees);
    }

    @Transactional
    public List<EmployeeBasicDTO> fetchAllEmployee() {
        List<Employee> employees = employeeRepository.fetchAll();
        return employeeMapper.toBasicEntityList(employees);
    }

    @Transactional
    public Long addEmployee(OnboardingVM employeeVM) throws Exception {
        UserGroup userGroupByAuthority = userGroupRepository.findUserGroupByAuthority(employeeVM.getInterview()
            .getRole()
            .getValue());
        if (userGroupByAuthority != null) {
            Employee employee = employeeMapper.toModel(employeeVM);
            employee.setResetCredential(true);
            employee.setGroup(userGroupByAuthority);
            Credential credential = credentialMapper.toEntity(
                CredentialVM.builder()
                    .password(DEFAULT_PASSWORD)
                    .userId(employeeVM.getInterview()
                        .getUserId())
                    .build()
            );
            addUpload(employee, employeeVM);
            employee.setCredential(credential);
            Employee emp = employeeRepository.saveAndFlush(employee);
            return emp.getId();
        }
        throw new Exception("Unable to find usergroup");
    }

    public void addUpload(Employee employee, OnboardingVM onboardingVM) {
        employee.getDependents()
            .stream()
            .filter(dep -> dep.getUploadedDocuments() != null && dep.getUploadedDocuments()
                .size() > 0)
            .forEach(dep -> dep.getUploadedDocuments()
                .forEach(docs -> docs.setDependent(dep)));
        if (onboardingVM.getAadhaarCard() != null) {
            employee.addDocuments(uploadMapper.toModelList(onboardingVM.getAadhaarCard()));
        }
        if (onboardingVM.getPanCard() != null) {
            employee.addDocuments(uploadMapper.toModelList(onboardingVM.getPanCard()));
        }
        if (onboardingVM.getHighestQualification() != null) {
            employee.addDocuments(uploadMapper.toModelList(onboardingVM.getHighestQualification()));
        }
    }

    @Transactional
    public List<EmployeeBasicDTO> searchEmployee(String name) {
        List<Employee> employees = employeeRepository.searchByName(name);
        return employeeMapper.toBasicEntityList(employees);
    }

    @Transactional
    public EmployeeBasicDTO searchByUserId(String userId) {
        Employee employee = employeeRepository.findByUserId(userId)
            .orElse(null);
        if (employee != null) {
            return employeeMapper.toBasicEntity(employee, uploadService);
        }
        return null;
    }

    @Transactional
    public EmployeeDetailsDTO searchEmployee(long id) {
        Employee employee = employeeRepository.search(id);
        if (employee != null) {
            EmployeeDetailsDTO employeeDetailsDTO = employeeMapper.toEntity(employee, uploadService);
            mapUpload(employeeDetailsDTO, employee);
            return employeeDetailsDTO;
        }
        return null;
    }

    private void mapUpload(EmployeeDetailsDTO employeeDetailsDTO, Employee byId) {
        byId
            .getUploadedDocuments()
            .forEach(item -> {
                if (item.getDocumentType()
                    .equals(AADHAAR_CARD)) {
                    employeeDetailsDTO.getAadhaarCard()
                        .add(uploadMapper.toEntity(item));
                } else if (item.getDocumentType()
                    .equals(PAN_CARD)) {
                    employeeDetailsDTO.getPanCard()
                        .add(uploadMapper.toEntity(item));
                } else if (item.getDocumentType()
                    .equals(HIGHEST_QUALIFICATION)) {
                    employeeDetailsDTO.getHighestQualification()
                        .add(uploadMapper.toEntity(item));
                }
            });
    }

    @Transactional
    public void editEmployee(UserEditVM editVM) {
        Employee byId = employeeRepository.findById(editVM.getId())
            .orElseThrow();
        employeeMapper.toUpdateModel(editVM, byId);
        if (editVM.getAadhaarCard() != null) {
            byId.addDocuments(uploadMapper.toModelList(editVM.getAadhaarCard()));
        }
        if (editVM.getPanCard() != null) {
            byId.addDocuments(uploadMapper.toModelList(editVM.getPanCard()));
        }
        if (editVM.getBirthCertificate() != null) {
            byId.addDocuments(uploadMapper.toModelList(editVM.getBirthCertificate()));
        }
        employeeRepository.saveAndFlush(byId);
        if (editVM.getDependent() != null) {
            editVM
                .getDependent()
                .forEach(dependentVM -> {
                    Dependent dependent = dependentRepository.findById(dependentVM.getId())
                        .orElse(null);

                    dependentMapper.toUpdateModel(dependentVM, dependent);
                    if (dependentVM.getAadhaarCard() != null) {
                        dependent.getUploadedDocuments()
                            .forEach(item -> item.setDependent(dependent));
                    }
                    dependentRepository.saveAndFlush(dependent);
                });
        }
    }

    public void terminate(long id) {
        Employee byId = employeeRepository.findById(id)
            .orElse(null);
        if (byId != null) {
            byId.setActive(Boolean.FALSE);
            byId.setTerminationDate(LocalDateTime.now());
            byId.setLocked(true);
            employeeRepository.save(byId);
        }
    }

    @Transactional
    public void forgotPassword(long id) {
        Employee byId = employeeRepository.findById(id)
            .orElse(null);
        if (byId != null) {
            byId.getCredential()
                .setPassword(encoderMapper.encode(DEFAULT_PASSWORD));
            byId.setResetCredential(true);
            employeeRepository.save(byId);
        }
    }

    @Transactional
    public byte[] generateJoiningLetter(Long empId) throws IOException {
        Employee byId = employeeRepository.findById(empId)
            .orElse(null);
        if (byId != null) {
            JoiningLetterDTO joiningLetterDTO = employeeMapper.toPrintEntity(byId);
            try (InputStream inputStream = getClass().getResourceAsStream(JASPER_FOLDER + "Appointment_Letter.jrxml")) {
                Map<String, Object> map = new HashMap<>();
                map.put(
                    "terms",
                    new JRBeanCollectionDataSource(
                        Arrays.asList(
                            LetterClause.builder()
                                .clause("PROBATIONARY PERIOD")
                                .value("3 MONTHS")
                                .build(),
                            LetterClause.builder()
                                .clause("NOTICE PERIOD")
                                .value("1 MONTH")
                                .build(),
                            LetterClause.builder()
                                .clause("CASUAL LEAVE")
                                .value("12 DAYS")
                                .build(),
                            LetterClause.builder()
                                .clause("MEDICAL LEAVE")
                                .value("4 DAYS")
                                .build()
                        )
                    )
                );
                return jasperService.generatePdf(inputStream, map, Arrays.asList(joiningLetterDTO));
            }
        }
        return null;
    }

    public void attachEmployment(Long id, FileUploadVM joining_letter) {
        Employee byId = employeeRepository.findById(id)
            .orElse(null);
        joining_letter.setName(byId.getName());
        if (byId != null) {
            byId.setAppointmentLetter(uploadMapper.toModel(joining_letter));
            employeeRepository.saveAndFlush(byId);
        }
    }

    @Transactional
    public String fetchAppointment(Long empId) {
        Employee byId = employeeRepository.findById(empId)
            .orElseThrow();
        Upload appointmentLetter = byId.getAppointmentLetter();
        if (appointmentLetter != null) {
            return uploadService.getURL(appointmentLetter.getUid());
        } else {
            try {
                byte[] bytes = generateJoiningLetter(empId);

                FileUploadVM joining_letter = uploadService.save(bytes, MediaType.APPLICATION_PDF_VALUE, JOINING_LETTER);
                joining_letter.setName(byId.getName());
                attachEmployment(empId, joining_letter);
                return uploadService.getURL(joining_letter.getUid());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Transactional
    public void notifyOnboard(Long id) {
        employeeRepository.findById(id)
            .ifPresent(employee -> {
                String[] ccEmails = getCCEmails(employee.getGroup().getAuthority());
                Map<String, Object> map = Map.of("employeeName", employee.getName(),
                    "designation", employee.getGroup()
                        .getAuthority(),
                    "joiningDate", CommonUtils.formatFromDate(employee.getInterview()
                        .getDoj()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate(), "dd MMMM yyyy"),
                    "username", employee.getCredential()
                        .getUserId(),
                    "password", DEFAULT_PASSWORD,
                    "portalUrl", "https://tulipschool.co.in/");
                outboundCommunicationService.send(CommunicationRequest.builder()
                    .recipient(new String[]{employee.getEmail()})
                    .cc(ccEmails)
                    .content(mailService.renderTemplate("mail/employee_onboard.vm", map))
                    .entityType(employee.getClass()
                        .getName())
                    .subject(employee.getName() + " - Welcome to Tulip Family")
                    .entityId(employee.getId())
                    .attachments(List.of(MailService.EmailAttachment.builder()
                        .filename("Appointment_Letter.pdf")
                        .content(uploadService.download(employee.getAppointmentLetter()
                            .getUid()))
                        .build()))
                    .build());
            });

    }

    public String[] getCCEmails(String role) {
        UserRoleEnum userRoleEnum = role.equalsIgnoreCase(UserRoleEnum.PRINCIPAL.name())
            ? UserRoleEnum.ADMIN : UserRoleEnum.PRINCIPAL;
        return employeeRepository.findByUserGroup(userRoleEnum)
            .stream()
            .filter(emp -> StringUtils.isNotBlank(emp.getEmail()))
            .map(Employee::getEmail)
            .toArray(String[]::new);
    }
}
