-- init.sql
-- DROP ROLE platform;

CREATE USER daily WITH
    LOGIN
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    NOINHERIT
    NOREPLICATION
    PASSWORD 'daily';

GRANT CONNECT ON DATABASE daily TO daily;

-- SCHEMA: daily

-- DROP SCHEMA IF EXISTS daily ;

CREATE SCHEMA IF NOT EXISTS "user"
    AUTHORIZATION daily;

GRANT USAGE ON SCHEMA "user" TO daily;

CREATE TABLE IF NOT EXISTS "user".user (
    id UUID PRIMARY KEY,
    nickname VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_user_nickname ON "user".user (nickname) WHERE NOT deleted;

ALTER TABLE "user".user
    OWNER to daily;

GRANT SELECT, INSERT, UPDATE ON "user".user TO daily;


CREATE TABLE IF NOT EXISTS "user".refresh_token (
    user_id UUID NOT NULL REFERENCES "user".user (id),
    token VARCHAR(255) UNIQUE NOT NULL,
    device_id UUID NOT NULL,
    expiry_date TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_refresh_token_token ON "user".refresh_token (token);
CREATE INDEX IF NOT EXISTS idx_refresh_token_user_id ON "user".refresh_token (user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_token_expiry_date ON "user".refresh_token (expiry_date);
CREATE UNIQUE INDEX IF NOT EXISTS idx_refresh_token_user_id_device_id ON "user".refresh_token (user_id, device_id);

ALTER TABLE "user".refresh_token
    OWNER to daily;

GRANT SELECT, INSERT, UPDATE, DELETE ON "user".refresh_token TO daily;
