CREATE TABLE gl_transaction_detail(
   transaction_id   bigint
  ,sort_order       integer
  ,gl_account_id    bigint
  ,debit_credit     varchar(6)
  ,amount           bigint
  ,description      varchar(256)

  ,CONSTRAINT gl_transaction_detail_transaction_fk FOREIGN KEY(transaction_id)
     REFERENCES gl_transaction(id)
  ,CONSTRAINT gl_transaction_detail_gl_account_fk FOREIGN KEY(gl_account_id)
     REFERENCES gl_account(id)

) INHERITS(BASE);

SELECT ist_pk('gl_transaction_detail');
SELECT ist_bk('gl_transaction_detail', ARRAY['transaction_id']);


COMMENT ON TABLE gl_transaction_detail IS
  'Represents an amount debited or credited to a gl account.';

COMMENT ON COLUMN gl_transaction_detail.transaction_id IS
  'The overall transaction of which this journal entry is a part.';

COMMENT ON COLUMN gl_transaction_detail.sort_order IS
  'A user defined sort order to apply when displaying transactions details of '
  'a given transaction.';

COMMENT ON COLUMN gl_transaction_detail.gl_account_id IS
  'A reference to the account this journal entry will be posted to.';

COMMENT ON COLUMN gl_transaction_detail.debit_credit IS
  'Is this a Debit entry or a Credit entry to the account?';

COMMENT ON COLUMN gl_transaction_detail.amount IS
  'The amount to post to the account. Amounts should be scaled according to the currency.';

COMMENT ON COLUMN gl_transaction_detail.description IS
  'In the case of an expense, this would be the service provider. For income '
  'it would be the income source.';


