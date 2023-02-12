package com.tulip.host.service;

import com.tulip.host.data.TransactionReportDTO;
import com.tulip.host.mapper.TransactionMapper;
import com.tulip.host.repository.StudentRepository;
import com.tulip.host.repository.TransactionRepository;
import com.tulip.host.utils.CommonUtils;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonitorService {

    private final TransactionRepository transactionRepository;

    private final StudentRepository studentRepository;

    private final TransactionMapper transactionMapper;

    public List<TransactionReportDTO> transactionReport(Date from, Date to) {
        //        Pageable pageable = CommonUtils.getPageRequest( Sort.Direction.ASC,"createdDate",pageNo, pageSize);
        List<TransactionReportDTO> fetchTransactionGroupBy = transactionRepository.fetchTransactionGroupBy(from, to);
        Collections.sort(fetchTransactionGroupBy, (a, b) -> a.getTransactionDate().compareTo(b.getTransactionDate()));
        return fetchTransactionGroupBy;
    }
}
