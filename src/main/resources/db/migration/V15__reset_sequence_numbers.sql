-- === MAJ SEQUENCE HIBERNATE ===


DO $$
DECLARE
v_max_seq bigint;
v_next bigint;
  v_sql  text;
BEGIN


EXECUTE format('SELECT COALESCE(MAX(pk), 0) + 1 FROM export')
    INTO v_next;



v_sql := format('ALTER SEQUENCE %I RESTART WITH %s', 'export_seq', v_next);
EXECUTE v_sql;


END
$$;


