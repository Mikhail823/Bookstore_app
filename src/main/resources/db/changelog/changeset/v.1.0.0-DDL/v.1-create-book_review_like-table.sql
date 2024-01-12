--liquibase formatted sql
--changeset Mikhail Popov:book_review_like

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