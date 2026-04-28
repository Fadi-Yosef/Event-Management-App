package com.eventmanagement.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a participant's registration for an event.
 * Tracks registration date and attendance status.
 */
public class Registration implements Comparable<Registration> {
    private int registrationId;
    private int eventId;
    private int participantId;
    private LocalDateTime registrationDate;
    private Status status;

    /**
     * Enum representing possible registration statuses.
     * Ordered by priority for sorting: ACCEPTED > PENDING > DECLINED
     */
    public enum Status {
        ACCEPTED, PENDING, DECLINED
    }

    // Default constructor for frameworks and reflection
    public Registration() {}

    /**
     * Creates a registration without ID (for new registrations).
     */
    public Registration(int eventId, int participantId, Status status) {
        validateInputs(eventId, participantId, status);
        this.eventId = eventId;
        this.participantId = participantId;
        this.status = status;
        this.registrationDate = LocalDateTime.now();
    }

    /**
     * Creates a registration with ID (for existing registrations from database).
     */
    public Registration(int registrationId, int eventId, int participantId, 
                       LocalDateTime registrationDate, Status status) {
        this(eventId, participantId, status);
        this.registrationId = registrationId;
        this.registrationDate = registrationDate;
    }
    
    /**
     * Validates registration inputs.
     */
    private void validateInputs(int eventId, int participantId, Status status) {
        if (eventId <= 0) {
            throw new IllegalArgumentException("Event ID must be positive");
        }
        if (participantId <= 0) {
            throw new IllegalArgumentException("Participant ID must be positive");
        }
        if (status == null) {
            throw new IllegalArgumentException("Registration status cannot be null");
        }
    }

    // Getters and Setters
    public int getRegistrationId() { 
        return registrationId; 
    }
    
    public void setRegistrationId(int registrationId) { 
        this.registrationId = registrationId; 
    }

    public int getEventId() { 
        return eventId; 
    }
    
    public void setEventId(int eventId) {
        if (eventId <= 0) {
            throw new IllegalArgumentException("Event ID must be positive");
        }
        this.eventId = eventId; 
    }

    public int getParticipantId() { 
        return participantId; 
    }
    
    public void setParticipantId(int participantId) {
        if (participantId <= 0) {
            throw new IllegalArgumentException("Participant ID must be positive");
        }
        this.participantId = participantId; 
    }

    public LocalDateTime getRegistrationDate() { 
        return registrationDate; 
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) { 
        this.registrationDate = registrationDate; 
    }

    public Status getStatus() { 
        return status; 
    }
    
    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Registration status cannot be null");
        }
        this.status = status; 
    }

    /**
     * Checks if registration is confirmed (accepted).
     */
    public boolean isAccepted() {
        return status == Status.ACCEPTED;
    }
    
    /**
     * Checks if registration is pending.
     */
    public boolean isPending() {
        return status == Status.PENDING;
    }
    
    /**
     * Checks if registration is declined.
     */
    public boolean isDeclined() {
        return status == Status.DECLINED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registration that = (Registration) o;
        return registrationId == that.registrationId &&
               eventId == that.eventId &&
               participantId == that.participantId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationId, eventId, participantId);
    }

    @Override
    public int compareTo(Registration other) {
        // Sort by status priority (ACCEPTED first), then by registration date
        int statusComparison = this.status.compareTo(other.status);
        if (statusComparison != 0) {
            return statusComparison;
        }
        if (this.registrationDate != null && other.registrationDate != null) {
            return this.registrationDate.compareTo(other.registrationDate);
        }
        return 0;
    }

    @Override
    public String toString() {
        return String.format("Registration{id=%d, eventId=%d, participantId=%d, status=%s, date=%s}",
                registrationId, eventId, participantId, status, registrationDate);
    }
    
    /**
     * Returns a formatted string for display.
     */
    public String toDisplayString(String participantName) {
        return String.format("Registration ID: %d | Participant: %s | Status: %s | Date: %s",
                registrationId, participantName, status, 
                registrationDate != null ? registrationDate.toLocalDate() : "N/A");
    }
}
