package com.eventmanagement.model;

import java.time.LocalDateTime;

public class Registration {
    private int registrationId;
    private int eventId;
    private int participantId;
    private LocalDateTime registrationDate;
    private Status status;

    public enum Status {
        ACCEPTED, DECLINED, PENDING
    }

    public Registration() {}

    public Registration(int eventId, int participantId, Status status) {
        this.eventId = eventId;
        this.participantId = participantId;
        this.status = status;
    }

    public Registration(int registrationId, int eventId, int participantId, LocalDateTime registrationDate, Status status) {
        this.registrationId = registrationId;
        this.eventId = eventId;
        this.participantId = participantId;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    // Getters and Setters
    public int getRegistrationId() { return registrationId; }
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public int getParticipantId() { return participantId; }
    public void setParticipantId(int participantId) { this.participantId = participantId; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    @Override
    public String toString() {
        return "Registration{" +
                "registrationId=" + registrationId +
                ", eventId=" + eventId +
                ", participantId=" + participantId +
                ", registrationDate=" + registrationDate +
                ", status=" + status +
                '}';
    }
}
