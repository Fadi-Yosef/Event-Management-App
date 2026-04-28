package com.eventmanagement.repository;

import com.eventmanagement.database.DatabaseConnection;
import com.eventmanagement.exception.DatabaseException;
import com.eventmanagement.model.Event;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcEventRepository implements EventRepository {

    @Override
    public void save(Event event) {
        String sql = "INSERT INTO events (name, date, location, capacity, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getName());
            stmt.setDate(2, Date.valueOf(event.getDate()));
            stmt.setString(3, event.getLocation());
            stmt.setInt(4, event.getCapacity());
            stmt.setString(5, event.getDescription());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setEventId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save event", e);
        }
    }

    @Override
    public void update(Event event) {
        String sql = "UPDATE events SET name = ?, date = ?, location = ?, capacity = ?, description = ? WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getName());
            stmt.setDate(2, Date.valueOf(event.getDate()));
            stmt.setString(3, event.getLocation());
            stmt.setInt(4, event.getCapacity());
            stmt.setString(5, event.getDescription());
            stmt.setInt(6, event.getEventId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update event", e);
        }
    }

    @Override
    public void delete(int eventId) {
        String sql = "DELETE FROM events WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete event", e);
        }
    }

    @Override
    public Optional<Event> findById(int eventId) {
        String sql = "SELECT * FROM events WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEvent(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find event by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all events", e);
        }
        return events;
    }

    @Override
    public List<Event> findByNameOrDate(String searchTerm) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE name LIKE ? OR date = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            LocalDate searchDate = null;
            try {
                searchDate = LocalDate.parse(searchTerm);
            } catch (java.time.format.DateTimeParseException e) {
                // Not a valid date, will use null for date parameter
            }
            stmt.setDate(2, searchDate != null ? Date.valueOf(searchDate) : null);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search events", e);
        }
        return events;
    }

    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getInt("event_id"));
        event.setName(rs.getString("name"));
        event.setDate(rs.getDate("date").toLocalDate());
        event.setLocation(rs.getString("location"));
        event.setCapacity(rs.getInt("capacity"));
        event.setDescription(rs.getString("description"));
        return event;
    }
}
