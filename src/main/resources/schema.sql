CREATE TABLE IF NOT EXISTS users (
   user_id BIGSERIAL PRIMARY KEY,
   username VARCHAR(255) NOT NULL,
   password VARCHAR(255) NOT NULL

);

CREATE TABLE IF NOT EXISTS cards (
    card_id UUID PRIMARY KEY,
    user_id BIGINT REFERENCES users(user_id),
    status VARCHAR(20),
    balance BIGINT,
    currency VARCHAR(20)
    );

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id UUID PRIMARY KEY,
    card_id UUID REFERENCES cards(card_id),
    amount BIGINT,
    type VARCHAR(20),
    external_id VARCHAR(255),
    currency VARCHAR(20)
    );
