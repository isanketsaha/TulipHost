package com.tulip.host.mapper;

import com.tulip.host.domain.AcademicCalendar;
import com.tulip.host.domain.Employee;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.service.EmployeeService;
import com.tulip.host.web.rest.vm.dataload.CalenderLoadVM;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CalenderMapper {
    @Autowired
   private EmployeeRepository employeeRepository;

    public abstract AcademicCalendar toEntity(CalenderLoadVM source);

    public abstract List<AcademicCalendar> toEntityList(List<CalenderLoadVM> source);

    protected Employee map(Long value) {
       return employeeRepository.findById(value).orElse(null);
    }
}
