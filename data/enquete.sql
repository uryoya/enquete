CREATE TABLE enquete (
    id          INTEGER PRIMARY KEY,
    author      VARCHAR(20) NOT NULL,
    title       VARCHAR(100) NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE answer (
    id          INTEGER PRIMARY KEY,
    author      VARCHAR(20) NOT NULL,
    answer       TEXT NOT NULL,
    enquete_id  INTEGER NOT NULL,
    FOREIGN KEY(enquete_id) REFERENCES enquete(id)
);

CREATE TABLE comment (
    id          INTEGER PRIMARY KEY,
    author      VARCHAR(20) NOT NULL,
    comment     TEXT NOT NULL,
    answer_id   INTEGER NOT NULL,
    FOREIGN KEY(answer_id) REFERENCES answer(id)
);
