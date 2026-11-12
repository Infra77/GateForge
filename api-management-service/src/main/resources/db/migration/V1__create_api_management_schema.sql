CREATE DATABASE IF NOT EXISTS api_management;
USE api_management;

-- -----------------------------------------------------
-- 1. organisations
-- -----------------------------------------------------

-- This table defines the organisations that will be a client of GateForge
CREATE TABLE IF NOT EXISTS organisations (
    organisation_id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(100) NOT NULL,     -- say the client name is Food Fast then we can store slug as foodfast/{their-route}
    status ENUM('active', 'inactive') NOT NULL,    -- says whether the organization is up or no
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,     -- time when client joined
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,     -- time when client asked for update
    deleted_at DATETIME NULL DEFAULT NULL,      -- time when the client left GateForge gateway service

    PRIMARY KEY (organisation_id),
    UNIQUE KEY uk_organisations_slug (slug)
);

-- -----------------------------------------------------
-- 2. roles
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
    role_id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,

    PRIMARY KEY (role_id),
    UNIQUE KEY uk_roles_name (name)
);


-- -----------------------------------------------------
-- 3. management_users
-- -----------------------------------------------------
-- Each management user can belong to one organisation; an organisation can have many users.
CREATE TABLE IF NOT EXISTS management_users (
    management_user_id BIGINT NOT NULL AUTO_INCREMENT,
    organisation_id BIGINT DEFAULT NULL,        -- organisation the user is linked to
    role_id BIGINT NOT NULL,                    -- role the user has
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    PRIMARY KEY (management_user_id),
    UNIQUE KEY uk_management_users_org_username (organisation_id, username),
    KEY idx_management_users_role_id (role_id),
    CONSTRAINT fk_management_users_organisation FOREIGN KEY (organisation_id) REFERENCES organisations (organisation_id),
    CONSTRAINT fk_management_users_role FOREIGN KEY (role_id) REFERENCES roles (role_id)
);

-- -----------------------------------------------------
-- 4. api_routes
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS api_routes (
    api_route_id BIGINT NOT NULL AUTO_INCREMENT,
    organisation_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    path_pattern VARCHAR(200) NOT NULL,
    target_base_url VARCHAR(200) NOT NULL,
    path_prefix VARCHAR(100) NULL,
    timeout INT NOT NULL,
    request_per_min INT NOT NULL,
    idempotent BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version_id BIGINT NULL,
    PRIMARY KEY (api_route_id),
    KEY idx_api_routes_organisation_id (organisation_id),
    CONSTRAINT fk_api_routes_organisation FOREIGN KEY (organisation_id) REFERENCES organisations (organisation_id)
);

-- -----------------------------------------------------
-- 5. route_allowed_methods
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS route_allowed_methods (
    route_allowed_method_id BIGINT NOT NULL AUTO_INCREMENT,
    api_route_id BIGINT NOT NULL,
    method VARCHAR(10) NOT NULL,    -- allowed method on this route - GET/POST/PATCH/PUT/DELETE
    PRIMARY KEY (route_allowed_method_id),
    KEY idx_route_allowed_methods_api_route_id (api_route_id),
    CONSTRAINT fk_route_allowed_methods_api_route FOREIGN KEY (api_route_id) REFERENCES api_routes (api_route_id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- 6. route_header_rules
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS route_header_rules (
    route_header_rule_id BIGINT NOT NULL AUTO_INCREMENT,
    api_route_id BIGINT NOT NULL,
    PRIMARY KEY (route_header_rule_id),
    KEY idx_route_header_rules_api_route_id (api_route_id),
    CONSTRAINT fk_route_header_rules_api_route FOREIGN KEY (api_route_id) REFERENCES api_routes (api_route_id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- 7. route_config_version
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS route_config_version (
    route_config_version_id BIGINT NOT NULL AUTO_INCREMENT,
    api_route_id BIGINT NOT NULL,
    version_id BIGINT NOT NULL,
    snapshot JSON NOT NULL,
    PRIMARY KEY (route_config_version_id),
    UNIQUE KEY uk_route_config_version_api_route_version (api_route_id, version_id),
    CONSTRAINT fk_route_config_version_api_route FOREIGN KEY (api_route_id) REFERENCES api_routes (api_route_id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- 8. control_plane_audit_records
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS control_plane_audit_records (
    control_plane_audit_record_id BIGINT NOT NULL AUTO_INCREMENT,
    organisation_id BIGINT,
    actor VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    resource_id VARCHAR(100) NOT NULL,
    correlation_id VARCHAR(100),
    duration_ms BIGINT NOT NULL,
    result VARCHAR(20) NOT NULL,
    details VARCHAR(2000),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,

    PRIMARY KEY (control_plane_audit_record_id),
    KEY idx_control_plane_audit_records_organisation_id (organisation_id),
    CONSTRAINT fk_control_plane_audit_records_organisation FOREIGN KEY (organisation_id) REFERENCES organisations (organisation_id)
);
