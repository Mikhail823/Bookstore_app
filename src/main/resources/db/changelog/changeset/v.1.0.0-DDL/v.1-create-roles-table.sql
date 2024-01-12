--liquibase formatted sql
--changeset Mikhail Popov:roles

CREATE TABLE roles (
                       id serial4 NOT NULL,
                       "name" varchar(255) NULL,
                       CONSTRAINT roles_pkey PRIMARY KEY (id)
);