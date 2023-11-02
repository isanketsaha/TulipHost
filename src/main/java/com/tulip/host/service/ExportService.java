package com.tulip.host.service;

import static com.tulip.host.config.Constants.JASPER_FOLDER;

import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.data.PrintTransactionDTO;
import com.tulip.host.data.StockExportDTO;
import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentExportDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Inventory;
import com.tulip.host.domain.Student;
import com.tulip.host.domain.Transaction;
import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.mapper.InventoryMapper;
import com.tulip.host.mapper.StudentMapper;
import com.tulip.host.mapper.TransactionMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.InventoryRepository;
import com.tulip.host.utils.CommonUtils;
import jakarta.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final ExcelExporterService excelExporterService;

    private final SessionService sessionService;
    private final InventoryRepository inventoryRepository;

    private final InventoryMapper inventoryMapper;

    private final ClassDetailRepository classDetailRepository;

    private final StudentMapper studentMapper;

    private final StudentService studentService;

    private final PaymentService paymentService;

    private final JasperService jasperService;

    private final TransactionMapper transactionMapper;

    @Transactional
    public XSSFWorkbook exportStock() {
        List<Inventory> stockReport = inventoryRepository.stockReport();
        List<StockExportDTO> stockExportDTOS = inventoryMapper.toExportEntityList(stockReport);
        List<Object> list = new ArrayList<>(stockExportDTOS);
        return excelExporterService.export(list, "Inventory");
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
            return excelExporterService.export(list, "ClassDetail");
        }
        return null;
    }

    @Transactional
    public byte[] paymentReceipt(Long paymentId) throws IOException {
        Transaction transactionRecord = paymentService.getTransactionRecord(paymentId);
        PrintTransactionDTO transaction = transactionMapper.toPrintEntity(transactionRecord);
        if (transaction != null && transaction.getFeesItem() != null) {
            try (
                InputStream inputStream = getClass()
                    .getResourceAsStream(
                        transaction.getPayType().equals(PayTypeEnum.FEES)
                            ? JASPER_FOLDER + "Fees_Receipt.jrxml"
                            : JASPER_FOLDER + "Purchase_Receipt.jrxml"
                    )
            ) {
                StudentBasicDTO basicDTO = studentService.basicSearchStudent(transaction.getStudentId());
                transaction.setStd(basicDTO.getStd());
                Map<String, Object> map = new HashMap<>();
                if (transaction.getPayType().equals(PayTypeEnum.FEES)) {
                    map.put("feesLineItem", new JRBeanCollectionDataSource(transaction.getFeesItem()));
                } else {
                    map.put("purchaseLineItems", new JRBeanCollectionDataSource(transaction.getPurchaseItems()));
                }
                return jasperService.generatePdf(inputStream, map, Arrays.asList(transaction));
            }
        }
        return null;
    }

    public XSSFWorkbook transactionHistory(List<Date> transactionMonths) {
        XSSFWorkbook export = null;
        for (Date months : transactionMonths) {
            List<PaySummaryDTO> month = paymentService.getTransactionRecordByDate(months, "MONTH");
            List<Object> list = new ArrayList<>(month);
            export =
                export == null
                    ? excelExporterService.export(list, CommonUtils.formatFromDate(months, "MMM-yyyy"))
                    : excelExporterService.export(export, CommonUtils.formatFromDate(months, "MMM-yyyy"), list);
        }
        return export;
    }
}
