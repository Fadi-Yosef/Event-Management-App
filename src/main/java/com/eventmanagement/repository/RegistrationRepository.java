package com.eventmanagement.repository;

import com.eventmanagement.model.Registration;
import com.eventmanagement.model.Participant;
import java.util.List;
import java.util.Optional;

public interface RegistrationRepository {
    void register(Registration registration);
    void cancelRegistration(int registrationId);
    void updateStatus(int registrationId, Registration.Status status);
    List<Registration> findByEventId(int eventId);
    Optional<Registration> findById(int registrationId);
    List<Participant> findParticipantsByEventId(int eventId);
    int getRegistrationCountForEvent(int eventId);
    Optional<Registration> findByEventAndParticipant(int eventId, int participantId);
}
