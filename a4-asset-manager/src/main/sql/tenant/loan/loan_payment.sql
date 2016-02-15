CREATE TABLE loan_payment(
  loan_id          bigint  NOT NULL
 ,cheque_id        bigint
 ,currency         char(3) NOT NULL
 ,amount           numeric NOT NULL
 ,deposit_date     date    NOT NULL
 ,note             text

 ,CONSTRAINT loan_payment_loan_id_fk FOREIGN KEY(loan_id) REFERENCES loan(id)
 ,CONSTRAINT loan_payment_cheque_id_fk FOREIGN KEY(cheque_id) REFERENCES cheque(id)

) INHERITS(base);

COMMENT ON TABLE loan_payment IS 'A payment applied towards a loan.';
COMMENT ON COLUMN loan_payment.currency IS 'The ISO 4217 3-character currency code for the payment.';
COMMENT ON COLUMN loan_payment.amount IS 'The amount applied towards the loan.';
COMMENT ON COLUMN loan_payment.deposit_date IS 'The date at which the payment was deposited towards the loan.';
COMMENT ON COLUMN loan_payment.cheque_id IS 'Optionally, the cheque/payment document that was used for the payment.';
COMMENT ON COLUMN loan_payment.note IS 'If a cheque_id was not associated with the payment, a description could go here.';

SELECT ist_pk('loan_payment');
SELECT ist_bk('loan_payment', ARRAY['loan_id', 'cheque_id']);




CREATE OR REPLACE FUNCTION trigger_payment_cheque_restore() RETURNS trigger AS $$
DECLARE

    v_on_file_document_status_id payment_document_status.id%TYPE;

BEGIN

    IF (tg_op = 'DELETE' AND OLD.cheque_id IS NULL) THEN
        RETURN OLD;
    END IF;

    IF (tg_op = 'UPDATE' AND NEW.cheque_id IS NOT NULL AND NEW.cheque_id = OLD.cheque_id) THEN
        RETURN NEW;
    END IF;

    SELECT id
      INTO v_on_file_document_status_id
      FROM payment_document_status
      WHERE document_status = 'On File';

    UPDATE cheque
      SET document_status_id = v_on_file_document_status_id
      WHERE id = OLD.cheque_id;

    IF (tg_op = 'DELETE') THEN
        RETURN OLD;
    END IF;

    RETURN NEW;

END;
$$ LANGUAGE plpgsql;


COMMENT ON FUNCTION trigger_payment_cheque_restore() IS $comment$
  DESCR:
  If a payment is being deleted, the cheque associated with the payment (if there was one)
  should be made available again (put "On File").
  Similarly, if a new cheque is being associated with a payment, make the old cheque available again.
$comment$;

CREATE TRIGGER payment_update_delete
  AFTER UPDATE OR DELETE ON loan_payment
  FOR EACH ROW
  EXECUTE PROCEDURE trigger_payment_cheque_restore();


-- ----------------------------------------------

CREATE OR REPLACE FUNCTION trigger_payment_cheque_set() RETURNS trigger AS $$
DECLARE

    v_deposited_document_status_id payment_document_status.id%TYPE;

BEGIN

    IF ( new.cheque_id IS NOT NULL)
    THEN

        SELECT id
          INTO v_deposited_document_status_id
          FROM payment_document_status
          WHERE document_status = 'Deposited';

        UPDATE cheque
          SET document_status_id = v_deposited_document_status_id
          WHERE id = new.cheque_id;

    END IF;

    RETURN new;

END;
$$ LANGUAGE plpgsql;


COMMENT ON FUNCTION trigger_payment_cheque_set() IS $comment$
  DESCR:
  If cheque is associated with a payment, it should be marked as consumed ("Deposited").
$comment$;


CREATE TRIGGER payment_insert_update
  AFTER INSERT OR UPDATE ON loan_payment
  FOR EACH ROW
  EXECUTE PROCEDURE trigger_payment_cheque_set();
