package com.tulip.host.service;

import com.tulip.host.domain.AcademicCalendar;
import com.tulip.host.mapper.CalenderMapper;
import com.tulip.host.repository.AcademicCalendarRepository;
import com.tulip.host.web.rest.vm.dataload.CalenderLoadVM;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final AcademicCalendarRepository academicCalendarRepository;

    private final CalenderMapper calenderMapper;

    @Transactional
    public List<AcademicCalendar> getAllEvents() {
        return academicCalendarRepository.findAll();
    }

    @Transactional
    public List<AcademicCalendar> getAllEvents(LocalDate from, LocalDate to) {
        return academicCalendarRepository.findByDateRange(from, to);
    }

    @Transactional
    public Optional<AcademicCalendar> getEventById(Long id) {
        return academicCalendarRepository.findById(id);
    }

    public void createEvent(List<CalenderLoadVM> event) {
        List<AcademicCalendar> entityList = calenderMapper.toEntityList(event);
        academicCalendarRepository.saveAll(entityList);
    }

    public AcademicCalendar updateEvent(Long id, AcademicCalendar event) {
        return academicCalendarRepository.findById(id)
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
                return academicCalendarRepository.save(existingEvent);
            })
            .orElseThrow(() -> new EntityNotFoundException("Event not found with id " + id));
    }

    public void deleteEvent(Long id) {
        academicCalendarRepository.deleteById(id);
    }
}
