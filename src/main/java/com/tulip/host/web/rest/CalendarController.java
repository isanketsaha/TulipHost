package com.tulip.host.web.rest;

import com.tulip.host.domain.AcademicCalendar;
import com.tulip.host.service.CalendarService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/calender")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService service;

    @GetMapping
    public List<AcademicCalendar> getAllEvents() {
        return service.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcademicCalendar> getEventById(@PathVariable Long id) {
        return service.getEventById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<AcademicCalendar> updateEvent(@PathVariable Long id, @RequestBody AcademicCalendar event) {
        try {
            AcademicCalendar updatedEvent = service.updateEvent(id, event);
            return ResponseEntity.ok(updatedEvent);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        service.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
