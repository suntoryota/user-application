```markdown
# User Management System

A Spring Boot application with Vaadin UI for managing users and generating reports.

## Technologies Used
- Spring Boot 3.2
- Vaadin 24.3
- JasperReports
- H2 Database
- ModelMapper
- Apache POI (Excel)
- Lombok

## Features
- CRUD operations for User management
- Search and filter users
- Export to PDF using JasperReports
- Export to Excel using Apache POI
- Responsive UI with Vaadin
- Form validation
- Error handling

## Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- IDE (IntelliJ IDEA recommended)

## Project Structure
src/
├── main/
│   ├── java/
│   │   └── com/example/demo/
│   │       ├── config/                 # Configuration classes
│   │       │   └── AppConfig.java      # Application configuration
│   │       │
│   │       ├── constant/               # Constants and enums
│   │       │   ├── AppConstant.java    # Application constants
│   │       │   ├── BaseResponse.java   # Base response wrapper
│   │       │   ├── ErrorCode.java      # Error codes enum
│   │       │   └── UserStatus.java     # User status enum
│   │       │
|   |       ├── controller              # Controller
|   |       |   ├── UserController.java # User Controller
|   |       |   
│   │       ├── domain/                 # Domain objects
│   │       │   ├── dto/                # Data Transfer Objects
│   │       │   │   ├── request/        # Request DTOs
│   │       │   │   │   └── UserRequest.java
│   │       │   │   └── response/       # Response DTOs
│   │       │   │       └── UserResponse.java
│   │       │   │
│   │       │   └── entity/            # JPA Entities
│   │       │       └── user/
│   │       │           ├── User.java
│   │       │           └── UserRepository.java
│   │       │
│   │       ├── exception/              # Exception handling
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   └── UserException.java
│   │       │
│   │       ├── service/                # Business logic
│   │       │   ├── ReportService.java
│   │       │   ├── UserService.java
│   │       │   └── impl/
│   │       │       ├── ReportServiceImpl.java
│   │       │       └── UserServiceImpl.java
│   │       │
│   │       ├── ui/                     # Vaadin UI components
│   │       │   └── views/
│   │       │       ├── MainView.java
│   │       │       ├── UserListView.java
│   │       │       └── components/
│   │       │           └── UserForm.java
│   │       │
│   │       └── util/                   # Utilities
│   │           └── Helper.java
│   │
│   └── resources/
│       ├── application.properties      # Application configuration
│       ├── data.sql                    # Sample data
│       ├── schema.sql                  # Database schema
│       └── reports/                    # JasperReport templates
│           └── user_report.jrxml
│
└── test/
    └── java/
        └── com/example/demo/
            └── service/
                └── UserServiceTest.java

Saya akan tambahkan dokumentasi error codes:

```markdown
## API Documentation

### Base URL
```
http://localhost:8080/user/api/v1
```

### REST Endpoints (Controller)

#### User Management
| Method | URL          | Description           |
|--------|-----         |-------------          |
| GET    | /users       | Get all users         |
| GET    | /users/{id}  | Get user by ID        |
| POST   | /users       | Create new user       |
| PUT    | /users/{id}  | Update user           |
| DELETE | /users/{id}  | Delete user           |

### Service Layer Features

#### Report Service
Report generation diimplementasikan di service layer (`ReportService`) dan hanya diakses melalui Vaadin UI:
- `generateUserReport()`: Generate PDF report
- `generateUserExcelReport()`: Generate Excel report

Download report hanya tersedia melalui UI interface dengan memanfaatkan ReportService.

### Response Format

#### Success Response
```json
{
    "code": 200,
    "status": "Success",
    "message": "Success",
    "data": {
        // Response data
    }
}
```

#### Error Response
```json
{
    "code": 404,
    "status": "Error",
    "message": "User not found",
    "data": null
}
```

### Error Codes
| Code | Status | Message | Description                                |
|------|---------|---------|--------------------------------------------|
| 404 | Error | User not found | User dengan ID tersebut tidak ditemukan    |
| 409 | Error | Email already exists | Email sudah digunakan user lain            |
| 400 | Error | Validation error | Input tidak sesuai validasi                |
| 500 | Error | System error | Terjadi kesalahan sistem                   |

