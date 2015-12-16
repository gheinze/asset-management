CREATE TABLE country_subdivision(
   country_id         bigint  CONSTRAINT country_subdivision_country_id_fk REFERENCES country(id)
  ,subdivision_code   CHAR(2)
  ,subdivision_name   VARCHAR(64)  NOT NULL
) INHERITS(base)
;

COMMENT ON TABLE country_subdivision IS 'ISO 3166-2 Country Code subdivision definitions (i.e. provinces and states).';

COMMENT ON COLUMN country_subdivision.country_id IS 'A reference to the ISO 2 character code for representing a country.';
COMMENT ON COLUMN country_subdivision.subdivision_code IS 'ISO 2 character code for representing a subdivision of a country.';
COMMENT ON COLUMN country_subdivision.subdivision_name IS 'English name representing a country subdivision (i.e. a state or province).';

SELECT ist_pk('country_subdivision');
SELECT ist_bk('country_subdivision', ARRAY['country_id', 'subdivision_code']);


DO $$
DECLARE

    v_country_id int;

BEGIN

    SELECT id
      INTO v_country_id
      FROM country
      WHERE country_code = 'CA'
    ;

    INSERT INTO country_subdivision(country_id, subdivision_code, subdivision_name) VALUES
       (v_country_id, 'AB', 'Alberta')
      ,(v_country_id, 'BC', 'British Columbia')
      ,(v_country_id, 'MB', 'Manitoba')
      ,(v_country_id, 'NB', 'New Brunswick')
      ,(v_country_id, 'NL', 'Newfoundland and Labrador')
      ,(v_country_id, 'NT', 'Northwest Territories')
      ,(v_country_id, 'NS', 'Nova Scotia')
      ,(v_country_id, 'NU', 'Nunavut')
      ,(v_country_id, 'ON', 'Ontario')
      ,(v_country_id, 'PE', 'Prince Edward Island')
      ,(v_country_id, 'QC', 'Quebec')
      ,(v_country_id, 'SK', 'Saskatchewan')
      ,(v_country_id, 'YT', 'Yukon')
    ;

    SELECT id
      INTO v_country_id
      FROM country
      WHERE country_code = 'US'
    ;

    INSERT INTO country_subdivision(country_id, subdivision_code, subdivision_name) VALUES
       (v_country_id, 'AL', 'Alabama')
      ,(v_country_id, 'CA', 'California')
      ,(v_country_id, 'MI', 'Michigan')
      ,(v_country_id, 'NY', 'New York')
    ;

END
$$;