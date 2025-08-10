package com.tulip.host.service;

import com.tulip.host.data.LeaveTypeDto;
import com.tulip.host.domain.LeaveType;
import com.tulip.host.mapper.LeaveTypeMapper;
import com.tulip.host.repository.LeaveTypeRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LeaveTypeService {

    private final Logger log = LoggerFactory.getLogger(LeaveTypeService.class);

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveTypeMapper leaveTypeMapper;

    public LeaveTypeService(LeaveTypeRepository leaveTypeRepository, LeaveTypeMapper leaveTypeMapper) {
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveTypeMapper = leaveTypeMapper;
    }

    public LeaveType createLeaveType(LeaveType leaveType) {
        log.debug("Request to save LeaveType : {}", leaveType);
        return leaveTypeRepository.save(leaveType);
    }

    public LeaveType updateLeaveType(LeaveType leaveType) {
        log.debug("Request to update LeaveType : {}", leaveType);
        return leaveTypeRepository.save(leaveType);
    }

    public List<LeaveType> getAllLeaveTypes() {
        log.debug("Request to get all LeaveTypes");
        return leaveTypeRepository.findAll();
    }

    public List<LeaveTypeDto> getAllLeaveTypesAsDto() {
        List<LeaveType> leaveTypes = getAllLeaveTypes();
        return leaveTypes.stream()
                .map(leaveTypeMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    public Optional<LeaveType> getLeaveType(Long id) {
        log.debug("Request to get LeaveType : {}", id);
        return leaveTypeRepository.findById(id);
    }

    public void deleteLeaveType(Long id) {
        log.debug("Request to delete LeaveType : {}", id);
        leaveTypeRepository.deleteById(id);
    }
}
