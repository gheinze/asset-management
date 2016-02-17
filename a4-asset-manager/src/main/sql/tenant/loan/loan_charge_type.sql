CREATE TABLE loan_charge_type(
  charge_type      text NOT NULL
 ,capitalizing     boolean NOT NULL
 ,sort_order       int
) INHERITS(base);

COMMENT ON TABLE loan_charge_type IS 'A list of classifications of a charges that can be applied against a loan.';
COMMENT ON COLUMN loan_charge_type.charge_type IS 'A classification of a charge that can be applied against a loan.';
COMMENT ON COLUMN loan_charge_type.capitalizing IS 'A charge of this type should be added to the principal amount of the loan, thereby incurring compound interest.';

SELECT ist_pk('loan_charge_type');
SELECT ist_bk('loan_charge_type', ARRAY['charge_type']);

INSERT INTO loan_charge_type(charge_type, capitalizing, sort_order) VALUES('Reversed payment', true, 5);
INSERT INTO loan_charge_type(charge_type, capitalizing, sort_order) VALUES('Interest', true, 10);
INSERT INTO loan_charge_type(charge_type, capitalizing, sort_order) VALUES('Tax', true, 20);
INSERT INTO loan_charge_type(charge_type, capitalizing, sort_order) VALUES('Insurance', true, 30);
INSERT INTO loan_charge_type(charge_type, capitalizing, sort_order) VALUES('Other (capitalizing)', true, 40);

INSERT INTO loan_charge_type(charge_type, capitalizing, sort_order) VALUES('Fee: NSF', false, 110);
INSERT INTO loan_charge_type(charge_type, capitalizing, sort_order) VALUES('Fee: Late', false, 120);
INSERT INTO loan_charge_type(charge_type, capitalizing, sort_order) VALUES('Fee: Inspection', false, 125);
INSERT INTO loan_charge_type(charge_type, capitalizing, sort_order) VALUES('Fee: Other', false, 130);
INSERT INTO loan_charge_type(charge_type, capitalizing, sort_order) VALUES('Bonus: pre-payment', false, 140);
INSERT INTO loan_charge_type(charge_type, capitalizing, sort_order) VALUES('Other (non-capitalizing)', false, 150);
