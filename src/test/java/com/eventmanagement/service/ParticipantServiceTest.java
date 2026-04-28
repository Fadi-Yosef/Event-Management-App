package com.eventmanagement.service;

import com.eventmanagement.model.Participant;
import com.eventmanagement.repository.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantService participantService;

    private Participant testParticipant;

    @BeforeEach
    void setUp() {
        testParticipant = new Participant(1, "John Doe", "john.doe@example.com");
    }

    @Test
    void testRegisterParticipantNew() {
        when(participantRepository.findByEmail(testParticipant.getEmail())).thenReturn(Optional.empty());
        participantService.registerParticipant(testParticipant);
        verify(participantRepository, times(1)).save(testParticipant);
    }

    @Test
    void testRegisterParticipantExistingEmail() {
        when(participantRepository.findByEmail(testParticipant.getEmail())).thenReturn(Optional.of(testParticipant));
        assertThrows(IllegalArgumentException.class, () -> participantService.registerParticipant(testParticipant));
        verify(participantRepository, never()).save(testParticipant);
    }

    @Test
    void testGetParticipantById() {
        when(participantRepository.findById(testParticipant.getParticipantId())).thenReturn(Optional.of(testParticipant));
        Optional<Participant> result = participantService.getParticipantById(testParticipant.getParticipantId());
        assertTrue(result.isPresent());
        assertEquals(testParticipant.getName(), result.get().getName());
        verify(participantRepository, times(1)).findById(testParticipant.getParticipantId());
    }

    @Test
    void testGetParticipantByIdNotFound() {
        when(participantRepository.findById(testParticipant.getParticipantId())).thenReturn(Optional.empty());
        Optional<Participant> result = participantService.getParticipantById(testParticipant.getParticipantId());
        assertTrue(result.isEmpty());
        verify(participantRepository, times(1)).findById(testParticipant.getParticipantId());
    }

    @Test
    void testGetParticipantByEmail() {
        when(participantRepository.findByEmail(testParticipant.getEmail())).thenReturn(Optional.of(testParticipant));
        Optional<Participant> result = participantService.getParticipantByEmail(testParticipant.getEmail());
        assertTrue(result.isPresent());
        assertEquals(testParticipant.getName(), result.get().getName());
        verify(participantRepository, times(1)).findByEmail(testParticipant.getEmail());
    }

    @Test
    void testGetParticipantByEmailNotFound() {
        when(participantRepository.findByEmail(testParticipant.getEmail())).thenReturn(Optional.empty());
        Optional<Participant> result = participantService.getParticipantByEmail(testParticipant.getEmail());
        assertTrue(result.isEmpty());
        verify(participantRepository, times(1)).findByEmail(testParticipant.getEmail());
    }

    @Test
    void testGetAllParticipants() {
        List<Participant> participants = Arrays.asList(testParticipant, new Participant(2, "Jane Doe", "jane.doe@example.com"));
        when(participantRepository.findAll()).thenReturn(participants);

        List<Participant> result = participantService.getAllParticipants();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(participantRepository, times(1)).findAll();
    }

    @Test
    void testGetAllParticipantsSortedByName() {
        Participant alice = new Participant(1, "Alice", "alice@example.com");
        Participant bob = new Participant(2, "Bob", "bob@example.com");
        when(participantRepository.findAll()).thenReturn(Arrays.asList(bob, alice));

        List<Participant> result = participantService.getAllParticipantsSortedByName();
        assertEquals("Alice", result.get(0).getName());
        assertEquals("Bob", result.get(1).getName());
    }

    @Test
    void testSearchParticipantsByName() {
        Participant john = new Participant(1, "John Doe", "john@example.com");
        Participant jane = new Participant(2, "Jane Doe", "jane@example.com");
        when(participantRepository.findAll()).thenReturn(Arrays.asList(john, jane));

        List<Participant> result = participantService.searchParticipantsByName("Jane");
        assertEquals(1, result.size());
        assertEquals("Jane Doe", result.get(0).getName());
    }

    @Test
    void testSearchParticipantsByNameNoMatch() {
        when(participantRepository.findAll()).thenReturn(Collections.emptyList());
        List<Participant> result = participantService.searchParticipantsByName("Nobody");
        assertTrue(result.isEmpty());
    }
}
