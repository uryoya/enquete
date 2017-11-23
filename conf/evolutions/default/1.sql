# --- !Ups
CREATE TABLE `user` (
  id          INTEGER PRIMARY KEY,
  name        TEXT NOT NULL,
  icon        BLOB,
  access_token    TEXT NOT NULL,
  admin       INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE pending_user (
  id          INTEGER PRIMARY KEY,
  name        TEXT NOT NULL,
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
  FOREIGN KEY(answer_id) REFERENCES answer(id),
  FOREIGN KEY(user_id) REFERENCES user(id)
);

# --- !Downs
DROP TABLE user;
DROP TABLE pending_user;
DROP TABLE enquete;
DROP TABLE answer;
DROP TABLE comment;
