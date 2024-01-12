--liquibase formatted sql
--changeset Mikhail Popov:genres

CREATE TABLE genres (
                        id serial4 NOT NULL,
                        "name" varchar(255) NOT NULL,
                        parent_id int4 NULL,
                        slug varchar(255) NOT NULL,
                        CONSTRAINT genres_pkey PRIMARY KEY (id)
);