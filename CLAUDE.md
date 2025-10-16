# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 2.1.3 web application for personal finance management using Java 8, Spring Data JPA, and Thymeleaf. The application demonstrates OOP principles with a layered architecture including domain entities, repositories, services, and controllers.

## Development Commands

### Build and Run
```bash
# Clean compile
mvn clean compile

# Run the application
mvn spring-boot:run

# Build JAR for deployment
mvn clean package

# Run tests
mvn test

# Target directory: target/personal-finance-manager-1.0.0.jar
```

### Database Configuration
The application supports two database configurations:

**Development (H2):**
- Uncomment H2 configuration in `src/main/resources/application.properties`
- Access H2 console at http://localhost:8080/h2-console
- Database URL: `jdbc:h2:mem:testdb`

**Production (MySQL):**
- Default configuration uses MySQL
- Database: `personal_finance_db`
- Update credentials in `application.properties`

### Access Points
- Application URL: http://localhost:8080
- Default admin account: admin / admin123

## Architecture

### Package Structure
```
com.finance/
├── PersonalFinanceManagerApplication.java  # Main application class
├── config/                                 # Configuration classes
│   ├── SecurityConfig.java                # Spring Security configuration
│   └── DataInitializer.java               # Sample data initialization
├── controller/                            # Web controllers
│   ├── HomeController.java                # Home and public pages
│   ├── DashboardController.java           # Main dashboard
│   ├── UserController.java                # User management
│   └── TransactionController.java         # Transaction CRUD
├── domain/                               # JPA entities
│   ├── User.java                         # User entity with roles
│   ├── Transaction.java                  # Financial transactions
│   ├── Category.java                     # Transaction categories
│   ├── Budget.java                       # Budget planning
│   └── Goal.java                         # Savings goals
├── repository/                           # Spring Data JPA repositories
├── service/                              # Business logic layer
└── form/                                 # DTOs and form backing objects
```

### Key Entity Relationships
- Users have many transactions, budgets, and goals
- Transactions belong to categories and users
- Categories can be INCOME or EXPENSE types
- Budgets are associated with users and categories
- Goals have progress tracking with target amounts

### Security Configuration
- Form-based authentication with Spring Security
- Role-based access: USER and ADMIN roles
- Public endpoints: `/`, `/login`, `/register`, static resources
- Authenticated endpoints: `/dashboard`, `/transactions/**`, `/budgets/**`, `/goals/**`
- Admin endpoints: `/admin/**`
- Password encryption with BCrypt

### Data Layer Patterns
- Repository pattern using Spring Data JPA
- Service layer with `@Transactional` methods
- Complex queries in repositories for financial statistics
- DTO pattern for form handling and data transfer

## Key Services and Methods

### TransactionService
- Comprehensive transaction CRUD operations
- Financial calculations (income, expenses, balance)
- Date range queries and statistical reporting
- Transaction statistics with nested `TransactionStatistics` class

### UserService
- User registration and authentication
- Password changes and profile management
- Integration with Spring Security's `UserDetailsService`

### BudgetService
- Budget creation and tracking
- Progress calculation with `BudgetProgress` DTO
- Category-wise budget monitoring

### GoalService
- Savings goal management
- Progress tracking with `GoalSummary` DTO
- Status management (ACTIVE, COMPLETED, PAUSED)

## Frontend Architecture

### Template Structure
- Uses Thymeleaf templating engine
- Layout inheritance with `fragments/layout.html`
- Bootstrap 5 for responsive styling
- JavaScript for interactive features

### Key Templates
- `dashboard.html` - Financial overview with statistics
- `login.html` - User authentication
- Transaction management forms
- User profile and settings pages

## Database Schema

### Core Tables
- `users` - User accounts with role-based access
- `categories` - Transaction categories (INCOME/EXPENSE)
- `transactions` - Financial transaction records
- `budgets` - Budget planning by category
- `goals` - Savings goals with progress tracking

### Important Fields
- Financial amounts use `BigDecimal` with precision (19,2)
- Dates use `LocalDateTime` for transaction timestamps
- Enum types for transaction types, categories, and goal statuses
- Foreign key relationships maintain data integrity

## Development Notes

### JPA Configuration
- `ddl-auto: update` for development schema management
- SQL logging enabled for debugging
- H2 dialect for development, MySQL for production

### Testing Approach
- Manual testing checklist documented in README
- Spring Boot test starter available for unit testing
- Test coverage focuses on service layer business logic

### Security Considerations
- All password operations use BCrypt encoding
- CSRF protection enabled (except H2 console)
- Session timeout configured to 30 minutes
- Input validation on form backing objects

## Common Development Tasks

### Adding New Transaction Categories
1. Add entries in `DataInitializer.java` for sample data
2. Categories are automatically created via JPA entity lifecycle
3. Use `CategoryType.INCOME` or `CategoryType.EXPENSE` enum

### Financial Calculations
- Use `TransactionService` methods for statistical operations
- Leverage `BigDecimal` for all monetary calculations
- Consider date range parameters for period-specific reports

### Security Extensions
- Modify `SecurityConfig.java` for endpoint access rules
- Create new roles and update `User.Role` enum
- Implement custom authentication providers if needed