CREATE DATABASE IF NOT EXISTS methodo;

\c methodo;

CREATE TABLE IF NOT EXISTS methodo.book (
                                    id BIGSERIAL PRIMARY KEY,
                                    title VARCHAR(255) NOT NULL,
                                    author VARCHAR(255) NOT NULL
    );