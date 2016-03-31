/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.accounted4.assetmanager.service;

import com.accounted4.assetmanager.entity.Loan;
import com.accounted4.finance.loan.AmortizationAttributes;
import com.accounted4.finance.loan.ScheduledPayment;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.money.MonetaryAmount;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author gheinze
 */
public interface LoanService {

    MonetaryAmount getPeriodicPayment(AmortizationAttributes amAttrs);

    List<ScheduledPayment> generateSchedule(AmortizationAttributes amAttrs);

    void writePdfScheduleToStream(final AmortizationAttributes amAttrs, final OutputStream outputStream) throws JRException, IOException;

    void writePdfLoanStatusToStream(final Loan loan, final OutputStream outputStream) throws JRException, IOException;

    List<Loan> findAllLoans();

}
