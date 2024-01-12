--liquibase formatted sql
--changeset Mikhail Popov:book_file

CREATE TABLE book_file (
                           id serial4 NOT NULL,
                           hash varchar(255) NOT NULL,
                           "path" varchar(255) NOT NULL,
                           type_id int4 NULL,
                           book_id int4 NULL,
                           CONSTRAINT book_file_pkey PRIMARY KEY (id),
                           CONSTRAINT fk9hhnrtr3w54i3cxuv1q6gjdbo FOREIGN KEY (book_id) REFERENCES book(id)
);
