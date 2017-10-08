CREATE TABLE user (
    id          INTEGER PRIMARY KEY,
    name        TEXT NOT NULL,
    icon        BLOB,
    access_token    TEXT NOT NULL
);

CREATE TABLE enquete (
    id          INTEGER PRIMARY KEY,
    title       VARCHAR(100) NOT NULL,
    user        VARCHAR(20) NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE answer (
    id          INTEGER PRIMARY KEY,
    user        VARCHAR(20) NOT NULL,
    answer      TEXT NOT NULL,
    enquete_id  INTEGER NOT NULL,
    FOREIGN KEY(enquete_id) REFERENCES enquete(id)
);

CREATE TABLE comment (
    id          INTEGER PRIMARY KEY,
    user        VARCHAR(20) NOT NULL,
    comment     TEXT NOT NULL,
    answer_id   INTEGER NOT NULL,
    FOREIGN KEY(answer_id) REFERENCES answer(id)
);
