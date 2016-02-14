CREATE TABLE gl_transaction(
   transaction_date date
  ,description varchar(256)
) INHERITS(BASE);

SELECT ist_pk('gl_transaction');


COMMENT ON TABLE gl_transaction IS
  'A balanced entry into the accounting system which will consist of postings '
  'to two or more gl accounts';

COMMENT ON COLUMN gl_transaction.transaction_date IS
  'The date the transaction occurred (not the necessarily the date entered).';

COMMENT ON COLUMN gl_transaction.description IS
  'In the case of an expense, this would be the service provider. For income '
  'it would be the income source.';


