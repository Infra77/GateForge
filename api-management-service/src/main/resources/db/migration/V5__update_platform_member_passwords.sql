UPDATE management_users
SET password_hash = '$2a$10$uk2GC4znAEAviL4RPvWfvueVkqx37V7ZSKlx8.htvFkh0rTiNtCz2'      -- password_platform_admin power 10
WHERE username = 'platform-admin';

UPDATE management_users
SET password_hash = '$2a$10$k8UhcS31JP8xFXM2yfnA2etbbyEi7M6CfxBaBbuJSKDZ1JmBvNite'      -- password_gateway_service power 10
WHERE username = 'gateway-service';