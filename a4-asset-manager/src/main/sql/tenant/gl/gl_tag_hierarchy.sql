CREATE TABLE gl_tag_hierarchy(
   gl_account_type_id bigint
  ,name            varchar(64)
  ,description     varchar(256)

  ,CONSTRAINT gl_tag_hier_gl_account_type_fk FOREIGN KEY(gl_account_type_id)
     REFERENCES gl_account_type(id)

) INHERITS(base);

SELECT ist_pk('gl_tag_hierarchy');
SELECT ist_bk('gl_tag_hierarchy', ARRAY['gl_account_type_id', 'name']);


COMMENT ON TABLE gl_tag_hierarchy IS
  'A hiearchy can be used to display accounts in a subaccount fashion. No '
  'accounts are true subaccounts, but a the listing of the accounts can '
  'be presented in a hierachical format depending on the tag hierarchy selected';

COMMENT ON COLUMN gl_tag_hierarchy.name IS
  'A unique identifier for the hierarchy definition.';

COMMENT ON COLUMN gl_tag_hierarchy.gl_account_type_id IS
  'A reference to the account type to which the hierarchy may be applied.';

COMMENT ON COLUMN gl_tag_hierarchy.description IS
  'An optional description of the purpose for the hierarchy.';


