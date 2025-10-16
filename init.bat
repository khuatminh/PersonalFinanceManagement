@echo off
REM ========================================
REM Personal Finance Manager - Initialization Script
REM ========================================
REM This script initializes and runs the Personal Finance Manager application
REM Author: Generated for Personal Finance Manager Project
REM Version: 1.0.0
REM ========================================

echo.
echo ========================================
echo Personal Finance Manager - Initialization
echo ========================================
echo.

REM Check if Java is installed
echo [1/6] Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 8 or higher and add it to your PATH
    pause
    exit /b 1
)
echo ✓ Java is installed

REM Check if Maven is installed
echo.
echo [2/6] Checking Maven installation...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven 3.6 or higher and add it to your PATH
    pause
    exit /b 1
)
echo ✓ Maven is installed

REM Clean and compile the project
echo.
echo [3/6] Cleaning and compiling the project...
call mvn clean compile
if %errorlevel% neq 0 (
    echo ERROR: Failed to compile the project
    echo Please check the error messages above
    pause
    exit /b 1
)
echo ✓ Project compiled successfully

REM Package the application
echo.
echo [4/6] Packaging the application...
call mvn package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Failed to package the application
    echo Please check the error messages above
    pause
    exit /b 1
)
echo ✓ Application packaged successfully

REM Display configuration information
echo.
echo [5/6] Application Configuration:
echo ========================================
echo Application Name: Personal Finance Manager
echo Version: 1.0.0
echo Port: 8088
echo Database: H2 (Development Mode)
echo H2 Console: http://localhost:8088/h2-console
echo Main Application: http://localhost:8088
echo ========================================
echo.
echo Default Admin Account:
echo Username: admin
echo Password: admin123
echo ========================================

REM Start the application
echo.
echo [6/6] Starting the Personal Finance Manager...
echo.
echo The application will start in a few seconds...
echo You can access it at: http://localhost:8088
echo.
echo Press Ctrl+C to stop the application
echo.

REM Run the Spring Boot application
call mvn spring-boot:run

echo.
echo Application has been stopped.
pause
