package com.eventmanagement.model;

import java.time.LocalDate;

public class Event {
    private int eventId;
    private String name;
    private LocalDate date;
    private String location;
    private int capacity;
    private String description;

    public Event() {}

    public Event(String name, LocalDate date, String location, int capacity, String description) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.capacity = capacity;
        this.description = description;
    }

    public Event(int eventId, String name, LocalDate date, String location, int capacity, String description) {
        this.eventId = eventId;
        this.name = name;
        this.date = date;
        this.location = location;
        this.capacity = capacity;
        this.description = description;
    }

    // Getters and Setters
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", location='" + location + '\'' +
                ", capacity=" + capacity +
                ", description='" + description + '\'' +
                '}';
    }
}
