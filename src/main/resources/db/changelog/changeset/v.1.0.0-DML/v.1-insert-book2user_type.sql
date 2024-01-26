--liquibase formatted sql
--changeset Mikhail Popov:insert-book2user_type

insert into book2user_type (code, name) values ('KEPT', 'Отложена');
insert into book2user_type (code, name) values ('CART', 'В корзине');
insert into book2user_type (code, name) values ('PAID', 'Куплена');
insert into book2user_type (code, name) values ('ARCHIVED', 'В архиве');
insert into book2user_type (code, name) values ('VIEWED', 'Просмотренная')