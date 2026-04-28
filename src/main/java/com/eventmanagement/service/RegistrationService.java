package com.eventmanagement.service;

import com.eventmanagement.exception.EventCapacityExceededException;
import com.eventmanagement.exception.EventNotFoundException;
import com.eventmanagement.exception.ParticipantNotFoundException;
import com.eventmanagement.exception.RegistrationNotFoundException;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.Participant;
import com.eventmanagement.model.Registration;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.ParticipantRepository;
import com.eventmanagement.repository.RegistrationRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for registration management business logic.
 * Handles participant registration, cancellation, and status management.
 */
public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    /**
     * Constructs RegistrationService with required repositories.
     */
    public RegistrationService(RegistrationRepository registrationRepository, 
                              EventRepository eventRepository, 
                              ParticipantRepository participantRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
    }

    /**
     * Registers a participant for an event with validation.
     */
    public void registerForEvent(int eventId, int participantId) {
        // Validate event exists
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with ID " + eventId + " not found."));
        
        // Validate participant exists
        if (!participantRepository.findById(participantId).isPresent()) {
            throw new ParticipantNotFoundException("Participant with ID " + participantId + " not found.");
        }

        // Check for duplicate registration
        if (registrationRepository.findByEventAndParticipant(eventId, participantId).isPresent()) {
            throw new IllegalArgumentException("Participant is already registered for this event.");
        }
        
        // Check event capacity
        int currentRegistrations = registrationRepository.getRegistrationCountForEvent(eventId);
        if (currentRegistrations >= event.getCapacity()) {
            throw new EventCapacityExceededException(
                    String.format("Event '%s' has reached maximum capacity (%d).", 
                            event.getName(), event.getCapacity()));
        }

        // Create and save registration
        Registration registration = new Registration(eventId, participantId, Registration.Status.PENDING);
        registrationRepository.register(registration);
    }

    /**
     * Cancels a registration by ID.
     */
    public void cancelRegistration(int registrationId) {
        if (!registrationRepository.findById(registrationId).isPresent()) {
            throw new RegistrationNotFoundException("Registration with ID " + registrationId + " not found.");
        }
        registrationRepository.cancelRegistration(registrationId);
    }

    /**
     * Updates the attendance status of a registration.
     */
    public void updateRegistrationStatus(int registrationId, Registration.Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (!registrationRepository.findById(registrationId).isPresent()) {
            throw new RegistrationNotFoundException("Registration with ID " + registrationId + " not found.");
        }
        registrationRepository.updateStatus(registrationId, status);
    }

    /**
     * Gets all participants registered for an event.
     */
    public List<Participant> getParticipantsForEvent(int eventId) {
        return registrationRepository.findParticipantsByEventId(eventId);
    }

    /**
     * Gets all registrations for an event sorted by status.
     */
    public List<Registration> getRegistrationsForEventSortedByStatus(int eventId) {
        return registrationRepository.findByEventId(eventId).stream()
                .sorted(Comparator.comparing(Registration::getStatus)
                        .thenComparing(Registration::getRegistrationDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets registration count for an event.
     */
    public int getRegistrationCount(int eventId) {
        return registrationRepository.getRegistrationCountForEvent(eventId);
    }
    
    /**
     * Checks if a participant is registered for an event.
     */
    public boolean isParticipantRegistered(int eventId, int participantId) {
        return registrationRepository.findByEventAndParticipant(eventId, participantId).isPresent();
    }
}
