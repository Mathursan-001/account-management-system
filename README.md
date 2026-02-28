# AMS - Account Management System

A Spring Boot application for managing accounts with REST API endpoints, built with modern Java technologies and comprehensive exception handling, testing, and logging.

## Project Overview

This is a Spring Boot 4.0.3 application that provides robust account management functionality. The system leverages:
- **JPA** for data persistence with H2 as an embedded database
- **Global exception handling** with standardized error responses
- **Comprehensive testing** using JUnit 5, Mockito, and Spring Test
- **AOP-based logging** for cross-cutting concerns
- **Bean validation** with detailed error messages
- **Builder patterns** (Lombok) for clean object creation

## Technology Stack

- **Framework**: Spring Boot 4.0.3
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: H2 (Embedded)
- **ORM**: Spring Data JPA
- **Validation**: Jakarta Bean Validation (Spring Validation)
- **Utilities**: Lombok (code generation - @Builder, @Data, etc.)
- **Testing**: JUnit 5, Mockito, Spring Boot Test Suite
- **AOP**: Spring AOP for logging and cross-cutting concerns
- **API Documentation**: Comprehensive guides for testing and exception handling

## Project Structure

```
ams/
├── src/
│   ├── main/
│   │   ├── java/com/rhb/ams/
│   │   │   ├── AmsApplication.java              # Main Spring Boot application
│   │   │   ├── config/
│   │   │   │   ├── WebClientConfig.java         # WebClient configuration
│   │   │   │   └── LoggingAspectConfig.java     # AOP logging configuration
│   │   │   ├── controller/
│   │   │   │   ├── AccountController.java
│   │   │   │   ├── CustomerController.java
│   │   │   │   ├── ExternalCallController.java
│   │   │   │   └── HealthController.java
│   │   │   ├── service/
│   │   │   │   ├── AccountService.java
│   │   │   │   ├── CustomerService.java
│   │   │   │   └── ExternalService.java
│   │   │   ├── repository/
│   │   │   │   ├── AccountRepository.java
│   │   │   │   └── CustomerRepository.java
│   │   │   ├── entity/
│   │   │   │   ├── Account.java
│   │   │   │   └── Customer.java
│   │   │   ├── dto/
│   │   │   │   ├── AccountRequestDTO.java
│   │   │   │   ├── AccountResponseDTO.java
│   │   │   │   ├── CustomerRequestDTO.java
│   │   │   │   ├── CustomerResponseDTO.java
│   │   │   │   ├── ErrorResponseDTO.java
│   │   │   │   └── ... (other DTOs)
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java   # Central exception handling
│   │   │       ├── ResourceNotFoundException.java
│   │   │       ├── DuplicateResourceException.java
│   │   │       ├── InvalidRequestException.java
│   │   │       ├── ExternalServiceException.java
│   │   │       └── DatabaseException.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/com/rhb/ams/
│           ├── service/
│           │   ├── AccountServiceTest.java
│           │   └── CustomerServiceTest.java
│           ├── controller/
│           │   ├── AccountControllerTest.java
│           │   └── CustomerControllerTest.java
│           ├── exception/
│           │   └── GlobalExceptionHandlerTest.java
│           └── AmsApplicationTests.java
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
├── EXCEPTION_HANDLER.md          # Exception handling guide
├── TESTING_GUIDE.md              # Comprehensive testing guide
├── IMPLEMENTATION.md             # Implementation details
├── IMPLEMENTATION_SUMMARY.md     # Summary of what was implemented
├── DTO_GUIDE.md                  # DTOs and validation guide
├── API_REFERENCE.md              # API endpoint reference
└── HELP.md                        # Getting started help
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use the included Maven wrapper)

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd ams
```

### 2. Build the Project

Using Maven wrapper (Windows):
```bash
mvnw.cmd clean package
```

Using Maven wrapper (Linux/Mac):
```bash
./mvnw clean package
```

Or using installed Maven:
```bash
mvn clean package
```

### 3. Run the Application

```bash
mvnw spring-boot:run
```

Or after building:
```bash
java -jar target/ams-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080` by default.

## Configuration

The application configuration is defined in `src/main/resources/application.properties`:

```properties
spring.application.name=ams
```

### H2 Database Console

Access the H2 Database Console at: `http://localhost:8080/h2-console`

**Default Configuration:**
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: (leave empty)

## Dependencies

### Core Dependencies
- `spring-boot-starter-webmvc` - Web and MVC support
- `spring-boot-starter-data-jpa` - JPA/Hibernate for ORM
- `spring-boot-starter-validation` - Bean validation with Jakarta Validation
- `spring-boot-starter-aop` - Aspect-Oriented Programming support for cross-cutting concerns
- `spring-boot-starter-webflux` - Reactive web support for external service calls
- `h2` - Embedded relational database
- `lombok` - Code generation library (Builders, Getters/Setters, etc.)

