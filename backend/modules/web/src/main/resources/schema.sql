-- This script is for dropping existing tables and creating new ones.
-- Be cautious when running this in a production environment.

-- Drop existing tables in reverse order of dependency
DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS income_history;
DROP TABLE IF EXISTS holding;
DROP TABLE IF EXISTS incoming_history;
DROP TABLE IF EXISTS benefit_history;
DROP TABLE IF EXISTS stock_lot;
DROP TABLE IF EXISTS stock;
DROP TABLE IF EXISTS sector;
DROP TABLE IF EXISTS owner;


-- ユーザー情報を管理するテーブル
-- ownerテーブル
CREATE TABLE owner (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- 銘柄のセクター情報を管理するテーブル
-- sectorテーブル
CREATE TABLE sector (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- 株式銘柄の基本情報を管理するテーブル
-- stockテーブル
CREATE TABLE stock (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    current_price DECIMAL(10, 2) NOT NULL,
    dividend DECIMAL(10, 2) NOT NULL,
    minimal_unit INT NOT NULL DEFAULT 100,
    earnings_date DATE NULL,
    sector_id INT,
    FOREIGN KEY (sector_id) REFERENCES sector(id)
);

-- stock_lotテーブル
CREATE TABLE stock_lot (
    id INT PRIMARY KEY AUTO_INCREMENT,
    owner_id INT,
    stock_id INT,
    unit INT NOT NULL,
    is_nisa BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(255) NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES owner(id),
    FOREIGN KEY (stock_id) REFERENCES stock(id)
);

-- transactionテーブル
CREATE TABLE transaction (
    id INT PRIMARY KEY AUTO_INCREMENT,
    lot_id INT,
    type VARCHAR(255) NOT NULL,
    unit INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    tax DECIMAL(10, 2) NOT NULL,
    transaction_date DATE NOT NULL,
    FOREIGN KEY (lot_id) REFERENCES stock_lot(id)
);

-- incoming_historyテーブル
CREATE TABLE incoming_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    lot_id INT,
    incoming DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    FOREIGN KEY (lot_id) REFERENCES stock_lot(id)
);

-- benefit_historyテーブル
CREATE TABLE benefit_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    lot_id INT,
    benefit DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (lot_id) REFERENCES stock_lot(id)
);
