-- Event Management Database Schema
-- Author: Event Management Team
-- Version: 1.0
-- Description: Complete database schema for event management system

-- Create database
CREATE DATABASE IF NOT EXISTS event_management_db;
USE event_management_db;

-- Events table
CREATE TABLE IF NOT EXISTS events (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    location VARCHAR(255) NOT NULL,
    capacity INT NOT NULL CHECK (capacity > 0),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_event_date (date),
    INDEX idx_event_name (name),
    INDEX idx_event_location (location)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Participants table
CREATE TABLE IF NOT EXISTS participants (
    participant_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_participant_name (name),
    INDEX idx_participant_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Registrations table (junction table with additional attributes)
CREATE TABLE IF NOT EXISTS registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    participant_id INT NOT NULL,
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ACCEPTED', 'DECLINED', 'PENDING') DEFAULT 'PENDING',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    FOREIGN KEY (participant_id) REFERENCES participants(participant_id) ON DELETE CASCADE,
    UNIQUE KEY unique_event_participant (event_id, participant_id),
    INDEX idx_registration_status (status),
    INDEX idx_registration_date (registration_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Views for common queries
CREATE OR REPLACE VIEW event_registration_summary AS
SELECT 
    e.event_id,
    e.name AS event_name,
    e.date AS event_date,
    e.capacity,
    COUNT(r.registration_id) AS total_registrations,
    SUM(CASE WHEN r.status = 'ACCEPTED' THEN 1 ELSE 0 END) AS accepted_count,
    SUM(CASE WHEN r.status = 'PENDING' THEN 1 ELSE 0 END) AS pending_count,
    SUM(CASE WHEN r.status = 'DECLINED' THEN 1 ELSE 0 END) AS declined_count,
    (e.capacity - COUNT(r.registration_id)) AS available_spots
FROM events e
LEFT JOIN registrations r ON e.event_id = r.event_id
GROUP BY e.event_id, e.name, e.date, e.capacity;

-- Sample data for testing (optional)
-- INSERT INTO events (name, date, location, capacity, description) VALUES
-- ('Java Conference 2024', '2024-12-15', 'Convention Center', 100, 'Annual Java developers conference'),
-- ('Spring Boot Workshop', '2024-11-20', 'Tech Hub', 50, 'Hands-on Spring Boot workshop');

-- INSERT INTO participants (name, email) VALUES
-- ('John Doe', 'john.doe@example.com'),
-- ('Jane Smith', 'jane.smith@example.com');