### Test Dependencies
- `spring-boot-starter-data-jpa-test` - JPA testing utilities
- `spring-boot-starter-validation-test` - Validation testing
- `spring-boot-starter-webmvc-test` - MockMvc and web testing
- JUnit 5 (included with Spring Boot Test)
- Mockito (included with Spring Boot Test)

## Exception Handling

The application implements a **global exception handling** strategy using Spring's `@ControllerAdvice` annotation for centralized error management.

### Features
- ✅ Automatic HTTP status code mapping
- ✅ Unique trace IDs for error tracking
- ✅ Field-level validation error details
- ✅ Comprehensive logging integration with AOP

### Custom Exceptions

| Exception | HTTP Status | Use Case |
|-----------|------------|----------|
| `ResourceNotFoundException` | 404 | Resource doesn't exist |
| `DuplicateResourceException` | 409 | Attempting to create duplicate |
| `InvalidRequestException` | 400 | Invalid input or business rule violation |
| `ExternalServiceException` | 502 | External API call failed |

### Error Response Format

All error responses follow a standardized structure:

```json
{
    "status": 404,
    "message": "Customer not found with id : '999'",
    "error": "Resource Not Found",
    "timestamp": "2026-02-28T10:30:45.123456",
    "path": "/api/v1/customers/999",
    "fieldErrors": {
        "email": "Customer email should be valid"
    },
    "traceId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Validation Errors

Validation errors include field-level details:

```json
{
    "status": 400,
    "message": "Validation failed",
    "error": "Validation Error",
    "fieldErrors": {
        "name": "Customer name is required",
        "email": "Customer email should be valid"
    },
    "traceId": "550e8400-e29b-41d4-a716-446655440001"
}
```
## Database Schema

### Entity Models

#### Customer
Represents a customer in the system.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key, auto-generated |
| name | String | Customer's full name |
| email | String | Customer's email address |
| createdAt | LocalDateTime | Timestamp when customer record was created |

**Relationships:**
- One-to-Many: Customer → Account (one customer can have multiple accounts)

#### Account
Represents a bank account associated with a customer.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key, auto-generated |
| accountNumber | String | Unique account number identifier |
| balance | BigDecimal | Current account balance |
| customer | Customer (ManyToOne) | Reference to the account owner (Customer) |

**Relationships:**
- Many-to-One: Account → Customer (many accounts belong to one customer)

## API Endpoints

The application provides versioned REST API endpoints for account management. The current version is **v1**.

### Base URL
```
http://localhost:8080/api/v1
```

### Customer Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/customers` | Create new customer |
| GET | `/customers/{id}` | Get customer by ID |
| GET | `/customers/search` | Search customers with filters and pagination |
| GET | `/customers/with-accounts/{customerId}` | Get customer with all accounts |
| PUT | `/customers/{id}` | Update customer |
| DELETE | `/customers/{id}` | Delete customer |
| POST | `/customers/generate-random` | Generate random customers (demo) |

**Query Parameters:**
- `name` - Filter by customer name (partial match)
- `fromDate` - Filter from date (yyyy-MM-dd)
- `toDate` - Filter to date (yyyy-MM-dd)
- `page` - Page number (default: 0)
- `size` - Records per page (default: 20)

### Account Endpoints

| Method | Endpoint                    | Description                   |
|--------|-----------------------------|-------------------------------|
| GET | `/accounts`                 | Get all accounts              |
| POST | `/accounts`                 | Create new account            |
| GET | `/accounts/{accountNumber}` | Get account by Account Number |
| PUT | `/accounts/{id}`            | Update account                |
| DELETE | `/accounts/{id}`            | Delete account                |

### Health & Utility Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | Application health status |
| GET | `/api/v1/objects` | Call external JSONPlaceholder API |

### Example Request/Response

**Create Customer:**
```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}'
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "createdAt": "2026-02-28T10:30:45"
}
```

**Error Response (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Validation failed",
  "error": "Validation Error",
  "fieldErrors": {
    "email": "Customer email should be valid"
  },
  "traceId": "550e8400-e29b-41d4-a716-446655440001"
}
```

## API Versioning

This API uses URL-based versioning to ensure backward compatibility and smooth API evolution.

- **Current Version**: v1
- **Version Format**:  `/api/v{major}`

### Version History
- **v1** - Initial release (Current)

## Build and Deployment

### Build Artifact

The build process creates an executable JAR file:
```
target/ams-0.0.1-SNAPSHOT.jar
```

### Maven Plugins
- `spring-boot-maven-plugin` - Creates executable JAR
- `maven-compiler-plugin` - Configured with Lombok annotation processor


## Support & Resources

For issues or questions, please refer to:

**Official Documentation:**
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA Guide](https://spring.io/projects/spring-data-jpa)
- [Spring AOP Documentation](https://docs.spring.io/spring-framework/reference/core/aop.html)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
