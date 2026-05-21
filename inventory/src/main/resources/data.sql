-- The default login user is created by DataInitializer with BCrypt password encryption.
-- Username: admin
-- Password: admin123

INSERT IGNORE INTO products (product_id, name, category, price, quantity) VALUES
(1, 'Wireless Mouse', 'Electronics', 18.99, 42),
(2, 'USB-C Keyboard', 'Electronics', 34.50, 18),
(3, 'A4 Printer Paper', 'Stationery', 6.75, 7),
(4, 'Ballpoint Pen Pack', 'Stationery', 4.20, 64),
(5, 'Receipt Printer', 'POS Hardware', 129.00, 5),
(6, 'Barcode Scanner', 'POS Hardware', 89.99, 12),
(7, 'Desk Organizer', 'Office Supplies', 14.25, 23),
(8, 'Thermal Paper Roll', 'POS Supplies', 2.80, 9);

INSERT IGNORE INTO customers (customer_id, name, phone, address) VALUES
(1, 'Ali Khan', '+92-300-1111111', 'Main Market, Lahore'),
(2, 'Sara Ahmed', '+92-321-2222222', 'Blue Area, Islamabad'),
(3, 'Hassan Malik', '+92-333-3333333', 'Saddar, Karachi');