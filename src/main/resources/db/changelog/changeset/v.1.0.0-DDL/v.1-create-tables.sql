--liquibase formatted sql
--changeset techgeeknext:create-tables

CREATE TABLE authors (
                                id serial4 NOT NULL,
                                description text NULL,
                                first_name varchar(255) NULL,
                                last_name varchar(255) NULL,
                                photo varchar(255) NULL,
                                slug varchar(255) NULL,
                                CONSTRAINT authors_pkey PRIMARY KEY (id)
);

CREATE TABLE book (
                             id serial4 NOT NULL,
                             description text NULL,
                             image varchar(255) NULL,
                             is_bestseller int4 NULL,
                             count_postponed int4 NULL,
                             count_purchased int4 NULL,
                             popularity int4 NULL,
                             discount float8 NULL,
                             price int4 NULL,
                             pub_date timestamp NULL,
                             quantity_basket int4 NULL,
                             rating int4 NULL,
                             slug varchar(255) NULL,
                             status varchar(255) NULL,
                             title varchar(255) NULL,
                             CONSTRAINT book_pkey PRIMARY KEY (id)
);

CREATE TABLE genres (
                        id serial4 NOT NULL,
                        "name" varchar(255) NOT NULL,
                        parent_id int4 NULL,
                        slug varchar(255) NOT NULL,
                        CONSTRAINT genres_pkey PRIMARY KEY (id)
);

CREATE TABLE tag (
                     id serial4 NOT NULL,
                     "name" varchar(255) NULL,
                     CONSTRAINT tag_pkey PRIMARY KEY (id)
);

