CREATE OR REPLACE FUNCTION public.set_schema_to(p_schema_name TEXT) RETURNS VOID AS $$
DECLARE

    v_new_search_path TEXT;
    k_default_schema CONSTANT TEXT := 'public';

BEGIN

    -- Three expected use cases:
    --   1. new schema not found -> set search_path=public
    --   2. new schema public  -> set search_path=public
    --   3. new schema found  -> -> set search_path=<new_schema>,public

    SELECT CASE WHEN x.new_schema_name <> 'public' THEN x.new_schema_name || ',public'
                ELSE x.new_schema_name END

      INTO v_new_search_path

      FROM  (
            -- Pull the schema from the catalog and default to public if not found
            SELECT COALESCE(MAX(schema_name), 'public') new_schema_name
               FROM information_schema.schemata
               WHERE schema_name = p_schema_name) x
    ;

     EXECUTE 'SET search_path TO ' || v_new_search_path;

END;
$$LANGUAGE plpgsql;
