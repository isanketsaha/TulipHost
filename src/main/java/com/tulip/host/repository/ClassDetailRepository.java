package com.tulip.host.repository;

import com.tulip.host.data.ClassDetailDTO;
import com.tulip.host.domain.ClassDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassDetailRepository extends JpaRepository<ClassDetail, Long> {
    List<ClassDetailDTO> fetchClassListBySession(Long SessionId);

    ClassDetailDTO fetchClass(Long SessionId, String std);
}
