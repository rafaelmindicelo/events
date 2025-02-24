package br.com.nlw.events.controller;

import br.com.nlw.events.model.Event;
import br.com.nlw.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventController {
    @Autowired
    private EventService eventService;

    @PostMapping("/events")
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        Event addedEvent = eventService.addEvent(event);
        return ResponseEntity.ok().body(addedEvent);
    }

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> allEvents = eventService.getAllEvents();
        return ResponseEntity.ok().body(allEvents);
    }

    @GetMapping("/events/{prettyName}")
    public ResponseEntity<Event> getEventByPrettyName(@PathVariable String prettyName) {
        Event event = eventService.getEventByPrettyName(prettyName);

        if (event == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(event);
    }
}
