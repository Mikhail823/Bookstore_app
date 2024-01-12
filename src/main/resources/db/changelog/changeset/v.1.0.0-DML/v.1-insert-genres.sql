--liquibase formatted sql
--changeset Mikhail Popov:insert-genres

INSERT INTO genres (id,parent_id,slug,name)
VALUES
    (1,3,'genre-0','Фантастика'),
    (2,2,'genre-2','Боевики'),
    (3,3,'genre-3', 'Дедективы'),
    (4,0,'genre-4','Романы'),
    (5,0,'genre-5','Деловая литература'),
    (6,1,'genre-6','Банковское дело'),
    (7,1,'genre-7','Комедия'),
    (8,2,'genre-8','Приключения'),
    (9,0,'genre-9','Драмма'),
    (10,3,'genre-10','Биография'),
    (11,0,'genre-11','Бизнес'),
    (12,0,'genre-12','Детская литература');