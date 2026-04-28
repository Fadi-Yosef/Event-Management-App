# UML Class Diagram - Event Management App

## Overview

This document provides a comprehensive UML class diagram description for the Event Management Application, illustrating the relationships, attributes, and methods of all classes in the system.

---

## 1. Model Layer

### 1.1 Event Class

```
┌─────────────────────────────────────────────────┐
│                    Event                        │
├─────────────────────────────────────────────────┤
│ - eventId: int                                  │
│ - name: String                                  │
│ - date: LocalDate                               │
│ - location: String                              │
│ - capacity: int                                 │
│ - description: String                           │
├─────────────────────────────────────────────────┤
│ + Event()                                       │
│ + Event(name, date, location, capacity, desc)   │
│ + Event(eventId, name, date, location, cap, d)  │
│ + getEventId(): int                             │
│ + setEventId(eventId: int): void                │
│ + getName(): String                             │
│ + setName(name: String): void                   │
│ + getDate(): LocalDate                          │
│ + setDate(date: LocalDate): void                │
│ + getLocation(): String                         │
│ + setLocation(location: String): void           │
│ + getCapacity(): int                            │
│ + setCapacity(capacity: int): void              │
│ + getDescription(): String                      │
│ + setDescription(desc: String): void            │
│ + toString(): String                            │
└─────────────────────────────────────────────────┘
```

**Responsibilities:**
- Represents an event with all its attributes
- Provides constructors for different initialization scenarios
- Encapsulates data with getters and setters

---

### 1.2 Participant Class

```
┌─────────────────────────────────────────────────┐
│                 Participant                     │
├─────────────────────────────────────────────────┤
│ - participantId: int                            │
│ - name: String                                  │
│ - email: String                                 │
├─────────────────────────────────────────────────┤
│ + Participant()                                 │
│ + Participant(name: String, email: String)      │
│ + Participant(id: int, name: String, email: S)  │
│ + getParticipantId(): int                       │
│ + setParticipantId(id: int): void               │
│ + getName(): String                             │
│ + setName(name: String): void                   │
│ + getEmail(): String                            │
│ + setEmail(email: String): void                 │
│ + toString(): String                            │
└─────────────────────────────────────────────────┘
```

**Responsibilities:**
- Represents a participant in the system
- Stores contact information (name and email)
- Email must be unique (enforced at database level)

---

### 1.3 Registration Class

```
┌─────────────────────────────────────────────────┐
│                Registration                     │
├─────────────────────────────────────────────────┤
│ - registrationId: int                           │
│ - eventId: int                                  │
│ - participantId: int                            │
│ - registrationDate: LocalDateTime               │
│ - status: Status                                │
├─────────────────────────────────────────────────┤
│ <<enumeration>> Status                          │
│   ACCEPTED                                      │
│   DECLINED                                      │
│   PENDING                                       │
├─────────────────────────────────────────────────┤
│ + Registration()                                │
│ + Registration(eventId, participantId, status)  │
│ + Registration(id, eventId, partId, date, stat) │
│ + getRegistrationId(): int                      │
│ + setRegistrationId(id: int): void              │
│ + getEventId(): int                             │
│ + setEventId(eventId: int): void                │
│ + getParticipantId(): int                       │
│ + setParticipantId(id: int): void               │
│ + getRegistrationDate(): LocalDateTime          │
│ + setRegistrationDate(date: LocalDateTime): v   │
│ + getStatus(): Status                           │
│ + setStatus(status: Status): void               │
│ + toString(): String                            │
└─────────────────────────────────────────────────┘
```

**Responsibilities:**
- Links participants to events
- Tracks registration date and attendance status
- Contains nested enum for type-safe status values

---

## 2. Repository Layer

### 2.1 EventRepository Interface

```
┌─────────────────────────────────────────────────┐
│           <<interface>>                         │
│             EventRepository                     │
├─────────────────────────────────────────────────┤
│ + save(event: Event): void                      │
│ + update(event: Event): void                    │
│ + delete(eventId: int): void                    │
│ + findById(eventId: int): Optional<Event>       │
│ + findAll(): List<Event>                        │
│ + findByNameOrDate(term: String): List<Event>   │
└─────────────────────────────────────────────────┘
```

**Responsibilities:**
- Defines contract for event data access
- Uses Optional for null-safe find operations
- Returns Lists for multiple results

---

### 2.2 JdbcEventRepository Class

