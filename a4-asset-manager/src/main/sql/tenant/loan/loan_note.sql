CREATE TABLE loan_note(
  loan_id bigint NOT NULL
 ,note text
 ,CONSTRAINT loan_note_loan_id_fk FOREIGN KEY(loan_id) REFERENCES loan(id)
) INHERITS(base);

COMMENT ON TABLE loan_note IS 'Unstructured data associated with this loan. In a rich text format.';

SELECT ist_pk('loan_note');
SELECT ist_bk('loan_note', ARRAY['loan_id']);
