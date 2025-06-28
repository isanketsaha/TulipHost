package com.tulip.host.service;

import com.tulip.host.domain.SystemDocument;
import com.tulip.host.mapper.DocumentMapper;
import com.tulip.host.repository.DocumentRepository;
import com.tulip.host.web.rest.vm.DocumentVM;
import com.tulip.host.web.rest.vm.GenericFilterVM;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class DocumentService {

    private final DocumentMapper documentMapper;
    private final DocumentRepository documentRepository;

    // Example method
    public ResponseEntity<Long> uploadDocument(DocumentVM vm) {
        SystemDocument entity = documentMapper.toEntity(vm);
        SystemDocument save = documentRepository.save(entity);
        return ResponseEntity.created(null)
            .body(save.getId());
    }

    public void deleteDocument(Long documentId) {
        documentRepository.findById(documentId)
            .ifPresent(document -> {
                document.setIsActive(false);
                documentRepository.save(document);
            });
    }

    @Transactional
    public List<DocumentVM> listDocuments(GenericFilterVM filterDTO) {
        GenericSpecification<SystemDocument> spec = new GenericSpecification<>();
        if(filterDTO != null) {
            Specification<SystemDocument> filterSpec = spec.filterBy(filterDTO.getFilters());

            // Apply sorting
            Sort sort = Sort.by(
                filterDTO.getSortDirection()
                    .equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC,
                filterDTO.getSortBy()
            );

            // Add filter for active documents only
            Specification<SystemDocument> activeSpec = (root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("isActive"), true);

            return documentRepository.findAll(filterSpec.and(activeSpec), sort)
                .stream()
                .map(document -> documentMapper.toDto(document))
                .toList();
        }
        return Collections.emptyList();
    }
}
