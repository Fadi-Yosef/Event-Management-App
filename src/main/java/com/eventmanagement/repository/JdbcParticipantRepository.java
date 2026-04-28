package com.eventmanagement.repository;

import com.eventmanagement.database.DatabaseConnection;
import com.eventmanagement.exception.DatabaseException;
import com.eventmanagement.model.Participant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcParticipantRepository implements ParticipantRepository {

    @Override
    public void save(Participant participant) {
        String sql = "INSERT INTO participants (name, email) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, participant.getName());
            stmt.setString(2, participant.getEmail());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    participant.setParticipantId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save participant", e);
        }
    }

    @Override
    public Optional<Participant> findById(int participantId) {
        String sql = "SELECT * FROM participants WHERE participant_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, participantId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToParticipant(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find participant by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Participant> findByEmail(String email) {
        String sql = "SELECT * FROM participants WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToParticipant(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find participant by email", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Participant> findAll() {
        List<Participant> participants = new ArrayList<>();
        String sql = "SELECT * FROM participants";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                participants.add(mapResultSetToParticipant(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all participants", e);
        }
        return participants;
    }

    private Participant mapResultSetToParticipant(ResultSet rs) throws SQLException {
        Participant participant = new Participant();
        participant.setParticipantId(rs.getInt("participant_id"));
        participant.setName(rs.getString("name"));
        participant.setEmail(rs.getString("email"));
        return participant;
    }
}
