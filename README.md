# Personal Finance Manager

A comprehensive web-based personal finance management system built with Spring Boot 3.x that enables users to track income, expenses, savings goals, and generate detailed financial reports.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Prerequisites](#prerequisites)
- [Installation and Setup](#installation-and-setup)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Deployment](#deployment)
- [Design Decisions](#design-decisions)

## Features

### User Management
- **User Registration**: Create new accounts with secure password handling
- **User Login**: Authenticate with email and password credentials
- **Session Management**: Secure session-based authentication with cookies
- **Data Isolation**: Complete data segregation between user accounts
- **Logout**: Proper session invalidation

### Transaction Management
- **Create Transactions**: Add income and expense transactions with categorization
- **View Transactions**: List all transactions sorted by date (newest first)
- **Filter Transactions**: Filter by date range, category, and transaction type
- **Update Transactions**: Modify transaction details (except date)
- **Delete Transactions**: Remove transactions from records

### Category Management
- **Default Categories**:
  - Income: Salary
  - Expenses: Food, Rent, Transportation, Entertainment, Healthcare, Utilities
- **Custom Categories**: Create user-specific income and expense categories
- **Category Validation**: Ensure all transactions reference valid categories
- **Unique Names**: Category names must be unique per user

### Savings Goals
- **Create Goals**: Set financial targets with name, amount, and dates
- **Progress Tracking**: Monitor progress based on income minus expenses since goal start date
- **Update Goals**: Modify target amount and dates
- **View Goals**: Display all goals with progress percentage and remaining amounts
- **Delete Goals**: Remove goals as needed

### Financial Reports
- **Monthly Reports**: Analyze spending patterns for specific months
  - Income by category
  - Expenses by category
  - Net savings calculation
- **Yearly Reports**: Aggregate data for entire years with comprehensive overview

## Technology Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.1 |
| Security | Spring Security with BCrypt |
| Database | H2 (in-memory for development) |
| ORM | Spring Data JPA / Hibernate |
| Build Tool | Maven |
| Testing | JUnit 5, Mockito |
| Code Coverage | JaCoCo |

## System Architecture

```
┌─────────────────────────────────────────────────┐
│           REST API Controllers                   │
│  (Auth, Transaction, Category, Goal, Report)    │
└────────────────┬────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────┐
│           Service Layer                          │
│  (Authentication, Transaction, Category,        │
│   SavingsGoal, Report Services)                 │
└────────────────┬────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────┐
│       Repository Layer (JPA)                    │
│  (User, Category, Transaction, SavingsGoal)    │
└────────────────┬────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────┐
│          H2 Database                            │
└─────────────────────────────────────────────────┘
```

### Layered Architecture
- **Controllers**: Handle HTTP requests/responses and validation
- **Services**: Implement business logic and data processing
- **Repositories**: Manage database operations using Spring Data JPA
- **Entities**: Define domain models
- **DTOs**: Separate request/response objects from entities

## Prerequisites

- Java Development Kit (JDK) 17 or higher
- Maven 3.6 or higher
- Git

## Installation and Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd finance-manager
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`. API endpoints are available at the application root (for example `http://localhost:8080/auth/login`).

### 4. Access H2 Console (Optional)

```
http://localhost:8080/h2-console
```

**JDBC URL**: `jdbc:h2:mem:financedb`  
**Username**: `sa`  
**Password**: (leave blank)

## API Documentation

### Authentication Endpoints

#### Register User
```
POST /auth/register
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phoneNumber": "+1234567890"
}

Response: 201 Created
{
  "message": "User registered successfully",
  "userId": 1
}
```

#### Login
```
POST /auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}

Response: 200 OK
{
  "message": "Login successful"
}
```

#### Logout
```
POST /auth/logout

Response: 200 OK
{
  "message": "Logout successful"
}
```

### Transaction Endpoints

#### Create Transaction
```
POST /transactions
Content-Type: application/json

{
  "amount": 50000.00,
  "date": "2024-01-15",
  "category": "Salary",
  "description": "January Salary"
}

Response: 201 Created
{
  "id": 1,
  "amount": 50000.00,
  "date": "2024-01-15",
  "category": "Salary",
  "description": "January Salary",
  "type": "INCOME"
}
```

#### Get Transactions
```
GET /transactions?startDate=2024-01-01&endDate=2024-01-31&categoryId=1

Response: 200 OK
{
  "transactions": [
    {
      "id": 1,
      "amount": 50000.00,
      "date": "2024-01-15",
      "category": "Salary",
      "description": "January Salary",
      "type": "INCOME"
    }
  ]
}
```

#### Update Transaction
```
PUT /api/transactions/{id}
Content-Type: application/json

{
  "amount": 60000.00,
  "description": "Updated January Salary"
}

Response: 200 OK
{
  "id": 1,
  "amount": 60000.00,
  "date": "2024-01-15",
  "category": "Salary",
  "description": "Updated January Salary",
  "type": "INCOME"
}
```

#### Delete Transaction
```
DELETE /api/transactions/{id}

Response: 200 OK
{
  "message": "Transaction deleted successfully"
}
```

### Category Endpoints

#### Get All Categories
```
GET /categories

Response: 200 OK
{
  "categories": [
    {
      "name": "Salary",
      "type": "INCOME",
      "isCustom": false
    },
    {
      "name": "Food",
      "type": "EXPENSE",
      "isCustom": false
    }
  ]
}
```

#### Create Custom Category
```
POST /categories
Content-Type: application/json

{
  "name": "SideBusinessIncome",
  "type": "INCOME"
}

Response: 201 Created
{
  "name": "SideBusinessIncome",
  "type": "INCOME",
  "isCustom": true
}
```

#### Delete Custom Category
```
DELETE /categories/{name}

Response: 200 OK
{
  "message": "Category deleted successfully"
}
```

### Savings Goals Endpoints

#### Create Goal
```
POST /goals
Content-Type: application/json

{
  "goalName": "Emergency Fund",
  "targetAmount": 5000.00,
  "targetDate": "2026-01-01",
  "startDate": "2025-01-01"
}

Response: 201 Created
{
  "id": 1,
  "goalName": "Emergency Fund",
  "targetAmount": 5000.00,
  "targetDate": "2026-01-01",
  "startDate": "2025-01-01",
  "currentProgress": 1000.00,
  "progressPercentage": 20.0,
  "remainingAmount": 4000.00
}
```

#### Get All Goals
```
GET /goals

Response: 200 OK
{
  "goals": [
    {
      "id": 1,
      "goalName": "Emergency Fund",
      "targetAmount": 5000.00,
      "targetDate": "2026-01-01",
      "startDate": "2025-01-01",
      "currentProgress": 1000.00,
      "progressPercentage": 20.0,
      "remainingAmount": 4000.00
    }
  ]
}
```

#### Get Single Goal
```
GET /goals/{id}

Response: 200 OK
{
  "id": 1,
  "goalName": "Emergency Fund",
  "targetAmount": 5000.00,
  "targetDate": "2026-01-01",
  "startDate": "2025-01-01",
  "currentProgress": 1000.00,
  "progressPercentage": 20.0,
  "remainingAmount": 4000.00
}
```

#### Update Goal
```
PUT /goals/{id}
Content-Type: application/json

{
  "targetAmount": 6000.00,
  "targetDate": "2026-02-01"
}

Response: 200 OK
{
  "id": 1,
  "goalName": "Emergency Fund",
  "targetAmount": 6000.00,
  "targetDate": "2026-02-01",
  "startDate": "2025-01-01",
  "currentProgress": 1000.00,
  "progressPercentage": 16.67,
  "remainingAmount": 5000.00
}
```

#### Delete Goal
```
DELETE /api/goals/{id}

Response: 200 OK
{
  "message": "Goal deleted successfully"
}
```

### Report Endpoints

#### Monthly Report
```
GET /api/reports/monthly/{year}/{month}

Example: GET /api/reports/monthly/2024/1

Response: 200 OK
{
  "month": 1,
  "year": 2024,
  "totalIncome": {
    "Salary": 3000.00,
    "Freelance": 500.00
  },
  "totalExpenses": {
    "Food": 400.00,
    "Rent": 1200.00,
    "Transportation": 200.00
  },
  "netSavings": 1700.00
}
```

#### Yearly Report
```
GET /api/reports/yearly/{year}

Example: GET /api/reports/yearly/2024

Response: 200 OK
{
  "year": 2024,
  "totalIncome": {
    "Salary": 36000.00,
    "Freelance": 6000.00
  },
  "totalExpenses": {
    "Food": 4800.00,
    "Rent": 14400.00,
    "Transportation": 2400.00
  },
  "netSavings": 20400.00
}
```

### Error Responses

| Status Code | Description | Example |
|------------|-------------|---------|
| 400 | Bad Request | Invalid input validation |
| 401 | Unauthorized | Invalid credentials, expired session |
| 403 | Forbidden | Accessing other user's data |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Duplicate category names |
| 500 | Internal Server Error | Unexpected errors |

## Testing

### Run All Tests
```bash
mvn test
```

### Run Tests with Coverage Report
```bash
mvn clean test jacoco:report
```

Coverage report will be generated at: `target/site/jacoco/index.html`

### Test Coverage
- Unit Tests: **5+ test classes**
- Integration Tests: Controller tests
- Minimum Coverage: **80%**

### Test Classes
1. `AuthenticationServiceTest` - Authentication logic
2. `TransactionServiceTest` - Transaction operations
3. `CategoryServiceTest` - Category management
4. `SavingsGoalServiceTest` - Goal management
5. `ReportServiceTest` - Report generation
6. `AuthControllerTest` - API endpoint validation

## Deployment

### Deploy to Render

1. **Create GitHub Repository**
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git remote add origin <your-repo-url>
   git push -u origin main
   ```

2. **Create Render Account**
   - Go to [render.com](https://render.com)
   - Sign up/login with GitHub

3. **Create New Service**
   - Click "New +"
   - Select "Web Service"
   - Connect your GitHub repository
   - Configure settings:
     - **Name**: finance-manager
     - **Environment**: Java
     - **Build Command**: `mvn clean install`
     - **Start Command**: `java -jar target/finance-manager-1.0.0.jar`
     - **Region**: Choose closest region

4. **Deploy**
   - Click "Create Web Service"
   - Render will automatically deploy when you push to main

5. **Test Deployment**
   ```bash
   bash financial_manager_tests.sh https://your-render-url/api
   ```

## Design Decisions

### 1. Layered Architecture
- **Rationale**: Separation of concerns, easier testing, better maintainability
- **Implementation**: Controllers → Services → Repositories → Database

### 2. DTOs for Request/Response
- **Rationale**: Decouple API contracts from entity models, add validation layer
- **Implementation**: Separate RequestDTOs and ResponseDTOs

### 3. Global Exception Handler
- **Rationale**: Consistent error responses, centralized error handling
- **Implementation**: `@ControllerAdvice` with specific exception handlers

### 4. Session-Based Authentication
- **Rationale**: Stateful authentication suitable for web applications
- **Implementation**: Spring Security with session management

### 5. H2 Database for Development
- **Rationale**: No setup required, in-memory storage, easy testing
- **Implementation**: JPA with Hibernate ORM

### 6. Data Isolation
- **Rationale**: Security compliance, multi-tenant support
- **Implementation**: User field in all entities, validation in services

### 7. Goal Progress Calculation
- **Rationale**: Real-time progress based on transactions
- **Implementation**: Calculated from income minus expenses since goal start date

### 8. Transaction Date Validation
- **Rationale**: Prevent future transactions, logical consistency
- **Implementation**: Validation in service layer

## Configuration

### Application Properties
Default configuration in `application.yml`:

```yaml
server:
  servlet:
    context-path: /api
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:financedb
  jpa:
    hibernate:
      ddl-auto: create-drop
```

### Custom Configuration
- Modify `application.yml` for different settings
- Environment variables can override properties

## Security Considerations

1. **Password Encoding**: BCrypt encryption
2. **Session Security**: HttpOnly, Secure cookies
3. **CSRF Protection**: Enabled by default
4. **Data Isolation**: User-based filtering on all queries
5. **Input Validation**: Comprehensive validation on all endpoints
6. **Authentication**: Session-based with Spring Security

## Troubleshooting

### Issue: Port 8080 already in use
**Solution**: Change port in `application.yml`
```yaml
server:
  port: 8081
```

### Issue: Database connection error
**Solution**: Ensure H2 is included in pom.xml and DDL auto is set correctly

### Issue: Authentication fails
**Solution**: Ensure user is registered and credentials are correct

## JavaDoc

- Generate API JavaDoc for public classes and methods with Maven:

```bash
mvn javadoc:javadoc
```

The generated documentation will be available in `target/site/apidocs/index.html`.

Public controllers, services, and repositories include JavaDoc comments in-source. To view interactive API docs, open the Swagger UI at `/api/swagger-ui/index.html`.
