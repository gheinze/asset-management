CREATE TABLE payment_document_status(
  document_status  VARCHAR(64) NOT NULL
 ,description      text

 ,CONSTRAINT payment_document_status_uk UNIQUE (document_status)

) INHERITS(base);

COMMENT ON TABLE payment_document_status IS 'A list of allowable values for types of payments (cheque, draft, etc.) Doesn''t support internationalization at this stage.';
COMMENT ON COLUMN payment_document_status.document_status IS 'The type of the reference document (on file, deposited, void, etc)';

SELECT ist_pk('payment_document_status');
SELECT ist_bk('payment_document_status', ARRAY['document_status']);

INSERT INTO payment_document_status(document_status) VALUES ('On File');
INSERT INTO payment_document_status(document_status) VALUES ('Deposited');
INSERT INTO payment_document_status(document_status) VALUES ('Void');
INSERT INTO payment_document_status(document_status) VALUES ('Other');
