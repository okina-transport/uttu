ALTER TABLE job ADD COLUMN IF NOT EXISTS correlation_id character varying(255);
ALTER TABLE job ALTER COLUMN message TYPE character varying(2000);
CREATE INDEX IF NOT EXISTS job_correlation_id_idx ON job (correlation_id);
