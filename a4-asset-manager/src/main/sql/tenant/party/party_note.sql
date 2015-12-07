CREATE TABLE party_note(
  party_id bigint NOT NULL
 ,note text
 ,CONSTRAINT party_note_party_id_fk FOREIGN KEY(party_id) REFERENCES party(id)
) INHERITS(base);

COMMENT ON TABLE party_note IS 'Unstructured data associated with this party. In a rich text format.';

SELECT ist_pk('party_note');
SELECT ist_bk('party_note', ARRAY['party_id']);
