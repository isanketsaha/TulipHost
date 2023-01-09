package com.tulip.host.service;

import com.tulip.host.data.FeesCatalogDTO;
import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.FeesCatalog;
import com.tulip.host.enums.StdEnum;
import com.tulip.host.mapper.FeesCatalogMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.FeesCatalogRepository;
import com.tulip.host.web.rest.vm.CatalogVM;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogService {

    private final FeesCatalogRepository feesCatalogRepository;

    private final ClassDetailRepository classDetailRepository;

    private final FeesCatalogMapper feesCatalogMapper;

    public List<FeesCatalogDTO> fetchFeesCatalog(CatalogVM catalogVM) {
        ClassDetail std = classDetailRepository.findBySessionIdAndStd(catalogVM.getSession(), catalogVM.getStd().name());
        List<FeesCatalog> allByActiveAndStd = feesCatalogRepository.findAllByActiveAndStd(Boolean.TRUE, std);
        return feesCatalogMapper.getBasicEntityListFromModelList(allByActiveAndStd);
    }
}
