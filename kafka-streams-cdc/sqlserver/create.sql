-- Create the test database
CREATE DATABASE testDB;
GO
USE testDB;
-- Create and populate our products using a single insert with many rows
CREATE TABLE products
(
    id          INTEGER IDENTITY (101,1) NOT NULL PRIMARY KEY,
    name        VARCHAR(255)             NOT NULL,
    description VARCHAR(512),
    weight      FLOAT
);
-- Create and populate the products on hand using multiple inserts
CREATE TABLE products_on_hand
(
    product_id INTEGER NOT NULL PRIMARY KEY,
    quantity   INTEGER NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products (id)
);
-- Create some customers ...
CREATE TABLE customers
(
    id         INTEGER IDENTITY (1001,1) NOT NULL PRIMARY KEY,
    uuid       UNIQUEIDENTIFIER DEFAULT NEWID(),
    first_name VARCHAR(255)              NOT NULL,
    last_name  VARCHAR(255)              NOT NULL,
    email      VARCHAR(255)              NOT NULL
);
-- Create some very simple orders
CREATE TABLE orders
(
    id         INTEGER IDENTITY (10001,1) NOT NULL PRIMARY KEY,
    order_date DATE                       NOT NULL,
    purchaser  INTEGER                    NOT NULL,
    quantity   INTEGER                    NOT NULL,
    product_id INTEGER                    NOT NULL,
    FOREIGN KEY (purchaser) REFERENCES customers (id),
    FOREIGN KEY (product_id) REFERENCES products (id)
);
GO
