--liquibase formatted sql
--changeset Mikhail Popov:book2tag

CREATE TABLE book2tag (
                          id serial4 NOT NULL,
                          book_id int4 NOT NULL,
                          tag_id int4 NOT NULL,
                          CONSTRAINT book2tag_pkey PRIMARY KEY (id),
                          CONSTRAINT fk_book_tag FOREIGN KEY (book_id) REFERENCES book(id),
                          CONSTRAINT fk_tag_book FOREIGN KEY (tag_id) REFERENCES tag(id)
);