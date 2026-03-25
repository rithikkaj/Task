-- Create employee first
INSERT INTO employee (name, email) VALUES 
('Admin User', 'admin@example.com');

-- Create user linked to employee
INSERT INTO user (username, password, role, employee_id) VALUES 
('admin', '1234', 'ADMIN', 1);

-- Create sample employee without user
INSERT INTO employee (name, email) VALUES 
('John Doe', 'john@example.com'),
('Jane Smith', 'jane@example.com');

-- Create sample tasks for employees
INSERT INTO task (title, description, assigned_to_id, created_by_id, status, created_at, updated_at) VALUES 
('Setup Dashboard', 'Create initial dashboard UI', 1, 1, 'IN_PROGRESS', NOW(), NOW()),
('Implement Notifications', 'Add notification system', 1, 1, 'TODO', NOW(), NOW()),
('Database Optimization', 'Optimize query performance', 2, 1, 'TODO', NOW(), NOW()),
('User Profile Page', 'Design user profile UI', 2, 1, 'DONE', NOW(), NOW()),
('API Testing', 'Test all API endpoints', 3, 1, 'IN_PROGRESS', NOW(), NOW()),
('Documentation', 'Write API documentation', 3, 1, 'TODO', NOW(), NOW());
