--liquibase formatted sql
--changeset Mikhail Popov:document

CREATE TABLE document (
                          id serial4 NOT NULL,
                          paragraph text NOT NULL,
                          slug varchar(255) NOT NULL,
                          sort_index int4 NOT NULL DEFAULT 0,
                          "text" text NOT NULL,
                          title varchar(255) NOT NULL,
                          CONSTRAINT document_pkey PRIMARY KEY (id)
);