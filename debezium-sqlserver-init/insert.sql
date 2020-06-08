USE testDB;
INSERT INTO products(name, description, weight)
VALUES ('scooter', 'Small 2-wheel scooter', 3.14);
INSERT INTO products(name, description, weight)
VALUES ('car battery', '12V car battery', 8.1);
INSERT INTO products(name, description, weight)
VALUES ('12-pack drill bits', '12-pack of drill bits with sizes ranging from #40 to #3', 0.8);
INSERT INTO products(name, description, weight)
VALUES ('hammer', '12oz carpenter''s hammer', 0.75);
INSERT INTO products(name, description, weight)
VALUES ('hammer', '14oz carpenter''s hammer', 0.875);
INSERT INTO products(name, description, weight)
VALUES ('hammer', '16oz carpenter''s hammer', 1.0);
INSERT INTO products(name, description, weight)
VALUES ('rocks', 'box of assorted rocks', 5.3);
INSERT INTO products(name, description, weight)
VALUES ('jacket', 'water resistent black wind breaker', 0.1);
INSERT INTO products(name, description, weight)
VALUES ('spare tire', '24 inch spare tire', 22.2);
INSERT INTO products_on_hand
VALUES (101, 3);
INSERT INTO products_on_hand
VALUES (102, 8);
INSERT INTO products_on_hand
VALUES (103, 18);
INSERT INTO products_on_hand
VALUES (104, 4);
INSERT INTO products_on_hand
VALUES (105, 5);
INSERT INTO products_on_hand
VALUES (106, 0);
INSERT INTO products_on_hand
VALUES (107, 44);
INSERT INTO products_on_hand
VALUES (108, 2);
INSERT INTO products_on_hand
VALUES (109, 5);
INSERT INTO customers(first_name, last_name, email)
VALUES ('Sally', 'Thomas', 'sally.thomas@acme.com');
INSERT INTO customers(first_name, last_name, email)
VALUES ('George', 'Bailey', 'gbailey@foobar.com');
INSERT INTO customers(first_name, last_name, email)
VALUES ('Edward', 'Walker', 'ed@walker.com');
INSERT INTO customers(first_name, last_name, email)
VALUES ('Anne', 'Kretchmar', 'annek@noanswer.org');
INSERT INTO orders(order_date, purchaser, quantity, product_id)
VALUES ('16-JAN-2016', 1001, 1, 102);
INSERT INTO orders(order_date, purchaser, quantity, product_id)
VALUES ('17-JAN-2016', 1002, 2, 105);
INSERT INTO orders(order_date, purchaser, quantity, product_id)
VALUES ('19-FEB-2016', 1002, 2, 106);
INSERT INTO orders(order_date, purchaser, quantity, product_id)
VALUES ('21-FEB-2016', 1003, 1, 107);
GO