-- Удаление таблиц если существуют
DROP TABLE IF EXISTS play_actor CASCADE;
DROP TABLE IF EXISTS session CASCADE;
DROP TABLE IF EXISTS play CASCADE;
DROP TABLE IF EXISTS person CASCADE;
DROP TABLE IF EXISTS theater CASCADE;

-- Создание таблицы театров
CREATE TABLE theater (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(300) NOT NULL,
    seats_parterre INTEGER NOT NULL,
    seats_balcony INTEGER NOT NULL,
    seats_mezzanine INTEGER NOT NULL
);

-- Создание таблицы персон (режиссеры и актеры)
CREATE TABLE person (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('DIRECTOR', 'ACTOR', 'BOTH'))
);

-- Создание таблицы спектаклей
CREATE TABLE play (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    theater_id BIGINT NOT NULL,
    director_id BIGINT NOT NULL,
    duration_minutes INTEGER NOT NULL,
    price_parterre INTEGER NOT NULL,
    price_balcony INTEGER NOT NULL,
    price_mezzanine INTEGER NOT NULL,
    FOREIGN KEY (theater_id) REFERENCES theater(id) ON DELETE CASCADE,
    FOREIGN KEY (director_id) REFERENCES person(id) ON DELETE RESTRICT
);

-- Создание таблицы сеансов
CREATE TABLE session (
    id BIGSERIAL PRIMARY KEY,
    play_id BIGINT NOT NULL,
    session_date DATE NOT NULL,
    session_time TIME NOT NULL,
    free_parterre INTEGER NOT NULL,
    free_balcony INTEGER NOT NULL,
    free_mezzanine INTEGER NOT NULL,
    FOREIGN KEY (play_id) REFERENCES play(id) ON DELETE CASCADE
);

-- Создание связующей таблицы спектакль-актер
CREATE TABLE play_actor (
    play_id BIGINT NOT NULL,
    actor_id BIGINT NOT NULL,
    PRIMARY KEY (play_id, actor_id),
    FOREIGN KEY (play_id) REFERENCES play(id) ON DELETE CASCADE,
    FOREIGN KEY (actor_id) REFERENCES person(id) ON DELETE RESTRICT
);
