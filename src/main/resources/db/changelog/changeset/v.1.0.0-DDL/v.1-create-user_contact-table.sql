--liquibase formatted sql
--changeset Mikhail Popov:user_contact

CREATE TABLE user_contact (
                              id serial4 NOT NULL,
                              approved int2 NOT NULL,
                              code varchar(255) NOT NULL,
                              code_time timestamp NULL,
                              code_trails int4 NULL,
                              contact varchar(255) NOT NULL,
                              "type" varchar(255) NULL,
                              user_id int4 NULL,
                              CONSTRAINT user_contact_pkey PRIMARY KEY (id),
                              CONSTRAINT fkigqfory4r46pqd0sl4csnwp72 FOREIGN KEY (user_id) REFERENCES users(id)
);