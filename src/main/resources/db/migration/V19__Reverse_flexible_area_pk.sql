ALTER TABLE flexible_area
    ADD COLUMN IF NOT EXISTS flexible_stop_place_pk bigint;

UPDATE flexible_area AS fa
SET flexible_stop_place_pk = ftp.pk
FROM flexible_stop_place AS ftp
WHERE ftp.flexible_area_pk = fa.pk;

ALTER TABLE flexible_stop_place
    DROP COLUMN IF EXISTS flexible_area_pk;

ALTER TABLE flexible_area
    ADD CONSTRAINT flexible_area_flexible_stop_place_fk
        FOREIGN KEY (flexible_stop_place_pk) references flexible_stop_place (pk);
