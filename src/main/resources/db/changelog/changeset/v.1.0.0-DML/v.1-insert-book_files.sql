--liquibase formatted sql
--changeset Mikhail Popov:insert-book_files

insert into book_file (hash, type_id, path, book_id) values ('fsdl342ladads76432', 1, '/Health Coach IV.pdf', 1);
insert into book_file (hash, type_id, path, book_id) values ('asdl35436dads34235', 2, '/Health Coach IV.epub', 1);
insert into book_file (hash, type_id, path, book_id) values ('qwer342lafdss34123', 3, '/Health Coach IV.fb2',  1);