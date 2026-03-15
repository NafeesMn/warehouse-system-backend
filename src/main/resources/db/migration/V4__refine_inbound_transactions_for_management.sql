ALTER TABLE inbound_transactions
    ADD COLUMN IF NOT EXISTS stock_after_update INTEGER NOT NULL DEFAULT 0;

CREATE INDEX IF NOT EXISTS idx_inbound_received_date ON inbound_transactions (received_date DESC);