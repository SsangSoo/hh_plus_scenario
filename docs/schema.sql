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


CREATE TABLE MEMBER (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        name CHAR(30) NOT NULL,
                        birth_date CHAR(8) ,
                        address CHAR(50) ,
                        created_date DATETIME NOT NULL DEFAULT current_timestamp,
                        modified_date DATETIME NOT NULL DEFAULT current_timestamp,
                        removed TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE POINT (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       member_id BIGINT NOT NULL UNIQUE,
                       point BIGINT NOT NULL DEFAULT 0,
                       created_date DATETIME NOT NULL DEFAULT current_timestamp,
                       modified_date DATETIME NOT NULL DEFAULT current_timestamp,
                       removed TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE POINT_HISTORY (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       member_id BIGINT NOT NULL,
                       point_id BIGINT NOT NULL,
                       point_amount BIGINT NOT NULL,
                       state CHAR(20) NOT NULL,
                       created_date DATETIME NOT NULL DEFAULT current_timestamp,
                       total_point BIGINT NOT NULL
);


CREATE TABLE ORDERS (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       member_id BIGINT NOT NULL,
                       orderDate DATETIME NOT NULL DEFAULT current_timestamp
);

CREATE TABLE ORDER_PRODUCT (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        product_id BIGINT NOT NULL,
                        order_id BIGINT NOT NULL,
                        quantity BIGINT NOT NULL,
                        created_date DATETIME NOT NULL DEFAULT current_timestamp
);

CREATE TABLE PAYMENT (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        order_id BIGINT NOT NULL,
                        total_amount BIGINT NOT NULL,
                        payment_state CHAR(30) NOT NULL,
                        created_date DATETIME NOT NULL DEFAULT current_timestamp,
                        modified_date DATETIME NOT NULL DEFAULT current_timestamp
);
