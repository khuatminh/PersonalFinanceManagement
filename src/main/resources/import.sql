-- This file contains sample data for the Personal Finance Manager application.
-- You can execute this script manually to populate the database for testing.

-- Roles
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('USER');

-- Users
-- Password for 'admin' is 'admin123'
-- Password for 'user' is 'user123'
INSERT INTO users (username, email, password, role_id, created_at) VALUES ('admin', 'admin@finance.com', '$2a$10$U//GCqqO03bbLNjbpTd9GO.RrZheV.L/F94/Qs6IV4/0iXWibk1ay', 1, NOW());
INSERT INTO users (username, email, password, role_id, created_at) VALUES ('user', 'user@finance.com', '$2a$10$GHfreUVp5WiL6pOguSJoweB2JQaZeDSK2v8Q2/WbDMEroZ5a4AhhW', 2, NOW());

-- Categories
INSERT INTO categories (name, description, type, color) VALUES ('Luong', 'Thu nhap thuong xuyen tu cong viec', 'INCOME', '#28a745');
INSERT INTO categories (name, description, type, color) VALUES ('Lam them', 'Thu nhap tu cong viec tu do', 'INCOME', '#20c997');
INSERT INTO categories (name, description, type, color) VALUES ('Dau tu', 'Thu nhap tu cac khoan dau tu', 'INCOME', '#6f42c1');
INSERT INTO categories (name, description, type, color) VALUES ('Qua tang', 'Thu nhap tu qua tang', 'INCOME', '#e83e8c');
INSERT INTO categories (name, description, type, color) VALUES ('Thu nhap khac', 'Cac nguon thu nhap khac', 'INCOME', '#6c757d');
INSERT INTO categories (name, description, type, color) VALUES ('An uong', 'Thuc pham, nha hang, v.v.', 'EXPENSE', '#fd7e14');
INSERT INTO categories (name, description, type, color) VALUES ('Di lai', 'Xang, phuong tien cong cong, v.v.', 'EXPENSE', '#007bff');
INSERT INTO categories (name, description, type, color) VALUES ('Mua sam', 'Quan ao, do dien tu, v.v.', 'EXPENSE', '#dc3545');
INSERT INTO categories (name, description, type, color) VALUES ('Giai tri', 'Phim anh, buoi hoa nhac, v.v.', 'EXPENSE', '#6610f2');
INSERT INTO categories (name, description, type, color) VALUES ('Hoa don & Tien ich', 'Tien thue nha, dien, internet, v.v.', 'EXPENSE', '#17a2b8');
INSERT INTO categories (name, description, type, color) VALUES ('Cham soc suc khoe', 'Kham bac si, thuoc men, v.v.', 'EXPENSE', '#20c997');
INSERT INTO categories (name, description, type, color) VALUES ('Giao duc', 'Hoc phi, sach vo, v.v.', 'EXPENSE', '#6f42c1');
INSERT INTO categories (name, description, type, color) VALUES ('Tien thue nha/Tra gop', 'Chi phi nha o', 'EXPENSE', '#e83e8c');
INSERT INTO categories (name, description, type, color) VALUES ('Tiet kiem', 'Dong gop vao cac khoan tiet kiem', 'EXPENSE', '#28a745');
INSERT INTO categories (name, description, type, color) VALUES ('Chi phi khac', 'Cac chi phi linh tinh khac', 'EXPENSE', '#6c757d');

-- Transactions for admin (user_id = 1)
INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date, created_at) VALUES (1, 1, 25000000, 'INCOME', 'Luong thang 9', '2025-09-20 10:00:00', NOW());
INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date, created_at) VALUES (1, 2, 5000000, 'INCOME', 'Du an tu do', '2025-09-30 15:30:00', NOW());
INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date, created_at) VALUES (1, 6, 1500000, 'EXPENSE', 'Mua sam thuc pham', '2025-10-13 12:00:00', NOW());
INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date, created_at) VALUES (1, 6, 850000, 'EXPENSE', 'An toi nha hang', '2025-10-10 20:00:00', NOW());
INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date, created_at) VALUES (1, 7, 650000, 'EXPENSE', 'Do xang', '2025-10-12 08:00:00', NOW());
INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date, created_at) VALUES (1, 9, 280000, 'EXPENSE', 'Ve xem phim', '2025-10-05 19:00:00', NOW());
INSERT INTO transactions (user_id, category_id, amount, type, description, transaction_date, created_at) VALUES (1, 10, 599000, 'EXPENSE', 'Hoa don Internet', '2025-10-03 11:00:00', NOW());

-- Budgets for admin (user_id = 1)
INSERT INTO budgets (user_id, category_id, name, amount, start_date, end_date, created_at) VALUES (1, 6, 'Ngan sach an uong hang thang', 7500000, '2025-10-01', '2025-10-31', NOW());
INSERT INTO budgets (user_id, category_id, name, amount, start_date, end_date, created_at) VALUES (1, 7, 'Ngan sach di lai', 2000000, '2025-10-01', '2025-10-31', NOW());
INSERT INTO budgets (user_id, category_id, name, amount, start_date, end_date, created_at) VALUES (1, NULL, 'Tong chi tieu', 20000000, '2025-10-01', '2025-10-31', NOW());

-- Goals for admin (user_id = 1)
INSERT INTO goals (user_id, name, target_amount, current_amount, target_date, status, created_at) VALUES (1, 'Quy mua Laptop moi', 15000000, 3500000, '2026-06-01', 'ACTIVE', NOW());
INSERT INTO goals (user_id, name, target_amount, current_amount, target_date, status, created_at) VALUES (1, 'Du lich Da Nang', 40000000, 12000000, '2026-12-20', 'ACTIVE', NOW());