### Sample Request & Response

#### Create/Update User Request
```json
{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+1234567890",
    "status": "ACTIVE"
}
```

### Validation Rules
- firstName: 2-50 characters, letters only
- lastName: 2-50 characters, letters only
- email: Valid format, unique
- phoneNumber: 10-15 digits (optional)
- status: ACTIVE, INACTIVE, or BLOCKED
```

## Setup & Installation

1. Clone the repository:

git clone https://github.com/suntoryota/user-application
```

2. Navigate to project directory:

Open terminal/command prompt dan masuk ke folder project:

cd demo
```
Atau buka project menggunakan IDE (IntelliJ IDEA/Eclipse):

- File -> Open -> Pilih folder "demo"

3. Build the project:

Menggunakan terminal di root project:

mvn clean install
```
Atau menggunakan IDE:
- IntelliJ IDEA: Maven -> Lifecycle -> clean, install
- Eclipse: Run As -> Maven build -> Goals: clean install

4. Run the application:
   Menggunakan terminal:
```bash
mvn spring-boot:run
```
Atau menggunakan IDE:
- IntelliJ IDEA: Run DemoApplication.java
- Eclipse: Run As -> Spring Boot App

5. Access the application:
   Buka browser dan akses:
   `http://localhost:8080/usewr`

## Configuration Files

### application.properties
```properties
# Vaadin Configuration
vaadin.launch-browser=true
spring.application.name=demo

# Server Configuration
server.port=8080
server.servlet.context-path=/user

# Database Configuration
spring.datasource.url=jdbc:h2:file:./data/demo_db;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# H2 Console Configuration
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.h2.console.settings.web-allow-others=false

# JPA Configuration
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.format_sql=true

# Logging Configuration
logging.level.com.example.user=DEBUG
logging.level.org.springframework=INFO
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always

# Vaadin Configuration
vaadin.whitelisted-packages=com.example.user
vaadin.compatibilityMode=false
vaadin.pnpm.enable=true


### Database Configuration 

### Database Schema (schema.sql)
```sql
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(15),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_status ON users(status);
```

### Sample Data (data.sql)
```sql
INSERT INTO users (first_name, last_name, email, phone_number, status, created_at)
VALUES 
('John', 'Doe', 'john.doe@example.com', '+1234567890', 'ACTIVE', CURRENT_TIMESTAMP),
('Jane', 'Smith', 'jane.smith@example.com', '+1987654321', 'ACTIVE', CURRENT_TIMESTAMP),
('Bob', 'Johnson', 'bob.johnson@example.com', '+1122334455', 'INACTIVE', CURRENT_TIMESTAMP),
('Alice', 'Williams', 'alice.williams@example.com', '+1555666777', 'BLOCKED', CURRENT_TIMESTAMP),
('Charlie', 'Brown', 'charlie.brown@example.com', '+1999888777', 'ACTIVE', CURRENT_TIMESTAMP);
```

## Key Features Description

### User Management
- Create new users with validation
- View user details in a grid
- Update existing user information
- Delete users
- Search users by name or email
- Filter users by status

### Report Generation
- Export user data to PDF using JasperReports
- Export user data to Excel using Apache POI
- Download reports through browser

### Validation Rules
- First Name & Last Name: 2-50 characters, letters only
- Email: Valid email format, unique
- Phone: Optional, 10-15 digits
- Status: Must be ACTIVE, INACTIVE, or BLOCKED

### Error Handling
- Global exception handling
- User-friendly error messages
- Form validation feedback
- API response wrapping

## Frontend Features

### Main Layout
- Responsive navigation drawer
- Clean header with toggle menu
- Welcome page

### User List View
- Data grid with sorting and filtering
- Add/Edit user dialog
- Export buttons for reports
- Search field
- Refresh functionality

### User Form
- Input validation
- Status selection
- Save/Cancel buttons
- Delete confirmation

## Database Access
- H2 Console: `http://localhost:8080/user/h2-console`
- JDBC URL: `jdbc:h2:file:./data/demo_db`
- Username: `sa`
- Password: ` ` 

