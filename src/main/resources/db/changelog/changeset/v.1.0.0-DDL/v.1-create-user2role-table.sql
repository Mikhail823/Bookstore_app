--liquibase formatted sql
--changeset Mikhail Popov:user2role

CREATE TABLE user2role (
                           id serial4 NOT NULL,
                           role_id int4 NOT NULL,
                           user_id int4 NOT NULL,
                           CONSTRAINT fkjt3q42fm2pedw690p8kkdryq6 FOREIGN KEY (user_id) REFERENCES users(id),
                           CONSTRAINT fkxev9jpfj2u0dkxofnqyevkt2 FOREIGN KEY (role_id) REFERENCES roles(id)
);
