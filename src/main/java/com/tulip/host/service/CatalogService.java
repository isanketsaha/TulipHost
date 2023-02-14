package com.tulip.host.service;

import com.tulip.host.data.CatalogDTO;
import com.tulip.host.data.FeesCatalogDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.FeesCatalog;
import com.tulip.host.domain.ProductCatalog;
import com.tulip.host.mapper.FeesCatalogMapper;
import com.tulip.host.mapper.ProductCatalogMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.FeesCatalogRepository;
import com.tulip.host.repository.ProductCatalogRepository;
import com.tulip.host.web.rest.vm.CatalogVM;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogService {

    private final FeesCatalogRepository feesCatalogRepository;

    private final ClassDetailRepository classDetailRepository;

    private final ProductCatalogRepository productCatalogRepository;

    private final FeesCatalogMapper feesCatalogMapper;

    private final ProductCatalogMapper catalogMapper;

    @Transactional
    public List<FeesCatalogDTO> fetchFeesCatalog(Long id) {
        ClassDetail std = classDetailRepository.findById(id).orElse(null);
        return feesCatalogMapper.toEntityList(std.getFeesCatalogs());
    }

    @Transactional
    public List<CatalogDTO> productCatalog(Long classId) {
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
}
