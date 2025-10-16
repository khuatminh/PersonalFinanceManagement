-- Fix database schema issues for user registration and login
USE personal_finance_db;

-- First, check if the old 'role' column exists and drop it
SET @column_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'personal_finance_db' 
    AND TABLE_NAME = 'users' 
    AND COLUMN_NAME = 'role'
);

-- Drop the old role column if it exists
SET @sql = IF(@column_exists > 0, 'ALTER TABLE users DROP COLUMN role', 'SELECT "Column role does not exist"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Ensure role_id column exists and is properly configured
ALTER TABLE users 
MODIFY COLUMN role_id BIGINT NULL;

-- Update existing users without roles to have USER role
UPDATE users 
SET role_id = (SELECT id FROM roles WHERE name = 'USER') 
WHERE role_id IS NULL;

-- Now make role_id NOT NULL since all users should have a role
ALTER TABLE users 
MODIFY COLUMN role_id BIGINT NOT NULL;

-- Ensure foreign key constraint exists
SET @fk_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
    WHERE TABLE_SCHEMA = 'personal_finance_db' 
    AND TABLE_NAME = 'users' 
    AND CONSTRAINT_NAME = 'FKp56c1712k691lhsyewcssf40f'
);

-- Add foreign key constraint if it doesn't exist
SET @sql = IF(@fk_exists = 0, 
    'ALTER TABLE users ADD CONSTRAINT FKp56c1712k691lhsyewcssf40f FOREIGN KEY (role_id) REFERENCES roles (id)', 
    'SELECT "Foreign key constraint already exists"'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Show final table structure
DESCRIBE users;
SELECT 'Database schema fixed successfully!' as Status;
