-- either stop place or quay
CREATE TABLE IF NOT EXISTS stop
(
    pk          bigint                      NOT NULL,
    changed     timestamp without time zone NOT NULL,
    changed_by  character varying(255)      NOT NULL,
    created     timestamp without time zone NOT NULL,
    created_by  character varying(255)      NOT NULL,
    version     bigint                      NOT NULL,
    netex_id    character varying(255)      NOT NULL,
    provider_pk bigint                      NOT NULL
);

ALTER TABLE stop
    DROP CONSTRAINT IF EXISTS stop_pkey;

ALTER TABLE stop
    ADD CONSTRAINT stop_pkey PRIMARY KEY (pk);

ALTER TABLE stop
    DROP CONSTRAINT IF EXISTS stop_provider_fk;

ALTER TABLE stop
    ADD CONSTRAINT stop_provider_fk FOREIGN KEY (provider_pk) REFERENCES provider (pk);

CREATE SEQUENCE IF NOT EXISTS stop_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS flexible_stop_place_stops
(
    flexible_stop_place_pk bigint NOT NULL references flexible_stop_place (pk),
    stops_pk               bigint NOT NULL references stop (pk)
);

ALTER TABLE flexible_stop_place_stops
    DROP CONSTRAINT IF EXISTS flexible_stop_place_stops_pkey;

ALTER TABLE flexible_stop_place_stops
    ADD CONSTRAINT flexible_stop_place_stops_pkey PRIMARY KEY (flexible_stop_place_pk, stops_pk);

ALTER TABLE flexible_stop_place_stops
    DROP CONSTRAINT IF EXISTS flexible_stop_place_stops_flexible_stop_place_fk;

ALTER TABLE flexible_stop_place_stops
    ADD CONSTRAINT flexible_stop_place_stops_flexible_stop_place_fk FOREIGN KEY (flexible_stop_place_pk) references flexible_stop_place (pk);

ALTER TABLE flexible_stop_place_stops
    DROP CONSTRAINT IF EXISTS flexible_stop_place_stops_stop_fk;

ALTER TABLE flexible_stop_place_stops
    ADD CONSTRAINT flexible_stop_place_stops_stop_fk FOREIGN KEY (stops_pk) REFERENCES stop (pk);

ALTER TABLE stop_point_in_journey_pattern
    ADD COLUMN IF NOT EXISTS stop_pk bigint;

ALTER TABLE stop_point_in_journey_pattern
    DROP CONSTRAINT IF EXISTS stop_point_in_journey_pattern_stop_fk;

ALTER TABLE stop_point_in_journey_pattern
    ADD CONSTRAINT stop_point_in_journey_pattern_stop_fk FOREIGN KEY (stop_pk) REFERENCES stop (pk);
