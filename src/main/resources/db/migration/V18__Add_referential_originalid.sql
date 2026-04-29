ALTER TABLE booking_arrangement
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE booking_arrangement
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE codespace
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE codespace
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE contact
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE contact
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE day_type
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE day_type
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE day_type_assignment
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE day_type_assignment
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE export
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE export
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE flexible_area
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE flexible_area
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE flexible_stop_place
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE flexible_stop_place
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE hail_and_ride_area
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE hail_and_ride_area
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE journey_pattern
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE journey_pattern
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE line
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE line
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE network
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE network
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE notice
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE notice
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE operating_period
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE operating_period
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE persistable_polygon
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE persistable_polygon
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE provider
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE provider
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE service_journey
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE service_journey
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE stop
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE stop
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE stop_point_in_journey_pattern
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE stop_point_in_journey_pattern
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);

ALTER TABLE timetabled_passing_time
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(255);

ALTER TABLE timetabled_passing_time
    ADD COLUMN IF NOT EXISTS dataset_id VARCHAR(255);
