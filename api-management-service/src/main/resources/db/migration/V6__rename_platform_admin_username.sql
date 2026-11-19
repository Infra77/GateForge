UPDATE management_users
SET username = 'platform_admin'
WHERE username = 'platform-admin'
    AND organisation_id IS NULL
    AND role_id = (SELECT role_id FROM roles WHERE name = 'PLATFORM_ADMIN');
