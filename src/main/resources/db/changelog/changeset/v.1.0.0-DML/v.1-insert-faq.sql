--liquibase formatted sql
--changeset techgeeknext:insert-faq-01

insert into faq (id, sort_index, question, answer) values (1, 2, 'Не получается скачать книгу. Что делать?', '<p>Здравствуте. Если книга у Вас куплена и находится в разделе "Мои книги", то необходимо написать сообщение менеджеру магазина по адресу: rabota822@bk.ru</p>
<p>С уважением магазин Bookstore.</p>');
insert into faq (id, sort_index, question, answer) values (2, 4, 'Не могу найти интересующую меня книгу.', '<p>Здравствуте. Если Вам необходима какая - либо книга, которой нет в нашем магазине, пожалуйста напишите нашему менеджеру магазина по адресу: <a href="mailto:rabota822@bk.ru,">rabota822@bk.ru,</a> указав автора и название книги и мы сможем Вам помочь.</p>
<p>С уваженеим магазин Bookstore.</p>');
insert into faq (id, sort_index, question, answer) values (3, 1, 'Не могу войти в личный кабинет.', '<p>Здравствуте. Мы отправили Ваше обращение модераторам сайта. Скоро Вам смогут помоч.</p>
<p>С уваженеим магазин Bookstore.</p>');
insert into faq (id, sort_index, question, answer) values (4, 3, 'Можно ли добавить в магазин новых авторов?', '<p>Здравствуте. В наш магазин добавляются самые востребованные, интересные и популярные книги, авторы. Если по интересующим Вас книгам и авторам будет больше спросов, то непременно добавим их.</p>
<p>С уваженеим магазин Bookstore.</p>');