```
┌─────────────────────────────────────────────────┐
│          JdbcEventRepository                    │
│     implements EventRepository                  │
├─────────────────────────────────────────────────┤
│ + save(event: Event): void                      │
│ + update(event: Event): void                    │
│ + delete(eventId: int): void                    │
│ + findById(eventId: int): Optional<Event>       │
│ + findAll(): List<Event>                        │
│ + findByNameOrDate(term: String): List<Event>   │
│ - mapResultSetToEvent(rs: ResultSet): Event     │
└─────────────────────────────────────────────────┘
```

**Responsibilities:**
- Implements EventRepository using JDBC
- Maps ResultSet to Event objects
- Handles SQL exceptions and converts to DatabaseException

---

### 2.3 ParticipantRepository Interface

```
┌─────────────────────────────────────────────────┐
│           <<interface>>                         │
│          ParticipantRepository                  │
├─────────────────────────────────────────────────┤
│ + save(participant: Participant): void          │
│ + findById(participantId: int): Optional<P>     │
│ + findByEmail(email: String): Optional<P>       │
│ + findAll(): List<Participant>                  │
└─────────────────────────────────────────────────┘
```

**Responsibilities:**
- Defines contract for participant data access
- Supports lookup by ID or email

---

### 2.4 JdbcParticipantRepository Class

```
┌─────────────────────────────────────────────────┐
│        JdbcParticipantRepository                │
│    implements ParticipantRepository             │
├─────────────────────────────────────────────────┤
│ + save(participant: Participant): void          │
│ + findById(participantId: int): Optional<P>     │
│ + findByEmail(email: String): Optional<P>       │
│ + findAll(): List<Participant>                  │
│ - mapResultSetToParticipant(rs: ResultSet): P   │
└─────────────────────────────────────────────────┘
```

**Responsibilities:**
- Implements ParticipantRepository using JDBC
- Handles participant persistence operations

---

### 2.5 RegistrationRepository Interface

```
┌─────────────────────────────────────────────────┐
│           <<interface>>                         │
│          RegistrationRepository                 │
├─────────────────────────────────────────────────┤
│ + register(registration: Registration): void    │
│ + cancelRegistration(regId: int): void          │
│ + updateStatus(regId: int, status: Status): v   │
│ + findByEventId(eventId: int): List<Regist>     │
│ + findById(regId: int): Optional<Registration>  │
│ + findParticipantsByEventId(eId: int): List<P>  │
│ + getRegistrationCountForEvent(eId: int): int   │
│ + findByEventAndParticipant(eId, pId): Opt<R>   │
└─────────────────────────────────────────────────┘
```

**Responsibilities:**
- Defines contract for registration data access
- Supports complex queries (join operations)
- Provides registration count for capacity checks

---

### 2.6 JdbcRegistrationRepository Class

```
┌─────────────────────────────────────────────────┐
│        JdbcRegistrationRepository               │
│    implements RegistrationRepository            │
├─────────────────────────────────────────────────┤
│ + register(registration: Registration): void    │
│ + cancelRegistration(regId: int): void          │
│ + updateStatus(regId: int, status: Status): v   │
│ + findByEventId(eventId: int): List<Regist>     │
│ + findById(regId: int): Optional<Registration>  │
│ + findParticipantsByEventId(eId: int): List<P>  │
│ + getRegistrationCountForEvent(eId: int): int   │
│ + findByEventAndParticipant(eId, pId): Opt<R>   │
│ - mapResultSetToRegistration(rs: ResultSet): R  │
└─────────────────────────────────────────────────┘
```

**Responsibilities:**
- Implements RegistrationRepository using JDBC
- Performs JOIN queries to fetch participants
- Handles registration lifecycle operations

---

## 3. Service Layer

### 3.1 EventService Class

```
┌─────────────────────────────────────────────────┐
│                EventService                     │
├─────────────────────────────────────────────────┤
│ - eventRepository: EventRepository              │
│ - registrationRepository: RegistrationRepo      │
├─────────────────────────────────────────────────┤
│ + EventService(eventRepo, regRepo)              │
│ + createEvent(event: Event): void               │
│ + updateEvent(event: Event): void               │
│ + deleteEvent(eventId: int): void               │
│ + getAllEvents(): List<Event>                   │
│ + getAllEventsSortedByDate(): List<Event>       │
│ + getAllEventsSortedByName(): List<Event>       │
│ + getEventById(eventId: int): Optional<Event>   │
│ + searchEvents(searchTerm: String): List<Event> │
│ + isEventFull(eventId: int): boolean            │
│ + getAvailableCapacity(eventId: int): int       │
└─────────────────────────────────────────────────┘
```

