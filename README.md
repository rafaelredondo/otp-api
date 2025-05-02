# OTP Service

A robust One-Time Password (OTP) service implementation with security, fault tolerance, and reliability features.

## Features

### Functional Requirements

#### OTP Management
- Generation of secure one-time passwords
- Validation of OTPs with expiration checks
- Case-insensitive email handling
- OTP history tracking
- Email notifications for OTP delivery

#### Security
- Encrypted OTP storage
- Spring Security integration
- Rate limiting for OTP requests
- Protection against brute force attacks
- Headers security configuration

#### Fault Tolerance
- Message queuing with RabbitMQ
- Automatic retry mechanism for failed operations
- Dead Letter Queue (DLQ) for failed attempts
- Email notification queuing
- Error recovery procedures

### Non-Functional Requirements

#### Security
- Data encryption at rest
- Secure communication channels
- Protection against common attack vectors
- Rate limiting implementation
- Access control and authentication

#### Reliability
- Fault tolerance through message queuing
- Automatic retry mechanisms
- Dead letter queuing for failed operations
- System state recovery
- Transaction management

#### Testability
- Comprehensive unit tests
- Integration tests
- Mocked external dependencies
- Isolated test environments
- Test coverage for retry scenarios

#### Maintainability
- Clean code architecture
- Dependency injection
- Interface-based design
- Clear separation of concerns
- Documented code and APIs

## Technical Stack
- Java 17+
- Spring Boot
- Spring Security
- RabbitMQ
- JUnit 5
- Mockito
- Maven

## Project Structure
```
src/
├── main/
│   └── java/         # Application source code
│       └── com/
│           └── example/    # Main package
└── test/
    └── java/         # Test source code
        └── com/
            └── example/    # Test package
```

## Getting Started
1. Make sure you have Java JDK 17 or later installed
2. Make sure you have Maven installed
3. Build the project: `mvn clean install`
4. Run the tests: `mvn test`

## Dependencies
- JUnit 5 for testing 