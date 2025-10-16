package com.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Personal Finance Manager Application - Main Entry Point
 *
 * This is a comprehensive Spring Boot web application for managing personal finances,
 * including income tracking, expense management, budget planning, and savings goals.
 *
 * Key Features:
 * - User authentication and authorization with role-based access control
 * - Transaction management (income/expense tracking)
 * - Budget planning and monitoring
 * - Savings goal setting and progress tracking
 * - Financial reporting and analytics
 * - Responsive web interface with Bootstrap
 *
 * Technology Stack:
 * - Spring Boot 2.1.3 with Spring Security
 * - JPA/Hibernate for data persistence
 * - MySQL database (H2 for development)
 * - Thymeleaf templating engine
 * - Bootstrap 5 for responsive UI
 *
 * @author Personal Finance Manager Team
 * @version 1.0.0
 * @since 2024
 */
@SpringBootApplication
public class PersonalFinanceManagerApplication {

    /**
     * Main method to bootstrap the Spring Boot application.
     *
     * This method initializes the Spring application context, sets up auto-configuration,
     * and starts the embedded web server on the configured port (default: 8080).
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(PersonalFinanceManagerApplication.class, args);
    }
}