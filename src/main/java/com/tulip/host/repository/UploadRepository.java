package com.tulip.host.repository;

import com.tulip.host.domain.Upload;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadRepository extends JpaRepository<Upload, Long> {
    List<Upload> findByDocumentTypeLikeOrderByCreatedDateDesc(String type);
}
