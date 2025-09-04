-- ownerテーブル
CREATE TABLE IF NOT EXISTS owner (
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
    release_date VARCHAR(255) NOT NULL
);
