package com.tulip.host.repository;

import com.tulip.host.domain.GlobalDocument;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalDocumentRepository extends BaseRepository<GlobalDocument, Long> {
    List<GlobalDocument> findAllByIsActiveTrueOrderByCreatedDateDesc();
}
