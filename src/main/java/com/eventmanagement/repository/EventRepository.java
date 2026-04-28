package com.eventmanagement.repository;

import com.eventmanagement.model.Event;
import java.util.List;
import java.util.Optional;

public interface EventRepository {
    void save(Event event);
    void update(Event event);
    void delete(int eventId);
    Optional<Event> findById(int eventId);
    List<Event> findAll();
    List<Event> findByNameOrDate(String searchTerm);
}
