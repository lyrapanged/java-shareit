DROP TABLE IF EXISTS users,items,comments,bookings,requests CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id        BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(30)        NOT NULL,
    email     VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGSERIAL PRIMARY KEY,
    description  VARCHAR(200)                NOT NULL,
    requester_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGSERIAL PRIMARY KEY,
    item_name    VARCHAR(30)  NOT NULL,
    description  VARCHAR(200) NOT NULL,
    is_available BOOLEAN      NOT NULL,
    owner_id     BIGINT REFERENCES users (id) ON DELETE CASCADE,
    request_id   BIGINT REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id           BIGSERIAL PRIMARY KEY,
    comment_text VARCHAR(200)                NOT NULL,
    author_id    BIGINT REFERENCES users (id) ON DELETE CASCADE,
    item_id      BIGINT REFERENCES items (id) ON DELETE CASCADE,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGSERIAL PRIMARY KEY,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id    BIGINT REFERENCES items (id) ON DELETE CASCADE,
    booker_id  BIGINT REFERENCES users (id) ON DELETE CASCADE,
    status     VARCHAR(50)
);
