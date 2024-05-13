CREATE TABLE utl_jwt_whitelists (
    id text NOT NULL,
    user_id text NOT NULL,
    access_token text NOT NULL,
    created_by text NOT NULL DEFAULT 'SYSTEM'::text,
    created_at timestamp NOT NULL DEFAULT now(),
    updated_by text NOT NULL DEFAULT 'SYSTEM'::text,
    updated_at timestamp NOT NULL DEFAULT now(),
    "version" int8 NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ NULL,
    CONSTRAINT utl_jwt_whitelist_utl_users_fk FOREIGN KEY (user_id) REFERENCES utl_users(id),
    CONSTRAINT utl_jwt_whitelist_access_token_key UNIQUE (access_token),
    CONSTRAINT utl_jwt_whitelist_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS utl_jwt_whitelist_access_token_un ON utl_jwt_whitelists USING btree (access_token) WHERE (deleted_at IS NULL);