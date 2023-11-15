package com.tulip.host.service;

import static com.tulip.host.config.Constants.TRANSPORT_FEES;

import com.tulip.host.data.FeesCatalogDTO;
import com.tulip.host.data.ProductDTO;
import com.tulip.host.data.TransportCatalogDto;
import com.tulip.host.data.TransportOptDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Inventory;
import com.tulip.host.domain.ProductCatalog;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.Student;
import com.tulip.host.domain.TransportCatalog;
import com.tulip.host.enums.FeesRuleType;
import com.tulip.host.mapper.FeesCatalogMapper;
import com.tulip.host.mapper.ProductCatalogMapper;
import com.tulip.host.mapper.StudentToTransportMapper;
import com.tulip.host.mapper.TransportCatalogMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.FeesCatalogRepository;
import com.tulip.host.repository.InventoryRepository;
import com.tulip.host.repository.ProductCatalogRepository;
import com.tulip.host.repository.SessionRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.repository.TransportCatalogRepository;
import com.tulip.host.web.rest.vm.StockUpdateVM;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogService {

    private final StudentRepository studentRepository;
    private final SessionRepository sessionRepository;

    private final FeesCatalogRepository feesCatalogRepository;

    private final ClassDetailRepository classDetailRepository;

    private final ProductCatalogRepository productCatalogRepository;

    private final FeesCatalogMapper feesCatalogMapper;

    private final ProductCatalogMapper catalogMapper;

    private final InventoryRepository inventoryRepository;

    private final TransportCatalogRepository transportCatalogRepository;

    private final TransportCatalogMapper transportMapper;

    private final StudentToTransportMapper toTransportMapper;

    @Transactional
    public List<FeesCatalogDTO> fetchFeesCatalog(Long id) {
        ClassDetail std = classDetailRepository.findById(id).orElse(null);
        return feesCatalogMapper.toEntityList(std.getFeesCatalogs());
    }

    @Transactional
    public List<FeesCatalogDTO> fetchFeesCatalogByStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow();
        if (CollectionUtils.isNotEmpty(student.getTransports())) {
            TransportOptDTO transportOptDTO = toTransportMapper.fromEntityList(student.getTransports());
            if (transportOptDTO != null) {
                FeesCatalogDTO feesCatalogDTO = FeesCatalogDTO
                    .builder()
                    .id(transportOptDTO.getId())
                    .name(TRANSPORT_FEES)
                    .applicableRule(FeesRuleType.MONTHLY.name())
                    .description("Location - " + transportOptDTO.getLocation())
                    .amount(transportOptDTO.getAmount())
                    .type(TRANSPORT_FEES.replace(" ", "_"))
                    .build();
                return Arrays.asList(feesCatalogDTO);
            }
        }

        return Collections.emptyList();
    }

    @Transactional
    public List<TransportCatalogDto> fetchTransportCatalog(Long sessionId) {
        Session session = sessionRepository.findById(sessionId).orElseThrow();
        List<TransportCatalog> allBySession = transportCatalogRepository.findAllBySession(session);
        return transportMapper.toDtoList(allBySession);
    }

    @Transactional
    public List<ProductDTO> productCatalog(Long classId) {
        List<ProductCatalog> catalogs = productCatalogRepository.findAllByActiveProduct(classId);
        List<ProductCatalog> collect = catalogs
            .stream()
            .filter(item -> {
                int soldQuantity = item.getPurchaseLineItems().stream().mapToInt(lineItem -> lineItem.getQty()).sum();
                int purchaseQuantity = item.getInventories().stream().mapToInt(lineItem -> lineItem.getQty()).sum();
                return soldQuantity < purchaseQuantity;
            })
            .collect(Collectors.toList());
        return catalogMapper.toModelList(collect);
    }

    @Transactional
    public void updateProduct(StockUpdateVM stockUpdateVM) {
        Inventory stockReport = inventoryRepository.findById(stockUpdateVM.getStockId()).orElse(null);
        if (stockReport != null) {
            ProductCatalog product = stockReport.getProduct();
            product.setItemName(stockUpdateVM.getProduct().getItemName().toUpperCase());
            product.setPrice(stockUpdateVM.getProduct().getPrice());
            stockReport.setQty(stockUpdateVM.getPurchasedQty());
            inventoryRepository.saveAndFlush(stockReport);
        }
    }
}
