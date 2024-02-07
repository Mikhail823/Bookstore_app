--liquibase formatted sql
--changeset Mikhail Popov:book2user

CREATE TABLE book2user (
                           id serial4 NOT NULL,
                           book_id int4 NOT NULL,
                           "time" timestamp NOT NULL,
                           type_id int4 NOT NULL,
                           user_id int4 NOT NULL,
                           CONSTRAINT book2user_pkey PRIMARY KEY (id),
                           CONSTRAINT fk1i8i82uo8kbv1wepiujenmj7x FOREIGN KEY (user_id) REFERENCES users(id),
                           CONSTRAINT fk7cv34daf9pi5ie147slv010b3 FOREIGN KEY (book_id) REFERENCES book(id)
);