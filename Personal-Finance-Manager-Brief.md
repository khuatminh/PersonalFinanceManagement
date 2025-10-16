# Personal Finance Manager - Development Project Brief

## Project Overview
Create a Personal Finance Manager web application following the same architecture and tech stack as PTITShop. This will be a comprehensive financial management system where users can track income, expenses, budgets, savings goals, and generate financial reports.

## Tech Stack Requirements
- **Backend**: Spring Boot 2.1.3.RELEASE
- **Build Tool**: Maven
- **Database**: MySQL (primary) with H2 for testing
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security with BCrypt password encryption
- **Frontend**: Thymeleaf templates with Spring MVC
- **Java Version**: 1.8
- **Development**: Spring DevTools for hot reloading

## Core Features

### 1. User Management & Authentication
- User registration and login system
- Role-based access control (USER, ADMIN)
- Profile management with password encryption
- Session management and security

### 2. Financial Transaction Management
- **Income Tracking**: Salary, freelance work, investments, other income sources
- **Expense Tracking**: Categorized expenses (food, transportation, utilities, entertainment, etc.)
- **Transaction Categories**: Hierarchical category system with customizable categories
- **Recurring Transactions**: Automatic recurring income/expenses (monthly bills, subscriptions)
- **Transaction Search & Filter**: By date range, category, amount, type

### 3. Budget Management
- **Budget Creation**: Set monthly/weekly budgets by category
- **Budget Tracking**: Real-time budget vs actual spending comparison
- **Budget Alerts**: Notifications when approaching or exceeding budget limits
- **Budget Reports**: Visual budget performance analytics

### 4. Savings Goals
- **Goal Setting**: Define financial goals (vacation, emergency fund, down payment)
- **Goal Tracking**: Progress monitoring with target dates
- **Automatic Savings**: Allocate portions of income to goals
- **Goal Achievement**: Celebrate milestones and achievements

### 5. Financial Dashboard & Analytics
- **Overview Dashboard**: Key financial metrics at a glance
- **Spending Trends**: Visual charts showing spending patterns over time
- **Income vs Expense Analysis**: Monthly comparisons and trends
- **Category Breakdown**: Pie charts showing expense distribution
- **Financial Reports**: Exportable monthly/annual reports

### 6. Bill Management
- **Bill Tracking**: Manage recurring bills and due dates
- **Payment Reminders**: Notifications for upcoming payments
- **Payment History**: Track bill payment status
- **Late Payment Alerts**: Warning system for missed payments

## Database Schema Design

### Core Tables
- `user` - User accounts and authentication
- `transaction` - All financial transactions (income/expenses)
- `category` - Transaction categories (food, transport, utilities, etc.)
- `budget` - Budget settings by category and period
- `savings_goal` - Financial savings goals
- `goal_contribution` - Contributions towards savings goals
- `bill` - Recurring bill management
- `payment_reminder` - Bill payment reminders
- `role` - User roles for authorization

### Key Entity Relationships
- User → Transactions (one-to-many)
- User → Budgets (one-to-many)
- User → Savings Goals (one-to-many)
- Category → Transactions (one-to-many)
- Transaction → Category (many-to-one)

## Package Structure
```
com.finance.manager/
├── config/          # Security and configuration
├── controller/      # Web endpoints and REST controllers
├── domain/          # JPA entities and domain models
├── repository/      # Spring Data JPA repositories
├── service/         # Business logic layer
├── form/           # Form backing objects
└── utility/        # Security and utility classes
```

## UI/UX Requirements

### Modern Design Principles
- **Clean, Minimalist Interface**: Card-based layouts with ample whitespace
- **Responsive Design**: Mobile-first approach with Bootstrap 4/5
- **Color Scheme**: Professional finance-themed colors (blues, greens, grays)
- **Typography**: Clear, readable fonts with proper hierarchy
- **Micro-interactions**: Smooth transitions and hover effects

### Key UI Components
- **Dashboard**: Widget-based layout with charts and quick stats
- **Transaction Forms**: Intuitive input forms with validation
- **Data Visualization**: Chart.js or similar for financial charts
- **Navigation**: Clear sidebar or top navigation with active states
- **Modals**: For quick actions like adding transactions
- **Tables**: Sortable, filterable data tables for transactions

### User Experience Flow
1. **Onboarding**: Guided setup for new users
2. **Quick Actions**: Easy access to common tasks from dashboard
3. **Progressive Disclosure**: Show advanced features as needed
4. **Feedback**: Clear success/error messages and confirmations
5. **Accessibility**: WCAG compliant design

## Security Requirements
- **Authentication**: Secure login with session management
- **Authorization**: Role-based access control
- **Data Protection**: HTTPS, CSRF protection, XSS prevention
- **Input Validation**: Comprehensive form validation
- **Password Security**: BCrypt encryption with strong password policies

## Development Guidelines

### Code Quality Standards
- **Clean Code**: Simple, readable, and maintainable code
- **Comments**: Clear documentation for complex business logic
- **Naming Conventions**: Consistent and descriptive naming
- **Error Handling**: Proper exception handling and user feedback
- **Testing**: Unit tests for business logic, integration tests for controllers

### Performance Considerations
- **Database Optimization**: Proper indexing and query optimization
- **Caching**: Appropriate caching for frequently accessed data
- **Lazy Loading**: Efficient JPA entity loading
- **Pagination**: For large datasets (transaction history)

## Project Structure
```
finance-manager/
├── pom.xml                    # Maven configuration
├── README.md                  # Project documentation
├── SQL_FINANCE_MANAGER.sql    # Database schema and initial data
└── src/
    └── main/
        ├── java/com/finance/manager/
        │   └── FinanceManagerApplication.java
        └── resources/
            ├── application.properties    # Database and app configuration
            ├── static/                   # CSS, JS, images
            └── templates/                # Thymeleaf templates
```

## Success Metrics
- **Functionality**: All core features working as specified
- **Usability**: Intuitive interface requiring minimal training
- **Performance**: Fast page loads and responsive interactions
- **Security**: Robust authentication and data protection
- **Maintainability**: Clean, well-documented codebase

## Deliverables
1. Complete Spring Boot application with all features
2. MySQL database schema with sample data
3. Comprehensive documentation (README, API docs)
4. Deployment instructions and configuration guide
5. Testing suite with unit and integration tests

This project should demonstrate proficiency in Spring Boot, JPA, Spring Security, and modern web development practices while maintaining the simplicity and readability.