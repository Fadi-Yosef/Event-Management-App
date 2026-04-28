package com.eventmanagement.repository;

import com.eventmanagement.model.Participant;
import java.util.List;
import java.util.Optional;

public interface ParticipantRepository {
    void save(Participant participant);
    Optional<Participant> findById(int participantId);
    Optional<Participant> findByEmail(String email);
    List<Participant> findAll();
}
