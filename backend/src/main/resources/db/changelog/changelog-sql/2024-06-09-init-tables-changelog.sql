-- changeset dev:1
CREATE TABLE IF NOT EXISTS users
(
    id                 UUID        DEFAULT GEN_RANDOM_UUID() PRIMARY KEY,
    name               VARCHAR(255),
    email              VARCHAR(255) UNIQUE,
    date_of_birth      DATE,
    place_of_birth     VARCHAR(255),
    mother_maiden_name VARCHAR(255),
    taj                CHAR(9),
    tax_id             CHAR(10)
);

-- changeset dev:2
CREATE TABLE IF NOT EXISTS addresses
(
    id      UUID DEFAULT GEN_RANDOM_UUID() PRIMARY KEY,
    user_id UUID,
    address VARCHAR(255),
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- changeset dev:3
CREATE TABLE IF NOT EXISTS phone_numbers
(
    id           UUID DEFAULT GEN_RANDOM_UUID() PRIMARY KEY,
    user_id      UUID,
    phone_number VARCHAR(255),
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

