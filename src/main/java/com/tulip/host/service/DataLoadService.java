package com.tulip.host.service;

import com.tulip.host.domain.ClassDetail;
import com.tulip.host.domain.Employee;
import com.tulip.host.domain.FeesCatalog;
import com.tulip.host.domain.Session;
import com.tulip.host.domain.Upload;
import com.tulip.host.enums.FeesRuleType;
import com.tulip.host.mapper.FeesCatalogMapper;
import com.tulip.host.mapper.SessionMapper;
import com.tulip.host.mapper.UploadMapper;
import com.tulip.host.repository.ClassDetailRepository;
import com.tulip.host.repository.EmployeeRepository;
import com.tulip.host.repository.FeesCatalogRepository;
import com.tulip.host.repository.SessionRepository;
import com.tulip.host.repository.UploadRepository;
import com.tulip.host.web.rest.vm.FileUploadVM;
import com.tulip.host.web.rest.vm.dataload.FeesLoadVM;
import com.tulip.host.web.rest.vm.dataload.SessionLoadVM;
import com.tulip.host.web.rest.vm.dataload.UpdateSessionVM;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataLoadService {

    private final EmployeeRepository employeeRepository;
    private final SessionRepository sessionRepository;
    private final ClassDetailRepository classDetailRepository;

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
                    return ClassDetail.builder()
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

    @Transactional
    public void updateSession(UpdateSessionVM vm) {
        Session session = sessionRepository.findById(vm.getSessionId()).orElseThrow();

        vm
            .getStdList()
            .forEach(stdVM -> {
                ClassDetail classDetail = classDetailRepository.findBySessionIdAndStd(vm.getSessionId(), stdVM.getStd());

                if (classDetail == null) {
                    classDetail = ClassDetail.builder().std(stdVM.getStd()).session(session).build();
                }

                Employee headTeacher = stdVM.getClassTeacher() != null
                    ? employeeRepository.findById(stdVM.getClassTeacher()).orElse(null)
                    : null;
                classDetail.setHeadTeacher(headTeacher);

                ClassDetail savedClass = classDetailRepository.save(classDetail);

                List<FeesLoadVM> newFees = vm.getFeesList() != null
                    ? vm.getFeesList().getOrDefault(savedClass.getStd(), Collections.emptyList())
                    : Collections.emptyList();

                // Load existing catalog entries to upsert — never delete, preserves FK refs in FeesLineItem
                List<FeesCatalog> existing = feesCatalogRepository.findAllByStd(savedClass);
                Map<String, FeesCatalog> existingByName = existing.stream().collect(Collectors.toMap(FeesCatalog::getFeesName, f -> f));

                Set<String> incomingNames = newFees.stream().map(f -> f.getItemName().toUpperCase()).collect(Collectors.toSet());

                // Soft-delete fees removed from the list
                existing.stream().filter(f -> !incomingNames.contains(f.getFeesName())).forEach(f -> f.setActive(false));

                // Update existing or insert new
                List<FeesCatalog> toSave = newFees
                    .stream()
                    .map(feeVM -> {
                        String name = feeVM.getItemName().toUpperCase();
                        FeesCatalog catalog = existingByName.getOrDefault(
                            name,
                            FeesCatalog.builder().feesName(name).std(savedClass).build()
                        );
                        catalog.setPrice(feeVM.getPrice());
                        catalog.setApplicableRule(FeesRuleType.valueOf(feeVM.getRule().toUpperCase()));
                        catalog.setActive(true);
                        return catalog;
                    })
                    .collect(Collectors.toList());

                feesCatalogRepository.saveAll(toSave);
            });
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
