package com.tulip.host.service;

import static com.tulip.host.config.Constants.INVOICE;
import static com.tulip.host.config.Constants.JASPER_FOLDER;

import com.tulip.host.data.PaySummaryDTO;
import com.tulip.host.data.PrintTransactionDTO;
import com.tulip.host.data.StockExportDTO;
import com.tulip.host.data.StudentBasicDTO;
import com.tulip.host.data.StudentExportDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Inventory;
import com.tulip.host.domain.ProductCatalog;
import com.tulip.host.domain.Student;
import com.tulip.host.domain.Transaction;
import com.tulip.host.domain.Upload;
import com.tulip.host.enums.PayTypeEnum;
import com.tulip.host.enums.UploadTypeEnum;
import com.tulip.host.mapper.InventoryMapper;
import com.tulip.host.mapper.StudentMapper;
import com.tulip.host.mapper.TransactionMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.InventoryRepository;
import com.tulip.host.service.communication.OutboundCommunicationService;
import com.tulip.host.utils.CommonUtils;
import com.tulip.host.web.rest.vm.FileUploadVM;
import io.github.rushuat.ocell.annotation.FieldName;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
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

    private final UploadService uploadService;

    @Transactional
    public XSSFWorkbook exportStock() {
        Map<ProductCatalog, List<Inventory>> catalogGrouped = inventoryRepository.findLatestInventoryByProductCatalogGrouped();
        List<StockExportDTO> stockExportDTOS = catalogGrouped
            .entrySet()
            .stream()
            .sorted(Comparator.comparing(entry -> entry.getKey().getId()))
            .map(entry -> inventoryMapper.toAggregatedStockExportDTO(entry.getKey(), entry.getValue()))
            .toList();
        List<Object> list = new ArrayList<>(stockExportDTOS);
        return excelExporterService.export(list, "Inventory");
    }

    @Transactional
    public XSSFWorkbook exportClassDetails(Long classroomId, List<Long> studentIds) {
        ClassDetail classDetail = classDetailRepository.findByClass(classroomId);
        if (classDetail != null) {
            Set<Student> students = classDetail.getStudents();
            List<Student> studentList = new ArrayList<>(students);
            if (studentIds != null && !studentIds.isEmpty()) {
                studentList = studentList.stream().filter(s -> studentIds.contains(s.getId())).toList();
            }
            studentList.sort(Comparator.comparing(Student::getName));
            List<StudentExportDTO> studentExportDTOS = studentMapper.toBasicEntityExportList(studentList);
            List<Object> list = new ArrayList<>(studentExportDTOS);
            return excelExporterService.export(list, "ClassDetail");
        }
        return null;
    }

    @Transactional
    public @Size(max = 100) @NotNull String downloadReceipt(Long paymentId) throws IOException, FileUploadException {
        Transaction transactionRecord = paymentService.getTransactionRecord(paymentId);
        if (transactionRecord.getInvoice() != null) {
            Upload invoice = transactionRecord.getInvoice();
            return invoice.getUid();
        } else {
            byte[] bytes = paymentReceipt(paymentId);
            FileUploadVM save = uploadService.save(bytes, MediaType.APPLICATION_PDF_VALUE, INVOICE, uploadService.getInvoiceBucket(), null);
            save.setName("INVOICE-" + paymentId);
            paymentService.attachInvoice(paymentId, save);
            return save.getUid();
        }
    }

    @Transactional
    public byte[] paymentReceipt(Long paymentId) throws IOException {
        Transaction transactionRecord = paymentService.getTransactionRecord(paymentId);
        PrintTransactionDTO transaction = transactionMapper.toPrintEntity(transactionRecord);
        StudentBasicDTO basicDTO = studentService.basicSearchStudent(transaction.getStudentId());
        if (transaction != null && transaction.getFeesItem() != null) {
            try (
                InputStream inputStream = getClass()
                    .getResourceAsStream(
                        transaction.getPayType().equals(PayTypeEnum.FEES)
                            ? JASPER_FOLDER + "Fees_Receipt.jrxml"
                            : JASPER_FOLDER + "Purchase_Receipt.jrxml"
                    )
            ) {
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

    /**
     * Generates an empty Excel template with headers derived from @FieldName annotations
     * on the upload type's VM class (and its superclasses). The sheet is named after the
     * upload type so the ocell parser can locate it during import.
     */
    public XSSFWorkbook downloadTemplate(UploadTypeEnum uploadType) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet(uploadType.name());
        org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);

        org.apache.poi.ss.usermodel.CellStyle style = workbook.createCellStyle();
        org.apache.poi.xssf.usermodel.XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(12);
        style.setFont(font);
        style.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        List<String> headers = new ArrayList<>();
        Class<?> clazz = uploadType.getFormat();
        // Walk class hierarchy to collect @FieldName annotations (superclass first)
        List<Class<?>> hierarchy = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            hierarchy.add(0, clazz);
            clazz = clazz.getSuperclass();
        }
        for (Class<?> c : hierarchy) {
            for (Field field : c.getDeclaredFields()) {
                FieldName annotation = field.getAnnotation(FieldName.class);
                if (annotation != null) {
                    headers.add(annotation.value());
                }
            }
        }

        for (int i = 0; i < headers.size(); i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(style);
            sheet.autoSizeColumn(i);
        }
        return workbook;
    }

    public XSSFWorkbook transactionHistory(List<LocalDate> transactionMonths) {
        XSSFWorkbook export = null;
        for (LocalDate months : transactionMonths) {
            List<PaySummaryDTO> month = paymentService.getTransactionRecordByDate(months, "MONTH");
            List<Object> list = new ArrayList<>(month);
            export = (export == null)
                ? excelExporterService.export(list, CommonUtils.formatFromDate(months, "MMM-yyyy"))
                : excelExporterService.export(export, CommonUtils.formatFromDate(months, "MMM-yyyy"), list);
        }
        return export;
    }
}
