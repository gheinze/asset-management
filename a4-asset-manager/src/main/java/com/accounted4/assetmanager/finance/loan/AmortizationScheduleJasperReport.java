package com.accounted4.assetmanager.finance.loan;

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * The Jasper report for an amortization schedule. The compiled report is wrapped in this bean.
 * TODO: Eventually this could be converted into a generic compiled report loader where the
 * reports are cached and retrieved by name and lazy loaded.
 *
 * @author gheinze
 */
@Component
public class AmortizationScheduleJasperReport {

    private JasperReport report;

    @PostConstruct
    public void init() throws IOException, JRException {
        ClassPathResource resource = new ClassPathResource("com/accounted4/assetmanager/finance/loan/AmortizationSchedule.jasper");
        try (InputStream resourceInputStream = resource.getInputStream()) {
            report = (JasperReport) JRLoader.loadObject(resourceInputStream);
        }
    }

    public JasperReport getCompiledReport() {
        return report;
    }

}
