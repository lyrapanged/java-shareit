TRUNCATE TABLE users,items,comments,bookings CASCADE ;
ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE items_id_seq RESTART WITH 1;
ALTER SEQUENCE comments_id_seq RESTART WITH 1;
ALTER SEQUENCE bookings_id_seq RESTART WITH 1;
CREATE TABLE IF NOT EXISTS users
(
    id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(30) NOT NULL ,
    email VARCHAR(50) UNIQUE NOT NULL
    );

CREATE TABLE IF NOT EXISTS items
(
    id BIGSERIAL PRIMARY KEY,
    item_name VARCHAR(30) NOT NULL ,
    description VARCHAR(200) NOT NULL ,
    is_available BOOLEAN NOT NULL ,
    owner_id BIGINT REFERENCES users(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS comments
(
    id BIGSERIAL PRIMARY KEY ,
    comment_text VARCHAR(200) NOT NULL ,
    author_id BIGINT REFERENCES users(id) ON DELETE CASCADE ,
    item_id BIGINT REFERENCES items(id) ON DELETE CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL
    );

CREATE TABLE IF NOT EXISTS bookings
(
    id BIGSERIAL PRIMARY KEY ,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT REFERENCES items(id) ON DELETE CASCADE,
    booker_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(50)
    );
