package com.tulip.host.service;

import com.tulip.host.domain.LeaveType;
import com.tulip.host.repository.LeaveTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveType createLeaveType(LeaveType leaveType) {
        leaveType.setCreatedDate(LocalDateTime.now());
        leaveType.setLastModifiedDate(LocalDateTime.now());
        return leaveTypeRepository.save(leaveType);
    }

    public Optional<LeaveType> getLeaveTypeById(Long id) {
        return leaveTypeRepository.findById(id);
    }


    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeRepository.findAll();
    }

    public LeaveType updateLeaveType(Long id, LeaveType updatedLeaveType) {
        Optional<LeaveType> existingLeaveType = leaveTypeRepository.findById(id);
        if (existingLeaveType.isPresent()) {
            LeaveType leaveType = existingLeaveType.get();
            leaveType.setName(updatedLeaveType.getName());
            leaveType.setDescription(updatedLeaveType.getDescription());
            leaveType.setIsPaid(updatedLeaveType.getIsPaid());
            leaveType.setIsActive(updatedLeaveType.getIsActive());
            leaveType.setLastModifiedDate(LocalDateTime.now());
            return leaveTypeRepository.save(leaveType);
        }
        throw new RuntimeException("LeaveType not found with id: " + id);
    }

    public void deleteLeaveType(Long id) {
        if (leaveTypeRepository.existsById(id)) {
            leaveTypeRepository.deleteById(id);
        } else {
            throw new RuntimeException("LeaveType not found with id: " + id);
        }
    }
}
