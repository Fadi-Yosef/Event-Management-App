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

/**
 * Main application entry point for Event Management System.
 * Provides console-based UI for managing events, participants, and registrations.
 */
public class EventManagementApp {
    private final EventService eventService;
    private final ParticipantService participantService;
    private final RegistrationService registrationService;

    /**
     * Initializes the application with all required services and repositories.
     */
    public EventManagementApp() {
        JdbcEventRepository eventRepository = new JdbcEventRepository();
        JdbcParticipantRepository participantRepository = new JdbcParticipantRepository();
        JdbcRegistrationRepository registrationRepository = new JdbcRegistrationRepository();

        this.eventService = new EventService(eventRepository, registrationRepository);
        this.participantService = new ParticipantService(participantRepository);
        this.registrationService = new RegistrationService(registrationRepository, eventRepository, participantRepository);
    }

    /**
     * Starts the application main loop.
     */
    public void start() {
        displayWelcome();
        
        boolean exit = false;
        while (!exit) {
            try {
                displayMainMenu();
                int choice = InputValidator.getMenuChoice(0, 16, "\nEnter your choice (0-16): ");
                exit = handleMenuChoice(choice);
            } catch (EventNotFoundException | ParticipantNotFoundException | EventCapacityExceededException |
                     RegistrationNotFoundException | IllegalArgumentException e) {
                System.out.println("\n⚠ Error: " + e.getMessage());
            } catch (DatabaseException e) {
                System.out.println("\n❌ Database error: " + e.getMessage());
                System.out.println("Please check your database connection and try again.");
            } catch (Exception e) {
                System.out.println("\n❌ Unexpected error: " + e.getMessage());
            }
        }
        
        displayGoodbye();
    }

