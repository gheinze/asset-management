package com.accounted4.assetmanager.finance.loan;

import com.accounted4.finance.loan.AmortizationAttributes;
import com.accounted4.finance.loan.AmortizationCalculator;
import com.accounted4.finance.loan.ScheduledPayment;
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


    @Autowired private AmortizationScheduleJasperReport amortizationScheduleJasperReport;
    @Autowired private LoanStatusJasperReport loanStatusJasperReport;


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
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(payments);

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
            customParameters.put("compoundPeriod", amAttrs.getCompoundingPeriodsPerYear());
        }
        customParameters.put("mortgagee", "Accounted4");
        customParameters.put("mortgagor", "Accounted4");


        JasperReport compiledReport = amortizationScheduleJasperReport.getCompiledReport();

        JasperPrint jasperPrint = JasperFillManager.fillReport(compiledReport, customParameters, ds);

        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

        //File pdfFile = File.createTempFile("amSchedule", ".pdf");
        //JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile.getCanonicalPath());

    }

    @Override
    public void writePdfLoanStatusToStream(final Loan loan, final OutputStream outputStream) throws JRException, IOException {

        LoanStatus loanStatus = new LoanStatus(loan);
        List<LoanStatusLineItem> lineItems = loanStatus.getOrderedLineItems();

        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(lineItems);

        // TODO: name, title, etc should be configurable parameters as well
        Map<String, Object> customParameters = new HashMap<>();
//        customParameters.put("amount", amAttrs.getLoanAmount());
//        customParameters.put("rate", amAttrs.getInterestRateAsPercent());
//
//        MonetaryAmount requestedMonthlyPayment = amAttrs.getRegularPayment();
//        MonetaryAmount periodicPayment = (null == requestedMonthlyPayment) ? getPeriodicPayment(amAttrs) : requestedMonthlyPayment;
//        customParameters.put("monthlyPayment", periodicPayment);
//
//        customParameters.put("term", amAttrs.getTermInMonths());
//        if (!amAttrs.isInterestOnly()) {
//            customParameters.put("amortizationYears", amAttrs.getAmortizationPeriodInMonths() / MONTHS_PER_YEAR);
//            customParameters.put("amortizationMonths", amAttrs.getAmortizationPeriodInMonths() % MONTHS_PER_YEAR);
//            customParameters.put("compoundPeriod", amAttrs.getCompoundingPeriodsPerYear());
//        }
//        customParameters.put("mortgagee", "Accounted4");
//        customParameters.put("mortgagor", "Accounted4");


        JasperReport compiledReport = loanStatusJasperReport.getCompiledReport();

        JasperPrint jasperPrint = JasperFillManager.fillReport(compiledReport, customParameters, ds);

        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

        //File pdfFile = File.createTempFile("amSchedule", ".pdf");
        //JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile.getCanonicalPath());

    }

}
