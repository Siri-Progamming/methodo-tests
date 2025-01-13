CREATE TABLE books (
                                    id INTEGER GENERATED ALWAYS  AS IDENTITY PRIMARY KEY,
                                    title VARCHAR(255) NOT NULL,
                                    author VARCHAR(255) NOT NULL
    );
