-- Create the test database
CREATE DATABASE testDB;
GO
USE testDB;
EXEC sys.sp_cdc_enable_db;
-- Create and populate our products using a single insert with many rows
CREATE TABLE products
(
    id          INTEGER IDENTITY (101,1) NOT NULL PRIMARY KEY,
    name        VARCHAR(255)             NOT NULL,
    description VARCHAR(512),
    weight      FLOAT
);
EXEC sys.sp_cdc_enable_table @source_schema = 'dbo', @source_name = 'products', @role_name = NULL,
     @supports_net_changes = 0;
-- Create and populate the products on hand using multiple inserts
CREATE TABLE products_on_hand
(
    product_id INTEGER NOT NULL PRIMARY KEY,
    quantity   INTEGER NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products (id)
);
EXEC sys.sp_cdc_enable_table @source_schema = 'dbo', @source_name = 'products_on_hand', @role_name = NULL,
     @supports_net_changes = 0;
-- Create some customers ...
CREATE TABLE customers
(
    id         INTEGER IDENTITY (1001,1) NOT NULL PRIMARY KEY,
    first_name VARCHAR(255)              NOT NULL,
    last_name  VARCHAR(255)              NOT NULL,
    email      VARCHAR(255)              NOT NULL UNIQUE
);
EXEC sys.sp_cdc_enable_table @source_schema = 'dbo', @source_name = 'customers', @role_name = NULL,
     @supports_net_changes = 0;
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
EXEC sys.sp_cdc_enable_table @source_schema = 'dbo', @source_name = 'orders', @role_name = NULL,
     @supports_net_changes = 0;
GO
