create database tg_vector;

create schema base;

create table if not exists base.user_chat
(
    user_id      bigint,
    chat_id      bigint,
    user_chat_id serial
        constraint user_chat_pk
            primary key
        unique
);

alter table base.user_chat
    owner to postgres;

create unique index if not exists user_chat_user_id_chat_id_uindex
    on base.user_chat (user_id, chat_id);



create table if not exists base.message
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

create index if not exists message_date_index
    on base.message (date);



