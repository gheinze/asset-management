CREATE TABLE payment_document_type(
  document_type    VARCHAR(64) NOT NULL
 ,description      text

 ,CONSTRAINT payment_document_type_uk UNIQUE (document_type)

) INHERITS(base);

COMMENT ON TABLE payment_document_type IS 'A list of allowable values for types of payments (cheque, draft, etc.) Doesn''t support internationalization at this stage.';
COMMENT ON COLUMN payment_document_type.document_type IS 'The type of the reference document (cheque, draft, etc)';

SELECT ist_pk('payment_document_type');
SELECT ist_bk('payment_document_type', ARRAY['document_type']);

INSERT INTO payment_document_type(document_type) VALUES ('Cheque');
INSERT INTO payment_document_type(document_type) VALUES ('Draft');
INSERT INTO payment_document_type(document_type) VALUES ('Money order');
INSERT INTO payment_document_type(document_type) VALUES ('Other');
