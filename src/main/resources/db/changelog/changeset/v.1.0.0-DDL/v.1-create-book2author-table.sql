--liquibase formatted sql
--changeset Mikhail Popov:book2author

CREATE TABLE book2author (
                             id serial4 NOT NULL,
                             author_id int4 NOT NULL,
                             book_id int4 NOT NULL,
                             sort_index int4 NOT NULL DEFAULT 0,
                             CONSTRAINT book2author_pkey PRIMARY KEY (id),
                             CONSTRAINT fk3hyom3yo5q6nfo9ytqofqil37 FOREIGN KEY (author_id) REFERENCES authors(id),
                             CONSTRAINT fkafij5snytuqywyya5gj5r30l3 FOREIGN KEY (book_id) REFERENCES book(id)
);