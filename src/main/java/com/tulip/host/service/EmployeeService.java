package com.tulip.host.service;

import static com.tulip.host.config.Constants.AADHAAR_CARD;
import static com.tulip.host.config.Constants.BIRTH_CERTIFICATE;
import static com.tulip.host.config.Constants.DEFAULT_PASSWORD;
import static com.tulip.host.config.Constants.JASPER_FOLDER;
import static com.tulip.host.config.Constants.JOINING_LETTER;
import static com.tulip.host.config.Constants.PAN_CARD;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

import com.tulip.host.data.EmployeeBasicDTO;
import com.tulip.host.data.EmployeeDetailsDTO;
import com.tulip.host.data.JoiningLetterDTO;
import com.tulip.host.data.LetterClause;
import com.tulip.host.data.StudentDetailsDTO;
import com.tulip.host.domain.Credential;
import com.tulip.host.domain.Employee;
import com.tulip.host.domain.Student;
import com.tulip.host.domain.Upload;
import com.tulip.host.domain.UserGroup;
import com.tulip.host.enums.UserRoleEnum;
import com.tulip.host.mapper.CredentialMapper;
import com.tulip.host.mapper.EmployeeMapper;
import com.tulip.host.mapper.PasswordEncoderMapper;
import com.tulip.host.mapper.UploadMapper;
import com.tulip.host.repository.CredentialRepository;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.repository.SessionRepository;
import com.tulip.host.repository.UserGroupRepository;
import com.tulip.host.repository.UserToDependentRepository;
import com.tulip.host.utils.CommonUtils;
import com.tulip.host.web.rest.vm.CredentialVM;
import com.tulip.host.web.rest.vm.OnboardingVM;
import com.tulip.host.web.rest.vm.UploadVM;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CredentialMapper credentialMapper;
    private final UploadService uploadService;
    private final PasswordEncoderMapper encoderMapper;
    private final UserGroupRepository userGroupRepository;

    private final UserToDependentRepository userToDependentRepository;

    private final JasperService jasperService;

    private final EmployeeMapper employeeMapper;
    private final CredentialRepository credentialRepository;
    private final SessionRepository sessionRepository;

    private final UploadMapper uploadMapper;

    @Transactional
    public List<EmployeeBasicDTO> fetchAllEmployee(boolean isActive, UserRoleEnum role) {
        Long id = sessionRepository.fetchCurrentSession().getId();
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
        UserGroup userGroupByAuthority = userGroupRepository.findUserGroupByAuthority(employeeVM.getInterview().getRole().getValue());
        if (userGroupByAuthority != null) {
            Employee employee = employeeMapper.toModel(employeeVM);
            employee.setLocked(userGroupByAuthority.getAuthority().equalsIgnoreCase(UserRoleEnum.TEACHER.getValue()));
            employee.setResetCredential(!userGroupByAuthority.getAuthority().equalsIgnoreCase(UserRoleEnum.TEACHER.getValue()));
            employee.setGroup(userGroupByAuthority);
            Credential credential = credentialMapper.toEntity(
                CredentialVM.builder().password(DEFAULT_PASSWORD).userId(employeeVM.getInterview().getUserId()).build()
            );
            addUpload(employee, employeeVM);
            employee.setCredential(credential);
            Employee emp = employeeRepository.saveAndFlush(employee);
            return emp.getId();
        }
        throw new Exception("Unable to find usergroup");
    }

    public void addUpload(Employee employee, OnboardingVM onboardingVM) {
        if (onboardingVM.getAadhaarCard() != null) {
            employee.addDocuments(uploadMapper.toModelList(onboardingVM.getAadhaarCard()));
        }
        if (onboardingVM.getPanCard() != null) {
            employee.addDocuments(uploadMapper.toModelList(onboardingVM.getPanCard()));
        }
    }

    @Transactional
    public List<EmployeeBasicDTO> searchEmployee(String name) {
        List<Employee> employees = employeeRepository.searchByName(name);
        return employeeMapper.toBasicEntityList(employees);
    }

    @jakarta.transaction.Transactional
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
                if (item.getDocumentType().equals(AADHAAR_CARD)) {
                    employeeDetailsDTO.getAadhaarCard().add(uploadMapper.toEntity(item));
                } else if (item.getDocumentType().equals(PAN_CARD)) {
                    employeeDetailsDTO.getPanCard().add(uploadMapper.toEntity(item));
                }
            });
    }

    @Transactional
    public EmployeeDetailsDTO editEmployee() {
        return employeeRepository.edit();
    }

    public void terminate(long id) {
        Employee byId = employeeRepository.findById(id).orElse(null);
        if (byId != null) {
            byId.setActive(Boolean.FALSE);
            byId.setTerminationDate(new Date());
            byId.setLocked(true);
            employeeRepository.save(byId);
        }
    }

    @Transactional
    public void forgotPassword(long id) {
        Employee byId = employeeRepository.findById(id).orElse(null);
        if (byId != null) {
            byId.getCredential().setPassword(encoderMapper.encode(DEFAULT_PASSWORD));
            byId.setResetCredential(true);
            employeeRepository.save(byId);
        }
    }

    @Transactional
    public byte[] generateJoiningLetter(Long empId) throws IOException {
        Employee byId = employeeRepository.findById(empId).orElse(null);
        if (byId != null) {
            JoiningLetterDTO joiningLetterDTO = employeeMapper.toPrintEntity(byId);
            try (InputStream inputStream = getClass().getResourceAsStream(JASPER_FOLDER + "Appointment_Letter.jrxml")) {
                Map<String, Object> map = new HashMap<>();
                map.put(
                    "terms",
                    new JRBeanCollectionDataSource(
                        Arrays.asList(
                            LetterClause.builder().clause("PROBATIONARY PERIOD").value("3 MONTHS").build(),
                            LetterClause.builder().clause("NOTICE PERIOD").value("1 MONTH").build(),
                            LetterClause
                                .builder()
                                .clause("WORKING HOURS")
                                .value("7:45 AM – 2:00 PM. Occasionally, you might need to work for extended hours, possibly on weekends.")
                                .build(),
                            LetterClause.builder().clause("CASUAL LEAVE").value("12 DAYS").build()
                        )
                    )
                );
                return jasperService.generatePdf(inputStream, map, Arrays.asList(joiningLetterDTO));
            }
        }
        return null;
    }

    public void attachEmployment(Long id, UploadVM joining_letter) {
        Employee byId = employeeRepository.findById(id).orElse(null);
        joining_letter.setName(byId.getName());
        if (byId != null) {
            byId.setAppointmentLetter(uploadMapper.toModel(joining_letter));
            employeeRepository.saveAndFlush(byId);
        }
    }

    @Transactional
    public String fetchAppointment(Long empId) throws IOException, FileUploadException {
        Employee byId = employeeRepository.findById(empId).orElseThrow();
        Upload appointmentLetter = byId.getAppointmentLetter();
        if (appointmentLetter != null) {
            return uploadService.getURL(appointmentLetter.getUid());
        } else {
            byte[] bytes = generateJoiningLetter(empId);
            UploadVM joining_letter = uploadService.save(bytes, MediaType.APPLICATION_PDF_VALUE, JOINING_LETTER);
            joining_letter.setName(byId.getName());
            attachEmployment(empId, joining_letter);
            return uploadService.getURL(joining_letter.getUid());
        }
    }
}
