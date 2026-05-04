ALTER TABLE destination_display
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE destination_display
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);