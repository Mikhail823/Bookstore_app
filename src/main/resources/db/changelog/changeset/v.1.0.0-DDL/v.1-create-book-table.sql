--liquibase formatted sql
--changeset Mikhail Popov:book

CREATE TABLE book (
                      id serial4 NOT NULL,
                      description text NULL,
                      image varchar(255) NULL,
                      is_bestseller int4 NULL,
                      count_postponed int4 NULL,
                      count_purchased int4 NULL,
                      count_of_views int4 NULL,
                      popularity int4 NULL,
                      discount float8 NULL,
                      price int4 NULL,
                      pub_date timestamp NULL,
                      quantity_basket int4 NULL,
                      rating int4 NULL,
                      slug varchar(255) NULL,
                      status varchar(255) NULL,
                      title varchar(255) NULL,
                      CONSTRAINT book_pkey PRIMARY KEY (id)
);
