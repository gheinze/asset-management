package com.accounted4.assetmanager.finance.loan;

import com.accounted4.assetmanager.util.JasperReportRegistry;
import com.accounted4.finance.loan.AmortizationAttributes;
import com.accounted4.finance.loan.AmortizationCalculator;
import com.accounted4.finance.loan.ScheduledPayment;
import com.accounted4.finance.loan.TimePeriod;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.money.MonetaryAmount;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author gheinze
 */
@Service
public class LoanServiceImpl implements LoanService {

    private static final String AMORTIZATION_SCHEDULE_REPORT = "com/accounted4/assetmanager/finance/loan/AmortizationSchedule.jasper";
    private static final String LOAN_STATUS_REPORT = "com/accounted4/assetmanager/finance/loan/LoanStatus.jasper";

    @Autowired private JasperReportRegistry reportRegistry;


    @Override
    public MonetaryAmount getPeriodicPayment(AmortizationAttributes amAttrs) {
        return AmortizationCalculator.getPeriodicPayment(amAttrs);
    }

    @Override
    public List<ScheduledPayment> generateSchedule(AmortizationAttributes amAttrs) {
        return AmortizationCalculator.generateSchedule(amAttrs);
    }


    private static final int MONTHS_PER_YEAR = 12;

    @Override
    public void writePdfScheduleToStream(final AmortizationAttributes amAttrs, final OutputStream outputStream) throws JRException, IOException {

        List<ScheduledPayment> payments = generateSchedule(amAttrs);

        // TODO: name, title, etc should be configurable parameters as well
        Map<String, Object> customParameters = new HashMap<>();
        customParameters.put("amount", amAttrs.getLoanAmount());
        customParameters.put("rate", amAttrs.getInterestRateAsPercent());

        MonetaryAmount requestedMonthlyPayment = amAttrs.getRegularPayment();
        MonetaryAmount periodicPayment = (null == requestedMonthlyPayment) ? getPeriodicPayment(amAttrs) : requestedMonthlyPayment;
        customParameters.put("monthlyPayment", periodicPayment);

        customParameters.put("term", amAttrs.getTermInMonths());
        if (!amAttrs.isInterestOnly()) {
            customParameters.put("amortizationYears", amAttrs.getAmortizationPeriodInMonths() / MONTHS_PER_YEAR);
            customParameters.put("amortizationMonths", amAttrs.getAmortizationPeriodInMonths() % MONTHS_PER_YEAR);
            customParameters.put("compoundPeriod",
                    TimePeriod.getTimePeriodWithPeriodCountOf(amAttrs.getCompoundingPeriodsPerYear()).getDisplayName()
            );
        }
        customParameters.put("mortgagee", "Accounted4");
        customParameters.put("mortgagor", "Accounted4");

        writePdfScheduleToStream(AMORTIZATION_SCHEDULE_REPORT, payments, customParameters, outputStream);

    }


    @Override
    public void writePdfLoanStatusToStream(final Loan loan, final OutputStream outputStream) throws JRException, IOException {
        LoanStatus loanStatus = new LoanStatus(loan);
        List<LoanStatusLineItem> lineItems = loanStatus.getOrderedLineItems();

        Map<String, Object> customParameters = new HashMap<>();
        customParameters.put("loanName", loanStatus.getLoanName());
        customParameters.put("nextPaymentDate", loanStatus.getNextScheduledPaymentDate());
        customParameters.put("regularDue", loanStatus.getRegularDue());
        customParameters.put("actualDue", loanStatus.getActualDue());
        customParameters.put("balance", loanStatus.getBalance());
        customParameters.put("nextChequeOnFile", loanStatus.getNextChequeOnFile());
        customParameters.put("daysToMaturity", loanStatus.getDaysToMaturity());
        customParameters.put("perDiem", loanStatus.getPerDiem());

        writePdfScheduleToStream(LOAN_STATUS_REPORT, lineItems, customParameters, outputStream);
    }


    private void writePdfScheduleToStream(
            final String reportPath,
            final List dataList,
            Map<String, Object> customParameters,
            final OutputStream outputStream
    ) throws JRException, IOException {

        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(dataList);
        JasperReport compiledReport = reportRegistry.getCompiledReport(reportPath);
        JasperPrint jasperPrint = JasperFillManager.fillReport(compiledReport, customParameters, ds);
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

        //File pdfFile = File.createTempFile("amSchedule", ".pdf");
        //JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile.getCanonicalPath());
    }

}
