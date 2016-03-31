CREATE TABLE loan(
  loan_name         VARCHAR(64)  NOT NULL
 ,close_date        DATE DEFAULT 'infinity'
) INHERITS(base);

COMMENT ON TABLE loan IS 'A party is an identifier for a person or organization.';

COMMENT ON COLUMN loan.loan_name IS 'The name to use for display purposes. Recommended format "YYYY-MM-DD Borrower Name"';
COMMENT ON COLUMN loan.close_date IS 'Date from which loan is terminated and no longer accruing interest.';

SELECT ist_pk('loan');
SELECT ist_bk('loan', ARRAY['loan_name']);
