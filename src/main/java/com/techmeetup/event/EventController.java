package com.techmeetup.event;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<Map<String, String>> registerForEvent(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(event -> {
                    if (event.getAvailableTickets() <= 0) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message", "No tickets available for this event."));
                    }

                    event.setAvailableTickets(event.getAvailableTickets() - 1);
                    eventRepository.save(event);

                    return ResponseEntity.ok(
                            Map.of("message", "Registration successful for " + event.getTitle() + ".")
                    );
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Event not found.")));
    }
}
