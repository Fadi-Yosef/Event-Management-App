# Event Management App

A complete Java console application for managing events, participants, and registrations using Java SE, Object-Oriented Programming, SQL, and JDBC.

## 📋 Table of Contents
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Setup Instructions](#setup-instructions)
- [Usage](#usage)
- [Architecture & Design](#architecture--design)
- [UML Class Diagram](#uml-class-diagram)
- [Git Strategy](#git-strategy)
- [Testing](#testing)
- [Exception Handling](#exception-handling)

## ✨ Features

### Event Management
1. ✅ Create events with name, date, location, capacity, and description
2. ✅ Update existing event details
3. ✅ Delete events
4. ✅ View all events
5. ✅ Search events by name or date
6. ✅ Sort events by date or name
7. ✅ Check event capacity and availability

### Participant Management
8. ✅ Register participants with name and email
9. ✅ View all participants
10. ✅ Search participants by name
11. ✅ Sort participants alphabetically

### Registration Management
12. ✅ Register participants for events
13. ✅ Cancel participant registrations
14. ✅ Manage attendance status (Accepted, Declined, Pending)
15. ✅ View participant list for each event
16. ✅ View registrations with status for each event

## 🛠 Technologies Used

- **Java 17** - Core programming language
- **JDBC** - Database connectivity
- **MySQL 8.0** - Relational database
- **Maven** - Build automation and dependency management
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework for tests

## 📁 Project Structure

```
event-management-app/
├── src/
│   ├── main/java/com/eventmanagement/
│   │   ├── app/
│   │   │   └── EventManagementApp.java        # Main application entry point
│   │   ├── model/
│   │   │   ├── Event.java                      # Event entity
│   │   │   ├── Participant.java                # Participant entity
│   │   │   └── Registration.java               # Registration entity with Status enum
│   │   ├── repository/
│   │   │   ├── EventRepository.java            # Event repository interface
│   │   │   ├── ParticipantRepository.java      # Participant repository interface
│   │   │   ├── RegistrationRepository.java     # Registration repository interface
│   │   │   ├── JdbcEventRepository.java        # JDBC implementation for events
│   │   │   ├── JdbcParticipantRepository.java  # JDBC implementation for participants
│   │   │   └── JdbcRegistrationRepository.java # JDBC implementation for registrations
│   │   ├── service/
│   │   │   ├── EventService.java               # Event business logic
│   │   │   ├── ParticipantService.java         # Participant business logic
│   │   │   └── RegistrationService.java        # Registration business logic
│   │   ├── database/
│   │   │   ├── DatabaseConnection.java         # Database connection management
│   │   │   └── schema.sql                      # Database schema definition
│   │   ├── exception/
│   │   │   ├── DatabaseException.java          # Database error exception
│   │   │   ├── EventNotFoundException.java     # Event not found exception
│   │   │   ├── EventCapacityExceededException.java # Capacity exceeded exception
│   │   │   ├── ParticipantNotFoundException.java   # Participant not found exception
│   │   │   └── RegistrationNotFoundException.java  # Registration not found exception
│   │   └── util/
│   │       └── InputValidator.java             # Input validation utilities
│   └── test/java/com/eventmanagement/service/
│       ├── EventServiceTest.java               # Event service unit tests
│       ├── ParticipantServiceTest.java         # Participant service unit tests
│       └── RegistrationServiceTest.java        # Registration service unit tests
├── pom.xml                                     # Maven configuration
└── README.md                                   # Project documentation
```

## 🗄 Database Schema

### Tables

#### 1. Events Table
```sql
CREATE TABLE IF NOT EXISTS events (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    location VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    description TEXT
);
```

#### 2. Participants Table
```sql
CREATE TABLE IF NOT EXISTS participants (
    participant_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);
```

#### 3. Registrations Table
```sql
CREATE TABLE IF NOT EXISTS registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    participant_id INT NOT NULL,
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ACCEPTED', 'DECLINED', 'PENDING') DEFAULT 'PENDING',
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    FOREIGN KEY (participant_id) REFERENCES participants(participant_id) ON DELETE CASCADE,
    UNIQUE (event_id, participant_id)
);
```

### Relationships
- **Events ↔ Registrations**: One-to-Many (One event can have many registrations)
- **Participants ↔ Registrations**: One-to-Many (One participant can have many registrations)
- **Unique Constraint**: A participant can only register once for an event

## 🚀 Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL Server 8.0+

### Step 1: Database Setup

1. Install and start MySQL Server
2. Create the database and tables:
```bash
mysql -u root -p < src/main/java/com/eventmanagement/database/schema.sql
```

Or manually execute the SQL script in MySQL Workbench or command line.

### Step 2: Configure Database Connection

Edit `DatabaseConnection.java` with your MySQL credentials:
```java
private static final String URL = "jdbc:mysql://localhost:3306/event_management_db";
private static final String USER = "root";           // Your MySQL username
private static final String PASSWORD = "password";   // Your MySQL password
```

### Step 3: Build the Project

```bash
mvn clean install
```

### Step 4: Run the Application

```bash
mvn exec:java -Dexec.mainClass="com.eventmanagement.app.EventManagementApp"
```

Or run the main class from your IDE.

## 💻 Usage

### Menu Options

```
========== Event Management Menu ==========
--- Event Management ---
1.  Create Event
2.  Update Event
3.  Delete Event
4.  View All Events
5.  View All Events (Sorted by Date)
6.  View All Events (Sorted by Name)
7.  Search Events (by Name or Date)
--- Participant Management ---
8.  Create Participant
9.  View All Participants
10. Search Participants (by Name)
--- Registration Management ---
11. Register Participant for Event
12. Cancel Participant Registration
13. Manage Attendance Status
14. View Participant List for an Event
15. View Registrations for an Event (with Status)
16. Check Event Capacity
0.  Exit
===========================================
```

### Example Workflow

1. **Create an Event**: Select option 1 and enter event details
2. **Create a Participant**: Select option 8 and enter participant details
3. **Register Participant**: Select option 11, enter event ID and participant ID
4. **Check Capacity**: Select option 16 to see available spots
5. **Manage Attendance**: Select option 13 to update registration status

## 🏗 Architecture & Design

### Design Patterns Used

1. **Repository Pattern**: Separates data access logic from business logic
   - Interfaces: `EventRepository`, `ParticipantRepository`, `RegistrationRepository`
   - Implementations: `JdbcEventRepository`, `JdbcParticipantRepository`, `JdbcRegistrationRepository`

2. **Service Layer Pattern**: Encapsulates business logic
   - `EventService`: Event CRUD operations and capacity checks
   - `ParticipantService`: Participant management and search
   - `RegistrationService`: Registration workflow and status management

3. **Dependency Injection**: Services receive repository instances via constructor injection

4. **Single Responsibility Principle (SRP)**: Each class has one reason to change
   - Models: Data representation
   - Repositories: Data persistence
   - Services: Business logic
   - UI: Console interaction

### OOP Principles Applied

- **Encapsulation**: Private fields with public getters/setters
- **Abstraction**: Repository interfaces hide implementation details
- **Polymorphism**: Different repository implementations can be swapped
- **Inheritance**: Custom exceptions extend RuntimeException

### Java Features Used

- **Streams API**: Filtering, sorting, and transforming collections
- **Optional**: Null-safe operations for database queries
- **Enums**: Type-safe status values (ACCEPTED, DECLINED, PENDING)
- **LocalDate/LocalDateTime**: Modern date-time API
- **Try-with-resources**: Automatic resource management for JDBC

## 📊 UML Class Diagram

### Class Relationships

```
┌─────────────────────────────────────────────────────────────────┐
│                         APP LAYER                                │
├─────────────────────────────────────────────────────────────────┤
│                    EventManagementApp                           │
│  - eventService: EventService                                   │
│  - participantService: ParticipantService                       │
│  - registrationService: RegistrationService                     │
│  - scanner: Scanner                                             │
│  + start(): void                                                │
│  + main(String[]): void                                         │
└──────────────┬──────────────────────────────────────────────────┘
               │ uses
               ▼
┌─────────────────────────────────────────────────────────────────┐
│                        SERVICE LAYER                             │
├──────────────────────┬──────────────────────┬───────────────────┤
│    EventService      │ ParticipantService   │RegistrationService│
│ - eventRepository    │ - participantRepo    │ - registrationRepo│
│ - registrationRepo   │                      │ - eventRepo       │
│                      │                      │ - participantRepo │
│ + createEvent()      │ + registerParticip.  │ + registerForEvt()│
│ + updateEvent()      │ + getParticipantById()│ + cancelRegist.  │
│ + deleteEvent()      │ + getAllParticipants()│ + updateStatus() │
│ + searchEvents()     │ + searchByName()     │ + getParticipants()│
│ + getAvailableCap.   │                      │                   │
└──────────┬───────────┴──────────┬───────────┴─────────┬─────────┘
           │ depends on           │ depends on          │ depends on
           ▼                      ▼                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                      REPOSITORY LAYER                            │
├──────────────────────┬──────────────────────┬───────────────────┤
│ <<interface>>        │ <<interface>>        │ <<interface>>     │
│ EventRepository      │ ParticipantRepository│ RegistrationRepo. │
│ + save()             │ + save()             │ + register()      │
│ + update()           │ + findById()         │ + cancelRegist.   │
│ + delete()           │ + findByEmail()      │ + updateStatus()  │
│ + findById()         │ + findAll()          │ + findByEventId() │
│ + findAll()          │                      │ + findParticipants│
│ + findByNameOrDate() │                      │ + findByEventAndP.│
└──────────┬───────────┴──────────┬───────────┴─────────┬─────────┘
           │ implements           │ implements          │ implements
           ▼                      ▼                     ▼
┌──────────────────────┬──────────────────────┬───────────────────┐
│ JdbcEventRepository  │JdbcParticipantRepo.  │JdbcRegistrationR. │
│ + save()             │ + save()             │ + register()      │
│ + update()           │ + findById()         │ + cancelRegist.   │
│ + delete()           │ + findByEmail()      │ + updateStatus()  │
│ + findById()         │ + findAll()          │ + findByEventId() │
│ + findAll()          │                      │ + findParticipants│
│ + findByNameOrDate() │                      │ + findByEventAndP.│
└──────────────────────┴──────────────────────┴───────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                        MODEL LAYER                               │
├──────────────────┬──────────────────────┬───────────────────────┤
│      Event       │    Participant       │     Registration      │
│ - eventId: int   │ - participantId: int │ - registrationId: int │
│ - name: String   │ - name: String       │ - eventId: int        │
│ - date: LocalDate│ - email: String      │ - participantId: int  │
│ - location       │                      │ - regDate: LocalDateTime│
│ - capacity: int  │                      │ - status: Status      │
│ - description    │                      │                       │
│ + getters/setters│ + getters/setters    │ + getters/setters     │
└──────────────────┴──────────────────────┴──────────┬────────────┘
                                                     │
                                        ┌────────────┴──────────┐
                                        │   <<enum>> Status     │
                                        │   ACCEPTED            │
                                        │   DECLINED            │
                                        │   PENDING             │
                                        └───────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                      SUPPORTING CLASSES                          │
├──────────────────────────────┬──────────────────────────────────┤
│   DatabaseConnection         │      InputValidator              │
│   + getConnection(): Connection │ + getNonEmptyString(): String │
│                                │ + getInt(): int                │
│                                │ + getPositiveInt(): int        │
│                                │ + getDate(): LocalDate         │
│                                │ + getEmail(): String           │
└──────────────────────────────┴──────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                     EXCEPTION CLASSES                            │
│   DatabaseException                                              │
│   EventNotFoundException                                         │
│   EventCapacityExceededException                                 │
│   ParticipantNotFoundException                                   │
│   RegistrationNotFoundException                                  │
│   (All extend RuntimeException)                                  │
└─────────────────────────────────────────────────────────────────┘
```

## 🌿 Git Strategy

### Branch Strategy

We recommend using **Git Flow** or a simplified version:

```
main (production)
  └── develop (integration)
       ├── feature/event-management
       ├── feature/participant-management
       ├── feature/registration-management
       ├── bugfix/database-connection
       └── release/v1.0
```

### Branch Names
- **Feature branches**: `feature/description` (e.g., `feature/event-search`)
- **Bug fix branches**: `bugfix/description` (e.g., `bugfix/null-pointer`)
- **Release branches**: `release/version` (e.g., `release/v1.0`)
- **Hotfix branches**: `hotfix/description` (e.g., `hotfix/critical-bug`)

### Commit Message Convention

Use conventional commits format:

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

**Examples:**
```bash
feat(event): add event creation functionality
fix(database): resolve connection pooling issue
docs(readme): update setup instructions
test(service): add unit tests for EventService
refactor(repository): extract common JDBC operations
chore(deps): update MySQL connector version
```

### Suggested Commit History

```bash
# Initial setup
git commit -m "chore: initialize project structure with Maven"
git commit -m "feat(database): add database connection and schema"

# Model layer
git commit -m "feat(model): create Event, Participant, Registration entities"

# Exception layer
git commit -m "feat(exception): add custom exception classes"

# Repository layer
git commit -m "feat(repository): implement EventRepository interface"
git commit -m "feat(repository): implement ParticipantRepository interface"
git commit -m "feat(repository): implement RegistrationRepository interface"

# Service layer
git commit -m "feat(service): add EventService business logic"
git commit -m "feat(service): add ParticipantService business logic"
git commit -m "feat(service): add RegistrationService business logic"

# UI layer
git commit -m "feat(ui): implement console menu interface"
git commit -m "feat(util): add InputValidator for user input"

# Testing
git commit -m "test(service): add EventService unit tests"
git commit -m "test(service): add ParticipantService unit tests"
git commit -m "test(service): add RegistrationService unit tests"

# Documentation
git commit -m "docs: add comprehensive README and UML diagrams"
```

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Test Coverage

The project includes comprehensive unit tests for all service layer classes:

#### EventServiceTest (20 tests)
- ✅ Create event with valid/invalid capacity
- ✅ Update event (success, not found, invalid capacity)
- ✅ Delete event (success, not found)
- ✅ Get all events
- ✅ Sort events by date and name
- ✅ Search events by name and date
- ✅ Check event capacity and availability

#### ParticipantServiceTest (10 tests)
- ✅ Register participant (new, duplicate email)
- ✅ Get participant by ID and email
- ✅ Get all participants
- ✅ Sort participants by name
- ✅ Search participants by name

#### RegistrationServiceTest (12 tests)
- ✅ Register for event (success, not found, capacity exceeded, duplicate)
- ✅ Cancel registration (success, not found)
- ✅ Update registration status (success, not found)
- ✅ Get participants for event
- ✅ Get registrations sorted by status

### Test Strategy

- **Mockito** is used to mock repository dependencies
- **Arrange-Act-Assert** pattern for test structure
- Tests cover both success and error scenarios
- Edge cases are tested (empty lists, invalid inputs, boundary conditions)

## ⚠️ Exception Handling

### Custom Exceptions

| Exception | When Thrown |
|-----------|-------------|
| `DatabaseException` | SQL errors, connection failures |
| `EventNotFoundException` | Event ID doesn't exist |
| `EventCapacityExceededException` | Registration exceeds event capacity |
| `ParticipantNotFoundException` | Participant ID doesn't exist |
| `RegistrationNotFoundException` | Registration ID doesn't exist |
| `IllegalArgumentException` | Invalid input (capacity, duplicate email, etc.) |

### Error Handling Flow

```
User Input → InputValidator → Service Layer → Repository Layer → Database
                ↓                  ↓                ↓
            Validation       Business Rules     SQL Operations
            Exceptions       Exceptions         Exceptions
```

All exceptions are caught at the UI layer and displayed as user-friendly messages.

## 📝 Input Validation

The `InputValidator` class ensures data integrity:

- **Non-empty strings**: Prevents blank inputs
- **Integer validation**: Handles NumberFormatException
- **Positive integers**: Ensures capacity > 0
- **Date validation**: Parses YYYY-MM-DD format
- **Email validation**: Regex pattern matching

## 🔐 Best Practices Applied

1. ✅ **SOLID Principles**
   - Single Responsibility: Each class has one purpose
   - Open/Closed: Easy to extend with new features
   - Liskov Substitution: Repository implementations are interchangeable
   - Interface Segregation: Focused repository interfaces
   - Dependency Inversion: Depend on abstractions (interfaces)

2. ✅ **Clean Code**
   - Meaningful names for classes, methods, variables
   - Small, focused methods
   - Comprehensive comments
   - Consistent formatting

3. ✅ **Security**
   - PreparedStatement prevents SQL injection
   - Input validation prevents invalid data
   - Connection pooling ready (can be added)

4. ✅ **Performance**
   - Efficient SQL queries with indexes
   - Stream API for collection operations
   - Try-with-resources for resource management

## 🚧 Future Enhancements

- [ ] Add connection pooling (HikariCP)
- [ ] Implement pagination for large datasets
- [ ] Add event categories and tags
- [ ] Implement user authentication and authorization
- [ ] Add email notifications for registration status
- [ ] Create GUI version with JavaFX
- [ ] Export reports to CSV/PDF
- [ ] Add logging framework (SLF4J + Logback)
- [ ] Implement caching layer
- [ ] Add REST API with Spring Boot

## 📄 License

This project is open-source and available for educational purposes.

## 👥 Author

Built with ❤️ using Java SE, JDBC, and MySQL

---

**Happy Coding!** 🎉
