CREATE TABLE users (
    id serial primary key,
    username text NOT NULL,
    mobile text,
    email text,
    encrypted_password text,
    reset_password_token text,
    reset_password_send_at timestamp without time zone,
    sign_in_count integer DEFAULT 0 NOT NULL,
    current_sign_in_at timestamp without time zone,
    current_sign_in_ip text,
    last_sign_in_ip text,
    failed_attempts integer DEFAULT 0 NOT NULL,
    unlock_token text,
    locked_at timestamp without time zone,
    status smallint DEFAULT 1 NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);
--;;
create unique index user_mobile_index on users(mobile);
--;;
create unique index user_username_index on users(username);
--;;
create unique index user_email_index on users(email);
--;;
create unique index user_reset_password_token_index on users(reset_password_token);
--;;
create unique index user_unlock_token_index on users(unlock_token);
