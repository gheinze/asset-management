CREATE TABLE gl_account_tag(
   name      varchar(64)
  ,asset     boolean DEFAULT false
  ,liability boolean DEFAULT false
  ,equity    boolean DEFAULT false
  ,revenue   boolean DEFAULT false
  ,expense   boolean DEFAULT false
  ,description  varchar(256)
) INHERITS(base);

SELECT ist_pk('gl_account_tag');
-- Enables FK references
SELECT ist_bk('gl_account_tag', ARRAY['name']);
-- Case-insensitive duplicate protection
CREATE UNIQUE INDEX gl_account_tag_name_lc ON gl_account_tag(LOWER(name));


COMMENT ON TABLE gl_account_tag IS
  'A gl_account_tag can be used to categorize an account. Rather than creating '
  'static account hierarchies, tagging accounts with different labels allows '
  'for dynamic hierarchies to be created.';

COMMENT ON COLUMN gl_account_tag.name IS
  'The name of the tag which will also act as the name of a container account '
  'in an account hierarchy.';

COMMENT ON COLUMN gl_account_tag.description IS
  'The purpose for the tag.';

COMMENT ON COLUMN gl_account_tag.asset IS
  'True if this tag is associated with Asset accounts.';

COMMENT ON COLUMN gl_account_tag.liability IS
  'True if this tag is associated with Liability accounts.';

COMMENT ON COLUMN gl_account_tag.equity IS
  'True if this tag is associated with Equity accounts.';

COMMENT ON COLUMN gl_account_tag.revenue IS
  'True if this tag is associated with Revenue accounts.';

COMMENT ON COLUMN gl_account_tag.expense IS
  'True if this tag is associated with Expense accounts.';



-- System defined Asset tags

INSERT INTO gl_account_tag(name, description, asset)
  VALUES('Receivable', 'Accounts Receivable', TRUE);

INSERT INTO gl_account_tag(name, description, asset)
  VALUES('Prepaid', 'Prepaid', TRUE);

INSERT INTO gl_account_tag(name, description, asset)
  VALUES('Accumulated Depreciation', 'Accumulated Depreciation', TRUE);

INSERT INTO gl_account_tag(name, description, asset)
  VALUES('Current', 'Current', TRUE);


-- System defined Liability tags

INSERT INTO gl_account_tag(name, description, liability)
  VALUES('Payable', 'Accounts Payable', TRUE);

INSERT INTO gl_account_tag(name, description, liability)
  VALUES('Unearned', 'Unearned', TRUE);

INSERT INTO gl_account_tag(name, description, liability)
  VALUES('Tax Federal', 'Tax Federal', TRUE);

INSERT INTO gl_account_tag(name, description, liability)
  VALUES('HST', 'Harmonized Sales Tax', TRUE);


-- System defined Equity tags

INSERT INTO gl_account_tag(name, description, equity)
  VALUES('Retained Earnings', 'Retained Earnings', TRUE);


-- System defined Income tags

INSERT INTO gl_account_tag(name, description, revenue)
  VALUES('Interest', 'Interest', TRUE);

INSERT INTO gl_account_tag(name, description, revenue)
  VALUES('NSF', 'Non-Sufficient Funds', TRUE);

INSERT INTO gl_account_tag(name, description, revenue)
  VALUES('Inspection Fee', 'Fees collected for property inspection', TRUE);

INSERT INTO gl_account_tag(name, description, revenue)
  VALUES('Fees -other', 'Fees collected for other purposes', TRUE);


-- Multi-type tags

INSERT INTO gl_account_tag(name, description, asset, revenue, expense)
  VALUES('Loan', 'Loan', TRUE, TRUE, TRUE);

