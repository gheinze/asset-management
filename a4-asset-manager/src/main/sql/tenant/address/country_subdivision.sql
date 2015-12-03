CREATE TABLE country_subdivision(
   country_code         CHAR(2)  CONSTRAINT country_subdivision_country_code_fk REFERENCES country(country_code)
  ,subdivision_code     CHAR(2)
  ,subdivision_name     VARCHAR(64)  NOT NULL

  ,CONSTRAINT country_subdivision_pk PRIMARY KEY(country_code, subdivision_code)
  ,CONSTRAINT country_subdivision_uk UNIQUE(country_code, subdivision_name)
);

COMMENT ON TABLE country_subdivision IS 'ISO 3166-2 Country Code subdivision definitions (i.e. provinces and states).';

COMMENT ON COLUMN country_subdivision.country_code IS 'ISO 2 character code for representing a country.';
COMMENT ON COLUMN country_subdivision.subdivision_code IS 'ISO 2 character code for representing a subdivision of a country.';
COMMENT ON COLUMN country_subdivision.subdivision_name IS 'English name representing a country subdivision (i.e. a state or province).';

INSERT INTO country_subdivision VALUES
   ('CA', 'ON', 'Ontario')
  ,('CA', 'QC', 'Quebec')
  ,('CA', 'NS', 'Nova Scotia')
  ,('CA', 'NB', 'New Brunswick')
  ,('CA', 'MB', 'Manitoba')
  ,('CA', 'BC', 'British Columbia')
  ,('CA', 'PE', 'Prince Edward Island')
  ,('CA', 'SK', 'Saskatchewan')
  ,('CA', 'AB', 'Alberta')
  ,('CA', 'NL', 'Newfoundland and Labrador')
  ,('CA', 'NT', 'Northwest Territories')
  ,('CA', 'YT', 'Yukon')
  ,('CA', 'NU', 'Nunavut')
;