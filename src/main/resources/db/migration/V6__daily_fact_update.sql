-- 1. Change category type
ALTER TABLE daily_fact
ALTER COLUMN category TYPE VARCHAR(100);

-- 2. Add fact_date  NOT NULL (no data to break)
ALTER TABLE daily_fact
    ADD COLUMN fact_date DATE NOT NULL;

-- 3. Add unique constraint (one fact per day)
ALTER TABLE daily_fact
    ADD CONSTRAINT uq_daily_fact_date UNIQUE (fact_date);

-- 4. Rename PK (optional, just for naming consistency)
ALTER TABLE daily_fact
    RENAME CONSTRAINT daily_fact_pkey TO pk_daily_fact;

-- 5. Remove DEFAULT from created_at (since Java handles it now)
ALTER TABLE daily_fact
    ALTER COLUMN created_at DROP DEFAULT;