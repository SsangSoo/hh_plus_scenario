CREATE TABLE PRODUCT (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(255) NOT NULL,
                         price BIGINT NOT NULL DEFAULT 0,
                         created_date DATETIME NOT NULL DEFAULT current_timestamp,
                         modified_date DATETIME NOT NULL DEFAULT current_timestamp,
                         deleted TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE STOCK (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       product_id BIGINT NOT NULL,
                       quantity BIGINT NOT NULL DEFAULT 0,
                       created_date DATETIME NOT NULL DEFAULT current_timestamp,
                       modified_date DATETIME NOT NULL DEFAULT current_timestamp,
                       deleted TINYINT NOT NULL DEFAULT 0
);


