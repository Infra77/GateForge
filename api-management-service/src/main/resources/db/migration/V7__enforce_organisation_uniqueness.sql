ALTER TABLE organisations
    MODIFY COLUMN name VARCHAR(150)
        CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL,
    MODIFY COLUMN slug VARCHAR(100)
        CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL,
    ADD UNIQUE KEY uk_organisations_name (name);