package com.tulip.host.service;

import com.tulip.host.web.rest.errors.InternalServerErrorException;
import java.io.FileInputStream;
import java.io.IOException;
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
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JasperService {

    public byte[] generatePdf(Resource file, Map<String, Object> parameters, List<Object> list) {
        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(list);
        try {
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(file.getFile()));
            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, parameters, beanCollectionDataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException("Failed to create excel : " + e.getMessage());
        }
    }
}
