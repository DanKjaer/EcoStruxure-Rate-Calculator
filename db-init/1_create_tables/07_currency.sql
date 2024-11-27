CREATE TABLE Currency (
                          currency_code VARCHAR(3) PRIMARY KEY,
                          eur_conversion_rate NUMERIC(15, 6) NOT NULL,
                          symbol VARCHAR(10) NOT NULL
);