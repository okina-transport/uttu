DROP TABLE IF EXISTS job;
DROP SEQUENCE IF EXISTS job_seq;

CREATE SEQUENCE job_seq START 1 INCREMENT 10;

CREATE TABLE IF NOT EXISTS job (
     id          BIGINT PRIMARY KEY DEFAULT nextval('job_seq'),
     started     TIMESTAMP WITH TIME ZONE,
     finished    TIMESTAMP WITH TIME ZONE,
     status      VARCHAR(255),
     type        VARCHAR(255),
     action      VARCHAR(255),
     job_url     VARCHAR(255),
     file_name   VARCHAR(255),
     sub_folder  VARCHAR(255),
     message     VARCHAR(255),
     user_name   VARCHAR(255),
     provider    VARCHAR(255)
);