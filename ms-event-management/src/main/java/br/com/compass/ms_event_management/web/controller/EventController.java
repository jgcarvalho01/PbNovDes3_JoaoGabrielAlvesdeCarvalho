package br.com.compass.ms_event_management.web.controller;

import br.com.compass.ms_event_management.service.EventService;
import br.com.compass.ms_event_management.web.dto.EventCreateDto;
import br.com.compass.ms_event_management.web.dto.EventResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/br/com/compass/eventmanagement/v1")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/create-event")
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventCreateDto dto) {
        EventResponseDto createdEvent = eventService.createEvent(dto);
        return ResponseEntity.ok(createdEvent);
    }

    @GetMapping("/get-event/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable String id) {
        EventResponseDto event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/get-all-events")
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        List<EventResponseDto> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }
}
