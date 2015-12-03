CREATE TABLE address(
  line1 VARCHAR(64)
 ,line2 VARCHAR(64)
 ,city VARCHAR(64)  NOT NULL
 ,subdivision_code CHAR(2) DEFAULT 'ON'
 ,country_code CHAR(2) DEFAULT 'CA'
 ,postal_code CHAR(7)
,note TEXT
, CONSTRAINT address_province_fk FOREIGN KEY(country_code,  subdivision_code) REFERENCES country_subdivision(country_code, subdivision_code)
) INHERITS(base);

COMMENT ON TABLE address IS 'A location for entities such as party, property, and asset.';

COMMENT ON COLUMN address.line1 IS 'First line of a mailing address, typically the street number and name.';
COMMENT ON COLUMN address.line2 IS 'Second line of a mailing address, if required.';

SELECT ist_pk('address');
SELECT ist_bk('address', ARRAY['line1', 'city', 'subdivision_code']);
