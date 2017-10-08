CREATE TABLE user (
    id          INTEGER PRIMARY KEY,
    name        TEXT NOT NULL,
    icon        BLOB,
    access_token    TEXT NOT NULL
);

CREATE TABLE enquete (
    id          INTEGER PRIMARY KEY,
    title       VARCHAR(100) NOT NULL,
    user_id     INTEGER NOT NULL,
    description TEXT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES user(id)
);

CREATE TABLE answer (
    id          INTEGER PRIMARY KEY,
    answer      TEXT NOT NULL,
    enquete_id  INTEGER NOT NULL,
    user_id     INTEGER NOT NULL,
    FOREIGN KEY(enquete_id) REFERENCES enquete(id),
    FOREIGN KEY(user_id) REFERENCES user(id)
);

CREATE TABLE comment (
    id          INTEGER PRIMARY KEY,
    comment     TEXT NOT NULL,
    answer_id   INTEGER NOT NULL,
    user_id     INTEGER NOT NULL,
    FOREIGN KEY(answer_id) REFERENCES answer(id)
    FOREIGN KEY(user_id) REFERENCES user(id)
);
