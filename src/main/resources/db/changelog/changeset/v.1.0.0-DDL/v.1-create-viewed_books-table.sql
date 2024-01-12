--liquibase formatted sql
--changeset Mikhail Popov:viewed_books

CREATE TABLE viewed_books (
                              id serial4 NOT NULL,
                              "time" timestamp NULL,
                              "type" varchar(255) NULL,
                              book_id int4 NULL,
                              user_id int4 NULL,
                              CONSTRAINT viewed_books_pkey PRIMARY KEY (id),
                              CONSTRAINT fkd3kbofp7y1ra3r9em3e4nt414 FOREIGN KEY (user_id) REFERENCES users(id),
                              CONSTRAINT fkhei30yltabemu3eyno4jlddhu FOREIGN KEY (book_id) REFERENCES book(id)
);