package com.accounted4.assetmanager.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Retrieve a compiled Jasper Report by name.
 *
 * @author gheinze
 */
@Component
public class JasperReportRegistry {


    private final Map<String, JasperReport> reportCache = new HashMap<>();


    public JasperReport getCompiledReport(String jasperReportClassPath) throws IOException, JRException {
        JasperReport report = reportCache.get(jasperReportClassPath);
        return null == report ? loadReport(jasperReportClassPath) : report;
    }

    private JasperReport loadReport(String jasperReportClassPath) throws IOException, JRException {
        ClassPathResource resource = new ClassPathResource(jasperReportClassPath);
        try (InputStream resourceInputStream = resource.getInputStream()) {
            JasperReport report = (JasperReport) JRLoader.loadObject(resourceInputStream);
            reportCache.put(jasperReportClassPath, report);
            return report;
        }
    }

}

