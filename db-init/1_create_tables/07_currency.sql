CREATE TABLE Currency (
                          currency_code VARCHAR(3) PRIMARY KEY,
                          eur_conversion_rate NUMERIC(38, 2) NOT NULL,
                          symbol VARCHAR(10) NOT NULL
);