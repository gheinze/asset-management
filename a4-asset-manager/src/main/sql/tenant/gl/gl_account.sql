CREATE TABLE gl_account(
   gl_account_type_id bigint
  ,name             varchar(64)
  ,currency_code    char(3)
  ,active_from      date
  ,active_to        date
  ,balance          numeric DEFAULT 0
  ,description      varchar(256)

  ,CONSTRAINT gl_account_gl_account_type_fk FOREIGN KEY(gl_account_type_id)
     REFERENCES gl_account_type(id)

) INHERITS(base);

SELECT ist_pk('gl_account');
SELECT ist_bk('gl_account', ARRAY['gl_account_type_id', 'name']);

COMMENT ON TABLE gl_account IS
  'A container in the accounting sub-system which groups together a set of '
  'transactions into a logical group which can provide useful business '
  'information.';

COMMENT ON COLUMN gl_account.name IS
  'The name given to a container. It may be modified, but it must be unique '
  'across all accounts of a given account_type.';

COMMENT ON COLUMN gl_account.gl_account_type_id IS
  'Every account belongs to one of the 5 basic accounting categories: Assets, '
  'Liabilities, Equity, Revenues, or Expenses. Reference to the account type.';

COMMENT ON COLUMN gl_account.currency_code IS
  'The ISO 4217 alphabetic currency code of the amounts represented within '
  'account.  See http://www.iso.org/iso/currency_codes_list-1 for a list of '
  'codes. Not that all currency amounts are stored as integer values and need '
  'to be scaled by the application.';

COMMENT ON COLUMN gl_account.active_from IS
  'No transactions can be recorded into this account if the transaction date '
  'precedes this date. A NULL value indicates an unrestricted from date.';

COMMENT ON COLUMN gl_account.active_to IS
  'No transactions can be recorded into this account if the transaction date '
  'follows this date. This can be used to close an account. A NULL value '
  'indicates an unbounded end date.';

COMMENT ON COLUMN gl_account.balance IS
  'A sum of all the transactions within the account. This is a de-normalized '
  'field created to speed up account operations. Integerity should be checked '
  'regularly.';

COMMENT ON COLUMN gl_account.description IS
  'The purpose of this account.';
