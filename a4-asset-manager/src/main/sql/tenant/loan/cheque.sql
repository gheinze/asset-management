CREATE TABLE cheque(
  loan_id          bigint  NOT NULL
 ,document_type_id bigint  NOT NULL
 ,post_date        date    NOT NULL
 ,currency         char(3) NOT NULL
 ,amount           numeric NOT NULL
 ,document_status_id bigint NOT NULL
 ,reference        VARCHAR(64)
 ,note             text

 ,CONSTRAINT cheque_loan_id_fk FOREIGN KEY(loan_id) REFERENCES loan(id)
 ,CONSTRAINT cheque_document_type_id_fk FOREIGN KEY(document_type_id) REFERENCES payment_document_type(id)
 ,CONSTRAINT cheque_document_status_id_fk FOREIGN KEY(document_status_id) REFERENCES payment_document_status(id)

) INHERITS(base);

COMMENT ON TABLE cheque IS 'A form of payment for future deposit (ex a post-dated cheque, draft, money order, etc) for the loan.';

COMMENT ON COLUMN cheque.loan_id IS 'The loan to which this cheque is meant to be applied.';
COMMENT ON COLUMN cheque.document_type_id IS 'A reference to the type of the payment document (cheque, draft, etc)';
COMMENT ON COLUMN cheque.post_date IS 'The date this payment may be deposited.';
COMMENT ON COLUMN cheque.currency IS 'The ISO 4217 3-character currency code of this cheque. Should be the same as that of the associated loan.';
COMMENT ON COLUMN cheque.amount IS 'The amount of the cheque.';
COMMENT ON COLUMN cheque.document_status_id IS 'A reference to an indicator if the cheque is on file, ready to be deposited, has already been deposited, or voided. Cheques "ON FILE" will be used as suggestions for deposit lists.';
COMMENT ON COLUMN cheque.reference IS 'The reference number of the cheque ';
COMMENT ON COLUMN cheque.note IS 'Any custom information to record about this payment document.';

SELECT ist_pk('cheque');
SELECT ist_bk('cheque', ARRAY['loan_id', 'post_date', 'reference']);
