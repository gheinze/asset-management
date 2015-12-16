CREATE TABLE address(
  line1 VARCHAR(64)
 ,line2 VARCHAR(64)
 ,city VARCHAR(64)  NOT NULL
 ,country_id  bigint
 ,country_subdivision_id bigint
 ,postal_code CHAR(7)
 ,note TEXT
 ,CONSTRAINT address_country_fk FOREIGN KEY(country_id) REFERENCES country(id)
 ,CONSTRAINT address_country_subdivision_fk FOREIGN KEY(country_subdivision_id) REFERENCES country_subdivision(id)
) INHERITS(base);

COMMENT ON TABLE address IS 'A location for entities such as party, property, and asset.';

COMMENT ON COLUMN address.line1 IS 'First line of a mailing address, typically the street number and name.';
COMMENT ON COLUMN address.line2 IS 'Second line of a mailing address, if required.';

SELECT ist_pk('address');
SELECT ist_bk('address', ARRAY['line1', 'city', 'country_id', 'country_subdivision_id']);
