# MySQL Database Setup Guide for Personal Finance Manager

## Prerequisites

1. **MySQL Server installed and running**
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Or install via package manager:
     - Windows: Use MySQL Installer
     - macOS: `brew install mysql`
     - Ubuntu: `sudo apt install mysql-server`

2. **MySQL service running**
   - Windows: Check Services or run `net start mysql80` (or `net start mysql`)
   - macOS/Linux: `sudo systemctl start mysql` or `brew services start mysql`

## Windows-Specific Setup

### Check if MySQL is installed and running:
1. **Check MySQL Service:**
   ```cmd
   sc query mysql80
   ```
   Or check in Services (services.msc)

2. **Start MySQL Service if not running:**
   ```cmd
   net start mysql80
   ```

3. **Add MySQL to PATH (if needed):**
   - Find MySQL installation directory (usually `C:\Program Files\MySQL\MySQL Server 8.0\bin`)
   - Add to System PATH environment variable
   - Or use full path: `"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"`

## Step 1: Create Database

### Option A: Using MySQL Command Line
```bash
mysql -u root -p
```

Then run:
```sql
CREATE DATABASE IF NOT EXISTS personal_finance_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Verify database creation
SHOW DATABASES LIKE 'personal_finance_db';
```

### Option B: Using the provided script
```bash
mysql -u root -p < mysql-setup.sql
```

## Step 2: Update Application Configuration

The configuration has been updated in `application.properties`:

```properties
# MySQL Database (for production)
spring.datasource.url=jdbc:mysql://localhost:3306/personal_finance_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=minhkhuat123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Database Dialect
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

## Step 3: Common Issues and Solutions

### Issue 1: "Access denied for user 'root'@'localhost'"
**Solution:** Check your MySQL root password
```bash
# Reset MySQL root password if needed
sudo mysql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'your_password';
FLUSH PRIVILEGES;
```

### Issue 2: "Unknown database 'personal_finance_db'"
**Solution:** The `createDatabaseIfNotExist=true` parameter should handle this, but if not:
```sql
CREATE DATABASE personal_finance_db;
```

### Issue 3: "Connection refused"
**Solution:** Ensure MySQL service is running
```bash
# Check MySQL status
sudo systemctl status mysql
# Start MySQL if not running
sudo systemctl start mysql
```

### Issue 4: Time zone issues
**Solution:** Already handled with `serverTimezone=UTC` parameter

## Step 4: Test the Connection

1. **Clean and rebuild the project:**
```bash
mvn clean compile
```

2. **Run the application:**
```bash
mvn spring-boot:run
```

3. **Check logs for successful connection:**
Look for logs showing table creation and successful startup.

## Security Recommendations (Optional)

For production, create a dedicated database user:

```sql
CREATE USER 'finance_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON personal_finance_db.* TO 'finance_user'@'localhost';
FLUSH PRIVILEGES;
```

Then update `application.properties`:
```properties
spring.datasource.username=finance_user
spring.datasource.password=secure_password
```

## Troubleshooting

If you still encounter issues, check:
1. MySQL server is running: `sudo systemctl status mysql`
2. Port 3306 is not blocked by firewall
3. MySQL root password is correct
4. Database exists: `SHOW DATABASES;`

Run the application and share any error messages for further assistance.
