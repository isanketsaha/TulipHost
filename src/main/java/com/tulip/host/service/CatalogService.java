package com.tulip.host.service;

import static com.tulip.host.config.Constants.TRANSPORT_FEES;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tulip.host.web.rest.vm.InventoryUpdateVM;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.tulip.host.repository.InventoryRepository;
import com.tulip.host.repository.ProductCatalogRepository;
import com.tulip.host.repository.SessionRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.repository.TransportCatalogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogService {

    private final StudentRepository studentRepository;
    private final SessionRepository sessionRepository;

    // FeesCatalogRepository - kept for future use if needed
    // private final FeesCatalogRepository feesCatalogRepository;

    private final ClassDetailRepository classDetailRepository;

    private final ProductCatalogRepository productCatalogRepository;

    private final FeesCatalogMapper feesCatalogMapper;

    private final ProductCatalogMapper catalogMapper;

    private final InventoryRepository inventoryRepository;

    private final TransportCatalogRepository transportCatalogRepository;

    private final TransportCatalogMapper transportMapper;

    private final StudentToTransportMapper toTransportMapper;

    private final InventoryService inventoryService;

    @Transactional
    public List<FeesCatalogDTO> fetchFeesCatalog(Long id) {
        ClassDetail std = classDetailRepository.findById(id).orElse(null);
        if (std == null || std.getFeesCatalogs() == null) {
            return Collections.emptyList();
        }
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
                return Collections.singletonList(feesCatalogDTO);
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

    /**
     * Get product catalog with unified pricing - shows highest price when multiple
     * inventory batches exist
     */
    @Transactional
    public List<ProductDTO> productCatalogWithUnifiedPricing(Long classId) {
        List<ProductCatalog> catalogs = productCatalogRepository.findAllByActiveProduct(classId);

        try {
            List<ProductDTO> productsWithUnifiedPricing = new ArrayList<>();

            for (ProductCatalog catalog : catalogs) {
                int totalAvailableQty = inventoryService.getTotalAvailableQuantity(catalog);
                if(catalog.getItemName().equals("ADMISSION FORM")) {
                    log.info("Debug: Product {} has available qty {}", catalog.getItemName(), totalAvailableQty);
                }
                if (totalAvailableQty > 0) {
                    ProductDTO productDTO = catalogMapper.toEntity(catalog);
                    productDTO.setAvailableStock(totalAvailableQty);
                    productDTO.setLowStock(totalAvailableQty < 10);
                    productsWithUnifiedPricing.add(productDTO);
                }
            }
            return productsWithUnifiedPricing;
        } finally {
            catalogs.clear();
        }
    }

    /**
     * Creates a new inventory refill entry for a product.
     * This follows the pattern: ProductCatalog (read-only reference) -> Multiple Inventory entries (refills).
     * Each refill is tracked as a separate Inventory record with its own:
     * - unitPrice (selling price at time of refill)
     * - qty (quantity in this batch)
     * - discountPercent (discount type for this batch)
     * - vendor (supplier for this batch)
     *
     * @param stockUpdateVM contains inventory refill details
     */
    @Transactional
    public void updateProduct(InventoryUpdateVM stockUpdateVM) {
        // Get existing inventory to access the product reference
        Inventory existingInventory = inventoryRepository.findById(stockUpdateVM.getInventoryId())
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + stockUpdateVM.getInventoryId()));

        ProductCatalog product = existingInventory.getProduct();

        // Create a NEW inventory entry for this refill (never update existing records)
        Inventory newInventoryRefill = Inventory.builder()
                .product(product)
                .unitPrice(stockUpdateVM.getUnitPrice())
                .qty(stockUpdateVM.getPurchasedQty())
                .discountPercent(stockUpdateVM.getDiscountPercent())
                .vendor(stockUpdateVM.getVendor())
                .active(true)
                .build();

        // If wanting to deactivate old batch, mark it inactive
        if (stockUpdateVM.isDeactivateOld()) {
            existingInventory.setActive(false);
            inventoryRepository.saveAndFlush(existingInventory);
        }

        // Save new refill entry
        inventoryRepository.saveAndFlush(newInventoryRefill);
        log.info("New inventory refill created for product {} with qty {} at price {}",
                product.getItemName(), stockUpdateVM.getPurchasedQty(), stockUpdateVM.getUnitPrice());
    }
}
