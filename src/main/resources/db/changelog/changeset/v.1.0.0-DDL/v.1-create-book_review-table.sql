--liquibase formatted sql
--changeset Mikhail Popov:book_review

CREATE TABLE book_review (
                             id serial4 NOT NULL,
                             rating int4 NULL DEFAULT 0,
                             "text" text NOT NULL,
                             "time" timestamp NOT NULL,
                             book_id int4 NULL,
                             user_id int4 NULL,
                             CONSTRAINT book_review_pkey PRIMARY KEY (id),
                             CONSTRAINT fk29oatdl4f30mtg65oxo1nkmjg FOREIGN KEY (book_id) REFERENCES book(id),
                             CONSTRAINT fkntncp0b191bex8jkm3vy3l13x FOREIGN KEY (user_id) REFERENCES users(id)
);