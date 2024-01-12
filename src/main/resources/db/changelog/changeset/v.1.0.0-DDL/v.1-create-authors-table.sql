--liquibase formatted sql
--changeset Mikhail Popov:authors

CREATE TABLE authors (
                         id serial4 NOT NULL,
                         description text NULL,
                         first_name varchar(255) NULL,
                         last_name varchar(255) NULL,
                         photo varchar(255) NULL,
                         slug varchar(255) NULL,
                         CONSTRAINT authors_pkey PRIMARY KEY (id)
);