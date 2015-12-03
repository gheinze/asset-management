CREATE TABLE country(
   country_code   CHAR(2) CONSTRAINT country_code_pk PRIMARY KEY
  ,country_name   VARCHAR(64) CONSTRAINT country_country_name_uk UNIQUE
)
;

COMMENT ON TABLE country IS 'ISO 3166-2 Country Code definitions.';

COMMENT ON COLUMN country.country_code IS 'Two letter code representing the country, always upper case (e.g. CA for Canada, US for United States).  See also country_subdivisions.';
COMMENT ON COLUMN country.country_name IS 'English name for the country represented by the country code.';

INSERT INTO country(country_code, country_name) VALUES('CA', 'Canada');
INSERT INTO country(country_code, country_name) VALUES('US', 'United States');
