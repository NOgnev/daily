-- init.sql
-- DROP ROLE platform;

CREATE USER diary WITH
    LOGIN
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    NOINHERIT
    NOREPLICATION
    PASSWORD 'diary';

GRANT CONNECT ON DATABASE diary TO diary;

-- SCHEMA: diary

-- DROP SCHEMA IF EXISTS diary ;

CREATE SCHEMA IF NOT EXISTS diary
    AUTHORIZATION diary;

GRANT USAGE ON SCHEMA diary TO diary;

CREATE TABLE IF NOT EXISTS diary.user (
    id UUID PRIMARY KEY,
    nickname VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_user_nickname ON diary.user (nickname) WHERE NOT deleted;

ALTER TABLE diary.user
    OWNER to diary;

GRANT SELECT, INSERT, UPDATE ON diary.user TO diary;


CREATE TABLE IF NOT EXISTS diary.refresh_token (
    user_id UUID NOT NULL REFERENCES diary.user (id),
    token VARCHAR(255) UNIQUE NOT NULL,
    device_id UUID NOT NULL,
    expiry_date TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_refresh_token_token ON diary.refresh_token (token);
CREATE INDEX IF NOT EXISTS idx_refresh_token_user_id ON diary.refresh_token (user_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_refresh_token_user_id_device_id ON diary.refresh_token (user_id, device_id);

ALTER TABLE diary.refresh_token
    OWNER to diary;

GRANT SELECT, INSERT, UPDATE, DELETE ON diary.refresh_token TO diary;
