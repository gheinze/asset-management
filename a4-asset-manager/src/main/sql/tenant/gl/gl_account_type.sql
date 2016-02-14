CREATE TABLE gl_account_type(
   gl_account_type  varchar(32)
  ,natural_balance  varchar(6)
  ,sort_order       integer
  ,display_name     varchar(256)

  ,CONSTRAINT gl_account_type_natural_bal CHECK (natural_balance IN ('Debit', 'Credit'))

) INHERITS(base);

COMMENT ON TABLE gl_account_type IS
  'All accounts belong to 1 of the 5 accounting types: Assets, Liabilities, '
  'Revenues, Expenses, or Equity.  Optionally, a 6th account type for '
  'Dividends could be defined, but it can also just be consided part of Equity. '
  'This table is used for lookup purposes and the number of records remains static.';

COMMENT ON COLUMN gl_account_type.gl_account_type IS
  'Represents one of the 5 basic accounting categories and is used a foreign '
  'key for referring tables.';

COMMENT ON COLUMN gl_account_type.natural_balance IS
  'Every account type has a natural balance which is either a Debit (left side '
  'entry) or a Credit (right side entry). Assets and Expenses are typically '
  'Debits and Liabilities, Equity, and Revenues are Credits';

COMMENT ON COLUMN gl_account_type.sort_order IS
  'System-wide default property inicating the preferred order of displaying '
  'account information.';

COMMENT ON COLUMN gl_account_type.display_name IS
  'Optionally allows renaming of the account types for reporting purposes. '
  'For example, the term Income may be preferred to Revenue. This column is '
  'not used for referential constraints. If the value of this column is null, '
  'use the default gl_account_type value.';


SELECT ist_pk('gl_account_type');
SELECT ist_bk('gl_account_type', ARRAY['gl_account_type']);


INSERT INTO gl_account_type(gl_account_type, natural_balance, sort_order)
  VALUES ('Asset', 'Debit', 1);

INSERT INTO gl_account_type(gl_account_type, natural_balance, sort_order)
  VALUES ('Liability', 'Credit', 2);

INSERT INTO gl_account_type(gl_account_type, natural_balance, sort_order)
  VALUES ('Equity', 'Credit', 3);

INSERT INTO gl_account_type(gl_account_type, natural_balance, sort_order)
  VALUES ('Revenue', 'Credit', 4);

INSERT INTO gl_account_type(gl_account_type, natural_balance, sort_order)
  VALUES ('Expense', 'Debit', 5);


-- Block further inserts and deletes on table
CREATE RULE gl_account_type_insert_block AS ON INSERT TO gl_account_type DO INSTEAD NOTHING;
CREATE RULE gl_account_type_delete_block AS ON DELETE TO gl_account_type DO INSTEAD NOTHING;

-- Trigger to block any updates of static fields account_type and natural_balance
CREATE OR REPLACE FUNCTION trg_gl_account_type_upd() RETURNS trigger AS $$
BEGIN
    new.gl_account_type := old.gl_account_type;
    new.natural_balance := old.natural_balance;
    RETURN new;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER gl_account_type_upd
  BEFORE UPDATE ON gl_account_type
  FOR EACH ROW EXECUTE PROCEDURE trg_gl_account_type_upd();