**Dependencies:**
- EventRepository (for event CRUD)
- RegistrationRepository (for capacity checks)

**Responsibilities:**
- Business logic for event management
- Validates capacity constraints
- Provides sorting and search functionality using Streams

---

### 3.2 ParticipantService Class

```
┌─────────────────────────────────────────────────┐
│             ParticipantService                  │
├─────────────────────────────────────────────────┤
│ - participantRepository: ParticipantRepository  │
├─────────────────────────────────────────────────┤
│ + ParticipantService(participantRepo)           │
│ + registerParticipant(p: Participant): void     │
│ + getParticipantById(id: int): Optional<P>      │
│ + getParticipantByEmail(email: String): Opt<P>  │
│ + getAllParticipants(): List<Participant>       │
│ + getAllParticipantsSortedByName(): List<P>     │
│ + searchParticipantsByName(name: String): L<P>  │
└─────────────────────────────────────────────────┘
```

**Dependencies:**
- ParticipantRepository

**Responsibilities:**
- Business logic for participant management
- Validates email uniqueness
- Provides search and sort operations

---

### 3.3 RegistrationService Class

```
┌─────────────────────────────────────────────────┐
│             RegistrationService                 │
├─────────────────────────────────────────────────┤
│ - registrationRepository: RegistrationRepo      │
│ - eventRepository: EventRepository              │
│ - participantRepository: ParticipantRepo        │
├─────────────────────────────────────────────────┤
│ + RegistrationService(regRepo, evtRepo, pRepo)  │
│ + registerForEvent(eventId, partId): void       │
│ + cancelRegistration(regId: int): void          │
│ + updateRegistrationStatus(id, status): void    │
│ + getParticipantsForEvent(eventId): List<Part>  │
│ + getRegistrationsForEventSortedByStatus: L<R>  │
└─────────────────────────────────────────────────┘
```

**Dependencies:**
- RegistrationRepository
- EventRepository
- ParticipantRepository

**Responsibilities:**
- Business logic for registration workflow
- Validates event capacity before registration
- Prevents duplicate registrations
- Manages attendance status updates

---

## 4. Application Layer

### 4.1 EventManagementApp Class

```
┌─────────────────────────────────────────────────┐
│            EventManagementApp                   │
├─────────────────────────────────────────────────┤
│ - eventService: EventService                    │
│ - participantService: ParticipantService        │
│ - registrationService: RegistrationService      │
│ - scanner: Scanner                              │
├─────────────────────────────────────────────────┤
│ + EventManagementApp()                          │
│ + start(): void                                 │
│ + main(args: String[]): void                    │
│ - printMainMenu(): void                         │
│ - createEvent(): void                           │
│ - updateEvent(): void                           │
│ - deleteEvent(): void                           │
│ - viewAllEvents(): void                         │
│ - viewAllEventsSortedByDate(): void             │
│ - viewAllEventsSortedByName(): void             │
│ - searchEvents(): void                          │
│ - createParticipant(): void                     │
│ - viewAllParticipants(): void                   │
│ - searchParticipants(): void                    │
│ - registerParticipantForEvent(): void           │
│ - cancelRegistration(): void                    │
│ - manageAttendanceStatus(): void                │
│ - viewParticipantsForEvent(): void              │
│ - viewRegistrationsForEvent(): void             │
│ - checkEventCapacity(): void                    │
│ - formatEvent(event: Event): String             │
│ - formatParticipant(p: Participant): String     │
└─────────────────────────────────────────────────┘
```

**Dependencies:**
- EventService
- ParticipantService
- RegistrationService

**Responsibilities:**
- Main application entry point
- Console UI and menu handling
- User input collection and output formatting
- Exception handling at UI layer

---

## 5. Database Layer

### 5.1 DatabaseConnection Class

```
┌─────────────────────────────────────────────────┐
│            DatabaseConnection                   │
├─────────────────────────────────────────────────┤
│ - URL: String = "jdbc:mysql://localhost:3306/   │
│            event_management_db"                 │
│ - USER: String = "root"                         │
│ - PASSWORD: String = "password"                 │
│ - connection: Connection (static)               │
├─────────────────────────────────────────────────┤
│ - DatabaseConnection() [private constructor]    │
│ + getConnection(): Connection [static]          │
└─────────────────────────────────────────────────┘
```

**Responsibilities:**
- Singleton pattern for database connection
- Manages MySQL connection lifecycle
- Loads JDBC driver

---

