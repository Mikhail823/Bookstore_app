--liquibase formatted sql
--changeset Mikhail Popov:tag

CREATE TABLE tag (
                     id serial4 NOT NULL,
                     "name" varchar(255) NULL,
                     CONSTRAINT tag_pkey PRIMARY KEY (id)
);
