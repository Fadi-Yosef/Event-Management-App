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

public class EventService {
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public EventService(EventRepository eventRepository, RegistrationRepository registrationRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    public void createEvent(Event event) {
        if (event.getCapacity() <= 0) {
            throw new IllegalArgumentException("Event capacity must be greater than zero.");
        }
        eventRepository.save(event);
    }

    public void updateEvent(Event event) {
        if (event.getCapacity() <= 0) {
            throw new IllegalArgumentException("Event capacity must be greater than zero.");
        }
        if (!eventRepository.findById(event.getEventId()).isPresent()) {
            throw new EventNotFoundException("Event with ID " + event.getEventId() + " not found.");
        }
        eventRepository.update(event);
    }

    public void deleteEvent(int eventId) {
        if (!eventRepository.findById(eventId).isPresent()) {
            throw new EventNotFoundException("Event with ID " + eventId + " not found.");
        }
        eventRepository.delete(eventId);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getAllEventsSortedByDate() {
        return eventRepository.findAll().stream()
                .sorted(Comparator.comparing(Event::getDate))
                .collect(Collectors.toList());
    }

    public List<Event> getAllEventsSortedByName() {
        return eventRepository.findAll().stream()
                .sorted(Comparator.comparing(Event::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public Optional<Event> getEventById(int eventId) {
        return eventRepository.findById(eventId);
    }

    public List<Event> searchEvents(String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        LocalDate searchDate = null;
        try {
            searchDate = LocalDate.parse(searchTerm);
        } catch (java.time.format.DateTimeParseException e) {
            // Not a valid date, ignore date filtering
        }
        final LocalDate finalSearchDate = searchDate;

        return eventRepository.findAll().stream()
                .filter(event -> event.getName().toLowerCase().contains(lowerSearchTerm)
                        || (finalSearchDate != null && event.getDate().equals(finalSearchDate)))
                .collect(Collectors.toList());
    }

    public boolean isEventFull(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        int currentRegistrations = registrationRepository.getRegistrationCountForEvent(eventId);
        return currentRegistrations >= event.getCapacity();
    }

    public int getAvailableCapacity(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        int currentRegistrations = registrationRepository.getRegistrationCountForEvent(eventId);
        return event.getCapacity() - currentRegistrations;
    }
}
