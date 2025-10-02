# Interview Scheduler

A Spring Boot application to schedule interviews, send invites, respond to invites, and track booking status.

## Features

- Create bookings with candidate, interviewer, date/time, and platform
- Respond to invites (Accept, Reject, Propose new time)
- Fetch booking status
- Email notifications for interview invitations
- RESTful API with Swagger documentation
- Comprehensive validation and error handling
- Unit testing

## Technologies

- **Java 22**
- **Spring Boot 3.5.6**
- **Spring Data JPA**
- **PostgreSQL**
- **Jakarta Validation**
- **Lombok**
- **Swagger/OpenAPI 3.0** (API Documentation)
- **JUnit 5 & Mockito** (Unit Testing)
- **SLF4J** (Logging)

## API Documentation

Once the application is running, access the interactive API documentation at:

- **Swagger UI**: `http://localhost:8081/swagger-ui.html`
- **API Docs**: `http://localhost:8081/v3/api-docs`

## Setup

### 1. Clone Repository

```bash
git clone https://github.com/Farin-dev/interview-booking-system
cd interview-scheduler
```

### 2. Configure Database

Update the following properties in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/DB_Name
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
```

### 3. Configure Email Account

Add your Gmail credentials in `application.properties`:

```properties
spring.mail.username=YOUR_EMAIL
spring.mail.password=YOUR_EMAIL_PASSWORD
```

**Note**: For Gmail, you need to use an [App Password] instead of your regular password.

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8081`

## Testing

Run the unit tests:

```bash
# Run all tests
mvn test

# Run with code coverage report
mvn clean test jacoco:report
```

View the coverage report at: `target/site/jacoco/index.html`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/bookings` | Create a new interview booking |
| POST | `/bookings/{id}/respond` | Respond to an invite (Accept/Reject/Propose) |
| GET | `/bookings/status/{id}` | Get booking status by ID |
