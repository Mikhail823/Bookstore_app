--liquibase formatted sql
--changeset Mikhail Popov:book2genres

CREATE TABLE book2genre (
                            id int4 NOT NULL,
                            book_id serial4 NOT NULL,
                            genre_id int4 NOT NULL,
                            CONSTRAINT book2genre_pkey PRIMARY KEY (book_id),
                            CONSTRAINT fkdyiaf682r8d022a3gi1q16ypw FOREIGN KEY (book_id) REFERENCES book(id),
                            CONSTRAINT fknb5tbib0eo6i1qhmy62b78b3o FOREIGN KEY (genre_id) REFERENCES genres(id)
);