-- Seed Users
INSERT INTO users (name, email, password, role)
VALUES ('Ammar User', 'user@example.com', '$2a$10$iG5ZOKLkhVUfdumJRaxTYeq/Z7baNGXKO71ABRy0OAvULjuknZPIW', 'USER');

INSERT INTO users (name, email, password, role)
VALUES ('Ammar Admin', 'admin@example.com', '$2a$10$ADWL0R.d9lnKyUsuVDUGl.HtYE.cl7kKk6fy57v7KSJ9mHxWtzgOm', 'ADMIN');

-- Seed Products
INSERT INTO products (name, description, price)
VALUES ('Nasi Goreng Spesial', 'Nasi goreng telor ayam', 15000.00);

INSERT INTO products (name, description, price)
VALUES ('Mie Ayam Bakso', 'Mie ayam topping bakso urat', 17000.00);

INSERT INTO products (name, description, price)
VALUES ('Es Teh Manis', 'Es teh manis', 5000.00);
