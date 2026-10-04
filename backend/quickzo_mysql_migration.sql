-- ==============================================================
-- Quickzo Database Migration Script: H2 to MySQL
-- Database: quickzo
-- ==============================================================

-- 1. Create database if it does not exist
CREATE DATABASE IF NOT EXISTS quickzo
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE quickzo;

-- 2. Create 'product' table
CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Create 'users' table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Seed initial products (INSERT IGNORE preserves any existing edits and prevents duplicate keys)
INSERT IGNORE INTO product (id, name, quantity) VALUES
(1, 'Moisturizer', 50),
(2, 'Sunscreen', 50),
(3, 'Serum', 50),
(4, 'Sheetmask', 50),
(5, 'Aloevera Gel', 50),
(6, 'Vaseline', 50),
(7, 'Ice Roller', 50),
(8, 'Micellar Water', 50),
(9, 'Milk', 50),
(10, 'Bread', 50),
(11, 'Eggs', 50),
(12, 'Refined Oil', 50),
(13, 'Rajma Beans', 50),
(14, 'Masoor', 50),
(15, 'Soya Beans', 50),
(16, 'Idli Batter', 50),
(17, 'Banana', 50),
(18, 'Chikoo', 50),
(19, 'Grapes', 50),
(20, 'Dragon Fruit', 50),
(21, 'Onion', 50),
(22, 'Lady Finger', 50),
(23, 'Potato', 50),
(24, 'Bottle Gourd', 50),
(25, 'Color Pens', 50),
(26, 'Craft Papers', 50),
(27, 'Notebooks', 50),
(28, 'Geometry', 50),
(29, 'Sketch Book', 50),
(30, 'Color Kit', 50),
(31, 'Color Tapes', 50),
(32, 'Whitener', 50);

-- ==============================================================
-- Verification Queries
-- ==============================================================
-- Check tables:
-- SHOW TABLES;
-- Check products:
-- SELECT COUNT(*) AS product_count FROM product;
-- SELECT * FROM product LIMIT 10;
-- Check users:
-- SELECT * FROM users;
