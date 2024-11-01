INSERT INTO users (first_name, last_name, email, phone_number, status, created_at)
VALUES
('John', 'Doe', 'john.doe@example.com', '+1234567890', 'ACTIVE', CURRENT_TIMESTAMP),
('Jane', 'Smith', 'jane.smith@example.com', '+1987654321', 'ACTIVE', CURRENT_TIMESTAMP),
('Bob', 'Johnson', 'bob.johnson@example.com', '+1122334455', 'INACTIVE', CURRENT_TIMESTAMP),
('Alice', 'Williams', 'alice.williams@example.com', '+1555666777', 'BLOCKED', CURRENT_TIMESTAMP),
('Charlie', 'Brown', 'charlie.brown@example.com', '+1999888777', 'ACTIVE', CURRENT_TIMESTAMP);