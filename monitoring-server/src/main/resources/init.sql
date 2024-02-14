CREATE SCHEMA IF NOT EXISTS app_schema;
CREATE SCHEMA IF NOT EXISTS audit_schema;
ALTER ROLE iamuser SET search_path TO app_schema;
DROP SCHEMA public CASCADE;