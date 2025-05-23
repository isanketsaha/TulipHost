package com.tulip.host.service;

import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.FeesCatalog;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.Upload;
import com.tulip.host.mapper.ClassMapper;
import com.tulip.host.mapper.FeesCatalogMapper;
import com.tulip.host.mapper.InventoryMapper;
import com.tulip.host.mapper.ProductCatalogMapper;
import com.tulip.host.mapper.SessionMapper;
import com.tulip.host.mapper.StudentMapper;
import com.tulip.host.mapper.UploadMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.repository.FeesCatalogRepository;
import com.tulip.host.repository.InventoryRepository;
import com.tulip.host.repository.SessionRepository;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.repository.UploadRepository;
import com.tulip.host.web.rest.vm.FileUploadVM;
import com.tulip.host.web.rest.vm.dataload.FeesLoadVM;
import com.tulip.host.web.rest.vm.dataload.SessionLoadVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataLoadService {

    private final EmployeeRepository employeeRepository;
    private final SessionRepository sessionRepository;


    private final FeesCatalogMapper feesCatalogMapper;
    private final FeesCatalogRepository feesCatalogRepository;


    private final SessionMapper sessionMapper;

    private final UploadMapper uploadMapper;
    private final UploadRepository uploadRepository;


    @Transactional
    public void loadFees(List<FeesLoadVM> fees) {
        List<FeesCatalog> feesCatalogList = fees
            .stream()
            .map(item -> {
                item.setRule(item.getRule().toUpperCase());
                //                ClassDetail classDetail = classDetailRepository.findBySessionIdAndStd(item.getSession(), item.getClassDetail());
                FeesCatalog feesCatalog = feesCatalogMapper.toModel(item);
                //                feesCatalog.setStd(classDetail);
                return feesCatalog;
            })
            .collect(Collectors.toList());
        feesCatalogRepository.saveAllAndFlush(feesCatalogList);
    }

    @Transactional
    public void loadSession(SessionLoadVM loadVM) {
        Session session = sessionMapper.toModel(loadVM);
        session.setStdList(
            loadVM
                .getStdList()
                .stream()
                .map(std -> {
                    return ClassDetail
                        .builder()
                        .std(std.getStd())
                        .headTeacher(employeeRepository.findById(std.getClassTeacher()).orElseThrow())
                        .session(session)
                        .build();
                })
                .collect(Collectors.toList())
        );
        session
            .getStdList()
            .forEach(item -> {
                Set<FeesCatalog> feesCatalogs = feesCatalogMapper
                    .toModelList(loadVM.getFeesList().get(item.getStd()))
                    .stream()
                    .peek(ele -> ele.setStd(item))
                    .collect(Collectors.toSet());
                item.setFeesCatalogs(feesCatalogs);
            });
        sessionRepository.save(session);
    }

    public void removeSession(Long id) {
        sessionRepository.deleteById(id);
    }

    public void addToUpload(FileUploadVM uploadVM) {
        uploadRepository.saveAndFlush(uploadMapper.toModel(uploadVM));
    }

    public List<FileUploadVM> get(String type) {
        List<Upload> byTypeEndsWith = uploadRepository.findByDocumentTypeContainingIgnoreCaseOrderByCreatedDateDesc(type);
        return uploadMapper.toEntityList(byTypeEndsWith);
    }
}
