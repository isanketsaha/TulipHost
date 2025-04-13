package com.tulip.host.service;

import com.tulip.host.domain.AcademicCalendar;
import com.tulip.host.mapper.CalenderMapper;
import com.tulip.host.repository.AcademicCalendarRepository;
import com.tulip.host.web.rest.vm.dataload.CalenderLoadVM;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final AcademicCalendarRepository repository;
    private final CalenderMapper calenderMapper;

    public List<AcademicCalendar> getAllEvents() {
        return repository.findAll();
    }

    public Optional<AcademicCalendar> getEventById(Long id) {
        return repository.findById(id);
    }

    public void createEvent(List<CalenderLoadVM> event) {
        List<AcademicCalendar> entityList = calenderMapper.toEntityList(event);
        repository.saveAll(entityList);
    }

    public AcademicCalendar updateEvent(Long id, AcademicCalendar event) {
        return repository.findById(id)
            .map(existingEvent -> {
                existingEvent.setName(event.getName());
                existingEvent.setDescription(event.getDescription());
//                existingEvent.setEventType(event.getEventType());
                existingEvent.setStartDate(event.getStartDate());
                existingEvent.setEndDate(event.getEndDate());
                existingEvent.setRecurringPattern(event.getRecurringPattern());
                existingEvent.setOrganizer(event.getOrganizer());
//            existingEvent.setSessionId(event.getSessionId());
                existingEvent.setLastModifiedBy(event.getLastModifiedBy());
                return repository.save(existingEvent);
            })
            .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + id));
    }

    public void deleteEvent(Long id) {
        repository.deleteById(id);
    }
}
