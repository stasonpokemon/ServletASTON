create table if not exists users
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    username CHARACTER VARYING(100)                  NOT NULL,
    email    CHARACTER VARYING(100)                  NOT NULL,
    CONSTRAINT user_pk PRIMARY KEY (id)
);

create table if not exists passports
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name       CHARACTER VARYING(100)                  NOT NULL,
    surname    CHARACTER VARYING(100)                  NOT NULL,
    patronymic CHARACTER VARYING(100)                  NOT NULL,
    birthday   CHARACTER VARYING(100)                  NOT NULL,
    address    CHARACTER VARYING(200)                  NOT NULL,
    user_id    BIGINT UNIQUE                           NOT NULL,
    CONSTRAINT passport_pk PRIMARY KEY (id),
    CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE

);

INSERT INTO users(username, email)
VALUES ('stasonpokemon', 'stasonpokemon@icloud.com');
INSERT INTO users(username, email)
VALUES ('test', 'test');
INSERT INTO users(username, email)
VALUES ('test2', 'test2');
INSERT INTO users(username, email)
VALUES ('test3', 'test3');

INSERT INTO passports(name, surname, patronymic, birthday, address, user_id)
VALUES ('Stanislau', 'Trebnikau', 'Andreevich', '13-07-2001', 'Vitebsk', 1);
INSERT INTO passports(name, surname, patronymic, birthday, address, user_id)
VALUES ('test', 'test', 'test', '13-07-2001', 'test', 2);
INSERT INTO passports(name, surname, patronymic, birthday, address, user_id)
VALUES ('test2', 'test2', 'test2', '13-07-2001', 'test2', 3);


truncate table passports restart identity;
truncate table users restart identity cascade ;
select * from passports;
select * from users;
show max_connections ;
SELECT count(*) FROM pg_stat_activity;