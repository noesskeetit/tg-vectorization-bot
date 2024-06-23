create database tg_vector;

\c tg_vector;

create schema base;

create sequence base.user_chat_user_chat_id_seq
    as integer;

alter sequence base.user_chat_user_chat_id_seq owner to postgres;

create table base.user_chat
(
    user_id      bigint,
    chat_id      bigint,
    user_chat_id bigint default nextval('base.user_chat_user_chat_id_seq'::regclass) not null
        constraint user_chat_pk
            primary key
        unique
);

alter table base.user_chat
    owner to postgres;

alter sequence base.user_chat_user_chat_id_seq owned by base.user_chat.user_chat_id;

create unique index user_chat_user_id_chat_id_uindex
    on base.user_chat (user_id, chat_id);

create table base.message
(
    user_id       bigint,
    chat_id       bigint,
    message       varchar,
    date          timestamp with time zone,
    message_id    bigserial
        constraint message_pk
            primary key
        unique,
    message_id_tg bigint not null
);

alter table base.message
    owner to postgres;

create index message_date_index
    on base.message (date);

