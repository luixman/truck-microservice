create table telegram_conn
(
    id                  serial
        primary key,
    chat_id             bigint,
    auth_key            varchar(128),
    authorized          boolean default false,
    first_auth_time     timestamp with time zone,
    activated_companies varchar
);

alter table telegram_conn
    owner to postgres;
