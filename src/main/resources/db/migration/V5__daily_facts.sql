CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE daily_fact (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                            content TEXT NOT NULL,
                            category TEXT NOT NULL,

                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);