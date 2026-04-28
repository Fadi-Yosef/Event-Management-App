package com.eventmanagement.service;

import com.eventmanagement.exception.EventNotFoundException;
import com.eventmanagement.model.Event;
import com.eventmanagement.repository.EventRepository;
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
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new Event(1, "Test Event", LocalDate.of(2023, 12, 25), "Test Location", 100, "A test description");
    }

    @Test
    void testCreateEvent() {
        eventService.createEvent(testEvent);
        verify(eventRepository, times(1)).save(testEvent);
    }

    @Test
    void testCreateEventWithInvalidCapacity() {
        // Event constructor throws IllegalArgumentException for capacity <= 0
        assertThrows(IllegalArgumentException.class, () -> 
            new Event("Invalid", LocalDate.now(), "Loc", 0, "Desc"));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void testUpdateEvent() {
        when(eventRepository.findById(testEvent.getEventId())).thenReturn(Optional.of(testEvent));
        eventService.updateEvent(testEvent);
        verify(eventRepository, times(1)).update(testEvent);
    }

    @Test
    void testUpdateEventWithInvalidCapacity() {
        // Event constructor throws IllegalArgumentException for capacity <= 0
        assertThrows(IllegalArgumentException.class, () -> 
            new Event(1, "Invalid", LocalDate.now(), "Loc", -1, "Desc"));
        verify(eventRepository, never()).update(any());
    }

    @Test
    void testUpdateEventNotFound() {
        when(eventRepository.findById(testEvent.getEventId())).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> eventService.updateEvent(testEvent));
        verify(eventRepository, never()).update(testEvent);
    }

    @Test
    void testDeleteEvent() {
        when(eventRepository.findById(testEvent.getEventId())).thenReturn(Optional.of(testEvent));
        eventService.deleteEvent(testEvent.getEventId());
        verify(eventRepository, times(1)).delete(testEvent.getEventId());
    }

    @Test
    void testDeleteEventNotFound() {
        when(eventRepository.findById(testEvent.getEventId())).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> eventService.deleteEvent(testEvent.getEventId()));
        verify(eventRepository, never()).delete(testEvent.getEventId());
    }

    @Test
    void testGetAllEvents() {
        List<Event> events = Arrays.asList(testEvent, new Event(2, "Another Event", LocalDate.now(), "Another Location", 50, ""));
        when(eventRepository.findAll()).thenReturn(events);

        List<Event> result = eventService.getAllEvents();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void testGetAllEventsSortedByDate() {
        Event earlyEvent = new Event(1, "Early", LocalDate.of(2023, 1, 1), "Loc", 10, "");
        Event lateEvent = new Event(2, "Late", LocalDate.of(2023, 12, 31), "Loc", 10, "");
        when(eventRepository.findAll()).thenReturn(Arrays.asList(lateEvent, earlyEvent));

        List<Event> result = eventService.getAllEventsSortedByDate();
        assertEquals("Early", result.get(0).getName());
        assertEquals("Late", result.get(1).getName());
    }

    @Test
    void testGetAllEventsSortedByName() {
        Event alphaEvent = new Event(1, "Alpha", LocalDate.now(), "Loc", 10, "");
        Event betaEvent = new Event(2, "Beta", LocalDate.now(), "Loc", 10, "");
        when(eventRepository.findAll()).thenReturn(Arrays.asList(betaEvent, alphaEvent));

        List<Event> result = eventService.getAllEventsSortedByName();
        assertEquals("Alpha", result.get(0).getName());
        assertEquals("Beta", result.get(1).getName());
    }

    @Test
    void testGetEventById() {
        when(eventRepository.findById(testEvent.getEventId())).thenReturn(Optional.of(testEvent));
        Optional<Event> result = eventService.getEventById(testEvent.getEventId());
        assertTrue(result.isPresent());
        assertEquals(testEvent.getName(), result.get().getName());
        verify(eventRepository, times(1)).findById(testEvent.getEventId());
    }

    @Test
    void testGetEventByIdNotFound() {
        when(eventRepository.findById(testEvent.getEventId())).thenReturn(Optional.empty());
        Optional<Event> result = eventService.getEventById(testEvent.getEventId());
        assertTrue(result.isEmpty());
        verify(eventRepository, times(1)).findById(testEvent.getEventId());
    }

    @Test
    void testSearchEventsByName() {
        when(eventRepository.findAll()).thenReturn(Arrays.asList(testEvent));
        List<Event> result = eventService.searchEvents("Test");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testEvent.getName(), result.get(0).getName());
    }

    @Test
    void testSearchEventsByDate() {
        when(eventRepository.findAll()).thenReturn(Arrays.asList(testEvent));
        List<Event> result = eventService.searchEvents("2023-12-25");
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testSearchEventsNoMatch() {
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());
        List<Event> result = eventService.searchEvents("NonExistent");
        assertTrue(result.isEmpty());
    }

    @Test
    void testIsEventFull() {
        when(eventRepository.findById(testEvent.getEventId())).thenReturn(Optional.of(testEvent));
        when(registrationRepository.getRegistrationCountForEvent(testEvent.getEventId())).thenReturn(testEvent.getCapacity());
        assertTrue(eventService.isEventFull(testEvent.getEventId()));
    }

    @Test
    void testIsEventNotFull() {
        when(eventRepository.findById(testEvent.getEventId())).thenReturn(Optional.of(testEvent));
        when(registrationRepository.getRegistrationCountForEvent(testEvent.getEventId())).thenReturn(testEvent.getCapacity() - 1);
        assertFalse(eventService.isEventFull(testEvent.getEventId()));
    }

    @Test
    void testIsEventFullEventNotFound() {
        when(eventRepository.findById(testEvent.getEventId())).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> eventService.isEventFull(testEvent.getEventId()));
    }

    @Test
    void testGetAvailableCapacity() {
        when(eventRepository.findById(testEvent.getEventId())).thenReturn(Optional.of(testEvent));
        when(registrationRepository.getRegistrationCountForEvent(testEvent.getEventId())).thenReturn(50);
        assertEquals(50, eventService.getAvailableCapacity(testEvent.getEventId()));
    }

    @Test
    void testGetAvailableCapacityEventNotFound() {
        when(eventRepository.findById(testEvent.getEventId())).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> eventService.getAvailableCapacity(testEvent.getEventId()));
    }
}
