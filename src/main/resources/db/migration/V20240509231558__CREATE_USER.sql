CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE utl_users(
    id text NOT NULL DEFAULT public.uuid_generate_v4(),
    username text NOT NULL,
    "password" text NOT NULL,
    email text NOT NULL,
    last_login timestamp with time zone NULL,
    eff_begin timestamp with time zone NULL,
    eff_end timestamp with time zone NULL,
    created_by text NOT NULL DEFAULT 'SYSTEM'::text,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_by text NOT NULL DEFAULT 'SYSTEM'::text,
    updated_at timestamp with time zone NOT NULL DEFAULT now(),
    "version" int8 NOT NULL DEFAULT 0,
    is_active bool DEFAULT true,
    deleted_at timestamp with time zone NULL,
    CONSTRAINT utl_users_pkey PRIMARY KEY (id)
);
CREATE UNIQUE INDEX utl_users_un ON utl_users (username)
    WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX utl_users_un2 ON utl_users (email)
    WHERE deleted_at IS NULL;