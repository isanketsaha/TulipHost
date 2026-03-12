package com.tulip.host.repository;

import com.tulip.host.domain.UploadRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadRecordRepository extends JpaRepository<UploadRecord, Long> {}
