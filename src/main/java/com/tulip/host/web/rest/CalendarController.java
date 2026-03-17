package com.tulip.host.web.rest;

import com.tulip.host.domain.AcademicCalendar;
import com.tulip.host.service.CalendarService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calender")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService service;

    @GetMapping
    public List<AcademicCalendar> getAllEvents() {
        return service.getAllEvents();
    }

    @GetMapping("/filter")
    public List<AcademicCalendar> getEventsByFilter(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        return service.getAllEvents(from, to);
    }
    // Not used by UI
    //    @GetMapping("/{id}")
    //    public ResponseEntity<AcademicCalendar> getEventById(@PathVariable Long id) {
    //        return service.getEventById(id)
    //            .map(ResponseEntity::ok)
    //            .orElse(ResponseEntity.notFound().build());
    //    }

    // Not used by UI
    //    @PutMapping("/{id}")
    //    public ResponseEntity<AcademicCalendar> updateEvent(@PathVariable Long id, @RequestBody AcademicCalendar event) {
    //        try {
    //            AcademicCalendar updatedEvent = service.updateEvent(id, event);
    //            return ResponseEntity.ok(updatedEvent);
    //        } catch (EntityNotFoundException e) {
    //            return ResponseEntity.notFound().build();
    //        }
    //    }

    // Not used by UI
    //    @DeleteMapping("/{id}")
    //    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
    //        service.deleteEvent(id);
    //        return ResponseEntity.noContent().build();
    //    }
}
