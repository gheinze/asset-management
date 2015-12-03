CREATE TABLE party(
  party_name         VARCHAR(64)  NOT NULL
) INHERITS(base);

COMMENT ON TABLE party IS 'A party is an identifier for a person or organization.';

COMMENT ON COLUMN party.party_name IS 'The name to use for display purposes.';

SELECT ist_pk('party');
SELECT ist_bk('party', ARRAY['party_name']);