## 6. Utility Layer

### 6.1 InputValidator Class

```
┌─────────────────────────────────────────────────┐
│              InputValidator                     │
├─────────────────────────────────────────────────┤
│ - scanner: Scanner (static)                     │
│ - EMAIL_PATTERN: Pattern (static, final)        │
├─────────────────────────────────────────────────┤
│ - InputValidator() [private constructor]        │
│ + getNonEmptyString(prompt): String [static]    │
│ + getInt(prompt): int [static]                  │
│ + getPositiveInt(prompt): int [static]          │
│ + getDate(prompt): LocalDate [static]           │
│ + getEmail(prompt): String [static]             │
└─────────────────────────────────────────────────┘
```

**Responsibilities:**
- Validates all user input
- Provides loop-based input correction
- Email validation using regex pattern

---

## 7. Exception Layer

### 7.1 Custom Exceptions

All custom exceptions extend `RuntimeException`:

```
┌─────────────────────────────────────────────────┐
│            DatabaseException                    │
├─────────────────────────────────────────────────┤
│ + DatabaseException(message: String)            │
│ + DatabaseException(message, cause: Throwable)  │
└─────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────┐
│         EventNotFoundException                  │
├─────────────────────────────────────────────────┤
│ + EventNotFoundException(message: String)       │
└─────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────┐
│      EventCapacityExceededException             │
├─────────────────────────────────────────────────┤
│ + EventCapacityExceededException(msg: String)   │
└─────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────┐
│       ParticipantNotFoundException              │
├─────────────────────────────────────────────────┤
│ + ParticipantNotFoundException(msg: String)     │
└─────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────┐
│       RegistrationNotFoundException             │
├─────────────────────────────────────────────────┤
│ + RegistrationNotFoundException(msg: String)    │
└─────────────────────────────────────────────────┘
```

---

## 8. Class Relationships

### 8.1 Dependency Diagram

```
EventManagementApp
    │
    ├──► EventService
    │       │
    │       ├──► EventRepository (interface)
    │       │       │
    │       │       └──► JdbcEventRepository (implementation)
    │       │               │
    │       │               └──► DatabaseConnection
    │       │
    │       └──► RegistrationRepository (interface)
    │               │
    │               └──► JdbcRegistrationRepository (implementation)
    │
    ├──► ParticipantService
    │       │
    │       └──► ParticipantRepository (interface)
    │               │
    │               └──► JdbcParticipantRepository (implementation)
    │
    └──► RegistrationService
            │
            ├──► RegistrationRepository (interface)
            │       │
            │       └──► JdbcRegistrationRepository
            │
            ├──► EventRepository (interface)
            │       │
            │       └──► JdbcEventRepository
            │
            └──► ParticipantRepository (interface)
                    │
                    └──► JdbcParticipantRepository
```

### 8.2 Relationship Types

| Relationship | From | To | Type |
|-------------|------|-----|------|
| Association | EventManagementApp | EventService | Composition |
| Association | EventManagementApp | ParticipantService | Composition |
| Association | EventManagementApp | RegistrationService | Composition |
| Dependency | EventService | EventRepository | Interface dependency |
| Dependency | EventService | RegistrationRepository | Interface dependency |
| Dependency | ParticipantService | ParticipantRepository | Interface dependency |
| Dependency | RegistrationService | RegistrationRepository | Interface dependency |
| Dependency | RegistrationService | EventRepository | Interface dependency |
| Dependency | RegistrationService | ParticipantRepository | Interface dependency |
| Realization | JdbcEventRepository | EventRepository | Implementation |
| Realization | JdbcParticipantRepository | ParticipantRepository | Implementation |
| Realization | JdbcRegistrationRepository | RegistrationRepository | Implementation |
| Association | JdbcEventRepository | DatabaseConnection | Usage |
| Association | JdbcParticipantRepository | DatabaseConnection | Usage |
| Association | JdbcRegistrationRepository | DatabaseConnection | Usage |
| Association | EventManagementApp | InputValidator | Usage |

---

## 9. Design Patterns Used

### 9.1 Repository Pattern
- **Purpose**: Abstracts data access logic
- **Implementation**: Repository interfaces with JDBC implementations
- **Benefit**: Easy to swap data sources (e.g., JPA, in-memory)

### 9.2 Service Layer Pattern
- **Purpose**: Encapsulates business logic
- **Implementation**: Service classes coordinate multiple repositories
- **Benefit**: Separation of concerns, testable business logic

