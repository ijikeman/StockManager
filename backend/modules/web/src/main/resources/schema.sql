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
    current_volume INT NOT NULL,
    average_price DECIMAL(10, 2) NOT NULL,
    updated_at DATE NULL,
    FOREIGN KEY (owner_id) REFERENCES owner(id),
    FOREIGN KEY (stock_id) REFERENCES stock(id)
);
