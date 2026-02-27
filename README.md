# AMS - Account Management System

A Spring Boot application for managing accounts with REST API endpoints, built with modern Java technologies.

## Project Overview

This is a Spring Boot 4.0.3 application that provides account management functionality. The system leverages JPA for data persistence, H2 as an embedded database, and includes comprehensive validation and testing capabilities.

## Technology Stack

- **Framework**: Spring Boot 4.0.3
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: H2 (Embedded)
- **ORM**: Spring Data JPA
- **Validation**: Spring Validation
- **Utilities**: Lombok (reduces boilerplate code)
- **Testing**: Spring Boot Test Suite

## Project Structure

```
ams/
├── src/
│   ├── main/
│   │   ├── java/com/rhb/ams/
│   │   │   └── AmsApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/com/rhb/ams/
│           └── AmsApplicationTests.java
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
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
- `spring-boot-starter-data-jpa` - JPA/Hibernate
- `spring-boot-starter-validation` - Bean validation
- `h2` - Embedded database
- `lombok` - Code generation library

### Test Dependencies
- `spring-boot-starter-data-jpa-test`
- `spring-boot-starter-validation-test`
- `spring-boot-starter-webmvc-test`

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

### Entity Diagram

```
┌─────────────────┐         ┌──────────────────┐
│    CUSTOMER     │ 1    *  │      ACCOUNT     │
├─────────────────┤────────┤──────────────────┤
│ id (PK)         │        │ id (PK)          │
│ name            │        │ accountNumber    │
│ email           │        │ balance          │
│ createdAt       │        │ customerId (FK)  │
└─────────────────┘        └──────────────────┘
```

## API Endpoints

The application provides versioned REST API endpoints for account management. The current version is **v1**.

### Base URL
```
http://localhost:8080/api/v1
```

### Customer Endpoints
```
POST   /api/v1/customers                 - Create new customer
GET    /api/v1/customers/{id}            - Get customer by ID
GET    /api/v1/customers/search          - Search customers with optional filters
PUT    /api/v1/customers/{id}            - Update customer
DELETE /api/v1/customers/{id}            - Delete customer
```

**Customer Search Query Parameters (Optional):**
- `name` - Filter by customer name (supports partial matching)
- `fromDate` - Filter customers created from this date (format: yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss)
- `toDate` - Filter customers created until this date (format: yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss)
- `page` - Zero-indexed page number (default: 0)
- `size` - Number of records per page (default: 20)

**Customer Search Examples:**
```
GET /api/v1/customers/search?name=John&page=0&size=10
GET /api/v1/customers/search?fromDate=2025-01-01&toDate=2026-12-31
GET /api/v1/customers/search?name=John&fromDate=2025-06-01&toDate=2026-02-27&sort=createdAt,desc
GET /api/v1/customers/search?page=0&size=20&sort=name,asc
```

### Account Endpoints
```
GET    /api/v1/accounts                  - Get all accounts
POST   /api/v1/accounts                  - Create new account
GET    /api/v1/accounts/{id}             - Get account by ID
PUT    /api/v1/accounts/{id}             - Update account
DELETE /api/v1/accounts/{id}             - Delete account
```

### Customer-Account Join Endpoint
```
GET    /api/v1/customers/{customerId}/accounts - Get all accounts for a specific customer
```

**Description:** Retrieves all accounts belonging to a specific customer by joining the Customer and Account tables.

**Path Parameter:**
- `customerId` - The ID of the customer (required)

**Response Example:**
```json
[
  {
    "id": 1,
    "accountNumber": "ACC001",
    "balance": 5000.00,
    "customer": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "createdAt": "2025-12-15T10:30:00"
    }
  },
  {
    "id": 2,
    "accountNumber": "ACC002",
    "balance": 10000.00,
    "customer": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "createdAt": "2025-12-15T10:30:00"
    }
  }
]
```

**Example Requests:**
```
GET /api/v1/customers/1/accounts
```

## API Versioning

This API uses URL-based versioning to ensure backward compatibility and smooth API evolution.

- **Current Version**: v1
- **Version Format**: `/api/v{major}.{minor}` or `/api/v{major}`
- **Deprecation Policy**: Older API versions will be maintained for at least 2 minor releases before deprecation

### Version History
- **v1** - Initial release (Current)

## Development

### Code Style
This project uses Lombok to reduce boilerplate code. Key Lombok annotations:
- `@Data` - Generates getters, setters, equals, hashCode, and toString
- `@Entity` - JPA entity annotation
- `@Repository` - Spring Data repository interface

### Testing

Run tests with:
```bash
mvnw test
```

Test files are located in `src/test/java/com/rhb/ams/`

## Build and Deployment

### Build Artifact

The build process creates an executable JAR file:
```
target/ams-0.0.1-SNAPSHOT.jar
```

### Maven Plugins
- `spring-boot-maven-plugin` - Creates executable JAR
- `maven-compiler-plugin` - Configured with Lombok annotation processor

## Contributing

1. Create a feature branch from `main`
2. Make your changes
3. Write/update tests
4. Submit a pull request

## License

This project is provided as-is for portfolio purposes.

## Support

For issues or questions, please refer to the official Spring Boot documentation:
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA Guide](https://spring.io/projects/spring-data-jpa)

---

**Version**: 0.0.1-SNAPSHOT  
**Last Updated**: February 2026
