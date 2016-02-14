CREATE TABLE gl_tag_hierarchy_tag_map(
   gl_tag_hierarchy_id  bigint
  ,gl_account_tag_id    bigint
  ,sort_order           integer

  ,CONSTRAINT gl_tag_hierarchy_id_fk FOREIGN KEY(gl_tag_hierarchy_id)
     REFERENCES gl_tag_hierarchy(id)
  ,CONSTRAINT gl_tag_hierarchy_tag_fk FOREIGN KEY(gl_account_tag_id)
     REFERENCES gl_account_tag(id)

);

SELECT ist_bk('gl_tag_hierarchy_tag_map', ARRAY['gl_tag_hierarchy_id', 'gl_account_tag_id']);


COMMENT ON TABLE gl_tag_hierarchy_tag_map IS
  'A priority listing of the order in which accounts should be displayed in'
  'a hierarchical fashion.  A depth-first-search through the accounts in the '
  'order specified in the hierarchy.';

COMMENT ON COLUMN gl_tag_hierarchy_tag_map.gl_tag_hierarchy_id IS
  'The hierarchy definition to which this record belongs.';

COMMENT ON COLUMN gl_tag_hierarchy_tag_map.gl_account_tag_id IS
  'A reference to the tag to add to the hierarchy.';

COMMENT ON COLUMN gl_tag_hierarchy_tag_map.sort_order IS
  'The order of preference when scanning the hierarchy.';