CREATE TABLE users (
                       id serial4 NOT NULL,
                       balance int4 NOT NULL,
                       hash varchar(255) NOT NULL,
                       "name" varchar(255) NOT NULL,
                       "password" varchar(255) NOT NULL,
                       reg_time timestamp NOT NULL,
                       CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE roles (
                       id serial4 NOT NULL,
                       "name" varchar(255) NULL,
                       CONSTRAINT roles_pkey PRIMARY KEY (id)
);

CREATE TABLE book2author (
                                    id serial4 NOT NULL,
                                    author_id int4 NOT NULL,
                                    book_id int4 NOT NULL,
                                    sort_index int4 NOT NULL DEFAULT 0,
                                    CONSTRAINT book2author_pkey PRIMARY KEY (id),
                                    CONSTRAINT fk3hyom3yo5q6nfo9ytqofqil37 FOREIGN KEY (author_id) REFERENCES authors(id),
                                    CONSTRAINT fkafij5snytuqywyya5gj5r30l3 FOREIGN KEY (book_id) REFERENCES book(id)
);

CREATE TABLE book2tag (
                          id serial4 NOT NULL,
                          book_id int4 NOT NULL,
                          tag_id int4 NOT NULL,
                          CONSTRAINT book2tag_pkey PRIMARY KEY (id),
                          CONSTRAINT fk_book_tag FOREIGN KEY (book_id) REFERENCES book(id),
                          CONSTRAINT fk_tag_book FOREIGN KEY (tag_id) REFERENCES tag(id)
);

CREATE TABLE book2genre (
                            id int4 NOT NULL,
                            book_id serial4 NOT NULL,
                            genre_id int4 NOT NULL,
                            CONSTRAINT book2genre_pkey PRIMARY KEY (book_id),
                            CONSTRAINT fkdyiaf682r8d022a3gi1q16ypw FOREIGN KEY (book_id) REFERENCES book(id),
                            CONSTRAINT fknb5tbib0eo6i1qhmy62b78b3o FOREIGN KEY (genre_id) REFERENCES genres(id)
);

CREATE TABLE book2user (
                           id serial4 NOT NULL,
                           book_id int4 NOT NULL,
                           "time" timestamp NOT NULL,
                           type_id int4 NOT NULL,
                           user_id int4 NOT NULL,
                           CONSTRAINT book2user_pkey PRIMARY KEY (id),
                           CONSTRAINT uk_5c8a5291v1ms4l6h7vt8p3rtp UNIQUE (user_id),
                           CONSTRAINT uk_k2elisr362g0lw42u06xspbyj UNIQUE (book_id),
                           CONSTRAINT fk1i8i82uo8kbv1wepiujenmj7x FOREIGN KEY (user_id) REFERENCES users(id),
                           CONSTRAINT fk7cv34daf9pi5ie147slv010b3 FOREIGN KEY (book_id) REFERENCES book(id)
);

CREATE TABLE book2user_type (
                                id serial4 NOT NULL,
                                code varchar(255) NULL,
                                "name" varchar(255) NULL,
                                CONSTRAINT book2user_type_pkey PRIMARY KEY (id)
);

CREATE TABLE user2role (
                           id serial4 NOT NULL,
                           role_id int4 NOT NULL,
                           user_id int4 NOT NULL,
                           CONSTRAINT fkjt3q42fm2pedw690p8kkdryq6 FOREIGN KEY (user_id) REFERENCES users(id),
                           CONSTRAINT fkxev9jpfj2u0dkxofnqyevkt2 FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE user_contact (
                              id serial4 NOT NULL,
                              approved int2 NOT NULL,
                              code varchar(255) NOT NULL,
                              code_time timestamp NULL,
                              code_trails int4 NULL,
                              contact varchar(255) NOT NULL,
                              "type" varchar(255) NULL,
                              user_id int4 NULL,
                              CONSTRAINT user_contact_pkey PRIMARY KEY (id),
                              CONSTRAINT fkigqfory4r46pqd0sl4csnwp72 FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE book_file (
                                  id serial4 NOT NULL,
                                  hash varchar(255) NOT NULL,
                                  "path" varchar(255) NOT NULL,
                                  type_id int4 NULL,
                                  book_id int4 NULL,
                                  CONSTRAINT book_file_pkey PRIMARY KEY (id),
                                  CONSTRAINT fk9hhnrtr3w54i3cxuv1q6gjdbo FOREIGN KEY (book_id) REFERENCES book(id)
);

CREATE TABLE book_file_type (
                                       id serial4 NOT NULL,
                                       description text NULL,
                                       "name" varchar(255) NOT NULL,
                                       CONSTRAINT book_file_type_pkey PRIMARY KEY (id)
);

CREATE TABLE book_review (
                                    id serial4 NOT NULL,
                                    rating int4 NULL DEFAULT 0,
                                    "text" text NOT NULL,
                                    "time" timestamp NOT NULL,
                                    book_id int4 NULL,
                                    user_id int4 NULL,
                                    CONSTRAINT book_review_pkey PRIMARY KEY (id),
                                    CONSTRAINT fk29oatdl4f30mtg65oxo1nkmjg FOREIGN KEY (book_id) REFERENCES book(id),
                                    CONSTRAINT fkntncp0b191bex8jkm3vy3l13x FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE book_review_like (
                                         id serial4 NOT NULL,
                                         "time" timestamp NOT NULL,
                                         value int2 NOT NULL,
                                         review_id int4 NULL,
                                         user_id int4 NULL,
                                         CONSTRAINT book_review_like_pkey PRIMARY KEY (id),
                                         CONSTRAINT fkbh4qufg71ntnx2v11gy8ldc5f FOREIGN KEY (user_id) REFERENCES users(id),
                                         CONSTRAINT fkl5konyr7ye2i76wv92vf9w6y2 FOREIGN KEY (review_id) REFERENCES book_review(id)
);

CREATE TABLE document (
                                   id serial4 NOT NULL,
                                   paragraph text NOT NULL,
                                   slug varchar(255) NOT NULL,
                                   sort_index int4 NOT NULL DEFAULT 0,
                                   "text" text NOT NULL,
                                   title varchar(255) NOT NULL,
                                   CONSTRAINT document_pkey PRIMARY KEY (id)
);

CREATE TABLE faq (
                            id serial4 NOT NULL,
                            answer text NOT NULL,
                            question varchar(255) NOT NULL,
                            sort_index int4 NOT NULL DEFAULT 0,
                            CONSTRAINT faq_pkey PRIMARY KEY (id)
);

CREATE TABLE file_download (
                                      id serial4 NOT NULL,
                                      book_id int4 NOT NULL,
                                      count int4 NOT NULL DEFAULT 1,
                                      user_id int4 NOT NULL,
                                      CONSTRAINT file_download_pkey PRIMARY KEY (id)
);

CREATE TABLE jwt_black_list (
                                       id serial4 NOT NULL,
                                       jwt_token varchar(600) NULL,
                                       CONSTRAINT jwt_black_list_pkey PRIMARY KEY (id)
);

CREATE TABLE message (
                                id serial4 NOT NULL,
                                email varchar(255) NULL,
                                "name" varchar(255) NULL,
                                subject varchar(255) NOT NULL,
                                "text" text NOT NULL,
                                "time" timestamp NOT NULL,
                                user_id int4 NULL,
                                CONSTRAINT message_pkey PRIMARY KEY (id),
                                CONSTRAINT fkpdrb79dg3bgym7pydlf9k3p1n FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE rating_book (
                                    id serial4 NOT NULL,
                                    five_star int4 NULL,
                                    four_star int4 NULL,
                                    one_star int4 NULL,
                                    three_star int4 NULL,
                                    two_star int4 NULL,
                                    book_id int4 NULL,
                                    CONSTRAINT rating_book_pkey PRIMARY KEY (id),
                                    CONSTRAINT fksac5jk0hnl0m06fa7bj02abcg FOREIGN KEY (book_id) REFERENCES book(id)
);



CREATE TABLE spring_session (
                                       primary_id bpchar(36) NOT NULL,
                                       session_id bpchar(36) NOT NULL,
                                       creation_time int8 NOT NULL,
                                       last_access_time int8 NOT NULL,
                                       max_inactive_interval int4 NOT NULL,
                                       expiry_time int8 NOT NULL,
                                       principal_name varchar(100) NULL,
                                       CONSTRAINT spring_session_pk PRIMARY KEY (primary_id)
);

CREATE UNIQUE INDEX spring_session_ix1 ON spring_session USING btree (session_id);

CREATE INDEX spring_session_ix2 ON spring_session USING btree (expiry_time);

CREATE INDEX spring_session_ix3 ON spring_session USING btree (principal_name);

CREATE TABLE spring_session_attributes (
                                                  session_primary_id bpchar(36) NOT NULL,
                                                  attribute_name varchar(200) NOT NULL,
                                                  attribute_bytes bytea NOT NULL,
                                                  CONSTRAINT spring_session_attributes_pk PRIMARY KEY (session_primary_id, attribute_name),
                                                  CONSTRAINT spring_session_attributes_fk FOREIGN KEY (session_primary_id) REFERENCES spring_session(primary_id) ON DELETE CASCADE
);

CREATE TABLE viewed_books (
                                     id serial4 NOT NULL,
                                     "time" timestamp NULL,
                                     "type" varchar(255) NULL,
                                     book_id int4 NULL,
                                     user_id int4 NULL,
                                     CONSTRAINT viewed_books_pkey PRIMARY KEY (id),
                                     CONSTRAINT fkd3kbofp7y1ra3r9em3e4nt414 FOREIGN KEY (user_id) REFERENCES users(id),
                                     CONSTRAINT fkhei30yltabemu3eyno4jlddhu FOREIGN KEY (book_id) REFERENCES book(id)
);
