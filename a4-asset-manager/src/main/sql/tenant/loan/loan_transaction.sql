CREATE OR REPLACE VIEW loan_transaction AS

SELECT c.loan_id, c.charge_date AS transaction_date, 'CHARGE' AS type, c.currency, c.amount, c.note, t.capitalizing
  FROM loan_charge c
  JOIN loan_charge_type t ON (t.id = c.charge_type_id)

UNION

SELECT p.loan_id, p.deposit_date, 'PAYMENT', p.currency, p.amount, p.note, false
  FROM loan_payment p
  ORDER BY 1, 2, 3
;
