//package com.finance.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DatabaseMigration implements CommandLineRunner {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Override
//    public void run(String... args) throws Exception {
//        try {
//            // Check if old 'role' column exists and drop it
//            String checkColumnSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
//                    "WHERE TABLE_SCHEMA = 'personal_finance_db' " +
//                    "AND TABLE_NAME = 'users' " +
//                    "AND COLUMN_NAME = 'role'";
//
//            Integer columnExists = jdbcTemplate.queryForObject(checkColumnSql, Integer.class);
//
//            if (columnExists != null && columnExists > 0) {
//                System.out.println("Dropping old 'role' column from users table...");
//                jdbcTemplate.execute("ALTER TABLE users DROP COLUMN role");
//                System.out.println("Old 'role' column dropped successfully.");
//            }
//
//            // Ensure role_id column is properly configured
//            jdbcTemplate.execute("ALTER TABLE users MODIFY COLUMN role_id BIGINT NULL");
//
//            // Update existing users without roles to have appropriate roles
//            // First, assign ADMIN role to admin user
//            String updateAdminSql = "UPDATE users SET role_id = (SELECT id FROM roles WHERE name = 'ADMIN') WHERE username = 'admin' AND role_id IS NULL";
//            int adminUpdated = jdbcTemplate.update(updateAdminSql);
//            if (adminUpdated > 0) {
//                System.out.println("Updated admin user with ADMIN role.");
//            }
//
//            // Then assign USER role to remaining users without roles
//            String updateUsersSql = "UPDATE users SET role_id = (SELECT id FROM roles WHERE name = 'USER') WHERE role_id IS NULL";
//            int updatedRows = jdbcTemplate.update(updateUsersSql);
//            if (updatedRows > 0) {
//                System.out.println("Updated " + updatedRows + " users with default USER role.");
//            }
//
//            // Fix existing admin user if it has wrong role
//            String fixAdminRoleSql = "UPDATE users SET role_id = (SELECT id FROM roles WHERE name = 'ADMIN') WHERE username = 'admin' AND role_id = (SELECT id FROM roles WHERE name = 'USER')";
//            int adminFixed = jdbcTemplate.update(fixAdminRoleSql);
//            if (adminFixed > 0) {
//                System.out.println("Fixed admin user role from USER to ADMIN.");
//            }
//
//            // Make role_id NOT NULL
//            jdbcTemplate.execute("ALTER TABLE users MODIFY COLUMN role_id BIGINT NOT NULL");
//
//            System.out.println("Database migration completed successfully!");
//
//        } catch (Exception e) {
//            System.err.println("Database migration failed: " + e.getMessage());
//            // Don't throw exception to prevent application startup failure
//        }
//    }
//}
