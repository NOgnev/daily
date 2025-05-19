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

CREATE SCHEMA IF NOT EXISTS daily
    AUTHORIZATION daily;

GRANT USAGE ON SCHEMA daily TO daily;

CREATE TABLE IF NOT EXISTS daily.note (
    user_id UUID NOT NULL REFERENCES "user".user (id),
    date DATE NOT NULL,
    note VARCHAR(20000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,

    PRIMARY KEY (user_id, date)
);

ALTER TABLE daily.note
    OWNER to daily;

GRANT SELECT, INSERT, UPDATE, DELETE ON daily.note TO daily;

CREATE TYPE daily.dialog_status AS ENUM ('in_progress', 'finished', 'error');

CREATE TABLE IF NOT EXISTS daily.dialog (
    user_id UUID NOT NULL REFERENCES "user".user (id),
    date DATE NOT NULL,
    status daily.dialog_status NOT NULL DEFAULT 'in_progress',
    messages JSONB NOT NULL DEFAULT '[]',
	summary TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    PRIMARY KEY (user_id, date)
);

ALTER TABLE daily.dialog
    OWNER to daily;

GRANT SELECT, INSERT, UPDATE, DELETE ON daily.dialog TO daily;
