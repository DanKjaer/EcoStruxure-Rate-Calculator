CREATE TABLE IF NOT EXISTS users (
                                     user_id UUID PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL,
                                     password VARCHAR(255) NOT NULL
);
