CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    balance NUMERIC(19, 4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_accounts_account_number ON accounts(account_number);

INSERT INTO accounts(id, account_number, balance, currency, version)
VALUES
('11111111-1111-1111-1111-111111111111', 'ACC-1001', 10000.0000, 'AED', 0),
('22222222-2222-2222-2222-222222222222', 'ACC-2001', 5000.0000, 'AED', 0);
