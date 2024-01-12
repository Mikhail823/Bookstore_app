--liquibase formatted sql
--changeset Mikhail Popov:rating_book

CREATE TABLE rating_book (
                             id serial4 NOT NULL,
                             five_star int4 NULL,
                             four_star int4 NULL,
                             one_star int4 NULL,
                             three_star int4 NULL,
                             two_star int4 NULL,
                             book_id int4 NULL,
                             CONSTRAINT rating_book_pkey PRIMARY KEY (id),
                             CONSTRAINT fksac5jk0hnl0m06fa7bj02abcg FOREIGN KEY (book_id) REFERENCES book(id)
);