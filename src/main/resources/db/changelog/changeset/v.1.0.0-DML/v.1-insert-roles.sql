--liquibase formatted sql
--changeset Mikhail Popov:insert-roles

insert into roles (name) values ('ROLE_ADMIN');
insert into roles (name) values ('ROLE_USER');
insert into roles (name) values ('ROLE_ANONYMOUS');