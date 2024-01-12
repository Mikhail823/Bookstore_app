--liquibase formatted sql
--changeset Mikhail Popov:faq

CREATE TABLE faq (
                     id serial4 NOT NULL,
                     answer text NOT NULL,
                     question varchar(255) NOT NULL,
                     sort_index int4 NOT NULL DEFAULT 0,
                     CONSTRAINT faq_pkey PRIMARY KEY (id)
);