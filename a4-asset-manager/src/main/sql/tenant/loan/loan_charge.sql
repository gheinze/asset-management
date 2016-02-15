CREATE TABLE loan_charge(
  loan_id          bigint  NOT NULL
 ,charge_type_id   bigint  NOT NULL
 ,currency         char(3) NOT NULL
 ,amount           numeric NOT NULL
 ,charge_date      date    NOT NULL
 ,note             text

 ,CONSTRAINT loan_charge_loan_id_fk FOREIGN KEY(loan_id) REFERENCES loan(id)
 ,CONSTRAINT loan_charge_charge_type_id_fk FOREIGN KEY(charge_type_id) REFERENCES loan_charge_type(id)

) INHERITS(base);

COMMENT ON TABLE loan_charge IS 'A expense applied against a loan, typically an interest charge or a fee.';
COMMENT ON COLUMN loan_charge.charge_type_id IS 'The classification of the charge being applied to the loan (interest, fee, etc).';
COMMENT ON COLUMN loan_charge.currency IS 'The ISO 4217 3-character currency code of the charge.';
COMMENT ON COLUMN loan_charge.amount IS 'The amount applied against the loan.';
COMMENT ON COLUMN loan_charge.charge_date IS 'The date at which the charge was applied against the loan.';
COMMENT ON COLUMN loan_charge.note IS 'Free-form explanation for the charge.';

SELECT ist_pk('loan_charge');
