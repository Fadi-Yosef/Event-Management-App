package com.eventmanagement.service;

import com.eventmanagement.exception.EventCapacityExceededException;
import com.eventmanagement.exception.EventNotFoundException;
import com.eventmanagement.exception.ParticipantNotFoundException;
import com.eventmanagement.exception.RegistrationNotFoundException;
import com.eventmanagement.model.Participant;
import com.eventmanagement.model.Registration;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.ParticipantRepository;
import com.eventmanagement.repository.RegistrationRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    public RegistrationService(RegistrationRepository registrationRepository, EventRepository eventRepository, ParticipantRepository participantRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
    }

    public void registerForEvent(int eventId, int participantId) {
        if (!eventRepository.findById(eventId).isPresent()) {
            throw new EventNotFoundException("Event with ID " + eventId + " not found.");
        }
        if (!participantRepository.findById(participantId).isPresent()) {
            throw new ParticipantNotFoundException("Participant with ID " + participantId + " not found.");
        }

        int currentRegistrations = registrationRepository.getRegistrationCountForEvent(eventId);
        int capacity = eventRepository.findById(eventId).get().getCapacity();
        if (currentRegistrations >= capacity) {
            throw new EventCapacityExceededException("Event capacity reached.");
        }

        if (registrationRepository.findByEventAndParticipant(eventId, participantId).isPresent()) {
            throw new IllegalArgumentException("Participant is already registered for this event.");
        }

        Registration registration = new Registration(eventId, participantId, Registration.Status.PENDING);
        registrationRepository.register(registration);
    }

    public void cancelRegistration(int registrationId) {
        if (!registrationRepository.findById(registrationId).isPresent()) {
            throw new RegistrationNotFoundException("Registration with ID " + registrationId + " not found.");
        }
        registrationRepository.cancelRegistration(registrationId);
    }

    public void updateRegistrationStatus(int registrationId, Registration.Status status) {
        if (!registrationRepository.findById(registrationId).isPresent()) {
            throw new RegistrationNotFoundException("Registration with ID " + registrationId + " not found.");
        }
        registrationRepository.updateStatus(registrationId, status);
    }

    public List<Participant> getParticipantsForEvent(int eventId) {
        return registrationRepository.findParticipantsByEventId(eventId);
    }

    public List<Registration> getRegistrationsForEventSortedByStatus(int eventId) {
        return registrationRepository.findByEventId(eventId).stream()
                .sorted(Comparator.comparing(Registration::getStatus))
                .collect(Collectors.toList());
    }
}
