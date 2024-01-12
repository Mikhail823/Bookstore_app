--liquibase formatted sql
--changeset Mikhail Popovt:file_download

CREATE TABLE file_download (
                               id serial4 NOT NULL,
                               book_id int4 NOT NULL,
                               count int4 NOT NULL DEFAULT 1,
                               user_id int4 NOT NULL,
                               CONSTRAINT file_download_pkey PRIMARY KEY (id)
);