package com.eventmanagement.service;

import com.eventmanagement.exception.EventNotFoundException;
import com.eventmanagement.model.Event;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.RegistrationRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for event management business logic.
 * Handles event CRUD operations, searching, sorting, and capacity checks.
 */
public class EventService {
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    /**
     * Constructs EventService with required repositories.
     */
    public EventService(EventRepository eventRepository, RegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    /**
     * Creates a new event after validation.
     */
    public void createEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (event.getCapacity() <= 0) {
            throw new IllegalArgumentException("Event capacity must be greater than zero.");
        }
        eventRepository.save(event);
    }

    /**
     * Updates an existing event after validation.
     */
    public void updateEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (event.getCapacity() <= 0) {
            throw new IllegalArgumentException("Event capacity must be greater than zero.");
        }
        if (!eventRepository.findById(event.getEventId()).isPresent()) {
            throw new EventNotFoundException("Event with ID " + event.getEventId() + " not found.");
        }
        eventRepository.update(event);
    }

    /**
     * Deletes an event by ID.
     */
    public void deleteEvent(int eventId) {
        if (!eventRepository.findById(eventId).isPresent()) {
            throw new EventNotFoundException("Event with ID " + eventId + " not found.");
        }
        eventRepository.delete(eventId);
    }

    /**
     * Retrieves all events.
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Retrieves all events sorted by date (ascending).
     */
    public List<Event> getAllEventsSortedByDate() {
        return eventRepository.findAll().stream()
                .sorted(Comparator.comparing(Event::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all events sorted by name (case-insensitive).
     */
    public List<Event> getAllEventsSortedByName() {
        return eventRepository.findAll().stream()
                .sorted(Comparator.comparing(Event::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    /**
     * Finds an event by its ID.
     */
    public Optional<Event> getEventById(int eventId) {
        return eventRepository.findById(eventId);
    }

    /**
     * Searches events by name or date.
     */
    public List<Event> searchEvents(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllEvents();
        }
        
        String lowerSearchTerm = searchTerm.toLowerCase().trim();
        LocalDate searchDate = null;
        try {
            searchDate = LocalDate.parse(searchTerm);
        } catch (java.time.format.DateTimeParseException e) {
            // Not a valid date, will only search by name
        }
        final LocalDate finalSearchDate = searchDate;

        return eventRepository.findAll().stream()
                .filter(event -> event.getName().toLowerCase().contains(lowerSearchTerm)
                        || (finalSearchDate != null && event.getDate().equals(finalSearchDate)))
                .collect(Collectors.toList());
    }

    /**
     * Checks if an event is full.
     */
    public boolean isEventFull(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with ID " + eventId + " not found."));
        int currentRegistrations = registrationRepository.getRegistrationCountForEvent(eventId);
        return currentRegistrations >= event.getCapacity();
    }

    /**
     * Gets available capacity for an event.
     */
    public int getAvailableCapacity(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with ID " + eventId + " not found."));
        int currentRegistrations = registrationRepository.getRegistrationCountForEvent(eventId);
        return event.getCapacity() - currentRegistrations;
    }
    
    /**
     * Gets event with registration summary.
     */
    public EventSummary getEventSummary(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with ID " + eventId + " not found."));
        int currentRegistrations = registrationRepository.getRegistrationCountForEvent(eventId);
        return new EventSummary(event, currentRegistrations);
    }
    
    /**
     * Inner class to hold event summary information.
     */
    public static class EventSummary {
        private final Event event;
        private final int currentRegistrations;
        private final int availableCapacity;
        private final boolean isFull;
        
        public EventSummary(Event event, int currentRegistrations) {
            this.event = event;
            this.currentRegistrations = currentRegistrations;
            this.availableCapacity = Math.max(0, event.getCapacity() - currentRegistrations);
            this.isFull = currentRegistrations >= event.getCapacity();
        }
        
        public Event getEvent() { return event; }
        public int getCurrentRegistrations() { return currentRegistrations; }
        public int getAvailableCapacity() { return availableCapacity; }
        public boolean isFull() { return isFull; }
        
        @Override
        public String toString() {
            return String.format("Event: %s | Date: %s | Capacity: %d/%d | Available: %d | Full: %s",
                    event.getName(), event.getDate(), currentRegistrations, 
                    event.getCapacity(), availableCapacity, isFull);
        }
    }
}
