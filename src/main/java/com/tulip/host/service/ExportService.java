package com.tulip.host.service;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.data.StockExportDTO;
import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentExportDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Inventory;
import com.tulip.host.domain.Student;
import com.tulip.host.domain.Transaction;
import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.mapper.ClassMapper;
import com.tulip.host.mapper.InventoryMapper;
import com.tulip.host.mapper.StudentMapper;
import com.tulip.host.mapper.TransactionMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.InventoryRepository;
import com.tulip.host.repository.TransactionRepository;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final ExcelExporterService excelExporterService;

    private final SessionService sessionService;
    private final InventoryRepository inventoryRepository;

    private final InventoryMapper inventoryMapper;

    private final ClassDetailRepository classDetailRepository;

    private final StudentMapper studentMapper;

    private final PaymentService paymentService;

    private final JasperService jasperService;

    @Transactional
    public XSSFWorkbook exportStock() {
        List<Inventory> stockReport = inventoryRepository.stockReport(sessionService.fetchCurrentSession().getId());
        List<StockExportDTO> stockExportDTOS = inventoryMapper.toExportEntityList(stockReport);
        List<Object> list = new ArrayList<>(stockExportDTOS);
        return excelExporterService.export(list);
    }

    @Transactional
    public XSSFWorkbook exportClassDetails(Long classroomId) {
        ClassDetail classDetail = classDetailRepository.findByClass(classroomId);
        if (classDetail != null) {
            Set<Student> students = classDetail.getStudents();
            List<Student> studentList = new ArrayList<>(students);
            studentList.sort(Comparator.comparing(Student::getName));
            List<StudentExportDTO> studentExportDTOS = studentMapper.toBasicEntityExportList(studentList);
            List<Object> list = new ArrayList<>(studentExportDTOS);
            return excelExporterService.export(list);
        }
        return null;
    }

    public byte[] paymentReceipt(Long paymentId) throws FileNotFoundException {
        PaySummaryDTO transaction = paymentService.paymentDetails(paymentId);
        if (transaction != null && transaction.getFeesItem() != null) {
            File file = ResourceUtils.getFile(
                "classpath:jasper/" + (transaction.getPayType().equals(PayTypeEnum.FEES) ? "Fees_Receipt.jrxml" : "Purchase_Receipt.jrxml")
            );
            Map<String, Object> map = new HashMap<>();
            if (transaction.getPayType().equals(PayTypeEnum.FEES)) {
                map.put("feesLineItem", new JRBeanCollectionDataSource(transaction.getFeesItem()));
            } else {
                map.put("purchaseLineItems", new JRBeanCollectionDataSource(transaction.getPurchaseItems()));
            }
            return jasperService.generatePdf(file, map, Arrays.asList(transaction));
        }
        return null;
    }
}
