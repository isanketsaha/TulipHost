//package com.tulip.host.repository;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.repository.PagingAndSortingRepository;
//
//import java.time.Instant;
//
//public interface FeesOrderRepository extends PagingAndSortingRepository<FeesOrder, Long> {
//
//    Page<FeesOrder> findAllByStudentAndCreatedDateBetween(Student student, Instant fromDate, Instant toDate, PageRequest pageRequest);
//
//}
