CREATE TABLE gl_account_gl_tag_map(
   gl_account_id      bigint
  ,gl_account_tag_id  bigint

  ,CONSTRAINT gl_account_gl_tag_map_account_fk FOREIGN KEY(gl_account_id)
     REFERENCES gl_account(id)
  ,CONSTRAINT gl_account_gl_tag_map_tag_fk FOREIGN KEY(gl_account_tag_id)
     REFERENCES gl_account_tag(id)
);

SELECT ist_bk('gl_account_gl_tag_map', ARRAY['gl_account_id', 'gl_account_tag_id']);


COMMENT ON TABLE gl_account_gl_tag_map IS
  'A mapping of tags applied to an account.';

COMMENT ON COLUMN gl_account_gl_tag_map.gl_account_id IS
  'A reference to the account to which the tag is applied.';

COMMENT ON COLUMN gl_account_gl_tag_map.gl_account_tag_id IS
  'A reference to the tag associated with the account.';
