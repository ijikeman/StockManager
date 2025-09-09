-- ownerテーブル
CREATE TABLE IF NOT EXISTS owner (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

-- sectorテーブル
CREATE TABLE IF NOT EXISTS sector (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

-- stockテーブル
CREATE TABLE IF NOT EXISTS stock (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    current_price DECIMAL(10, 2) NOT NULL,
    dividend DECIMAL(10, 2) NOT NULL,
    earnings_date DATE NULL,
    sector_id INT,
    FOREIGN KEY (sector_id) REFERENCES sector(id)
);

-- holdingテーブル
CREATE TABLE IF NOT EXISTS holding (
    id INT PRIMARY KEY AUTO_INCREMENT,
    owner_id INT,
    stock_id INT,
    nisa BOOLEAN NOT NULL DEFAULT FALSE,
    current_volume INT NOT NULL,
    average_price DECIMAL(10, 2) NOT NULL,
    updated_at DATE NULL,
    FOREIGN KEY (owner_id) REFERENCES owner(id),
    FOREIGN KEY (stock_id) REFERENCES stock(id)
);

-- transactionテーブル
CREATE TABLE IF NOT EXISTS transaction (
    id INT PRIMARY KEY AUTO_INCREMENT,
    holding_id INT,
    transaction_type VARCHAR(255) NOT NULL,
    volume INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    tax DECIMAL(10, 2) NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (holding_id) REFERENCES holding(id)
);

-- income_historyテーブル
CREATE TABLE IF NOT EXISTS income_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    holding_id INT,
    income_type VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (holding_id) REFERENCES holding(id)
);
