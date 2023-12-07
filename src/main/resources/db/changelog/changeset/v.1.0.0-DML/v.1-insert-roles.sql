--liquibase formatted sql
--changeset techgeeknext:insert-roles-01

insert into roles (name) values ('ROLE_ADMIN');
insert into roles (name) values ('ROLE_USER');
insert into roles (name) values ('ROLE_ANONYMOUS');