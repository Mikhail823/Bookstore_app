--liquibase formatted sql
--changeset Mikhail Popov:table-users

CREATE TABLE users (
                       id serial4 NOT NULL,
                       balance int4 NOT NULL,
                       hash varchar(255) NOT NULL,
                       "name" varchar(255) NOT NULL,
                       "password" varchar(255) NOT NULL,
                       reg_time timestamp NOT NULL,
                       CONSTRAINT users_pkey PRIMARY KEY (id)
);