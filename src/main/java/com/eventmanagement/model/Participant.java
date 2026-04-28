package com.eventmanagement.model;

import java.util.Objects;

/**
 * Represents a participant in the event management system.
 * Encapsulates participant details with validation.
 */
public class Participant implements Comparable<Participant> {
    private int participantId;
    private String name;
    private String email;

    // Default constructor for frameworks and reflection
    public Participant() {}

    /**
     * Creates a participant without ID (for new participants).
     */
    public Participant(String name, String email) {
        validateInputs(name, email);
        this.name = name;
        this.email = email.toLowerCase(); // Normalize email
    }

    /**
     * Creates a participant with ID (for existing participants from database).
     */
    public Participant(int participantId, String name, String email) {
        this(name, email);
        this.participantId = participantId;
    }
    
    /**
     * Validates participant inputs.
     */
    private void validateInputs(String name, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Participant name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Participant email cannot be empty");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    // Getters and Setters
    public int getParticipantId() { 
        return participantId; 
    }
    
    public void setParticipantId(int participantId) { 
        this.participantId = participantId; 
    }

    public String getName() { 
        return name; 
    }
    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Participant name cannot be empty");
        }
        this.name = name; 
    }

    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Participant email cannot be empty");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return participantId == that.participantId &&
               Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participantId, email);
    }

    @Override
    public int compareTo(Participant other) {
        // Sort by name (case-insensitive), then by email
        int nameComparison = this.name.compareToIgnoreCase(other.name);
        if (nameComparison != 0) {
            return nameComparison;
        }
        return this.email.compareTo(other.email);
    }

    @Override
    public String toString() {
        return String.format("Participant{id=%d, name='%s', email='%s'}",
                participantId, name, email);
    }
    
    /**
     * Returns a formatted string for display.
     */
    public String toDisplayString() {
        return String.format("ID: %d | Name: %s | Email: %s",
                participantId, name, email);
    }
}
