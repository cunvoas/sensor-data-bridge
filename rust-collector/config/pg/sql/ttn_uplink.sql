-- Table SQL pour TtnUplinkMessage
CREATE TABLE IF NOT EXISTS ttn_uplink (
    id SERIAL PRIMARY KEY,
    app_id TEXT NOT NULL,
    dev_id TEXT NOT NULL,
    dev_eui TEXT NOT NULL,
    raw_payload JSONB NOT NULL,
    decoded_fields TEXT NOT NULL,
    port INTEGER NOT NULL,
    rssi DOUBLE PRECISION,
    snr DOUBLE PRECISION,
    sf INTEGER
);