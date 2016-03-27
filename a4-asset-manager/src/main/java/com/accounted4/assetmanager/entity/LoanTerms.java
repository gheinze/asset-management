package com.accounted4.assetmanager.entity;

import com.accounted4.finance.loan.AmortizationAttributes;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.Money;

/**
 *
 * @author gheinze
 */
@Getter @Setter
@Entity
public class LoanTerms extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @OneToOne
    @JoinColumn(name="loan_id")
    private Loan loan;

    private String loanCurrency;
    private double loanAmount;
    private double regularPayment;
    private LocalDate startDate;
    private LocalDate adjustmentDate;
    private int termInMonths;
    private boolean interestOnly;
    private int amortizationPeriodInMonths;
    private int compoundingPeriodsPerYear;
    private int paymentFrequency;
    private double interestRateAsPercent;


    public void refreshFrom(AmortizationAttributes amAttr) {
        loanCurrency = amAttr.getLoanAmount().getCurrency().getCurrencyCode();
        loanAmount = amAttr.getLoanAmount().getNumber().doubleValue();
        regularPayment = amAttr.getRegularPayment().getNumber().doubleValue();
        startDate = amAttr.getStartDate();
        adjustmentDate = amAttr.getAdjustmentDate();
        termInMonths = amAttr.getTermInMonths();
        interestOnly = amAttr.isInterestOnly();
        amortizationPeriodInMonths = amAttr.getAmortizationPeriodInMonths();
        compoundingPeriodsPerYear = amAttr.getCompoundingPeriodsPerYear();
        paymentFrequency = amAttr.getPaymentFrequency();
        interestRateAsPercent = amAttr.getInterestRateAsPercent();
    }


    public AmortizationAttributes getAsAmAttributes() {

        AmortizationAttributes amAttrs = new AmortizationAttributes();

        amAttrs.setLoanAmount(Money.of(loanAmount, loanCurrency));
        amAttrs.setRegularPayment(Money.of(regularPayment, loanCurrency));
        amAttrs.setStartDate(startDate);
        amAttrs.setAdjustmentDate(adjustmentDate);
        amAttrs.setTermInMonths(termInMonths);
        amAttrs.setInterestOnly(interestOnly);
        amAttrs.setAmortizationPeriodInMonths(amortizationPeriodInMonths);
        amAttrs.setCompoundingPeriodsPerYear(compoundingPeriodsPerYear);
        amAttrs.setPaymentFrequency(paymentFrequency);
        amAttrs.setInterestRateAsPercent(interestRateAsPercent);

        return amAttrs;
    }

}
