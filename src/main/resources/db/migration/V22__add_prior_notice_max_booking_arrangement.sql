ALTER TABLE booking_arrangement ADD COLUMN IF NOT EXISTS prior_notice_duration_max int;
ALTER TABLE booking_arrangement ADD COLUMN IF NOT EXISTS prior_notice_last_day int;
ALTER TABLE booking_arrangement ADD COLUMN IF NOT EXISTS prior_notice_start_day int;
ALTER TABLE booking_arrangement ADD COLUMN IF NOT EXISTS prior_notice_start_time timestamp without time zone;
ALTER TABLE booking_arrangement ADD COLUMN IF NOT EXISTS service_id character varying(255);

