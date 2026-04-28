package com.eventmanagement.service;

import com.eventmanagement.model.Participant;
import com.eventmanagement.repository.ParticipantRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ParticipantService {
    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public void registerParticipant(Participant participant) {
        if (participantRepository.findByEmail(participant.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Participant with email " + participant.getEmail() + " already exists.");
        }
        participantRepository.save(participant);
    }

    public Optional<Participant> getParticipantById(int participantId) {
        return participantRepository.findById(participantId);
    }

    public Optional<Participant> getParticipantByEmail(String email) {
        return participantRepository.findByEmail(email);
    }

    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    public List<Participant> getAllParticipantsSortedByName() {
        return participantRepository.findAll().stream()
                .sorted(Comparator.comparing(Participant::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public List<Participant> searchParticipantsByName(String name) {
        String lowerName = name.toLowerCase();
        return participantRepository.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }
}
