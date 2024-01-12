--liquibase formatted sql
--changeset Mikhail Popov:book2user_type

CREATE TABLE book2user_type (
                                id serial4 NOT NULL,
                                code varchar(255) NULL,
                                "name" varchar(255) NULL,
                                CONSTRAINT book2user_type_pkey PRIMARY KEY (id)
);