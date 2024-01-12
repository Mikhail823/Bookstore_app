--liquibase formatted sql
--changeset Mikhail Popov:spring_session

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