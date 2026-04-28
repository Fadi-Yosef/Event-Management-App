package com.eventmanagement.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an event in the management system.
 * Encapsulates event details with proper validation.
 */
public class Event implements Comparable<Event> {
    private int eventId;
    private String name;
    private LocalDate date;
    private String location;
    private int capacity;
    private String description;

    // Default constructor for frameworks and reflection
    public Event() {}

    /**
     * Creates an event without ID (for new events).
     */
    public Event(String name, LocalDate date, String location, int capacity, String description) {
        validateInputs(name, date, location, capacity);
        this.name = name;
        this.date = date;
        this.location = location;
        this.capacity = capacity;
        this.description = description;
    }

    /**
     * Creates an event with ID (for existing events from database).
     */
    public Event(int eventId, String name, LocalDate date, String location, int capacity, String description) {
        this(name, date, location, capacity, description);
        this.eventId = eventId;
    }
    
    /**
     * Validates event inputs.
     */
    private void validateInputs(String name, LocalDate date, String location, int capacity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Event name cannot be empty");
        }
        if (date == null) {
            throw new IllegalArgumentException("Event date cannot be null");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Event location cannot be empty");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Event capacity must be greater than zero");
        }
    }

    // Getters and Setters
    public int getEventId() { 
        return eventId; 
    }
    
    public void setEventId(int eventId) { 
        this.eventId = eventId; 
    }

    public String getName() { 
        return name; 
    }
    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Event name cannot be empty");
        }
        this.name = name; 
    }

    public LocalDate getDate() { 
        return date; 
    }
    
    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Event date cannot be null");
        }
        this.date = date; 
    }

    public String getLocation() { 
        return location; 
    }
    
    public void setLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Event location cannot be empty");
        }
        this.location = location; 
    }

    public int getCapacity() { 
        return capacity; 
    }
    
    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Event capacity must be greater than zero");
        }
        this.capacity = capacity; 
    }

    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }

    /**
     * Checks if event has available capacity.
     */
    public boolean hasAvailableCapacity(int currentRegistrations) {
        return currentRegistrations < capacity;
    }
    
    /**
     * Gets remaining capacity.
     */
    public int getRemainingCapacity(int currentRegistrations) {
        return Math.max(0, capacity - currentRegistrations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return eventId == event.eventId &&
               capacity == event.capacity &&
               Objects.equals(name, event.name) &&
               Objects.equals(date, event.date) &&
               Objects.equals(location, event.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, name, date, location, capacity);
    }

    @Override
    public int compareTo(Event other) {
        // Primary sort by date, secondary by name
        int dateComparison = this.date.compareTo(other.date);
        if (dateComparison != 0) {
            return dateComparison;
        }
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        return String.format("Event{id=%d, name='%s', date=%s, location='%s', capacity=%d}",
                eventId, name, date, location, capacity);
    }
    
    /**
     * Returns a formatted string for display.
     */
    public String toDisplayString() {
        return String.format("ID: %d | Name: %s | Date: %s | Location: %s | Capacity: %d | Description: %s",
                eventId, name, date, location, capacity, 
                description != null ? description : "N/A");
    }
}
