CREATE TABLE loan(
  loan_name         VARCHAR(64)  NOT NULL
) INHERITS(base);

COMMENT ON TABLE loan IS 'A party is an identifier for a person or organization.';

COMMENT ON COLUMN loan.loan_name IS 'The name to use for display purposes. Recommended format "YYYY-MM-DD Borrower Name"';

SELECT ist_pk('loan');
SELECT ist_bk('loan', ARRAY['loan_name']);
