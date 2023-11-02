package com.tulip.host.service;

import com.tulip.host.domain.Credential;
import com.tulip.host.domain.Employee;
import com.tulip.host.mapper.PasswordEncoderMapper;
import com.tulip.host.repository.CredentialRepository;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.web.rest.vm.CredentialVM;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final CredentialRepository credentialRepository;

    private final PasswordEncoderMapper passwordEncoderMapper;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public void reset(CredentialVM credentialVM) {
        Employee byUserId = credentialRepository.findUserProfileByUserId(credentialVM.getUserId());
        Credential credential = byUserId.getCredential();
        credential.setPassword(passwordEncoderMapper.encode(credentialVM.getPassword()));
        byUserId.setResetCredential(false);
        employeeRepository.saveAndFlush(byUserId);
    }

    public boolean checkUserId(@NotNull String userId) {
        Employee byUserId = credentialRepository.findUserProfileByUserId(userId);
        return byUserId == null;
    }
}
