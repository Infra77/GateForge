-- Convert the old enum to text before mapping its stored values.
ALTER TABLE organisations MODIFY COLUMN status VARCHAR(16) NOT NULL;

UPDATE organisations
SET status = CASE status
        WHEN 'active' THEN 'ACTIVE'
        WHEN 'suspended' THEN 'SUSPENDED'
        WHEN 'inactive' THEN 'SUSPENDED'  -- Legacy value from V1.
    END,
    updated_at = updated_at
WHERE status IN ('active', 'suspended', 'inactive');

ALTER TABLE organisations
    MODIFY COLUMN status ENUM('PENDING', 'ACTIVE', 'SUSPENDED', 'DELETED') NOT NULL;
