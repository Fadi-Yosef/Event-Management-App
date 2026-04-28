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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private RegistrationService registrationService;

    private Event testEvent;
    private Participant testParticipant;
    private Registration testRegistration;

    @BeforeEach
    void setUp() {
        testEvent = new Event(1, "Test Event", LocalDate.of(2023, 12, 25), "Test Location", 100, "A test description");
        testParticipant = new Participant(1, "John Doe", "john.doe@example.com");
        testRegistration = new Registration(1, 1, 1, null, Registration.Status.PENDING);
    }

    @Test
    void testRegisterForEventSuccess() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(testEvent));
        when(participantRepository.findById(1)).thenReturn(Optional.of(testParticipant));
        when(registrationRepository.getRegistrationCountForEvent(1)).thenReturn(50);
        when(registrationRepository.findByEventAndParticipant(1, 1)).thenReturn(Optional.empty());

        registrationService.registerForEvent(1, 1);
        verify(registrationRepository, times(1)).register(any(Registration.class));
    }

    @Test
    void testRegisterForEventNotFound() {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> registrationService.registerForEvent(1, 1));
        verify(registrationRepository, never()).register(any());
    }

    @Test
    void testRegisterForEventParticipantNotFound() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(testEvent));
        when(participantRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ParticipantNotFoundException.class, () -> registrationService.registerForEvent(1, 1));
        verify(registrationRepository, never()).register(any());
    }

    @Test
    void testRegisterForEventCapacityExceeded() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(testEvent));
        when(participantRepository.findById(1)).thenReturn(Optional.of(testParticipant));
        when(registrationRepository.getRegistrationCountForEvent(1)).thenReturn(100);

        assertThrows(EventCapacityExceededException.class, () -> registrationService.registerForEvent(1, 1));
        verify(registrationRepository, never()).register(any());
    }

    @Test
    void testRegisterForEventAlreadyRegistered() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(testEvent));
        when(participantRepository.findById(1)).thenReturn(Optional.of(testParticipant));
        when(registrationRepository.getRegistrationCountForEvent(1)).thenReturn(50);
        when(registrationRepository.findByEventAndParticipant(1, 1)).thenReturn(Optional.of(testRegistration));

        assertThrows(IllegalArgumentException.class, () -> registrationService.registerForEvent(1, 1));
        verify(registrationRepository, never()).register(any());
    }

    @Test
    void testCancelRegistrationSuccess() {
        when(registrationRepository.findById(1)).thenReturn(Optional.of(testRegistration));
        registrationService.cancelRegistration(1);
        verify(registrationRepository, times(1)).cancelRegistration(1);
    }

    @Test
    void testCancelRegistrationNotFound() {
        when(registrationRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(RegistrationNotFoundException.class, () -> registrationService.cancelRegistration(1));
        verify(registrationRepository, never()).cancelRegistration(anyInt());
    }

    @Test
    void testUpdateRegistrationStatusSuccess() {
        when(registrationRepository.findById(1)).thenReturn(Optional.of(testRegistration));
        registrationService.updateRegistrationStatus(1, Registration.Status.ACCEPTED);
        verify(registrationRepository, times(1)).updateStatus(1, Registration.Status.ACCEPTED);
    }

    @Test
    void testUpdateRegistrationStatusNotFound() {
        when(registrationRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(RegistrationNotFoundException.class, () -> registrationService.updateRegistrationStatus(1, Registration.Status.ACCEPTED));
        verify(registrationRepository, never()).updateStatus(anyInt(), any());
    }

    @Test
    void testGetParticipantsForEvent() {
        List<Participant> participants = Arrays.asList(testParticipant);
        when(registrationRepository.findParticipantsByEventId(1)).thenReturn(participants);

        List<Participant> result = registrationService.getParticipantsForEvent(1);
        assertEquals(1, result.size());
        verify(registrationRepository, times(1)).findParticipantsByEventId(1);
    }

    @Test
    void testGetRegistrationsForEventSortedByStatus() {
        Registration reg1 = new Registration(1, 1, 1, null, Registration.Status.PENDING);
        Registration reg2 = new Registration(2, 1, 2, null, Registration.Status.ACCEPTED);
        Registration reg3 = new Registration(3, 1, 3, null, Registration.Status.DECLINED);
        when(registrationRepository.findByEventId(1)).thenReturn(Arrays.asList(reg1, reg2, reg3));

        List<Registration> result = registrationService.getRegistrationsForEventSortedByStatus(1);
        assertEquals(Registration.Status.ACCEPTED, result.get(0).getStatus());
        assertEquals(Registration.Status.DECLINED, result.get(1).getStatus());
        assertEquals(Registration.Status.PENDING, result.get(2).getStatus());
    }

    @Test
    void testGetRegistrationsForEventSortedByStatusEmpty() {
        when(registrationRepository.findByEventId(1)).thenReturn(Collections.emptyList());
        List<Registration> result = registrationService.getRegistrationsForEventSortedByStatus(1);
        assertTrue(result.isEmpty());
    }
}
