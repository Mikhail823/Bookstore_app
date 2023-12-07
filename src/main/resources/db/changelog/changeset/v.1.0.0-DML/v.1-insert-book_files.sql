--liquibase formatted sql
--changeset techgeeknext:insert-book_files-01

insert into book_file (id, hash, type_id, path, book_id) values (1 , 'fsdl342ladads76432', 1, '/Virunga.pdf', 1);
insert into book_file (id, hash, type_id, path, book_id) values (2 , 'asdl35436dads34235', 2, '/Virunga.epub', 1);
insert into book_file (id, hash, type_id, path, book_id) values (3 , 'qwer342lafdss34123', 3, '/Virunga.fb2',  1);