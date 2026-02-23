-- Удаление таблиц если существуют
DROP TABLE IF EXISTS performance_actor CASCADE;
DROP TABLE IF EXISTS performance CASCADE;
DROP TABLE IF EXISTS person CASCADE;
DROP TABLE IF EXISTS theater CASCADE;

-- Создание таблицы театров
CREATE TABLE theater (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(300) NOT NULL,
    seats_parterre INTEGER NOT NULL,
    seats_balcony INTEGER NOT NULL,
    seats_mezzanine INTEGER NOT NULL
);

-- Создание таблицы персон (режиссеры и актеры)
CREATE TABLE person (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('DIRECTOR', 'ACTOR', 'BOTH'))
);

-- Создание таблицы представлений
CREATE TABLE performance (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    theater_id INTEGER NOT NULL,
    director_id INTEGER NOT NULL,
    performance_date DATE NOT NULL,
    performance_time TIME NOT NULL,
    duration_minutes INTEGER NOT NULL,
    price_parterre INTEGER NOT NULL,
    price_balcony INTEGER NOT NULL,
    price_mezzanine INTEGER NOT NULL,
    free_parterre INTEGER NOT NULL,
    free_balcony INTEGER NOT NULL,
    free_mezzanine INTEGER NOT NULL,
    FOREIGN KEY (theater_id) REFERENCES theater(id) ON DELETE CASCADE,
    FOREIGN KEY (director_id) REFERENCES person(id) ON DELETE RESTRICT
);

-- Создание связующей таблицы представление-актер
CREATE TABLE performance_actor (
    performance_id INTEGER NOT NULL,
    actor_id INTEGER NOT NULL,
    PRIMARY KEY (performance_id, actor_id),
    FOREIGN KEY (performance_id) REFERENCES performance(id) ON DELETE CASCADE,
    FOREIGN KEY (actor_id) REFERENCES person(id) ON DELETE RESTRICT
);
