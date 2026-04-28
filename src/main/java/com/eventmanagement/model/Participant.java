package com.eventmanagement.model;

public class Participant {
    private int participantId;
    private String name;
    private String email;

    public Participant() {}

    public Participant(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Participant(int participantId, String name, String email) {
        this.participantId = participantId;
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public int getParticipantId() { return participantId; }
    public void setParticipantId(int participantId) { this.participantId = participantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Participant{" +
                "participantId=" + participantId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
