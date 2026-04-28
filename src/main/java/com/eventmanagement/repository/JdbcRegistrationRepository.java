package com.eventmanagement.repository;

import com.eventmanagement.database.DatabaseConnection;
import com.eventmanagement.exception.DatabaseException;
import com.eventmanagement.model.Participant;
import com.eventmanagement.model.Registration;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcRegistrationRepository implements RegistrationRepository {

    @Override
    public void register(Registration registration) {
        String sql = "INSERT INTO registrations (event_id, participant_id, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, registration.getEventId());
            stmt.setInt(2, registration.getParticipantId());
            stmt.setString(3, registration.getStatus().name());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    registration.setRegistrationId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to register participant for event", e);
        }
    }

    @Override
    public void cancelRegistration(int registrationId) {
        String sql = "DELETE FROM registrations WHERE registration_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, registrationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to cancel registration", e);
        }
    }

    @Override
    public void updateStatus(int registrationId, Registration.Status status) {
        String sql = "UPDATE registrations SET status = ? WHERE registration_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, registrationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update registration status", e);
        }
    }

    @Override
    public List<Registration> findByEventId(int eventId) {
        List<Registration> registrations = new ArrayList<>();
        String sql = "SELECT * FROM registrations WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find registrations by event ID", e);
        }
        return registrations;
    }

    @Override
    public Optional<Registration> findById(int registrationId) {
        String sql = "SELECT * FROM registrations WHERE registration_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, registrationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRegistration(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find registration by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Participant> findParticipantsByEventId(int eventId) {
        List<Participant> participants = new ArrayList<>();
        String sql = "SELECT p.* FROM participants p JOIN registrations r ON p.participant_id = r.participant_id WHERE r.event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Participant participant = new Participant();
                    participant.setParticipantId(rs.getInt("participant_id"));
                    participant.setName(rs.getString("name"));
                    participant.setEmail(rs.getString("email"));
                    participants.add(participant);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find participants by event ID", e);
        }
        return participants;
    }

    @Override
    public int getRegistrationCountForEvent(int eventId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get registration count for event", e);
        }
        return 0;
    }

    @Override
    public Optional<Registration> findByEventAndParticipant(int eventId, int participantId) {
        String sql = "SELECT * FROM registrations WHERE event_id = ? AND participant_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, participantId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRegistration(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find registration by event and participant", e);
        }
        return Optional.empty();
    }

    private Registration mapResultSetToRegistration(ResultSet rs) throws SQLException {
        Registration registration = new Registration();
        registration.setRegistrationId(rs.getInt("registration_id"));
        registration.setEventId(rs.getInt("event_id"));
        registration.setParticipantId(rs.getInt("participant_id"));
        Timestamp timestamp = rs.getTimestamp("registration_date");
        registration.setRegistrationDate(timestamp != null ? timestamp.toLocalDateTime() : null);
        registration.setStatus(Registration.Status.valueOf(rs.getString("status")));
        return registration;
    }
}
