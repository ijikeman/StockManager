-- This script is for dropping existing tables and creating new ones.
-- Be cautious when running this in a production environment.

-- Drop existing tables in reverse order of dependency
DROP TABLE IF EXISTS benefit_history;
DROP TABLE IF EXISTS incoming_history;
DROP TABLE IF EXISTS sell_transaction;
DROP TABLE IF EXISTS buy_transaction;
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
    incoming DECIMAL(10, 2) NOT NULL,
    minimal_unit INT NOT NULL DEFAULT 100,
    earnings_date DATE NULL,
    sector_id INT,
    previous_price DECIMAL(10, 2) NULL,
    latest_disclosure_date DATE NULL,
    FOREIGN KEY (sector_id) REFERENCES sector(id)
);

-- stock_lotテーブル
CREATE TABLE stock_lot (
    id INT PRIMARY KEY AUTO_INCREMENT,
    owner_id INT,
    stock_id INT,
    current_unit INT NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES owner(id),
    FOREIGN KEY (stock_id) REFERENCES stock(id)
);

-- buy_transactionテーブル
CREATE TABLE buy_transaction (
    id INT PRIMARY KEY AUTO_INCREMENT,
    stock_lot_id INT,
    unit INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    fee DECIMAL(10, 2) NOT NULL,
    is_nisa BOOLEAN NOT NULL DEFAULT FALSE,
    transaction_date DATE NOT NULL,
    FOREIGN KEY (stock_lot_id) REFERENCES stock_lot(id)
);

-- sell_transactionテーブル
CREATE TABLE sell_transaction (
    id INT PRIMARY KEY AUTO_INCREMENT,
    buy_transaction_id INT,
    unit INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    fee DECIMAL(10, 2) NOT NULL,
    transaction_date DATE NOT NULL,
    FOREIGN KEY (buy_transaction_id) REFERENCES buy_transaction(id)
);

-- incoming_historyテーブル
CREATE TABLE incoming_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    stock_lot_id INT,
    sell_transaction_id INT,
    incoming DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    FOREIGN KEY (stock_lot_id) REFERENCES stock_lot(id),
    FOREIGN KEY (sell_transaction_id) REFERENCES sell_transaction(id)
);

-- benefit_historyテーブル
CREATE TABLE benefit_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    stock_lot_id INT,
    sell_transaction_id INT,
    benefit DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    FOREIGN KEY (stock_lot_id) REFERENCES stock_lot(id),
    FOREIGN KEY (sell_transaction_id) REFERENCES sell_transaction(id)
);
