package com.tulip.host.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JasperService {

    public byte[] generatePdf(InputStream file, Map<String, Object> parameters, List<Object> list) {
        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(list);
        try {
            JasperReport compileReport = JasperCompileManager.compileReport(file);
            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, parameters, beanCollectionDataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed to create PDF : " + e.getMessage());
        }
    }
}
