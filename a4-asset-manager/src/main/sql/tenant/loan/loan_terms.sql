CREATE TABLE loan_terms(
  loan_id          bigint  NOT NULL
 ,loan_currency    char(3) NOT NULL
 ,loan_amount      numeric NOT NULL
 ,regular_payment  numeric DEFAULT 0
 ,start_date       date    NOT NULL
 ,adjustment_date  date    NOT NULL
 ,term_in_months   int     NOT NULL
 ,interest_only    boolean DEFAULT TRUE
 ,amortization_period_in_months int NOT NULL
 ,compounding_periods_per_year  int NOT NULL
 ,payment_frequency             int NOT NULL
 ,interest_rate_as_percent      numeric NOT NULL

 ,CONSTRAINT loan_terms_loan_id_fk FOREIGN KEY(loan_id) REFERENCES loan(id)
 ,CONSTRAINT loan_terms_term_in_months_ck CHECK(term_in_months > 0 AND term_in_months <= 360)
 ,CONSTRAINT loan_terms_amortization_period_in_months_ck CHECK(amortization_period_in_months > 0 AND amortization_period_in_months <= 360)
 ,CONSTRAINT loan_terms_compounding_periods_per_year_ck CHECK(compounding_periods_per_year > 0 AND compounding_periods_per_year <= 52)
 ,CONSTRAINT loan_terms_payment_frequency_ck CHECK(payment_frequency > 0 AND payment_frequency <= 52)
 ,CONSTRAINT loan_terms_interest_rate_as_percent_ck CHECK(interest_rate_as_percent > 0)

) INHERITS(base);

COMMENT ON TABLE loan_terms IS 'The payment terms of the loan. In a 1-1 relationship with the loan table.';
COMMENT ON COLUMN loan_terms.loan_currency IS 'The ISO 4217 3-character currency code applicable to all "amounts" of this loan.';
COMMENT ON COLUMN loan_terms.loan_amount IS 'The original principal amount.';
COMMENT ON COLUMN loan_terms.regular_payment IS 'The periodic payment to be applied to this loan.';
COMMENT ON COLUMN loan_terms.start_date IS 'The date at which the funds were advanced for the loan.';
COMMENT ON COLUMN loan_terms.adjustment_date IS 'The date from which periodic payments are to commence.';
COMMENT ON COLUMN loan_terms.term_in_months IS 'The number of months from the adjustment date at which amortization stops and remaining principal is due.';
COMMENT ON COLUMN loan_terms.interest_only IS 'True if this is an interest only calculation (i.e. no amortization)';
COMMENT ON COLUMN loan_terms.amortization_period_in_months IS 'Number of months over which to amortize the payments. If the calculated regular payments are made till this date, principal remaining will be 0';
COMMENT ON COLUMN loan_terms.compounding_periods_per_year IS 'Number of times a year interest compounding is calculated. Canadian rules: 2 (semi-annually). American rules: 12 (monthly)';
COMMENT ON COLUMN loan_terms.payment_frequency IS 'Number of times a year payments will be made';
COMMENT ON COLUMN loan_terms.interest_rate_as_percent IS 'The nominal interest rate being paid (effective rate can be higher if compounding)';

SELECT ist_pk('loan_terms');
SELECT ist_bk('loan_terms', ARRAY['loan_id']);
