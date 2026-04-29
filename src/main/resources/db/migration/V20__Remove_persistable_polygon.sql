ALTER TABLE flexible_area
    ADD COLUMN polygon geometry;

UPDATE flexible_area fa
SET polygon = pp.polygon
FROM persistable_polygon pp
WHERE pp.id = fa.polygon_id;

ALTER TABLE flexible_area
    DROP COLUMN IF EXISTS polygon_id;
DROP TABLE IF EXISTS persistable_polygon;
DROP SEQUENCE IF EXISTS persistable_polygon_seq;