### 9.3 Dependency Injection
- **Purpose**: Decouple classes from their dependencies
- **Implementation**: Constructor injection in service classes
- **Benefit**: Easy mocking for unit tests

### 9.4 Singleton Pattern
- **Purpose**: Single database connection instance
- **Implementation**: Private constructor, static getConnection()
- **Benefit**: Connection reuse, prevents multiple connections

### 9.5 Factory Method (Implicit)
- **Purpose**: Object creation via constructors
- **Implementation**: Multiple constructors for different scenarios
- **Benefit**: Flexible object initialization

---

## 10. Sequence Diagram Examples

### 10.1 Create Event Flow

```
User -> EventManagementApp: select "Create Event"
EventManagementApp -> InputValidator: getNonEmptyString("Enter event name")
InputValidator --> EventManagementApp: return name
EventManagementApp -> InputValidator: getDate("Enter event date")
InputValidator --> EventManagementApp: return date
EventManagementApp -> Event: new Event(name, date, ...)
EventManagementApp -> EventService: createEvent(event)
EventService -> EventRepository: save(event)
EventRepository -> DatabaseConnection: getConnection()
DatabaseConnection --> EventRepository: Connection
EventRepository -> Database: INSERT INTO events...
Database --> EventRepository: success
EventRepository --> EventService: void
EventService --> EventManagementApp: void
EventManagementApp --> User: "Event created with ID: X"
```

### 10.2 Register Participant Flow

```
User -> EventManagementApp: select "Register Participant"
EventManagementApp -> RegistrationService: registerForEvent(eventId, participantId)
RegistrationService -> EventRepository: findById(eventId)
EventRepository --> RegistrationService: Optional<Event>
RegistrationService -> ParticipantRepository: findById(participantId)
ParticipantRepository --> RegistrationService: Optional<Participant>
RegistrationService -> RegistrationRepository: getRegistrationCountForEvent(eventId)
RegistrationRepository --> RegistrationService: count
RegistrationService -> EventRepository: findById(eventId) // get capacity
EventRepository --> RegistrationService: Event
RegistrationService -> RegistrationRepository: findByEventAndParticipant(eventId, participantId)
RegistrationRepository --> RegistrationService: Optional<Registration>
RegistrationService -> RegistrationRepository: register(new Registration(...))
RegistrationRepository -> Database: INSERT INTO registrations...
Database --> RegistrationRepository: success
RegistrationRepository --> RegistrationService: void
RegistrationService --> EventManagementApp: void
EventManagementApp --> User: "Participant registered successfully"
```

---

## 11. Database ER Diagram

```
┌─────────────────────────┐
│       EVENTS            │
├─────────────────────────┤
│ PK event_id INT         │
│    name VARCHAR(255)    │
│    date DATE            │
│    location VARCHAR     │
│    capacity INT         │
│    description TEXT     │
└────────────┬────────────┘
             │
             │ 1
             │
             │
             │ N
┌────────────┴────────────┐
│    REGISTRATIONS        │
├─────────────────────────┤
│ PK registration_id INT  │
│ FK event_id INT         │
│ FK participant_id INT   │
│    registration_date    │
│    status ENUM          │
└────────────┬────────────┘
             │
             │ N
             │
             │
             │ 1
┌────────────┴────────────┐
│    PARTICIPANTS         │
├─────────────────────────┤
│ PK participant_id INT   │
│    name VARCHAR(255)    │
│    email VARCHAR(255)   │
│    UNIQUE(email)        │
└─────────────────────────┘
```

---

## 12. Package Dependencies

```
com.eventmanagement.app
    └── depends on: service, util, exception

com.eventmanagement.service
    └── depends on: repository, model, exception

com.eventmanagement.repository
    └── depends on: database, model, exception

com.eventmanagement.model
    └── depends on: (none)

com.eventmanagement.database
    └── depends on: (none)

com.eventmanagement.exception
    └── depends on: (none)

com.eventmanagement.util
    └── depends on: (none)
```

---

## Summary

This UML class diagram documentation provides a complete view of the Event Management Application's architecture:

- **3 Model Classes**: Event, Participant, Registration
- **3 Repository Interfaces**: Abstract data access contracts
- **3 Repository Implementations**: JDBC-based persistence
- **3 Service Classes**: Business logic layer
- **1 Application Class**: Console UI and main entry point
- **1 Database Utility**: Connection management
- **1 Input Validator**: User input validation
- **5 Custom Exceptions**: Error handling

The design follows clean architecture principles with clear separation of concerns, making the system maintainable, testable, and extensible.
