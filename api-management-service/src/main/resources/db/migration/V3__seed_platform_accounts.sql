INSERT INTO management_users(organisation_id, role_id, username, password_hash, enabled)
VALUES
(NULL, (SELECT role_id FROM roles WHERE name = 'PLATFORM_ADMIN'), 'platform-admin', '$2y$10$tN40GlfKi18/Z37o8T45ruSnQI4O4FT/CLs8zxqDgFlGKZZJ65XH2', TRUE),
(NULL, (SELECT role_id FROM roles WHERE name = 'GATEWAY_SERVICE'), 'gateway_service', '$2y$10$dZNcVtA7XB/5AfMiKtF8nuLzOMwrRAUQoT.JJx5Y2kVUhRT2q1Wt2', TRUE);