    /**
     * Displays welcome message.
     */
    private void displayWelcome() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("   Welcome to Event Management System");
        System.out.println("=".repeat(50));
    }

    /**
     * Displays goodbye message.
     */
    private void displayGoodbye() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("   Thank you for using Event Management System");
        System.out.println("   Goodbye!");
        System.out.println("=".repeat(50));
    }

    /**
     * Displays the main menu options.
     */
    private void displayMainMenu() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("           MAIN MENU");
        System.out.println("-".repeat(50));
        System.out.println("  EVENT MANAGEMENT:");
        System.out.println("    1.  Create Event");
        System.out.println("    2.  Update Event");
        System.out.println("    3.  Delete Event");
        System.out.println("    4.  View All Events");
        System.out.println("    5.  View Events (Sorted by Date)");
        System.out.println("    6.  View Events (Sorted by Name)");
        System.out.println("    7.  Search Events");
        System.out.println("  PARTICIPANT MANAGEMENT:");
        System.out.println("    8.  Register Participant");
        System.out.println("    9.  View All Participants");
        System.out.println("    10. Search Participants");
        System.out.println("  REGISTRATION MANAGEMENT:");
        System.out.println("    11. Register for Event");
        System.out.println("    12. Cancel Registration");
        System.out.println("    13. Update Attendance Status");
        System.out.println("    14. View Event Participants");
        System.out.println("    15. View Event Registrations");
        System.out.println("    16. Check Event Capacity");
        System.out.println("  EXIT:");
        System.out.println("    0.  Exit Application");
        System.out.println("-".repeat(50));
    }
    
    /**
     * Handles menu choice and executes corresponding action.
     * @param choice User's menu selection
     * @return true if user wants to exit, false otherwise
     */
    private boolean handleMenuChoice(int choice) {
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
            case 0: return true;
            default: System.out.println("⚠ Invalid choice. Please try again.");
        }
        return false;
    }

    /**
     * Creates a new event with user input.
     */
    private void createEvent() {
        System.out.println("\n>>> Create New Event");
        String name = InputValidator.getName("Enter event name: ");
        LocalDate date = InputValidator.getDate("Enter event date");
        String location = InputValidator.getNonEmptyString("Enter event location: ");
        int capacity = InputValidator.getPositiveInt("Enter maximum capacity: ");
        String description = InputValidator.getNonEmptyString("Enter event description: ");

        Event event = new Event(name, date, location, capacity, description);
        eventService.createEvent(event);
        System.out.println("✓ Event created successfully with ID: " + event.getEventId());
    }

    /**
     * Updates an existing event.
     */
    private void updateEvent() {
        System.out.println("\n>>> Update Event");
        int id = InputValidator.getPositiveInt("Enter event ID to update: ");
        
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            System.out.println("⚠ Event not found with ID: " + id);
            return;
        }
        
        Event event = eventOpt.get();
        System.out.println("Current: " + event.toDisplayString());
        
        if (InputValidator.getConfirmation("Update this event?")) {
            event.setName(InputValidator.getName("Enter new name: "));
            event.setDate(InputValidator.getDate("Enter new date"));
            event.setLocation(InputValidator.getNonEmptyString("Enter new location: "));
            event.setCapacity(InputValidator.getPositiveInt("Enter new capacity: "));
            event.setDescription(InputValidator.getNonEmptyString("Enter new description: "));
            
            eventService.updateEvent(event);
            System.out.println("✓ Event updated successfully.");
        } else {
            System.out.println("Update cancelled.");
        }
    }

    /**
     * Deletes an event with confirmation.
     */
    private void deleteEvent() {
        System.out.println("\n>>> Delete Event");
        int id = InputValidator.getPositiveInt("Enter event ID to delete: ");
        
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            System.out.println("⚠ Event not found with ID: " + id);
            return;
        }
        
        System.out.println("Event to delete: " + eventOpt.get().toDisplayString());
        if (InputValidator.getConfirmation("Are you sure you want to delete this event?")) {
            eventService.deleteEvent(id);
            System.out.println("✓ Event deleted successfully.");
        } else {
            System.out.println("Delete cancelled.");
        }
    }

    /**
     * Displays all events.
     */
    private void viewAllEvents() {
        System.out.println("\n>>> All Events");
        List<Event> events = eventService.getAllEvents();
        displayEvents(events, "No events found.");
    }

    /**
     * Displays events sorted by date.
     */
    private void viewAllEventsSortedByDate() {
        System.out.println("\n>>> Events Sorted by Date");
        List<Event> events = eventService.getAllEventsSortedByDate();
        displayEvents(events, "No events found.");
    }

    /**
     * Displays events sorted by name.
     */
    private void viewAllEventsSortedByName() {
        System.out.println("\n>>> Events Sorted by Name");
        List<Event> events = eventService.getAllEventsSortedByName();
        displayEvents(events, "No events found.");
    }

    /**
     * Helper method to display a list of events.
     */
    private void displayEvents(List<Event> events, String emptyMessage) {
        if (events.isEmpty()) {
            System.out.println(emptyMessage);
        } else {
            System.out.println("Found " + events.size() + " event(s):\n");
            events.forEach(event -> System.out.println(event.toDisplayString()));
        }
    }

    /**
     * Searches events by name or date.
     */
    private void searchEvents() {
        System.out.println("\n>>> Search Events");
        String searchTerm = InputValidator.getNonEmptyString("Enter event name or date (YYYY-MM-DD): ");
        List<Event> events = eventService.searchEvents(searchTerm);
        displayEvents(events, "No matching events found.");
    }

    /**
     * Creates a new participant.
     */
    private void createParticipant() {
        System.out.println("\n>>> Register New Participant");
        String name = InputValidator.getName("Enter participant name: ");
        String email = InputValidator.getEmail("Enter participant email: ");
        
        Participant participant = new Participant(name, email);
        participantService.registerParticipant(participant);
        System.out.println("✓ Participant created successfully with ID: " + participant.getParticipantId());
    }

    /**
     * Displays all participants.
     */
    private void viewAllParticipants() {
        System.out.println("\n>>> All Participants");
        List<Participant> participants = participantService.getAllParticipantsSortedByName();
        displayParticipants(participants, "No participants found.");
    }

    /**
     * Searches participants by name.
     */
    private void searchParticipants() {
        System.out.println("\n>>> Search Participants");
        String name = InputValidator.getName("Enter participant name to search: ");
        List<Participant> participants = participantService.searchParticipantsByName(name);
        displayParticipants(participants, "No matching participants found.");
    }

    /**
     * Helper method to display a list of participants.
     */
    private void displayParticipants(List<Participant> participants, String emptyMessage) {
        if (participants.isEmpty()) {
            System.out.println(emptyMessage);
        } else {
            System.out.println("Found " + participants.size() + " participant(s):\n");
            participants.forEach(p -> System.out.println(p.toDisplayString()));
        }
    }

    /**
     * Registers a participant for an event.
     */
    private void registerParticipantForEvent() {
        System.out.println("\n>>> Register Participant for Event");
        int eventId = InputValidator.getPositiveInt("Enter event ID: ");
        int participantId = InputValidator.getPositiveInt("Enter participant ID: ");
        
        registrationService.registerForEvent(eventId, participantId);
        System.out.println("✓ Participant registered successfully for event.");
    }

    /**
     * Cancels a registration.
     */
    private void cancelRegistration() {
        System.out.println("\n>>> Cancel Registration");
        int registrationId = InputValidator.getPositiveInt("Enter registration ID to cancel: ");
        
        if (InputValidator.getConfirmation("Are you sure you want to cancel this registration?")) {
            registrationService.cancelRegistration(registrationId);
            System.out.println("✓ Registration canceled successfully.");
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    /**
     * Updates attendance status for a registration.
     */
    private void manageAttendanceStatus() {
        System.out.println("\n>>> Update Attendance Status");
        int registrationId = InputValidator.getPositiveInt("Enter registration ID: ");
        
        Registration.Status status = promptForStatus();
        if (status == null) {
            System.out.println("Invalid status selection.");
            return;
        }
        
        registrationService.updateRegistrationStatus(registrationId, status);
        System.out.println("✓ Status updated to: " + status);
    }

    /**
     * Prompts user to select a registration status.
     * @return Selected status or null if invalid
     */
    private Registration.Status promptForStatus() {
        System.out.println("Select status:");
        System.out.println("  1. ACCEPTED - Participant will attend");
        System.out.println("  2. DECLINED - Participant cannot attend");
        System.out.println("  3. PENDING  - Awaiting confirmation");
        
        int choice = InputValidator.getMenuChoice(1, 3, "Enter choice (1-3): ");
        return switch (choice) {
            case 1 -> Registration.Status.ACCEPTED;
            case 2 -> Registration.Status.DECLINED;
            case 3 -> Registration.Status.PENDING;
            default -> null;
        };
    }

    /**
     * Views all participants registered for an event.
     */
    private void viewParticipantsForEvent() {
        System.out.println("\n>>> View Event Participants");
        int eventId = InputValidator.getPositiveInt("Enter event ID: ");
        
        List<Participant> participants = registrationService.getParticipantsForEvent(eventId);
        if (participants.isEmpty()) {
            System.out.println("No participants registered for this event.");
        } else {
            System.out.println("Found " + participants.size() + " participant(s):\n");
            participants.forEach(p -> System.out.println(p.toDisplayString()));
        }
    }

    /**
     * Views all registrations for an event with status.
     */
    private void viewRegistrationsForEvent() {
        System.out.println("\n>>> View Event Registrations");
        int eventId = InputValidator.getPositiveInt("Enter event ID: ");
        
        List<Registration> registrations = registrationService.getRegistrationsForEventSortedByStatus(eventId);
        if (registrations.isEmpty()) {
            System.out.println("No registrations found for this event.");
        } else {
            System.out.println("Found " + registrations.size() + " registration(s):\n");
            registrations.forEach(registration -> {
                String participantName = getParticipantName(registration.getParticipantId());
                System.out.println(registration.toDisplayString(participantName));
            });
        }
    }

    /**
     * Helper method to get participant name by ID.
     */
    private String getParticipantName(int participantId) {
        return participantService.getParticipantById(participantId)
                .map(Participant::getName)
                .orElse("Unknown");
    }

    /**
     * Checks and displays event capacity information.
     */
    private void checkEventCapacity() {
        System.out.println("\n>>> Check Event Capacity");
        int eventId = InputValidator.getPositiveInt("Enter event ID: ");
        
        EventService.EventSummary summary = eventService.getEventSummary(eventId);
        
        System.out.println("\nEvent: " + summary.getEvent().getName());
        System.out.println("Date: " + summary.getEvent().getDate());
        System.out.println("Total Capacity: " + summary.getEvent().getCapacity());
        System.out.println("Current Registrations: " + summary.getCurrentRegistrations());
        System.out.println("Available Spots: " + summary.getAvailableCapacity());
        System.out.println("Status: " + (summary.isFull() ? "❌ FULL" : "✓ OPEN"));
    }

    /**
     * Application entry point.
     */
    public static void main(String[] args) {
        new EventManagementApp().start();
    }
}
