--liquibase formatted sql
--changeset Mikhail Popov:jwt_black_list

CREATE TABLE jwt_black_list (
                                id serial4 NOT NULL,
                                jwt_token varchar(600) NULL,
                                CONSTRAINT jwt_black_list_pkey PRIMARY KEY (id)
);