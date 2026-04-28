package com.eventmanagement.app;

import com.eventmanagement.exception.DatabaseException;
import com.eventmanagement.exception.EventCapacityExceededException;
import com.eventmanagement.exception.EventNotFoundException;
import com.eventmanagement.exception.ParticipantNotFoundException;
import com.eventmanagement.exception.RegistrationNotFoundException;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.Participant;
import com.eventmanagement.model.Registration;
import com.eventmanagement.repository.JdbcEventRepository;
import com.eventmanagement.repository.JdbcParticipantRepository;
import com.eventmanagement.repository.JdbcRegistrationRepository;
import com.eventmanagement.service.EventService;
import com.eventmanagement.service.ParticipantService;
import com.eventmanagement.service.RegistrationService;
import com.eventmanagement.util.InputValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class EventManagementApp {
    private final EventService eventService;
    private final ParticipantService participantService;
    private final RegistrationService registrationService;
    private final Scanner scanner = new Scanner(System.in);

    public EventManagementApp() {
        JdbcEventRepository eventRepository = new JdbcEventRepository();
        JdbcParticipantRepository participantRepository = new JdbcParticipantRepository();
        JdbcRegistrationRepository registrationRepository = new JdbcRegistrationRepository();

        this.eventService = new EventService(eventRepository, registrationRepository);
        this.participantService = new ParticipantService(participantRepository);
        this.registrationService = new RegistrationService(registrationRepository, eventRepository, participantRepository);
    }

    public void start() {
        System.out.println("--- Welcome to Event Management App ---");
        boolean exit = false;
        while (!exit) {
            printMainMenu();
            int choice = InputValidator.getInt("Enter your choice: ");
            try {
                switch (choice) {
                    case 1: createEvent(); break;
                    case 2: updateEvent(); break;
                    case 3: deleteEvent(); break;
                    case 4: viewAllEvents(); break;
                    case 5: viewAllEventsSortedByDate(); break;
                    case 6: viewAllEventsSortedByName(); break;
                    case 7: searchEvents(); break;
                    case 8: createParticipant(); break;
                    case 9: viewAllParticipants(); break;
                    case 10: searchParticipants(); break;
                    case 11: registerParticipantForEvent(); break;
                    case 12: cancelRegistration(); break;
                    case 13: manageAttendanceStatus(); break;
                    case 14: viewParticipantsForEvent(); break;
                    case 15: viewRegistrationsForEvent(); break;
                    case 16: checkEventCapacity(); break;
                    case 0: exit = true; break;
                    default: System.out.println("Invalid choice. Try again.");
                }
            } catch (EventNotFoundException | ParticipantNotFoundException | EventCapacityExceededException |
                     RegistrationNotFoundException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (DatabaseException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    private void printMainMenu() {
        System.out.println("\n========== Event Management Menu ==========");
        System.out.println("--- Event Management ---");
        System.out.println("1.  Create Event");
        System.out.println("2.  Update Event");
        System.out.println("3.  Delete Event");
        System.out.println("4.  View All Events");
        System.out.println("5.  View All Events (Sorted by Date)");
        System.out.println("6.  View All Events (Sorted by Name)");
        System.out.println("7.  Search Events (by Name or Date)");
        System.out.println("--- Participant Management ---");
        System.out.println("8.  Create Participant");
        System.out.println("9.  View All Participants");
        System.out.println("10. Search Participants (by Name)");
        System.out.println("--- Registration Management ---");
        System.out.println("11. Register Participant for Event");
        System.out.println("12. Cancel Participant Registration");
        System.out.println("13. Manage Attendance Status");
        System.out.println("14. View Participant List for an Event");
        System.out.println("15. View Registrations for an Event (with Status)");
        System.out.println("16. Check Event Capacity");
        System.out.println("0.  Exit");
        System.out.println("===========================================");
    }

    private void createEvent() {
        String name = InputValidator.getNonEmptyString("Enter event name: ");
        LocalDate date = InputValidator.getDate("Enter event date");
        String location = InputValidator.getNonEmptyString("Enter event location: ");
        int capacity = InputValidator.getPositiveInt("Enter event capacity: ");
        String description = InputValidator.getNonEmptyString("Enter event description: ");

        Event event = new Event(name, date, location, capacity, description);
        eventService.createEvent(event);
        System.out.println("Event created successfully with ID: " + event.getEventId());
    }

    private void updateEvent() {
        int id = InputValidator.getInt("Enter event ID to update: ");
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            System.out.println("Current details: " + event);
            event.setName(InputValidator.getNonEmptyString("Enter new name: "));
            event.setDate(InputValidator.getDate("Enter new date"));
            event.setLocation(InputValidator.getNonEmptyString("Enter new location: "));
            event.setCapacity(InputValidator.getPositiveInt("Enter new capacity: "));
            event.setDescription(InputValidator.getNonEmptyString("Enter new description: "));
            eventService.updateEvent(event);
            System.out.println("Event updated successfully.");
        } else {
            System.out.println("Event not found.");
        }
    }

    private void deleteEvent() {
        int id = InputValidator.getInt("Enter event ID to delete: ");
        eventService.deleteEvent(id);
        System.out.println("Event deleted successfully.");
    }

    private void viewAllEvents() {
        List<Event> events = eventService.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("No events found.");
        } else {
            System.out.println("\n--- All Events ---");
            events.forEach(e -> System.out.println(formatEvent(e)));
        }
    }

    private void viewAllEventsSortedByDate() {
        List<Event> events = eventService.getAllEventsSortedByDate();
        if (events.isEmpty()) {
            System.out.println("No events found.");
        } else {
            System.out.println("\n--- Events Sorted by Date ---");
            events.forEach(e -> System.out.println(formatEvent(e)));
        }
    }

    private void viewAllEventsSortedByName() {
        List<Event> events = eventService.getAllEventsSortedByName();
        if (events.isEmpty()) {
            System.out.println("No events found.");
        } else {
            System.out.println("\n--- Events Sorted by Name ---");
            events.forEach(e -> System.out.println(formatEvent(e)));
        }
    }

    private void searchEvents() {
        String searchTerm = InputValidator.getNonEmptyString("Enter event name or date (YYYY-MM-DD): ");
        List<Event> events = eventService.searchEvents(searchTerm);
        if (events.isEmpty()) {
            System.out.println("No matching events found.");
        } else {
            System.out.println("\n--- Search Results ---");
            events.forEach(e -> System.out.println(formatEvent(e)));
        }
    }

    private void createParticipant() {
        String name = InputValidator.getNonEmptyString("Enter participant name: ");
        String email = InputValidator.getEmail("Enter participant email: ");
        Participant participant = new Participant(name, email);
        participantService.registerParticipant(participant);
        System.out.println("Participant created successfully with ID: " + participant.getParticipantId());
    }

    private void viewAllParticipants() {
        List<Participant> participants = participantService.getAllParticipantsSortedByName();
        if (participants.isEmpty()) {
            System.out.println("No participants found.");
        } else {
            System.out.println("\n--- All Participants ---");
            participants.forEach(p -> System.out.println(formatParticipant(p)));
        }
    }

    private void searchParticipants() {
        String name = InputValidator.getNonEmptyString("Enter participant name to search: ");
        List<Participant> participants = participantService.searchParticipantsByName(name);
        if (participants.isEmpty()) {
            System.out.println("No matching participants found.");
        } else {
            System.out.println("\n--- Search Results ---");
            participants.forEach(p -> System.out.println(formatParticipant(p)));
        }
    }

    private void registerParticipantForEvent() {
        int eventId = InputValidator.getInt("Enter event ID: ");
        int participantId = InputValidator.getInt("Enter participant ID: ");
        registrationService.registerForEvent(eventId, participantId);
        System.out.println("Participant registered successfully for event.");
    }

    private void cancelRegistration() {
        int registrationId = InputValidator.getInt("Enter registration ID to cancel: ");
        registrationService.cancelRegistration(registrationId);
        System.out.println("Registration canceled successfully.");
    }

    private void manageAttendanceStatus() {
        int registrationId = InputValidator.getInt("Enter registration ID: ");
        System.out.println("1. ACCEPTED");
        System.out.println("2. DECLINED");
        System.out.println("3. PENDING");
        int statusChoice = InputValidator.getInt("Choose status: ");
        Registration.Status status;
        switch (statusChoice) {
            case 1: status = Registration.Status.ACCEPTED; break;
            case 2: status = Registration.Status.DECLINED; break;
            case 3: status = Registration.Status.PENDING; break;
            default: System.out.println("Invalid choice."); return;
        }
        registrationService.updateRegistrationStatus(registrationId, status);
        System.out.println("Status updated successfully.");
    }

    private void viewParticipantsForEvent() {
        int eventId = InputValidator.getInt("Enter event ID: ");
        List<Participant> participants = registrationService.getParticipantsForEvent(eventId);
        if (participants.isEmpty()) {
            System.out.println("No participants registered for this event.");
        } else {
            System.out.println("\n--- Participants for Event ---");
            participants.forEach(p -> System.out.println(formatParticipant(p)));
        }
    }

    private void viewRegistrationsForEvent() {
        int eventId = InputValidator.getInt("Enter event ID: ");
        List<Registration> registrations = registrationService.getRegistrationsForEventSortedByStatus(eventId);
        if (registrations.isEmpty()) {
            System.out.println("No registrations found for this event.");
        } else {
            System.out.println("\n--- Registrations for Event ---");
            for (Registration r : registrations) {
                Participant p = participantService.getParticipantById(r.getParticipantId()).orElse(null);
                String participantName = p != null ? p.getName() : "Unknown";
                System.out.printf("Registration ID: %d | Participant: %s | Status: %s | Date: %s%n",
                        r.getRegistrationId(), participantName, r.getStatus(), r.getRegistrationDate());
            }
        }
    }

    private void checkEventCapacity() {
        int eventId = InputValidator.getInt("Enter event ID: ");
        int available = eventService.getAvailableCapacity(eventId);
        System.out.println("Available capacity: " + available);
        if (available <= 0) {
            System.out.println("Event is FULL.");
        } else {
            System.out.println("Event has " + available + " spot(s) remaining.");
        }
    }

    private String formatEvent(Event event) {
        return String.format("ID: %d | Name: %s | Date: %s | Location: %s | Capacity: %d | Description: %s",
                event.getEventId(), event.getName(), event.getDate(), event.getLocation(), event.getCapacity(), event.getDescription());
    }

    private String formatParticipant(Participant participant) {
        return String.format("ID: %d | Name: %s | Email: %s",
                participant.getParticipantId(), participant.getName(), participant.getEmail());
    }

    public static void main(String[] args) {
        new EventManagementApp().start();
    }
}
