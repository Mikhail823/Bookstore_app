--liquibase formatted sql
--changeset Mikhail Popov:users_transaction

CREATE TABLE balance_transaction (
                                   id serial4 NOT NULL,
                                   user_id int4 NOT NULL,
                                   time timestamp NULL,
                                   value int NOT NULL DEFAULT 0,
                                   description text NOT NULL,
                                   payment_status  varchar(255) NULL,
                                   CONSTRAINT fk2u1ku81bve51qrdo8nofn92di FOREIGN KEY (user_id) REFERENCES users(id)

);